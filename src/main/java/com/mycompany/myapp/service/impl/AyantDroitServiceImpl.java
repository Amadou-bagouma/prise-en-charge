package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.enumeration.StatutAyantDroit;
import com.mycompany.myapp.domain.enumeration.StatutValidationAyantDroit;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.AyantDroitService;
import com.mycompany.myapp.service.GenerateurCodeAyantDroit;
import com.mycompany.myapp.service.JournalActions;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.mapper.AyantDroitMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.AyantDroit}.
 */
@Service
@Transactional
public class AyantDroitServiceImpl implements AyantDroitService {

    private static final Logger LOG = LoggerFactory.getLogger(AyantDroitServiceImpl.class);

    private static final String ENTITY_NAME = "ayantDroit";

    private final AyantDroitRepository ayantDroitRepository;

    private final AyantDroitMapper ayantDroitMapper;

    private final AgentRepository agentRepository;

    private final GenerateurCodeAyantDroit generateurCodeAyantDroit;

    private final JournalActions journal;

    public AyantDroitServiceImpl(
        AyantDroitRepository ayantDroitRepository,
        AyantDroitMapper ayantDroitMapper,
        AgentRepository agentRepository,
        GenerateurCodeAyantDroit generateurCodeAyantDroit,
        JournalActions journal
    ) {
        this.agentRepository = agentRepository;
        this.generateurCodeAyantDroit = generateurCodeAyantDroit;
        this.ayantDroitRepository = ayantDroitRepository;
        this.ayantDroitMapper = ayantDroitMapper;
        this.journal = journal;
    }

    @Override
    public AyantDroitDTO valider(Long id) {
        AyantDroit ayantDroit = ayantDroitRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        if (ayantDroit.getStatutValidation() == StatutValidationAyantDroit.VALIDE) {
            throw new BadRequestAlertException("Ce rattachement est deja verifie", ENTITY_NAME, "validation.dejafaite");
        }
        ayantDroit.setStatutValidation(StatutValidationAyantDroit.VALIDE);
        ayantDroit.setDateValidation(Instant.now());
        journal.surBeneficiaire(
            JournalActions.CIBLE_AYANT_DROIT,
            id,
            "VALIDATION_RATTACHEMENT",
            "Rattachement verifie : l'ayant droit peut desormais fonder une prise en charge."
        );
        LOG.debug("Rattachement de l'ayant droit {} verifie", id);
        return ayantDroitMapper.toDto(ayantDroitRepository.save(ayantDroit));
    }

    @Override
    public AyantDroitDTO changerStatut(Long id, String statut, String motif) {
        AyantDroit ayantDroit = ayantDroitRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        StatutAyantDroit nouveau = lireStatut(statut);
        StatutAyantDroit ancien = ayantDroit.getStatut();
        if (nouveau != StatutAyantDroit.ACTIF && (motif == null || motif.isBlank())) {
            throw new BadRequestAlertException(
                "Un motif est obligatoire pour retirer le droit a un ayant droit",
                ENTITY_NAME,
                "motif.requis"
            );
        }

        ayantDroit.setStatut(nouveau);
        ayantDroit.setDateStatut(Instant.now());
        ayantDroit.setMotifStatut(motif == null || motif.isBlank() ? null : motif.trim());
        // Decision prise sur l'ayant droit : elle remplace celle que l'agent avait repercutee.
        // Sans cet effacement, reactiver l'agent viendrait defaire ce que l'on vient de decider.
        ayantDroit.setStatutAvantCascade(null);
        ayantDroit.setMotifAvantCascade(null);

        journal.surBeneficiaire(
            JournalActions.CIBLE_AYANT_DROIT,
            id,
            "CHANGEMENT_STATUT",
            "Situation portee de %s a %s. Motif : %s.".formatted(
                ancien,
                nouveau,
                ayantDroit.getMotifStatut() == null ? "non precise" : ayantDroit.getMotifStatut()
            )
        );
        LOG.debug("Statut de l'ayant droit {} porte a {}", id, nouveau);
        return ayantDroitMapper.toDto(ayantDroitRepository.save(ayantDroit));
    }

    /** Recopie la situation persistee sur l'entite reconstruite a partir du DTO. */
    private void reporterSituation(AyantDroit depuis, AyantDroit vers) {
        vers.setStatut(depuis.getStatut());
        vers.setDateStatut(depuis.getDateStatut());
        vers.setMotifStatut(depuis.getMotifStatut());
        vers.setStatutAvantCascade(depuis.getStatutAvantCascade());
        vers.setMotifAvantCascade(depuis.getMotifAvantCascade());
        // Le circuit d'enregistrement ne passe que par valider() : sinon le formulaire de
        // modification permettrait de se declarer verifie soi-meme.
        vers.setStatutValidation(depuis.getStatutValidation());
        vers.setDateValidation(depuis.getDateValidation());
    }

    /** Un libelle inconnu est refuse avec la liste des valeurs admises, pas par une 500. */
    private StatutAyantDroit lireStatut(String statut) {
        try {
            return StatutAyantDroit.valueOf(statut.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new BadRequestAlertException(
                "Statut inconnu. Valeurs admises : " +
                    Arrays.stream(StatutAyantDroit.values()).map(Enum::name).collect(Collectors.joining(", ")),
                ENTITY_NAME,
                "statut.inconnu"
            );
        }
    }

    @Override
    public AyantDroitDTO save(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to save AyantDroit : {}", ayantDroitDTO);

        AyantDroit ayantDroit = ayantDroitMapper.toEntity(ayantDroitDTO);
        ayantDroit.setStatut(StatutAyantDroit.ACTIF);
        ayantDroit.setStatutValidation(StatutValidationAyantDroit.EN_SAISIE);
        attribuerCodeSiAbsent(ayantDroit);
        ayantDroit = ayantDroitRepository.save(ayantDroit);
        return ayantDroitMapper.toDto(ayantDroit);
    }

    /**
     * Attribue le code au serveur quand le client n'en fournit pas.
     *
     * <p>C'est le cas normal : le formulaire affiche un apercu, mais le code qui fait foi est
     * calcule ici, au moment de l'enregistrement. Deux saisies simultanees obtiennent ainsi deux
     * numeros distincts, ce qu'un apercu calcule cote navigateur ne pourrait pas garantir.
     *
     * <p>Un code fourni explicitement est respecte : la reprise de donnees existantes doit
     * pouvoir conserver les codes deja en circulation.
     */
    private void attribuerCodeSiAbsent(AyantDroit ayantDroit) {
        if (ayantDroit.getCodeAyantDroit() != null && !ayantDroit.getCodeAyantDroit().isBlank()) {
            return;
        }
        if (ayantDroit.getAgent() == null || ayantDroit.getAgent().getId() == null) {
            throw new BadRequestAlertException(
                "Un ayant droit doit etre rattache a un agent pour qu'un code lui soit attribue.",
                ENTITY_NAME,
                "agent.obligatoire"
            );
        }
        String matricule = agentRepository
            .findById(ayantDroit.getAgent().getId())
            .map(Agent::getMatricule)
            .orElseThrow(() -> new BadRequestAlertException("Agent introuvable", ENTITY_NAME, "agent.introuvable"));
        ayantDroit.setCodeAyantDroit(generateurCodeAyantDroit.genererPour(matricule, ayantDroit.getLien()));
    }

    @Override
    public AyantDroitDTO update(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to update AyantDroit : {}", ayantDroitDTO);
        AyantDroit ayantDroit = ayantDroitMapper.toEntity(ayantDroitDTO);
        // La situation ne passe que par changerStatut() : sinon le formulaire de modification
        // permettrait de retirer un droit sans motif, et d'effacer la memoire d'une repercussion.
        ayantDroitRepository.findById(ayantDroitDTO.getId()).ifPresent(existant -> reporterSituation(existant, ayantDroit));
        return ayantDroitMapper.toDto(ayantDroitRepository.save(ayantDroit));
    }

    @Override
    public Optional<AyantDroitDTO> partialUpdate(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to partially update AyantDroit : {}", ayantDroitDTO);

        return ayantDroitRepository
            .findById(ayantDroitDTO.getId())
            .map(existingAyantDroit -> {
                // Meme regle que update() : la situation ne se change que par changerStatut().
                StatutAyantDroit statutPersiste = existingAyantDroit.getStatut();
                Instant datePersistee = existingAyantDroit.getDateStatut();
                String motifPersiste = existingAyantDroit.getMotifStatut();
                StatutAyantDroit avantCascadePersiste = existingAyantDroit.getStatutAvantCascade();
                String motifAvantCascadePersiste = existingAyantDroit.getMotifAvantCascade();
                StatutValidationAyantDroit validationPersistee = existingAyantDroit.getStatutValidation();
                Instant dateValidationPersistee = existingAyantDroit.getDateValidation();
                ayantDroitMapper.partialUpdate(existingAyantDroit, ayantDroitDTO);
                existingAyantDroit.setStatut(statutPersiste);
                existingAyantDroit.setDateStatut(datePersistee);
                existingAyantDroit.setMotifStatut(motifPersiste);
                existingAyantDroit.setStatutAvantCascade(avantCascadePersiste);
                existingAyantDroit.setMotifAvantCascade(motifAvantCascadePersiste);
                existingAyantDroit.setStatutValidation(validationPersistee);
                existingAyantDroit.setDateValidation(dateValidationPersistee);

                return existingAyantDroit;
            })
            .map(ayantDroitRepository::save)
            .map(ayantDroitMapper::toDto);
    }

    public Page<AyantDroitDTO> findAllWithEagerRelationships(Pageable pageable) {
        return ayantDroitRepository.findAllWithEagerRelationships(pageable).map(ayantDroitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AyantDroitDTO> findOne(Long id) {
        LOG.debug("Request to get AyantDroit : {}", id);
        return ayantDroitRepository.findOneWithEagerRelationships(id).map(ayantDroitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        // Un rattachement verifie a servi de base a des dossiers : le supprimer les priverait de
        // leur beneficiaire. La suppression reste possible, mais releve de l'administration.
        ayantDroitRepository.findById(id).ifPresent(ayantDroit -> {
            if (
                ayantDroit.getStatutValidation() == StatutValidationAyantDroit.VALIDE &&
                !SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)
            ) {
                throw new AccessDeniedException(
                    "Ce rattachement est verifie : sa suppression releve de l'administration. Il peut etre radie depuis sa fiche."
                );
            }
        });
        LOG.debug("Request to delete AyantDroit : {}", id);
        ayantDroitRepository.deleteById(id);
    }
}
