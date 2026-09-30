package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    /**
     * {@code profil.authorities} (not just {@code profil}) has to be in the graph: AdminUserDTO
     * reads the profil's own authorities to expose them, and it does so after this method returned,
     * i.e. outside the transaction - a lazily-fetched collection would throw
     * LazyInitializationException there.
     */
    // « agent » figure dans le graphe : un graphe de chargement rend paresseux tout ce qu il ne
    // nomme pas, et la fiche du compte lit le rattachement une fois l entite detachee.
    @EntityGraph(attributePaths = { "authorities", "profil.authorities", "agent" })
    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE, unless = "#result == null")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE, unless = "#result == null")
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);

    Page<User> findAllByIdNotNullAndActivatedIsTrue(Pageable pageable);

    List<User> findAllByAuthoritiesNameAndActivatedIsTrue(String authorityName);

    boolean existsByProfilId(Long profilId);

    /** Les comptes portant ce profil, pour leur repercuter un changement de droits. */
    @EntityGraph(attributePaths = "authorities")
    List<User> findAllByProfilId(Long profilId);

    /** Le compte rattache a cet agent, s'il en existe un : un agent n'a qu'un acces. */
    Optional<User> findOneByAgentId(Long agentId);

    /**
     * Combien d'agents portent ce profil.
     *
     * <p>Comptee plutot que deduite de {@link #findAllByProfilId} : afficher une liste de
     * profils chargerait autrement tous les utilisateurs de chacun pour n'en montrer que le
     * nombre.
     */
    long countByProfilId(Long profilId);
}
