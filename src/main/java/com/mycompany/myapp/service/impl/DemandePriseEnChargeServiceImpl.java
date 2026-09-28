package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutAgent;
import com.mycompany.myapp.domain.enumeration.StatutAyantDroit;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.domain.enumeration.TypeNotification;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.repository.NotificationRepository;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.DemandePriseEnChargeService;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.mapper.DemandePriseEnChargeMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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

    private final NotificationRepository notificationRepository;

    private final TypeSoinRepository typeSoinRepository;

    private final AgentRepository agentRepository;

    private final AyantDroitRepository ayantDroitRepository;

    private static final List<StatutTache> STATUTS_TACHE_CLOTURES = List.of(StatutTache.TERMINEE, StatutTache.ANNULEE);

    private static final DateTimeFormatter REFERENCE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyMMdd");

    public DemandePriseEnChargeServiceImpl(
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        DemandePriseEnChargeMapper demandePriseEnChargeMapper,
        UserRepository userRepository,
        HistoriqueActionRepository historiqueActionRepository,
        TacheRepository tacheRepository,
        NotificationRepository notificationRepository,
        TypeSoinRepository typeSoinRepository,
        AgentRepository agentRepository,
        AyantDroitRepository ayantDroitRepository
    ) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.demandePriseEnChargeMapper = demandePriseEnChargeMapper;
        this.userRepository = userRepository;
        this.historiqueActionRepository = historiqueActionRepository;
        this.tacheRepository = tacheRepository;
        this.notificationRepository = notificationRepository;
        this.typeSoinRepository = typeSoinRepository;
        this.agentRepository = agentRepository;
        this.ayantDroitRepository = ayantDroitRepository;
    }

    @Override
    public DemandePriseEnChargeDTO save(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to save DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        DemandePriseEnCharge demandePriseEnCharge = demandePriseEnChargeMapper.toEntity(demandePriseEnChargeDTO);
        demandePriseEnCharge.setTypeSoins(resoudreTypeSoins(demandePriseEnChargeDTO));
        exigerBeneficiaireCouvert(demandePriseEnCharge);
        User currentUser = getCurrentUser();
        Instant now = Instant.now();
        // L'auteur et la reference sont poses par le serveur, quoi qu'ait envoye le client.
        // Le dossier nait en saisie : creer un dossier ne doit pas, du meme geste, mobiliser un
        // controleur pour quelque chose qui n'est pas encore fini d'ecrire.
        demandePriseEnCharge.setReference(genererReference());
        demandePriseEnCharge.setGestionnaireCreateur(currentUser);
        demandePriseEnCharge.setDateCreation(now);
        demandePriseEnCharge.setDateModification(now);
        demandePriseEnCharge.setStatut(StatutDemande.EN_SAISIE);
        demandePriseEnCharge = demandePriseEnChargeRepository.save(demandePriseEnCharge);
        logHistorique(demandePriseEnCharge, currentUser, "CREATION", "Dossier ouvert, en cours de saisie");
        return demandePriseEnChargeMapper.toDto(demandePriseEnCharge);
    }

    @Override
    public DemandePriseEnChargeDTO update(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to update DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        DemandePriseEnCharge demandePriseEnCharge = demandePriseEnChargeMapper.toEntity(demandePriseEnChargeDTO);
        demandePriseEnCharge.setTypeSoins(resoudreTypeSoins(demandePriseEnChargeDTO));
        DemandePriseEnCharge existante = getDemandeOrThrow(demandePriseEnCharge.getId());
        requireAuteurOuAdmin(existante, getCurrentUser());
        // The workflow status is only ever changed through valider/rejeter/resoumettre, never through the
        // generic update endpoint, so callers cannot bypass the DRH / infirmerie validation steps. The
        // reference, author, creation date and rejection reason are likewise fixed by the workflow and
        // never editable through this endpoint, and the modification date is always set by the server.
        demandePriseEnCharge.setStatut(existante.getStatut());
        demandePriseEnCharge.setReference(existante.getReference());
        demandePriseEnCharge.setGestionnaireCreateur(existante.getGestionnaireCreateur());
        demandePriseEnCharge.setDateCreation(existante.getDateCreation());
        demandePriseEnCharge.setMotifRejet(existante.getMotifRejet());
        demandePriseEnCharge.setDateModification(Instant.now());
        demandePriseEnCharge = demandePriseEnChargeRepository.save(demandePriseEnCharge);
        return demandePriseEnChargeMapper.toDto(demandePriseEnCharge);
    }

    @Override
    public Optional<DemandePriseEnChargeDTO> partialUpdate(DemandePriseEnChargeDTO demandePriseEnChargeDTO) {
        LOG.debug("Request to partially update DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        User currentUser = getCurrentUser();

        return demandePriseEnChargeRepository
            .findById(demandePriseEnChargeDTO.getId())
            .map(existingDemandePriseEnCharge -> {
                requireAuteurOuAdmin(existingDemandePriseEnCharge, currentUser);
                StatutDemande statutPersiste = existingDemandePriseEnCharge.getStatut();
                String referencePersistee = existingDemandePriseEnCharge.getReference();
                User auteurPersiste = existingDemandePriseEnCharge.getGestionnaireCreateur();
                Instant dateCreationPersistee = existingDemandePriseEnCharge.getDateCreation();
                String motifRejetPersiste = existingDemandePriseEnCharge.getMotifRejet();
                demandePriseEnChargeMapper.partialUpdate(existingDemandePriseEnCharge, demandePriseEnChargeDTO);
                if (demandePriseEnChargeDTO.getTypeSoins() != null && !demandePriseEnChargeDTO.getTypeSoins().isEmpty()) {
                    existingDemandePriseEnCharge.setTypeSoins(resoudreTypeSoins(demandePriseEnChargeDTO));
                }
                // Same rule as update(): none of these fields can be changed through this endpoint,
                // and the modification date is always set by the server.
                existingDemandePriseEnCharge.setStatut(statutPersiste);
                existingDemandePriseEnCharge.setReference(referencePersistee);
                existingDemandePriseEnCharge.setGestionnaireCreateur(auteurPersiste);
                existingDemandePriseEnCharge.setDateCreation(dateCreationPersistee);
                existingDemandePriseEnCharge.setMotifRejet(motifRejetPersiste);
                existingDemandePriseEnCharge.setDateModification(Instant.now());

                return existingDemandePriseEnCharge;
            })
            .map(demandePriseEnChargeRepository::save)
            .map(demandePriseEnChargeMapper::toDto);
    }

    /**
     * Refuse d'ouvrir une demande pour un beneficiaire qui n'est plus couvert.
     *
     * <p>Le statut est relu en base et non pris dans le corps de la requete : le client envoie
     * l'objet complet, et rien ne l'empecherait d'y declarer un agent actif.
     *
     * <p>La couverture d'un ayant droit derive de celle de son agent. Un ayant droit actif
     * rattache a un agent radie n'ouvre donc aucun droit, et le message le dit, faute de quoi le
     * gestionnaire chercherait l'erreur du mauvais cote.
     */
    private void exigerBeneficiaireCouvert(DemandePriseEnCharge demande) {
        if (demande.getAgent() != null && demande.getAgent().getId() != null) {
            Agent agent = agentRepository
                .findById(demande.getAgent().getId())
                .orElseThrow(() -> new BadRequestAlertException("Agent introuvable", ENTITY_NAME, "agent.introuvable"));
            exigerAgentActif(agent);
        }
        if (demande.getAyantDroit() != null && demande.getAyantDroit().getId() != null) {
            AyantDroit ayantDroit = ayantDroitRepository
                .findById(demande.getAyantDroit().getId())
                .orElseThrow(() -> new BadRequestAlertException("Ayant droit introuvable", ENTITY_NAME, "ayantdroit.introuvable"));
            if (ayantDroit.getStatut() != StatutAyantDroit.ACTIF) {
                throw new BadRequestAlertException(
                    "Cet ayant droit n'ouvre plus droit a la prise en charge (%s). Motif : %s".formatted(
                        ayantDroit.getStatut(),
                        ayantDroit.getMotifStatut() == null ? "non precise" : ayantDroit.getMotifStatut()
                    ),
                    ENTITY_NAME,
                    "ayantdroit.inactif"
                );
            }
            if (ayantDroit.getAgent() != null) {
                exigerAgentActif(ayantDroit.getAgent());
            }
        }
    }

    private void exigerAgentActif(Agent agent) {
        if (agent.getStatut() == StatutAgent.ACTIF) {
            return;
        }
        throw new BadRequestAlertException(
            "L'agent %s n'ouvre plus droit a la prise en charge (%s). Motif : %s".formatted(
                agent.getMatricule(),
                agent.getStatut(),
                agent.getMotifStatut() == null ? "non precise" : agent.getMotifStatut()
            ),
            ENTITY_NAME,
            "agent.inactif"
        );
    }

    /**
     * Relit les types de soin dans le referentiel, plutot que de faire confiance a ce que le
     * client a envoye.
     *
     * <p>Le mapper reconstruit des objets a partir du JSON : leur libelle et leur code viennent
     * du navigateur. Les relire ici garantit qu'un identifiant inconnu est refuse avec un
     * message clair, et qu'un client ne peut pas renommer une categorie au passage.
     */
    private Set<com.mycompany.myapp.domain.TypeSoin> resoudreTypeSoins(DemandePriseEnChargeDTO dto) {
        if (dto.getTypeSoins() == null || dto.getTypeSoins().isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> ids = dto.getTypeSoins().stream().map(TypeSoinDTO::getId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.size() != dto.getTypeSoins().size()) {
            throw new BadRequestAlertException("Un type de soin est sans identifiant", ENTITY_NAME, "typesoin.invalide");
        }
        List<com.mycompany.myapp.domain.TypeSoin> trouves = typeSoinRepository.findAllByIdIn(ids);
        if (trouves.size() != ids.size()) {
            throw new BadRequestAlertException("Un type de soin demande n'existe pas", ENTITY_NAME, "typesoin.introuvable");
        }
        return new HashSet<>(trouves);
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

    /**
     * Les etapes ou une demande n'a encore ete soumise au jugement de personne.
     *
     * <p>Au-dela, le dossier porte une decision - une validation, un retour, un rejet - et la
     * supprimer effacerait la trace de cette decision. Un dossier que l'on ne veut plus suivre
     * s'annule, il ne disparait pas.
     */
    private static final List<StatutDemande> STATUTS_SUPPRIMABLES = List.of(StatutDemande.EN_SAISIE, StatutDemande.NOUVELLE);

    /** La seule action que peut porter une demande encore supprimable. */
    private static final String ACTION_CREATION = "CREATION";

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        exigerSuppressionPossible(demande);

        // Les objets rattaches partent avec le dossier : sans cela la suppression echouerait sur
        // les cles etrangeres, et les taches resteraient a jamais dans les boites de reception.
        notificationRepository.deleteAll(notificationRepository.findByDemandeId(id));
        tacheRepository.deleteAll(tacheRepository.findByDemandeId(id));
        historiqueActionRepository.deleteAll(historiqueActionRepository.findByDemandeIdOrderByDateActionAsc(id));
        demandePriseEnChargeRepository.delete(demande);
    }

    /**
     * Refuse la suppression d'un dossier sur lequel quelque chose a deja ete decide.
     *
     * <p>Deux conditions, et non une seule : le statut dit ou en est le dossier, l'historique dit
     * ce qui lui est arrive. Une demande retournee puis resoumise repasse par
     * {@code EN_ATTENTE_VALIDATION_DRH} - le statut seul la declarerait supprimable alors qu'un
     * validateur s'est deja prononce.
     */
    private void exigerSuppressionPossible(DemandePriseEnCharge demande) {
        if (!STATUTS_SUPPRIMABLES.contains(demande.getStatut())) {
            throw new BadRequestAlertException(
                "Cette demande a deja ete instruite (%s) et ne peut plus etre supprimee. Elle peut etre annulee.".formatted(
                    demande.getStatut()
                ),
                ENTITY_NAME,
                "suppression.instruite"
            );
        }
        boolean dejaInstruite = historiqueActionRepository
            .findByDemandeIdOrderByDateActionAsc(demande.getId())
            .stream()
            .anyMatch(action -> !ACTION_CREATION.equals(action.getAction()));
        if (dejaInstruite) {
            throw new BadRequestAlertException(
                "Cette demande porte deja des decisions et ne peut plus etre supprimee. Elle peut etre annulee.",
                ENTITY_NAME,
                "suppression.instruite"
            );
        }
    }

    @Override
    public DemandePriseEnChargeDTO soumettre(Long id) {
        LOG.debug("Request to soumettre DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        User currentUser = getCurrentUser();
        requireAuteurOuAdmin(demande, currentUser);
        if (demande.getStatut() != StatutDemande.EN_SAISIE) {
            throw new BadRequestAlertException("Ce dossier n'est plus en saisie", ENTITY_NAME, "workflow.invalidstatus");
        }
        exigerBeneficiaireCouvert(demande);

        demande.setStatut(StatutDemande.EN_VERIFICATION_RH);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, "SOUMISSION", "Dossier soumis au controle RH");
        creerTachesValidation(demande, AuthoritiesConstants.VERIFICATEUR_RH, "Verification RH");
        return demandePriseEnChargeMapper.toDto(demande);
    }

    @Override
    public DemandePriseEnChargeDTO verifier(Long id, String commentaire) {
        LOG.debug("Request to verifier DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        if (demande.getStatut() != StatutDemande.EN_VERIFICATION_RH) {
            throw new BadRequestAlertException("Ce dossier n'est pas en verification", ENTITY_NAME, "workflow.invalidstatus");
        }
        requireAuthority(AuthoritiesConstants.VERIFICATEUR_RH);
        User currentUser = getCurrentUser();
        // Le controleur ne valide pas son propre dossier : c'est le principe du double regard.
        requireNotAuteur(demande, currentUser);

        demande.setStatut(StatutDemande.EN_ATTENTE_VALIDATION_DRH);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(
            demande,
            currentUser,
            "VERIFICATION_RH",
            commentaire == null || commentaire.isBlank() ? "Dossier controle, conforme" : commentaire
        );
        cloturerTachesValidation(demande.getId());
        creerTachesValidation(demande, AuthoritiesConstants.VALIDATEUR_DRH, "Validation DRH");
        return demandePriseEnChargeMapper.toDto(demande);
    }

    @Override
    public DemandePriseEnChargeDTO valider(Long id, String commentaire) {
        LOG.debug("Request to valider DemandePriseEnCharge : {}", id);
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        User currentUser = getCurrentUser();
        requireNotAuteur(demande, currentUser);
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
        } else if (statutSuivant == StatutDemande.VALIDEE && demande.getGestionnaireCreateur() != null) {
            creerNotification(
                demande.getGestionnaireCreateur(),
                TypeNotification.DEMANDE_VALIDEE,
                "Demande validée - " + demande.getReference(),
                "Votre demande de prise en charge " + demande.getReference() + " a été validée.",
                demande,
                null
            );
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
        requireNotAuteur(demande, currentUser);
        switch (demande.getStatut()) {
            case EN_VERIFICATION_RH -> requireAuthority(AuthoritiesConstants.VERIFICATEUR_RH);
            case EN_ATTENTE_VALIDATION_DRH -> requireAuthority(AuthoritiesConstants.VALIDATEUR_DRH);
            case EN_ATTENTE_VALIDATION_INFIRMERIE -> requireAuthority(AuthoritiesConstants.VALIDATEUR_INFIRMERIE);
            default -> throw new BadRequestAlertException(
                "Ce dossier n'est ni en verification ni en attente de validation",
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
        if (demande.getGestionnaireCreateur() != null) {
            creerNotification(
                demande.getGestionnaireCreateur(),
                TypeNotification.DEMANDE_REJETEE,
                "Demande retournée - " + demande.getReference(),
                "Votre demande de prise en charge " + demande.getReference() + " a été retournée pour correction : " + motif,
                demande,
                null
            );
        }
        return demandePriseEnChargeMapper.toDto(demande);
    }

    @Override
    public DemandePriseEnChargeDTO rejeterDefinitivement(Long id, String motif) {
        LOG.debug("Request to rejeter definitivement DemandePriseEnCharge : {}", id);
        if (motif == null || motif.isBlank()) {
            throw new BadRequestAlertException("Le motif de rejet est obligatoire", ENTITY_NAME, "workflow.motifrequired");
        }
        DemandePriseEnCharge demande = getDemandeOrThrow(id);
        User currentUser = getCurrentUser();
        requireNotAuteur(demande, currentUser);
        switch (demande.getStatut()) {
            case EN_VERIFICATION_RH -> requireAuthority(AuthoritiesConstants.VERIFICATEUR_RH);
            case EN_ATTENTE_VALIDATION_DRH -> requireAuthority(AuthoritiesConstants.VALIDATEUR_DRH);
            case EN_ATTENTE_VALIDATION_INFIRMERIE -> requireAuthority(AuthoritiesConstants.VALIDATEUR_INFIRMERIE);
            default -> throw new BadRequestAlertException(
                "Ce dossier n'est pas en cours d'instruction",
                ENTITY_NAME,
                "workflow.invalidstatus"
            );
        }

        demande.setStatut(StatutDemande.REJETEE);
        demande.setMotifRejet(motif.trim());
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, "REJET", motif.trim());
        cloturerTachesValidation(demande.getId());
        if (demande.getGestionnaireCreateur() != null) {
            creerNotification(
                demande.getGestionnaireCreateur(),
                TypeNotification.DEMANDE_REJETEE,
                "Demande rejetée - " + demande.getReference(),
                "La demande de prise en charge " +
                    demande.getReference() +
                    " a été rejetée : " +
                    motif.trim() +
                    " La notification de décision est éditable depuis le dossier.",
                demande,
                null
            );
        }
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
        // Le dossier corrige repasse par le controle : sauter l'etape reviendrait a faire
        // valider des pieces que personne n'a revues depuis la correction.
        demande.setStatut(StatutDemande.EN_VERIFICATION_RH);
        demande.setMotifRejet(null);
        demande.setDateModification(Instant.now());
        demande = demandePriseEnChargeRepository.save(demande);
        logHistorique(demande, currentUser, "RESOUMISSION", "Dossier corrige et resoumis au controle RH");
        creerTachesValidation(demande, AuthoritiesConstants.VERIFICATEUR_RH, "Verification RH");
        return demandePriseEnChargeMapper.toDto(demande);
    }

    /**
     * Builds a new, unique reference: today's date (yyMMdd) followed by a 7-digit sequence
     * that never resets (a dedicated DB sequence, so it's gap-free and safe under concurrent
     * creations), e.g. {@code 2609150000001} for the first demande ever created on 2026-09-15.
     */
    private String genererReference() {
        String prefixeDate = REFERENCE_DATE_FORMATTER.format(LocalDate.now(ZoneId.systemDefault()));
        long sequence = demandePriseEnChargeRepository.nextReferenceSequenceValue();
        return prefixeDate + String.format("%07d", sequence);
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

    /**
     * A validator who also happens to hold ROLE_USER (and so can create demandes) must not be able to
     * validate or reject their own submission - that would let a single person clear a workflow step
     * meant to be an independent check.
     */
    private void requireNotAuteur(DemandePriseEnCharge demande, User currentUser) {
        if (demande.getGestionnaireCreateur() != null && currentUser.getId().equals(demande.getGestionnaireCreateur().getId())) {
            throw new AccessDeniedException("Un validateur ne peut pas valider ou rejeter sa propre demande");
        }
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
    }

    /**
     * Only the demande's own author (gestionnaireCreateur) or an admin may go through the generic
     * update/partialUpdate endpoints - anyone else, including a validator who happens to also hold
     * ROLE_USER, must go through valider/rejeter instead.
     */
    private void requireAuteurOuAdmin(DemandePriseEnCharge demande, User currentUser) {
        boolean estAuteur =
            demande.getGestionnaireCreateur() != null && currentUser.getId().equals(demande.getGestionnaireCreateur().getId());
        if (!estAuteur && !SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            throw new AccessDeniedException("Seul l'auteur de cette demande (ou un administrateur) peut la modifier");
        }
    }

    /**
     * Creates a Tache for every user holding the given authority, so that the demande shows up in
     * "Mes taches" for whoever is meant to validate it next.
     */
    /**
     * Cree la tache de l'etape : une seule, adressee au droit qui en donne la charge.
     *
     * <p>Auparavant une tache etait creee par titulaire du droit. Quatre validateurs DRH
     * produisaient quatre taches pour un seul dossier : la boite du profil les affichait toutes
     * les quatre, et personne ne savait laquelle lui revenait. Une tache par etape, que tous
     * voient et que le premier disponible prend en charge.
     *
     * <p>Aucune notification n'accompagne la tache : elle en repeterait le titre et le texte, et
     * ferait deux boites a relever pour un seul evenement. La tache est la file de travail, la
     * notification signale ce qui ne cree pas de travail.
     */
    private void creerTachesValidation(DemandePriseEnCharge demande, String authority, String titrePrefix) {
        // Idempotent : une reprise du circuit ne doit pas empiler deux fois la meme etape.
        if (tacheRepository.existsByDemandeIdAndDroitRequisAndStatutNotIn(demande.getId(), authority, STATUTS_TACHE_CLOTURES)) {
            return;
        }
        Instant now = Instant.now();
        Tache tache = new Tache();
        tache.setTitre(titrePrefix + " - " + demande.getReference());
        tache.setDescription("Demande de prise en charge en attente de validation.");
        tache.setDateCreation(now);
        tache.setDateEcheance(demande.getDateEcheance());
        tache.setStatut(StatutTache.A_FAIRE);
        tache.setPriorite(PrioriteTache.valueOf(demande.getPriorite().name()));
        tache.setLu(false);
        tache.setDemande(demande);
        tache.setDroitRequis(authority);
        // Pas d'utilisateur : la tache attend que quelqu'un s'en saisisse.
        tacheRepository.save(tache);
    }

    /**
     * Clot les taches encore ouvertes du dossier : l'etape vient de changer, ce qui attendait
     * n'attend plus.
     */
    private void cloturerTachesValidation(Long demandeId) {
        List<Tache> taches = tacheRepository.findByDemandeIdAndStatutNotIn(demandeId, STATUTS_TACHE_CLOTURES);
        Instant now = Instant.now();
        for (Tache tache : taches) {
            tache.setStatut(StatutTache.TERMINEE);
            tache.setDateTerminaison(now);
        }
        tacheRepository.saveAll(taches);
    }

    private void creerNotification(
        User destinataire,
        TypeNotification type,
        String titre,
        String message,
        DemandePriseEnCharge demande,
        Tache tache
    ) {
        Notification notification = new Notification();
        notification.setTitre(titre);
        notification.setMessage(message);
        notification.setDateCreation(Instant.now());
        notification.setLu(false);
        notification.setType(type);
        notification.setUtilisateur(destinataire);
        notification.setDemande(demande);
        notification.setTache(tache);
        notificationRepository.save(notification);
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
