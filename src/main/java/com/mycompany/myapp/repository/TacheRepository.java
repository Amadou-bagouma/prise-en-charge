package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tache entity.
 */
@Repository
public interface TacheRepository extends JpaRepository<Tache, Long>, JpaSpecificationExecutor<Tache> {
    @Query("select tache from Tache tache where tache.utilisateur.login = ?#{authentication.name}")
    List<Tache> findByUtilisateurIsCurrentUser();

    List<Tache> findByDemandeIdAndStatutNotIn(Long demandeId, List<StatutTache> statuts);

    default Optional<Tache> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Tache> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Tache> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur",
        countQuery = "select count(tache) from Tache tache"
    )
    Page<Tache> findAllWithToOneRelationships(Pageable pageable);

    @Query("select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur")
    List<Tache> findAllWithToOneRelationships();

    @Query("select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur where tache.id =:id")
    Optional<Tache> findOneWithToOneRelationships(@Param("id") Long id);
}
