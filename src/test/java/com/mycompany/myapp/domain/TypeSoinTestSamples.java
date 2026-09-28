package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TypeSoinTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static TypeSoin getTypeSoinSample1() {
        return new TypeSoin().id(1L).code("CODE1").libelle("libelle1").description("description1").ordre(1).actif(true);
    }

    public static TypeSoin getTypeSoinSample2() {
        return new TypeSoin().id(2L).code("CODE2").libelle("libelle2").description("description2").ordre(2).actif(true);
    }

    /**
     * Le code suit la contrainte du referentiel : majuscules, chiffres et tirets bas seulement.
     * Un UUID ne conviendrait pas, il contient des minuscules et des tirets.
     */
    public static TypeSoin getTypeSoinRandomSampleGenerator() {
        long suffixe = longCount.incrementAndGet();
        return new TypeSoin()
            .id(suffixe)
            .code("CODE_" + Math.abs(suffixe))
            .libelle("libelle" + suffixe)
            .description("description" + suffixe)
            .ordre(intCount.incrementAndGet())
            .actif(true);
    }
}
