package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.FonctionDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Fonction}.
 */
public interface FonctionService {
    /**
     * Save a fonction.
     *
     * @param fonctionDTO the entity to save.
     * @return the persisted entity.
     */
    FonctionDTO save(FonctionDTO fonctionDTO);

    /**
     * Updates a fonction.
     *
     * @param fonctionDTO the entity to update.
     * @return the persisted entity.
     */
    FonctionDTO update(FonctionDTO fonctionDTO);

    /**
     * Partially updates a fonction.
     *
     * @param fonctionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FonctionDTO> partialUpdate(FonctionDTO fonctionDTO);

    /**
     * Get all the fonctions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FonctionDTO> findAll(Pageable pageable);

    /**
     * Get the "id" fonction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FonctionDTO> findOne(Long id);

    /**
     * Supprime une fonction.
     *
     * @throws com.mycompany.myapp.web.rest.errors.BadRequestAlertException si un agent l'occupe
     *     encore : la supprimer effacerait sa fonction de sa fiche.
     */
    void delete(Long id);
}
