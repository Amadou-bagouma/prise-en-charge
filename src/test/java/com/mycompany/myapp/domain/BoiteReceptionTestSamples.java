package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BoiteReceptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static BoiteReception getBoiteReceptionSample1() {
        return new BoiteReception().id(1L).nombreNonLus(1);
    }

    public static BoiteReception getBoiteReceptionSample2() {
        return new BoiteReception().id(2L).nombreNonLus(2);
    }

    public static BoiteReception getBoiteReceptionRandomSampleGenerator() {
        return new BoiteReception().id(longCount.incrementAndGet()).nombreNonLus(intCount.incrementAndGet());
    }
}
