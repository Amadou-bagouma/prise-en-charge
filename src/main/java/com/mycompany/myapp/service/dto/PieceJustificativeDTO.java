package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.PieceJustificative} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PieceJustificativeDTO implements Serializable {

    private Long id;

    @NotNull
    private String nomFichier;

    @NotNull
    private String cheminFichier;

    @NotNull
    private Instant dateAjout;

    @NotNull
    private DemandePriseEnChargeDTO demande;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public Instant getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Instant dateAjout) {
        this.dateAjout = dateAjout;
    }

    public DemandePriseEnChargeDTO getDemande() {
        return demande;
    }

    public void setDemande(DemandePriseEnChargeDTO demande) {
        this.demande = demande;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PieceJustificativeDTO)) {
            return false;
        }

        PieceJustificativeDTO pieceJustificativeDTO = (PieceJustificativeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pieceJustificativeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PieceJustificativeDTO{" +
            "id=" + getId() +
            ", nomFichier='" + getNomFichier() + "'" +
            ", cheminFichier='" + getCheminFichier() + "'" +
            ", dateAjout='" + getDateAjout() + "'" +
            ", demande=" + getDemande() +
            "}";
    }
}
