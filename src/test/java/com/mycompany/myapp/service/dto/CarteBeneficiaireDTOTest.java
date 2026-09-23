package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarteBeneficiaireDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CarteBeneficiaireDTO.class);
        CarteBeneficiaireDTO carteBeneficiaireDTO1 = new CarteBeneficiaireDTO();
        carteBeneficiaireDTO1.setId(1L);
        CarteBeneficiaireDTO carteBeneficiaireDTO2 = new CarteBeneficiaireDTO();
        assertThat(carteBeneficiaireDTO1).isNotEqualTo(carteBeneficiaireDTO2);
        carteBeneficiaireDTO2.setId(carteBeneficiaireDTO1.getId());
        assertThat(carteBeneficiaireDTO1).isEqualTo(carteBeneficiaireDTO2);
        carteBeneficiaireDTO2.setId(2L);
        assertThat(carteBeneficiaireDTO1).isNotEqualTo(carteBeneficiaireDTO2);
        carteBeneficiaireDTO1.setId(null);
        assertThat(carteBeneficiaireDTO1).isNotEqualTo(carteBeneficiaireDTO2);
    }
}
