package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.service.dto.AgentDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Agent}.
 */
public interface AgentService {
    /**
     * Change la situation d'un agent et la repercute sur ses ayants droit.
     *
     * <p>Sortir un agent de l'activite retire le droit a ses ayants droit ; l'y ramener leur rend
     * le statut qu'ils avaient avant - voir {@link RepercussionStatutAyantDroit}.
     *
     * @param id l'agent concerne.
     * @param statut le nouveau statut, nomme comme dans {@code StatutAgent}.
     * @param motif la raison. Obligatoire des que l'on quitte ACTIF.
     * @return l'agent mis a jour.
     */
    AgentDTO changerStatut(Long id, String statut, String motif);

    /**
     * Save a agent.
     *
     * @param agentDTO the entity to save.
     * @return the persisted entity.
     */
    AgentDTO save(AgentDTO agentDTO);

    /**
     * Updates a agent.
     *
     * @param agentDTO the entity to update.
     * @return the persisted entity.
     */
    AgentDTO update(AgentDTO agentDTO);

    /**
     * Partially updates a agent.
     *
     * @param agentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AgentDTO> partialUpdate(AgentDTO agentDTO);

    /**
     * Get all the agents with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AgentDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" agent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AgentDTO> findOne(Long id);

    /**
     * Delete the "id" agent.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Retrouve un agent par son matricule.
     *
     * @param matricule le matricule recherche.
     * @return l'agent, vide si aucun ne porte ce matricule.
     */
    Optional<Agent> findByMatricule(String matricule);
}
