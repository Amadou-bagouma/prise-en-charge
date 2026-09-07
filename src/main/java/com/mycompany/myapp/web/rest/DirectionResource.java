package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DirectionRepository;
import com.mycompany.myapp.service.DirectionQueryService;
import com.mycompany.myapp.service.DirectionService;
import com.mycompany.myapp.service.criteria.DirectionCriteria;
import com.mycompany.myapp.service.dto.DirectionDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Direction}.
 */
@RestController
@RequestMapping("/api/directions")
public class DirectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(DirectionResource.class);

    private static final String ENTITY_NAME = "direction";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final DirectionService directionService;

    private final DirectionRepository directionRepository;

    private final DirectionQueryService directionQueryService;

    public DirectionResource(
        DirectionService directionService,
        DirectionRepository directionRepository,
        DirectionQueryService directionQueryService
    ) {
        this.directionService = directionService;
        this.directionRepository = directionRepository;
        this.directionQueryService = directionQueryService;
    }

    /**
     * {@code POST  /directions} : Create a new direction.
     *
     * @param directionDTO the directionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new directionDTO, or with status {@code 400 (Bad Request)} if the direction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DirectionDTO> createDirection(@Valid @RequestBody DirectionDTO directionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Direction : {}", directionDTO);
        if (directionDTO.getId() != null) {
            throw new BadRequestAlertException("A new direction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        directionDTO = directionService.save(directionDTO);
        return ResponseEntity.created(new URI("/api/directions/" + directionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, directionDTO.getId().toString()))
            .body(directionDTO);
    }

    /**
     * {@code PUT  /directions/:id} : Updates an existing direction.
     *
     * @param id the id of the directionDTO to save.
     * @param directionDTO the directionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directionDTO,
     * or with status {@code 400 (Bad Request)} if the directionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the directionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DirectionDTO> updateDirection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DirectionDTO directionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Direction : {}, {}", id, directionDTO);
        if (directionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!directionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        directionDTO = directionService.update(directionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, directionDTO.getId().toString()))
            .body(directionDTO);
    }

    /**
     * {@code PATCH  /directions/:id} : Partial updates given fields of an existing direction, field will ignore if it is null
     *
     * @param id the id of the directionDTO to save.
     * @param directionDTO the directionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated directionDTO,
     * or with status {@code 400 (Bad Request)} if the directionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the directionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the directionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DirectionDTO> partialUpdateDirection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DirectionDTO directionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Direction partially : {}, {}", id, directionDTO);
        if (directionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, directionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!directionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DirectionDTO> result = directionService.partialUpdate(directionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, directionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /directions} : get all the Directions.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Directions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DirectionDTO>> getAllDirections(
        DirectionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Directions by criteria: {}", criteria);

        Page<DirectionDTO> page = directionQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /directions/count} : count all the directions.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDirections(DirectionCriteria criteria) {
        LOG.debug("REST request to count Directions by criteria: {}", criteria);
        return ResponseEntity.ok().body(directionQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /directions/:id} : get the "id" direction.
     *
     * @param id the id of the directionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the directionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DirectionDTO> getDirection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Direction : {}", id);
        Optional<DirectionDTO> directionDTO = directionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(directionDTO);
    }

    /**
     * {@code DELETE  /directions/:id} : delete the "id" direction.
     *
     * @param id the id of the directionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Direction : {}", id);
        directionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
