package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TypeSoin;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TypeSoin entity.
 */
@Repository
public interface TypeSoinRepository extends JpaRepository<TypeSoin, Long>, JpaSpecificationExecutor<TypeSoin> {
    Optional<TypeSoin> findOneByCode(String code);

    List<TypeSoin> findAllByActifIsTrueOrderByOrdreAscLibelleAsc();

    List<TypeSoin> findAllByIdIn(Set<Long> ids);

    /** Un type encore porte par un dossier ne peut pas disparaitre du referentiel. */
    @Query("select count(d) > 0 from DemandePriseEnCharge d join d.typeSoins t where t.id = :typeSoinId")
    boolean isUtiliseParUneDemande(@Param("typeSoinId") Long typeSoinId);
}
