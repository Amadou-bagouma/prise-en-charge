package com.mycompany.myapp.service.mapper;

import static com.mycompany.myapp.domain.CarteBeneficiaireAsserts.*;
import static com.mycompany.myapp.domain.CarteBeneficiaireTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CarteBeneficiaireMapperTest {

    private CarteBeneficiaireMapper carteBeneficiaireMapper;

    @BeforeEach
    void setUp() {
        carteBeneficiaireMapper = new CarteBeneficiaireMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCarteBeneficiaireSample1();
        var actual = carteBeneficiaireMapper.toEntity(carteBeneficiaireMapper.toDto(expected));
        assertCarteBeneficiaireAllPropertiesEquals(expected, actual);
    }
}
