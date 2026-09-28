package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.StatutAgent;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Agent.
 */
@Entity
@Table(name = "agent")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Agent extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "matricule", nullable = false, unique = true)
    private String matricule;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "fonction")
    private String fonction;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "region" }, allowSetters = true)
    private Direction direction;

    @ManyToOne(fetch = FetchType.LAZY)
    private Gestion gestion;

    /**
     * Situation au regard de la couverture medicale. Jamais nulle : un dossier sans situation
     * connue laisserait le guichet decider au cas par cas.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutAgent statut = StatutAgent.ACTIF;

    /** Date du dernier changement de situation. */
    @Column(name = "date_statut")
    private Instant dateStatut;

    /**
     * Motif du dernier changement. Exige des que l'on sort de ACTIF : une radiation sans raison
     * ecrite est incontestable au guichet et inexplicable six mois plus tard.
     */
    @Column(name = "motif_statut", length = 500)
    private String motifStatut;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Agent id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricule() {
        return this.matricule;
    }

    public Agent matricule(String matricule) {
        this.setMatricule(matricule);
        return this;
    }

    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    public String getNom() {
        return this.nom;
    }

    public Agent nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Agent prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public Agent dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LocalDate getDateEmbauche() {
        return this.dateEmbauche;
    }

    public Agent dateEmbauche(LocalDate dateEmbauche) {
        this.setDateEmbauche(dateEmbauche);
        return this;
    }

    public void setDateEmbauche(LocalDate dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public Agent telephone(String telephone) {
        this.setTelephone(telephone);
        return this;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFonction() {
        return this.fonction;
    }

    public Agent fonction(String fonction) {
        this.setFonction(fonction);
        return this;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public Agent photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public Agent photoContentType(String photoContentType) {
        this.setPhotoContentType(photoContentType);
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Agent direction(Direction direction) {
        this.setDirection(direction);
        return this;
    }

    public Gestion getGestion() {
        return this.gestion;
    }

    public void setGestion(Gestion gestion) {
        this.gestion = gestion;
    }

    public Agent gestion(Gestion gestion) {
        this.setGestion(gestion);
        return this;
    }

    public StatutAgent getStatut() {
        return this.statut;
    }

    public void setStatut(StatutAgent statut) {
        this.statut = statut;
    }

    public Agent statut(StatutAgent statut) {
        this.setStatut(statut);
        return this;
    }

    public Instant getDateStatut() {
        return this.dateStatut;
    }

    public void setDateStatut(Instant dateStatut) {
        this.dateStatut = dateStatut;
    }

    public Agent dateStatut(Instant dateStatut) {
        this.setDateStatut(dateStatut);
        return this;
    }

    public String getMotifStatut() {
        return this.motifStatut;
    }

    public void setMotifStatut(String motifStatut) {
        this.motifStatut = motifStatut;
    }

    public Agent motifStatut(String motifStatut) {
        this.setMotifStatut(motifStatut);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Agent)) {
            return false;
        }
        return getId() != null && getId().equals(((Agent) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Agent{" +
            "id=" + getId() +
            ", matricule='" + getMatricule() + "'" +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", dateEmbauche='" + getDateEmbauche() + "'" +
            ", telephone='" + getTelephone() + "'" +
            ", fonction='" + getFonction() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
