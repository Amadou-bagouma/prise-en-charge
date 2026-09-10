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
}
