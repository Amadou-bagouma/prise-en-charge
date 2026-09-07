package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.TypeSoinTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TypeSoinTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TypeSoin.class);
        TypeSoin typeSoin1 = getTypeSoinSample1();
        TypeSoin typeSoin2 = new TypeSoin();
        assertThat(typeSoin1).isNotEqualTo(typeSoin2);

        typeSoin2.setId(typeSoin1.getId());
        assertThat(typeSoin1).isEqualTo(typeSoin2);

        typeSoin2 = getTypeSoinSample2();
        assertThat(typeSoin1).isNotEqualTo(typeSoin2);
    }
}
