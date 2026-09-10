package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class HistoriqueActionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(HistoriqueActionDTO.class);
        HistoriqueActionDTO historiqueActionDTO1 = new HistoriqueActionDTO();
        historiqueActionDTO1.setId(1L);
        HistoriqueActionDTO historiqueActionDTO2 = new HistoriqueActionDTO();
        assertThat(historiqueActionDTO1).isNotEqualTo(historiqueActionDTO2);
        historiqueActionDTO2.setId(historiqueActionDTO1.getId());
        assertThat(historiqueActionDTO1).isEqualTo(historiqueActionDTO2);
        historiqueActionDTO2.setId(2L);
        assertThat(historiqueActionDTO1).isNotEqualTo(historiqueActionDTO2);
        historiqueActionDTO1.setId(null);
        assertThat(historiqueActionDTO1).isNotEqualTo(historiqueActionDTO2);
    }
}
