package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EtablissementSanteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EtablissementSante getEtablissementSanteSample1() {
        return new EtablissementSante().id(1L).code("code1").nom("nom1").adresse("adresse1").telephone("telephone1");
    }

    public static EtablissementSante getEtablissementSanteSample2() {
        return new EtablissementSante().id(2L).code("code2").nom("nom2").adresse("adresse2").telephone("telephone2");
    }

    public static EtablissementSante getEtablissementSanteRandomSampleGenerator() {
        return new EtablissementSante()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .nom(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString());
    }
}
