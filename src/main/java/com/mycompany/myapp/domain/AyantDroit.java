package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.domain.enumeration.StatutAyantDroit;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A AyantDroit.
 */
@Entity
@Table(name = "ayant_droit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AyantDroit extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom", nullable = false)
    private String nom;

    @NotNull
    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "lien", nullable = false)
    private LienParente lien;

    /** Identifiant de l'ayant droit, unique dans le referentiel. */
    @NotNull
    @Size(max = 50)
    @Column(name = "code_ayant_droit", length = 50, unique = true, nullable = false)
    private String codeAyantDroit;

    @Lob
    @Column(name = "photo")
    private byte[] photo;

    @Column(name = "photo_content_type")
    private String photoContentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "direction", "gestion", "user" }, allowSetters = true)
    private Agent agent;

    /**
     * Situation au regard de la couverture medicale. Jamais nulle : un dossier sans situation
     * connue laisserait le guichet decider au cas par cas.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutAyantDroit statut = StatutAyantDroit.ACTIF;

    /** Date du dernier changement de situation. */
    @Column(name = "date_statut")
    private Instant dateStatut;

    /**
     * Motif du dernier changement. Exige des que l'on sort de ACTIF : une radiation sans raison
     * ecrite est incontestable au guichet et inexplicable six mois plus tard.
     */
    @Column(name = "motif_statut", length = 500)
    private String motifStatut;

    /**
     * Le statut propre de l'ayant droit, mis de cote quand celui de l'agent se repercute sur lui.
     *
     * <p>Sans cette memoire, reactiver un agent rendrait actifs des ayants droit qui ne l'etaient
     * pas : un enfant radie pour depassement d'age redeviendrait couvert. Renseigne uniquement
     * pendant une repercussion, et efface des qu'elle est levee - sa presence signifie donc
     * exactement « ce statut vient de l'agent, pas de l'ayant droit ».
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "statut_avant_cascade")
    private StatutAyantDroit statutAvantCascade;

    /**
     * Le motif propre mis de cote en meme temps que le statut.
     *
     * <p>Rendre le statut sans son motif laisserait un enfant radie pour depassement d'age
     * porter, apres reactivation de l'agent, la raison de la radiation de celui-ci : statut
     * juste, explication fausse.
     */
    @Column(name = "motif_avant_cascade", length = 500)
    private String motifAvantCascade;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AyantDroit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeAyantDroit() {
        return this.codeAyantDroit;
    }

    public AyantDroit codeAyantDroit(String codeAyantDroit) {
        this.setCodeAyantDroit(codeAyantDroit);
        return this;
    }

    public void setCodeAyantDroit(String codeAyantDroit) {
        this.codeAyantDroit = codeAyantDroit;
    }

    public String getNom() {
        return this.nom;
    }

    public AyantDroit nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public AyantDroit prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return this.dateNaissance;
    }

    public AyantDroit dateNaissance(LocalDate dateNaissance) {
        this.setDateNaissance(dateNaissance);
        return this;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public LienParente getLien() {
        return this.lien;
    }

    public AyantDroit lien(LienParente lien) {
        this.setLien(lien);
        return this;
    }

    public void setLien(LienParente lien) {
        this.lien = lien;
    }

    public byte[] getPhoto() {
        return this.photo;
    }

    public AyantDroit photo(byte[] photo) {
        this.setPhoto(photo);
        return this;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getPhotoContentType() {
        return this.photoContentType;
    }

    public AyantDroit photoContentType(String photoContentType) {
        this.setPhotoContentType(photoContentType);
        return this;
    }

    public void setPhotoContentType(String photoContentType) {
        this.photoContentType = photoContentType;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public AyantDroit agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public StatutAyantDroit getStatut() {
        return this.statut;
    }

    public void setStatut(StatutAyantDroit statut) {
        this.statut = statut;
    }

    public AyantDroit statut(StatutAyantDroit statut) {
        this.setStatut(statut);
        return this;
    }

    public Instant getDateStatut() {
        return this.dateStatut;
    }

    public void setDateStatut(Instant dateStatut) {
        this.dateStatut = dateStatut;
    }

    public AyantDroit dateStatut(Instant dateStatut) {
        this.setDateStatut(dateStatut);
        return this;
    }

    public String getMotifStatut() {
        return this.motifStatut;
    }

    public void setMotifStatut(String motifStatut) {
        this.motifStatut = motifStatut;
    }

    public AyantDroit motifStatut(String motifStatut) {
        this.setMotifStatut(motifStatut);
        return this;
    }

    public StatutAyantDroit getStatutAvantCascade() {
        return this.statutAvantCascade;
    }

    public void setStatutAvantCascade(StatutAyantDroit statutAvantCascade) {
        this.statutAvantCascade = statutAvantCascade;
    }

    public String getMotifAvantCascade() {
        return this.motifAvantCascade;
    }

    public void setMotifAvantCascade(String motifAvantCascade) {
        this.motifAvantCascade = motifAvantCascade;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AyantDroit)) {
            return false;
        }
        return getId() != null && getId().equals(((AyantDroit) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AyantDroit{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", dateNaissance='" + getDateNaissance() + "'" +
            ", lien='" + getLien() + "'" +
            ", statut='" + getStatut() + "'" +
            "}";
    }
}
