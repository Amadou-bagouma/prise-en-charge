package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.PieceJustificative;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PieceJustificative entity.
 */
@Repository
public interface PieceJustificativeRepository extends JpaRepository<PieceJustificative, Long> {
    default Optional<PieceJustificative> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<PieceJustificative> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<PieceJustificative> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select pieceJustificative from PieceJustificative pieceJustificative left join fetch pieceJustificative.demande",
        countQuery = "select count(pieceJustificative) from PieceJustificative pieceJustificative"
    )
    Page<PieceJustificative> findAllWithToOneRelationships(Pageable pageable);

    @Query("select pieceJustificative from PieceJustificative pieceJustificative left join fetch pieceJustificative.demande")
    List<PieceJustificative> findAllWithToOneRelationships();

    @Query(
        "select pieceJustificative from PieceJustificative pieceJustificative left join fetch pieceJustificative.demande where pieceJustificative.id =:id"
    )
    Optional<PieceJustificative> findOneWithToOneRelationships(@Param("id") Long id);
}
