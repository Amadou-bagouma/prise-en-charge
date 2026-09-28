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

    /**
     * Validates the current workflow step of a demandePriseEnCharge (DRH then infirmerie du personnel).
     * Moves the demande to the next step, or to {@code VALIDEE} once both steps are done.
     *
     * @param id the id of the entity.
     * @param commentaire an optional comment to record in the history.
     * @return the persisted entity.
     */
    /**
     * Soumet au controle RH un dossier encore en saisie.
     *
     * <p>C'est le geste qui fait sortir le dossier du brouillon : jusque-la il n'attend personne
     * et peut etre supprime, apres il entre dans le circuit et ne peut plus qu'etre annule.
     *
     * @param id le dossier, qui doit etre en saisie.
     * @return le dossier mis a jour.
     */
    DemandePriseEnChargeDTO soumettre(Long id);

    /**
     * Declare le controle RH fait : le dossier passe en attente de validation.
     *
     * @param id le dossier, qui doit etre en verification.
     * @param commentaire l'observation du controleur, facultative.
     * @return le dossier mis a jour.
     */
    DemandePriseEnChargeDTO verifier(Long id, String commentaire);

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
    /**
     * Refuse definitivement un dossier.
     *
     * <p>A distinguer du retour pour correction : celui-ci attend une suite, celui-la clot le
     * dossier. Sans cette distinction, un dossier qui ne peut aboutir - beneficiaire non couvert,
     * soin hors garanties - reste indefiniment « retourne », et l'agent attend une correction
     * qu'il ne peut pas faire.
     *
     * @param id le dossier a refuser.
     * @param motif la raison, obligatoire : c'est ce qui figurera sur la notification remise a
     *        l'agent, et ce sur quoi portera un eventuel recours.
     * @return le dossier mis a jour.
     */
    DemandePriseEnChargeDTO rejeterDefinitivement(Long id, String motif);

    DemandePriseEnChargeDTO resoumettre(Long id);
}
