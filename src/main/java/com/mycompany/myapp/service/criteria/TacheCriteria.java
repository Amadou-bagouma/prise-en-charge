package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Tache} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.TacheResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /taches?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TacheCriteria implements Serializable, Criteria {

    /**
     * Class for filtering StatutTache
     */
    public static class StatutTacheFilter extends Filter<StatutTache> {

        public StatutTacheFilter() {}

        public StatutTacheFilter(StatutTacheFilter filter) {
            super(filter);
        }

        @Override
        public StatutTacheFilter copy() {
            return new StatutTacheFilter(this);
        }
    }

    /**
     * Class for filtering PrioriteTache
     */
    public static class PrioriteTacheFilter extends Filter<PrioriteTache> {

        public PrioriteTacheFilter() {}

        public PrioriteTacheFilter(PrioriteTacheFilter filter) {
            super(filter);
        }

        @Override
        public PrioriteTacheFilter copy() {
            return new PrioriteTacheFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titre;

    private StringFilter description;

    private InstantFilter dateCreation;

    private InstantFilter dateAssignation;

    private InstantFilter dateEcheance;

    private InstantFilter dateTerminaison;

    private StatutTacheFilter statut;

    private PrioriteTacheFilter priorite;

    private BooleanFilter lu;

    private StringFilter commentaire;

    private LongFilter demandeId;

    private LongFilter utilisateurId;

    private LongFilter boiteReceptionId;

    private Boolean distinct;

    public TacheCriteria() {}

    public TacheCriteria(TacheCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.dateAssignation = other.optionalDateAssignation().map(InstantFilter::copy).orElse(null);
        this.dateEcheance = other.optionalDateEcheance().map(InstantFilter::copy).orElse(null);
        this.dateTerminaison = other.optionalDateTerminaison().map(InstantFilter::copy).orElse(null);
        this.statut = other.optionalStatut().map(StatutTacheFilter::copy).orElse(null);
        this.priorite = other.optionalPriorite().map(PrioriteTacheFilter::copy).orElse(null);
        this.lu = other.optionalLu().map(BooleanFilter::copy).orElse(null);
        this.commentaire = other.optionalCommentaire().map(StringFilter::copy).orElse(null);
        this.demandeId = other.optionalDemandeId().map(LongFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.boiteReceptionId = other.optionalBoiteReceptionId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TacheCriteria copy() {
        return new TacheCriteria(this);
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

    public StringFilter getTitre() {
        return titre;
    }

    public Optional<StringFilter> optionalTitre() {
        return Optional.ofNullable(titre);
    }

    public StringFilter titre() {
        if (titre == null) {
            setTitre(new StringFilter());
        }
        return titre;
    }

    public void setTitre(StringFilter titre) {
        this.titre = titre;
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

    public InstantFilter getDateEcheance() {
        return dateEcheance;
    }

    public Optional<InstantFilter> optionalDateEcheance() {
        return Optional.ofNullable(dateEcheance);
    }

    public InstantFilter dateEcheance() {
        if (dateEcheance == null) {
            setDateEcheance(new InstantFilter());
        }
        return dateEcheance;
    }

    public void setDateEcheance(InstantFilter dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public InstantFilter getDateTerminaison() {
        return dateTerminaison;
    }

    public Optional<InstantFilter> optionalDateTerminaison() {
        return Optional.ofNullable(dateTerminaison);
    }

    public InstantFilter dateTerminaison() {
        if (dateTerminaison == null) {
            setDateTerminaison(new InstantFilter());
        }
        return dateTerminaison;
    }

    public void setDateTerminaison(InstantFilter dateTerminaison) {
        this.dateTerminaison = dateTerminaison;
    }

    public StatutTacheFilter getStatut() {
        return statut;
    }

    public Optional<StatutTacheFilter> optionalStatut() {
        return Optional.ofNullable(statut);
    }

    public StatutTacheFilter statut() {
        if (statut == null) {
            setStatut(new StatutTacheFilter());
        }
        return statut;
    }

    public void setStatut(StatutTacheFilter statut) {
        this.statut = statut;
    }

    public PrioriteTacheFilter getPriorite() {
        return priorite;
    }

    public Optional<PrioriteTacheFilter> optionalPriorite() {
        return Optional.ofNullable(priorite);
    }

    public PrioriteTacheFilter priorite() {
        if (priorite == null) {
            setPriorite(new PrioriteTacheFilter());
        }
        return priorite;
    }

    public void setPriorite(PrioriteTacheFilter priorite) {
        this.priorite = priorite;
    }

    public BooleanFilter getLu() {
        return lu;
    }

    public Optional<BooleanFilter> optionalLu() {
        return Optional.ofNullable(lu);
    }

    public BooleanFilter lu() {
        if (lu == null) {
            setLu(new BooleanFilter());
        }
        return lu;
    }

    public void setLu(BooleanFilter lu) {
        this.lu = lu;
    }

    public StringFilter getCommentaire() {
        return commentaire;
    }

    public Optional<StringFilter> optionalCommentaire() {
        return Optional.ofNullable(commentaire);
    }

    public StringFilter commentaire() {
        if (commentaire == null) {
            setCommentaire(new StringFilter());
        }
        return commentaire;
    }

    public void setCommentaire(StringFilter commentaire) {
        this.commentaire = commentaire;
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

    public LongFilter getBoiteReceptionId() {
        return boiteReceptionId;
    }

    public Optional<LongFilter> optionalBoiteReceptionId() {
        return Optional.ofNullable(boiteReceptionId);
    }

    public LongFilter boiteReceptionId() {
        if (boiteReceptionId == null) {
            setBoiteReceptionId(new LongFilter());
        }
        return boiteReceptionId;
    }

    public void setBoiteReceptionId(LongFilter boiteReceptionId) {
        this.boiteReceptionId = boiteReceptionId;
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
        final TacheCriteria that = (TacheCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(description, that.description) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(dateAssignation, that.dateAssignation) &&
            Objects.equals(dateEcheance, that.dateEcheance) &&
            Objects.equals(dateTerminaison, that.dateTerminaison) &&
            Objects.equals(statut, that.statut) &&
            Objects.equals(priorite, that.priorite) &&
            Objects.equals(lu, that.lu) &&
            Objects.equals(commentaire, that.commentaire) &&
            Objects.equals(demandeId, that.demandeId) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(boiteReceptionId, that.boiteReceptionId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            titre,
            description,
            dateCreation,
            dateAssignation,
            dateEcheance,
            dateTerminaison,
            statut,
            priorite,
            lu,
            commentaire,
            demandeId,
            utilisateurId,
            boiteReceptionId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TacheCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalDateAssignation().map(f -> "dateAssignation=" + f + ", ").orElse("") +
            optionalDateEcheance().map(f -> "dateEcheance=" + f + ", ").orElse("") +
            optionalDateTerminaison().map(f -> "dateTerminaison=" + f + ", ").orElse("") +
            optionalStatut().map(f -> "statut=" + f + ", ").orElse("") +
            optionalPriorite().map(f -> "priorite=" + f + ", ").orElse("") +
            optionalLu().map(f -> "lu=" + f + ", ").orElse("") +
            optionalCommentaire().map(f -> "commentaire=" + f + ", ").orElse("") +
            optionalDemandeId().map(f -> "demandeId=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalBoiteReceptionId().map(f -> "boiteReceptionId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
