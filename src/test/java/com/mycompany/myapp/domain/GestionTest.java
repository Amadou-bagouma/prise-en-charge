package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.GestionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GestionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Gestion.class);
        Gestion gestion1 = getGestionSample1();
        Gestion gestion2 = new Gestion();
        assertThat(gestion1).isNotEqualTo(gestion2);

        gestion2.setId(gestion1.getId());
        assertThat(gestion1).isEqualTo(gestion2);

        gestion2 = getGestionSample2();
        assertThat(gestion1).isNotEqualTo(gestion2);
    }
}
