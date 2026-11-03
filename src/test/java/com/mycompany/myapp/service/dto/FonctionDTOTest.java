package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FonctionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FonctionDTO.class);
        FonctionDTO fonctionDTO1 = new FonctionDTO();
        fonctionDTO1.setId(1L);
        FonctionDTO fonctionDTO2 = new FonctionDTO();
        assertThat(fonctionDTO1).isNotEqualTo(fonctionDTO2);
        fonctionDTO2.setId(fonctionDTO1.getId());
        assertThat(fonctionDTO1).isEqualTo(fonctionDTO2);
        fonctionDTO2.setId(2L);
        assertThat(fonctionDTO1).isNotEqualTo(fonctionDTO2);
        fonctionDTO1.setId(null);
        assertThat(fonctionDTO1).isNotEqualTo(fonctionDTO2);
    }
}
