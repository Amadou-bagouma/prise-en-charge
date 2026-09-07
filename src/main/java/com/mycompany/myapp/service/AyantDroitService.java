package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AyantDroitDTO;
import java.util.Optional;

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
