package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A HistoriqueAction.
 */
@Entity
@Table(name = "historique_action")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueAction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "date_action", nullable = false)
    private Instant dateAction;

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

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public HistoriqueAction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return this.action;
    }

    public HistoriqueAction action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return this.description;
    }

    public HistoriqueAction description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateAction() {
        return this.dateAction;
    }

    public HistoriqueAction dateAction(Instant dateAction) {
        this.setDateAction(dateAction);
        return this;
    }

    public void setDateAction(Instant dateAction) {
        this.dateAction = dateAction;
    }

    public DemandePriseEnCharge getDemande() {
        return this.demande;
    }

    public void setDemande(DemandePriseEnCharge demandePriseEnCharge) {
        this.demande = demandePriseEnCharge;
    }

    public HistoriqueAction demande(DemandePriseEnCharge demandePriseEnCharge) {
        this.setDemande(demandePriseEnCharge);
        return this;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public HistoriqueAction utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HistoriqueAction)) {
            return false;
        }
        return getId() != null && getId().equals(((HistoriqueAction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueAction{" +
            "id=" + getId() +
            ", action='" + getAction() + "'" +
            ", description='" + getDescription() + "'" +
            ", dateAction='" + getDateAction() + "'" +
            "}";
    }
}
