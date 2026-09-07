package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;
import static com.mycompany.myapp.domain.PieceJustificativeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PieceJustificativeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PieceJustificative.class);
        PieceJustificative pieceJustificative1 = getPieceJustificativeSample1();
        PieceJustificative pieceJustificative2 = new PieceJustificative();
        assertThat(pieceJustificative1).isNotEqualTo(pieceJustificative2);

        pieceJustificative2.setId(pieceJustificative1.getId());
        assertThat(pieceJustificative1).isEqualTo(pieceJustificative2);

        pieceJustificative2 = getPieceJustificativeSample2();
        assertThat(pieceJustificative1).isNotEqualTo(pieceJustificative2);
    }

    @Test
    void demandeTest() {
        PieceJustificative pieceJustificative = getPieceJustificativeRandomSampleGenerator();
        DemandePriseEnCharge demandePriseEnChargeBack = getDemandePriseEnChargeRandomSampleGenerator();

        pieceJustificative.setDemande(demandePriseEnChargeBack);
        assertThat(pieceJustificative.getDemande()).isEqualTo(demandePriseEnChargeBack);

        pieceJustificative.demande(null);
        assertThat(pieceJustificative.getDemande()).isNull();
    }
}
