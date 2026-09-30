package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.repository.CarteBeneficiaireRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CarteBeneficiaireQueryService;
import com.mycompany.myapp.service.CarteBeneficiaireService;
import com.mycompany.myapp.service.PerimetreAgent;
import com.mycompany.myapp.service.criteria.CarteBeneficiaireCriteria;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.CarteBeneficiaire}.
 */
@RestController
@RequestMapping("/api/carte-beneficiaires")
public class CarteBeneficiaireResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarteBeneficiaireResource.class);

    private static final String ENTITY_NAME = "carteBeneficiaire";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final PerimetreAgent perimetreAgent;

    private final CarteBeneficiaireService carteBeneficiaireService;

    private final CarteBeneficiaireRepository carteBeneficiaireRepository;

    private final CarteBeneficiaireQueryService carteBeneficiaireQueryService;

    public CarteBeneficiaireResource(
        CarteBeneficiaireService carteBeneficiaireService,
        CarteBeneficiaireRepository carteBeneficiaireRepository,
        CarteBeneficiaireQueryService carteBeneficiaireQueryService,
        PerimetreAgent perimetreAgent
    ) {
        this.perimetreAgent = perimetreAgent;
        this.carteBeneficiaireService = carteBeneficiaireService;
        this.carteBeneficiaireRepository = carteBeneficiaireRepository;
        this.carteBeneficiaireQueryService = carteBeneficiaireQueryService;
    }

    /**
     * {@code POST  /carte-beneficiaires} : Create a new carteBeneficiaire.
     *
     * @param carteBeneficiaireDTO the carteBeneficiaireDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carteBeneficiaireDTO, or with status {@code 400 (Bad Request)} if the carteBeneficiaire has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    /**
     * {@code POST  /carte-beneficiaires/generer} : etablit une carte pour un beneficiaire.
     *
     * <p>Rien n'est demande que le beneficiaire : le numero, la periode de validite et la date
     * d'emission sont poses par le serveur. C'est le geste qu'on accomplit depuis la fiche d'un
     * agent ou d'un ayant droit, sans passer par un formulaire.
     *
     * @param typeBeneficiaire {@code AGENT} ou {@code AYANT_DROIT}.
     * @param beneficiaireId l'agent ou l'ayant droit concerne.
     * @return {@link ResponseEntity} avec le statut {@code 201 (Created)} et la carte etablie.
     */
    @PostMapping("/generer")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> genererCarteBeneficiaire(
        @RequestParam("typeBeneficiaire") TypeBeneficiaire typeBeneficiaire,
        @RequestParam("beneficiaireId") Long beneficiaireId
    ) throws URISyntaxException {
        LOG.debug("REST request to generate CarteBeneficiaire for {} {}", typeBeneficiaire, beneficiaireId);
        CarteBeneficiaireDTO carte = carteBeneficiaireService.generer(typeBeneficiaire, beneficiaireId);
        return ResponseEntity.created(new URI("/api/carte-beneficiaires/" + carte.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carte.getId().toString()))
            .body(carte);
    }

    /**
     * {@code POST /carte-beneficiaires/:id/duplicata} : etablit le duplicata d'une carte perdue.
     *
     * @param id la carte perdue.
     * @param motif ce qui est arrive, conserve au journal.
     * @return {@link ResponseEntity} avec le statut {@code 201 (Created)} et le duplicata.
     */
    @PostMapping("/{id}/duplicata")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> dupliquerCarteBeneficiaire(@PathVariable("id") Long id, @RequestParam("motif") String motif)
        throws URISyntaxException {
        LOG.debug("REST request to duplicate CarteBeneficiaire {}", id);
        CarteBeneficiaireDTO duplicata = carteBeneficiaireService.dupliquer(id, motif);
        return ResponseEntity.created(new URI("/api/carte-beneficiaires/" + duplicata.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, duplicata.getId().toString()))
            .body(duplicata);
    }

    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CREER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> createCarteBeneficiaire(@Valid @RequestBody CarteBeneficiaireDTO carteBeneficiaireDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save CarteBeneficiaire : {}", carteBeneficiaireDTO);
        if (carteBeneficiaireDTO.getId() != null) {
            throw new BadRequestAlertException("A new carteBeneficiaire cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carteBeneficiaireDTO = carteBeneficiaireService.save(carteBeneficiaireDTO);
        return ResponseEntity.created(new URI("/api/carte-beneficiaires/" + carteBeneficiaireDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carteBeneficiaireDTO.getId().toString()))
            .body(carteBeneficiaireDTO);
    }

    /**
     * {@code PUT  /carte-beneficiaires/:id} : Updates an existing carteBeneficiaire.
     *
     * @param id the id of the carteBeneficiaireDTO to save.
     * @param carteBeneficiaireDTO the carteBeneficiaireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carteBeneficiaireDTO,
     * or with status {@code 400 (Bad Request)} if the carteBeneficiaireDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carteBeneficiaireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> updateCarteBeneficiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CarteBeneficiaireDTO carteBeneficiaireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarteBeneficiaire : {}, {}", id, carteBeneficiaireDTO);
        if (carteBeneficiaireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carteBeneficiaireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carteBeneficiaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carteBeneficiaireDTO = carteBeneficiaireService.update(carteBeneficiaireDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carteBeneficiaireDTO.getId().toString()))
            .body(carteBeneficiaireDTO);
    }

    /**
     * {@code PATCH  /carte-beneficiaires/:id} : Partial updates given fields of an existing carteBeneficiaire, field will ignore if it is null
     *
     * @param id the id of the carteBeneficiaireDTO to save.
     * @param carteBeneficiaireDTO the carteBeneficiaireDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carteBeneficiaireDTO,
     * or with status {@code 400 (Bad Request)} if the carteBeneficiaireDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carteBeneficiaireDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carteBeneficiaireDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_MODIFIER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> partialUpdateCarteBeneficiaire(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CarteBeneficiaireDTO carteBeneficiaireDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarteBeneficiaire partially : {}, {}", id, carteBeneficiaireDTO);
        if (carteBeneficiaireDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carteBeneficiaireDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carteBeneficiaireRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarteBeneficiaireDTO> result = carteBeneficiaireService.partialUpdate(carteBeneficiaireDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carteBeneficiaireDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /carte-beneficiaires} : get all the Carte Beneficiaires.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Carte Beneficiaires in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<CarteBeneficiaireDTO>> getAllCarteBeneficiaires(
        CarteBeneficiaireCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get CarteBeneficiaires by criteria: {}", criteria);
        // Un agent connecte a son espace ne voit que ce qui le concerne : le critere est impose
        // ici, apres celui que l'appelant a pu envoyer, de sorte qu'il ne puisse pas l'elargir.
        perimetreAgent.agentDuCompteCourant().ifPresent(sien -> {
            LongFilter siens = new LongFilter();
            siens.setEquals(sien);
            criteria.setAgentId(siens);
        });

        Page<CarteBeneficiaireDTO> page = carteBeneficiaireQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /carte-beneficiaires/count} : count all the carteBeneficiaires.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countCarteBeneficiaires(CarteBeneficiaireCriteria criteria) {
        LOG.debug("REST request to count CarteBeneficiaires by criteria: {}", criteria);
        // Un agent connecte a son espace ne voit que ce qui le concerne : le critere est impose
        // ici, apres celui que l'appelant a pu envoyer, de sorte qu'il ne puisse pas l'elargir.
        perimetreAgent.agentDuCompteCourant().ifPresent(sien -> {
            LongFilter siens = new LongFilter();
            siens.setEquals(sien);
            criteria.setAgentId(siens);
        });
        return ResponseEntity.ok().body(carteBeneficiaireQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /carte-beneficiaires/:id} : get the "id" carteBeneficiaire.
     *
     * @param id the id of the carteBeneficiaireDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carteBeneficiaireDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_CONSULTER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CarteBeneficiaireDTO> getCarteBeneficiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarteBeneficiaire : {}", id);
        // Une adresse tapee a la main ne doit pas ouvrir la fiche d'un autre.
        carteBeneficiaireService
            .findOne(id)
            .ifPresent(carte -> perimetreAgent.exigerDansLePerimetre(carte.getAgent() == null ? null : carte.getAgent().getId()));
        Optional<CarteBeneficiaireDTO> carteBeneficiaireDTO = carteBeneficiaireService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carteBeneficiaireDTO);
    }

    /**
     * {@code DELETE  /carte-beneficiaires/:id} : delete the "id" carteBeneficiaire.
     *
     * @param id the id of the carteBeneficiaireDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + ActionsConstants.CARTE_SUPPRIMER + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteCarteBeneficiaire(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarteBeneficiaire : {}", id);
        carteBeneficiaireService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
