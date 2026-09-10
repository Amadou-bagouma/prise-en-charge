package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.BoiteReceptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BoiteReceptionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BoiteReception.class);
        BoiteReception boiteReception1 = getBoiteReceptionSample1();
        BoiteReception boiteReception2 = new BoiteReception();
        assertThat(boiteReception1).isNotEqualTo(boiteReception2);

        boiteReception2.setId(boiteReception1.getId());
        assertThat(boiteReception1).isEqualTo(boiteReception2);

        boiteReception2 = getBoiteReceptionSample2();
        assertThat(boiteReception1).isNotEqualTo(boiteReception2);
    }
}
