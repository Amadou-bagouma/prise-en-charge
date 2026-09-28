package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.NotificationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Notification}.
 */
public interface NotificationService {
    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    NotificationDTO save(NotificationDTO notificationDTO);

    /**
     * Updates a notification.
     *
     * @param notificationDTO the entity to update.
     * @return the persisted entity.
     */
    NotificationDTO update(NotificationDTO notificationDTO);

    /**
     * Partially updates a notification.
     *
     * @param notificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO);

    /**
     * Get all the notifications with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" notification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<NotificationDTO> findOne(Long id);

    /**
     * Marque une notification comme lue.
     *
     * <p>Idempotent : une notification deja lue garde sa date de lecture d'origine. Relire une
     * notification ne doit pas reecrire le moment ou l'agent en a pris connaissance.
     *
     * @param id la notification.
     * @return la notification mise a jour, vide si elle n'existe pas.
     */
    Optional<NotificationDTO> marquerLue(Long id);

    /**
     * Marque lues toutes les notifications de l'agent courant.
     *
     * @return le nombre de notifications qui etaient encore non lues.
     */
    int marquerToutLu();

    /**
     * Delete the "id" notification.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
