package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Tache.
 */
@Entity
@Table(name = "tache")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Tache implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @Column(name = "date_assignation")
    private Instant dateAssignation;

    @Column(name = "date_echeance")
    private Instant dateEcheance;

    @Column(name = "date_terminaison")
    private Instant dateTerminaison;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutTache statut;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false)
    private PrioriteTache priorite;

    @NotNull
    @Column(name = "lu", nullable = false)
    private Boolean lu;

    @Column(name = "commentaire")
    private String commentaire;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "agent", "ayantDroit", "typeSoin", "etablissementSante", "gestionnaireCreateur", "assigneA" },
        allowSetters = true
    )
    private DemandePriseEnCharge demande;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "utilisateur" }, allowSetters = true)
    private BoiteReception boiteReception;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Tache id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Tache titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return this.description;
    }

    public Tache description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Tache dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateAssignation() {
        return this.dateAssignation;
    }

    public Tache dateAssignation(Instant dateAssignation) {
        this.setDateAssignation(dateAssignation);
        return this;
    }

    public void setDateAssignation(Instant dateAssignation) {
        this.dateAssignation = dateAssignation;
    }

    public Instant getDateEcheance() {
        return this.dateEcheance;
    }

    public Tache dateEcheance(Instant dateEcheance) {
        this.setDateEcheance(dateEcheance);
        return this;
    }

    public void setDateEcheance(Instant dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public Instant getDateTerminaison() {
        return this.dateTerminaison;
    }

    public Tache dateTerminaison(Instant dateTerminaison) {
        this.setDateTerminaison(dateTerminaison);
        return this;
    }

    public void setDateTerminaison(Instant dateTerminaison) {
        this.dateTerminaison = dateTerminaison;
    }

    public StatutTache getStatut() {
        return this.statut;
    }

    public Tache statut(StatutTache statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutTache statut) {
        this.statut = statut;
    }

    public PrioriteTache getPriorite() {
        return this.priorite;
    }

    public Tache priorite(PrioriteTache priorite) {
        this.setPriorite(priorite);
        return this;
    }

    public void setPriorite(PrioriteTache priorite) {
        this.priorite = priorite;
    }

    public Boolean getLu() {
        return this.lu;
    }

    public Tache lu(Boolean lu) {
        this.setLu(lu);
        return this;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public String getCommentaire() {
        return this.commentaire;
    }

    public Tache commentaire(String commentaire) {
        this.setCommentaire(commentaire);
        return this;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public DemandePriseEnCharge getDemande() {
        return this.demande;
    }

    public void setDemande(DemandePriseEnCharge demandePriseEnCharge) {
        this.demande = demandePriseEnCharge;
    }

    public Tache demande(DemandePriseEnCharge demandePriseEnCharge) {
        this.setDemande(demandePriseEnCharge);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public Tache utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    public BoiteReception getBoiteReception() {
        return this.boiteReception;
    }

    public void setBoiteReception(BoiteReception boiteReception) {
        this.boiteReception = boiteReception;
    }

    public Tache boiteReception(BoiteReception boiteReception) {
        this.setBoiteReception(boiteReception);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tache)) {
            return false;
        }
        return getId() != null && getId().equals(((Tache) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Tache{" +
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
            "}";
    }
}
