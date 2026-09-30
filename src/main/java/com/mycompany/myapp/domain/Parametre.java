package com.mycompany.myapp.domain;

import com.mycompany.myapp.domain.enumeration.TypeParametre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Un reglage de l'application, modifiable sans passer par le code.
 *
 * <p>La duree de validite d'une prise en charge, celle d'une carte, le nom du directeur general
 * et sa signature vivaient jusqu'ici dans des constantes Java. Les changer supposait une
 * livraison : quatorze jours devenaient un chiffre gele, alors que c'est une decision de
 * service, revisable.
 *
 * <p>Chaque reglage porte un code stable - c'est lui que le code interroge - et un libelle
 * destine a qui l'ajuste. Le type dit comment lire la valeur : un entier mal saisi se refuse a
 * l'enregistrement plutot que de casser un calcul trois semaines plus tard.
 *
 * <p>La valeur est stockee en texte, quel que soit le type. Une colonne par type multiplierait
 * les colonnes vides, et un reglage change parfois de nature - un delai en jours devient un
 * delai en mois - sans qu'il faille migrer la table.
 */
@Entity
@Table(name = "parametre")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Parametre implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    /**
     * Le code interroge par le code applicatif : stable, en majuscules, jamais traduit.
     *
     * <p>C'est la cle du reglage. Le renommer romprait le lien avec le code qui le lit, et
     * celui-ci retomberait silencieusement sur sa valeur par defaut.
     */
    @NotNull
    @Size(max = 100)
    @Column(name = "code", length = 100, nullable = false, unique = true)
    private String code;

    /** Ce que le reglage regle, en francais : c'est ce que lit celui qui l'ajuste. */
    @NotNull
    @Size(max = 200)
    @Column(name = "libelle", length = 200, nullable = false)
    private String libelle;

    /** Ce que la valeur change concretement, et dans quelles limites elle a du sens. */
    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * La valeur, en texte quel que soit le type.
     *
     * <p>Vide pour un reglage de type image : celle-ci vit dans {@link #valeurBinaire}.
     */
    @Column(name = "valeur", length = 2000)
    private String valeur;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeParametre type;

    /** L'image, pour les reglages qui en portent une - la signature du directeur general. */
    @Lob
    @Column(name = "valeur_binaire")
    private byte[] valeurBinaire;

    @Column(name = "valeur_binaire_content_type")
    private String valeurBinaireContentType;

    /**
     * Vrai pour un reglage pose par l'application au demarrage.
     *
     * <p>Un reglage du socle ne se supprime pas : le code l'interroge, et son absence le ferait
     * retomber sur une valeur par defaut que personne n'a choisie. Sa valeur, elle, se modifie.
     */
    @NotNull
    @Column(name = "socle", nullable = false)
    private boolean socle = false;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeur() {
        return this.valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public TypeParametre getType() {
        return this.type;
    }

    public void setType(TypeParametre type) {
        this.type = type;
    }

    public byte[] getValeurBinaire() {
        return this.valeurBinaire;
    }

    public void setValeurBinaire(byte[] valeurBinaire) {
        this.valeurBinaire = valeurBinaire;
    }

    public String getValeurBinaireContentType() {
        return this.valeurBinaireContentType;
    }

    public void setValeurBinaireContentType(String valeurBinaireContentType) {
        this.valeurBinaireContentType = valeurBinaireContentType;
    }

    public boolean isSocle() {
        return this.socle;
    }

    public void setSocle(boolean socle) {
        this.socle = socle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Parametre autre)) {
            return false;
        }
        return getId() != null && getId().equals(autre.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Parametre{id=" + getId() + ", code='" + getCode() + "', type='" + getType() + "'}";
    }
}
