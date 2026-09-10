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
        value = "select boiteReception from BoiteReception boiteReception left join fetch boiteReception.utilisateur",
        countQuery = "select count(boiteReception) from BoiteReception boiteReception"
    )
    Page<BoiteReception> findAllWithToOneRelationships(Pageable pageable);

    @Query("select boiteReception from BoiteReception boiteReception left join fetch boiteReception.utilisateur")
    List<BoiteReception> findAllWithToOneRelationships();

    @Query(
        "select boiteReception from BoiteReception boiteReception left join fetch boiteReception.utilisateur where boiteReception.id =:id"
    )
    Optional<BoiteReception> findOneWithToOneRelationships(@Param("id") Long id);
}
