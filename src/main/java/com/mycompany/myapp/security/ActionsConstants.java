package com.mycompany.myapp.security;

/**
 * Les habilitations fines de l'application : une par action que l'on peut accomplir.
 *
 * <p>{@link AuthoritiesConstants} nomme les habilitations de metier - « validateur DRH »,
 * « verificateur RH » - qui disent un role tenu dans le circuit. Celles-ci disent un geste :
 * creer un agent, imprimer une prise en charge, reinitialiser un mot de passe. Les deux
 * coexistent : le role de metier reste ce qui decide du circuit de validation, l'habilitation
 * fine permet de composer un profil qui ne donne que ce dont quelqu'un a besoin.
 *
 * <p>Le nom suit toujours {@code ROLE_<DOMAINE>_<ACTION>} : sans le prefixe {@code ROLE_},
 * Spring Security ne les reconnait pas comme des autorites attribuables.
 *
 * <p>Ajouter une constante ici ne suffit pas a la rendre effective : elle doit etre decrite dans
 * {@code InitData} pour apparaitre a l'ecran d'administration, puis exigee la ou l'action est
 * servie. Une habilitation declaree et jamais exigee n'interdit rien.
 */
public final class ActionsConstants {

    // ------------------------------------------------------------------ agents
    public static final String AGENT_CONSULTER = "ROLE_AGENT_CONSULTER";
    public static final String AGENT_CREER = "ROLE_AGENT_CREER";
    public static final String AGENT_MODIFIER = "ROLE_AGENT_MODIFIER";
    public static final String AGENT_SUPPRIMER = "ROLE_AGENT_SUPPRIMER";
    public static final String AGENT_CHANGER_STATUT = "ROLE_AGENT_CHANGER_STATUT";
    public static final String AGENT_EXPORTER = "ROLE_AGENT_EXPORTER";

    // ------------------------------------------------------------- ayants droit
    public static final String AYANT_DROIT_CONSULTER = "ROLE_AYANT_DROIT_CONSULTER";
    public static final String AYANT_DROIT_CREER = "ROLE_AYANT_DROIT_CREER";
    public static final String AYANT_DROIT_MODIFIER = "ROLE_AYANT_DROIT_MODIFIER";
    public static final String AYANT_DROIT_SUPPRIMER = "ROLE_AYANT_DROIT_SUPPRIMER";
    public static final String AYANT_DROIT_VALIDER = "ROLE_AYANT_DROIT_VALIDER";
    public static final String AYANT_DROIT_CHANGER_STATUT = "ROLE_AYANT_DROIT_CHANGER_STATUT";
    public static final String AYANT_DROIT_EXPORTER = "ROLE_AYANT_DROIT_EXPORTER";

    // ------------------------------------------------------- cartes beneficiaire
    public static final String CARTE_CONSULTER = "ROLE_CARTE_CONSULTER";
    public static final String CARTE_CREER = "ROLE_CARTE_CREER";
    public static final String CARTE_MODIFIER = "ROLE_CARTE_MODIFIER";
    public static final String CARTE_SUPPRIMER = "ROLE_CARTE_SUPPRIMER";
    public static final String CARTE_IMPRIMER = "ROLE_CARTE_IMPRIMER";

    // ------------------------------------------------------ prises en charge
    public static final String DEMANDE_CONSULTER = "ROLE_DEMANDE_CONSULTER";
    public static final String DEMANDE_CREER = "ROLE_DEMANDE_CREER";
    public static final String DEMANDE_MODIFIER = "ROLE_DEMANDE_MODIFIER";
    public static final String DEMANDE_SUPPRIMER = "ROLE_DEMANDE_SUPPRIMER";
    public static final String DEMANDE_SOUMETTRE = "ROLE_DEMANDE_SOUMETTRE";
    public static final String DEMANDE_VERIFIER = "ROLE_DEMANDE_VERIFIER";
    public static final String DEMANDE_VALIDER_INFIRMERIE = "ROLE_DEMANDE_VALIDER_INFIRMERIE";
    public static final String DEMANDE_VALIDER_DRH = "ROLE_DEMANDE_VALIDER_DRH";
    public static final String DEMANDE_RETOURNER = "ROLE_DEMANDE_RETOURNER";
    public static final String DEMANDE_REJETER = "ROLE_DEMANDE_REJETER";
    public static final String DEMANDE_RESOUMETTRE = "ROLE_DEMANDE_RESOUMETTRE";
    public static final String DEMANDE_ANNULER = "ROLE_DEMANDE_ANNULER";
    public static final String DEMANDE_IMPRIMER = "ROLE_DEMANDE_IMPRIMER";
    public static final String DEMANDE_NOTIFIER = "ROLE_DEMANDE_NOTIFIER";
    public static final String DEMANDE_EXPORTER = "ROLE_DEMANDE_EXPORTER";

    // --------------------------------------------------- pieces justificatives
    public static final String PIECE_CONSULTER = "ROLE_PIECE_CONSULTER";
    public static final String PIECE_AJOUTER = "ROLE_PIECE_AJOUTER";
    public static final String PIECE_MODIFIER = "ROLE_PIECE_MODIFIER";
    public static final String PIECE_SUPPRIMER = "ROLE_PIECE_SUPPRIMER";

    // ------------------------------------------------------------------ taches
    public static final String TACHE_CONSULTER = "ROLE_TACHE_CONSULTER";
    public static final String TACHE_PRENDRE_EN_CHARGE = "ROLE_TACHE_PRENDRE_EN_CHARGE";
    public static final String TACHE_RELACHER = "ROLE_TACHE_RELACHER";
    public static final String TACHE_CLOTURER = "ROLE_TACHE_CLOTURER";

    // ------------------------------------------------- boite, avis, historique
    public static final String BOITE_CONSULTER = "ROLE_BOITE_CONSULTER";
    public static final String NOTIFICATION_CONSULTER = "ROLE_NOTIFICATION_CONSULTER";
    public static final String NOTIFICATION_MARQUER_LUE = "ROLE_NOTIFICATION_MARQUER_LUE";
    public static final String HISTORIQUE_CONSULTER = "ROLE_HISTORIQUE_CONSULTER";

    // ------------------------------------------------------------ referentiels
    public static final String REFERENTIEL_CONSULTER = "ROLE_REFERENTIEL_CONSULTER";
    public static final String REFERENTIEL_CREER = "ROLE_REFERENTIEL_CREER";
    public static final String REFERENTIEL_MODIFIER = "ROLE_REFERENTIEL_MODIFIER";
    public static final String REFERENTIEL_SUPPRIMER = "ROLE_REFERENTIEL_SUPPRIMER";

    // ---------------------------------------------------------- administration
    public static final String UTILISATEUR_CONSULTER = "ROLE_UTILISATEUR_CONSULTER";
    public static final String UTILISATEUR_CREER = "ROLE_UTILISATEUR_CREER";
    public static final String UTILISATEUR_MODIFIER = "ROLE_UTILISATEUR_MODIFIER";
    public static final String UTILISATEUR_SUPPRIMER = "ROLE_UTILISATEUR_SUPPRIMER";
    public static final String UTILISATEUR_REINITIALISER_MOT_DE_PASSE = "ROLE_UTILISATEUR_REINITIALISER_MOT_DE_PASSE";
    public static final String UTILISATEUR_ACTIVER = "ROLE_UTILISATEUR_ACTIVER";

    public static final String PROFIL_CONSULTER = "ROLE_PROFIL_CONSULTER";
    public static final String PROFIL_CREER = "ROLE_PROFIL_CREER";
    public static final String PROFIL_MODIFIER = "ROLE_PROFIL_MODIFIER";
    public static final String PROFIL_SUPPRIMER = "ROLE_PROFIL_SUPPRIMER";

    public static final String HABILITATION_CONSULTER = "ROLE_HABILITATION_CONSULTER";
    public static final String HABILITATION_MODIFIER = "ROLE_HABILITATION_MODIFIER";

    private ActionsConstants() {}
}
