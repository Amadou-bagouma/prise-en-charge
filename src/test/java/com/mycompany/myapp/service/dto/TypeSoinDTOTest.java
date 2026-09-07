package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeSoinDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeSoinDTO.class);
        TypeSoinDTO typeSoinDTO1 = new TypeSoinDTO();
        typeSoinDTO1.setId(1L);
        TypeSoinDTO typeSoinDTO2 = new TypeSoinDTO();
        assertThat(typeSoinDTO1).isNotEqualTo(typeSoinDTO2);
        typeSoinDTO2.setId(typeSoinDTO1.getId());
        assertThat(typeSoinDTO1).isEqualTo(typeSoinDTO2);
        typeSoinDTO2.setId(2L);
        assertThat(typeSoinDTO1).isNotEqualTo(typeSoinDTO2);
        typeSoinDTO1.setId(null);
        assertThat(typeSoinDTO1).isNotEqualTo(typeSoinDTO2);
    }
}
