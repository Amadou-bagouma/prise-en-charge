package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.AyantDroit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AyantDroit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AyantDroitRepository extends JpaRepository<AyantDroit, Long>, JpaSpecificationExecutor<AyantDroit> {}
