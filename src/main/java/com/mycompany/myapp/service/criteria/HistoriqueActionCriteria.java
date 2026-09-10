package com.mycompany.myapp.service.criteria;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.HistoriqueAction} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.HistoriqueActionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /historique-actions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class HistoriqueActionCriteria implements Serializable, Criteria {

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter action;

    private StringFilter description;

    private InstantFilter dateAction;

    private LongFilter demandeId;

    private LongFilter utilisateurId;

    private Boolean distinct;

    public HistoriqueActionCriteria() {}

    public HistoriqueActionCriteria(HistoriqueActionCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.action = other.optionalAction().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.dateAction = other.optionalDateAction().map(InstantFilter::copy).orElse(null);
        this.demandeId = other.optionalDemandeId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public HistoriqueActionCriteria copy() {
        return new HistoriqueActionCriteria(this);
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

    public StringFilter getAction() {
        return action;
    }

    public Optional<StringFilter> optionalAction() {
        return Optional.ofNullable(action);
    }

    public StringFilter action() {
        if (action == null) {
            setAction(new StringFilter());
        }
        return action;
    }

    public void setAction(StringFilter action) {
        this.action = action;
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

    public InstantFilter getDateAction() {
        return dateAction;
    }

    public Optional<InstantFilter> optionalDateAction() {
        return Optional.ofNullable(dateAction);
    }

    public InstantFilter dateAction() {
        if (dateAction == null) {
            setDateAction(new InstantFilter());
        }
        return dateAction;
    }

    public void setDateAction(InstantFilter dateAction) {
        this.dateAction = dateAction;
    }

    public LongFilter getDemandeId() {
        return demandeId;
    }

    public Optional<LongFilter> optionalDemandeId() {
        return Optional.ofNullable(demandeId);
    }

    public LongFilter demandeId() {
        if (demandeId == null) {
            setDemandeId(new LongFilter());
        }
        return demandeId;
    }

    public void setDemandeId(LongFilter demandeId) {
        this.demandeId = demandeId;
    }

    public LongFilter getUtilisateurId() {
        return utilisateurId;
    }

    public Optional<LongFilter> optionalUtilisateurId() {
        return Optional.ofNullable(utilisateurId);
    }

    public LongFilter utilisateurId() {
        if (utilisateurId == null) {
            setUtilisateurId(new LongFilter());
        }
        return utilisateurId;
    }

    public void setUtilisateurId(LongFilter utilisateurId) {
        this.utilisateurId = utilisateurId;
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
        final HistoriqueActionCriteria that = (HistoriqueActionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(action, that.action) &&
            Objects.equals(description, that.description) &&
            Objects.equals(dateAction, that.dateAction) &&
            Objects.equals(demandeId, that.demandeId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, action, description, dateAction, demandeId, utilisateurId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "HistoriqueActionCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAction().map(f -> "action=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalDateAction().map(f -> "dateAction=" + f + ", ").orElse("") +
            optionalDemandeId().map(f -> "demandeId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
