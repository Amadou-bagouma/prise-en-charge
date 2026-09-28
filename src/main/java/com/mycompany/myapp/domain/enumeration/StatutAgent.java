package com.mycompany.myapp.domain.enumeration;

/**
 * Situation d'un agent au regard de la couverture medicale.
 *
 * <p>Seul un agent {@link #ACTIF} ouvre droit a une prise en charge, pour lui comme pour ses
 * ayants droit : leur couverture derive de la sienne.
 */
public enum StatutAgent {
    /** En activite : ouvre droit a la prise en charge. */
    ACTIF,
    /** Droits suspendus a titre temporaire, le dossier reste ouvert. */
    SUSPENDU,
    /** Parti a la retraite : la couverture releve d'un autre regime. */
    RETRAITE,
    /** Radie des effectifs. */
    RADIE,
    /** Decede. */
    DECEDE,
}
