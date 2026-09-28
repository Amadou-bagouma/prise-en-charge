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
 * A PieceJustificative.
 */
@Entity
@Table(name = "piece_justificative")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PieceJustificative extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @NotNull
    @Column(name = "chemin_fichier", nullable = false)
    private String cheminFichier;

    @NotNull
    @Column(name = "date_ajout", nullable = false)
    private Instant dateAjout;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(
        value = { "agent", "ayantDroit", "typeSoin", "etablissementSante", "gestionnaireCreateur", "assigneA" },
        allowSetters = true
    )
    private DemandePriseEnCharge demande;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PieceJustificative id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomFichier() {
        return this.nomFichier;
    }

    public PieceJustificative nomFichier(String nomFichier) {
        this.setNomFichier(nomFichier);
        return this;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getCheminFichier() {
        return this.cheminFichier;
    }

    public PieceJustificative cheminFichier(String cheminFichier) {
        this.setCheminFichier(cheminFichier);
        return this;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public Instant getDateAjout() {
        return this.dateAjout;
    }

    public PieceJustificative dateAjout(Instant dateAjout) {
        this.setDateAjout(dateAjout);
        return this;
    }

    public void setDateAjout(Instant dateAjout) {
        this.dateAjout = dateAjout;
    }

    public DemandePriseEnCharge getDemande() {
        return this.demande;
    }

    public void setDemande(DemandePriseEnCharge demandePriseEnCharge) {
        this.demande = demandePriseEnCharge;
    }

    public PieceJustificative demande(DemandePriseEnCharge demandePriseEnCharge) {
        this.setDemande(demandePriseEnCharge);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PieceJustificative)) {
            return false;
        }
        return getId() != null && getId().equals(((PieceJustificative) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PieceJustificative{" +
            "id=" + getId() +
            ", nomFichier='" + getNomFichier() + "'" +
            ", cheminFichier='" + getCheminFichier() + "'" +
            ", dateAjout='" + getDateAjout() + "'" +
            "}";
    }
}
