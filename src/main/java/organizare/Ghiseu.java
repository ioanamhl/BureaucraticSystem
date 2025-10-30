package organizare;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class Ghiseu implements Runnable{

    private final BlockingQueue<Cerere> coada;
    private final AtomicBoolean deschis = new AtomicBoolean(true);
    private final String nume;

    public Ghiseu(String nume, BlockingQueue<Cerere> coada) {
        this.nume = nume; this.coada = coada;
    }
    public void setDeschis(boolean v) { deschis.set(v); }
    public boolean esteDeschis() { return deschis.get(); }

    @Override public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!deschis.get()) { Thread.sleep(150); continue; }
                Cerere c = coada.take();              // așteaptă următorul client
                // simulează procesare
                Thread.sleep(ThreadLocalRandom.current().nextInt(200, 600));
                c.gata.countDown();                   // livrează documentul
                System.out.println(nume + " a servit " + c.clientId + " pentru " + c.docId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
