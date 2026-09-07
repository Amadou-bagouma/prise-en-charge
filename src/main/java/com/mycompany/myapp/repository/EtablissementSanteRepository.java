package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EtablissementSante;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EtablissementSante entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtablissementSanteRepository
    extends JpaRepository<EtablissementSante, Long>, JpaSpecificationExecutor<EtablissementSante> {}
