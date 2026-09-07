package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TypeSoinTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TypeSoin getTypeSoinSample1() {
        return new TypeSoin().id(1L).libelle("libelle1").description("description1");
    }

    public static TypeSoin getTypeSoinSample2() {
        return new TypeSoin().id(2L).libelle("libelle2").description("description2");
    }

    public static TypeSoin getTypeSoinRandomSampleGenerator() {
        return new TypeSoin()
            .id(longCount.incrementAndGet())
            .libelle(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
