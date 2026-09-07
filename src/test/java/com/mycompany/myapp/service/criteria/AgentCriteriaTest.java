package com.mycompany.myapp.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class AgentCriteriaTest {

    @Test
    void newAgentCriteriaHasAllFiltersNullTest() {
        var agentCriteria = new AgentCriteria();
        assertThat(agentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void agentCriteriaFluentMethodsCreatesFiltersTest() {
        var agentCriteria = new AgentCriteria();

        setAllFilters(agentCriteria);

        assertThat(agentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void agentCriteriaCopyCreatesNullFilterTest() {
        var agentCriteria = new AgentCriteria();
        var copy = agentCriteria.copy();

        assertThat(agentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(agentCriteria)
        );
    }

    @Test
    void agentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var agentCriteria = new AgentCriteria();
        setAllFilters(agentCriteria);

        var copy = agentCriteria.copy();

        assertThat(agentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(copyFiltersAre(copy, (a, b) -> a == null || a instanceof Boolean ? a == b : a != b && a.equals(b))),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(agentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var agentCriteria = new AgentCriteria();

        assertThat(agentCriteria).hasToString("AgentCriteria{}");
    }

    private static void setAllFilters(AgentCriteria agentCriteria) {
        agentCriteria.id();
        agentCriteria.matricule();
        agentCriteria.nom();
        agentCriteria.prenom();
        agentCriteria.dateNaissance();
        agentCriteria.telephone();
        agentCriteria.fonction();
        agentCriteria.directionId();
        agentCriteria.gestionId();
        agentCriteria.userId();
        agentCriteria.distinct();
    }

    private static Condition<AgentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getMatricule()) &&
                condition.apply(criteria.getNom()) &&
                condition.apply(criteria.getPrenom()) &&
                condition.apply(criteria.getDateNaissance()) &&
                condition.apply(criteria.getTelephone()) &&
                condition.apply(criteria.getFonction()) &&
                condition.apply(criteria.getDirectionId()) &&
                condition.apply(criteria.getGestionId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<AgentCriteria> copyFiltersAre(AgentCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getMatricule(), copy.getMatricule()) &&
                condition.apply(criteria.getNom(), copy.getNom()) &&
                condition.apply(criteria.getPrenom(), copy.getPrenom()) &&
                condition.apply(criteria.getDateNaissance(), copy.getDateNaissance()) &&
                condition.apply(criteria.getTelephone(), copy.getTelephone()) &&
                condition.apply(criteria.getFonction(), copy.getFonction()) &&
                condition.apply(criteria.getDirectionId(), copy.getDirectionId()) &&
                condition.apply(criteria.getGestionId(), copy.getGestionId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
