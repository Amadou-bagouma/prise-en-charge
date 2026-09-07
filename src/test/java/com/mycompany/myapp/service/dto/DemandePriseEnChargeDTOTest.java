package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DemandePriseEnChargeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DemandePriseEnChargeDTO.class);
        DemandePriseEnChargeDTO demandePriseEnChargeDTO1 = new DemandePriseEnChargeDTO();
        demandePriseEnChargeDTO1.setId(1L);
        DemandePriseEnChargeDTO demandePriseEnChargeDTO2 = new DemandePriseEnChargeDTO();
        assertThat(demandePriseEnChargeDTO1).isNotEqualTo(demandePriseEnChargeDTO2);
        demandePriseEnChargeDTO2.setId(demandePriseEnChargeDTO1.getId());
        assertThat(demandePriseEnChargeDTO1).isEqualTo(demandePriseEnChargeDTO2);
        demandePriseEnChargeDTO2.setId(2L);
        assertThat(demandePriseEnChargeDTO1).isNotEqualTo(demandePriseEnChargeDTO2);
        demandePriseEnChargeDTO1.setId(null);
        assertThat(demandePriseEnChargeDTO1).isNotEqualTo(demandePriseEnChargeDTO2);
    }
}
