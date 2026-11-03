package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.StatutAgent;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Agent} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.AgentResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /agents?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AgentCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Class for filtering StatutAgent
     */
    public static class StatutAgentFilter extends Filter<StatutAgent> {

        public StatutAgentFilter() {}

        public StatutAgentFilter(StatutAgentFilter filter) {
            super(filter);
        }

        @Override
        public StatutAgentFilter copy() {
            return new StatutAgentFilter(this);
        }
    }

    private LongFilter id;

    private StatutAgentFilter statut;

    private StringFilter matricule;

    private StringFilter nom;

    private StringFilter prenom;

    private LocalDateFilter dateNaissance;

    private StringFilter telephone;

    private LongFilter fonctionId;

    private LongFilter directionId;

    private LongFilter gestionId;

    private Boolean distinct;

    public AgentCriteria() {}

    public AgentCriteria(AgentCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutAgentFilter::copy).orElse(null);
        this.matricule = other.optionalMatricule().map(StringFilter::copy).orElse(null);
        this.nom = other.optionalNom().map(StringFilter::copy).orElse(null);
        this.prenom = other.optionalPrenom().map(StringFilter::copy).orElse(null);
        this.dateNaissance = other.optionalDateNaissance().map(LocalDateFilter::copy).orElse(null);
        this.telephone = other.optionalTelephone().map(StringFilter::copy).orElse(null);
        this.fonctionId = other.optionalFonctionId().map(LongFilter::copy).orElse(null);
        this.directionId = other.optionalDirectionId().map(LongFilter::copy).orElse(null);
        this.gestionId = other.optionalGestionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public AgentCriteria copy() {
        return new AgentCriteria(this);
    }

    public StatutAgentFilter getStatut() {
        return statut;
    }

    public Optional<StatutAgentFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutAgentFilter statut() {
        if (statut == null) {
            setStatut(new StatutAgentFilter());
        }
        return statut;
    }

    public void setStatut(StatutAgentFilter statut) {
        this.statut = statut;
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getMatricule() {
        return matricule;
    }

    public Optional<StringFilter> optionalMatricule() {
        return Optional.ofNullable(matricule);
    }

    public StringFilter matricule() {
        if (matricule == null) {
            setMatricule(new StringFilter());
        }
        return matricule;
    }

    public void setMatricule(StringFilter matricule) {
        this.matricule = matricule;
    }

    public StringFilter getNom() {
        return nom;
    }

    public Optional<StringFilter> optionalNom() {
        return Optional.ofNullable(nom);
    }

    public StringFilter nom() {
        if (nom == null) {
            setNom(new StringFilter());
        }
        return nom;
    }

    public void setNom(StringFilter nom) {
        this.nom = nom;
    }

    public StringFilter getPrenom() {
        return prenom;
    }

    public Optional<StringFilter> optionalPrenom() {
        return Optional.ofNullable(prenom);
    }

    public StringFilter prenom() {
        if (prenom == null) {
            setPrenom(new StringFilter());
        }
        return prenom;
    }

    public void setPrenom(StringFilter prenom) {
        this.prenom = prenom;
    }

    public LocalDateFilter getDateNaissance() {
        return dateNaissance;
    }

    public Optional<LocalDateFilter> optionalDateNaissance() {
        return Optional.ofNullable(dateNaissance);
    }

    public LocalDateFilter dateNaissance() {
        if (dateNaissance == null) {
            setDateNaissance(new LocalDateFilter());
        }
        return dateNaissance;
    }

    public void setDateNaissance(LocalDateFilter dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public StringFilter getTelephone() {
        return telephone;
    }

    public Optional<StringFilter> optionalTelephone() {
        return Optional.ofNullable(telephone);
    }

    public StringFilter telephone() {
        if (telephone == null) {
            setTelephone(new StringFilter());
        }
        return telephone;
    }

    public void setTelephone(StringFilter telephone) {
        this.telephone = telephone;
    }

    public LongFilter getFonctionId() {
        return fonctionId;
    }

    public Optional<LongFilter> optionalFonctionId() {
        return Optional.ofNullable(fonctionId);
    }

    public LongFilter fonctionId() {
        if (fonctionId == null) {
            setFonctionId(new LongFilter());
        }
        return fonctionId;
    }

    public void setFonctionId(LongFilter fonctionId) {
        this.fonctionId = fonctionId;
    }

    public LongFilter getDirectionId() {
        return directionId;
    }

    public Optional<LongFilter> optionalDirectionId() {
        return Optional.ofNullable(directionId);
    }

    public LongFilter directionId() {
        if (directionId == null) {
            setDirectionId(new LongFilter());
        }
        return directionId;
    }

    public void setDirectionId(LongFilter directionId) {
        this.directionId = directionId;
    }

    public LongFilter getGestionId() {
        return gestionId;
    }

    public Optional<LongFilter> optionalGestionId() {
        return Optional.ofNullable(gestionId);
    }

    public LongFilter gestionId() {
        if (gestionId == null) {
            setGestionId(new LongFilter());
        }
        return gestionId;
    }

    public void setGestionId(LongFilter gestionId) {
        this.gestionId = gestionId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AgentCriteria that = (AgentCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(matricule, that.matricule) &&
            Objects.equals(nom, that.nom) &&
            Objects.equals(prenom, that.prenom) &&
            Objects.equals(dateNaissance, that.dateNaissance) &&
            Objects.equals(telephone, that.telephone) &&
            Objects.equals(fonctionId, that.fonctionId) &&
            Objects.equals(directionId, that.directionId) &&
            Objects.equals(gestionId, that.gestionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statut, matricule, nom, prenom, dateNaissance, telephone, fonctionId, directionId, gestionId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AgentCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalMatricule().map(f -> "matricule=" + f + ", ").orElse("") +
            optionalNom().map(f -> "nom=" + f + ", ").orElse("") +
            optionalPrenom().map(f -> "prenom=" + f + ", ").orElse("") +
            optionalDateNaissance().map(f -> "dateNaissance=" + f + ", ").orElse("") +
            optionalTelephone().map(f -> "telephone=" + f + ", ").orElse("") +
            optionalFonctionId().map(f -> "fonctionId=" + f + ", ").orElse("") +
            optionalDirectionId().map(f -> "directionId=" + f + ", ").orElse("") +
            optionalGestionId().map(f -> "gestionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
