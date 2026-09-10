package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.TacheQueryService;
import com.mycompany.myapp.service.TacheService;
import com.mycompany.myapp.service.criteria.TacheCriteria;
import com.mycompany.myapp.service.dto.TacheDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Tache}.
 */
@RestController
@RequestMapping("/api/taches")
public class TacheResource {

    private static final Logger LOG = LoggerFactory.getLogger(TacheResource.class);

    private static final String ENTITY_NAME = "tache";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final TacheService tacheService;

    private final TacheRepository tacheRepository;

    private final TacheQueryService tacheQueryService;

    private final UserRepository userRepository;

    public TacheResource(
        TacheService tacheService,
        TacheRepository tacheRepository,
        TacheQueryService tacheQueryService,
        UserRepository userRepository
    ) {
        this.tacheService = tacheService;
        this.tacheRepository = tacheRepository;
        this.tacheQueryService = tacheQueryService;
        this.userRepository = userRepository;
    }

    /**
     * Restricts the given criteria to the tasks assigned to the currently authenticated user,
     * unless that user has the {@code ROLE_ADMIN} authority.
     */
    private TacheCriteria restrictToCurrentUser(TacheCriteria criteria) {
        if (SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            return criteria;
        }
        Long currentUserId = SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .map(User::getId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));

        TacheCriteria restricted = criteria != null ? criteria.copy() : new TacheCriteria();
        LongFilter utilisateurIdFilter = new LongFilter();
        utilisateurIdFilter.setEquals(currentUserId);
        restricted.setUtilisateurId(utilisateurIdFilter);
        return restricted;
    }

    /**
     * {@code POST  /taches} : Create a new tache.
     *
     * @param tacheDTO the tacheDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tacheDTO, or with status {@code 400 (Bad Request)} if the tache has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TacheDTO> createTache(@Valid @RequestBody TacheDTO tacheDTO) throws URISyntaxException {
        LOG.debug("REST request to save Tache : {}", tacheDTO);
        if (tacheDTO.getId() != null) {
            throw new BadRequestAlertException("A new tache cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tacheDTO = tacheService.save(tacheDTO);
        return ResponseEntity.created(new URI("/api/taches/" + tacheDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tacheDTO.getId().toString()))
            .body(tacheDTO);
    }

    /**
     * {@code PUT  /taches/:id} : Updates an existing tache.
     *
     * @param id the id of the tacheDTO to save.
     * @param tacheDTO the tacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tacheDTO,
     * or with status {@code 400 (Bad Request)} if the tacheDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TacheDTO> updateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TacheDTO tacheDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Tache : {}, {}", id, tacheDTO);
        if (tacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tacheRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tacheDTO = tacheService.update(tacheDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tacheDTO.getId().toString()))
            .body(tacheDTO);
    }

    /**
     * {@code PATCH  /taches/:id} : Partial updates given fields of an existing tache, field will ignore if it is null
     *
     * @param id the id of the tacheDTO to save.
     * @param tacheDTO the tacheDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tacheDTO,
     * or with status {@code 400 (Bad Request)} if the tacheDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tacheDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tacheDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TacheDTO> partialUpdateTache(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TacheDTO tacheDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tache partially : {}, {}", id, tacheDTO);
        if (tacheDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tacheDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tacheRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TacheDTO> result = tacheService.partialUpdate(tacheDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tacheDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /taches} : get all the Taches.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Taches in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TacheDTO>> getAllTaches(
        TacheCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Taches by criteria: {}", criteria);

        criteria = restrictToCurrentUser(criteria);
        Page<TacheDTO> page = tacheQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /taches/count} : count all the taches.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaches(TacheCriteria criteria) {
        LOG.debug("REST request to count Taches by criteria: {}", criteria);
        criteria = restrictToCurrentUser(criteria);
        return ResponseEntity.ok().body(tacheQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /taches/:id} : get the "id" tache.
     *
     * @param id the id of the tacheDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tacheDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TacheDTO> getTache(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Tache : {}", id);
        Optional<TacheDTO> tacheDTO = tacheService.findOne(id);
        if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(AuthoritiesConstants.ADMIN)) {
            Long currentUserId = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .map(User::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
            tacheDTO = tacheDTO.filter(dto -> dto.getUtilisateur() != null && currentUserId.equals(dto.getUtilisateur().getId()));
        }
        return ResponseUtil.wrapOrNotFound(tacheDTO);
    }

    /**
     * {@code DELETE  /taches/:id} : delete the "id" tache.
     *
     * @param id the id of the tacheDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTache(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Tache : {}", id);
        tacheService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
