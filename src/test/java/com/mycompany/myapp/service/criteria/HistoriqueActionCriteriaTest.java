package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class HistoriqueActionCriteriaTest {

    @Test
    void newHistoriqueActionCriteriaHasAllFiltersNullTest() {
        var historiqueActionCriteria = new HistoriqueActionCriteria();
        assertThat(historiqueActionCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void historiqueActionCriteriaFluentMethodsCreatesFiltersTest() {
        var historiqueActionCriteria = new HistoriqueActionCriteria();

        setAllFilters(historiqueActionCriteria);

        assertThat(historiqueActionCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void historiqueActionCriteriaCopyCreatesNullFilterTest() {
        var historiqueActionCriteria = new HistoriqueActionCriteria();
        var copy = historiqueActionCriteria.copy();

        assertThat(historiqueActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(historiqueActionCriteria)
        );
    }

    @Test
    void historiqueActionCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var historiqueActionCriteria = new HistoriqueActionCriteria();
        setAllFilters(historiqueActionCriteria);

        var copy = historiqueActionCriteria.copy();

        assertThat(historiqueActionCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(historiqueActionCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var historiqueActionCriteria = new HistoriqueActionCriteria();

        assertThat(historiqueActionCriteria).hasToString("HistoriqueActionCriteria{}");
    }

    private static void setAllFilters(HistoriqueActionCriteria historiqueActionCriteria) {
        historiqueActionCriteria.id();
        historiqueActionCriteria.action();
        historiqueActionCriteria.description();
        historiqueActionCriteria.dateAction();
        historiqueActionCriteria.demandeId();
        historiqueActionCriteria.utilisateurId();
        historiqueActionCriteria.distinct();
    }

    private static Condition<HistoriqueActionCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDateAction()) &&
                condition.apply(criteria.getDemandeId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<HistoriqueActionCriteria> copyFiltersAre(
        HistoriqueActionCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDateAction(), copy.getDateAction()) &&
                condition.apply(criteria.getDemandeId(), copy.getDemandeId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
