package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.DemandePriseEnCharge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandePriseEnChargeDTO implements Serializable {

    private Long id;

    @NotNull
    private String reference;

    @NotNull
    private Instant dateCreation;

    private Instant dateModification;

    @NotNull
    private TypeBeneficiaire typeBeneficiaire;

    private String description;

    @NotNull
    private StatutDemande statut;

    private Instant dateAssignation;

    private AgentDTO agent;

    private AyantDroitDTO ayantDroit;

    private TypeSoinDTO typeSoin;

    private EtablissementSanteDTO etablissementSante;

    @NotNull
    private UserDTO gestionnaireCreateur;

    private UserDTO assigneA;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateModification() {
        return dateModification;
    }

    public void setDateModification(Instant dateModification) {
        this.dateModification = dateModification;
    }

    public TypeBeneficiaire getTypeBeneficiaire() {
        return typeBeneficiaire;
    }

    public void setTypeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.typeBeneficiaire = typeBeneficiaire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutDemande getStatut() {
        return statut;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public Instant getDateAssignation() {
        return dateAssignation;
    }

    public void setDateAssignation(Instant dateAssignation) {
        this.dateAssignation = dateAssignation;
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

    public TypeSoinDTO getTypeSoin() {
        return typeSoin;
    }

    public void setTypeSoin(TypeSoinDTO typeSoin) {
        this.typeSoin = typeSoin;
    }

    public EtablissementSanteDTO getEtablissementSante() {
        return etablissementSante;
    }

    public void setEtablissementSante(EtablissementSanteDTO etablissementSante) {
        this.etablissementSante = etablissementSante;
    }

    public UserDTO getGestionnaireCreateur() {
        return gestionnaireCreateur;
    }

    public void setGestionnaireCreateur(UserDTO gestionnaireCreateur) {
        this.gestionnaireCreateur = gestionnaireCreateur;
    }

    public UserDTO getAssigneA() {
        return assigneA;
    }

    public void setAssigneA(UserDTO assigneA) {
        this.assigneA = assigneA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandePriseEnChargeDTO)) {
            return false;
        }

        DemandePriseEnChargeDTO demandePriseEnChargeDTO = (DemandePriseEnChargeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, demandePriseEnChargeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandePriseEnChargeDTO{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateModification='" + getDateModification() + "'" +
            ", typeBeneficiaire='" + getTypeBeneficiaire() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            ", dateAssignation='" + getDateAssignation() + "'" +
            ", agent=" + getAgent() +
            ", ayantDroit=" + getAyantDroit() +
            ", typeSoin=" + getTypeSoin() +
            ", etablissementSante=" + getEtablissementSante() +
            ", gestionnaireCreateur=" + getGestionnaireCreateur() +
            ", assigneA=" + getAssigneA() +
            "}";
    }
}
