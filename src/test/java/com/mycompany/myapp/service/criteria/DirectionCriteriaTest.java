package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DirectionCriteriaTest {

    @Test
    void newDirectionCriteriaHasAllFiltersNullTest() {
        var directionCriteria = new DirectionCriteria();
        assertThat(directionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void directionCriteriaFluentMethodsCreatesFiltersTest() {
        var directionCriteria = new DirectionCriteria();

        setAllFilters(directionCriteria);

        assertThat(directionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void directionCriteriaCopyCreatesNullFilterTest() {
        var directionCriteria = new DirectionCriteria();
        var copy = directionCriteria.copy();

        assertThat(directionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(directionCriteria)
        );
    }

    @Test
    void directionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var directionCriteria = new DirectionCriteria();
        setAllFilters(directionCriteria);

        var copy = directionCriteria.copy();

        assertThat(directionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(directionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var directionCriteria = new DirectionCriteria();

        assertThat(directionCriteria).hasToString("DirectionCriteria{}");
    }

    private static void setAllFilters(DirectionCriteria directionCriteria) {
        directionCriteria.id();
        directionCriteria.code();
        directionCriteria.nom();
        directionCriteria.regionId();
        directionCriteria.distinct();
    }

    private static Condition<DirectionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getRegionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DirectionCriteria> copyFiltersAre(DirectionCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getRegionId(), copy.getRegionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
