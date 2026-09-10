package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 */
public interface BoiteReceptionService {
    /**
     * Save a boiteReception.
     *
     * @param boiteReceptionDTO the entity to save.
     * @return the persisted entity.
     */
    BoiteReceptionDTO save(BoiteReceptionDTO boiteReceptionDTO);

    /**
     * Updates a boiteReception.
     *
     * @param boiteReceptionDTO the entity to update.
     * @return the persisted entity.
     */
    BoiteReceptionDTO update(BoiteReceptionDTO boiteReceptionDTO);

    /**
     * Partially updates a boiteReception.
     *
     * @param boiteReceptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<BoiteReceptionDTO> partialUpdate(BoiteReceptionDTO boiteReceptionDTO);

    /**
     * Get all the boiteReceptions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BoiteReceptionDTO> findAll(Pageable pageable);

    /**
     * Get all the boiteReceptions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<BoiteReceptionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" boiteReception.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<BoiteReceptionDTO> findOne(Long id);

    /**
     * Delete the "id" boiteReception.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
