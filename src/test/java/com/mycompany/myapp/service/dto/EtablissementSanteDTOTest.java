package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EtablissementSanteDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EtablissementSanteDTO.class);
        EtablissementSanteDTO etablissementSanteDTO1 = new EtablissementSanteDTO();
        etablissementSanteDTO1.setId(1L);
        EtablissementSanteDTO etablissementSanteDTO2 = new EtablissementSanteDTO();
        assertThat(etablissementSanteDTO1).isNotEqualTo(etablissementSanteDTO2);
        etablissementSanteDTO2.setId(etablissementSanteDTO1.getId());
        assertThat(etablissementSanteDTO1).isEqualTo(etablissementSanteDTO2);
        etablissementSanteDTO2.setId(2L);
        assertThat(etablissementSanteDTO1).isNotEqualTo(etablissementSanteDTO2);
        etablissementSanteDTO1.setId(null);
        assertThat(etablissementSanteDTO1).isNotEqualTo(etablissementSanteDTO2);
    }
}
