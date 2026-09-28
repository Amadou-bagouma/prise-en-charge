package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CarteBeneficiaireCriteriaTest {

    @Test
    void newCarteBeneficiaireCriteriaHasAllFiltersNullTest() {
        var carteBeneficiaireCriteria = new CarteBeneficiaireCriteria();
        assertThat(carteBeneficiaireCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void carteBeneficiaireCriteriaFluentMethodsCreatesFiltersTest() {
        var carteBeneficiaireCriteria = new CarteBeneficiaireCriteria();

        setAllFilters(carteBeneficiaireCriteria);

        assertThat(carteBeneficiaireCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void carteBeneficiaireCriteriaCopyCreatesNullFilterTest() {
        var carteBeneficiaireCriteria = new CarteBeneficiaireCriteria();
        var copy = carteBeneficiaireCriteria.copy();

        assertThat(carteBeneficiaireCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(carteBeneficiaireCriteria)
        );
    }

    @Test
    void carteBeneficiaireCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var carteBeneficiaireCriteria = new CarteBeneficiaireCriteria();
        setAllFilters(carteBeneficiaireCriteria);

        var copy = carteBeneficiaireCriteria.copy();

        assertThat(carteBeneficiaireCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(carteBeneficiaireCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var carteBeneficiaireCriteria = new CarteBeneficiaireCriteria();

        assertThat(carteBeneficiaireCriteria).hasToString("CarteBeneficiaireCriteria{}");
    }

    private static void setAllFilters(CarteBeneficiaireCriteria carteBeneficiaireCriteria) {
        carteBeneficiaireCriteria.id();
        carteBeneficiaireCriteria.numeroCarte();
        carteBeneficiaireCriteria.typeBeneficiaire();
        carteBeneficiaireCriteria.dateDebutValidite();
        carteBeneficiaireCriteria.dateFinValidite();
        carteBeneficiaireCriteria.dateEmission();
        carteBeneficiaireCriteria.agentId();
        carteBeneficiaireCriteria.ayantDroitId();
        carteBeneficiaireCriteria.distinct();
    }

    private static Condition<CarteBeneficiaireCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNumeroCarte()) &&
                condition.apply(criteria.getTypeBeneficiaire()) &&
                condition.apply(criteria.getDateDebutValidite()) &&
                condition.apply(criteria.getDateFinValidite()) &&
                condition.apply(criteria.getDateEmission()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getAyantDroitId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CarteBeneficiaireCriteria> copyFiltersAre(
        CarteBeneficiaireCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNumeroCarte(), copy.getNumeroCarte()) &&
                condition.apply(criteria.getTypeBeneficiaire(), copy.getTypeBeneficiaire()) &&
                condition.apply(criteria.getDateDebutValidite(), copy.getDateDebutValidite()) &&
                condition.apply(criteria.getDateFinValidite(), copy.getDateFinValidite()) &&
                condition.apply(criteria.getDateEmission(), copy.getDateEmission()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getAyantDroitId(), copy.getAyantDroitId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
