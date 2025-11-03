package organizare;

import com.fasterxml.jackson.databind.ObjectMapper;
import config.Configuration;

import java.io.File; import java.util.*;

public class ConfigurationLoader {

    public static Configuration load(String path) throws Exception {
        ObjectMapper om = new ObjectMapper();
        Configuration cfg = om.readValue(new File(path), Configuration.class);
        validate(cfg);
        return cfg;
    }

    private static void validate(Configuration cfg) {
        if (cfg.documents==null || cfg.documents.isEmpty())
            throw new IllegalArgumentException("No documents");
        if (cfg.offices==null   || cfg.offices.isEmpty())
            throw new IllegalArgumentException("No offices");

        //2 documente nu pot avea acelasi id
        Set<String> docIds = new HashSet<>();
        for (var d: cfg.documents)
            if (!docIds.add(d.id))
                throw new IllegalArgumentException("Documentul exista deja: "+d.id);

        //fiecare dep trebuie sa existe in lista de documente
        for (var d: cfg.documents)
            if (d.deps!=null)
                for (String dep: d.deps)
                    if (!docIds.contains(dep))
                        throw new IllegalArgumentException("Dependenta necunoscuta '"+dep+"' pentru "+d.id);

        //fiecare document trebuie emis de o institutie care are cel putin un ghiseu
        Set<String> emitted = new HashSet<>();
        for (var o: cfg.offices) {
            if (o.counters<1)
                throw new IllegalArgumentException("Institutia "+o.id+" trebuie sa aiba macar un ghiseu");
            if (o.emits!=null)
                emitted.addAll(o.emits);
        }
        for (String id: docIds)
            if (!emitted.contains(id))
                throw new IllegalArgumentException("Nicio institutie nu emite ducumentul: "+id);

        //facem un graf de dependente pentru decumente, pentru a verifica daca avem cicluri de dependente
        Map<String,List<String>> g = new HashMap<>();
        for (var d: cfg.documents)
            g.put(d.id, d.deps==null? List.of(): d.deps);
        detectCycles(g);
    }

    static void detectCycles(Map<String, List<String>> g)
    {
        Map<String, Integer> grad_in = new HashMap<>();
        for (String k : g.keySet()) {
            grad_in.put(k, 0);
        }
        for (var deps : g.values())
            for (var u : deps)
                if (grad_in.containsKey(u))
                    grad_in.put(u, grad_in.get(u) + 1);
                else
                    grad_in.put(u, 1);


        Deque<String> q = new ArrayDeque<>();
        for (var e : grad_in.entrySet())
            if (e.getValue() == 0)
                q.add(e.getKey());

        int visited = 0;
        while (!q.isEmpty()) {
            String v = q.removeFirst();
            visited++;
            for (String u : g.getOrDefault(v, List.of())) {
                int nouGrad = grad_in.get(u) - 1;
                grad_in.put(u, nouGrad);
                if (nouGrad == 0)
                    q.add(u);

            }
        }

        if (visited != g.size())
            throw new IllegalArgumentException("cicluri detectate, fisierul json incorect");
    }
}
