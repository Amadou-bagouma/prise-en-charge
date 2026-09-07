package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.repository.DirectionRepository;
import com.mycompany.myapp.service.criteria.DirectionCriteria;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.mapper.DirectionMapper;
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
 * Service for executing complex queries for {@link Direction} entities in the database.
 * The main input is a {@link DirectionCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link DirectionDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DirectionQueryService extends QueryService<Direction> {

    private static final Logger LOG = LoggerFactory.getLogger(DirectionQueryService.class);

    private final DirectionRepository directionRepository;

    private final DirectionMapper directionMapper;

    public DirectionQueryService(DirectionRepository directionRepository, DirectionMapper directionMapper) {
        this.directionRepository = directionRepository;
        this.directionMapper = directionMapper;
    }

    /**
     * Return a {@link Page} of {@link DirectionDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DirectionDTO> findByCriteria(DirectionCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Direction> specification = createSpecification(criteria);
        return directionRepository.findAll(specification, page).map(directionMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DirectionCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Direction> specification = createSpecification(criteria);
        return directionRepository.count(specification);
    }

    /**
     * Function to convert {@link DirectionCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Direction> createSpecification(DirectionCriteria criteria) {
        Specification<Direction> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Direction_.region, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Direction_.id),
                    buildStringSpecification(criteria.getCode(), Direction_.code),
                    buildStringSpecification(criteria.getNom(), Direction_.nom),
                    buildSpecification(criteria.getRegionId(), root -> root.join(Direction_.region, JoinType.LEFT).get(Region_.id))
                )
            );
        }
        return specification;
    }
}
