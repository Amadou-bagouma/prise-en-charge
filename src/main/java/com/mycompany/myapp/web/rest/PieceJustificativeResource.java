package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.PieceJustificativeRepository;
import com.mycompany.myapp.service.PieceJustificativeService;
import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.PieceJustificative}.
 */
@RestController
@RequestMapping("/api/piece-justificatives")
public class PieceJustificativeResource {

    private static final Logger LOG = LoggerFactory.getLogger(PieceJustificativeResource.class);

    private static final String ENTITY_NAME = "pieceJustificative";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final PieceJustificativeService pieceJustificativeService;

    private final PieceJustificativeRepository pieceJustificativeRepository;

    public PieceJustificativeResource(
        PieceJustificativeService pieceJustificativeService,
        PieceJustificativeRepository pieceJustificativeRepository
    ) {
        this.pieceJustificativeService = pieceJustificativeService;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
    }

    /**
     * {@code POST  /piece-justificatives} : Create a new pieceJustificative.
     *
     * @param pieceJustificativeDTO the pieceJustificativeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pieceJustificativeDTO, or with status {@code 400 (Bad Request)} if the pieceJustificative has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PieceJustificativeDTO> createPieceJustificative(@Valid @RequestBody PieceJustificativeDTO pieceJustificativeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PieceJustificative : {}", pieceJustificativeDTO);
        if (pieceJustificativeDTO.getId() != null) {
            throw new BadRequestAlertException("A new pieceJustificative cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pieceJustificativeDTO = pieceJustificativeService.save(pieceJustificativeDTO);
        return ResponseEntity.created(new URI("/api/piece-justificatives/" + pieceJustificativeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pieceJustificativeDTO.getId().toString()))
            .body(pieceJustificativeDTO);
    }

    /**
     * {@code PUT  /piece-justificatives/:id} : Updates an existing pieceJustificative.
     *
     * @param id the id of the pieceJustificativeDTO to save.
     * @param pieceJustificativeDTO the pieceJustificativeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pieceJustificativeDTO,
     * or with status {@code 400 (Bad Request)} if the pieceJustificativeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pieceJustificativeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PieceJustificativeDTO> updatePieceJustificative(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PieceJustificativeDTO pieceJustificativeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PieceJustificative : {}, {}", id, pieceJustificativeDTO);
        if (pieceJustificativeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pieceJustificativeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pieceJustificativeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pieceJustificativeDTO = pieceJustificativeService.update(pieceJustificativeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pieceJustificativeDTO.getId().toString()))
            .body(pieceJustificativeDTO);
    }

    /**
     * {@code PATCH  /piece-justificatives/:id} : Partial updates given fields of an existing pieceJustificative, field will ignore if it is null
     *
     * @param id the id of the pieceJustificativeDTO to save.
     * @param pieceJustificativeDTO the pieceJustificativeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pieceJustificativeDTO,
     * or with status {@code 400 (Bad Request)} if the pieceJustificativeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pieceJustificativeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pieceJustificativeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PieceJustificativeDTO> partialUpdatePieceJustificative(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PieceJustificativeDTO pieceJustificativeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PieceJustificative partially : {}, {}", id, pieceJustificativeDTO);
        if (pieceJustificativeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pieceJustificativeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pieceJustificativeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PieceJustificativeDTO> result = pieceJustificativeService.partialUpdate(pieceJustificativeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pieceJustificativeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /piece-justificatives} : get all the Piece Justificatives.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Piece Justificatives in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PieceJustificativeDTO>> getAllPieceJustificatives(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload,
        @RequestParam(name = "demandeId", required = false) Long demandeId
    ) {
        LOG.debug("REST request to get a page of PieceJustificatives");
        Page<PieceJustificativeDTO> page;
        // L'ecran de detail d'une demande n'a besoin que des pieces de ce dossier : le filtre
        // evite de rapatrier toute la table pour en garder trois lignes.
        if (demandeId != null) {
            page = pieceJustificativeService.findAllByDemande(demandeId, pageable);
        } else if (eagerload) {
            page = pieceJustificativeService.findAllWithEagerRelationships(pageable);
        } else {
            page = pieceJustificativeService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /piece-justificatives/:id} : get the "id" pieceJustificative.
     *
     * @param id the id of the pieceJustificativeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pieceJustificativeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PieceJustificativeDTO> getPieceJustificative(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PieceJustificative : {}", id);
        Optional<PieceJustificativeDTO> pieceJustificativeDTO = pieceJustificativeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pieceJustificativeDTO);
    }

    /**
     * {@code DELETE  /piece-justificatives/:id} : delete the "id" pieceJustificative.
     *
     * @param id the id of the pieceJustificativeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePieceJustificative(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PieceJustificative : {}", id);
        pieceJustificativeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
