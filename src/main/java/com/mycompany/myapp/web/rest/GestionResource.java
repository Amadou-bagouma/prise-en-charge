package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.GestionRepository;
import com.mycompany.myapp.service.GestionService;
import com.mycompany.myapp.service.dto.GestionDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Gestion}.
 */
@RestController
@RequestMapping("/api/gestions")
public class GestionResource {

    private static final Logger LOG = LoggerFactory.getLogger(GestionResource.class);

    private static final String ENTITY_NAME = "gestion";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final GestionService gestionService;

    private final GestionRepository gestionRepository;

    public GestionResource(GestionService gestionService, GestionRepository gestionRepository) {
        this.gestionService = gestionService;
        this.gestionRepository = gestionRepository;
    }

    /**
     * {@code POST  /gestions} : Create a new gestion.
     *
     * @param gestionDTO the gestionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new gestionDTO, or with status {@code 400 (Bad Request)} if the gestion has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<GestionDTO> createGestion(@Valid @RequestBody GestionDTO gestionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Gestion : {}", gestionDTO);
        if (gestionDTO.getId() != null) {
            throw new BadRequestAlertException("A new gestion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        gestionDTO = gestionService.save(gestionDTO);
        return ResponseEntity.created(new URI("/api/gestions/" + gestionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, gestionDTO.getId().toString()))
            .body(gestionDTO);
    }

    /**
     * {@code PUT  /gestions/:id} : Updates an existing gestion.
     *
     * @param id the id of the gestionDTO to save.
     * @param gestionDTO the gestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestionDTO,
     * or with status {@code 400 (Bad Request)} if the gestionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the gestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<GestionDTO> updateGestion(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GestionDTO gestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Gestion : {}, {}", id, gestionDTO);
        if (gestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        gestionDTO = gestionService.update(gestionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gestionDTO.getId().toString()))
            .body(gestionDTO);
    }

    /**
     * {@code PATCH  /gestions/:id} : Partial updates given fields of an existing gestion, field will ignore if it is null
     *
     * @param id the id of the gestionDTO to save.
     * @param gestionDTO the gestionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated gestionDTO,
     * or with status {@code 400 (Bad Request)} if the gestionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the gestionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the gestionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<GestionDTO> partialUpdateGestion(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GestionDTO gestionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Gestion partially : {}, {}", id, gestionDTO);
        if (gestionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, gestionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!gestionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GestionDTO> result = gestionService.partialUpdate(gestionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, gestionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /gestions} : get all the Gestions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Gestions in body.
     */
    @GetMapping("")
    public ResponseEntity<List<GestionDTO>> getAllGestions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Gestions");
        Page<GestionDTO> page = gestionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /gestions/:id} : get the "id" gestion.
     *
     * @param id the id of the gestionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the gestionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GestionDTO> getGestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Gestion : {}", id);
        Optional<GestionDTO> gestionDTO = gestionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(gestionDTO);
    }

    /**
     * {@code DELETE  /gestions/:id} : delete the "id" gestion.
     *
     * @param id the id of the gestionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGestion(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Gestion : {}", id);
        gestionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
