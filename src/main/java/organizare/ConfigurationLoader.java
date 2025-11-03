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
        if (cfg.documents==null || cfg.documents.isEmpty()) throw new IllegalArgumentException("No documents");
        if (cfg.offices==null   || cfg.offices.isEmpty())   throw new IllegalArgumentException("No offices");

        // doc ids unice
        Set<String> docIds = new HashSet<>();
        for (var d: cfg.documents) if (!docIds.add(d.id)) throw new IllegalArgumentException("Duplicate doc: "+d.id);

        // fiecare dep trebuie să existe
        for (var d: cfg.documents)
            if (d.deps!=null) for (String dep: d.deps)
                if (!docIds.contains(dep)) throw new IllegalArgumentException("Unknown dep '"+dep+"' for "+d.id);

        // fiecare document trebuie emis de un birou
        Set<String> emitted = new HashSet<>();
        for (var o: cfg.offices) {
            if (o.counters<1) throw new IllegalArgumentException("Office "+o.id+" must have >=1 counter");
            if (o.emits!=null) emitted.addAll(o.emits);
        }
        for (String id: docIds) if (!emitted.contains(id))
            throw new IllegalArgumentException("No office emits document: "+id);

        // detectare cicluri în dependențe (DFS)
        Map<String,List<String>> g = new HashMap<>();
        for (var d: cfg.documents) g.put(d.id, d.deps==null? List.of(): d.deps);
        detectCycles(g);
    }

    private enum State { NEW, VISITING, DONE }
    private static void detectCycles(Map<String,List<String>> g){
        Map<String,State> st = new HashMap<>(); for (var k: g.keySet()) st.put(k, State.NEW);
        Deque<String> stack = new ArrayDeque<>();
        for (var k: g.keySet()) dfs(k,g,st,stack);
    }
    private static void dfs(String v, Map<String,List<String>> g, Map<String,State> st, Deque<String> stack){
        State s = st.get(v); if (s==State.DONE) return;
        if (s==State.VISITING) throw new IllegalArgumentException("Cycle in deps: "+stack+" -> "+v);
        st.put(v, State.VISITING); stack.addLast(v);
        for (String u: g.getOrDefault(v,List.of())) dfs(u,g,st,stack);
        stack.removeLast(); st.put(v, State.DONE);
    }
}
