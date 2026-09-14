package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.DemandePriseEnChargeQueryService;
import com.mycompany.myapp.service.DemandePriseEnChargeService;
import com.mycompany.myapp.service.RapportDemandeService;
import com.mycompany.myapp.service.criteria.DemandePriseEnChargeCriteria;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.vm.DemandeWorkflowActionVM;
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
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.DemandePriseEnCharge}.
 */
@RestController
@RequestMapping("/api/demande-prise-en-charges")
public class DemandePriseEnChargeResource {

    private static final Logger LOG = LoggerFactory.getLogger(DemandePriseEnChargeResource.class);

    private static final String ENTITY_NAME = "demandePriseEnCharge";

    @Value("${jhipster.clientApp.name:peccnss}")
    private String applicationName;

    private final DemandePriseEnChargeService demandePriseEnChargeService;

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final DemandePriseEnChargeQueryService demandePriseEnChargeQueryService;

    private final RapportDemandeService rapportDemandeService;

    public DemandePriseEnChargeResource(
        DemandePriseEnChargeService demandePriseEnChargeService,
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        DemandePriseEnChargeQueryService demandePriseEnChargeQueryService,
        RapportDemandeService rapportDemandeService
    ) {
        this.demandePriseEnChargeService = demandePriseEnChargeService;
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.demandePriseEnChargeQueryService = demandePriseEnChargeQueryService;
        this.rapportDemandeService = rapportDemandeService;
    }

    /**
     * {@code POST  /demande-prise-en-charges} : Create a new demandePriseEnCharge.
     *
     * @param demandePriseEnChargeDTO the demandePriseEnChargeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new demandePriseEnChargeDTO, or with status {@code 400 (Bad Request)} if the demandePriseEnCharge has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DemandePriseEnChargeDTO> createDemandePriseEnCharge(
        @Valid @RequestBody DemandePriseEnChargeDTO demandePriseEnChargeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save DemandePriseEnCharge : {}", demandePriseEnChargeDTO);
        if (demandePriseEnChargeDTO.getId() != null) {
            throw new BadRequestAlertException("A new demandePriseEnCharge cannot already have an ID", ENTITY_NAME, "idexists");
        }
        demandePriseEnChargeDTO = demandePriseEnChargeService.save(demandePriseEnChargeDTO);
        return ResponseEntity.created(new URI("/api/demande-prise-en-charges/" + demandePriseEnChargeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, demandePriseEnChargeDTO.getId().toString()))
            .body(demandePriseEnChargeDTO);
    }

    /**
     * {@code PUT  /demande-prise-en-charges/:id} : Updates an existing demandePriseEnCharge.
     *
     * @param id the id of the demandePriseEnChargeDTO to save.
     * @param demandePriseEnChargeDTO the demandePriseEnChargeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandePriseEnChargeDTO,
     * or with status {@code 400 (Bad Request)} if the demandePriseEnChargeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the demandePriseEnChargeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemandePriseEnChargeDTO> updateDemandePriseEnCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DemandePriseEnChargeDTO demandePriseEnChargeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DemandePriseEnCharge : {}, {}", id, demandePriseEnChargeDTO);
        if (demandePriseEnChargeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandePriseEnChargeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!demandePriseEnChargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        demandePriseEnChargeDTO = demandePriseEnChargeService.update(demandePriseEnChargeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, demandePriseEnChargeDTO.getId().toString()))
            .body(demandePriseEnChargeDTO);
    }

    /**
     * {@code PATCH  /demande-prise-en-charges/:id} : Partial updates given fields of an existing demandePriseEnCharge, field will ignore if it is null
     *
     * @param id the id of the demandePriseEnChargeDTO to save.
     * @param demandePriseEnChargeDTO the demandePriseEnChargeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandePriseEnChargeDTO,
     * or with status {@code 400 (Bad Request)} if the demandePriseEnChargeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the demandePriseEnChargeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the demandePriseEnChargeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DemandePriseEnChargeDTO> partialUpdateDemandePriseEnCharge(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody DemandePriseEnChargeDTO demandePriseEnChargeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DemandePriseEnCharge partially : {}, {}", id, demandePriseEnChargeDTO);
        if (demandePriseEnChargeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, demandePriseEnChargeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!demandePriseEnChargeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DemandePriseEnChargeDTO> result = demandePriseEnChargeService.partialUpdate(demandePriseEnChargeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, demandePriseEnChargeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /demande-prise-en-charges} : get all the Demande Prise En Charges.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Demande Prise En Charges in body.
     */
    @GetMapping("")
    public ResponseEntity<List<DemandePriseEnChargeDTO>> getAllDemandePriseEnCharges(
        DemandePriseEnChargeCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get DemandePriseEnCharges by criteria: {}", criteria);

        Page<DemandePriseEnChargeDTO> page = demandePriseEnChargeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /demande-prise-en-charges/count} : count all the demandePriseEnCharges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countDemandePriseEnCharges(DemandePriseEnChargeCriteria criteria) {
        LOG.debug("REST request to count DemandePriseEnCharges by criteria: {}", criteria);
        return ResponseEntity.ok().body(demandePriseEnChargeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /demande-prise-en-charges/:id} : get the "id" demandePriseEnCharge.
     *
     * @param id the id of the demandePriseEnChargeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the demandePriseEnChargeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemandePriseEnChargeDTO> getDemandePriseEnCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DemandePriseEnCharge : {}", id);
        Optional<DemandePriseEnChargeDTO> demandePriseEnChargeDTO = demandePriseEnChargeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(demandePriseEnChargeDTO);
    }

    /**
     * {@code DELETE  /demande-prise-en-charges/:id} : delete the "id" demandePriseEnCharge.
     *
     * @param id the id of the demandePriseEnChargeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemandePriseEnCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DemandePriseEnCharge : {}", id);
        demandePriseEnChargeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code POST  /demande-prise-en-charges/:id/valider} : validates the current workflow step (DRH, then
     * infirmerie du personnel) of the "id" demandePriseEnCharge.
     *
     * @param id the id of the demandePriseEnChargeDTO to validate.
     * @param actionVM an optional comment to record in the history.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandePriseEnChargeDTO.
     */
    @PostMapping("/{id}/valider")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.VALIDATEUR_DRH + "', '" + AuthoritiesConstants.VALIDATEUR_INFIRMERIE + "')")
    public ResponseEntity<DemandePriseEnChargeDTO> validerDemandePriseEnCharge(
        @PathVariable("id") Long id,
        @RequestBody(required = false) DemandeWorkflowActionVM actionVM
    ) {
        LOG.debug("REST request to valider DemandePriseEnCharge : {}", id);
        String commentaire = actionVM != null ? actionVM.getCommentaire() : null;
        DemandePriseEnChargeDTO result = demandePriseEnChargeService.valider(id, commentaire);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code POST  /demande-prise-en-charges/:id/rejeter} : rejects the current workflow step of the "id"
     * demandePriseEnCharge and returns it to its author for correction.
     *
     * @param id the id of the demandePriseEnChargeDTO to reject.
     * @param actionVM the mandatory rejection reason (in {@code commentaire}).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandePriseEnChargeDTO.
     */
    @PostMapping("/{id}/rejeter")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.VALIDATEUR_DRH + "', '" + AuthoritiesConstants.VALIDATEUR_INFIRMERIE + "')")
    public ResponseEntity<DemandePriseEnChargeDTO> rejeterDemandePriseEnCharge(
        @PathVariable("id") Long id,
        @RequestBody DemandeWorkflowActionVM actionVM
    ) {
        LOG.debug("REST request to rejeter DemandePriseEnCharge : {}", id);
        DemandePriseEnChargeDTO result = demandePriseEnChargeService.rejeter(id, actionVM.getCommentaire());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code POST  /demande-prise-en-charges/:id/resoumettre} : resubmits a {@code RETOURNEE} demandePriseEnCharge,
     * sending it back to the 1st validation step (DRH). Only the original author may do this.
     *
     * @param id the id of the demandePriseEnChargeDTO to resubmit.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated demandePriseEnChargeDTO.
     */
    @PostMapping("/{id}/resoumettre")
    public ResponseEntity<DemandePriseEnChargeDTO> resoumettreDemandePriseEnCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to resoumettre DemandePriseEnCharge : {}", id);
        DemandePriseEnChargeDTO result = demandePriseEnChargeService.resoumettre(id);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .body(result);
    }

    /**
     * {@code GET  /demande-prise-en-charges/:id/rapport} : downloads the PDF report for the "id" demandePriseEnCharge,
     * which must be in the {@code VALIDEE} state.
     *
     * @param id the id of the demandePriseEnChargeDTO to report on.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the PDF file.
     */
    @GetMapping("/{id}/rapport")
    public ResponseEntity<byte[]> getRapportDemandePriseEnCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to get the PDF rapport for DemandePriseEnCharge : {}", id);
        byte[] rapport = rapportDemandeService.genererRapport(id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.attachment()
                    .filename("rapport-" + id + ".pdf")
                    .build()
                    .toString()
            )
            .body(rapport);
    }
}
