package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.DirectionTestSamples.*;
import static com.mycompany.myapp.domain.RegionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DirectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Direction.class);
        Direction direction1 = getDirectionSample1();
        Direction direction2 = new Direction();
        assertThat(direction1).isNotEqualTo(direction2);

        direction2.setId(direction1.getId());
        assertThat(direction1).isEqualTo(direction2);

        direction2 = getDirectionSample2();
        assertThat(direction1).isNotEqualTo(direction2);
    }

    @Test
    void regionTest() {
        Direction direction = getDirectionRandomSampleGenerator();
        Region regionBack = getRegionRandomSampleGenerator();

        direction.setRegion(regionBack);
        assertThat(direction.getRegion()).isEqualTo(regionBack);

        direction.region(null);
        assertThat(direction.getRegion()).isNull();
    }
}
