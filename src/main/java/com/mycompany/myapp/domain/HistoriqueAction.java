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
public class HistoriqueAction extends AbstractAuditingEntity<Long> implements Serializable {

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

    /**
     * Le dossier concerne, quand l'action en vise un.
     *
     * <p>Facultatif depuis que l'historique enregistre aussi ce qui arrive a un agent ou a un
     * ayant droit : une radiation ne porte sur aucune demande, et l'exiger revenait a ne jamais
     * la tracer.
     */
    @ManyToOne
    @JsonIgnoreProperties(
        value = { "agent", "ayantDroit", "typeSoin", "etablissementSante", "gestionnaireCreateur", "assigneA" },
        allowSetters = true
    )
    private DemandePriseEnCharge demande;

    /**
     * Ce sur quoi porte l'action quand ce n'est pas une demande : {@code AGENT},
     * {@code AYANT_DROIT}.
     *
     * <p>Un type et un identifiant plutot qu'une relation par cible : ajouter une relation par
     * entite tracable ferait autant de colonnes vides sur chaque ligne, et il faudrait toucher la
     * table a chaque nouvel objet suivi.
     */
    @Size(max = 50)
    @Column(name = "cible_type", length = 50)
    private String cibleType;

    @Column(name = "cible_id")
    private Long cibleId;

    /**
     * Qui a fait l'action. Facultatif : un traitement automatique n'a pas d'auteur, et savoir
     * qu'une radiation a eu lieu sans savoir par qui vaut mieux que de ne pas le savoir.
     */
    @ManyToOne
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

    public String getCibleType() {
        return this.cibleType;
    }

    public void setCibleType(String cibleType) {
        this.cibleType = cibleType;
    }

    public Long getCibleId() {
        return this.cibleId;
    }

    public void setCibleId(Long cibleId) {
        this.cibleId = cibleId;
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
