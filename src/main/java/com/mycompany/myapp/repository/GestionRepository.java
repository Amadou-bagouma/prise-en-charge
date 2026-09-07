package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Gestion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Gestion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GestionRepository extends JpaRepository<Gestion, Long> {}
