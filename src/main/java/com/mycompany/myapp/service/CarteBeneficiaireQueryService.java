package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.CarteBeneficiaire;
import com.mycompany.myapp.repository.CarteBeneficiaireRepository;
import com.mycompany.myapp.service.criteria.CarteBeneficiaireCriteria;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import com.mycompany.myapp.service.mapper.CarteBeneficiaireMapper;
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
 * Service for executing complex queries for {@link CarteBeneficiaire} entities in the database.
 * The main input is a {@link CarteBeneficiaireCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link CarteBeneficiaireDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CarteBeneficiaireQueryService extends QueryService<CarteBeneficiaire> {

    private static final Logger LOG = LoggerFactory.getLogger(CarteBeneficiaireQueryService.class);

    private final CarteBeneficiaireRepository carteBeneficiaireRepository;

    private final CarteBeneficiaireMapper carteBeneficiaireMapper;

    public CarteBeneficiaireQueryService(
        CarteBeneficiaireRepository carteBeneficiaireRepository,
        CarteBeneficiaireMapper carteBeneficiaireMapper
    ) {
        this.carteBeneficiaireRepository = carteBeneficiaireRepository;
        this.carteBeneficiaireMapper = carteBeneficiaireMapper;
    }

    /**
     * Return a {@link Page} of {@link CarteBeneficiaireDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CarteBeneficiaireDTO> findByCriteria(CarteBeneficiaireCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CarteBeneficiaire> specification = createSpecification(criteria);
        return carteBeneficiaireRepository.findAll(specification, page).map(carteBeneficiaireMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CarteBeneficiaireCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<CarteBeneficiaire> specification = createSpecification(criteria);
        return carteBeneficiaireRepository.count(specification);
    }

    /**
     * Function to convert {@link CarteBeneficiaireCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CarteBeneficiaire> createSpecification(CarteBeneficiaireCriteria criteria) {
        Specification<CarteBeneficiaire> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(CarteBeneficiaire_.agent, JoinType.LEFT);
                root.fetch(CarteBeneficiaire_.ayantDroit, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), CarteBeneficiaire_.id),
                    buildStringSpecification(criteria.getNumeroCarte(), CarteBeneficiaire_.numeroCarte),
                    buildSpecification(criteria.getTypeBeneficiaire(), CarteBeneficiaire_.typeBeneficiaire),
                    buildRangeSpecification(criteria.getDateDebutValidite(), CarteBeneficiaire_.dateDebutValidite),
                    buildRangeSpecification(criteria.getDateFinValidite(), CarteBeneficiaire_.dateFinValidite),
                    buildRangeSpecification(criteria.getDateEmission(), CarteBeneficiaire_.dateEmission),
                    buildSpecification(criteria.getAgentId(), root -> root.join(CarteBeneficiaire_.agent, JoinType.LEFT).get(Agent_.id)),
                    buildSpecification(criteria.getAyantDroitId(), root ->
                        root.join(CarteBeneficiaire_.ayantDroit, JoinType.LEFT).get(AyantDroit_.id)
                    )
                )
            );
        }
        return specification;
    }
}
