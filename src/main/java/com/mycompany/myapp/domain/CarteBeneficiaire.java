package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CarteBeneficiaire.
 */
@Entity
@Table(name = "carte_beneficiaire")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarteBeneficiaire implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "numero_carte", nullable = false, unique = true)
    private String numeroCarte;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_beneficiaire", nullable = false)
    private TypeBeneficiaire typeBeneficiaire;

    @NotNull
    @Column(name = "date_debut_validite", nullable = false)
    private LocalDate dateDebutValidite;

    @NotNull
    @Column(name = "date_fin_validite", nullable = false)
    private LocalDate dateFinValidite;

    @NotNull
    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "direction", "gestion" }, allowSetters = true)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "agent" }, allowSetters = true)
    private AyantDroit ayantDroit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CarteBeneficiaire id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroCarte() {
        return this.numeroCarte;
    }

    public CarteBeneficiaire numeroCarte(String numeroCarte) {
        this.setNumeroCarte(numeroCarte);
        return this;
    }

    public void setNumeroCarte(String numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public TypeBeneficiaire getTypeBeneficiaire() {
        return this.typeBeneficiaire;
    }

    public CarteBeneficiaire typeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.setTypeBeneficiaire(typeBeneficiaire);
        return this;
    }

    public void setTypeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.typeBeneficiaire = typeBeneficiaire;
    }

    public LocalDate getDateDebutValidite() {
        return this.dateDebutValidite;
    }

    public CarteBeneficiaire dateDebutValidite(LocalDate dateDebutValidite) {
        this.setDateDebutValidite(dateDebutValidite);
        return this;
    }

    public void setDateDebutValidite(LocalDate dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public LocalDate getDateFinValidite() {
        return this.dateFinValidite;
    }

    public CarteBeneficiaire dateFinValidite(LocalDate dateFinValidite) {
        this.setDateFinValidite(dateFinValidite);
        return this;
    }

    public void setDateFinValidite(LocalDate dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }

    public LocalDate getDateEmission() {
        return this.dateEmission;
    }

    public CarteBeneficiaire dateEmission(LocalDate dateEmission) {
        this.setDateEmission(dateEmission);
        return this;
    }

    public void setDateEmission(LocalDate dateEmission) {
        this.dateEmission = dateEmission;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public CarteBeneficiaire agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public AyantDroit getAyantDroit() {
        return this.ayantDroit;
    }

    public void setAyantDroit(AyantDroit ayantDroit) {
        this.ayantDroit = ayantDroit;
    }

    public CarteBeneficiaire ayantDroit(AyantDroit ayantDroit) {
        this.setAyantDroit(ayantDroit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarteBeneficiaire)) {
            return false;
        }
        return getId() != null && getId().equals(((CarteBeneficiaire) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarteBeneficiaire{" +
            "id=" + getId() +
            ", numeroCarte='" + getNumeroCarte() + "'" +
            ", typeBeneficiaire='" + getTypeBeneficiaire() + "'" +
            ", dateDebutValidite='" + getDateDebutValidite() + "'" +
            ", dateFinValidite='" + getDateFinValidite() + "'" +
            ", dateEmission='" + getDateEmission() + "'" +
            "}";
    }
}
