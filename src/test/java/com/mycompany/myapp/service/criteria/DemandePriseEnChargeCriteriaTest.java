package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class DemandePriseEnChargeCriteriaTest {

    @Test
    void newDemandePriseEnChargeCriteriaHasAllFiltersNullTest() {
        var demandePriseEnChargeCriteria = new DemandePriseEnChargeCriteria();
        assertThat(demandePriseEnChargeCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void demandePriseEnChargeCriteriaFluentMethodsCreatesFiltersTest() {
        var demandePriseEnChargeCriteria = new DemandePriseEnChargeCriteria();

        setAllFilters(demandePriseEnChargeCriteria);

        assertThat(demandePriseEnChargeCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void demandePriseEnChargeCriteriaCopyCreatesNullFilterTest() {
        var demandePriseEnChargeCriteria = new DemandePriseEnChargeCriteria();
        var copy = demandePriseEnChargeCriteria.copy();

        assertThat(demandePriseEnChargeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(demandePriseEnChargeCriteria)
        );
    }

    @Test
    void demandePriseEnChargeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var demandePriseEnChargeCriteria = new DemandePriseEnChargeCriteria();
        setAllFilters(demandePriseEnChargeCriteria);

        var copy = demandePriseEnChargeCriteria.copy();

        assertThat(demandePriseEnChargeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(demandePriseEnChargeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var demandePriseEnChargeCriteria = new DemandePriseEnChargeCriteria();

        assertThat(demandePriseEnChargeCriteria).hasToString("DemandePriseEnChargeCriteria{}");
    }

    private static void setAllFilters(DemandePriseEnChargeCriteria demandePriseEnChargeCriteria) {
        demandePriseEnChargeCriteria.id();
        demandePriseEnChargeCriteria.reference();
        demandePriseEnChargeCriteria.dateCreation();
        demandePriseEnChargeCriteria.dateModification();
        demandePriseEnChargeCriteria.typeBeneficiaire();
        demandePriseEnChargeCriteria.description();
        demandePriseEnChargeCriteria.statut();
        demandePriseEnChargeCriteria.priorite();
        demandePriseEnChargeCriteria.dateAssignation();
        demandePriseEnChargeCriteria.dateEcheance();
        demandePriseEnChargeCriteria.motifRejet();
        demandePriseEnChargeCriteria.observation();
        demandePriseEnChargeCriteria.agentId();
        demandePriseEnChargeCriteria.ayantDroitId();
        demandePriseEnChargeCriteria.typeSoinId();
        demandePriseEnChargeCriteria.etablissementSanteId();
        demandePriseEnChargeCriteria.gestionnaireCreateurId();
        demandePriseEnChargeCriteria.assigneAId();
        demandePriseEnChargeCriteria.distinct();
    }

    private static Condition<DemandePriseEnChargeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getReference()) &&
                condition.apply(criteria.getDateCreation()) &&
                condition.apply(criteria.getDateModification()) &&
                condition.apply(criteria.getTypeBeneficiaire()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStatut()) &&
                condition.apply(criteria.getPriorite()) &&
                condition.apply(criteria.getDateAssignation()) &&
                condition.apply(criteria.getDateEcheance()) &&
                condition.apply(criteria.getMotifRejet()) &&
                condition.apply(criteria.getObservation()) &&
                condition.apply(criteria.getAgentId()) &&
                condition.apply(criteria.getAyantDroitId()) &&
                condition.apply(criteria.getTypeSoinId()) &&
                condition.apply(criteria.getEtablissementSanteId()) &&
                condition.apply(criteria.getGestionnaireCreateurId()) &&
                condition.apply(criteria.getAssigneAId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<DemandePriseEnChargeCriteria> copyFiltersAre(
        DemandePriseEnChargeCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getReference(), copy.getReference()) &&
                condition.apply(criteria.getDateCreation(), copy.getDateCreation()) &&
                condition.apply(criteria.getDateModification(), copy.getDateModification()) &&
                condition.apply(criteria.getTypeBeneficiaire(), copy.getTypeBeneficiaire()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStatut(), copy.getStatut()) &&
                condition.apply(criteria.getPriorite(), copy.getPriorite()) &&
                condition.apply(criteria.getDateAssignation(), copy.getDateAssignation()) &&
                condition.apply(criteria.getDateEcheance(), copy.getDateEcheance()) &&
                condition.apply(criteria.getMotifRejet(), copy.getMotifRejet()) &&
                condition.apply(criteria.getObservation(), copy.getObservation()) &&
                condition.apply(criteria.getAgentId(), copy.getAgentId()) &&
                condition.apply(criteria.getAyantDroitId(), copy.getAyantDroitId()) &&
                condition.apply(criteria.getTypeSoinId(), copy.getTypeSoinId()) &&
                condition.apply(criteria.getEtablissementSanteId(), copy.getEtablissementSanteId()) &&
                condition.apply(criteria.getGestionnaireCreateurId(), copy.getGestionnaireCreateurId()) &&
                condition.apply(criteria.getAssigneAId(), copy.getAssigneAId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
