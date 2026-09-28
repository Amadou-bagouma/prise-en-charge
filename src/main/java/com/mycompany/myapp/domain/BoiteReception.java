package com.mycompany.myapp.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A BoiteReception.
 */
@Entity
@Table(name = "boite_reception")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BoiteReception extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @Column(name = "date_derniere_lecture")
    private Instant dateDerniereLecture;

    @NotNull
    @Column(name = "nombre_non_lus", nullable = false)
    private Integer nombreNonLus;

    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull
    @JoinColumn(unique = true)
    private User utilisateur;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BoiteReception id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public BoiteReception dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateDerniereLecture() {
        return this.dateDerniereLecture;
    }

    public BoiteReception dateDerniereLecture(Instant dateDerniereLecture) {
        this.setDateDerniereLecture(dateDerniereLecture);
        return this;
    }

    public void setDateDerniereLecture(Instant dateDerniereLecture) {
        this.dateDerniereLecture = dateDerniereLecture;
    }

    public Integer getNombreNonLus() {
        return this.nombreNonLus;
    }

    public BoiteReception nombreNonLus(Integer nombreNonLus) {
        this.setNombreNonLus(nombreNonLus);
        return this;
    }

    public void setNombreNonLus(Integer nombreNonLus) {
        this.nombreNonLus = nombreNonLus;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public BoiteReception actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public User getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(User user) {
        this.utilisateur = user;
    }

    public BoiteReception utilisateur(User user) {
        this.setUtilisateur(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BoiteReception)) {
            return false;
        }
        return getId() != null && getId().equals(((BoiteReception) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BoiteReception{" +
            "id=" + getId() +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateDerniereLecture='" + getDateDerniereLecture() + "'" +
            ", nombreNonLus=" + getNombreNonLus() +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
