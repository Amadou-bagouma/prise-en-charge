package com.mycompany.myapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.HistoriqueAction} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueActionDTO implements Serializable {

    private Long id;

    @NotNull
    private String action;

    private String description;

    @NotNull
    private Instant dateAction;

    @NotNull
    private DemandePriseEnChargeDTO demande;

    @NotNull
    private UserDTO utilisateur;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateAction() {
        return dateAction;
    }

    public void setDateAction(Instant dateAction) {
        this.dateAction = dateAction;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoriqueActionDTO)) {
            return false;
        }

        HistoriqueActionDTO historiqueActionDTO = (HistoriqueActionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, historiqueActionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueActionDTO{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateAction='" + getDateAction() + "'" +
            ", demande=" + getDemande() +
            ", utilisateur=" + getUtilisateur() +
            "}";
    }
}
