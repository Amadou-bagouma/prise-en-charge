package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class EtablissementSanteCriteriaTest {

    @Test
    void newEtablissementSanteCriteriaHasAllFiltersNullTest() {
        var etablissementSanteCriteria = new EtablissementSanteCriteria();
        assertThat(etablissementSanteCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void etablissementSanteCriteriaFluentMethodsCreatesFiltersTest() {
        var etablissementSanteCriteria = new EtablissementSanteCriteria();

        setAllFilters(etablissementSanteCriteria);

        assertThat(etablissementSanteCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void etablissementSanteCriteriaCopyCreatesNullFilterTest() {
        var etablissementSanteCriteria = new EtablissementSanteCriteria();
        var copy = etablissementSanteCriteria.copy();

        assertThat(etablissementSanteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(etablissementSanteCriteria)
        );
    }

    @Test
    void etablissementSanteCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var etablissementSanteCriteria = new EtablissementSanteCriteria();
        setAllFilters(etablissementSanteCriteria);

        var copy = etablissementSanteCriteria.copy();

        assertThat(etablissementSanteCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(etablissementSanteCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var etablissementSanteCriteria = new EtablissementSanteCriteria();

        assertThat(etablissementSanteCriteria).hasToString("EtablissementSanteCriteria{}");
    }

    private static void setAllFilters(EtablissementSanteCriteria etablissementSanteCriteria) {
        etablissementSanteCriteria.id();
        etablissementSanteCriteria.code();
        etablissementSanteCriteria.nom();
        etablissementSanteCriteria.adresse();
        etablissementSanteCriteria.telephone();
        etablissementSanteCriteria.actif();
        etablissementSanteCriteria.distinct();
    }

    private static Condition<EtablissementSanteCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getCode()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getAdresse()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getActif()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<EtablissementSanteCriteria> copyFiltersAre(
        EtablissementSanteCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getCode(), copy.getCode()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getAdresse(), copy.getAdresse()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getActif(), copy.getActif()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
