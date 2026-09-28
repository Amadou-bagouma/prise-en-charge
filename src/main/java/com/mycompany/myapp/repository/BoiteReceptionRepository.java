package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.BoiteReception;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the BoiteReception entity.
 */
@Repository
public interface BoiteReceptionRepository extends JpaRepository<BoiteReception, Long> {
    default Optional<BoiteReception> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<BoiteReception> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<BoiteReception> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select boiteReception from BoiteReception boiteReception left join fetch boiteReception.profil",
        countQuery = "select count(boiteReception) from BoiteReception boiteReception"
    )
    Page<BoiteReception> findAllWithToOneRelationships(Pageable pageable);

    @Query("select boiteReception from BoiteReception boiteReception left join fetch boiteReception.profil")
    List<BoiteReception> findAllWithToOneRelationships();

    @Query("select boiteReception from BoiteReception boiteReception left join fetch boiteReception.profil where boiteReception.id =:id")
    Optional<BoiteReception> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * La boite d'un profil, avec ses droits charges : le routage des taches lit les authorities du
     * profil juste apres, hors transaction ouverte dans le cas de l'appel REST.
     */
    @Query("select b from BoiteReception b left join fetch b.profil p left join fetch p.authorities where p.id = :profilId")
    Optional<BoiteReception> trouverParProfil(@Param("profilId") Long profilId);

    boolean existsByProfilId(Long profilId);

    /** Les profils qui n'ont pas encore de boite : la synchronisation leur en cree une. */
    @Query("select p.id from Profil p where p.id not in (select b.profil.id from BoiteReception b where b.profil is not null)")
    List<Long> trouverProfilsSansBoite();
}
