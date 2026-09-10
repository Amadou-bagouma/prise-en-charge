package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.HistoriqueActionAsserts.*;
import static com.mycompany.myapp.domain.HistoriqueActionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistoriqueActionMapperTest {

    private HistoriqueActionMapper historiqueActionMapper;

    @BeforeEach
    void setUp() {
        historiqueActionMapper = new HistoriqueActionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getHistoriqueActionSample1();
        var actual = historiqueActionMapper.toEntity(historiqueActionMapper.toDto(expected));
        assertHistoriqueActionAllPropertiesEquals(expected, actual);
    }
}
