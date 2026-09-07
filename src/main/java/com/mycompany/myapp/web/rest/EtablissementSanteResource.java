package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.EtablissementSanteRepository;
import com.mycompany.myapp.service.EtablissementSanteQueryService;
import com.mycompany.myapp.service.EtablissementSanteService;
import com.mycompany.myapp.service.criteria.EtablissementSanteCriteria;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.EtablissementSante}.
 */
@RestController
@RequestMapping("/api/etablissement-santes")
public class EtablissementSanteResource {

    private static final Logger LOG = LoggerFactory.getLogger(EtablissementSanteResource.class);

    private static final String ENTITY_NAME = "etablissementSante";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final EtablissementSanteService etablissementSanteService;

    private final EtablissementSanteRepository etablissementSanteRepository;

    private final EtablissementSanteQueryService etablissementSanteQueryService;

    public EtablissementSanteResource(
        EtablissementSanteService etablissementSanteService,
        EtablissementSanteRepository etablissementSanteRepository,
        EtablissementSanteQueryService etablissementSanteQueryService
    ) {
        this.etablissementSanteService = etablissementSanteService;
        this.etablissementSanteRepository = etablissementSanteRepository;
        this.etablissementSanteQueryService = etablissementSanteQueryService;
    }

    /**
     * {@code POST  /etablissement-santes} : Create a new etablissementSante.
     *
     * @param etablissementSanteDTO the etablissementSanteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new etablissementSanteDTO, or with status {@code 400 (Bad Request)} if the etablissementSante has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EtablissementSanteDTO> createEtablissementSante(@Valid @RequestBody EtablissementSanteDTO etablissementSanteDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EtablissementSante : {}", etablissementSanteDTO);
        if (etablissementSanteDTO.getId() != null) {
            throw new BadRequestAlertException("A new etablissementSante cannot already have an ID", ENTITY_NAME, "idexists");
        }
        etablissementSanteDTO = etablissementSanteService.save(etablissementSanteDTO);
        return ResponseEntity.created(new URI("/api/etablissement-santes/" + etablissementSanteDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, etablissementSanteDTO.getId().toString()))
            .body(etablissementSanteDTO);
    }

    /**
     * {@code PUT  /etablissement-santes/:id} : Updates an existing etablissementSante.
     *
     * @param id the id of the etablissementSanteDTO to save.
     * @param etablissementSanteDTO the etablissementSanteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etablissementSanteDTO,
     * or with status {@code 400 (Bad Request)} if the etablissementSanteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the etablissementSanteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EtablissementSanteDTO> updateEtablissementSante(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EtablissementSanteDTO etablissementSanteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EtablissementSante : {}, {}", id, etablissementSanteDTO);
        if (etablissementSanteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etablissementSanteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etablissementSanteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        etablissementSanteDTO = etablissementSanteService.update(etablissementSanteDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etablissementSanteDTO.getId().toString()))
            .body(etablissementSanteDTO);
    }

    /**
     * {@code PATCH  /etablissement-santes/:id} : Partial updates given fields of an existing etablissementSante, field will ignore if it is null
     *
     * @param id the id of the etablissementSanteDTO to save.
     * @param etablissementSanteDTO the etablissementSanteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated etablissementSanteDTO,
     * or with status {@code 400 (Bad Request)} if the etablissementSanteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the etablissementSanteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the etablissementSanteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EtablissementSanteDTO> partialUpdateEtablissementSante(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EtablissementSanteDTO etablissementSanteDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EtablissementSante partially : {}, {}", id, etablissementSanteDTO);
        if (etablissementSanteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, etablissementSanteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!etablissementSanteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EtablissementSanteDTO> result = etablissementSanteService.partialUpdate(etablissementSanteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, etablissementSanteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /etablissement-santes} : get all the Etablissement Santes.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Etablissement Santes in body.
     */
    @GetMapping("")
    public ResponseEntity<List<EtablissementSanteDTO>> getAllEtablissementSantes(
        EtablissementSanteCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get EtablissementSantes by criteria: {}", criteria);

        Page<EtablissementSanteDTO> page = etablissementSanteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /etablissement-santes/count} : count all the etablissementSantes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countEtablissementSantes(EtablissementSanteCriteria criteria) {
        LOG.debug("REST request to count EtablissementSantes by criteria: {}", criteria);
        return ResponseEntity.ok().body(etablissementSanteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /etablissement-santes/:id} : get the "id" etablissementSante.
     *
     * @param id the id of the etablissementSanteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the etablissementSanteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EtablissementSanteDTO> getEtablissementSante(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EtablissementSante : {}", id);
        Optional<EtablissementSanteDTO> etablissementSanteDTO = etablissementSanteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(etablissementSanteDTO);
    }

    /**
     * {@code DELETE  /etablissement-santes/:id} : delete the "id" etablissementSante.
     *
     * @param id the id of the etablissementSanteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtablissementSante(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EtablissementSante : {}", id);
        etablissementSanteService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
