package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.EtablissementSanteAsserts.*;
import static com.mycompany.myapp.domain.EtablissementSanteTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EtablissementSanteMapperTest {

    private EtablissementSanteMapper etablissementSanteMapper;

    @BeforeEach
    void setUp() {
        etablissementSanteMapper = new EtablissementSanteMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEtablissementSanteSample1();
        var actual = etablissementSanteMapper.toEntity(etablissementSanteMapper.toDto(expected));
        assertEtablissementSanteAllPropertiesEquals(expected, actual);
    }
}
