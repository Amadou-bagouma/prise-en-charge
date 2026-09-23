package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.CarteBeneficiaire}.
 */
public interface CarteBeneficiaireService {
    /**
     * Save a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to save.
     * @return the persisted entity.
     */
    CarteBeneficiaireDTO save(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Updates a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to update.
     * @return the persisted entity.
     */
    CarteBeneficiaireDTO update(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Partially updates a carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarteBeneficiaireDTO> partialUpdate(CarteBeneficiaireDTO carteBeneficiaireDTO);

    /**
     * Get all the carteBeneficiaires with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<CarteBeneficiaireDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" carteBeneficiaire.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarteBeneficiaireDTO> findOne(Long id);

    /**
     * Delete the "id" carteBeneficiaire.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
