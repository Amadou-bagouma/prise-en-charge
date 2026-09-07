package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AyantDroitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AyantDroitDTO.class);
        AyantDroitDTO ayantDroitDTO1 = new AyantDroitDTO();
        ayantDroitDTO1.setId(1L);
        AyantDroitDTO ayantDroitDTO2 = new AyantDroitDTO();
        assertThat(ayantDroitDTO1).isNotEqualTo(ayantDroitDTO2);
        ayantDroitDTO2.setId(ayantDroitDTO1.getId());
        assertThat(ayantDroitDTO1).isEqualTo(ayantDroitDTO2);
        ayantDroitDTO2.setId(2L);
        assertThat(ayantDroitDTO1).isNotEqualTo(ayantDroitDTO2);
        ayantDroitDTO1.setId(null);
        assertThat(ayantDroitDTO1).isNotEqualTo(ayantDroitDTO2);
    }
}
