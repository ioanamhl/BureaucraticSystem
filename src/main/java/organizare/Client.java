package organizare;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ConcurrentHashMap;

public class Client implements Runnable {
    private final String id;
    private final String tinta;
    private final Map<String, Birou> doc2birou;
    private final Map<String, Set<String>> deps;
    private final Set<String> have = ConcurrentHashMap.newKeySet();

    public Client(String id, String tinta, Map<String, Birou> d2o, Map<String, Set<String>> deps) {
        this.id = id; this.tinta = tinta; this.doc2birou = d2o; this.deps = deps;
    }

    @Override public void run() {
        try {
            while (!have.contains(tinta)) {
                String next = urmatorulEligibil();
                if (next == null) { Thread.sleep(50); continue; }
                Birou b = doc2birou.get(next);
                CountDownLatch gata = new CountDownLatch(1);
                b.coada.add(new Cerere(id, next, gata));
                gata.await();               // așteaptă să fie servit
                have.add(next);
                System.out.println(id + " a obținut " + next);
            }
            System.out.println(id + " FINALIZAT tinta " + tinta);
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private String urmatorulEligibil() {
        for (var e : deps.entrySet()) {
            String doc = e.getKey();
            if (have.contains(doc)) continue;
            if (e.getValue().stream().allMatch(have::contains)) return doc;
        }
        return null;
    }
}
