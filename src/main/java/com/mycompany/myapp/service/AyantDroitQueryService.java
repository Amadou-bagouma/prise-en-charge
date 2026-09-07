package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.service.criteria.AyantDroitCriteria;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.mapper.AyantDroitMapper;
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
 * Service for executing complex queries for {@link AyantDroit} entities in the database.
 * The main input is a {@link AyantDroitCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link AyantDroitDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AyantDroitQueryService extends QueryService<AyantDroit> {

    private static final Logger LOG = LoggerFactory.getLogger(AyantDroitQueryService.class);

    private final AyantDroitRepository ayantDroitRepository;

    private final AyantDroitMapper ayantDroitMapper;

    public AyantDroitQueryService(AyantDroitRepository ayantDroitRepository, AyantDroitMapper ayantDroitMapper) {
        this.ayantDroitRepository = ayantDroitRepository;
        this.ayantDroitMapper = ayantDroitMapper;
    }

    /**
     * Return a {@link Page} of {@link AyantDroitDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AyantDroitDTO> findByCriteria(AyantDroitCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<AyantDroit> specification = createSpecification(criteria);
        return ayantDroitRepository.findAll(specification, page).map(ayantDroitMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AyantDroitCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<AyantDroit> specification = createSpecification(criteria);
        return ayantDroitRepository.count(specification);
    }

    /**
     * Function to convert {@link AyantDroitCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<AyantDroit> createSpecification(AyantDroitCriteria criteria) {
        Specification<AyantDroit> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(AyantDroit_.agent, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), AyantDroit_.id),
                    buildStringSpecification(criteria.getNom(), AyantDroit_.nom),
                    buildStringSpecification(criteria.getPrenom(), AyantDroit_.prenom),
                    buildRangeSpecification(criteria.getDateNaissance(), AyantDroit_.dateNaissance),
                    buildSpecification(criteria.getLien(), AyantDroit_.lien),
                    buildSpecification(criteria.getAgentId(), root -> root.join(AyantDroit_.agent, JoinType.LEFT).get(Agent_.id))
                )
            );
        }
        return specification;
    }
}
