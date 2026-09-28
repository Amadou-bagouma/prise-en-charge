package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.BoiteReceptionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.domain.Profil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoiteReceptionMapperTest {

    private BoiteReceptionMapper boiteReceptionMapper;

    @BeforeEach
    void setUp() {
        boiteReceptionMapper = new BoiteReceptionMapperImpl();
    }

    /**
     * Le mapper ne va que dans un sens : une boite n'est jamais construite a partir d'un DTO,
     * il n'y a donc pas d'aller-retour a verifier.
     */
    @Test
    void shouldConvertToDto() {
        var expected = getBoiteReceptionSample1();
        expected.setProfil(new Profil().id(7L).nom("Gestionnaire"));

        var actual = boiteReceptionMapper.toDto(expected);

        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getNombreNonLus()).isEqualTo(expected.getNombreNonLus());
        assertThat(actual.getProfil().getId()).isEqualTo(7L);
        assertThat(actual.getProfil().getNom()).isEqualTo("Gestionnaire");
    }

    /** Les droits du profil n'ont pas a transiter par une boite de reception. */
    @Test
    void shouldNotExposeProfilAuthorities() {
        var boite = getBoiteReceptionSample1();
        boite.setProfil(new Profil().id(7L).nom("Gestionnaire"));

        assertThat(boiteReceptionMapper.toDto(boite).getProfil().getAuthorities()).isNull();
    }
}
