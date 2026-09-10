package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.BoiteReceptionAsserts.*;
import static com.mycompany.myapp.domain.BoiteReceptionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoiteReceptionMapperTest {

    private BoiteReceptionMapper boiteReceptionMapper;

    @BeforeEach
    void setUp() {
        boiteReceptionMapper = new BoiteReceptionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getBoiteReceptionSample1();
        var actual = boiteReceptionMapper.toEntity(boiteReceptionMapper.toDto(expected));
        assertBoiteReceptionAllPropertiesEquals(expected, actual);
    }
}
