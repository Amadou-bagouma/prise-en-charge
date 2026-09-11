package com.mycompany.myapp.domain.enumeration;

/**
 * The StatutDemande enumeration.
 */
public enum StatutDemande {
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
