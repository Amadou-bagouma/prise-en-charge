package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PieceJustificativeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PieceJustificative getPieceJustificativeSample1() {
        return new PieceJustificative().id(1L).nomFichier("nomFichier1").cheminFichier("cheminFichier1");
    }

    public static PieceJustificative getPieceJustificativeSample2() {
        return new PieceJustificative().id(2L).nomFichier("nomFichier2").cheminFichier("cheminFichier2");
    }

    public static PieceJustificative getPieceJustificativeRandomSampleGenerator() {
        return new PieceJustificative()
            .id(longCount.incrementAndGet())
            .nomFichier(UUID.randomUUID().toString())
            .cheminFichier(UUID.randomUUID().toString());
    }
}
