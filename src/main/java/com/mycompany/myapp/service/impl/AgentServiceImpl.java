package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.enumeration.StatutAgent;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.service.AgentService;
import com.mycompany.myapp.service.JournalActions;
import com.mycompany.myapp.service.RepercussionStatutAyantDroit;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.mapper.AgentMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Agent}.
 */
@Service
@Transactional
public class AgentServiceImpl implements AgentService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentServiceImpl.class);

    private final AgentRepository agentRepository;

    private static final String ENTITY_NAME = "agent";

    private final AgentMapper agentMapper;

    private final AyantDroitRepository ayantDroitRepository;

    private final RepercussionStatutAyantDroit repercussion;

    private final JournalActions journal;

    public AgentServiceImpl(
        AgentRepository agentRepository,
        AgentMapper agentMapper,
        AyantDroitRepository ayantDroitRepository,
        RepercussionStatutAyantDroit repercussion,
        JournalActions journal
    ) {
        this.agentRepository = agentRepository;
        this.agentMapper = agentMapper;
        this.ayantDroitRepository = ayantDroitRepository;
        this.repercussion = repercussion;
        this.journal = journal;
    }

    @Override
    public AgentDTO changerStatut(Long id, String statut, String motif) {
        Agent agent = agentRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        StatutAgent nouveau = lireStatut(statut);
        exigerMotif(nouveau, motif);
        StatutAgent ancien = agent.getStatut();

        agent.setStatut(nouveau);
        agent.setDateStatut(Instant.now());
        agent.setMotifStatut(motif == null || motif.isBlank() ? null : motif.trim());
        agent = agentRepository.save(agent);

        // La couverture des ayants droit derive de celle de l'agent : elle suit, dans les deux
        // sens. Les entites sont gerees par la transaction courante, la sauvegarde explicite
        // rend seulement l'intention lisible.
        List<AyantDroit> ayantsDroit = ayantDroitRepository.findByAgentId(id);
        int repercutes = repercussion.appliquer(ayantsDroit, nouveau, agent.getMotifStatut());
        if (repercutes > 0) {
            ayantDroitRepository.saveAll(ayantsDroit);
        }

        journal.surBeneficiaire(
            JournalActions.CIBLE_AGENT,
            id,
            "CHANGEMENT_STATUT",
            "Situation portee de %s a %s. Motif : %s.%s".formatted(
                ancien,
                nouveau,
                agent.getMotifStatut() == null ? "non precise" : agent.getMotifStatut(),
                repercutes > 0 ? " Repercute sur %d ayant(s) droit.".formatted(repercutes) : ""
            )
        );

        LOG.debug("Statut de l'agent {} porte a {} ({} ayant(s) droit rattache(s))", id, nouveau, ayantsDroit.size());
        return agentMapper.toDto(agent);
    }

    /** Recopie la situation persistee sur l'entite reconstruite a partir du DTO. */
    private void reporterSituation(Agent depuis, Agent vers) {
        vers.setStatut(depuis.getStatut());
        vers.setDateStatut(depuis.getDateStatut());
        vers.setMotifStatut(depuis.getMotifStatut());
    }

    /** Un libelle inconnu est refuse avec la liste des valeurs admises, pas par une 500. */
    private StatutAgent lireStatut(String statut) {
        try {
            return StatutAgent.valueOf(statut.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Statut inconnu. Valeurs admises : " +
                    Arrays.stream(StatutAgent.values()).map(Enum::name).collect(Collectors.joining(", ")),
                ENTITY_NAME,
                "statut.inconnu"
            );
        }
    }

    /**
     * Le motif est exige des que l'on retire le droit.
     *
     * <p>Le rendre facultatif reviendrait a produire des dossiers fermes sans raison consultable :
     * l'agent au guichet ne pourrait ni expliquer, ni contester.
     */
    private void exigerMotif(StatutAgent statut, String motif) {
        if (statut != StatutAgent.ACTIF && (motif == null || motif.isBlank())) {
            throw new BadRequestAlertException("Un motif est obligatoire pour retirer un agent de l'activite", ENTITY_NAME, "motif.requis");
        }
    }

    @Override
    public AgentDTO save(AgentDTO agentDTO) {
        LOG.debug("Request to save Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        agent.setStatut(StatutAgent.ACTIF);
        agent = agentRepository.save(agent);
        return agentMapper.toDto(agent);
    }

    @Override
    public AgentDTO update(AgentDTO agentDTO) {
        LOG.debug("Request to update Agent : {}", agentDTO);
        Agent agent = agentMapper.toEntity(agentDTO);
        // La situation ne passe que par changerStatut() : sinon le formulaire de modification
        // permettrait de radier un agent sans motif et sans repercussion sur ses ayants droit.
        agentRepository.findById(agentDTO.getId()).ifPresent(existant -> reporterSituation(existant, agent));
        return agentMapper.toDto(agentRepository.save(agent));
    }

    @Override
    public Optional<AgentDTO> partialUpdate(AgentDTO agentDTO) {
        LOG.debug("Request to partially update Agent : {}", agentDTO);

        return agentRepository
            .findById(agentDTO.getId())
            .map(existingAgent -> {
                // Meme regle que update() : la situation ne se change que par changerStatut().
                StatutAgent statutPersiste = existingAgent.getStatut();
                Instant datePersistee = existingAgent.getDateStatut();
                String motifPersiste = existingAgent.getMotifStatut();
                agentMapper.partialUpdate(existingAgent, agentDTO);
                existingAgent.setStatut(statutPersiste);
                existingAgent.setDateStatut(datePersistee);
                existingAgent.setMotifStatut(motifPersiste);

                return existingAgent;
            })
            .map(agentRepository::save)
            .map(agentMapper::toDto);
    }

    public Page<AgentDTO> findAllWithEagerRelationships(Pageable pageable) {
        return agentRepository.findAllWithEagerRelationships(pageable).map(agentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgentDTO> findOne(Long id) {
        LOG.debug("Request to get Agent : {}", id);
        return agentRepository.findOneWithEagerRelationships(id).map(agentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Agent : {}", id);
        agentRepository.deleteById(id);
    }

    @Override
    public Optional<Agent> findByMatricule(String matricule) {
        LOG.debug("Request to get Agent by matricule : {}", matricule);
        return this.agentRepository.findByMatricule(matricule);
    }
}
