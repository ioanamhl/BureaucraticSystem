package organizare;

import java.util.*;
import java.util.concurrent.*;

public class Simulare {
    private final Configuration cfg;
    public Simulare(Configuration cfg) { this.cfg = cfg; }

    public void ruleaza() throws InterruptedException {
        // mapări utile
        Map<String, Set<String>> deps = new HashMap<>();
        for (var d : cfg.documents) deps.put(d.id, d.deps==null? Set.of() : new HashSet<>(d.deps));

        Map<String, Birou> doc2birou = new HashMap<>();
        List<Birou> birouri = new ArrayList<>();
        List<Ghiseu> toateGhiseele = new ArrayList<>();
        for (var o : cfg.offices) {
            Birou b = new Birou(o.id, new HashSet<>(o.emits), o.counters);
            birouri.add(b);
            toateGhiseele.addAll(b.ghisee);
            for (String d : o.emits) doc2birou.put(d, b);
        }

        // pauze
        PauzeScheduler ps = new PauzeScheduler();
        if (cfg.breaks != null) {
            ps.programeaza(toateGhiseele, cfg.breaks.everySec, cfg.breaks.minSec, cfg.breaks.maxSec);
        }

        // clienți
        ExecutorService poolClienti = Executors.newCachedThreadPool();
        int n = cfg.clients.count;
        int min = cfg.clients.arrivalMs.min, max = cfg.clients.arrivalMs.max;
        String[] targets = cfg.clients.targets;

        for (int i=1;i<=n;i++) {
            String tinta = targets[Math.abs(i) % targets.length];
            poolClienti.submit(new Client("C"+i, tinta, doc2birou, deps));
            Thread.sleep(ThreadLocalRandom.current().nextInt(min, max+1)); // sosire
        }

        poolClienti.shutdown();
        poolClienti.awaitTermination(5, TimeUnit.MINUTES);

        // oprire curată
        ps.stop();
        for (Birou b : birouri) b.inchide();
        System.out.println("Simulare terminată.");
    }
}
