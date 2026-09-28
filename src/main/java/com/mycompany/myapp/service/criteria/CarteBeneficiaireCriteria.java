package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.CarteBeneficiaire} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.CarteBeneficiaireResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /carte-beneficiaires?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarteBeneficiaireCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeBeneficiaire
     */
    public static class TypeBeneficiaireFilter extends Filter<TypeBeneficiaire> {

        public TypeBeneficiaireFilter() {}

        public TypeBeneficiaireFilter(TypeBeneficiaireFilter filter) {
            super(filter);
        }

        @Override
        public TypeBeneficiaireFilter copy() {
            return new TypeBeneficiaireFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter numeroCarte;

    private TypeBeneficiaireFilter typeBeneficiaire;

    private LocalDateFilter dateDebutValidite;

    private LocalDateFilter dateFinValidite;

    private LocalDateFilter dateEmission;

    private LongFilter agentId;

    private LongFilter ayantDroitId;

    private Boolean distinct;

    public CarteBeneficiaireCriteria() {}

    public CarteBeneficiaireCriteria(CarteBeneficiaireCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.numeroCarte = other.optionalNumeroCarte().map(StringFilter::copy).orElse(null);
        this.typeBeneficiaire = other.optionalTypeBeneficiaire().map(TypeBeneficiaireFilter::copy).orElse(null);
        this.dateDebutValidite = other.optionalDateDebutValidite().map(LocalDateFilter::copy).orElse(null);
        this.dateFinValidite = other.optionalDateFinValidite().map(LocalDateFilter::copy).orElse(null);
        this.dateEmission = other.optionalDateEmission().map(LocalDateFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.ayantDroitId = other.optionalAyantDroitId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CarteBeneficiaireCriteria copy() {
        return new CarteBeneficiaireCriteria(this);
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

    public StringFilter getNumeroCarte() {
        return numeroCarte;
    }

    public Optional<StringFilter> optionalNumeroCarte() {
        return Optional.ofNullable(numeroCarte);
    }

    public StringFilter numeroCarte() {
        if (numeroCarte == null) {
            setNumeroCarte(new StringFilter());
        }
        return numeroCarte;
    }

    public void setNumeroCarte(StringFilter numeroCarte) {
        this.numeroCarte = numeroCarte;
    }

    public TypeBeneficiaireFilter getTypeBeneficiaire() {
        return typeBeneficiaire;
    }

    public Optional<TypeBeneficiaireFilter> optionalTypeBeneficiaire() {
        return Optional.ofNullable(typeBeneficiaire);
    }

    public TypeBeneficiaireFilter typeBeneficiaire() {
        if (typeBeneficiaire == null) {
            setTypeBeneficiaire(new TypeBeneficiaireFilter());
        }
        return typeBeneficiaire;
    }

    public void setTypeBeneficiaire(TypeBeneficiaireFilter typeBeneficiaire) {
        this.typeBeneficiaire = typeBeneficiaire;
    }

    public LocalDateFilter getDateDebutValidite() {
        return dateDebutValidite;
    }

    public Optional<LocalDateFilter> optionalDateDebutValidite() {
        return Optional.ofNullable(dateDebutValidite);
    }

    public LocalDateFilter dateDebutValidite() {
        if (dateDebutValidite == null) {
            setDateDebutValidite(new LocalDateFilter());
        }
        return dateDebutValidite;
    }

    public void setDateDebutValidite(LocalDateFilter dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }

    public LocalDateFilter getDateFinValidite() {
        return dateFinValidite;
    }

    public Optional<LocalDateFilter> optionalDateFinValidite() {
        return Optional.ofNullable(dateFinValidite);
    }

    public LocalDateFilter dateFinValidite() {
        if (dateFinValidite == null) {
            setDateFinValidite(new LocalDateFilter());
        }
        return dateFinValidite;
    }

    public void setDateFinValidite(LocalDateFilter dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }

    public LocalDateFilter getDateEmission() {
        return dateEmission;
    }

    public Optional<LocalDateFilter> optionalDateEmission() {
        return Optional.ofNullable(dateEmission);
    }

    public LocalDateFilter dateEmission() {
        if (dateEmission == null) {
            setDateEmission(new LocalDateFilter());
        }
        return dateEmission;
    }

    public void setDateEmission(LocalDateFilter dateEmission) {
        this.dateEmission = dateEmission;
    }

    public LongFilter getAgentId() {
        return agentId;
    }

    public Optional<LongFilter> optionalAgentId() {
        return Optional.ofNullable(agentId);
    }

    public LongFilter agentId() {
        if (agentId == null) {
            setAgentId(new LongFilter());
        }
        return agentId;
    }

    public void setAgentId(LongFilter agentId) {
        this.agentId = agentId;
    }

    public LongFilter getAyantDroitId() {
        return ayantDroitId;
    }

    public Optional<LongFilter> optionalAyantDroitId() {
        return Optional.ofNullable(ayantDroitId);
    }

    public LongFilter ayantDroitId() {
        if (ayantDroitId == null) {
            setAyantDroitId(new LongFilter());
        }
        return ayantDroitId;
    }

    public void setAyantDroitId(LongFilter ayantDroitId) {
        this.ayantDroitId = ayantDroitId;
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
        final CarteBeneficiaireCriteria that = (CarteBeneficiaireCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(numeroCarte, that.numeroCarte) &&
            Objects.equals(typeBeneficiaire, that.typeBeneficiaire) &&
            Objects.equals(dateDebutValidite, that.dateDebutValidite) &&
            Objects.equals(dateFinValidite, that.dateFinValidite) &&
            Objects.equals(dateEmission, that.dateEmission) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(ayantDroitId, that.ayantDroitId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            numeroCarte,
            typeBeneficiaire,
            dateDebutValidite,
            dateFinValidite,
            dateEmission,
            agentId,
            ayantDroitId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarteBeneficiaireCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalNumeroCarte().map(f -> "numeroCarte=" + f + ", ").orElse("") +
            optionalTypeBeneficiaire().map(f -> "typeBeneficiaire=" + f + ", ").orElse("") +
            optionalDateDebutValidite().map(f -> "dateDebutValidite=" + f + ", ").orElse("") +
            optionalDateFinValidite().map(f -> "dateFinValidite=" + f + ", ").orElse("") +
            optionalDateEmission().map(f -> "dateEmission=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalAyantDroitId().map(f -> "ayantDroitId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
