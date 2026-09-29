package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.StatutAgent;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Agent} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 4, max = 4)
    @Pattern(regexp = "^[A-Z0-9]{4}$", message = "Le matricule doit faire 4 caracteres, en majuscules ou chiffres.")
    private String matricule;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    private LocalDate dateNaissance;

    private LocalDate dateEmbauche;

    private String telephone;

    private String fonction;

    private byte[] photo;

    private String photoContentType;

    private DirectionDTO direction;

    private GestionDTO gestion;

    /** Situation au regard de la couverture medicale. Toujours renseignee. */
    private StatutAgent statut;

    private Instant dateStatut;

    @Size(max = 500)
    private String motifStatut;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return matricule;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFonction() {
        return fonction;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return photoContentType;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public DirectionDTO getDirection() {
        return direction;
    }

    public void setDirection(DirectionDTO direction) {
        this.direction = direction;
    }

    public GestionDTO getGestion() {
        return gestion;
    }

    public void setGestion(GestionDTO gestion) {
        this.gestion = gestion;
    }

    public StatutAgent getStatut() {
        return statut;
    }

    public void setStatut(StatutAgent statut) {
        this.statut = statut;
    }

    public Instant getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(Instant dateStatut) {
        this.dateStatut = dateStatut;
    }

    public String getMotifStatut() {
        return motifStatut;
    }

    public void setMotifStatut(String motifStatut) {
        this.motifStatut = motifStatut;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AgentDTO)) {
            return false;
        }

        AgentDTO agentDTO = (AgentDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, agentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentDTO{" +
            "id=" + getId() +
            ", matricule='" + getMatricule() + "'" +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", dateEmbauche='" + getDateEmbauche() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", fonction='" + getFonction() + "'" +
            ", direction=" + getDirection() +
            ", gestion=" + getGestion() +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
