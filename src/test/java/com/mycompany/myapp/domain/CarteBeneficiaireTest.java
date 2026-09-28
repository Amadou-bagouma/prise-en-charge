package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AgentTestSamples.*;
import static com.mycompany.myapp.domain.AyantDroitTestSamples.*;
import static com.mycompany.myapp.domain.CarteBeneficiaireTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarteBeneficiaireTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarteBeneficiaire.class);
        CarteBeneficiaire carteBeneficiaire1 = getCarteBeneficiaireSample1();
        CarteBeneficiaire carteBeneficiaire2 = new CarteBeneficiaire();
        assertThat(carteBeneficiaire1).isNotEqualTo(carteBeneficiaire2);

        carteBeneficiaire2.setId(carteBeneficiaire1.getId());
        assertThat(carteBeneficiaire1).isEqualTo(carteBeneficiaire2);

        carteBeneficiaire2 = getCarteBeneficiaireSample2();
        assertThat(carteBeneficiaire1).isNotEqualTo(carteBeneficiaire2);
    }

    @Test
    void agentTest() {
        CarteBeneficiaire carteBeneficiaire = getCarteBeneficiaireRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        carteBeneficiaire.setAgent(agentBack);
        assertThat(carteBeneficiaire.getAgent()).isEqualTo(agentBack);

        carteBeneficiaire.agent(null);
        assertThat(carteBeneficiaire.getAgent()).isNull();
    }

    @Test
    void ayantDroitTest() {
        CarteBeneficiaire carteBeneficiaire = getCarteBeneficiaireRandomSampleGenerator();
        AyantDroit ayantDroitBack = getAyantDroitRandomSampleGenerator();

        carteBeneficiaire.setAyantDroit(ayantDroitBack);
        assertThat(carteBeneficiaire.getAyantDroit()).isEqualTo(ayantDroitBack);

        carteBeneficiaire.ayantDroit(null);
        assertThat(carteBeneficiaire.getAyantDroit()).isNull();
    }
}
