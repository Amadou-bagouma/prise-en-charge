package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Agent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Agent entity.
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {
    @Query("select agent from Agent agent where agent.user.login = ?#{authentication.name}")
    List<Agent> findByUserIsCurrentUser();

    default Optional<Agent> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Agent> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Agent> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select agent from Agent agent left join fetch agent.direction left join fetch agent.gestion left join fetch agent.user",
        countQuery = "select count(agent) from Agent agent"
    )
    Page<Agent> findAllWithToOneRelationships(Pageable pageable);

    @Query("select agent from Agent agent left join fetch agent.direction left join fetch agent.gestion left join fetch agent.user")
    List<Agent> findAllWithToOneRelationships();

    @Query(
        "select agent from Agent agent left join fetch agent.direction left join fetch agent.gestion left join fetch agent.user where agent.id =:id"
    )
    Optional<Agent> findOneWithToOneRelationships(@Param("id") Long id);
}
