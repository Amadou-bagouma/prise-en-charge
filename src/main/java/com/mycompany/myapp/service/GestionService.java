package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.GestionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Gestion}.
 */
public interface GestionService {
    /**
     * Save a gestion.
     *
     * @param gestionDTO the entity to save.
     * @return the persisted entity.
     */
    GestionDTO save(GestionDTO gestionDTO);

    /**
     * Updates a gestion.
     *
     * @param gestionDTO the entity to update.
     * @return the persisted entity.
     */
    GestionDTO update(GestionDTO gestionDTO);

    /**
     * Partially updates a gestion.
     *
     * @param gestionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GestionDTO> partialUpdate(GestionDTO gestionDTO);

    /**
     * Get all the gestions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GestionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" gestion.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GestionDTO> findOne(Long id);

    /**
     * Delete the "id" gestion.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
