package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.DirectionAsserts.*;
import static com.mycompany.myapp.domain.DirectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DirectionMapperTest {

    private DirectionMapper directionMapper;

    @BeforeEach
    void setUp() {
        directionMapper = new DirectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDirectionSample1();
        var actual = directionMapper.toEntity(directionMapper.toDto(expected));
        assertDirectionAllPropertiesEquals(expected, actual);
    }
}
