package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.TypeSoinQueryService;
import com.mycompany.myapp.service.TypeSoinService;
import com.mycompany.myapp.service.criteria.TypeSoinCriteria;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 *
 * <p>Le referentiel se lit par tout agent — il faut bien remplir une demande — mais ne s'ecrit
 * que par l'administration : une categorie de soins engage l'imprime officiel.
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

    private final TypeSoinQueryService typeSoinQueryService;

    public TypeSoinResource(
        TypeSoinService typeSoinService,
        TypeSoinRepository typeSoinRepository,
        TypeSoinQueryService typeSoinQueryService
    ) {
        this.typeSoinService = typeSoinService;
        this.typeSoinRepository = typeSoinRepository;
        this.typeSoinQueryService = typeSoinQueryService;
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeSoinDTO> createTypeSoin(@Valid @RequestBody TypeSoinDTO typeSoinDTO) throws URISyntaxException {
        LOG.debug("REST request to save TypeSoin : {}", typeSoinDTO);
        if (typeSoinDTO.getId() != null) {
            throw new BadRequestAlertException("A new typeSoin cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeSoinDTO resultat = typeSoinService.save(typeSoinDTO);
        return ResponseEntity.created(new URI("/api/type-soins/" + resultat.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, resultat.getId().toString()))
            .body(resultat);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeSoinDTO> updateTypeSoin(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeSoinDTO typeSoinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TypeSoin : {}, {}", id, typeSoinDTO);
        verifierId(id, typeSoinDTO);

        TypeSoinDTO resultat = typeSoinService.update(typeSoinDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, resultat.getId().toString()))
            .body(resultat);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeSoinDTO> partialUpdateTypeSoin(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeSoinDTO typeSoinDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TypeSoin partially : {}, {}", id, typeSoinDTO);
        verifierId(id, typeSoinDTO);

        Optional<TypeSoinDTO> result = typeSoinService.partialUpdate(typeSoinDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeSoinDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<TypeSoinDTO>> getAllTypeSoins(
        TypeSoinCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TypeSoins by criteria: {}", criteria);

        Page<TypeSoinDTO> page = typeSoinQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTypeSoins(TypeSoinCriteria criteria) {
        LOG.debug("REST request to count TypeSoins by criteria: {}", criteria);
        return ResponseEntity.ok().body(typeSoinQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET /type-soins/actifs} : les types proposes a la saisie, dans l'ordre de l'imprime.
     *
     * <p>Sans pagination : le referentiel tient sur un ecran, et la saisie d'une demande a besoin
     * de la liste entiere pour afficher ses cases a cocher.
     */
    @GetMapping("/actifs")
    public ResponseEntity<List<TypeSoinDTO>> getTypeSoinsActifs() {
        LOG.debug("REST request to get actifs TypeSoins");
        return ResponseEntity.ok().body(typeSoinService.findAllActifs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeSoinDTO> getTypeSoin(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TypeSoin : {}", id);
        return ResponseUtil.wrapOrNotFound(typeSoinService.findOne(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteTypeSoin(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TypeSoin : {}", id);
        typeSoinService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private void verifierId(Long id, TypeSoinDTO typeSoinDTO) {
        if (typeSoinDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeSoinDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!typeSoinRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
    }
}
