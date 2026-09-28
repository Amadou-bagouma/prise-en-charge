package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Un type de soin pris en charge.
 *
 * <p>Les quatre categories imprimees sur l'imprime officiel CNSS « Prise en charge des soins
 * medicaux » (consultations, examens medicaux, intervention chirurgicale, hospitalisation) sont
 * livrees comme donnees de reference. Le referentiel reste ouvert : l'administration peut en
 * ajouter d'autres sans passer par une livraison.
 *
 * <p>Le {@code code} est la cle stable : il ne change pas quand le libelle est reformule, et
 * c'est lui que le rapport PDF interroge pour cocher les cases de l'imprime officiel. Les
 * categories officielles portent donc un code fige.
 */
@Entity
@Table(name = "type_soin")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TypeSoin extends AbstractAuditingEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** Codes des categories imprimees sur l'imprime officiel. */
    public static final String CODE_CONSULTATIONS = "CONSULTATIONS";
    public static final String CODE_EXAMENS_MEDICAUX = "EXAMENS_MEDICAUX";
    public static final String CODE_INTERVENTION_CHIRURGICALE = "INTERVENTION_CHIRURGICALE";
    public static final String CODE_HOSPITALISATION = "HOSPITALISATION";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Le code doit etre en majuscules, sans espace ni accent.")
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 100)
    @Column(name = "libelle", length = 100, nullable = false, unique = true)
    private String libelle;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Rang d'affichage dans les listes et sur l'imprime.
     *
     * <p>Un referentiel medical ne se lit pas par ordre alphabetique : l'ordre de l'imprime
     * officiel fait foi, et une nouvelle categorie se glisse a sa place.
     */
    @NotNull
    @Column(name = "ordre", nullable = false)
    private Integer ordre = 0;

    /**
     * Un type retire du service reste sur les dossiers ou il a servi, mais ne se propose plus
     * a la saisie. On ne reecrit pas l'histoire d'un dossier deja instruit.
     */
    @NotNull
    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    @ManyToMany(mappedBy = "typeSoins")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "agent", "ayantDroit", "typeSoins", "etablissementSante" }, allowSetters = true)
    private Set<DemandePriseEnCharge> demandes = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public TypeSoin id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public TypeSoin code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return this.libelle;
    }

    public TypeSoin libelle(String libelle) {
        this.setLibelle(libelle);
        return this;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getDescription() {
        return this.description;
    }

    public TypeSoin description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrdre() {
        return this.ordre;
    }

    public TypeSoin ordre(Integer ordre) {
        this.setOrdre(ordre);
        return this;
    }

    public void setOrdre(Integer ordre) {
        this.ordre = ordre;
    }

    public Boolean getActif() {
        return this.actif;
    }

    public TypeSoin actif(Boolean actif) {
        this.setActif(actif);
        return this;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public Set<DemandePriseEnCharge> getDemandes() {
        return this.demandes;
    }

    public void setDemandes(Set<DemandePriseEnCharge> demandes) {
        this.demandes = demandes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TypeSoin)) {
            return false;
        }
        return getId() != null && getId().equals(((TypeSoin) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TypeSoin{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelle='" + getLibelle() + "'" +
            ", ordre=" + getOrdre() +
            ", actif='" + getActif() + "'" +
            "}";
    }
}
