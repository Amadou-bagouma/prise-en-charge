package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DirectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DirectionDTO.class);
        DirectionDTO directionDTO1 = new DirectionDTO();
        directionDTO1.setId(1L);
        DirectionDTO directionDTO2 = new DirectionDTO();
        assertThat(directionDTO1).isNotEqualTo(directionDTO2);
        directionDTO2.setId(directionDTO1.getId());
        assertThat(directionDTO1).isEqualTo(directionDTO2);
        directionDTO2.setId(2L);
        assertThat(directionDTO1).isNotEqualTo(directionDTO2);
        directionDTO1.setId(null);
        assertThat(directionDTO1).isNotEqualTo(directionDTO2);
    }
}
