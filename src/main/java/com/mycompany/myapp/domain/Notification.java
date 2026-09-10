package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.TypeNotification;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Notification implements Serializable {

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

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @Column(name = "date_lecture")
    private Instant dateLecture;

    @NotNull
    @Column(name = "lu", nullable = false)
    private Boolean lu;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TypeNotification type;

    @ManyToOne(optional = false)
    @NotNull
    private User utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(
        value = { "agent", "ayantDroit", "typeSoin", "etablissementSante", "gestionnaireCreateur", "assigneA" },
        allowSetters = true
    )
    private DemandePriseEnCharge demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "demande", "utilisateur", "boiteReception" }, allowSetters = true)
    private Tache tache;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return this.titre;
    }

    public Notification titre(String titre) {
        this.setTitre(titre);
        return this;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getMessage() {
        return this.message;
    }

    public Notification message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public Notification dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateLecture() {
        return this.dateLecture;
    }

    public Notification dateLecture(Instant dateLecture) {
        this.setDateLecture(dateLecture);
        return this;
    }

    public void setDateLecture(Instant dateLecture) {
        this.dateLecture = dateLecture;
    }

    public Boolean getLu() {
        return this.lu;
    }

    public Notification lu(Boolean lu) {
        this.setLu(lu);
        return this;
    }

    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    public TypeNotification getType() {
        return this.type;
    }

    public Notification type(TypeNotification type) {
        this.setType(type);
        return this;
    }

    public void setType(TypeNotification type) {
        this.type = type;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public Notification utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    public DemandePriseEnCharge getDemande() {
        return this.demande;
    }

    public void setDemande(DemandePriseEnCharge demandePriseEnCharge) {
        this.demande = demandePriseEnCharge;
    }

    public Notification demande(DemandePriseEnCharge demandePriseEnCharge) {
        this.setDemande(demandePriseEnCharge);
        return this;
    }

    public Tache getTache() {
        return this.tache;
    }

    public void setTache(Tache tache) {
        this.tache = tache;
    }

    public Notification tache(Tache tache) {
        this.setTache(tache);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return getId() != null && getId().equals(((Notification) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", titre='" + getTitre() + "'" +
            ", message='" + getMessage() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateLecture='" + getDateLecture() + "'" +
            ", lu='" + getLu() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
