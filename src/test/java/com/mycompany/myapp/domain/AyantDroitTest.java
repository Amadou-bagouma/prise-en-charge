package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AgentTestSamples.*;
import static com.mycompany.myapp.domain.AyantDroitTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AyantDroitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AyantDroit.class);
        AyantDroit ayantDroit1 = getAyantDroitSample1();
        AyantDroit ayantDroit2 = new AyantDroit();
        assertThat(ayantDroit1).isNotEqualTo(ayantDroit2);

        ayantDroit2.setId(ayantDroit1.getId());
        assertThat(ayantDroit1).isEqualTo(ayantDroit2);

        ayantDroit2 = getAyantDroitSample2();
        assertThat(ayantDroit1).isNotEqualTo(ayantDroit2);
    }

    @Test
    void agentTest() {
        AyantDroit ayantDroit = getAyantDroitRandomSampleGenerator();
        Agent agentBack = getAgentRandomSampleGenerator();

        ayantDroit.setAgent(agentBack);
        assertThat(ayantDroit.getAgent()).isEqualTo(agentBack);

        ayantDroit.agent(null);
        assertThat(ayantDroit.getAgent()).isNull();
    }
}
