package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.service.TypeSoinService;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 */
@RestController
@RequestMapping("/api/type-soins")
public class TypeSoinResource {

    private static final Logger LOG = LoggerFactory.getLogger(TypeSoinResource.class);

    private static final String ENTITY_NAME = "typeSoin";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final TypeSoinService typeSoinService;

    private final TypeSoinRepository typeSoinRepository;

    public TypeSoinResource(TypeSoinService typeSoinService, TypeSoinRepository typeSoinRepository) {
        this.typeSoinService = typeSoinService;
        this.typeSoinRepository = typeSoinRepository;
    }

    /**
     * {@code POST  /type-soins} : Create a new typeSoin.
     *
     * @param typeSoinDTO the typeSoinDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeSoinDTO, or with status {@code 400 (Bad Request)} if the typeSoin has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TypeSoinDTO> createTypeSoin(@Valid @RequestBody TypeSoinDTO typeSoinDTO) throws URISyntaxException {
        LOG.debug("REST request to save TypeSoin : {}", typeSoinDTO);
        if (typeSoinDTO.getId() != null) {
            throw new BadRequestAlertException("A new typeSoin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        typeSoinDTO = typeSoinService.save(typeSoinDTO);
        return ResponseEntity.created(new URI("/api/type-soins/" + typeSoinDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, typeSoinDTO.getId().toString()))
            .body(typeSoinDTO);
    }

    /**
     * {@code PUT  /type-soins/:id} : Updates an existing typeSoin.
     *
     * @param id the id of the typeSoinDTO to save.
     * @param typeSoinDTO the typeSoinDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeSoinDTO,
     * or with status {@code 400 (Bad Request)} if the typeSoinDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeSoinDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TypeSoinDTO> updateTypeSoin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeSoinDTO typeSoinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TypeSoin : {}, {}", id, typeSoinDTO);
        if (typeSoinDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeSoinDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeSoinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        typeSoinDTO = typeSoinService.update(typeSoinDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeSoinDTO.getId().toString()))
            .body(typeSoinDTO);
    }

    /**
     * {@code PATCH  /type-soins/:id} : Partial updates given fields of an existing typeSoin, field will ignore if it is null
     *
     * @param id the id of the typeSoinDTO to save.
     * @param typeSoinDTO the typeSoinDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeSoinDTO,
     * or with status {@code 400 (Bad Request)} if the typeSoinDTO is not valid,
     * or with status {@code 404 (Not Found)} if the typeSoinDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeSoinDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TypeSoinDTO> partialUpdateTypeSoin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeSoinDTO typeSoinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TypeSoin partially : {}, {}", id, typeSoinDTO);
        if (typeSoinDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeSoinDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeSoinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeSoinDTO> result = typeSoinService.partialUpdate(typeSoinDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeSoinDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /type-soins} : get all the Type Soins.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Type Soins in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TypeSoinDTO>> getAllTypeSoins(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of TypeSoins");
        Page<TypeSoinDTO> page = typeSoinService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /type-soins/:id} : get the "id" typeSoin.
     *
     * @param id the id of the typeSoinDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeSoinDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TypeSoinDTO> getTypeSoin(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TypeSoin : {}", id);
        Optional<TypeSoinDTO> typeSoinDTO = typeSoinService.findOne(id);
        return ResponseUtil.wrapOrNotFound(typeSoinDTO);
    }

    /**
     * {@code DELETE  /type-soins/:id} : delete the "id" typeSoin.
     *
     * @param id the id of the typeSoinDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTypeSoin(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TypeSoin : {}", id);
        typeSoinService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
