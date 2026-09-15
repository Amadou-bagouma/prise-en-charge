package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mycompany.myapp.domain.enumeration.PrioriteDemande;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A DemandePriseEnCharge.
 */
@Entity
@Table(name = "demande_prise_en_charge")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DemandePriseEnCharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;

    @NotNull
    @Column(name = "date_creation", nullable = false)
    private Instant dateCreation;

    @Column(name = "date_modification")
    private Instant dateModification;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type_beneficiaire", nullable = false)
    private TypeBeneficiaire typeBeneficiaire;

    @Column(name = "description")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutDemande statut;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priorite", nullable = false)
    private PrioriteDemande priorite;

    @Column(name = "date_assignation")
    private Instant dateAssignation;

    @Column(name = "date_echeance")
    private Instant dateEcheance;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "observation")
    private String observation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "direction", "gestion", "user" }, allowSetters = true)
    private Agent agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "agent" }, allowSetters = true)
    private AyantDroit ayantDroit;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @BatchSize(size = 20)
    @JoinTable(
        name = "rel_demande_prise_en_charge__type_soin",
        joinColumns = @JoinColumn(name = "demande_prise_en_charge_id"),
        inverseJoinColumns = @JoinColumn(name = "type_soin_id")
    )
    private Set<TypeSoin> typeSoins = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private EtablissementSante etablissementSante;

    @ManyToOne(optional = false)
    @NotNull
    private User gestionnaireCreateur;

    @ManyToOne(fetch = FetchType.LAZY)
    private User assigneA;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DemandePriseEnCharge id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return this.reference;
    }

    public DemandePriseEnCharge reference(String reference) {
        this.setReference(reference);
        return this;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getDateCreation() {
        return this.dateCreation;
    }

    public DemandePriseEnCharge dateCreation(Instant dateCreation) {
        this.setDateCreation(dateCreation);
        return this;
    }

    public void setDateCreation(Instant dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Instant getDateModification() {
        return this.dateModification;
    }

    public DemandePriseEnCharge dateModification(Instant dateModification) {
        this.setDateModification(dateModification);
        return this;
    }

    public void setDateModification(Instant dateModification) {
        this.dateModification = dateModification;
    }

    public TypeBeneficiaire getTypeBeneficiaire() {
        return this.typeBeneficiaire;
    }

    public DemandePriseEnCharge typeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.setTypeBeneficiaire(typeBeneficiaire);
        return this;
    }

    public void setTypeBeneficiaire(TypeBeneficiaire typeBeneficiaire) {
        this.typeBeneficiaire = typeBeneficiaire;
    }

    public String getDescription() {
        return this.description;
    }

    public DemandePriseEnCharge description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatutDemande getStatut() {
        return this.statut;
    }

    public DemandePriseEnCharge statut(StatutDemande statut) {
        this.setStatut(statut);
        return this;
    }

    public void setStatut(StatutDemande statut) {
        this.statut = statut;
    }

    public PrioriteDemande getPriorite() {
        return this.priorite;
    }

    public DemandePriseEnCharge priorite(PrioriteDemande priorite) {
        this.setPriorite(priorite);
        return this;
    }

    public void setPriorite(PrioriteDemande priorite) {
        this.priorite = priorite;
    }

    public Instant getDateAssignation() {
        return this.dateAssignation;
    }

    public DemandePriseEnCharge dateAssignation(Instant dateAssignation) {
        this.setDateAssignation(dateAssignation);
        return this;
    }

    public void setDateAssignation(Instant dateAssignation) {
        this.dateAssignation = dateAssignation;
    }

    public Instant getDateEcheance() {
        return this.dateEcheance;
    }

    public DemandePriseEnCharge dateEcheance(Instant dateEcheance) {
        this.setDateEcheance(dateEcheance);
        return this;
    }

    public void setDateEcheance(Instant dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public String getMotifRejet() {
        return this.motifRejet;
    }

    public DemandePriseEnCharge motifRejet(String motifRejet) {
        this.setMotifRejet(motifRejet);
        return this;
    }

    public void setMotifRejet(String motifRejet) {
        this.motifRejet = motifRejet;
    }

    public String getObservation() {
        return this.observation;
    }

    public DemandePriseEnCharge observation(String observation) {
        this.setObservation(observation);
        return this;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Agent getAgent() {
        return this.agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public DemandePriseEnCharge agent(Agent agent) {
        this.setAgent(agent);
        return this;
    }

    public AyantDroit getAyantDroit() {
        return this.ayantDroit;
    }

    public void setAyantDroit(AyantDroit ayantDroit) {
        this.ayantDroit = ayantDroit;
    }

    public DemandePriseEnCharge ayantDroit(AyantDroit ayantDroit) {
        this.setAyantDroit(ayantDroit);
        return this;
    }

    public Set<TypeSoin> getTypeSoins() {
        return this.typeSoins;
    }

    public void setTypeSoins(Set<TypeSoin> typeSoins) {
        this.typeSoins = typeSoins;
    }

    public DemandePriseEnCharge typeSoins(Set<TypeSoin> typeSoins) {
        this.setTypeSoins(typeSoins);
        return this;
    }

    public DemandePriseEnCharge addTypeSoin(TypeSoin typeSoin) {
        this.typeSoins.add(typeSoin);
        return this;
    }

    public DemandePriseEnCharge removeTypeSoin(TypeSoin typeSoin) {
        this.typeSoins.remove(typeSoin);
        return this;
    }

    public EtablissementSante getEtablissementSante() {
        return this.etablissementSante;
    }

    public void setEtablissementSante(EtablissementSante etablissementSante) {
        this.etablissementSante = etablissementSante;
    }

    public DemandePriseEnCharge etablissementSante(EtablissementSante etablissementSante) {
        this.setEtablissementSante(etablissementSante);
        return this;
    }

    public User getGestionnaireCreateur() {
        return this.gestionnaireCreateur;
    }

    public void setGestionnaireCreateur(User user) {
        this.gestionnaireCreateur = user;
    }

    public DemandePriseEnCharge gestionnaireCreateur(User user) {
        this.setGestionnaireCreateur(user);
        return this;
    }

    public User getAssigneA() {
        return this.assigneA;
    }

    public void setAssigneA(User user) {
        this.assigneA = user;
    }

    public DemandePriseEnCharge assigneA(User user) {
        this.setAssigneA(user);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DemandePriseEnCharge)) {
            return false;
        }
        return getId() != null && getId().equals(((DemandePriseEnCharge) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DemandePriseEnCharge{" +
            "id=" + getId() +
            ", reference='" + getReference() + "'" +
            ", dateCreation='" + getDateCreation() + "'" +
            ", dateModification='" + getDateModification() + "'" +
            ", typeBeneficiaire='" + getTypeBeneficiaire() + "'" +
            ", description='" + getDescription() + "'" +
            ", statut='" + getStatut() + "'" +
            ", priorite='" + getPriorite() + "'" +
            ", dateAssignation='" + getDateAssignation() + "'" +
            ", dateEcheance='" + getDateEcheance() + "'" +
            ", motifRejet='" + getMotifRejet() + "'" +
            ", observation='" + getObservation() + "'" +
            "}";
    }
}
