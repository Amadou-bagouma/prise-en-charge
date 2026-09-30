package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeParametre;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * Un reglage de l'application, tel qu'il circule entre le serveur et l'ecran.
 */
public class ParametreDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 100)
    private String code;

    @NotNull
    @Size(max = 200)
    private String libelle;

    @Size(max = 1000)
    private String description;

    @Size(max = 2000)
    private String valeur;

    @NotNull
    private TypeParametre type;

    private byte[] valeurBinaire;

    private String valeurBinaireContentType;

    /**
     * Vrai pour un reglage pose par l'application.
     *
     * <p>Rendu a l'ecran pour qu'il retire la suppression : le code interroge ces reglages, et
     * leur absence le ferait retomber sur une valeur que personne n'a choisie.
     */
    private boolean socle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public TypeParametre getType() {
        return type;
    }

    public void setType(TypeParametre type) {
        this.type = type;
    }

    public byte[] getValeurBinaire() {
        return valeurBinaire;
    }

    public void setValeurBinaire(byte[] valeurBinaire) {
        this.valeurBinaire = valeurBinaire;
    }

    public String getValeurBinaireContentType() {
        return valeurBinaireContentType;
    }

    public void setValeurBinaireContentType(String valeurBinaireContentType) {
        this.valeurBinaireContentType = valeurBinaireContentType;
    }

    public boolean isSocle() {
        return socle;
    }

    public void setSocle(boolean socle) {
        this.socle = socle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParametreDTO autre)) {
            return false;
        }
        return this.id != null && Objects.equals(this.id, autre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ParametreDTO{id=" + id + ", code='" + code + "', type=" + type + "}";
    }
}
