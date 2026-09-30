/**
 * Les habilitations fines de l'application : une par action que l'on peut accomplir.
 *
 * Miroir exact de `ActionsConstants` côté serveur. Les deux listes doivent rester identiques :
 * un nom qui diverge ne provoque aucune erreur — le bouton disparaît simplement pour tout le
 * monde, ou reste offert pour se faire refuser à l'envoi.
 *
 * L'écran s'en sert pour ne pas proposer ce qui sera refusé. Il ne protège rien : c'est le
 * serveur qui décide, et lui seul. Retirer un bouton n'empêche personne d'appeler le point
 * d'entrée directement.
 */
export enum Action {
  // ------------------------------------------------------------------ agents
  AGENT_CONSULTER = 'ROLE_AGENT_CONSULTER',
  AGENT_CREER = 'ROLE_AGENT_CREER',
  AGENT_MODIFIER = 'ROLE_AGENT_MODIFIER',
  AGENT_SUPPRIMER = 'ROLE_AGENT_SUPPRIMER',
  AGENT_CHANGER_STATUT = 'ROLE_AGENT_CHANGER_STATUT',
  AGENT_EXPORTER = 'ROLE_AGENT_EXPORTER',

  // ------------------------------------------------------------ ayants droit
  AYANT_DROIT_CONSULTER = 'ROLE_AYANT_DROIT_CONSULTER',
  AYANT_DROIT_CREER = 'ROLE_AYANT_DROIT_CREER',
  AYANT_DROIT_MODIFIER = 'ROLE_AYANT_DROIT_MODIFIER',
  AYANT_DROIT_SUPPRIMER = 'ROLE_AYANT_DROIT_SUPPRIMER',
  AYANT_DROIT_VALIDER = 'ROLE_AYANT_DROIT_VALIDER',
  AYANT_DROIT_CHANGER_STATUT = 'ROLE_AYANT_DROIT_CHANGER_STATUT',
  AYANT_DROIT_EXPORTER = 'ROLE_AYANT_DROIT_EXPORTER',

  // ----------------------------------------------------- cartes beneficiaire
  CARTE_CONSULTER = 'ROLE_CARTE_CONSULTER',
  CARTE_CREER = 'ROLE_CARTE_CREER',
  CARTE_MODIFIER = 'ROLE_CARTE_MODIFIER',
  CARTE_SUPPRIMER = 'ROLE_CARTE_SUPPRIMER',
  CARTE_IMPRIMER = 'ROLE_CARTE_IMPRIMER',

  // -------------------------------------------------------- prises en charge
  DEMANDE_CONSULTER = 'ROLE_DEMANDE_CONSULTER',
  DEMANDE_CREER = 'ROLE_DEMANDE_CREER',
  DEMANDE_MODIFIER = 'ROLE_DEMANDE_MODIFIER',
  DEMANDE_SUPPRIMER = 'ROLE_DEMANDE_SUPPRIMER',
  DEMANDE_SOUMETTRE = 'ROLE_DEMANDE_SOUMETTRE',
  DEMANDE_VERIFIER = 'ROLE_DEMANDE_VERIFIER',
  DEMANDE_VALIDER_INFIRMERIE = 'ROLE_DEMANDE_VALIDER_INFIRMERIE',
  DEMANDE_VALIDER_DRH = 'ROLE_DEMANDE_VALIDER_DRH',
  DEMANDE_RETOURNER = 'ROLE_DEMANDE_RETOURNER',
  DEMANDE_REJETER = 'ROLE_DEMANDE_REJETER',
  DEMANDE_RESOUMETTRE = 'ROLE_DEMANDE_RESOUMETTRE',
  DEMANDE_ANNULER = 'ROLE_DEMANDE_ANNULER',
  DEMANDE_IMPRIMER = 'ROLE_DEMANDE_IMPRIMER',
  DEMANDE_NOTIFIER = 'ROLE_DEMANDE_NOTIFIER',
  DEMANDE_EXPORTER = 'ROLE_DEMANDE_EXPORTER',

  // ----------------------------------------------------- pieces justificatives
  PIECE_CONSULTER = 'ROLE_PIECE_CONSULTER',
  PIECE_AJOUTER = 'ROLE_PIECE_AJOUTER',
  PIECE_MODIFIER = 'ROLE_PIECE_MODIFIER',
  PIECE_SUPPRIMER = 'ROLE_PIECE_SUPPRIMER',

  // ------------------------------------------------------------------ taches
  TACHE_CONSULTER = 'ROLE_TACHE_CONSULTER',
  TACHE_PRENDRE_EN_CHARGE = 'ROLE_TACHE_PRENDRE_EN_CHARGE',
  TACHE_RELACHER = 'ROLE_TACHE_RELACHER',
  TACHE_CLOTURER = 'ROLE_TACHE_CLOTURER',

  // --------------------------------------------------- boite, avis, historique
  BOITE_CONSULTER = 'ROLE_BOITE_CONSULTER',
  NOTIFICATION_CONSULTER = 'ROLE_NOTIFICATION_CONSULTER',
  NOTIFICATION_MARQUER_LUE = 'ROLE_NOTIFICATION_MARQUER_LUE',
  HISTORIQUE_CONSULTER = 'ROLE_HISTORIQUE_CONSULTER',

  // ------------------------------------------------------------- referentiels
  REFERENTIEL_CONSULTER = 'ROLE_REFERENTIEL_CONSULTER',
  REFERENTIEL_CREER = 'ROLE_REFERENTIEL_CREER',
  REFERENTIEL_MODIFIER = 'ROLE_REFERENTIEL_MODIFIER',
  REFERENTIEL_SUPPRIMER = 'ROLE_REFERENTIEL_SUPPRIMER',

  // ----------------------------------------------------------- administration
  UTILISATEUR_CONSULTER = 'ROLE_UTILISATEUR_CONSULTER',
  UTILISATEUR_CREER = 'ROLE_UTILISATEUR_CREER',
  UTILISATEUR_MODIFIER = 'ROLE_UTILISATEUR_MODIFIER',
  UTILISATEUR_SUPPRIMER = 'ROLE_UTILISATEUR_SUPPRIMER',
  UTILISATEUR_REINITIALISER_MOT_DE_PASSE = 'ROLE_UTILISATEUR_REINITIALISER_MOT_DE_PASSE',
  UTILISATEUR_ACTIVER = 'ROLE_UTILISATEUR_ACTIVER',

  PROFIL_CONSULTER = 'ROLE_PROFIL_CONSULTER',
  PROFIL_CREER = 'ROLE_PROFIL_CREER',
  PROFIL_MODIFIER = 'ROLE_PROFIL_MODIFIER',
  PROFIL_SUPPRIMER = 'ROLE_PROFIL_SUPPRIMER',

  HABILITATION_CONSULTER = 'ROLE_HABILITATION_CONSULTER',
  HABILITATION_MODIFIER = 'ROLE_HABILITATION_MODIFIER',

  // ------------------------------------------------------------- espace agent
  /**
   * Accès à son propre espace : ses dossiers, ses ayants droit, sa carte.
   *
   * Portée seule, elle borne ce que l'agent voit à ce qui le concerne. Le bornage est fait au
   * serveur : l'écran s'en sert seulement pour proposer l'espace, jamais pour le protéger.
   */
  ESPACE_AGENT = 'ROLE_ESPACE_AGENT',
}
