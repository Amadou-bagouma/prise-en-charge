package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.BoiteReception} entity.
 *
 * <p>La boite appartient a un profil. {@link #nombreNonLus} et {@link #nombreTaches} sont
 * recalcules a la lecture a partir des taches reellement en portee : ils ne sont jamais servis
 * depuis la colonne seule, qui se perimerait des qu'une demande change d'etape.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoiteReceptionDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant dateCreation;

    private Instant dateDerniereLecture;

    @NotNull
    private Integer nombreNonLus;

    @NotNull
    private Boolean actif;

    @NotNull
    private ProfilDTO profil;

    /** Le nombre total de taches adressees a la boite, lues ou non. */
    private Long nombreTaches;

    /** Parmi elles, celles qui restent a traiter : ni terminees, ni annulees. */
    private Long nombreOuvertes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateDerniereLecture() {
        return dateDerniereLecture;
    }

    public void setDateDerniereLecture(Instant dateDerniereLecture) {
        this.dateDerniereLecture = dateDerniereLecture;
    }

    public Integer getNombreNonLus() {
        return nombreNonLus;
    }

    public void setNombreNonLus(Integer nombreNonLus) {
        this.nombreNonLus = nombreNonLus;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public ProfilDTO getProfil() {
        return profil;
    }

    public void setProfil(ProfilDTO profil) {
        this.profil = profil;
    }

    public Long getNombreTaches() {
        return nombreTaches;
    }

    public void setNombreTaches(Long nombreTaches) {
        this.nombreTaches = nombreTaches;
    }

    public Long getNombreOuvertes() {
        return nombreOuvertes;
    }

    public void setNombreOuvertes(Long nombreOuvertes) {
        this.nombreOuvertes = nombreOuvertes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoiteReceptionDTO)) {
            return false;
        }

        BoiteReceptionDTO boiteReceptionDTO = (BoiteReceptionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, boiteReceptionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoiteReceptionDTO{" +
            "id=" + getId() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateDerniereLecture='" + getDateDerniereLecture() + "'" +
            ", nombreNonLus=" + getNombreNonLus() +
            ", actif='" + getActif() + "'" +
            ", nombreTaches=" + getNombreTaches() +
            ", nombreOuvertes=" + getNombreOuvertes() +
            ", profil=" + getProfil() +
            "}";
    }
}
