package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;
import static com.mycompany.myapp.domain.HistoriqueActionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoriqueActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoriqueAction.class);
        HistoriqueAction historiqueAction1 = getHistoriqueActionSample1();
        HistoriqueAction historiqueAction2 = new HistoriqueAction();
        assertThat(historiqueAction1).isNotEqualTo(historiqueAction2);

        historiqueAction2.setId(historiqueAction1.getId());
        assertThat(historiqueAction1).isEqualTo(historiqueAction2);

        historiqueAction2 = getHistoriqueActionSample2();
        assertThat(historiqueAction1).isNotEqualTo(historiqueAction2);
    }

    @Test
    void demandeTest() {
        HistoriqueAction historiqueAction = getHistoriqueActionRandomSampleGenerator();
        DemandePriseEnCharge demandePriseEnChargeBack = getDemandePriseEnChargeRandomSampleGenerator();

        historiqueAction.setDemande(demandePriseEnChargeBack);
        assertThat(historiqueAction.getDemande()).isEqualTo(demandePriseEnChargeBack);

        historiqueAction.demande(null);
        assertThat(historiqueAction.getDemande()).isNull();
    }
}
