package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.AyantDroitQueryService;
import com.mycompany.myapp.service.AyantDroitService;
import com.mycompany.myapp.service.GenerateurCodeAyantDroit;
import com.mycompany.myapp.service.criteria.AyantDroitCriteria;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.dto.ChangementStatutDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.AyantDroit}.
 */
@RestController
@RequestMapping("/api/ayant-droits")
public class AyantDroitResource {

    private static final Logger LOG = LoggerFactory.getLogger(AyantDroitResource.class);

    private static final String ENTITY_NAME = "ayantDroit";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final AyantDroitService ayantDroitService;

    private final AyantDroitRepository ayantDroitRepository;

    private final AyantDroitQueryService ayantDroitQueryService;

    private final AgentRepository agentRepository;

    private final GenerateurCodeAyantDroit generateurCodeAyantDroit;

    public AyantDroitResource(
        AyantDroitService ayantDroitService,
        AyantDroitRepository ayantDroitRepository,
        AyantDroitQueryService ayantDroitQueryService,
        AgentRepository agentRepository,
        GenerateurCodeAyantDroit generateurCodeAyantDroit
    ) {
        this.ayantDroitService = ayantDroitService;
        this.ayantDroitRepository = ayantDroitRepository;
        this.ayantDroitQueryService = ayantDroitQueryService;
        this.agentRepository = agentRepository;
        this.generateurCodeAyantDroit = generateurCodeAyantDroit;
    }

    /**
     * {@code POST  /ayant-droits} : Create a new ayantDroit.
     *
     * @param ayantDroitDTO the ayantDroitDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new ayantDroitDTO, or with status {@code 400 (Bad Request)} if the ayantDroit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> createAyantDroit(@Valid @RequestBody AyantDroitDTO ayantDroitDTO) throws URISyntaxException {
        LOG.debug("REST request to save AyantDroit : {}", ayantDroitDTO);
        if (ayantDroitDTO.getId() != null) {
            throw new BadRequestAlertException("A new ayantDroit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ayantDroitDTO = ayantDroitService.save(ayantDroitDTO);
        return ResponseEntity.created(new URI("/api/ayant-droits/" + ayantDroitDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, ayantDroitDTO.getId().toString()))
            .body(ayantDroitDTO);
    }

    /**
     * {@code PUT  /ayant-droits/:id} : Updates an existing ayantDroit.
     *
     * @param id the id of the ayantDroitDTO to save.
     * @param ayantDroitDTO the ayantDroitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ayantDroitDTO,
     * or with status {@code 400 (Bad Request)} if the ayantDroitDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the ayantDroitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> updateAyantDroit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AyantDroitDTO ayantDroitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update AyantDroit : {}, {}", id, ayantDroitDTO);
        if (ayantDroitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ayantDroitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ayantDroitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ayantDroitDTO = ayantDroitService.update(ayantDroitDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ayantDroitDTO.getId().toString()))
            .body(ayantDroitDTO);
    }

    /**
     * {@code PUT  /ayant-droits/:id/valider} : declare le rattachement verifie.
     *
     * <p>Reserve au controle RH : c'est le meme travail que la verification d'un dossier, sur
     * les pieces qui etablissent le lien de parente.
     *
     * @param id l'ayant droit a valider.
     * @return {@link ResponseEntity} avec le statut {@code 200 (OK)} et l'ayant droit mis a jour.
     */
    @PutMapping("/{id}/valider")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.VERIFICATEUR_RH + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> validerAyantDroit(@PathVariable("id") Long id) {
        LOG.debug("REST request to validate AyantDroit : {}", id);
        AyantDroitDTO misAJour = ayantDroitService.valider(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(misAJour);
    }

    /**
     * {@code PUT  /ayant-droits/:id/statut} : change la situation d'un ayant droit.
     *
     * <p>Seul point d'entree pour cela : les formulaires de modification ne touchent pas a la
     * situation, faute de quoi on pourrait retirer un droit sans motif.
     *
     * @param id l'ayant droit concerne.
     * @param changement le nouveau statut et sa raison.
     * @return {@link ResponseEntity} avec le statut {@code 200 (OK)} et l'entite mise a jour.
     */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CHANGER_STATUT + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> changerStatut(@PathVariable("id") Long id, @Valid @RequestBody ChangementStatutDTO changement) {
        LOG.debug("REST request to change the status of AyantDroit {} to {}", id, changement.statut());
        AyantDroitDTO misAJour = ayantDroitService.changerStatut(id, changement.statut(), changement.motif());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(misAJour);
    }

    /**
     * {@code PATCH  /ayant-droits/:id} : Partial updates given fields of an existing ayantDroit, field will ignore if it is null
     *
     * @param id the id of the ayantDroitDTO to save.
     * @param ayantDroitDTO the ayantDroitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated ayantDroitDTO,
     * or with status {@code 400 (Bad Request)} if the ayantDroitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the ayantDroitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the ayantDroitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> partialUpdateAyantDroit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AyantDroitDTO ayantDroitDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update AyantDroit partially : {}, {}", id, ayantDroitDTO);
        if (ayantDroitDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, ayantDroitDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!ayantDroitRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AyantDroitDTO> result = ayantDroitService.partialUpdate(ayantDroitDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, ayantDroitDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /ayant-droits} : get all the Ayant Droits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Ayant Droits in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<AyantDroitDTO>> getAllAyantDroits(
        AyantDroitCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get AyantDroits by criteria: {}", criteria);

        Page<AyantDroitDTO> page = ayantDroitQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /ayant-droits/count} : count all the ayantDroits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countAyantDroits(AyantDroitCriteria criteria) {
        LOG.debug("REST request to count AyantDroits by criteria: {}", criteria);
        return ResponseEntity.ok().body(ayantDroitQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /ayant-droits/:id} : get the "id" ayantDroit.
     *
     * @param id the id of the ayantDroitDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the ayantDroitDTO, or with status {@code 404 (Not Found)}.
     */
    /**
     * {@code GET /ayant-droits/code-suggere} : l'apercu du code qui sera attribue.
     *
     * <p>Sert uniquement a montrer le code a l'agent pendant la saisie. Celui qui fait foi est
     * calcule au moment de l'enregistrement : entre l'apercu et la validation du formulaire, un
     * autre agent a pu creer un ayant droit pour le meme titulaire.
     */
    @GetMapping("/code-suggere")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<String> codeSuggere(@RequestParam("agentId") Long agentId, @RequestParam("lien") LienParente lien) {
        LOG.debug("REST request to preview AyantDroit code for agent {} and lien {}", agentId, lien);
        String matricule = agentRepository
            .findById(agentId)
            .map(Agent::getMatricule)
            .orElseThrow(() -> new BadRequestAlertException("Agent introuvable", ENTITY_NAME, "agent.introuvable"));
        return ResponseEntity.ok().body(generateurCodeAyantDroit.genererPour(matricule, lien));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AyantDroitDTO> getAyantDroit(@PathVariable("id") Long id) {
        LOG.debug("REST request to get AyantDroit : {}", id);
        Optional<AyantDroitDTO> ayantDroitDTO = ayantDroitService.findOne(id);
        return ResponseUtil.wrapOrNotFound(ayantDroitDTO);
    }

    /**
     * {@code DELETE  /ayant-droits/:id} : delete the "id" ayantDroit.
     *
     * @param id the id of the ayantDroitDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AYANT_DROIT_SUPPRIMER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteAyantDroit(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete AyantDroit : {}", id);
        ayantDroitService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
