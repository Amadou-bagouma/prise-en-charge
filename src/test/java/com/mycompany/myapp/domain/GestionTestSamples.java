package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Gestion getGestionSample1() {
        return new Gestion().id(1L).nom("nom1");
    }

    public static Gestion getGestionSample2() {
        return new Gestion().id(2L).nom("nom2");
    }

    public static Gestion getGestionRandomSampleGenerator() {
        return new Gestion().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString());
    }
}
