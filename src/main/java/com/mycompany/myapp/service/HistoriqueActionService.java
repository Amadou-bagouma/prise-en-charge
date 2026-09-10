package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.HistoriqueActionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.HistoriqueAction}.
 */
public interface HistoriqueActionService {
    /**
     * Save a historiqueAction.
     *
     * @param historiqueActionDTO the entity to save.
     * @return the persisted entity.
     */
    HistoriqueActionDTO save(HistoriqueActionDTO historiqueActionDTO);

    /**
     * Updates a historiqueAction.
     *
     * @param historiqueActionDTO the entity to update.
     * @return the persisted entity.
     */
    HistoriqueActionDTO update(HistoriqueActionDTO historiqueActionDTO);

    /**
     * Partially updates a historiqueAction.
     *
     * @param historiqueActionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<HistoriqueActionDTO> partialUpdate(HistoriqueActionDTO historiqueActionDTO);

    /**
     * Get all the historiqueActions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<HistoriqueActionDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" historiqueAction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<HistoriqueActionDTO> findOne(Long id);

    /**
     * Delete the "id" historiqueAction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
