package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.service.BoiteReceptionService;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 */
@RestController
@RequestMapping("/api/boite-receptions")
public class BoiteReceptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(BoiteReceptionResource.class);

    private static final String ENTITY_NAME = "boiteReception";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final BoiteReceptionService boiteReceptionService;

    private final BoiteReceptionRepository boiteReceptionRepository;

    public BoiteReceptionResource(BoiteReceptionService boiteReceptionService, BoiteReceptionRepository boiteReceptionRepository) {
        this.boiteReceptionService = boiteReceptionService;
        this.boiteReceptionRepository = boiteReceptionRepository;
    }

    /**
     * {@code POST  /boite-receptions} : Create a new boiteReception.
     *
     * @param boiteReceptionDTO the boiteReceptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new boiteReceptionDTO, or with status {@code 400 (Bad Request)} if the boiteReception has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BoiteReceptionDTO> createBoiteReception(@Valid @RequestBody BoiteReceptionDTO boiteReceptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BoiteReception : {}", boiteReceptionDTO);
        if (boiteReceptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new boiteReception cannot already have an ID", ENTITY_NAME, "idexists");
        }
        boiteReceptionDTO = boiteReceptionService.save(boiteReceptionDTO);
        return ResponseEntity.created(new URI("/api/boite-receptions/" + boiteReceptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, boiteReceptionDTO.getId().toString()))
            .body(boiteReceptionDTO);
    }

    /**
     * {@code PUT  /boite-receptions/:id} : Updates an existing boiteReception.
     *
     * @param id the id of the boiteReceptionDTO to save.
     * @param boiteReceptionDTO the boiteReceptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boiteReceptionDTO,
     * or with status {@code 400 (Bad Request)} if the boiteReceptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the boiteReceptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoiteReceptionDTO> updateBoiteReception(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody BoiteReceptionDTO boiteReceptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BoiteReception : {}, {}", id, boiteReceptionDTO);
        if (boiteReceptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boiteReceptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boiteReceptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        boiteReceptionDTO = boiteReceptionService.update(boiteReceptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boiteReceptionDTO.getId().toString()))
            .body(boiteReceptionDTO);
    }

    /**
     * {@code PATCH  /boite-receptions/:id} : Partial updates given fields of an existing boiteReception, field will ignore if it is null
     *
     * @param id the id of the boiteReceptionDTO to save.
     * @param boiteReceptionDTO the boiteReceptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated boiteReceptionDTO,
     * or with status {@code 400 (Bad Request)} if the boiteReceptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the boiteReceptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the boiteReceptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BoiteReceptionDTO> partialUpdateBoiteReception(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody BoiteReceptionDTO boiteReceptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BoiteReception partially : {}, {}", id, boiteReceptionDTO);
        if (boiteReceptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, boiteReceptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!boiteReceptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BoiteReceptionDTO> result = boiteReceptionService.partialUpdate(boiteReceptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, boiteReceptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /boite-receptions} : get all the Boite Receptions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Boite Receptions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<BoiteReceptionDTO>> getAllBoiteReceptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of BoiteReceptions");
        Page<BoiteReceptionDTO> page;
        if (eagerload) {
            page = boiteReceptionService.findAllWithEagerRelationships(pageable);
        } else {
            page = boiteReceptionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /boite-receptions/:id} : get the "id" boiteReception.
     *
     * @param id the id of the boiteReceptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the boiteReceptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoiteReceptionDTO> getBoiteReception(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BoiteReception : {}", id);
        Optional<BoiteReceptionDTO> boiteReceptionDTO = boiteReceptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(boiteReceptionDTO);
    }

    /**
     * {@code DELETE  /boite-receptions/:id} : delete the "id" boiteReception.
     *
     * @param id the id of the boiteReceptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoiteReception(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BoiteReception : {}", id);
        boiteReceptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
