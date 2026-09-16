package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.PrioriteDemande;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.domain.enumeration.TypeSoin;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.DemandePriseEnCharge} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandePriseEnChargeDTO implements Serializable {

    private Long id;

    // Generated server-side (see DemandePriseEnChargeServiceImpl); never required from the client.
    private String reference;

    @NotNull
    private Instant dateCreation;

    private Instant dateModification;

    @NotNull
    private TypeBeneficiaire typeBeneficiaire;

    private String description;

    @NotNull
    private StatutDemande statut;

    @NotNull
    private PrioriteDemande priorite;

    private Instant dateAssignation;

    private Instant dateEcheance;

    private String motifRejet;

    private String observation;

    private AgentDTO agent;

    private AyantDroitDTO ayantDroit;

    @NotEmpty
    private Set<TypeSoin> typeSoins = new HashSet<>();

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

    public PrioriteDemande getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteDemande priorite) {
        this.priorite = priorite;
    }

    public Instant getDateAssignation() {
        return dateAssignation;
    }

    public void setDateAssignation(Instant dateAssignation) {
        this.dateAssignation = dateAssignation;
    }

    public Instant getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(Instant dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getMotifRejet() {
        return motifRejet;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
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

    public Set<TypeSoin> getTypeSoins() {
        return typeSoins;
    }

    public void setTypeSoins(Set<TypeSoin> typeSoins) {
        this.typeSoins = typeSoins;
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
            ", priorite='" + getPriorite() + "'" +
            ", dateAssignation='" + getDateAssignation() + "'" +
            ", dateEcheance='" + getDateEcheance() + "'" +
            ", motifRejet='" + getMotifRejet() + "'" +
            ", observation='" + getObservation() + "'" +
            ", agent=" + getAgent() +
            ", ayantDroit=" + getAyantDroit() +
            ", typeSoins=" + getTypeSoins() +
            ", etablissementSante=" + getEtablissementSante() +
            ", gestionnaireCreateur=" + getGestionnaireCreateur() +
            ", assigneA=" + getAssigneA() +
            "}";
    }
}
