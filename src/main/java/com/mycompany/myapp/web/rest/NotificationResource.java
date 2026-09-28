package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.NotificationRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.NotificationQueryService;
import com.mycompany.myapp.service.NotificationService;
import com.mycompany.myapp.service.criteria.NotificationCriteria;
import com.mycompany.myapp.service.dto.NotificationDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Notification}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final NotificationQueryService notificationQueryService;

    private final UserRepository userRepository;

    public NotificationResource(
        NotificationService notificationService,
        NotificationRepository notificationRepository,
        NotificationQueryService notificationQueryService,
        UserRepository userRepository
    ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.notificationQueryService = notificationQueryService;
        this.userRepository = userRepository;
    }

    /**
     * Restricts the given criteria to the notifications addressed to the currently authenticated user,
     * unless that user has the {@code ROLE_ADMIN} authority.
     */
    private NotificationCriteria restrictToCurrentUser(NotificationCriteria criteria) {
        if (SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            return criteria;
        }
        Long currentUserId = SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));

        NotificationCriteria restricted = criteria != null ? criteria.copy() : new NotificationCriteria();
        LongFilter utilisateurIdFilter = new LongFilter();
        utilisateurIdFilter.setEquals(currentUserId);
        restricted.setUtilisateurId(utilisateurIdFilter);
        return restricted;
    }

    /**
     * Restreint les criteres aux notifications adressees a l'agent authentifie, sans exception.
     *
     * <p>Contrairement a {@link #restrictToCurrentUser}, un administrateur n'y echappe pas :
     * c'est ce que demande la boite de reception, qui est personnelle. Sans cela, un
     * administrateur y verrait les notifications de tous les agents alors que « tout marquer
     * comme lu » ne solde que les siennes, et l'ecran se contredirait.
     */
    private NotificationCriteria restrictToMine(NotificationCriteria criteria) {
        NotificationCriteria restricted = criteria != null ? criteria.copy() : new NotificationCriteria();
        LongFilter utilisateurIdFilter = new LongFilter();
        utilisateurIdFilter.setEquals(currentUserId());
        restricted.setUtilisateurId(utilisateurIdFilter);
        return restricted;
    }

    private Long currentUserId() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
    }

    /**
     * Restricts write access to a notification to its recipient, or an admin. A missing notification
     * is let through so the caller's own not-found / idempotent-delete handling still applies.
     */
    private void requireRecipientOrAdmin(Long notificationId) {
        if (SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            return;
        }
        Long currentUserId = SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
        boolean allowed = notificationRepository
            .findById(notificationId)
            .map(
                (Notification notification) ->
                    notification.getUtilisateur() != null && currentUserId.equals(notification.getUtilisateur().getId())
            )
            .orElse(true);
        if (!allowed) {
            throw new AccessDeniedException("Seul le destinataire de cette notification (ou un administrateur) peut la modifier");
        }
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationDTO = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/notifications/" + notificationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notification : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        requireRecipientOrAdmin(id);

        notificationDTO = notificationService.update(notificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(notificationDTO);
    }

    /**
     * {@code PATCH  /notifications/:id} : Partial updates given fields of an existing notification, field will ignore if it is null
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<NotificationDTO> partialUpdateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notification partially : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!notificationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        requireRecipientOrAdmin(id);

        Optional<NotificationDTO> result = notificationService.partialUpdate(notificationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /notifications} : get all the Notifications.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Notifications in body.
     */
    /**
     * {@code PUT /notifications/:id/lue} : marque une notification comme lue.
     *
     * <p>Une operation a part, et non un PATCH generique : marquer lu n'est pas modifier le
     * contenu d'une notification, et le client n'a pas a connaitre la forme de la donnee pour
     * le faire. Reserve au destinataire, comme les autres ecritures.
     */
    @PutMapping("/{id}/lue")
    public ResponseEntity<NotificationDTO> marquerLue(@PathVariable("id") Long id) {
        LOG.debug("REST request to mark Notification as read : {}", id);
        requireRecipientOrAdmin(id);
        return ResponseUtil.wrapOrNotFound(notificationService.marquerLue(id));
    }

    /**
     * {@code PUT /notifications/lire-tout} : marque lues toutes les notifications de l'agent.
     *
     * <p>Toujours celles de l'agent authentifie : l'identifiant vient du jeton, jamais du
     * client, pour qu'on ne puisse pas solder la boite de quelqu'un d'autre.
     *
     * @return le nombre de notifications qui etaient encore non lues.
     */
    @PutMapping("/lire-tout")
    public ResponseEntity<Integer> marquerToutLu() {
        LOG.debug("REST request to mark all Notifications as read");
        return ResponseEntity.ok().body(notificationService.marquerToutLu());
    }

    @GetMapping("")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
        NotificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "mesNotifications", required = false) Boolean mesNotifications
    ) {
        LOG.debug("REST request to get Notifications by criteria: {}", criteria);

        criteria = Boolean.TRUE.equals(mesNotifications) ? restrictToMine(criteria) : restrictToCurrentUser(criteria);
        Page<NotificationDTO> page = notificationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications/count} : count all the notifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countNotifications(
        NotificationCriteria criteria,
        @RequestParam(name = "mesNotifications", required = false) Boolean mesNotifications
    ) {
        LOG.debug("REST request to count Notifications by criteria: {}", criteria);
        criteria = Boolean.TRUE.equals(mesNotifications) ? restrictToMine(criteria) : restrictToCurrentUser(criteria);
        return ResponseEntity.ok().body(notificationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notification : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            Long currentUserId = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
            notificationDTO = notificationDTO.filter(
                dto -> dto.getUtilisateur() != null && currentUserId.equals(dto.getUtilisateur().getId())
            );
        }
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notification : {}", id);
        requireRecipientOrAdmin(id);
        notificationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
