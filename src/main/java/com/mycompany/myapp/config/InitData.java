package com.mycompany.myapp.config;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Parametre;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.TypeParametre;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.ParametreRepository;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.Parametres;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cree au demarrage le referentiel fin des habilitations - une par action - et les profils types
 * qui les portent.
 *
 * <p>{@link InitialisationHabilitations} pose les habilitations de metier : « validateur DRH »,
 * « verificateur RH ». Elles disent un role tenu dans le circuit, et restent ce qui decide des
 * etapes de validation. Elles ne disent rien, en revanche, de ce que quelqu'un peut faire au
 * quotidien : le meme mot « gestionnaire » recouvre celui qui saisit un dossier et celui qui
 * tient le referentiel des etablissements. Cette classe descend d'un cran : une habilitation par
 * geste, de sorte qu'un profil puisse n'ouvrir que ce dont son titulaire a besoin.
 *
 * <p>Le traitement est idempotent et n'enleve jamais rien. Il cree ce qui manque, complete les
 * descriptions vides, et laisse intact ce qui existe : un profil ajuste a la main en production
 * ne doit pas etre defait au redemarrage. En particulier, un profil deja present n'est pas
 * recompose - seules ses habilitations manquantes lui sont ajoutees, jamais retirees.
 *
 * <p>Une erreur ici n'empeche pas l'application de demarrer : refuser le demarrage bloquerait
 * tout le monde pour un defaut qui ne concerne que le referentiel des droits.
 *
 * <p><strong>Ces habilitations sont declarees, pas encore exigees.</strong> Les points d'entree
 * continuent de s'appuyer sur les habilitations de metier : les exiger d'un coup retirerait
 * l'acces a tous les comptes en place, dont aucun ne les porte. Elles sont donc disponibles a
 * l'attribution, et chaque point d'entree pourra s'y raccorder une fois les profils distribues.
 */
@Component
@Order(InitData.ORDRE)
public class InitData {

    /**
     * Apres les habilitations de metier, avant la synchronisation des boites de reception.
     *
     * <p>L'ordre compte : les profils crees ici doivent recevoir leur boite, et celle-ci est
     * posee par {@code BoiteReceptionInitialiseur} a l'ordre {@code ORDRE + 10} du precedent.
     */
    public static final int ORDRE = InitialisationHabilitations.ORDRE + 5;

    private static final Logger LOG = LoggerFactory.getLogger(InitData.class);

    /**
     * Les habilitations fines et ce qu'elles autorisent, en francais administratif.
     *
     * <p>Ce texte est lu par un gestionnaire au moment de composer un profil, pas par un
     * developpeur : il dit le geste, pas le point d'entree qui le sert.
     *
     * <p>L'ordre est celui de l'ecran : les beneficiaires, puis les dossiers, puis le travail
     * courant, puis les referentiels, puis l'administration.
     */
    private static final Map<String, String> HABILITATIONS = new LinkedHashMap<>();

    static {
        // ---------------------------------------------------------------- agents
        HABILITATIONS.put(ActionsConstants.AGENT_CONSULTER, "Consulter la liste des agents et le dossier de chacun.");
        HABILITATIONS.put(ActionsConstants.AGENT_CREER, "Enregistrer un nouvel agent.");
        HABILITATIONS.put(ActionsConstants.AGENT_MODIFIER, "Corriger l'identite et les coordonnees d'un agent.");
        HABILITATIONS.put(ActionsConstants.AGENT_SUPPRIMER, "Supprimer un agent enregistre par erreur.");
        HABILITATIONS.put(
            ActionsConstants.AGENT_CHANGER_STATUT,
            "Changer la situation d'un agent : suspension, retraite, radiation, deces. Se repercute sur ses ayants droit."
        );
        HABILITATIONS.put(ActionsConstants.AGENT_EXPORTER, "Exporter la liste des agents au format tableur ou PDF.");

        // --------------------------------------------------------- ayants droit
        HABILITATIONS.put(ActionsConstants.AYANT_DROIT_CONSULTER, "Consulter les ayants droit rattaches a un agent.");
        HABILITATIONS.put(ActionsConstants.AYANT_DROIT_CREER, "Rattacher un ayant droit a un agent.");
        HABILITATIONS.put(ActionsConstants.AYANT_DROIT_MODIFIER, "Corriger l'identite d'un ayant droit ou son lien de parente.");
        HABILITATIONS.put(ActionsConstants.AYANT_DROIT_SUPPRIMER, "Supprimer un rattachement saisi par erreur, avant sa verification.");
        HABILITATIONS.put(
            ActionsConstants.AYANT_DROIT_VALIDER,
            "Declarer un rattachement verifie sur pieces. Tant qu'il ne l'est pas, il ne fonde aucune prise en charge."
        );
        HABILITATIONS.put(
            ActionsConstants.AYANT_DROIT_CHANGER_STATUT,
            "Changer la situation d'un ayant droit : suspension, radiation, deces."
        );
        HABILITATIONS.put(ActionsConstants.AYANT_DROIT_EXPORTER, "Exporter la liste des ayants droit au format tableur ou PDF.");

        // ------------------------------------------------------ cartes de soins
        HABILITATIONS.put(ActionsConstants.CARTE_CONSULTER, "Consulter les cartes de beneficiaire et leur validite.");
        HABILITATIONS.put(ActionsConstants.CARTE_CREER, "Etablir une carte de beneficiaire.");
        HABILITATIONS.put(ActionsConstants.CARTE_MODIFIER, "Corriger une carte de beneficiaire ou en prolonger la validite.");
        HABILITATIONS.put(ActionsConstants.CARTE_SUPPRIMER, "Supprimer une carte etablie par erreur.");
        HABILITATIONS.put(ActionsConstants.CARTE_IMPRIMER, "Imprimer une carte de beneficiaire.");

        // ------------------------------------------------------ prises en charge
        HABILITATIONS.put(ActionsConstants.DEMANDE_CONSULTER, "Consulter les demandes de prise en charge.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_CREER, "Ouvrir une demande de prise en charge.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_MODIFIER, "Modifier une demande tant qu'elle est en saisie.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_SUPPRIMER, "Supprimer une demande ouverte par erreur, avant toute soumission.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_SOUMETTRE, "Soumettre une demande a l'avis de l'infirmerie du personnel.");
        HABILITATIONS.put(
            ActionsConstants.DEMANDE_VERIFIER,
            "Controler les pieces d'une demande et attester de sa conformite avant la validation finale."
        );
        HABILITATIONS.put(ActionsConstants.DEMANDE_VALIDER_INFIRMERIE, "Donner l'avis de l'infirmerie du personnel sur une demande.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_VALIDER_DRH, "Prononcer la validation finale d'une demande, au titre de la DRH.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_RETOURNER, "Retourner une demande a son auteur pour correction.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_REJETER, "Rejeter definitivement une demande, avec le motif du refus.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_RESOUMETTRE, "Corriger une demande retournee et la resoumettre au circuit.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_ANNULER, "Annuler une demande sans la supprimer, en conservant sa trace.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_IMPRIMER, "Imprimer l'attestation de prise en charge d'une demande validee.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_NOTIFIER, "Editer la notification de decision a remettre a l'agent.");
        HABILITATIONS.put(ActionsConstants.DEMANDE_EXPORTER, "Exporter la liste des demandes au format tableur ou PDF.");

        // -------------------------------------------------- pieces justificatives
        HABILITATIONS.put(ActionsConstants.PIECE_CONSULTER, "Consulter les pieces jointes a un dossier.");
        HABILITATIONS.put(ActionsConstants.PIECE_AJOUTER, "Joindre une piece a un dossier en saisie.");
        HABILITATIONS.put(ActionsConstants.PIECE_MODIFIER, "Remplacer ou renommer une piece d'un dossier en saisie.");
        HABILITATIONS.put(ActionsConstants.PIECE_SUPPRIMER, "Retirer une piece d'un dossier en saisie.");

        // ---------------------------------------------------------------- taches
        HABILITATIONS.put(ActionsConstants.TACHE_CONSULTER, "Consulter les taches de sa boite de reception.");
        HABILITATIONS.put(
            ActionsConstants.TACHE_PRENDRE_EN_CHARGE,
            "Prendre une tache en charge, pour que nul autre ne la traite en meme temps."
        );
        HABILITATIONS.put(ActionsConstants.TACHE_RELACHER, "Relacher une tache prise en charge, pour la rendre a la boite.");
        HABILITATIONS.put(ActionsConstants.TACHE_CLOTURER, "Clore une tache traitee.");

        // ------------------------------------------------ boite, avis, historique
        HABILITATIONS.put(ActionsConstants.BOITE_CONSULTER, "Consulter sa boite de reception.");
        HABILITATIONS.put(ActionsConstants.NOTIFICATION_CONSULTER, "Consulter les avis recus.");
        HABILITATIONS.put(ActionsConstants.NOTIFICATION_MARQUER_LUE, "Marquer un avis comme lu.");
        HABILITATIONS.put(ActionsConstants.HISTORIQUE_CONSULTER, "Consulter le journal des actions portees sur un dossier.");

        // ----------------------------------------------------------- referentiels
        HABILITATIONS.put(
            ActionsConstants.REFERENTIEL_CONSULTER,
            "Consulter les referentiels : regions, directions, etablissements de sante, types de soin, gestions."
        );
        HABILITATIONS.put(ActionsConstants.REFERENTIEL_CREER, "Ajouter une entree a un referentiel.");
        HABILITATIONS.put(ActionsConstants.REFERENTIEL_MODIFIER, "Corriger une entree d'un referentiel.");
        HABILITATIONS.put(ActionsConstants.REFERENTIEL_SUPPRIMER, "Supprimer une entree d'un referentiel.");

        // --------------------------------------------------------- administration
        HABILITATIONS.put(ActionsConstants.UTILISATEUR_CONSULTER, "Consulter les comptes d'acces a l'application.");
        HABILITATIONS.put(ActionsConstants.UTILISATEUR_CREER, "Creer un compte d'acces.");
        HABILITATIONS.put(ActionsConstants.UTILISATEUR_MODIFIER, "Modifier un compte d'acces et le profil qui lui est attribue.");
        HABILITATIONS.put(ActionsConstants.UTILISATEUR_SUPPRIMER, "Supprimer un compte cree par erreur.");
        HABILITATIONS.put(
            ActionsConstants.UTILISATEUR_REINITIALISER_MOT_DE_PASSE,
            "Donner un mot de passe provisoire a un compte, a charge pour son titulaire d'en choisir un autre."
        );
        HABILITATIONS.put(
            ActionsConstants.UTILISATEUR_ACTIVER,
            "Ouvrir ou fermer un compte. Fermer n'est pas supprimer : les dossiers instruits gardent leur auteur."
        );
        HABILITATIONS.put(ActionsConstants.PROFIL_CONSULTER, "Consulter les profils et les droits qu'ils accordent.");
        HABILITATIONS.put(ActionsConstants.PROFIL_CREER, "Composer un nouveau profil.");
        HABILITATIONS.put(ActionsConstants.PROFIL_MODIFIER, "Modifier les droits d'un profil. Se repercute aussitot sur ses titulaires.");
        HABILITATIONS.put(ActionsConstants.PROFIL_SUPPRIMER, "Supprimer un profil qui n'est attribue a personne.");
        HABILITATIONS.put(ActionsConstants.HABILITATION_CONSULTER, "Consulter le referentiel des habilitations.");
        HABILITATIONS.put(ActionsConstants.HABILITATION_MODIFIER, "Modifier le libelle d'une habilitation.");

        // ---------------------------------------------------------- espace agent
        HABILITATIONS.put(
            ActionsConstants.ESPACE_AGENT,
            "Acceder a son propre espace : ses dossiers, ses ayants droit, sa carte. Portee seule, elle borne ce que l'agent voit a ce qui le concerne."
        );
    }

    /**
     * Un profil type : son nom, ce qu'il recouvre, et les habilitations qu'il porte.
     *
     * @param nom le nom affiche, qui sert aussi de cle : deux profils ne peuvent pas le partager.
     * @param description ce que le profil recouvre, en une phrase.
     * @param habilitations les habilitations accordees, metier et fines confondues.
     */
    private record ProfilType(String nom, String description, List<String> habilitations) {}

    /**
     * Le socle commun a tout profil : entrer dans l'application, voir sa boite, ses avis.
     *
     * <p>Un profil qui ne le porterait pas se connecterait sur un ecran vide - ce qui se lit
     * comme une panne, et non comme un droit manquant.
     */
    private static final List<String> SOCLE = List.of(
        AuthoritiesConstants.USER,
        ActionsConstants.BOITE_CONSULTER,
        ActionsConstants.TACHE_CONSULTER,
        ActionsConstants.NOTIFICATION_CONSULTER,
        ActionsConstants.NOTIFICATION_MARQUER_LUE,
        ActionsConstants.HISTORIQUE_CONSULTER
    );

    /** Voir les beneficiaires sans pouvoir les modifier : necessaire pour instruire un dossier. */
    private static final List<String> LECTURE_BENEFICIAIRES = List.of(
        ActionsConstants.AGENT_CONSULTER,
        ActionsConstants.AYANT_DROIT_CONSULTER,
        ActionsConstants.CARTE_CONSULTER,
        ActionsConstants.REFERENTIEL_CONSULTER
    );

    /**
     * Les profils que l'application pose si elle ne les trouve pas.
     *
     * <p>Ils correspondent aux postes reellement tenus : celui qui saisit, celui qui controle,
     * celui qui donne l'avis medical, celui qui tranche, celui qui administre. Une installation
     * neuve est ainsi utilisable sans qu'il faille d'abord composer cinq profils a la main -
     * l'occasion, sinon, d'accorder « administrateur » a tout le monde pour avancer.
     */
    private static final List<ProfilType> PROFILS_TYPES = List.of(
        new ProfilType(
            "Gestionnaire",
            "Saisit et suit les demandes de prise en charge. Profil par defaut.",
            concatener(
                SOCLE,
                LECTURE_BENEFICIAIRES,
                List.of(
                    ActionsConstants.AGENT_CREER,
                    ActionsConstants.AGENT_MODIFIER,
                    ActionsConstants.AYANT_DROIT_CREER,
                    ActionsConstants.AYANT_DROIT_MODIFIER,
                    ActionsConstants.AYANT_DROIT_SUPPRIMER,
                    ActionsConstants.DEMANDE_CONSULTER,
                    ActionsConstants.DEMANDE_CREER,
                    ActionsConstants.DEMANDE_MODIFIER,
                    ActionsConstants.DEMANDE_SUPPRIMER,
                    ActionsConstants.DEMANDE_SOUMETTRE,
                    ActionsConstants.DEMANDE_RESOUMETTRE,
                    ActionsConstants.DEMANDE_ANNULER,
                    ActionsConstants.DEMANDE_IMPRIMER,
                    ActionsConstants.DEMANDE_EXPORTER,
                    ActionsConstants.PIECE_CONSULTER,
                    ActionsConstants.PIECE_AJOUTER,
                    ActionsConstants.PIECE_MODIFIER,
                    ActionsConstants.PIECE_SUPPRIMER,
                    ActionsConstants.TACHE_PRENDRE_EN_CHARGE,
                    ActionsConstants.TACHE_RELACHER,
                    ActionsConstants.TACHE_CLOTURER
                )
            )
        ),
        new ProfilType(
            "Verificateur RH",
            "Controle les dossiers et les rattachements avant validation.",
            concatener(
                SOCLE,
                LECTURE_BENEFICIAIRES,
                List.of(
                    AuthoritiesConstants.VERIFICATEUR_RH,
                    ActionsConstants.AYANT_DROIT_VALIDER,
                    ActionsConstants.DEMANDE_CONSULTER,
                    ActionsConstants.DEMANDE_VERIFIER,
                    ActionsConstants.DEMANDE_RETOURNER,
                    ActionsConstants.DEMANDE_EXPORTER,
                    ActionsConstants.PIECE_CONSULTER,
                    ActionsConstants.TACHE_PRENDRE_EN_CHARGE,
                    ActionsConstants.TACHE_RELACHER,
                    ActionsConstants.TACHE_CLOTURER
                )
            )
        ),
        new ProfilType(
            "Validateur infirmerie",
            "Donne l'avis de l'infirmerie du personnel sur les demandes de prise en charge.",
            concatener(
                SOCLE,
                LECTURE_BENEFICIAIRES,
                List.of(
                    AuthoritiesConstants.VALIDATEUR_INFIRMERIE,
                    ActionsConstants.DEMANDE_CONSULTER,
                    ActionsConstants.DEMANDE_VALIDER_INFIRMERIE,
                    ActionsConstants.DEMANDE_RETOURNER,
                    ActionsConstants.DEMANDE_REJETER,
                    ActionsConstants.PIECE_CONSULTER,
                    ActionsConstants.TACHE_PRENDRE_EN_CHARGE,
                    ActionsConstants.TACHE_RELACHER,
                    ActionsConstants.TACHE_CLOTURER
                )
            )
        ),
        new ProfilType(
            "Validateur DRH",
            "Prononce la validation finale des demandes de prise en charge.",
            concatener(
                SOCLE,
                LECTURE_BENEFICIAIRES,
                List.of(
                    AuthoritiesConstants.VALIDATEUR_DRH,
                    ActionsConstants.DEMANDE_CONSULTER,
                    ActionsConstants.DEMANDE_VALIDER_DRH,
                    ActionsConstants.DEMANDE_RETOURNER,
                    ActionsConstants.DEMANDE_REJETER,
                    ActionsConstants.DEMANDE_IMPRIMER,
                    ActionsConstants.DEMANDE_NOTIFIER,
                    ActionsConstants.DEMANDE_EXPORTER,
                    ActionsConstants.PIECE_CONSULTER,
                    ActionsConstants.TACHE_PRENDRE_EN_CHARGE,
                    ActionsConstants.TACHE_RELACHER,
                    ActionsConstants.TACHE_CLOTURER
                )
            )
        ),
        new ProfilType(
            "Gestionnaire des referentiels",
            "Tient a jour les regions, directions, etablissements de sante et types de soin.",
            concatener(
                SOCLE,
                List.of(
                    ActionsConstants.REFERENTIEL_CONSULTER,
                    ActionsConstants.REFERENTIEL_CREER,
                    ActionsConstants.REFERENTIEL_MODIFIER,
                    ActionsConstants.REFERENTIEL_SUPPRIMER
                )
            )
        ),
        new ProfilType(
            "Gestionnaire des cartes",
            "Etablit et imprime les cartes de beneficiaire.",
            concatener(
                SOCLE,
                LECTURE_BENEFICIAIRES,
                List.of(
                    ActionsConstants.CARTE_CREER,
                    ActionsConstants.CARTE_MODIFIER,
                    ActionsConstants.CARTE_SUPPRIMER,
                    ActionsConstants.CARTE_IMPRIMER
                )
            )
        ),
        new ProfilType(
            "Agent",
            "Acces d'un agent a son propre espace : ses dossiers, ses ayants droit, sa carte.",
            concatener(
                // Pas le socle entier : un agent n'a ni boite de reception ni taches. Les lui
                // ouvrir afficherait deux ecrans vides, qu'il prendrait pour une panne.
                List.of(
                    AuthoritiesConstants.USER,
                    ActionsConstants.NOTIFICATION_CONSULTER,
                    ActionsConstants.NOTIFICATION_MARQUER_LUE,
                    ActionsConstants.HISTORIQUE_CONSULTER
                ),
                List.of(
                    // C'est elle qui borne : sans les habilitations de consultation d'ensemble,
                    // l'agent ne voit que ce qui le concerne. Voir PerimetreAgent.
                    ActionsConstants.ESPACE_AGENT,
                    ActionsConstants.AGENT_CONSULTER,
                    ActionsConstants.AYANT_DROIT_CONSULTER,
                    ActionsConstants.CARTE_CONSULTER,
                    ActionsConstants.CARTE_IMPRIMER,
                    // Le referentiel lui sert a designer un etablissement et un type de soin :
                    // sans lui, le formulaire d'ouverture de dossier serait vide.
                    ActionsConstants.REFERENTIEL_CONSULTER,
                    ActionsConstants.DEMANDE_CONSULTER,
                    ActionsConstants.DEMANDE_CREER,
                    ActionsConstants.DEMANDE_MODIFIER,
                    ActionsConstants.DEMANDE_SUPPRIMER,
                    ActionsConstants.DEMANDE_SOUMETTRE,
                    ActionsConstants.DEMANDE_RESOUMETTRE,
                    ActionsConstants.DEMANDE_ANNULER,
                    ActionsConstants.DEMANDE_IMPRIMER,
                    ActionsConstants.DEMANDE_NOTIFIER,
                    ActionsConstants.PIECE_CONSULTER,
                    ActionsConstants.PIECE_AJOUTER,
                    ActionsConstants.PIECE_MODIFIER,
                    ActionsConstants.PIECE_SUPPRIMER
                )
            )
        ),
        new ProfilType(
            "Consultation",
            "Consulte les dossiers et les beneficiaires, sans rien pouvoir modifier.",
            concatener(SOCLE, LECTURE_BENEFICIAIRES, List.of(ActionsConstants.DEMANDE_CONSULTER, ActionsConstants.PIECE_CONSULTER))
        )
    );

    private final AuthorityRepository authorityRepository;

    private final ProfilRepository profilRepository;

    private final UserRepository userRepository;

    private final CacheManager cacheManager;

    private final ParametreRepository parametreRepository;

    public InitData(
        AuthorityRepository authorityRepository,
        ProfilRepository profilRepository,
        UserRepository userRepository,
        CacheManager cacheManager,
        ParametreRepository parametreRepository
    ) {
        this.authorityRepository = authorityRepository;
        this.profilRepository = profilRepository;
        this.userRepository = userRepository;
        this.cacheManager = cacheManager;
        this.parametreRepository = parametreRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialiser() {
        try {
            creerLesParametresManquants();
            creerLesHabilitationsManquantes();
            creerLesProfilsManquants();
            completerLeSuperAdmin();
            repercuterSurLesTitulaires();
        } catch (RuntimeException e) {
            LOG.error("Le referentiel des habilitations fines n'a pas pu etre initialise au demarrage", e);
        }
    }

    /**
     * Un reglage du socle : son code, ce qu'il regle, sa valeur d'origine et son type.
     *
     * @param code le code interroge par le programme, stable et jamais traduit.
     * @param libelle ce que le reglage regle, lu par celui qui l'ajuste.
     * @param description ce que la valeur change, et dans quelles limites elle a du sens.
     * @param valeur la valeur d'origine ; vide pour une image, qui se depose ensuite.
     * @param type comment lire la valeur.
     */
    private record ParametreSocle(String code, String libelle, String description, String valeur, TypeParametre type) {}

    /**
     * Les reglages que l'application pose si elle ne les trouve pas.
     *
     * <p>Leur valeur d'origine est celle qui etait ecrite en dur dans le code : poser le
     * referentiel ne change donc rien au comportement tant que personne n'y touche. C'est
     * voulu - une migration qui modifie en meme temps la structure et les regles ne se verifie
     * pas.
     */
    private static final List<ParametreSocle> PARAMETRES = List.of(
        new ParametreSocle(
            Parametres.VALIDITE_PRISE_EN_CHARGE_JOURS,
            "Validite d'une prise en charge (jours)",
            "Delai au-dela duquel un dossier expire : il ne poursuit plus le circuit et ne s'imprime plus. Il reste consultable.",
            "14",
            TypeParametre.ENTIER
        ),
        new ParametreSocle(
            Parametres.VALIDITE_CARTE_ANNEES,
            "Validite d'une carte de beneficiaire (annees)",
            "Duree pour laquelle une carte est etablie. Assez longue pour ne pas refaire le geste chaque annee, assez courte pour que la photo reste juste.",
            "3",
            TypeParametre.ENTIER
        ),
        new ParametreSocle(
            Parametres.ALERTE_CARTE_JOURS,
            "Alerte avant echeance d'une carte (jours)",
            "En deca de ce delai, une carte encore valide est signalee comme a renouveler.",
            "30",
            TypeParametre.ENTIER
        ),
        new ParametreSocle(
            Parametres.TAILLE_MAX_PIECE_MO,
            "Taille maximale d'une piece jointe (Mo)",
            "Au-dela, c'est un scan a reprendre plutot qu'une piece a joindre.",
            "10",
            TypeParametre.ENTIER
        ),
        new ParametreSocle(
            Parametres.NOM_DIRECTEUR_GENERAL,
            "Nom du directeur general",
            "Imprime sous la signature, sur les cartes et les imprimes.",
            "",
            TypeParametre.TEXTE
        ),
        new ParametreSocle(
            Parametres.SIGNATURE_DIRECTEUR_GENERAL,
            "Signature du directeur general",
            "Image apposee sur les cartes de beneficiaire. Un fond transparent (PNG) se pose proprement sur la carte.",
            "",
            TypeParametre.IMAGE
        ),
        new ParametreSocle(
            Parametres.NOM_INSTITUTION,
            "Nom de l'institution",
            "Tel qu'il s'imprime en tete des cartes et des imprimes.",
            "Caisse Nationale de Securite Sociale",
            TypeParametre.TEXTE
        ),
        new ParametreSocle(
            Parametres.PAYS_INSTITUTION,
            "Pays",
            "Tel qu'il s'imprime au dos des cartes.",
            "Republique du Niger",
            TypeParametre.TEXTE
        )
    );

    /**
     * Cree les reglages absents.
     *
     * <p>Un reglage deja present n'est jamais ecrase : sa valeur a pu etre ajustee, et c'est
     * tout l'objet du referentiel. Seuls son libelle et sa description sont completes quand ils
     * manquent, pour qu'un ajout de documentation atteigne les bases existantes.
     */
    private void creerLesParametresManquants() {
        int crees = 0;
        for (ParametreSocle modele : PARAMETRES) {
            Parametre parametre = parametreRepository.findOneByCode(modele.code()).orElse(null);
            if (parametre == null) {
                parametre = new Parametre();
                parametre.setCode(modele.code());
                parametre.setLibelle(modele.libelle());
                parametre.setDescription(modele.description());
                parametre.setValeur(modele.valeur());
                parametre.setType(modele.type());
                parametre.setSocle(true);
                parametreRepository.save(parametre);
                crees++;
                continue;
            }
            boolean complete = false;
            if (parametre.getDescription() == null || parametre.getDescription().isBlank()) {
                parametre.setDescription(modele.description());
                complete = true;
            }
            if (!parametre.isSocle()) {
                parametre.setSocle(true);
                complete = true;
            }
            if (complete) {
                parametreRepository.save(parametre);
            }
        }
        if (crees > 0) {
            LOG.info("Parametres : {} cree(s)", crees);
        }
    }

    /**
     * Cree les habilitations absentes et renseigne les descriptions vides.
     *
     * <p>Une description deja saisie n'est pas ecrasee : elle a pu etre precisee en base pour
     * coller au vocabulaire de l'institution, et un redemarrage ne doit pas defaire cela.
     */
    private void creerLesHabilitationsManquantes() {
        int creees = 0;
        int completees = 0;

        for (Map.Entry<String, String> entree : HABILITATIONS.entrySet()) {
            Authority habilitation = authorityRepository.findById(entree.getKey()).orElse(null);
            if (habilitation == null) {
                habilitation = new Authority();
                habilitation.setName(entree.getKey());
                habilitation.setDescription(entree.getValue());
                authorityRepository.save(habilitation);
                creees++;
            } else if (habilitation.getDescription() == null || habilitation.getDescription().isBlank()) {
                habilitation.setDescription(entree.getValue());
                authorityRepository.save(habilitation);
                completees++;
            }
        }

        if (creees > 0 || completees > 0) {
            LOG.info("Habilitations par action : {} creee(s), {} description(s) completee(s)", creees, completees);
        }
    }

    /**
     * Cree les profils types absents.
     *
     * <p>Un profil qui existe deja n'est pas recompose : ses habilitations ont pu etre ajustees
     * pour coller a l'organisation du service, et les remettre a la liste d'origine retirerait
     * des droits que quelqu'un a explicitement accordes. Il est seulement complete du socle
     * commun s'il lui manque - sans quoi son titulaire se connecterait sur un ecran vide.
     */
    private void creerLesProfilsManquants() {
        int crees = 0;

        for (ProfilType type : PROFILS_TYPES) {
            Profil existant = profilRepository.findOneWithAuthoritiesByNom(type.nom()).orElse(null);
            if (existant != null) {
                completer(existant, type.habilitations());
                continue;
            }
            Profil profil = new Profil();
            profil.setNom(type.nom());
            profil.setDescription(type.description());
            profil.setAuthorities(resoudre(type.habilitations()));
            profilRepository.save(profil);
            crees++;
            LOG.info("Profil « {} » cree avec {} habilitation(s)", type.nom(), profil.getAuthorities().size());
        }

        if (crees == 0) {
            LOG.debug("Profils types : tous deja presents");
        }
    }

    /**
     * Ajoute a un profil les habilitations qui lui manquent, sans jamais rien retirer.
     *
     * <p>C'est ce qui rend l'exigence des habilitations fines praticable : les profils en place
     * ont ete composes avant qu'elles n'existent, et les exiger sans les leur accorder mettrait
     * dehors tous leurs titulaires du jour au lendemain.
     *
     * <p>La contrepartie est assumee : une habilitation retiree a la main d'un profil type lui
     * sera rendue au redemarrage suivant. Retirer un droit se fait donc en composant un profil
     * propre, et non en amputant un profil type.
     */
    private void completer(Profil profil, List<String> habilitations) {
        Set<Authority> manquantes = resoudre(habilitations);
        manquantes.removeAll(profil.getAuthorities());
        if (manquantes.isEmpty()) {
            return;
        }
        profil.getAuthorities().addAll(manquantes);
        profilRepository.save(profil);
        LOG.info("Profil « {} » complete de {} habilitation(s)", profil.getNom(), manquantes.size());
    }

    /**
     * Donne au profil super administrateur toutes les habilitations, fines comprises.
     *
     * <p>{@link InitialisationHabilitations} ne lui accorde que les habilitations de metier -
     * il ignore celles declarees ici. Sans ce complement, le profil « toutes les habilitations »
     * n'en porterait qu'une poignee, et l'ecran d'administration le dirait.
     */
    private void completerLeSuperAdmin() {
        profilRepository
            .findOneWithAuthoritiesByNom(InitialisationHabilitations.PROFIL_SUPER_ADMIN)
            .ifPresent(profil -> completer(profil, List.copyOf(HABILITATIONS.keySet())));
    }

    /**
     * Retrouve en base les habilitations nommees.
     *
     * <p>Celles qui manquent sont passees et signalees plutot que creees a la volee : leur
     * absence indique un nom mal orthographie dans la liste ci-dessus, et la creer silencieusement
     * poserait en base une habilitation sans description que personne n'exigera jamais.
     */
    private Set<Authority> resoudre(List<String> noms) {
        Set<Authority> resolues = new LinkedHashSet<>();
        for (String nom : noms) {
            authorityRepository
                .findById(nom)
                .ifPresentOrElse(resolues::add, () ->
                    LOG.warn("Habilitation « {} » introuvable : elle ne sera accordee a aucun profil", nom)
                );
        }
        return resolues;
    }

    /**
     * Reporte sur chaque compte les habilitations de son profil.
     *
     * <p>Un compte ne lit pas ses droits dans son profil : il en porte sa propre copie, posee au
     * moment ou le profil lui a ete attribue. Completer un profil ne suffit donc pas - sans ce
     * report, les comptes en place garderaient les droits d'avant, et se verraient refuser les
     * ecrans au lendemain de la mise en service.
     *
     * <p>Seules les habilitations manquantes sont ajoutees. En retirer celles qui ne figurent
     * plus au profil serait defendable, mais un profil mal renseigne mettrait alors tout un
     * service dehors au redemarrage : on prefere un droit de trop, visible et retirable a la
     * main, a un acces perdu sans explication.
     *
     * <p>Le cache des comptes est vide au passage : il est lu a chaque connexion, et le laisser
     * en place rendrait les anciens droits jusqu'a son expiration.
     */
    private void repercuterSurLesTitulaires() {
        int completes = 0;
        for (Profil profil : profilRepository.findAll()) {
            Set<Authority> droits = profilRepository
                .findOneWithAuthoritiesByNom(profil.getNom())
                .map(Profil::getAuthorities)
                .orElse(Set.of());
            if (droits.isEmpty()) {
                continue;
            }
            for (User titulaire : userRepository.findAllByProfilId(profil.getId())) {
                Set<Authority> manquants = new LinkedHashSet<>(droits);
                manquants.removeAll(titulaire.getAuthorities());
                if (manquants.isEmpty()) {
                    continue;
                }
                titulaire.getAuthorities().addAll(manquants);
                userRepository.save(titulaire);
                purgerCache(titulaire);
                completes++;
            }
        }
        if (completes > 0) {
            LOG.info("Comptes completes des habilitations de leur profil : {}", completes);
        }
    }

    /** Evince le compte des caches, sans quoi sa prochaine connexion rendrait les anciens droits. */
    private void purgerCache(User utilisateur) {
        Cache parLogin = cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE);
        if (parLogin != null) {
            parLogin.evictIfPresent(utilisateur.getLogin());
        }
        Cache parEmail = cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE);
        if (parEmail != null && utilisateur.getEmail() != null) {
            parEmail.evictIfPresent(utilisateur.getEmail());
        }
    }

    /** Assemble plusieurs listes d'habilitations en une seule, sans doublon. */
    @SafeVarargs
    private static List<String> concatener(List<String>... listes) {
        Set<String> assemblees = new LinkedHashSet<>();
        Arrays.stream(listes).forEach(assemblees::addAll);
        return List.copyOf(assemblees);
    }
}
