package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AyantDroitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AyantDroit getAyantDroitSample1() {
        return new AyantDroit().id(1L).nom("nom1").prenom("prenom1");
    }

    public static AyantDroit getAyantDroitSample2() {
        return new AyantDroit().id(2L).nom("nom2").prenom("prenom2");
    }

    public static AyantDroit getAyantDroitRandomSampleGenerator() {
        return new AyantDroit().id(longCount.incrementAndGet()).nom(UUID.randomUUID().toString()).prenom(UUID.randomUUID().toString());
    }
}
