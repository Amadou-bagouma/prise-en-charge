package com.mycompany.myapp.domain.enumeration;

/**
 * The StatutDemande enumeration.
 */
public enum StatutDemande {
    /**
     * En cours de saisie par le gestionnaire. Le dossier n'est encore soumis a personne : il
     * n'apparait dans aucune boite de reception, et c'est le seul etat ou il peut etre supprime.
     */
    EN_SAISIE,
    /**
     * Soumis au controle de la direction des ressources humaines : completude du dossier et
     * conformite des pieces, avant que la decision soit demandee.
     */
    EN_VERIFICATION_RH,
    NOUVELLE,
    EN_ATTENTE_PIECES,
    A_TRAITER,
    EN_COURS_TRAITEMENT,
    EN_ATTENTE_AVIS_MEDICAL,
    EN_ATTENTE_DECISION,
    /** 1ere etape du workflow : en attente de validation par un utilisateur de la direction DRH. */
    EN_ATTENTE_VALIDATION_DRH,
    /** 2eme etape du workflow : en attente de validation par un utilisateur de l'infirmerie du personnel. */
    EN_ATTENTE_VALIDATION_INFIRMERIE,
    /** La demande a ete rejetee par un validateur et renvoyee au demandeur pour correction. */
    RETOURNEE,
    VALIDEE,
    REJETEE,
    ANNULEE,
    CLOTUREE,
}
