package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DemandePriseEnChargeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DemandePriseEnCharge getDemandePriseEnChargeSample1() {
        return new DemandePriseEnCharge().id(1L).reference("reference1").description("description1");
    }

    public static DemandePriseEnCharge getDemandePriseEnChargeSample2() {
        return new DemandePriseEnCharge().id(2L).reference("reference2").description("description2");
    }

    public static DemandePriseEnCharge getDemandePriseEnChargeRandomSampleGenerator() {
        return new DemandePriseEnCharge()
            .id(longCount.incrementAndGet())
            .reference(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
