package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.DemandePriseEnCharge}.
 */
public interface DemandePriseEnChargeService {
    /**
     * Save a demandePriseEnCharge.
     *
     * @param demandePriseEnChargeDTO the entity to save.
     * @return the persisted entity.
     */
    DemandePriseEnChargeDTO save(DemandePriseEnChargeDTO demandePriseEnChargeDTO);

    /**
     * Updates a demandePriseEnCharge.
     *
     * @param demandePriseEnChargeDTO the entity to update.
     * @return the persisted entity.
     */
    DemandePriseEnChargeDTO update(DemandePriseEnChargeDTO demandePriseEnChargeDTO);

    /**
     * Partially updates a demandePriseEnCharge.
     *
     * @param demandePriseEnChargeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DemandePriseEnChargeDTO> partialUpdate(DemandePriseEnChargeDTO demandePriseEnChargeDTO);

    /**
     * Get all the demandePriseEnCharges with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DemandePriseEnChargeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" demandePriseEnCharge.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DemandePriseEnChargeDTO> findOne(Long id);

    /**
     * Delete the "id" demandePriseEnCharge.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
