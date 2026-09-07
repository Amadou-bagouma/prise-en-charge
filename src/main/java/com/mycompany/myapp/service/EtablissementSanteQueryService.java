package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.repository.EtablissementSanteRepository;
import com.mycompany.myapp.service.criteria.EtablissementSanteCriteria;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import com.mycompany.myapp.service.mapper.EtablissementSanteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link EtablissementSante} entities in the database.
 * The main input is a {@link EtablissementSanteCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link EtablissementSanteDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class EtablissementSanteQueryService extends QueryService<EtablissementSante> {

    private static final Logger LOG = LoggerFactory.getLogger(EtablissementSanteQueryService.class);

    private final EtablissementSanteRepository etablissementSanteRepository;

    private final EtablissementSanteMapper etablissementSanteMapper;

    public EtablissementSanteQueryService(
        EtablissementSanteRepository etablissementSanteRepository,
        EtablissementSanteMapper etablissementSanteMapper
    ) {
        this.etablissementSanteRepository = etablissementSanteRepository;
        this.etablissementSanteMapper = etablissementSanteMapper;
    }

    /**
     * Return a {@link Page} of {@link EtablissementSanteDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<EtablissementSanteDTO> findByCriteria(EtablissementSanteCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<EtablissementSante> specification = createSpecification(criteria);
        return etablissementSanteRepository.findAll(specification, page).map(etablissementSanteMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(EtablissementSanteCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<EtablissementSante> specification = createSpecification(criteria);
        return etablissementSanteRepository.count(specification);
    }

    /**
     * Function to convert {@link EtablissementSanteCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<EtablissementSante> createSpecification(EtablissementSanteCriteria criteria) {
        Specification<EtablissementSante> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), EtablissementSante_.id),
                    buildStringSpecification(criteria.getCode(), EtablissementSante_.code),
                    buildStringSpecification(criteria.getNom(), EtablissementSante_.nom),
                    buildStringSpecification(criteria.getAdresse(), EtablissementSante_.adresse),
                    buildStringSpecification(criteria.getTelephone(), EtablissementSante_.telephone),
                    buildSpecification(criteria.getActif(), EtablissementSante_.actif)
                )
            );
        }
        return specification;
    }
}
