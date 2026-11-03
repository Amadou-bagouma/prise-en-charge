package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Fonction;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Fonction entity.
 */
@Repository
public interface FonctionRepository extends JpaRepository<Fonction, Long> {
    Optional<Fonction> findOneByLibelleIgnoreCase(String libelle);

    /** Une fonction encore portee par un agent ne peut pas disparaitre du referentiel. */
    @Query("select count(a) > 0 from Agent a where a.fonction.id = :fonctionId")
    boolean isUtiliseeParUnAgent(@Param("fonctionId") Long fonctionId);
}
