package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.service.criteria.TacheCriteria;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
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
 * Service for executing complex queries for {@link Tache} entities in the database.
 * The main input is a {@link TacheCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link TacheDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TacheQueryService extends QueryService<Tache> {

    private static final Logger LOG = LoggerFactory.getLogger(TacheQueryService.class);

    private final TacheRepository tacheRepository;

    private final TacheMapper tacheMapper;

    public TacheQueryService(TacheRepository tacheRepository, TacheMapper tacheMapper) {
        this.tacheRepository = tacheRepository;
        this.tacheMapper = tacheMapper;
    }

    /**
     * Return a {@link Page} of {@link TacheDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TacheDTO> findByCriteria(TacheCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Tache> specification = createSpecification(criteria);
        return tacheRepository.findAll(specification, page).map(tacheMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TacheCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Tache> specification = createSpecification(criteria);
        return tacheRepository.count(specification);
    }

    /**
     * Function to convert {@link TacheCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Tache> createSpecification(TacheCriteria criteria) {
        Specification<Tache> specification = Specification.unrestricted();
        specification = specification.and((root, query, builder) -> {
            if (Long.class != query.getResultType()) {
                root.fetch(Tache_.demande, JoinType.LEFT);
                root.fetch(Tache_.utilisateur, JoinType.LEFT);
                root.fetch(Tache_.boiteReception, JoinType.LEFT);
            }
            return null;
        });
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = specification.and(
                Specification.allOf(
                    Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : Specification.unrestricted(),
                    buildRangeSpecification(criteria.getId(), Tache_.id),
                    buildStringSpecification(criteria.getTitre(), Tache_.titre),
                    buildStringSpecification(criteria.getDescription(), Tache_.description),
                    buildRangeSpecification(criteria.getDateCreation(), Tache_.dateCreation),
                    buildRangeSpecification(criteria.getDateAssignation(), Tache_.dateAssignation),
                    buildRangeSpecification(criteria.getDateEcheance(), Tache_.dateEcheance),
                    buildRangeSpecification(criteria.getDateTerminaison(), Tache_.dateTerminaison),
                    buildSpecification(criteria.getStatut(), Tache_.statut),
                    buildSpecification(criteria.getPriorite(), Tache_.priorite),
                    buildSpecification(criteria.getLu(), Tache_.lu),
                    buildStringSpecification(criteria.getCommentaire(), Tache_.commentaire),
                    buildSpecification(criteria.getDemandeId(), root ->
                        root.join(Tache_.demande, JoinType.LEFT).get(DemandePriseEnCharge_.id)
                    ),
                    buildSpecification(criteria.getUtilisateurId(), root -> root.join(Tache_.utilisateur, JoinType.LEFT).get(User_.id)),
                    buildSpecification(criteria.getBoiteReceptionId(), root ->
                        root.join(Tache_.boiteReception, JoinType.LEFT).get(BoiteReception_.id)
                    )
                )
            );
        }
        return specification;
    }
}
