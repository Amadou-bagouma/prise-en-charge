package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.EtablissementSante}.
 */
public interface EtablissementSanteService {
    /**
     * Save a etablissementSante.
     *
     * @param etablissementSanteDTO the entity to save.
     * @return the persisted entity.
     */
    EtablissementSanteDTO save(EtablissementSanteDTO etablissementSanteDTO);

    /**
     * Updates a etablissementSante.
     *
     * @param etablissementSanteDTO the entity to update.
     * @return the persisted entity.
     */
    EtablissementSanteDTO update(EtablissementSanteDTO etablissementSanteDTO);

    /**
     * Partially updates a etablissementSante.
     *
     * @param etablissementSanteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EtablissementSanteDTO> partialUpdate(EtablissementSanteDTO etablissementSanteDTO);

    /**
     * Get the "id" etablissementSante.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EtablissementSanteDTO> findOne(Long id);

    /**
     * Delete the "id" etablissementSante.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
