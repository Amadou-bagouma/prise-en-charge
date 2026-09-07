package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.DemandePriseEnCharge} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.DemandePriseEnChargeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /demande-prise-en-charges?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandePriseEnChargeCriteria implements Serializable, Criteria {

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

    /**
     * Class for filtering StatutDemande
     */
    public static class StatutDemandeFilter extends Filter<StatutDemande> {

        public StatutDemandeFilter() {}

        public StatutDemandeFilter(StatutDemandeFilter filter) {
            super(filter);
        }

        @Override
        public StatutDemandeFilter copy() {
            return new StatutDemandeFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter reference;

    private InstantFilter dateCreation;

    private InstantFilter dateModification;

    private TypeBeneficiaireFilter typeBeneficiaire;

    private StringFilter description;

    private StatutDemandeFilter statut;

    private InstantFilter dateAssignation;

    private LongFilter agentId;

    private LongFilter ayantDroitId;

    private LongFilter typeSoinId;

    private LongFilter etablissementSanteId;

    private LongFilter gestionnaireCreateurId;

    private LongFilter assigneAId;

    private Boolean distinct;

    public DemandePriseEnChargeCriteria() {}

    public DemandePriseEnChargeCriteria(DemandePriseEnChargeCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.reference = other.optionalReference().map(StringFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.dateModification = other.optionalDateModification().map(InstantFilter::copy).orElse(null);
        this.typeBeneficiaire = other.optionalTypeBeneficiaire().map(TypeBeneficiaireFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutDemandeFilter::copy).orElse(null);
        this.dateAssignation = other.optionalDateAssignation().map(InstantFilter::copy).orElse(null);
        this.agentId = other.optionalAgentId().map(LongFilter::copy).orElse(null);
        this.ayantDroitId = other.optionalAyantDroitId().map(LongFilter::copy).orElse(null);
        this.typeSoinId = other.optionalTypeSoinId().map(LongFilter::copy).orElse(null);
        this.etablissementSanteId = other.optionalEtablissementSanteId().map(LongFilter::copy).orElse(null);
        this.gestionnaireCreateurId = other.optionalGestionnaireCreateurId().map(LongFilter::copy).orElse(null);
        this.assigneAId = other.optionalAssigneAId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public DemandePriseEnChargeCriteria copy() {
        return new DemandePriseEnChargeCriteria(this);
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

    public StringFilter getReference() {
        return reference;
    }

    public Optional<StringFilter> optionalReference() {
        return Optional.ofNullable(reference);
    }

    public StringFilter reference() {
        if (reference == null) {
            setReference(new StringFilter());
        }
        return reference;
    }

    public void setReference(StringFilter reference) {
        this.reference = reference;
    }

    public InstantFilter getDateCreation() {
        return dateCreation;
    }

    public Optional<InstantFilter> optionalDateCreation() {
        return Optional.ofNullable(dateCreation);
    }

    public InstantFilter dateCreation() {
        if (dateCreation == null) {
            setDateCreation(new InstantFilter());
        }
        return dateCreation;
    }

    public void setDateCreation(InstantFilter dateCreation) {
        this.dateCreation = dateCreation;
    }

    public InstantFilter getDateModification() {
        return dateModification;
    }

    public Optional<InstantFilter> optionalDateModification() {
        return Optional.ofNullable(dateModification);
    }

    public InstantFilter dateModification() {
        if (dateModification == null) {
            setDateModification(new InstantFilter());
        }
        return dateModification;
    }

    public void setDateModification(InstantFilter dateModification) {
        this.dateModification = dateModification;
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

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StatutDemandeFilter getStatut() {
        return statut;
    }

    public Optional<StatutDemandeFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutDemandeFilter statut() {
        if (statut == null) {
            setStatut(new StatutDemandeFilter());
        }
        return statut;
    }

    public void setStatut(StatutDemandeFilter statut) {
        this.statut = statut;
    }

    public InstantFilter getDateAssignation() {
        return dateAssignation;
    }

    public Optional<InstantFilter> optionalDateAssignation() {
        return Optional.ofNullable(dateAssignation);
    }

    public InstantFilter dateAssignation() {
        if (dateAssignation == null) {
            setDateAssignation(new InstantFilter());
        }
        return dateAssignation;
    }

    public void setDateAssignation(InstantFilter dateAssignation) {
        this.dateAssignation = dateAssignation;
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

    public LongFilter getTypeSoinId() {
        return typeSoinId;
    }

    public Optional<LongFilter> optionalTypeSoinId() {
        return Optional.ofNullable(typeSoinId);
    }

    public LongFilter typeSoinId() {
        if (typeSoinId == null) {
            setTypeSoinId(new LongFilter());
        }
        return typeSoinId;
    }

    public void setTypeSoinId(LongFilter typeSoinId) {
        this.typeSoinId = typeSoinId;
    }

    public LongFilter getEtablissementSanteId() {
        return etablissementSanteId;
    }

    public Optional<LongFilter> optionalEtablissementSanteId() {
        return Optional.ofNullable(etablissementSanteId);
    }

    public LongFilter etablissementSanteId() {
        if (etablissementSanteId == null) {
            setEtablissementSanteId(new LongFilter());
        }
        return etablissementSanteId;
    }

    public void setEtablissementSanteId(LongFilter etablissementSanteId) {
        this.etablissementSanteId = etablissementSanteId;
    }

    public LongFilter getGestionnaireCreateurId() {
        return gestionnaireCreateurId;
    }

    public Optional<LongFilter> optionalGestionnaireCreateurId() {
        return Optional.ofNullable(gestionnaireCreateurId);
    }

    public LongFilter gestionnaireCreateurId() {
        if (gestionnaireCreateurId == null) {
            setGestionnaireCreateurId(new LongFilter());
        }
        return gestionnaireCreateurId;
    }

    public void setGestionnaireCreateurId(LongFilter gestionnaireCreateurId) {
        this.gestionnaireCreateurId = gestionnaireCreateurId;
    }

    public LongFilter getAssigneAId() {
        return assigneAId;
    }

    public Optional<LongFilter> optionalAssigneAId() {
        return Optional.ofNullable(assigneAId);
    }

    public LongFilter assigneAId() {
        if (assigneAId == null) {
            setAssigneAId(new LongFilter());
        }
        return assigneAId;
    }

    public void setAssigneAId(LongFilter assigneAId) {
        this.assigneAId = assigneAId;
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
        final DemandePriseEnChargeCriteria that = (DemandePriseEnChargeCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(reference, that.reference) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(dateModification, that.dateModification) &&
            Objects.equals(typeBeneficiaire, that.typeBeneficiaire) &&
            Objects.equals(description, that.description) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(dateAssignation, that.dateAssignation) &&
            Objects.equals(agentId, that.agentId) &&
            Objects.equals(ayantDroitId, that.ayantDroitId) &&
            Objects.equals(typeSoinId, that.typeSoinId) &&
            Objects.equals(etablissementSanteId, that.etablissementSanteId) &&
            Objects.equals(gestionnaireCreateurId, that.gestionnaireCreateurId) &&
            Objects.equals(assigneAId, that.assigneAId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            reference,
            dateCreation,
            dateModification,
            typeBeneficiaire,
            description,
            statut,
            dateAssignation,
            agentId,
            ayantDroitId,
            typeSoinId,
            etablissementSanteId,
            gestionnaireCreateurId,
            assigneAId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandePriseEnChargeCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalReference().map(f -> "reference=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalDateModification().map(f -> "dateModification=" + f + ", ").orElse("") +
            optionalTypeBeneficiaire().map(f -> "typeBeneficiaire=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalDateAssignation().map(f -> "dateAssignation=" + f + ", ").orElse("") +
            optionalAgentId().map(f -> "agentId=" + f + ", ").orElse("") +
            optionalAyantDroitId().map(f -> "ayantDroitId=" + f + ", ").orElse("") +
            optionalTypeSoinId().map(f -> "typeSoinId=" + f + ", ").orElse("") +
            optionalEtablissementSanteId().map(f -> "etablissementSanteId=" + f + ", ").orElse("") +
            optionalGestionnaireCreateurId().map(f -> "gestionnaireCreateurId=" + f + ", ").orElse("") +
            optionalAssigneAId().map(f -> "assigneAId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
