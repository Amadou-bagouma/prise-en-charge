package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AgentTestSamples.*;
import static com.mycompany.myapp.domain.AyantDroitTestSamples.*;
import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;
import static com.mycompany.myapp.domain.EtablissementSanteTestSamples.*;
import static com.mycompany.myapp.domain.TypeSoinTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DemandePriseEnChargeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DemandePriseEnCharge.class);
        DemandePriseEnCharge demandePriseEnCharge1 = getDemandePriseEnChargeSample1();
        DemandePriseEnCharge demandePriseEnCharge2 = new DemandePriseEnCharge();
        assertThat(demandePriseEnCharge1).isNotEqualTo(demandePriseEnCharge2);

        demandePriseEnCharge2.setId(demandePriseEnCharge1.getId());
        assertThat(demandePriseEnCharge1).isEqualTo(demandePriseEnCharge2);

        demandePriseEnCharge2 = getDemandePriseEnChargeSample2();
        assertThat(demandePriseEnCharge1).isNotEqualTo(demandePriseEnCharge2);
    }

    @Test
    void agentTest() {
        DemandePriseEnCharge demandePriseEnCharge = getDemandePriseEnChargeRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        demandePriseEnCharge.setAgent(agentBack);
        assertThat(demandePriseEnCharge.getAgent()).isEqualTo(agentBack);

        demandePriseEnCharge.agent(null);
        assertThat(demandePriseEnCharge.getAgent()).isNull();
    }

    @Test
    void ayantDroitTest() {
        DemandePriseEnCharge demandePriseEnCharge = getDemandePriseEnChargeRandomSampleGenerator();
        AyantDroit ayantDroitBack = getAyantDroitRandomSampleGenerator();

        demandePriseEnCharge.setAyantDroit(ayantDroitBack);
        assertThat(demandePriseEnCharge.getAyantDroit()).isEqualTo(ayantDroitBack);

        demandePriseEnCharge.ayantDroit(null);
        assertThat(demandePriseEnCharge.getAyantDroit()).isNull();
    }

    @Test
    void typeSoinTest() {
        DemandePriseEnCharge demandePriseEnCharge = getDemandePriseEnChargeRandomSampleGenerator();
        TypeSoin typeSoinBack = getTypeSoinRandomSampleGenerator();

        demandePriseEnCharge.addTypeSoin(typeSoinBack);
        assertThat(demandePriseEnCharge.getTypeSoins()).containsOnly(typeSoinBack);

        demandePriseEnCharge.removeTypeSoin(typeSoinBack);
        assertThat(demandePriseEnCharge.getTypeSoins()).doesNotContain(typeSoinBack);

        demandePriseEnCharge.typeSoins(new HashSet<>(Set.of(typeSoinBack)));
        assertThat(demandePriseEnCharge.getTypeSoins()).containsOnly(typeSoinBack);

        demandePriseEnCharge.setTypeSoins(new HashSet<>());
        assertThat(demandePriseEnCharge.getTypeSoins()).doesNotContain(typeSoinBack);
    }

    @Test
    void etablissementSanteTest() {
        DemandePriseEnCharge demandePriseEnCharge = getDemandePriseEnChargeRandomSampleGenerator();
        EtablissementSante etablissementSanteBack = getEtablissementSanteRandomSampleGenerator();

        demandePriseEnCharge.setEtablissementSante(etablissementSanteBack);
        assertThat(demandePriseEnCharge.getEtablissementSante()).isEqualTo(etablissementSanteBack);

        demandePriseEnCharge.etablissementSante(null);
        assertThat(demandePriseEnCharge.getEtablissementSante()).isNull();
    }
}
