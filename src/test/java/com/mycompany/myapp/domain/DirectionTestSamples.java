package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DirectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Direction getDirectionSample1() {
        return new Direction().id(1L).code("code1").nom("nom1");
    }

    public static Direction getDirectionSample2() {
        return new Direction().id(2L).code("code2").nom("nom2");
    }

    public static Direction getDirectionRandomSampleGenerator() {
        return new Direction().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).nom(UUID.randomUUID().toString());
    }
}
