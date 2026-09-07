package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.AgentTestSamples.*;
import static com.mycompany.myapp.domain.DirectionTestSamples.*;
import static com.mycompany.myapp.domain.GestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AgentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Agent.class);
        Agent agent1 = getAgentSample1();
        Agent agent2 = new Agent();
        assertThat(agent1).isNotEqualTo(agent2);

        agent2.setId(agent1.getId());
        assertThat(agent1).isEqualTo(agent2);

        agent2 = getAgentSample2();
        assertThat(agent1).isNotEqualTo(agent2);
    }

    @Test
    void directionTest() {
        Agent agent = getAgentRandomSampleGenerator();
        Direction directionBack = getDirectionRandomSampleGenerator();

        agent.setDirection(directionBack);
        assertThat(agent.getDirection()).isEqualTo(directionBack);

        agent.direction(null);
        assertThat(agent.getDirection()).isNull();
    }

    @Test
    void gestionTest() {
        Agent agent = getAgentRandomSampleGenerator();
        Gestion gestionBack = getGestionRandomSampleGenerator();

        agent.setGestion(gestionBack);
        assertThat(agent.getGestion()).isEqualTo(gestionBack);

        agent.gestion(null);
        assertThat(agent.getGestion()).isNull();
    }
}
