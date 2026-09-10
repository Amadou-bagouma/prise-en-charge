package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AyantDroitDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.AyantDroit}.
 */
public interface AyantDroitService {
    /**
     * Save a ayantDroit.
     *
     * @param ayantDroitDTO the entity to save.
     * @return the persisted entity.
     */
    AyantDroitDTO save(AyantDroitDTO ayantDroitDTO);

    /**
     * Updates a ayantDroit.
     *
     * @param ayantDroitDTO the entity to update.
     * @return the persisted entity.
     */
    AyantDroitDTO update(AyantDroitDTO ayantDroitDTO);

    /**
     * Partially updates a ayantDroit.
     *
     * @param ayantDroitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AyantDroitDTO> partialUpdate(AyantDroitDTO ayantDroitDTO);

    /**
     * Get all the ayantDroits with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AyantDroitDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" ayantDroit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AyantDroitDTO> findOne(Long id);

    /**
     * Delete the "id" ayantDroit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
