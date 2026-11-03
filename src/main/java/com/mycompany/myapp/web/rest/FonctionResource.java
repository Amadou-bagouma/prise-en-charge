package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.FonctionRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.FonctionService;
import com.mycompany.myapp.service.dto.FonctionDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Fonction}.
 */
@RestController
@RequestMapping("/api/fonctions")
public class FonctionResource {

    private static final Logger LOG = LoggerFactory.getLogger(FonctionResource.class);

    private static final String ENTITY_NAME = "fonction";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final FonctionService fonctionService;

    private final FonctionRepository fonctionRepository;

    public FonctionResource(FonctionService fonctionService, FonctionRepository fonctionRepository) {
        this.fonctionService = fonctionService;
        this.fonctionRepository = fonctionRepository;
    }

    /**
     * {@code POST  /fonctions} : Create a new fonction.
     *
     * @param fonctionDTO the fonctionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new fonctionDTO, or with status {@code 400 (Bad Request)} if the fonction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<FonctionDTO> createFonction(@Valid @RequestBody FonctionDTO fonctionDTO) throws URISyntaxException {
        LOG.debug("REST request to save Fonction : {}", fonctionDTO);
        if (fonctionDTO.getId() != null) {
            throw new BadRequestAlertException("A new fonction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        fonctionDTO = fonctionService.save(fonctionDTO);
        return ResponseEntity.created(new URI("/api/fonctions/" + fonctionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, fonctionDTO.getId().toString()))
            .body(fonctionDTO);
    }

    /**
     * {@code PUT  /fonctions/:id} : Updates an existing fonction.
     *
     * @param id the id of the fonctionDTO to save.
     * @param fonctionDTO the fonctionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fonctionDTO,
     * or with status {@code 400 (Bad Request)} if the fonctionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the fonctionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<FonctionDTO> updateFonction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FonctionDTO fonctionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Fonction : {}, {}", id, fonctionDTO);
        if (fonctionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fonctionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fonctionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        fonctionDTO = fonctionService.update(fonctionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fonctionDTO.getId().toString()))
            .body(fonctionDTO);
    }

    /**
     * {@code PATCH  /fonctions/:id} : Partial updates given fields of an existing fonction, field will ignore if it is null
     *
     * @param id the id of the fonctionDTO to save.
     * @param fonctionDTO the fonctionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated fonctionDTO,
     * or with status {@code 400 (Bad Request)} if the fonctionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the fonctionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the fonctionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<FonctionDTO> partialUpdateFonction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FonctionDTO fonctionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Fonction partially : {}, {}", id, fonctionDTO);
        if (fonctionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, fonctionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!fonctionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FonctionDTO> result = fonctionService.partialUpdate(fonctionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, fonctionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /fonctions} : get all the Fonctions.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Fonctions in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<FonctionDTO>> getAllFonctions(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Fonctions");
        Page<FonctionDTO> page = fonctionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /fonctions/:id} : get the "id" fonction.
     *
     * @param id the id of the fonctionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the fonctionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<FonctionDTO> getFonction(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Fonction : {}", id);
        Optional<FonctionDTO> fonctionDTO = fonctionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(fonctionDTO);
    }

    /**
     * {@code DELETE  /fonctions/:id} : delete the "id" fonction.
     *
     * @param id the id of the fonctionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.REFERENTIEL_SUPPRIMER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteFonction(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Fonction : {}", id);
        fonctionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
