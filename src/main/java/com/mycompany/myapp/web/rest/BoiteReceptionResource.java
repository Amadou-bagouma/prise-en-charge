package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.BoiteReceptionService;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 *
 * <p>Il n'y a volontairement ni creation, ni modification, ni suppression : une boite existe
 * parce qu'un profil existe, et disparait avec lui. Les seules ecritures possibles sont la
 * consultation et la lecture d'une tache, toutes deux limitees a sa propre boite.
 *
 * <p>Les routes {@code /mienne} sont ouvertes a tout utilisateur authentifie, mais ne montrent
 * que les taches que ses droits lui donnent : le service resout lui-meme le profil du compte
 * appelant, aucun identifiant de boite n'est accepte du client. Les routes de consultation
 * globale sont reservees aux administrateurs.
 */
@RestController
@RequestMapping("/api/boite-receptions")
public class BoiteReceptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(BoiteReceptionResource.class);

    private final BoiteReceptionService boiteReceptionService;

    public BoiteReceptionResource(BoiteReceptionService boiteReceptionService) {
        this.boiteReceptionService = boiteReceptionService;
    }

    /**
     * {@code GET  /boite-receptions/mienne} : la boite du profil de l'utilisateur courant.
     */
    @GetMapping("/mienne")
    public ResponseEntity<BoiteReceptionDTO> getMaBoite() {
        LOG.debug("REST request to get the current user's BoiteReception");
        return ResponseEntity.ok(boiteReceptionService.maBoite());
    }

    /**
     * {@code GET  /boite-receptions/mienne/taches} : les taches que les droits de l'utilisateur
     * courant lui donnent a traiter.
     */
    @GetMapping("/mienne/taches")
    public ResponseEntity<List<TacheDTO>> getMesTaches(
        @RequestParam(name = "ouvertes", required = false, defaultValue = "false") boolean ouvertes,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get the tasks addressed to the current user's BoiteReception");
        Page<TacheDTO> page = boiteReceptionService.mesTaches(pageable, ouvertes);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code PUT  /boite-receptions/mienne/consultee} : enregistre que la boite vient d'etre ouverte.
     */
    @PutMapping("/mienne/consultee")
    public ResponseEntity<BoiteReceptionDTO> marquerConsultee() {
        LOG.debug("REST request to mark the current user's BoiteReception as consulted");
        return ResponseEntity.ok(boiteReceptionService.marquerConsultee());
    }

    /**
     * {@code PUT  /boite-receptions/mienne/taches/:id/lue} : marque une tache de sa boite comme lue.
     */
    @PutMapping("/mienne/taches/{id}/lue")
    public ResponseEntity<TacheDTO> marquerTacheLue(@PathVariable("id") Long id) {
        LOG.debug("REST request to mark Tache {} as read in the current user's BoiteReception", id);
        return ResponseUtil.wrapOrNotFound(boiteReceptionService.marquerTacheLue(id));
    }

    /**
     * {@code PUT  /boite-receptions/mienne/taches/:id/prendre} : s'attribuer une tache de sa boite.
     */
    @PutMapping("/mienne/taches/{id}/prendre")
    public ResponseEntity<TacheDTO> prendreEnCharge(@PathVariable("id") Long id) {
        LOG.debug("REST request to take charge of Tache {}", id);
        return ResponseUtil.wrapOrNotFound(boiteReceptionService.prendreEnCharge(id));
    }

    /**
     * {@code PUT  /boite-receptions/mienne/taches/:id/relacher} : rendre une tache a la file.
     */
    @PutMapping("/mienne/taches/{id}/relacher")
    public ResponseEntity<TacheDTO> relacher(@PathVariable("id") Long id) {
        LOG.debug("REST request to release Tache {}", id);
        return ResponseUtil.wrapOrNotFound(boiteReceptionService.relacher(id));
    }

    /**
     * {@code POST  /boite-receptions/synchroniser} : cree les boites manquantes et recalcule les
     * compteurs. Idempotent.
     */
    @PostMapping("/synchroniser")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Map<String, Integer>> synchroniser() {
        LOG.debug("REST request to synchronise the BoiteReceptions");
        return ResponseEntity.ok(Map.of("boitesCreees", boiteReceptionService.synchroniser()));
    }

    /**
     * {@code GET  /boite-receptions} : toutes les boites, pour l'administration.
     */
    @GetMapping("")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<BoiteReceptionDTO>> getAllBoiteReceptions(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get a page of BoiteReceptions");
        Page<BoiteReceptionDTO> page = boiteReceptionService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /boite-receptions/:id} : une boite, pour l'administration.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<BoiteReceptionDTO> getBoiteReception(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BoiteReception : {}", id);
        Optional<BoiteReceptionDTO> boiteReceptionDTO = boiteReceptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(boiteReceptionDTO);
    }
}
