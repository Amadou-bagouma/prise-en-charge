package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import java.util.Collection;
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

    /** Tout ce qui est rattache a une demande, pour la supprimer avec elle. */
    List<Tache> findByDemandeId(Long demandeId);

    boolean existsByDemandeIdAndUtilisateurIdAndStatutNotIn(Long demandeId, Long utilisateurId, List<StatutTache> statuts);

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

    /**
     * Les taches adressees a une boite de reception : celles dont l'habilitation requise figure
     * parmi les droits du profil.
     *
     * <p>L'appelant ne doit jamais passer une collection vide - un profil sans droit ne voit
     * aucune tache, et {@code in ()} est refuse par le dialecte.
     */
    @Query(
        value = "select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur " +
            "where tache.droitRequis in :droits",
        countQuery = "select count(tache) from Tache tache where tache.droitRequis in :droits"
    )
    Page<Tache> trouverPourDroits(@Param("droits") Collection<String> droits, Pageable pageable);

    @Query(
        value = "select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur " +
            "where tache.droitRequis in :droits and tache.statut in :statutsTache",
        countQuery = "select count(tache) from Tache tache where tache.droitRequis in :droits and tache.statut in :statutsTache"
    )
    Page<Tache> trouverOuvertesPourDroits(
        @Param("droits") Collection<String> droits,
        @Param("statutsTache") Collection<StatutTache> statutsTache,
        Pageable pageable
    );

    long countByDroitRequisInAndLuFalse(Collection<String> droits);

    long countByDroitRequisInAndStatutIn(Collection<String> droits, Collection<StatutTache> statuts);

    /** Les memes decomptes pour un administrateur, dont la boite n'est bornee par aucun droit. */
    long countByLuFalse();

    long countByStatutIn(Collection<StatutTache> statuts);

    @Query(
        value = "select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur",
        countQuery = "select count(tache) from Tache tache"
    )
    Page<Tache> trouverToutes(Pageable pageable);

    @Query(
        value = "select tache from Tache tache left join fetch tache.demande left join fetch tache.utilisateur " +
            "where tache.statut in :statutsTache",
        countQuery = "select count(tache) from Tache tache where tache.statut in :statutsTache"
    )
    Page<Tache> trouverOuvertes(@Param("statutsTache") Collection<StatutTache> statutsTache, Pageable pageable);

    /** Les taches encore ouvertes adressees a un droit, pour la cloture d'une etape. */
    List<Tache> findByDemandeIdAndDroitRequisAndStatutNotIn(Long demandeId, String droitRequis, List<StatutTache> statuts);

    boolean existsByDemandeIdAndDroitRequisAndStatutNotIn(Long demandeId, String droitRequis, List<StatutTache> statuts);
}
