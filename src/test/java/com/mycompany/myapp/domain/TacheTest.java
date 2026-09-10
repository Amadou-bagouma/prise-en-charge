package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BoiteReceptionTestSamples.*;
import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;
import static com.mycompany.myapp.domain.TacheTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TacheTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tache.class);
        Tache tache1 = getTacheSample1();
        Tache tache2 = new Tache();
        assertThat(tache1).isNotEqualTo(tache2);

        tache2.setId(tache1.getId());
        assertThat(tache1).isEqualTo(tache2);

        tache2 = getTacheSample2();
        assertThat(tache1).isNotEqualTo(tache2);
    }

    @Test
    void demandeTest() {
        Tache tache = getTacheRandomSampleGenerator();
        DemandePriseEnCharge demandePriseEnChargeBack = getDemandePriseEnChargeRandomSampleGenerator();

        tache.setDemande(demandePriseEnChargeBack);
        assertThat(tache.getDemande()).isEqualTo(demandePriseEnChargeBack);

        tache.demande(null);
        assertThat(tache.getDemande()).isNull();
    }

    @Test
    void boiteReceptionTest() {
        Tache tache = getTacheRandomSampleGenerator();
        BoiteReception boiteReceptionBack = getBoiteReceptionRandomSampleGenerator();

        tache.setBoiteReception(boiteReceptionBack);
        assertThat(tache.getBoiteReception()).isEqualTo(boiteReceptionBack);

        tache.boiteReception(null);
        assertThat(tache.getBoiteReception()).isNull();
    }
}
