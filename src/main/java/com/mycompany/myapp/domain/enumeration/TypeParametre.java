package com.mycompany.myapp.domain.enumeration;

/**
 * Comment lire la valeur d'un parametre.
 *
 * <p>La valeur est toujours stockee en texte : le type dit comment l'interpreter, et surtout ce
 * qui se refuse a l'enregistrement. Un delai saisi « quinze » plutot que « 15 » doit etre refuse
 * au moment ou on l'ecrit, et non trois semaines plus tard, au milieu d'un calcul d'echeance.
 */
public enum TypeParametre {
    /** Un nombre entier : un delai, un nombre d'annees, une taille. */
    ENTIER,
    /** Une ligne de texte : un nom, un intitule. */
    TEXTE,
    /** Oui ou non. */
    BOOLEEN,
    /** Une image : un logo, une signature. La valeur textuelle reste vide. */
    IMAGE,
}
