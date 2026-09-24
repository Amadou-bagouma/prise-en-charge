package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.service.criteria.TypeSoinCriteria;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.mapper.TypeSoinMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TypeSoin} entities in the database.
 */
@Service
@Transactional(readOnly = true)
public class TypeSoinQueryService extends QueryService<TypeSoin> {

    private static final Logger LOG = LoggerFactory.getLogger(TypeSoinQueryService.class);

    private final TypeSoinRepository typeSoinRepository;

    private final TypeSoinMapper typeSoinMapper;

    public TypeSoinQueryService(TypeSoinRepository typeSoinRepository, TypeSoinMapper typeSoinMapper) {
        this.typeSoinRepository = typeSoinRepository;
        this.typeSoinMapper = typeSoinMapper;
    }

    @Transactional(readOnly = true)
    public Page<TypeSoinDTO> findByCriteria(TypeSoinCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TypeSoin> specification = createSpecification(criteria);
        return typeSoinRepository.findAll(specification, page).map(typeSoinMapper::toDto);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(TypeSoinCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TypeSoin> specification = createSpecification(criteria);
        return typeSoinRepository.count(specification);
    }

    protected Specification<TypeSoin> createSpecification(TypeSoinCriteria criteria) {
        Specification<TypeSoin> specification = Specification.unrestricted();
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), TypeSoin_.id),
                    buildStringSpecification(criteria.getCode(), TypeSoin_.code),
                    buildStringSpecification(criteria.getLibelle(), TypeSoin_.libelle),
                    buildStringSpecification(criteria.getDescription(), TypeSoin_.description),
                    buildRangeSpecification(criteria.getOrdre(), TypeSoin_.ordre),
                    buildSpecification(criteria.getActif(), TypeSoin_.actif)
                )
            );
        }
        return specification;
    }
}
