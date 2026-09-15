package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DemandePriseEnCharge entity.
 */
@Repository
public interface DemandePriseEnChargeRepository
    extends JpaRepository<DemandePriseEnCharge, Long>, JpaSpecificationExecutor<DemandePriseEnCharge>
{
    @Query(
        "select demandePriseEnCharge from DemandePriseEnCharge demandePriseEnCharge where demandePriseEnCharge.gestionnaireCreateur.login = ?#{authentication.name}"
    )
    List<DemandePriseEnCharge> findByGestionnaireCreateurIsCurrentUser();

    @Query(
        "select demandePriseEnCharge from DemandePriseEnCharge demandePriseEnCharge where demandePriseEnCharge.assigneA.login = ?#{authentication.name}"
    )
    List<DemandePriseEnCharge> findByAssigneAIsCurrentUser();

    List<DemandePriseEnCharge> findByStatut(StatutDemande statut);

    default Optional<DemandePriseEnCharge> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<DemandePriseEnCharge> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<DemandePriseEnCharge> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select demandePriseEnCharge from DemandePriseEnCharge demandePriseEnCharge left join fetch demandePriseEnCharge.agent left join fetch demandePriseEnCharge.ayantDroit left join fetch demandePriseEnCharge.etablissementSante left join fetch demandePriseEnCharge.gestionnaireCreateur left join fetch demandePriseEnCharge.assigneA",
        countQuery = "select count(demandePriseEnCharge) from DemandePriseEnCharge demandePriseEnCharge"
    )
    Page<DemandePriseEnCharge> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select demandePriseEnCharge from DemandePriseEnCharge demandePriseEnCharge left join fetch demandePriseEnCharge.agent left join fetch demandePriseEnCharge.ayantDroit left join fetch demandePriseEnCharge.etablissementSante left join fetch demandePriseEnCharge.gestionnaireCreateur left join fetch demandePriseEnCharge.assigneA"
    )
    List<DemandePriseEnCharge> findAllWithToOneRelationships();

    @Query(
        "select demandePriseEnCharge from DemandePriseEnCharge demandePriseEnCharge left join fetch demandePriseEnCharge.agent left join fetch demandePriseEnCharge.ayantDroit left join fetch demandePriseEnCharge.typeSoins left join fetch demandePriseEnCharge.etablissementSante left join fetch demandePriseEnCharge.gestionnaireCreateur left join fetch demandePriseEnCharge.assigneA where demandePriseEnCharge.id =:id"
    )
    Optional<DemandePriseEnCharge> findOneWithToOneRelationships(@Param("id") Long id);
}
