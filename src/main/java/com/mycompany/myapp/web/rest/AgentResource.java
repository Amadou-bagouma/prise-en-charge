package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.AgentQueryService;
import com.mycompany.myapp.service.AgentService;
import com.mycompany.myapp.service.PerimetreAgent;
import com.mycompany.myapp.service.criteria.AgentCriteria;
import com.mycompany.myapp.service.dto.AgentDTO;
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
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Agent}.
 */
@RestController
@RequestMapping("/api/agents")
public class AgentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AgentResource.class);

    private static final String ENTITY_NAME = "agent";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final PerimetreAgent perimetreAgent;

    private final AgentService agentService;

    private final AgentRepository agentRepository;

    private final AgentQueryService agentQueryService;

    public AgentResource(
        AgentService agentService,
        AgentRepository agentRepository,
        AgentQueryService agentQueryService,
        PerimetreAgent perimetreAgent
    ) {
        this.perimetreAgent = perimetreAgent;
        this.agentService = agentService;
        this.agentRepository = agentRepository;
        this.agentQueryService = agentQueryService;
    }

    /**
     * {@code POST  /agents} : Create a new agent.
     *
     * @param agentDTO the agentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new agentDTO, or with status {@code 400 (Bad Request)} if the agent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AgentDTO> createAgent(@Valid @RequestBody AgentDTO agentDTO) throws URISyntaxException {
        LOG.debug("REST request to save Agent : {}", agentDTO);
        if (agentDTO.getId() != null) {
            throw new BadRequestAlertException("A new agent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        agentDTO = agentService.save(agentDTO);
        return ResponseEntity.created(new URI("/api/agents/" + agentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString()))
            .body(agentDTO);
    }

    /**
     * {@code PUT  /agents/:id} : Updates an existing agent.
     *
     * @param id the id of the agentDTO to save.
     * @param agentDTO the agentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO,
     * or with status {@code 400 (Bad Request)} if the agentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AgentDTO> updateAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AgentDTO agentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Agent : {}, {}", id, agentDTO);
        if (agentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        agentDTO = agentService.update(agentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString()))
            .body(agentDTO);
    }

    /**
     * {@code PUT  /agents/:id/statut} : change la situation d'un agent.
     *
     * <p>Seul point d'entree pour cela : les formulaires de modification ne touchent pas a la
     * situation, faute de quoi on pourrait retirer un droit sans motif et sans que les ayants droit suivent.
     *
     * @param id l'agent concerne.
     * @param changement le nouveau statut et sa raison.
     * @return {@link ResponseEntity} avec le statut {@code 200 (OK)} et l'entite mise a jour.
     */
    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_CHANGER_STATUT + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AgentDTO> changerStatut(@PathVariable("id") Long id, @Valid @RequestBody ChangementStatutDTO changement) {
        LOG.debug("REST request to change the status of Agent {} to {}", id, changement.statut());
        AgentDTO misAJour = agentService.changerStatut(id, changement.statut(), changement.motif());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(misAJour);
    }

    /**
     * {@code PATCH  /agents/:id} : Partial updates given fields of an existing agent, field will ignore if it is null
     *
     * @param id the id of the agentDTO to save.
     * @param agentDTO the agentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated agentDTO,
     * or with status {@code 400 (Bad Request)} if the agentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the agentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the agentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AgentDTO> partialUpdateAgent(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AgentDTO agentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Agent partially : {}, {}", id, agentDTO);
        if (agentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, agentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!agentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AgentDTO> result = agentService.partialUpdate(agentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, agentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /agents} : get all the Agents.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Agents in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<AgentDTO>> getAllAgents(
        AgentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Agents by criteria: {}", criteria);
        // Un agent connecte a son espace ne voit que ce qui le concerne : le critere est impose
        // ici, apres celui que l'appelant a pu envoyer, de sorte qu'il ne puisse pas l'elargir.
        perimetreAgent.agentDuCompteCourant().ifPresent(sien -> {
            LongFilter siens = new LongFilter();
            siens.setEquals(sien);
            criteria.setId(siens);
        });

        Page<AgentDTO> page = agentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /agents/count} : count all the agents.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countAgents(AgentCriteria criteria) {
        LOG.debug("REST request to count Agents by criteria: {}", criteria);
        // Un agent connecte a son espace ne voit que ce qui le concerne : le critere est impose
        // ici, apres celui que l'appelant a pu envoyer, de sorte qu'il ne puisse pas l'elargir.
        perimetreAgent.agentDuCompteCourant().ifPresent(sien -> {
            LongFilter siens = new LongFilter();
            siens.setEquals(sien);
            criteria.setId(siens);
        });
        return ResponseEntity.ok().body(agentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /agents/:id} : get the "id" agent.
     *
     * @param id the id of the agentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the agentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<AgentDTO> getAgent(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Agent : {}", id);
        // Une adresse tapee a la main ne doit pas ouvrir la fiche d'un autre.
        perimetreAgent.exigerDansLePerimetre(id);
        Optional<AgentDTO> agentDTO = agentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(agentDTO);
    }

    /**
     * {@code DELETE  /agents/:id} : delete the "id" agent.
     *
     * @param id the id of the agentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.AGENT_SUPPRIMER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteAgent(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Agent : {}", id);
        agentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
