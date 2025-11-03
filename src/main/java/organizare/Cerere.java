package organizare;

import java.util.concurrent.CountDownLatch;

public class Cerere {
    public final String clientId;
    public final String docId;
    public final CountDownLatch gata;
    public final long st_time;

    public Cerere(String clientId, String docId, CountDownLatch gata)
    {
        this.clientId = clientId;
        this.docId = docId;
        this.gata = gata;
        this.st_time = System.nanoTime();
    }
}
