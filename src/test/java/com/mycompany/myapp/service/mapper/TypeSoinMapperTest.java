package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.TypeSoinAsserts.*;
import static com.mycompany.myapp.domain.TypeSoinTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TypeSoinMapperTest {

    private TypeSoinMapper typeSoinMapper;

    @BeforeEach
    void setUp() {
        typeSoinMapper = new TypeSoinMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTypeSoinSample1();
        var actual = typeSoinMapper.toEntity(typeSoinMapper.toDto(expected));
        assertTypeSoinAllPropertiesEquals(expected, actual);
    }
}
