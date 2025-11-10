package organizare;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ThreadLocalRandom;

public class PauzeScheduler {
    private final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor();

    public void programeaza(List<Ghiseu> ghisee, int everySec, int minSec, int maxSec)
    {
        if (ghisee.isEmpty())
            return;
        sch.scheduleAtFixedRate(() -> {
            int idx = ThreadLocalRandom.current().nextInt(ghisee.size());
            Ghiseu g = ghisee.get(idx);
            int dur = ThreadLocalRandom.current().nextInt(minSec, maxSec+1);
            g.setDeschis(false);
            System.out.println(g + " a luat PAUZA ");
            sch.schedule(() -> g.setDeschis(true), dur, TimeUnit.SECONDS);
        }, everySec, everySec, TimeUnit.SECONDS);
    }
    public void stop()
    {
        sch.shutdownNow();
    }
}
