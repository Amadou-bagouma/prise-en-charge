package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.DemandePriseEnChargeService;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.mapper.DemandePriseEnChargeMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.DemandePriseEnCharge}.
 */
@Service
@Transactional
public class DemandePriseEnChargeServiceImpl implements DemandePriseEnChargeService {

    private static final Logger LOG = LoggerFactory.getLogger(DemandePriseEnChargeServiceImpl.class);

    private static final String ENTITY_NAME = "demandePriseEnCharge";

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final DemandePriseEnChargeMapper demandePriseEnChargeMapper;

    private final UserRepository userRepository;

    private final HistoriqueActionRepository historiqueActionRepository;

    private final TacheRepository tacheRepository;

    private static final List<StatutTache> STATUTS_TACHE_CLOTURES = List.of(StatutTache.TERMINEE, StatutTache.ANNULEE);

    public DemandePriseEnChargeServiceImpl(
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        DemandePriseEnChargeMapper demandePriseEnChargeMapper,
        UserRepository userRepository,
        HistoriqueActionRepository historiqueActionRepository,
        TacheRepository tacheRepository
    ) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.demandePriseEnChargeMapper = demandePriseEnChargeMapper;
        this.userRepository = userRepository;
        this.historiqueActionRepository = historiqueActionRepository;
        this.tacheRepository = tacheRepository;
    }

    @Override
    public DemandePriseEnChargeDTO save(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to save DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        DemandePriseEnCharge demandePriseEnCharge = demandePriseEnChargeMapper.toEntity(demandePriseEnChargeDTO);
        User currentUser = getCurrentUser();
        Instant now = Instant.now();
        // The workflow always starts with the current user as author and the 1st validation step (DRH),
        // regardless of what the client sent.
        demandePriseEnCharge.setGestionnaireCreateur(currentUser);
        demandePriseEnCharge.setDateCreation(now);
        demandePriseEnCharge.setDateModification(now);
        demandePriseEnCharge.setStatut(StatutDemande.EN_ATTENTE_VALIDATION_DRH);
        demandePriseEnCharge = demandePriseEnChargeRepository.save(demandePriseEnCharge);
        logHistorique(demandePriseEnCharge, currentUser, "SOUMISSION", "Demande soumise pour validation DRH");
        creerTachesValidation(demandePriseEnCharge, AuthoritiesConstants.VALIDATEUR_DRH, "Validation DRH");
        return demandePriseEnChargeMapper.toDto(demandePriseEnCharge);
    }

    @Override
    public DemandePriseEnChargeDTO update(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to update DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        DemandePriseEnCharge demandePriseEnCharge = demandePriseEnChargeMapper.toEntity(demandePriseEnChargeDTO);
        // The workflow status is only ever changed through valider/rejeter/resoumettre, never through the
        // generic update endpoint, so callers cannot bypass the DRH / infirmerie validation steps.
        StatutDemande statutPersiste = getDemandeOrThrow(demandePriseEnCharge.getId()).getStatut();
        demandePriseEnCharge.setStatut(statutPersiste);
        demandePriseEnCharge = demandePriseEnChargeRepository.save(demandePriseEnCharge);
        return demandePriseEnChargeMapper.toDto(demandePriseEnCharge);
    }

    @Override
    public Optional<DemandePriseEnChargeDTO> partialUpdate(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to partially update DemandePriseEnCharge : {}", demandePriseEnChargeDTO);

        return demandePriseEnChargeRepository
            .findById(demandePriseEnChargeDTO.getId())
            .map(existingDemandePriseEnCharge -> {
                StatutDemande statutPersiste = existingDemandePriseEnCharge.getStatut();
                demandePriseEnChargeMapper.partialUpdate(existingDemandePriseEnCharge, demandePriseEnChargeDTO);
                // Same rule as update(): the workflow status cannot be changed through this endpoint.
                existingDemandePriseEnCharge.setStatut(statutPersiste);

                return existingDemandePriseEnCharge;
            })
            .map(demandePriseEnChargeRepository::save)
            .map(demandePriseEnChargeMapper::toDto);
    }

    public Page<DemandePriseEnChargeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return demandePriseEnChargeRepository.findAllWithEagerRelationships(pageable).map(demandePriseEnChargeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DemandePriseEnChargeDTO> findOne(Long id) {
        LOG.debug("Request to get DemandePriseEnCharge : {}", id);
        return demandePriseEnChargeRepository.findOneWithEagerRelationships(id).map(demandePriseEnChargeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DemandePriseEnCharge : {}", id);
        demandePriseEnChargeRepository.deleteById(id);
    }

    @Override
    public DemandePriseEnChargeDTO valider(Long id, String commentaire) {
        LOG.debug("Request to valider DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        User currentUser = getCurrentUser();
        StatutDemande statutSuivant;
        String action;
        switch (demande.getStatut()) {
            case EN_ATTENTE_VALIDATION_DRH -> {
                requireAuthority(AuthoritiesConstants.VALIDATEUR_DRH);
                statutSuivant = StatutDemande.EN_ATTENTE_VALIDATION_INFIRMERIE;
                action = "VALIDATION_DRH";
            }
            case EN_ATTENTE_VALIDATION_INFIRMERIE -> {
                requireAuthority(AuthoritiesConstants.VALIDATEUR_INFIRMERIE);
                statutSuivant = StatutDemande.VALIDEE;
                action = "VALIDATION_INFIRMERIE";
            }
            default -> throw new BadRequestAlertException(
                "Cette demande n'est pas en attente de validation",
                ENTITY_NAME,
                "workflow.invalidstatus"
            );
        }
        demande.setStatut(statutSuivant);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, action, commentaire);
        cloturerTachesValidation(demande.getId());
        if (statutSuivant == StatutDemande.EN_ATTENTE_VALIDATION_INFIRMERIE) {
            creerTachesValidation(demande, AuthoritiesConstants.VALIDATEUR_INFIRMERIE, "Validation infirmerie");
        }
        return demandePriseEnChargeMapper.toDto(demande);
    }

    @Override
    public DemandePriseEnChargeDTO rejeter(Long id, String motif) {
        LOG.debug("Request to rejeter DemandePriseEnCharge : {}", id);
        if (motif == null || motif.isBlank()) {
            throw new BadRequestAlertException("Le motif de rejet est obligatoire", ENTITY_NAME, "workflow.motifrequired");
        }
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        User currentUser = getCurrentUser();
        switch (demande.getStatut()) {
            case EN_ATTENTE_VALIDATION_DRH -> requireAuthority(AuthoritiesConstants.VALIDATEUR_DRH);
            case EN_ATTENTE_VALIDATION_INFIRMERIE -> requireAuthority(AuthoritiesConstants.VALIDATEUR_INFIRMERIE);
            default -> throw new BadRequestAlertException(
                "Cette demande n'est pas en attente de validation",
                ENTITY_NAME,
                "workflow.invalidstatus"
            );
        }
        demande.setStatut(StatutDemande.RETOURNEE);
        demande.setMotifRejet(motif);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, "RETOUR", motif);
        cloturerTachesValidation(demande.getId());
        return demandePriseEnChargeMapper.toDto(demande);
    }

    @Override
    public DemandePriseEnChargeDTO resoumettre(Long id) {
        LOG.debug("Request to resoumettre DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        if (demande.getStatut() != StatutDemande.RETOURNEE) {
            throw new BadRequestAlertException(
                "Cette demande n'a pas ete retournee pour correction",
                ENTITY_NAME,
                "workflow.invalidstatus"
            );
        }
        User currentUser = getCurrentUser();
        if (demande.getGestionnaireCreateur() == null || !currentUser.getId().equals(demande.getGestionnaireCreateur().getId())) {
            throw new AccessDeniedException("Seul l'auteur de la demande peut la resoumettre");
        }
        demande.setStatut(StatutDemande.EN_ATTENTE_VALIDATION_DRH);
        demande.setMotifRejet(null);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, "RESOUMISSION", "Demande corrigee et resoumise pour validation DRH");
        creerTachesValidation(demande, AuthoritiesConstants.VALIDATEUR_DRH, "Validation DRH");
        return demandePriseEnChargeMapper.toDto(demande);
    }

    private DemandePriseEnCharge getDemandeOrThrow(Long id) {
        return demandePriseEnChargeRepository
            .findById(id)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
    }

    private void requireAuthority(String authority) {
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(authority)) {
            throw new AccessDeniedException("L'utilisateur courant n'a pas l'autorite " + authority);
        }
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
    }

    /**
     * Creates a Tache for every user holding the given authority, so that the demande shows up in
     * "Mes taches" for whoever is meant to validate it next.
     */
    private void creerTachesValidation(DemandePriseEnCharge demande, String authority, String titrePrefix) {
        List<User> validateurs = userRepository.findAllByAuthoritiesNameAndActivatedIsTrue(authority);
        for (User validateur : validateurs) {
            creerTacheValidation(demande, validateur, titrePrefix);
        }
    }

    private void creerTacheValidation(DemandePriseEnCharge demande, User validateur, String titrePrefix) {
        Instant now = Instant.now();
        Tache tache = new Tache();
        tache.setTitre(titrePrefix + " - " + demande.getReference());
        tache.setDescription("Demande de prise en charge en attente de votre validation.");
        tache.setDateCreation(now);
        tache.setDateAssignation(now);
        tache.setDateEcheance(demande.getDateEcheance());
        tache.setStatut(StatutTache.A_FAIRE);
        tache.setPriorite(PrioriteTache.valueOf(demande.getPriorite().name()));
        tache.setLu(false);
        tache.setDemande(demande);
        tache.setUtilisateur(validateur);
        tacheRepository.save(tache);
    }

    @Override
    public void rattraperTachesValidationPourNouveauValidateur(User validateur, String authority) {
        StatutDemande statutCorrespondant;
        String titrePrefix;
        if (AuthoritiesConstants.VALIDATEUR_DRH.equals(authority)) {
            statutCorrespondant = StatutDemande.EN_ATTENTE_VALIDATION_DRH;
            titrePrefix = "Validation DRH";
        } else if (AuthoritiesConstants.VALIDATEUR_INFIRMERIE.equals(authority)) {
            statutCorrespondant = StatutDemande.EN_ATTENTE_VALIDATION_INFIRMERIE;
            titrePrefix = "Validation infirmerie";
        } else {
            return;
        }
        List<DemandePriseEnCharge> demandesEnAttente = demandePriseEnChargeRepository.findByStatut(statutCorrespondant);
        for (DemandePriseEnCharge demande : demandesEnAttente) {
            boolean aDejaUneTacheOuverte = tacheRepository.existsByDemandeIdAndUtilisateurIdAndStatutNotIn(
                demande.getId(),
                validateur.getId(),
                STATUTS_TACHE_CLOTURES
            );
            if (!aDejaUneTacheOuverte) {
                creerTacheValidation(demande, validateur, titrePrefix);
            }
        }
    }

    /**
     * Closes any still-open validation Tache for a demande (e.g. once one validator has acted on it,
     * the same pending task on other validators' "Mes taches" no longer applies).
     */
    private void cloturerTachesValidation(Long demandeId) {
        Instant now = Instant.now();
        List<Tache> taches = tacheRepository.findByDemandeIdAndStatutNotIn(demandeId, STATUTS_TACHE_CLOTURES);
        for (Tache tache : taches) {
            tache.setStatut(StatutTache.TERMINEE);
            tache.setDateTerminaison(now);
        }
        tacheRepository.saveAll(taches);
    }

    private void logHistorique(DemandePriseEnCharge demande, User utilisateur, String action, String description) {
        HistoriqueAction historique = new HistoriqueAction();
        historique.setAction(action);
        historique.setDescription(description);
        historique.setDateAction(Instant.now());
        historique.setDemande(demande);
        historique.setUtilisateur(utilisateur);
        historiqueActionRepository.save(historique);
    }
}
