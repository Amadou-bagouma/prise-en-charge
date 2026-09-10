package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TacheCriteriaTest {

    @Test
    void newTacheCriteriaHasAllFiltersNullTest() {
        var tacheCriteria = new TacheCriteria();
        assertThat(tacheCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void tacheCriteriaFluentMethodsCreatesFiltersTest() {
        var tacheCriteria = new TacheCriteria();

        setAllFilters(tacheCriteria);

        assertThat(tacheCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void tacheCriteriaCopyCreatesNullFilterTest() {
        var tacheCriteria = new TacheCriteria();
        var copy = tacheCriteria.copy();

        assertThat(tacheCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(tacheCriteria)
        );
    }

    @Test
    void tacheCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var tacheCriteria = new TacheCriteria();
        setAllFilters(tacheCriteria);

        var copy = tacheCriteria.copy();

        assertThat(tacheCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(tacheCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var tacheCriteria = new TacheCriteria();

        assertThat(tacheCriteria).hasToString("TacheCriteria{}");
    }

    private static void setAllFilters(TacheCriteria tacheCriteria) {
        tacheCriteria.id();
        tacheCriteria.titre();
        tacheCriteria.description();
        tacheCriteria.dateCreation();
        tacheCriteria.dateAssignation();
        tacheCriteria.dateEcheance();
        tacheCriteria.dateTerminaison();
        tacheCriteria.statut();
        tacheCriteria.priorite();
        tacheCriteria.lu();
        tacheCriteria.commentaire();
        tacheCriteria.demandeId();
        tacheCriteria.utilisateurId();
        tacheCriteria.boiteReceptionId();
        tacheCriteria.distinct();
    }

    private static Condition<TacheCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitre()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getDateAssignation()) &&
                condition.apply(criteria.getDateEcheance()) &&
                condition.apply(criteria.getDateTerminaison()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getPriorite()) &&
                condition.apply(criteria.getLu()) &&
                condition.apply(criteria.getCommentaire()) &&
                condition.apply(criteria.getDemandeId()) &&
                condition.apply(criteria.getUtilisateurId()) &&
                condition.apply(criteria.getBoiteReceptionId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TacheCriteria> copyFiltersAre(TacheCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitre(), copy.getTitre()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getDateAssignation(), copy.getDateAssignation()) &&
                condition.apply(criteria.getDateEcheance(), copy.getDateEcheance()) &&
                condition.apply(criteria.getDateTerminaison(), copy.getDateTerminaison()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getPriorite(), copy.getPriorite()) &&
                condition.apply(criteria.getLu(), copy.getLu()) &&
                condition.apply(criteria.getCommentaire(), copy.getCommentaire()) &&
                condition.apply(criteria.getDemandeId(), copy.getDemandeId()) &&
                condition.apply(criteria.getUtilisateurId(), copy.getUtilisateurId()) &&
                condition.apply(criteria.getBoiteReceptionId(), copy.getBoiteReceptionId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
