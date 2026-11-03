package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FonctionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Fonction getFonctionSample1() {
        return new Fonction().id(1L).libelle("libelle1");
    }

    public static Fonction getFonctionSample2() {
        return new Fonction().id(2L).libelle("libelle2");
    }

    public static Fonction getFonctionRandomSampleGenerator() {
        return new Fonction().id(longCount.incrementAndGet()).libelle(UUID.randomUUID().toString());
    }
}
