package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.FonctionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FonctionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Fonction.class);
        Fonction fonction1 = getFonctionSample1();
        Fonction fonction2 = new Fonction();
        assertThat(fonction1).isNotEqualTo(fonction2);

        fonction2.setId(fonction1.getId());
        assertThat(fonction1).isEqualTo(fonction2);

        fonction2 = getFonctionSample2();
        assertThat(fonction1).isNotEqualTo(fonction2);
    }
}
