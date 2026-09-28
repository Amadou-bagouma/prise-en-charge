package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.ProfilService;
import com.mycompany.myapp.service.dto.ProfilDTO;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BoiteReceptionResource} REST controller.
 *
 * <p>La boite de reception n'a plus de CRUD : elle appartient a un profil, elle est creee
 * automatiquement, et son contenu se deduit des droits. Ces tests verifient donc trois choses
 * qui, si elles cassaient, passeraient inapercues : la boite nait bien avec le profil, un
 * utilisateur ne voit que les taches de son perimetre, et la console globale reste fermee aux
 * non-administrateurs.
 */
@IntegrationTest
@AutoConfigureMockMvc
class BoiteReceptionResourceIT {

    private static final String ENTITY_API_URL = "/api/boite-receptions";

    @Autowired
    private MockMvc restBoiteReceptionMockMvc;

    @Autowired
    private BoiteReceptionRepository boiteReceptionRepository;

    @Autowired
    private ProfilRepository profilRepository;

    @Autowired
    private ProfilService profilService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private EntityManager em;

    private String login;

    @BeforeEach
    void initTest() {
        login = "boite-" + RandomStringUtils.insecure().nextAlphabetic(8).toLowerCase();
    }

    @Test
    @Transactional
    void creerUnProfilCreeSaBoiteDeReception() throws Exception {
        ProfilDTO dto = new ProfilDTO();
        dto.setNom("profil-" + RandomStringUtils.insecure().nextAlphanumeric(10));
        dto.setAuthorities(Set.of(AuthoritiesConstants.USER));

        ProfilDTO cree = profilService.save(dto);

        assertThat(boiteReceptionRepository.existsByProfilId(cree.getId())).as("la boite nait avec le profil, sans intervention").isTrue();
    }

    @Test
    @Transactional
    void synchroniserEstIdempotent() throws Exception {
        creerProfil(AuthoritiesConstants.USER);
        long avant = boiteReceptionRepository.count();

        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL + "/synchroniser").with(user("admin").authorities(autorite(AuthoritiesConstants.ADMIN))))
            .andExpect(status().isOk());
        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL + "/synchroniser").with(user("admin").authorities(autorite(AuthoritiesConstants.ADMIN))))
            .andExpect(status().isOk());

        assertThat(boiteReceptionRepository.count()).as("relancer la synchronisation ne cree pas de doublon").isEqualTo(avant);
    }

    @Test
    @Transactional
    void maBoiteNeMontreQueLesTachesDeMesDroits() throws Exception {
        // Un validateur DRH : seules les demandes en attente de SA validation le concernent.
        Profil profil = creerProfil(AuthoritiesConstants.VALIDATEUR_DRH);
        User utilisateur = creerUtilisateur(profil);
        Tache aMoi = creerTache(utilisateur, StatutDemande.EN_ATTENTE_VALIDATION_DRH);
        Tache pasAMoi = creerTache(utilisateur, StatutDemande.EN_ATTENTE_VALIDATION_INFIRMERIE);
        em.flush();

        restBoiteReceptionMockMvc
            .perform(get(ENTITY_API_URL + "/mienne/taches?size=100").with(user(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[?(@.id == " + aMoi.getId() + ")]").exists())
            .andExpect(jsonPath("$[?(@.id == " + pasAMoi.getId() + ")]").doesNotExist());
    }

    @Test
    @Transactional
    void maBoiteEstCelleDeMonProfil() throws Exception {
        Profil profil = creerProfil(AuthoritiesConstants.VALIDATEUR_DRH);
        creerUtilisateur(profil);
        em.flush();

        restBoiteReceptionMockMvc
            .perform(get(ENTITY_API_URL + "/mienne").with(user(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.profil.id").value(profil.getId().intValue()))
            .andExpect(jsonPath("$.profil.nom").value(profil.getNom()));
    }

    @Test
    @Transactional
    void laConsoleGlobaleEstReserveeAuxAdministrateurs() throws Exception {
        restBoiteReceptionMockMvc.perform(get(ENTITY_API_URL).with(user(login))).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void aucuneRouteDeCreationNiDeSuppression() throws Exception {
        // Une boite ne se cree ni ne se supprime a la main : ces verbes n'existent plus.
        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL).with(user(login)).contentType("application/json").content("{}"))
            .andExpect(status().isMethodNotAllowed());
        restBoiteReceptionMockMvc.perform(delete(ENTITY_API_URL + "/1").with(user(login))).andExpect(status().isMethodNotAllowed());
    }

    private static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user(
        String login
    ) {
        return org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user(login);
    }

    private static org.springframework.security.core.authority.SimpleGrantedAuthority autorite(String nom) {
        return new org.springframework.security.core.authority.SimpleGrantedAuthority(nom);
    }

    private Profil creerProfil(String... droits) {
        Profil profil = new Profil();
        profil.setNom("profil-" + RandomStringUtils.insecure().nextAlphanumeric(10));
        Set<Authority> authorities = new HashSet<>();
        for (String droit : droits) {
            authorityRepository.findById(droit).ifPresent(authorities::add);
        }
        profil.setAuthorities(authorities);
        profil = profilRepository.saveAndFlush(profil);

        BoiteReception boite = new BoiteReception().profil(profil).dateCreation(Instant.now()).nombreNonLus(0).actif(Boolean.TRUE);
        boiteReceptionRepository.saveAndFlush(boite);
        return profil;
    }

    private User creerUtilisateur(Profil profil) {
        User utilisateur = UserResourceIT.createEntity();
        utilisateur.setLogin(login);
        utilisateur.setProfil(profil);
        return userRepository.saveAndFlush(utilisateur);
    }

    private Tache creerTache(User utilisateur, StatutDemande statutDemande) {
        DemandePriseEnCharge demande = DemandePriseEnChargeResourceIT.createEntity(em);
        demande.setStatut(statutDemande);
        em.persist(demande);

        Tache tache = new Tache()
            .titre("tache " + statutDemande)
            .dateCreation(Instant.now())
            .statut(StatutTache.A_FAIRE)
            .priorite(PrioriteTache.NORMALE)
            .lu(Boolean.FALSE)
            .demande(demande)
            .utilisateur(utilisateur);
        return tacheRepository.saveAndFlush(tache);
    }
}
