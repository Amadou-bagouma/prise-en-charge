package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class HistoriqueActionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static HistoriqueAction getHistoriqueActionSample1() {
        return new HistoriqueAction().id(1L).action("action1").description("description1");
    }

    public static HistoriqueAction getHistoriqueActionSample2() {
        return new HistoriqueAction().id(2L).action("action2").description("description2");
    }

    public static HistoriqueAction getHistoriqueActionRandomSampleGenerator() {
        return new HistoriqueAction()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
