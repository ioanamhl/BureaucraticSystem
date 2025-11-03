package organizare;

import java.util.*;
import java.util.concurrent.*;

public class Birou {
    public final String id;
    public final Set<String> emite;
    public final BlockingQueue<Cerere> coada = new LinkedBlockingQueue<>();
    public final ExecutorService pool;
    public final List<Ghiseu> ghisee = new ArrayList<>();

    public Birou(String id, Set<String> emite, int counters) {
        this.id = id; this.emite = emite;
        this.pool = Executors.newFixedThreadPool(counters, r -> {
            Thread t = new Thread(r);
            t.setName("Ghiseu-"+id+"-angajat");
            return t;
        });
        for (int i=0;i<counters;i++) {
            Ghiseu g = new Ghiseu("Ghiseu-"+id+"#"+(i+1), coada);
            ghisee.add(g);
            pool.submit(g);
        }
    }
    public void inchide() { pool.shutdownNow(); }
}
