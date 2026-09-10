package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.service.criteria.HistoriqueActionCriteria;
import com.mycompany.myapp.service.dto.HistoriqueActionDTO;
import com.mycompany.myapp.service.mapper.HistoriqueActionMapper;
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
 * Service for executing complex queries for {@link HistoriqueAction} entities in the database.
 * The main input is a {@link HistoriqueActionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link HistoriqueActionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class HistoriqueActionQueryService extends QueryService<HistoriqueAction> {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueActionQueryService.class);

    private final HistoriqueActionRepository historiqueActionRepository;

    private final HistoriqueActionMapper historiqueActionMapper;

    public HistoriqueActionQueryService(
        HistoriqueActionRepository historiqueActionRepository,
        HistoriqueActionMapper historiqueActionMapper
    ) {
        this.historiqueActionRepository = historiqueActionRepository;
        this.historiqueActionMapper = historiqueActionMapper;
    }

    /**
     * Return a {@link Page} of {@link HistoriqueActionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<HistoriqueActionDTO> findByCriteria(HistoriqueActionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<HistoriqueAction> specification = createSpecification(criteria);
        return historiqueActionRepository.findAll(specification, page).map(historiqueActionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(HistoriqueActionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<HistoriqueAction> specification = createSpecification(criteria);
        return historiqueActionRepository.count(specification);
    }

    /**
     * Function to convert {@link HistoriqueActionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<HistoriqueAction> createSpecification(HistoriqueActionCriteria criteria) {
        Specification<HistoriqueAction> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(HistoriqueAction_.demande, JoinType.LEFT);
                root.fetch(HistoriqueAction_.utilisateur, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), HistoriqueAction_.id),
                    buildStringSpecification(criteria.getAction(), HistoriqueAction_.action),
                    buildStringSpecification(criteria.getDescription(), HistoriqueAction_.description),
                    buildRangeSpecification(criteria.getDateAction(), HistoriqueAction_.dateAction),
                    buildSpecification(criteria.getDemandeId(), root ->
                        root.join(HistoriqueAction_.demande, JoinType.LEFT).get(DemandePriseEnCharge_.id)
                    ),
                    buildSpecification(criteria.getUtilisateurId(), root ->
                        root.join(HistoriqueAction_.utilisateur, JoinType.LEFT).get(User_.id)
                    )
                )
            );
        }
        return specification;
    }
}
