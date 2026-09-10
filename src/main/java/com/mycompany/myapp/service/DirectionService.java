package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DirectionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Direction}.
 */
public interface DirectionService {
    /**
     * Save a direction.
     *
     * @param directionDTO the entity to save.
     * @return the persisted entity.
     */
    DirectionDTO save(DirectionDTO directionDTO);

    /**
     * Updates a direction.
     *
     * @param directionDTO the entity to update.
     * @return the persisted entity.
     */
    DirectionDTO update(DirectionDTO directionDTO);

    /**
     * Partially updates a direction.
     *
     * @param directionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DirectionDTO> partialUpdate(DirectionDTO directionDTO);

    /**
     * Get all the directions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DirectionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" direction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DirectionDTO> findOne(Long id);

    /**
     * Delete the "id" direction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
