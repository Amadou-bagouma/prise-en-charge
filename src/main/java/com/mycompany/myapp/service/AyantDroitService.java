package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AyantDroitDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.AyantDroit}.
 */
public interface AyantDroitService {
    /**
     * Declare le rattachement verifie : acte de naissance, acte de mariage, piece d'identite.
     *
     * <p>Tant qu'il ne l'est pas, l'ayant droit ne peut pas fonder une prise en charge, et son
     * enregistrement reste supprimable - c'est l'etape ou l'on corrige une saisie.
     *
     * @param id l'ayant droit a valider.
     * @return l'ayant droit mis a jour.
     */
    AyantDroitDTO valider(Long id);

    /**
     * Change la situation d'un ayant droit.
     *
     * <p>Le changement est une decision prise sur l'ayant droit lui-meme : il efface la memoire
     * d'une eventuelle repercussion, de sorte que reactiver l'agent ne vienne pas ensuite
     * defaire ce que l'on vient de decider.
     *
     * @param id l'ayant droit concerne.
     * @param statut le nouveau statut, nomme comme dans {@code StatutAyantDroit}.
     * @param motif la raison. Obligatoire des que l'on quitte ACTIF.
     * @return l'ayant droit mis a jour.
     */
    AyantDroitDTO changerStatut(Long id, String statut, String motif);

    /**
     * Save a ayantDroit.
     *
     * @param ayantDroitDTO the entity to save.
     * @return the persisted entity.
     */
    AyantDroitDTO save(AyantDroitDTO ayantDroitDTO);

    /**
     * Updates a ayantDroit.
     *
     * @param ayantDroitDTO the entity to update.
     * @return the persisted entity.
     */
    AyantDroitDTO update(AyantDroitDTO ayantDroitDTO);

    /**
     * Partially updates a ayantDroit.
     *
     * @param ayantDroitDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AyantDroitDTO> partialUpdate(AyantDroitDTO ayantDroitDTO);

    /**
     * Get all the ayantDroits with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AyantDroitDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" ayantDroit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AyantDroitDTO> findOne(Long id);

    /**
     * Delete the "id" ayantDroit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
