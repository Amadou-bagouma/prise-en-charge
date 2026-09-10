package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.TypeNotification;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationDTO implements Serializable {

    private Long id;

    @NotNull
    private String titre;

    @NotNull
    private String message;

    @NotNull
    private Instant dateCreation;

    private Instant dateLecture;

    @NotNull
    private Boolean lu;

    @NotNull
    private TypeNotification type;

    @NotNull
    private UserDTO utilisateur;

    private DemandePriseEnChargeDTO demande;

    private TacheDTO tache;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(Instant dateLecture) {
        this.dateLecture = dateLecture;
    }

    public Boolean getLu() {
        return lu;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public TypeNotification getType() {
        return type;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }

    public UserDTO getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(UserDTO utilisateur) {
        this.utilisateur = utilisateur;
    }

    public DemandePriseEnChargeDTO getDemande() {
        return demande;
    }

    public void setDemande(DemandePriseEnChargeDTO demande) {
        this.demande = demande;
    }

    public TacheDTO getTache() {
        return tache;
    }

    public void setTache(TacheDTO tache) {
        this.tache = tache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationDTO)) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, notificationDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", message='" + getMessage() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateLecture='" + getDateLecture() + "'" +
            ", lu='" + getLu() + "'" +
            ", type='" + getType() + "'" +
            ", utilisateur=" + getUtilisateur() +
            ", demande=" + getDemande() +
            ", tache=" + getTache() +
            "}";
    }
}
