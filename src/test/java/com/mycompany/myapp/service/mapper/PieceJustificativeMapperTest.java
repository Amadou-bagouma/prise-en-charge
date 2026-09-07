package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.PieceJustificativeAsserts.*;
import static com.mycompany.myapp.domain.PieceJustificativeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PieceJustificativeMapperTest {

    private PieceJustificativeMapper pieceJustificativeMapper;

    @BeforeEach
    void setUp() {
        pieceJustificativeMapper = new PieceJustificativeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPieceJustificativeSample1();
        var actual = pieceJustificativeMapper.toEntity(pieceJustificativeMapper.toDto(expected));
        assertPieceJustificativeAllPropertiesEquals(expected, actual);
    }
}
