package com.mycompany.myapp.domain.enumeration;

/**
 * Le lien entre l'agent et son ayant droit.
 *
 * <p>Un enfant adopte suit les memes regles qu'un enfant : meme serie de code, meme serie de carte.
 */
public enum LienParente {
    CONJOINT,
    ENFANT,
    ADOPTE,
    AUTRE,
}
