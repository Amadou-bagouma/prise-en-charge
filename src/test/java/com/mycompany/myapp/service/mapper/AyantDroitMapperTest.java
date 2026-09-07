package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.AyantDroitAsserts.*;
import static com.mycompany.myapp.domain.AyantDroitTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AyantDroitMapperTest {

    private AyantDroitMapper ayantDroitMapper;

    @BeforeEach
    void setUp() {
        ayantDroitMapper = new AyantDroitMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAyantDroitSample1();
        var actual = ayantDroitMapper.toEntity(ayantDroitMapper.toDto(expected));
        assertAyantDroitAllPropertiesEquals(expected, actual);
    }
}
