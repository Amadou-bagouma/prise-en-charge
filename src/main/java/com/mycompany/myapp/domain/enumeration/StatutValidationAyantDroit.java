package com.mycompany.myapp.domain.enumeration;

/**
 * Etat d'un ayant droit dans le circuit d'enregistrement.
 *
 * <p>A ne pas confondre avec {@link StatutAyantDroit}, qui dit si la couverture joue. Celui-ci
 * dit si le rattachement lui-meme a ete verifie : acte de naissance, acte de mariage, piece
 * d'identite. Un rattachement non verifie ne doit pas fonder une prise en charge.
 */
public enum StatutValidationAyantDroit {
    /** Saisi, pas encore verifie. Modifiable et supprimable. */
    EN_SAISIE,
    /** Rattachement verifie. La suppression devient reservee a l'administration. */
    VALIDE,
}
