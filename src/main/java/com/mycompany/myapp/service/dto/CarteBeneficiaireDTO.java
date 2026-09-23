package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.CarteBeneficiaire} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarteBeneficiaireDTO implements Serializable {

    private Long id;

    @NotNull
    private String numeroCarte;

    @NotNull
    private TypeBeneficiaire typeBeneficiaire;

    @NotNull
    private LocalDate dateDebutValidite;

    @NotNull
    private LocalDate dateFinValidite;

    @NotNull
    private LocalDate dateEmission;

    private AgentDTO agent;

    private AyantDroitDTO ayantDroit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCarte() {
        return numeroCarte;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public TypeBeneficiaire getTypeBeneficiaire() {
        return typeBeneficiaire;
    }

    public void setTypeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.typeBeneficiaire = typeBeneficiaire;
    }

    public LocalDate getDateDebutValidite() {
        return dateDebutValidite;
    }

    public void setDateDebutValidite(LocalDate dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public LocalDate getDateFinValidite() {
        return dateFinValidite;
    }

    public void setDateFinValidite(LocalDate dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }

    public LocalDate getDateEmission() {
        return dateEmission;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public AyantDroitDTO getAyantDroit() {
        return ayantDroit;
    }

    public void setAyantDroit(AyantDroitDTO ayantDroit) {
        this.ayantDroit = ayantDroit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarteBeneficiaireDTO)) {
            return false;
        }

        CarteBeneficiaireDTO carteBeneficiaireDTO = (CarteBeneficiaireDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carteBeneficiaireDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarteBeneficiaireDTO{" +
            "id=" + getId() +
            ", numeroCarte='" + getNumeroCarte() + "'" +
            ", typeBeneficiaire='" + getTypeBeneficiaire() + "'" +
            ", dateDebutValidite='" + getDateDebutValidite() + "'" +
            ", dateFinValidite='" + getDateFinValidite() + "'" +
            ", dateEmission='" + getDateEmission() + "'" +
            ", agent=" + getAgent() +
            ", ayantDroit=" + getAyantDroit() +
            "}";
    }
}
