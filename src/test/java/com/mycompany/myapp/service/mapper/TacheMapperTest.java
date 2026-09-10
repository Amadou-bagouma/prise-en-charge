package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TacheAsserts.*;
import static com.mycompany.myapp.domain.TacheTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TacheMapperTest {

    private TacheMapper tacheMapper;

    @BeforeEach
    void setUp() {
        tacheMapper = new TacheMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTacheSample1();
        var actual = tacheMapper.toEntity(tacheMapper.toDto(expected));
        assertTacheAllPropertiesEquals(expected, actual);
    }
}
