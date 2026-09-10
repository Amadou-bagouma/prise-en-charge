package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Tache} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TacheDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    private String description;

    @NotNull
    private Instant dateCreation;

    private Instant dateAssignation;

    private Instant dateEcheance;

    private Instant dateTerminaison;

    @NotNull
    private StatutTache statut;

    @NotNull
    private PrioriteTache priorite;

    @NotNull
    private Boolean lu;

    private String commentaire;

    @NotNull
    private DemandePriseEnChargeDTO demande;

    @NotNull
    private UserDTO utilisateur;

    private BoiteReceptionDTO boiteReception;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
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

    public Instant getDateTerminaison() {
        return dateTerminaison;
    }

    public void setDateTerminaison(Instant dateTerminaison) {
        this.dateTerminaison = dateTerminaison;
    }

    public StatutTache getStatut() {
        return statut;
    }

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }

    public PrioriteTache getPriorite() {
        return priorite;
    }

    public void setPriorite(PrioriteTache priorite) {
        this.priorite = priorite;
    }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public DemandePriseEnChargeDTO getDemande() {
        return demande;
    }

    public void setDemande(DemandePriseEnChargeDTO demande) {
        this.demande = demande;
    }

    public UserDTO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserDTO utilisateur) {
        this.utilisateur = utilisateur;
    }

    public BoiteReceptionDTO getBoiteReception() {
        return boiteReception;
    }

    public void setBoiteReception(BoiteReceptionDTO boiteReception) {
        this.boiteReception = boiteReception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TacheDTO)) {
            return false;
        }

        TacheDTO tacheDTO = (TacheDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tacheDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TacheDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateAssignation='" + getDateAssignation() + "'" +
            ", dateEcheance='" + getDateEcheance() + "'" +
            ", dateTerminaison='" + getDateTerminaison() + "'" +
            ", statut='" + getStatut() + "'" +
            ", priorite='" + getPriorite() + "'" +
            ", lu='" + getLu() + "'" +
            ", commentaire='" + getCommentaire() + "'" +
            ", demande=" + getDemande() +
            ", utilisateur=" + getUtilisateur() +
            ", boiteReception=" + getBoiteReception() +
            "}";
    }
}
