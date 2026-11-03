package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.FonctionAsserts.*;
import static com.mycompany.myapp.domain.FonctionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FonctionMapperTest {

    private FonctionMapper fonctionMapper;

    @BeforeEach
    void setUp() {
        fonctionMapper = new FonctionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFonctionSample1();
        var actual = fonctionMapper.toEntity(fonctionMapper.toDto(expected));
        assertFonctionAllPropertiesEquals(expected, actual);
    }
}
