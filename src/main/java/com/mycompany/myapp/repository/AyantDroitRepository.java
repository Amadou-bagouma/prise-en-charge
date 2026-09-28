package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AyantDroit;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AyantDroit entity.
 */
@Repository
public interface AyantDroitRepository extends JpaRepository<AyantDroit, Long>, JpaSpecificationExecutor<AyantDroit> {
    default Optional<AyantDroit> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<AyantDroit> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<AyantDroit> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select ayantDroit from AyantDroit ayantDroit left join fetch ayantDroit.agent",
        countQuery = "select count(ayantDroit) from AyantDroit ayantDroit"
    )
    Page<AyantDroit> findAllWithToOneRelationships(Pageable pageable);

    @Query("select ayantDroit from AyantDroit ayantDroit left join fetch ayantDroit.agent")
    List<AyantDroit> findAllWithToOneRelationships();

    @Query("select ayantDroit from AyantDroit ayantDroit left join fetch ayantDroit.agent where ayantDroit.id =:id")
    Optional<AyantDroit> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Le plus grand code deja attribue pour un prefixe donne.
     *
     * <p>Le prefixe vaut « matricule + chiffre de type » : la sequence repart donc de zero pour
     * chaque agent et chaque famille de lien. Conjoints et enfants ne partagent pas la meme
     * serie, mais enfants et « autres » si, puisqu'ils partagent le chiffre 3.
     *
     * <p>Le tri est lexicographique, ce qui est exact ici parce que la sequence est sur deux
     * chiffres a longueur fixe : « 09 » precede bien « 10 ».
     */
    /** Les ayants droit d'un agent, pour repercuter le statut de celui-ci. */
    List<AyantDroit> findByAgentId(Long agentId);

    @Query("select max(a.codeAyantDroit) from AyantDroit a where a.codeAyantDroit like concat(:prefixe, '%')")
    Optional<String> trouverDernierCode(@Param("prefixe") String prefixe);
}
