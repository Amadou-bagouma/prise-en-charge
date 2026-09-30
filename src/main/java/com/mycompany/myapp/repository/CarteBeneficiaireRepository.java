package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.CarteBeneficiaire;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CarteBeneficiaire entity.
 */
@Repository
public interface CarteBeneficiaireRepository extends JpaRepository<CarteBeneficiaire, Long>, JpaSpecificationExecutor<CarteBeneficiaire> {
    default Optional<CarteBeneficiaire> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<CarteBeneficiaire> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<CarteBeneficiaire> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select carteBeneficiaire from CarteBeneficiaire carteBeneficiaire left join fetch carteBeneficiaire.agent left join fetch carteBeneficiaire.ayantDroit",
        countQuery = "select count(carteBeneficiaire) from CarteBeneficiaire carteBeneficiaire"
    )
    Page<CarteBeneficiaire> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select carteBeneficiaire from CarteBeneficiaire carteBeneficiaire left join fetch carteBeneficiaire.agent left join fetch carteBeneficiaire.ayantDroit"
    )
    List<CarteBeneficiaire> findAllWithToOneRelationships();

    @Query(
        "select carteBeneficiaire from CarteBeneficiaire carteBeneficiaire left join fetch carteBeneficiaire.agent left join fetch carteBeneficiaire.ayantDroit where carteBeneficiaire.id =:id"
    )
    Optional<CarteBeneficiaire> findOneWithToOneRelationships(@Param("id") Long id);

    @Query(value = "select nextval('carte_beneficiaire_numero_seq')", nativeQuery = true)
    long nextNumeroSequenceValue();

    /** Les cartes deja etablies pour cet agent, de la plus recente a la plus ancienne. */
    List<CarteBeneficiaire> findByAgentIdOrderByDateFinValiditeDesc(Long agentId);

    /** Les cartes deja etablies pour cet ayant droit, de la plus recente a la plus ancienne. */
    List<CarteBeneficiaire> findByAyantDroitIdOrderByDateFinValiditeDesc(Long ayantDroitId);
}
