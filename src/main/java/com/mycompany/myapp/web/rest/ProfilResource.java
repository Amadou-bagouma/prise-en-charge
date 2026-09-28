package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.ProfilService;
import com.mycompany.myapp.service.dto.ProfilDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Profil}.
 *
 * <p>Like Gestion, Region and Direction, a Profil is reference/master data: any authenticated
 * user may read it (the user-management screen needs the list to populate its picker), but only
 * an admin may create, update or delete one (see {@code SecurityConfiguration}).
 */
@RestController
@RequestMapping("/api/profils")
public class ProfilResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilResource.class);

    private static final String ENTITY_NAME = "profil";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final ProfilService profilService;

    private final ProfilRepository profilRepository;

    public ProfilResource(ProfilService profilService, ProfilRepository profilRepository) {
        this.profilService = profilService;
        this.profilRepository = profilRepository;
    }

    /**
     * {@code POST  /profils} : Create a new profil.
     *
     * @param profilDTO the profilDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profilDTO, or with status {@code 400 (Bad Request)} if the profil has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<ProfilDTO> createProfil(@Valid @RequestBody ProfilDTO profilDTO) throws URISyntaxException {
        LOG.debug("REST request to save Profil : {}", profilDTO);
        if (profilDTO.getId() != null) {
            throw new BadRequestAlertException("A new profil cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profilDTO = profilService.save(profilDTO);
        return ResponseEntity.created(new URI("/api/profils/" + profilDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, profilDTO.getId().toString()))
            .body(profilDTO);
    }

    /**
     * {@code PUT  /profils/:id} : Updates an existing profil.
     *
     * @param id the id of the profilDTO to save.
     * @param profilDTO the profilDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profilDTO,
     * or with status {@code 400 (Bad Request)} if the profilDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profilDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<ProfilDTO> updateProfil(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProfilDTO profilDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Profil : {}, {}", id, profilDTO);
        if (profilDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profilDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profilRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profilDTO = profilService.update(profilDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profilDTO.getId().toString()))
            .body(profilDTO);
    }

    /**
     * {@code PATCH  /profils/:id} : Partial updates given fields of an existing profil, field will ignore if it is null
     *
     * @param id the id of the profilDTO to save.
     * @param profilDTO the profilDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profilDTO,
     * or with status {@code 400 (Bad Request)} if the profilDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profilDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profilDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<ProfilDTO> partialUpdateProfil(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProfilDTO profilDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Profil partially : {}, {}", id, profilDTO);
        if (profilDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profilDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profilRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfilDTO> result = profilService.partialUpdate(profilDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, profilDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profils} : get all the Profils.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profils in body.
     */
    @GetMapping("")
    public ResponseEntity<List<ProfilDTO>> getAllProfils(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Profils");
        Page<ProfilDTO> page = profilService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /profils/:id} : get the "id" profil.
     *
     * @param id the id of the profilDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profilDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfilDTO> getProfil(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Profil : {}", id);
        Optional<ProfilDTO> profilDTO = profilService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profilDTO);
    }

    /**
     * {@code DELETE  /profils/:id} : delete the "id" profil.
     *
     * @param id the id of the profilDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteProfil(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Profil : {}", id);
        profilService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
