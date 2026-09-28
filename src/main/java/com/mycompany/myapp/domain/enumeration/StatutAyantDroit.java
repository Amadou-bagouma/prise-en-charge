package com.mycompany.myapp.domain.enumeration;

/**
 * Situation d'un ayant droit au regard de la couverture medicale.
 *
 * <p>Un ayant droit {@link #ACTIF} n'ouvre droit a une prise en charge que si son agent l'est
 * aussi : sa couverture derive de celle de l'agent, elle ne s'y substitue pas.
 */
public enum StatutAyantDroit {
    /** Couvert, sous reserve que son agent le soit. */
    ACTIF,
    /** Droits suspendus a titre temporaire, le rattachement reste en place. */
    SUSPENDU,
    /** N'ouvre plus droit : enfant ayant depasse la limite d'age, divorce, retrait. */
    RADIE,
    /** Decede. */
    DECEDE,
}
