package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.service.criteria.DemandePriseEnChargeCriteria;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.mapper.DemandePriseEnChargeMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DemandePriseEnCharge} entities in the database.
 * The main input is a {@link DemandePriseEnChargeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DemandePriseEnChargeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DemandePriseEnChargeQueryService extends QueryService<DemandePriseEnCharge> {

    private static final Logger LOG = LoggerFactory.getLogger(DemandePriseEnChargeQueryService.class);

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final DemandePriseEnChargeMapper demandePriseEnChargeMapper;

    public DemandePriseEnChargeQueryService(
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        DemandePriseEnChargeMapper demandePriseEnChargeMapper
    ) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.demandePriseEnChargeMapper = demandePriseEnChargeMapper;
    }

    /**
     * Return a {@link Page} of {@link DemandePriseEnChargeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @param restrictToUserId when not {@code null}, only demandes created by or assigned to this user are
     *                         returned, regardless of the criteria. Callers pass {@code null} for users
     *                         allowed to browse every demande (admins and validators).
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DemandePriseEnChargeDTO> findByCriteria(DemandePriseEnChargeCriteria criteria, Pageable page, Long restrictToUserId) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DemandePriseEnCharge> specification = restrict(createSpecification(criteria), restrictToUserId);
        return demandePriseEnChargeRepository.findAll(specification, page).map(demandePriseEnChargeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param restrictToUserId see {@link #findByCriteria(DemandePriseEnChargeCriteria, Pageable, Long)}.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DemandePriseEnChargeCriteria criteria, Long restrictToUserId) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<DemandePriseEnCharge> specification = restrict(createSpecification(criteria), restrictToUserId);
        return demandePriseEnChargeRepository.count(specification);
    }

    /**
     * Narrows a specification to only the demandes where the given user is either the author
     * (gestionnaireCreateur) or the current assignee (assigneA).
     */
    private Specification<DemandePriseEnCharge> restrict(Specification<DemandePriseEnCharge> specification, Long restrictToUserId) {
        if (restrictToUserId == null) {
            return specification;
        }
        return specification.and((root, query, builder) ->
            builder.or(
                builder.equal(root.join(DemandePriseEnCharge_.gestionnaireCreateur, JoinType.LEFT).get(User_.id), restrictToUserId),
                builder.equal(root.join(DemandePriseEnCharge_.assigneA, JoinType.LEFT).get(User_.id), restrictToUserId)
            )
        );
    }

    /**
     * Function to convert {@link DemandePriseEnChargeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DemandePriseEnCharge> createSpecification(DemandePriseEnChargeCriteria criteria) {
        Specification<DemandePriseEnCharge> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(DemandePriseEnCharge_.agent, JoinType.LEFT);
                root.fetch(DemandePriseEnCharge_.ayantDroit, JoinType.LEFT);
                root.fetch(DemandePriseEnCharge_.etablissementSante, JoinType.LEFT);
                root.fetch(DemandePriseEnCharge_.gestionnaireCreateur, JoinType.LEFT);
                root.fetch(DemandePriseEnCharge_.assigneA, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), DemandePriseEnCharge_.id),
                    buildStringSpecification(criteria.getReference(), DemandePriseEnCharge_.reference),
                    buildRangeSpecification(criteria.getDateCreation(), DemandePriseEnCharge_.dateCreation),
                    buildRangeSpecification(criteria.getDateModification(), DemandePriseEnCharge_.dateModification),
                    buildSpecification(criteria.getTypeBeneficiaire(), DemandePriseEnCharge_.typeBeneficiaire),
                    buildStringSpecification(criteria.getDescription(), DemandePriseEnCharge_.description),
                    buildSpecification(criteria.getStatut(), DemandePriseEnCharge_.statut),
                    buildSpecification(criteria.getPriorite(), DemandePriseEnCharge_.priorite),
                    buildRangeSpecification(criteria.getDateAssignation(), DemandePriseEnCharge_.dateAssignation),
                    buildRangeSpecification(criteria.getDateEcheance(), DemandePriseEnCharge_.dateEcheance),
                    buildStringSpecification(criteria.getMotifRejet(), DemandePriseEnCharge_.motifRejet),
                    buildStringSpecification(criteria.getObservation(), DemandePriseEnCharge_.observation),
                    buildSpecification(criteria.getAgentId(), root -> root.join(DemandePriseEnCharge_.agent, JoinType.LEFT).get(Agent_.id)),
                    buildSpecification(criteria.getAyantDroitId(), root ->
                        root.join(DemandePriseEnCharge_.ayantDroit, JoinType.LEFT).get(AyantDroit_.id)
                    ),
                    buildSpecification(criteria.getEtablissementSanteId(), root ->
                        root.join(DemandePriseEnCharge_.etablissementSante, JoinType.LEFT).get(EtablissementSante_.id)
                    ),
                    buildSpecification(criteria.getGestionnaireCreateurId(), root ->
                        root.join(DemandePriseEnCharge_.gestionnaireCreateur, JoinType.LEFT).get(User_.id)
                    ),
                    buildSpecification(criteria.getAssigneAId(), root ->
                        root.join(DemandePriseEnCharge_.assigneA, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
