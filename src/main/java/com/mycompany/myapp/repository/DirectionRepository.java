package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Direction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Direction entity.
 */
@Repository
public interface DirectionRepository extends JpaRepository<Direction, Long>, JpaSpecificationExecutor<Direction> {
    default Optional<Direction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Direction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Direction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select direction from Direction direction left join fetch direction.region",
        countQuery = "select count(direction) from Direction direction"
    )
    Page<Direction> findAllWithToOneRelationships(Pageable pageable);

    @Query("select direction from Direction direction left join fetch direction.region")
    List<Direction> findAllWithToOneRelationships();

    @Query("select direction from Direction direction left join fetch direction.region where direction.id =:id")
    Optional<Direction> findOneWithToOneRelationships(@Param("id") Long id);
}
