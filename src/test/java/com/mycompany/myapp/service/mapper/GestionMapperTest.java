package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.GestionAsserts.*;
import static com.mycompany.myapp.domain.GestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestionMapperTest {

    private GestionMapper gestionMapper;

    @BeforeEach
    void setUp() {
        gestionMapper = new GestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getGestionSample1();
        var actual = gestionMapper.toEntity(gestionMapper.toDto(expected));
        assertGestionAllPropertiesEquals(expected, actual);
    }
}
