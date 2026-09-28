package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    @Query("select notification from Notification notification where notification.utilisateur.login = ?#{authentication.name}")
    List<Notification> findByUtilisateurIsCurrentUser();

    default Optional<Notification> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Notification> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Notification> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select notification from Notification notification left join fetch notification.utilisateur left join fetch notification.demande left join fetch notification.tache",
        countQuery = "select count(notification) from Notification notification"
    )
    Page<Notification> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select notification from Notification notification left join fetch notification.utilisateur left join fetch notification.demande left join fetch notification.tache"
    )
    List<Notification> findAllWithToOneRelationships();

    @Query(
        "select notification from Notification notification left join fetch notification.utilisateur left join fetch notification.demande left join fetch notification.tache where notification.id =:id"
    )
    Optional<Notification> findOneWithToOneRelationships(@Param("id") Long id);

    /**
     * Marque lues toutes les notifications encore non lues d'un agent.
     *
     * <p>En une requete plutot qu'en chargeant chaque ligne : « tout marquer comme lu » sur une
     * boite qui en compte des centaines ne doit pas se payer en centaines d'allers-retours.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        "update Notification n set n.lu = true, n.dateLecture = :dateLecture where n.utilisateur.id = :utilisateurId and (n.lu = false or n.lu is null)"
    )
    int marquerToutLu(@Param("utilisateurId") Long utilisateurId, @Param("dateLecture") Instant dateLecture);
}
