package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AyantDroitCriteriaTest {

    @Test
    void newAyantDroitCriteriaHasAllFiltersNullTest() {
        var ayantDroitCriteria = new AyantDroitCriteria();
        assertThat(ayantDroitCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void ayantDroitCriteriaFluentMethodsCreatesFiltersTest() {
        var ayantDroitCriteria = new AyantDroitCriteria();

        setAllFilters(ayantDroitCriteria);

        assertThat(ayantDroitCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void ayantDroitCriteriaCopyCreatesNullFilterTest() {
        var ayantDroitCriteria = new AyantDroitCriteria();
        var copy = ayantDroitCriteria.copy();

        assertThat(ayantDroitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(ayantDroitCriteria)
        );
    }

    @Test
    void ayantDroitCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var ayantDroitCriteria = new AyantDroitCriteria();
        setAllFilters(ayantDroitCriteria);

        var copy = ayantDroitCriteria.copy();

        assertThat(ayantDroitCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(ayantDroitCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var ayantDroitCriteria = new AyantDroitCriteria();

        assertThat(ayantDroitCriteria).hasToString("AyantDroitCriteria{}");
    }

    private static void setAllFilters(AyantDroitCriteria ayantDroitCriteria) {
        ayantDroitCriteria.id();
        ayantDroitCriteria.nom();
        ayantDroitCriteria.prenom();
        ayantDroitCriteria.dateNaissance();
        ayantDroitCriteria.lien();
        ayantDroitCriteria.agentId();
        ayantDroitCriteria.distinct();
    }

    private static Condition<AyantDroitCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getPrenom()) &&
                condition.apply(criteria.getDateNaissance()) &&
                condition.apply(criteria.getLien()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AyantDroitCriteria> copyFiltersAre(AyantDroitCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getPrenom(), copy.getPrenom()) &&
                condition.apply(criteria.getDateNaissance(), copy.getDateNaissance()) &&
                condition.apply(criteria.getLien(), copy.getLien()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
