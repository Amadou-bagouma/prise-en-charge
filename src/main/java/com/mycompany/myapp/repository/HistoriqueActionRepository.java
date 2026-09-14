package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.HistoriqueAction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the HistoriqueAction entity.
 */
@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long>, JpaSpecificationExecutor<HistoriqueAction> {
    @Query(
        "select historiqueAction from HistoriqueAction historiqueAction where historiqueAction.utilisateur.login = ?#{authentication.name}"
    )
    List<HistoriqueAction> findByUtilisateurIsCurrentUser();

    List<HistoriqueAction> findByDemandeIdOrderByDateActionAsc(Long demandeId);

    default Optional<HistoriqueAction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<HistoriqueAction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<HistoriqueAction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select historiqueAction from HistoriqueAction historiqueAction left join fetch historiqueAction.demande left join fetch historiqueAction.utilisateur",
        countQuery = "select count(historiqueAction) from HistoriqueAction historiqueAction"
    )
    Page<HistoriqueAction> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select historiqueAction from HistoriqueAction historiqueAction left join fetch historiqueAction.demande left join fetch historiqueAction.utilisateur"
    )
    List<HistoriqueAction> findAllWithToOneRelationships();

    @Query(
        "select historiqueAction from HistoriqueAction historiqueAction left join fetch historiqueAction.demande left join fetch historiqueAction.utilisateur where historiqueAction.id =:id"
    )
    Optional<HistoriqueAction> findOneWithToOneRelationships(@Param("id") Long id);
}
