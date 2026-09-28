package com.mycompany.myapp.service.dto;

import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.domain.enumeration.StatutAyantDroit;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.AyantDroit} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AyantDroitDTO implements Serializable {

    private Long id;

    /**
     * Attribue par le serveur a la creation quand il est absent (voir GenerateurCodeAyantDroit).
     * Non obligatoire dans la charge utile, pour que le client n'ait pas a le calculer.
     */
    @Size(max = 50)
    private String codeAyantDroit;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    private LocalDate dateNaissance;

    @NotNull
    private LienParente lien;

    private byte[] photo;

    private String photoContentType;

    private AgentDTO agent;

    /** Situation au regard de la couverture medicale. Toujours renseignee. */
    @NotNull
    private StatutAyantDroit statut;

    private Instant dateStatut;

    @Size(max = 500)
    private String motifStatut;

    /**
     * Non nul quand le statut courant vient d'une repercussion du statut de l'agent : l'ecran
     * peut alors le dire, au lieu de laisser croire a une decision prise sur l'ayant droit.
     */
    private StatutAyantDroit statutAvantCascade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeAyantDroit() {
        return codeAyantDroit;
    }

    public void setCodeAyantDroit(String codeAyantDroit) {
        this.codeAyantDroit = codeAyantDroit;
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

    public LienParente getLien() {
        return lien;
    }

    public void setLien(LienParente lien) {
        this.lien = lien;
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

    public AgentDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentDTO agent) {
        this.agent = agent;
    }

    public StatutAyantDroit getStatut() {
        return statut;
    }

    public void setStatut(StatutAyantDroit statut) {
        this.statut = statut;
    }

    public Instant getDateStatut() {
        return dateStatut;
    }

    public void setDateStatut(Instant dateStatut) {
        this.dateStatut = dateStatut;
    }

    public StatutAyantDroit getStatutAvantCascade() {
        return statutAvantCascade;
    }

    public void setStatutAvantCascade(StatutAyantDroit statutAvantCascade) {
        this.statutAvantCascade = statutAvantCascade;
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
        if (!(o instanceof AyantDroitDTO)) {
            return false;
        }

        AyantDroitDTO ayantDroitDTO = (AyantDroitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ayantDroitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AyantDroitDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", lien='" + getLien() + "'" +
            ", agent=" + getAgent() +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
