package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.User;
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

    /**
     * Validates the current workflow step of a demandePriseEnCharge (DRH then infirmerie du personnel).
     * Moves the demande to the next step, or to {@code VALIDEE} once both steps are done.
     *
     * @param id the id of the entity.
     * @param commentaire an optional comment to record in the history.
     * @return the persisted entity.
     */
    DemandePriseEnChargeDTO valider(Long id, String commentaire);

    /**
     * Rejects the current workflow step of a demandePriseEnCharge and returns it to its author for correction.
     *
     * @param id the id of the entity.
     * @param motif the mandatory rejection reason.
     * @return the persisted entity.
     */
    DemandePriseEnChargeDTO rejeter(Long id, String motif);

    /**
     * Resubmits a {@code RETOURNEE} demandePriseEnCharge, sending it back to the 1st validation step (DRH).
     * Only the original author (gestionnaireCreateur) may do this.
     *
     * @param id the id of the entity.
     * @return the persisted entity.
     */
    DemandePriseEnChargeDTO resoumettre(Long id);

    /**
     * Catches up a user who was just granted a validation authority (DRH or infirmerie): creates a
     * validation Tache for every already-pending demande matching that step which doesn't already have
     * an open task for them. Without this, a demande stuck in a validation step before the user held the
     * role would never surface in their "Mes taches".
     *
     * @param validateur the user who was granted the authority.
     * @param authority the granted authority (only {@code ROLE_VALIDATEUR_DRH} and
     *     {@code ROLE_VALIDATEUR_INFIRMERIE} trigger a catch-up; anything else is a no-op).
     */
    void rattraperTachesValidationPourNouveauValidateur(User validateur, String authority);
}
