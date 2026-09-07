package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EtablissementSanteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtablissementSanteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtablissementSante.class);
        EtablissementSante etablissementSante1 = getEtablissementSanteSample1();
        EtablissementSante etablissementSante2 = new EtablissementSante();
        assertThat(etablissementSante1).isNotEqualTo(etablissementSante2);

        etablissementSante2.setId(etablissementSante1.getId());
        assertThat(etablissementSante1).isEqualTo(etablissementSante2);

        etablissementSante2 = getEtablissementSanteSample2();
        assertThat(etablissementSante1).isNotEqualTo(etablissementSante2);
    }
}
