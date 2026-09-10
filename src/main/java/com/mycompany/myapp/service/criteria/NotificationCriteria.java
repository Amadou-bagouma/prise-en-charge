package com.mycompany.myapp.service.criteria;

import com.mycompany.myapp.domain.enumeration.TypeNotification;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Notification} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.NotificationResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /notifications?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class NotificationCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TypeNotification
     */
    public static class TypeNotificationFilter extends Filter<TypeNotification> {

        public TypeNotificationFilter() {}

        public TypeNotificationFilter(TypeNotificationFilter filter) {
            super(filter);
        }

        @Override
        public TypeNotificationFilter copy() {
            return new TypeNotificationFilter(this);
        }
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter titre;

    private StringFilter message;

    private InstantFilter dateCreation;

    private InstantFilter dateLecture;

    private BooleanFilter lu;

    private TypeNotificationFilter type;

    private LongFilter utilisateurId;

    private LongFilter demandeId;

    private LongFilter tacheId;

    private Boolean distinct;

    public NotificationCriteria() {}

    public NotificationCriteria(NotificationCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.titre = other.optionalTitre().map(StringFilter::copy).orElse(null);
        this.message = other.optionalMessage().map(StringFilter::copy).orElse(null);
        this.dateCreation = other.optionalDateCreation().map(InstantFilter::copy).orElse(null);
        this.dateLecture = other.optionalDateLecture().map(InstantFilter::copy).orElse(null);
        this.lu = other.optionalLu().map(BooleanFilter::copy).orElse(null);
        this.type = other.optionalType().map(TypeNotificationFilter::copy).orElse(null);
        this.utilisateurId = other.optionalUtilisateurId().map(LongFilter::copy).orElse(null);
        this.demandeId = other.optionalDemandeId().map(LongFilter::copy).orElse(null);
        this.tacheId = other.optionalTacheId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public NotificationCriteria copy() {
        return new NotificationCriteria(this);
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

    public StringFilter getMessage() {
        return message;
    }

    public Optional<StringFilter> optionalMessage() {
        return Optional.ofNullable(message);
    }

    public StringFilter message() {
        if (message == null) {
            setMessage(new StringFilter());
        }
        return message;
    }

    public void setMessage(StringFilter message) {
        this.message = message;
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

    public InstantFilter getDateLecture() {
        return dateLecture;
    }

    public Optional<InstantFilter> optionalDateLecture() {
        return Optional.ofNullable(dateLecture);
    }

    public InstantFilter dateLecture() {
        if (dateLecture == null) {
            setDateLecture(new InstantFilter());
        }
        return dateLecture;
    }

    public void setDateLecture(InstantFilter dateLecture) {
        this.dateLecture = dateLecture;
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

    public TypeNotificationFilter getType() {
        return type;
    }

    public Optional<TypeNotificationFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public TypeNotificationFilter type() {
        if (type == null) {
            setType(new TypeNotificationFilter());
        }
        return type;
    }

    public void setType(TypeNotificationFilter type) {
        this.type = type;
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

    public LongFilter getTacheId() {
        return tacheId;
    }

    public Optional<LongFilter> optionalTacheId() {
        return Optional.ofNullable(tacheId);
    }

    public LongFilter tacheId() {
        if (tacheId == null) {
            setTacheId(new LongFilter());
        }
        return tacheId;
    }

    public void setTacheId(LongFilter tacheId) {
        this.tacheId = tacheId;
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
        final NotificationCriteria that = (NotificationCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(titre, that.titre) &&
            Objects.equals(message, that.message) &&
            Objects.equals(dateCreation, that.dateCreation) &&
            Objects.equals(dateLecture, that.dateLecture) &&
            Objects.equals(lu, that.lu) &&
            Objects.equals(type, that.type) &&
            Objects.equals(utilisateurId, that.utilisateurId) &&
            Objects.equals(demandeId, that.demandeId) &&
            Objects.equals(tacheId, that.tacheId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, message, dateCreation, dateLecture, lu, type, utilisateurId, demandeId, tacheId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NotificationCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitre().map(f -> "titre=" + f + ", ").orElse("") +
            optionalMessage().map(f -> "message=" + f + ", ").orElse("") +
            optionalDateCreation().map(f -> "dateCreation=" + f + ", ").orElse("") +
            optionalDateLecture().map(f -> "dateLecture=" + f + ", ").orElse("") +
            optionalLu().map(f -> "lu=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalUtilisateurId().map(f -> "utilisateurId=" + f + ", ").orElse("") +
            optionalDemandeId().map(f -> "demandeId=" + f + ", ").orElse("") +
            optionalTacheId().map(f -> "tacheId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
