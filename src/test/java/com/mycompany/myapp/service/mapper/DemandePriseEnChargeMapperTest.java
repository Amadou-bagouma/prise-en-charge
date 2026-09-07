package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.DemandePriseEnChargeAsserts.*;
import static com.mycompany.myapp.domain.DemandePriseEnChargeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DemandePriseEnChargeMapperTest {

    private DemandePriseEnChargeMapper demandePriseEnChargeMapper;

    @BeforeEach
    void setUp() {
        demandePriseEnChargeMapper = new DemandePriseEnChargeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDemandePriseEnChargeSample1();
        var actual = demandePriseEnChargeMapper.toEntity(demandePriseEnChargeMapper.toDto(expected));
        assertDemandePriseEnChargeAllPropertiesEquals(expected, actual);
    }
}
