package com.mycompany.myapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PieceJustificativeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PieceJustificativeDTO.class);
        PieceJustificativeDTO pieceJustificativeDTO1 = new PieceJustificativeDTO();
        pieceJustificativeDTO1.setId(1L);
        PieceJustificativeDTO pieceJustificativeDTO2 = new PieceJustificativeDTO();
        assertThat(pieceJustificativeDTO1).isNotEqualTo(pieceJustificativeDTO2);
        pieceJustificativeDTO2.setId(pieceJustificativeDTO1.getId());
        assertThat(pieceJustificativeDTO1).isEqualTo(pieceJustificativeDTO2);
        pieceJustificativeDTO2.setId(2L);
        assertThat(pieceJustificativeDTO1).isNotEqualTo(pieceJustificativeDTO2);
        pieceJustificativeDTO1.setId(null);
        assertThat(pieceJustificativeDTO1).isNotEqualTo(pieceJustificativeDTO2);
    }
}
