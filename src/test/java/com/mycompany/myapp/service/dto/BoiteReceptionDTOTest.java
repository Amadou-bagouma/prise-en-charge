package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoiteReceptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoiteReceptionDTO.class);
        BoiteReceptionDTO boiteReceptionDTO1 = new BoiteReceptionDTO();
        boiteReceptionDTO1.setId(1L);
        BoiteReceptionDTO boiteReceptionDTO2 = new BoiteReceptionDTO();
        assertThat(boiteReceptionDTO1).isNotEqualTo(boiteReceptionDTO2);
        boiteReceptionDTO2.setId(boiteReceptionDTO1.getId());
        assertThat(boiteReceptionDTO1).isEqualTo(boiteReceptionDTO2);
        boiteReceptionDTO2.setId(2L);
        assertThat(boiteReceptionDTO1).isNotEqualTo(boiteReceptionDTO2);
        boiteReceptionDTO1.setId(null);
        assertThat(boiteReceptionDTO1).isNotEqualTo(boiteReceptionDTO2);
    }
}
