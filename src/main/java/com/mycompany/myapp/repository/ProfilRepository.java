package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Profil;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Profil entity.
 */
@Repository
public interface ProfilRepository extends JpaRepository<Profil, Long> {
    /**
     * The profil auto-assigned to a user who self-registers via {@code POST /api/register}, who
     * cannot be allowed to pick their own profil. Must always resolve - seeded unconditionally by
     * {@code 20260920180000_add_profil.xml}, in every environment including production.
     */
    String PROFIL_PAR_DEFAUT = "Gestionnaire";

    Optional<Profil> findOneByNom(String nom);

    @EntityGraph(attributePaths = "authorities")
    @Query("select p from Profil p where p.id = :id")
    Optional<Profil> findOneWithAuthoritiesById(@org.springframework.data.repository.query.Param("id") Long id);

    @EntityGraph(attributePaths = "authorities")
    @Query("select p from Profil p where p.nom = :nom")
    Optional<Profil> findOneWithAuthoritiesByNom(@org.springframework.data.repository.query.Param("nom") String nom);
}
