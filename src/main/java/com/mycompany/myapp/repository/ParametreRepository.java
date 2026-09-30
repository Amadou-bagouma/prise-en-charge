package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Parametre;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Parametre entity.
 */
@Repository
public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    /** Le reglage portant ce code, s'il existe. C'est par le code que le programme l'interroge. */
    Optional<Parametre> findOneByCode(String code);
}
