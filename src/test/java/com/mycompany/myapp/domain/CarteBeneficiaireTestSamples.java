package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CarteBeneficiaireTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static CarteBeneficiaire getCarteBeneficiaireSample1() {
        return new CarteBeneficiaire().id(1L).numeroCarte("numeroCarte1");
    }

    public static CarteBeneficiaire getCarteBeneficiaireSample2() {
        return new CarteBeneficiaire().id(2L).numeroCarte("numeroCarte2");
    }

    public static CarteBeneficiaire getCarteBeneficiaireRandomSampleGenerator() {
        return new CarteBeneficiaire().id(longCount.incrementAndGet()).numeroCarte(UUID.randomUUID().toString());
    }
}
