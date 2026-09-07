package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AyantDroitAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.mapper.AyantDroitMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link AyantDroitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AyantDroitResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.parse("2023-12-25");
    private static final LocalDate SMALLER_DATE_NAISSANCE = LocalDate.ofEpochDay(-1L);

    private static final LienParente DEFAULT_LIEN = LienParente.CONJOINT;
    private static final LienParente UPDATED_LIEN = LienParente.ENFANT;

    private static final String ENTITY_API_URL = "/api/ayant-droits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AyantDroitRepository ayantDroitRepository;

    @Autowired
    private AyantDroitMapper ayantDroitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAyantDroitMockMvc;

    private AyantDroit ayantDroit;

    private AyantDroit insertedAyantDroit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AyantDroit createEntity() {
        return new AyantDroit().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).dateNaissance(DEFAULT_DATE_NAISSANCE).lien(DEFAULT_LIEN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AyantDroit createUpdatedEntity() {
        return new AyantDroit().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).dateNaissance(UPDATED_DATE_NAISSANCE).lien(UPDATED_LIEN);
    }

    @BeforeEach
    void initTest() {
        ayantDroit = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAyantDroit != null) {
            ayantDroitRepository.delete(insertedAyantDroit);
            insertedAyantDroit = null;
        }
    }

    @Test
    @Transactional
    void createAyantDroit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);
        var returnedAyantDroitDTO = om.readValue(
            restAyantDroitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AyantDroitDTO.class
        );

        // Validate the AyantDroit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAyantDroit = ayantDroitMapper.toEntity(returnedAyantDroitDTO);
        assertAyantDroitUpdatableFieldsEquals(returnedAyantDroit, getPersistedAyantDroit(returnedAyantDroit));

        insertedAyantDroit = returnedAyantDroit;
    }

    @Test
    @Transactional
    void createAyantDroitWithExistingId() throws Exception {
        // Create the AyantDroit with an existing ID
        ayantDroit.setId(1L);
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAyantDroitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ayantDroit.setNom(null);

        // Create the AyantDroit, which fails.
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        restAyantDroitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ayantDroit.setPrenom(null);

        // Create the AyantDroit, which fails.
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        restAyantDroitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLienIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        ayantDroit.setLien(null);

        // Create the AyantDroit, which fails.
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        restAyantDroitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAyantDroits() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ayantDroit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].lien").value(hasItem(DEFAULT_LIEN.toString())));
    }

    @Test
    @Transactional
    void getAyantDroit() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get the ayantDroit
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL_ID, ayantDroit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(ayantDroit.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.lien").value(DEFAULT_LIEN.toString()));
    }

    @Test
    @Transactional
    void getAyantDroitsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        Long id = ayantDroit.getId();

        defaultAyantDroitFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAyantDroitFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAyantDroitFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where nom equals to
        defaultAyantDroitFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where nom in
        defaultAyantDroitFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where nom is not null
        defaultAyantDroitFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllAyantDroitsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where nom contains
        defaultAyantDroitFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where nom does not contain
        defaultAyantDroitFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where prenom equals to
        defaultAyantDroitFiltering("prenom.equals=" + DEFAULT_PRENOM, "prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where prenom in
        defaultAyantDroitFiltering("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM, "prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where prenom is not null
        defaultAyantDroitFiltering("prenom.specified=true", "prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllAyantDroitsByPrenomContainsSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where prenom contains
        defaultAyantDroitFiltering("prenom.contains=" + DEFAULT_PRENOM, "prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where prenom does not contain
        defaultAyantDroitFiltering("prenom.doesNotContain=" + UPDATED_PRENOM, "prenom.doesNotContain=" + DEFAULT_PRENOM);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance equals to
        defaultAyantDroitFiltering("dateNaissance.equals=" + DEFAULT_DATE_NAISSANCE, "dateNaissance.equals=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance in
        defaultAyantDroitFiltering(
            "dateNaissance.in=" + DEFAULT_DATE_NAISSANCE + "," + UPDATED_DATE_NAISSANCE,
            "dateNaissance.in=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance is not null
        defaultAyantDroitFiltering("dateNaissance.specified=true", "dateNaissance.specified=false");
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance is greater than or equal to
        defaultAyantDroitFiltering(
            "dateNaissance.greaterThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.greaterThanOrEqual=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance is less than or equal to
        defaultAyantDroitFiltering(
            "dateNaissance.lessThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.lessThanOrEqual=" + SMALLER_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance is less than
        defaultAyantDroitFiltering("dateNaissance.lessThan=" + UPDATED_DATE_NAISSANCE, "dateNaissance.lessThan=" + DEFAULT_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByDateNaissanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where dateNaissance is greater than
        defaultAyantDroitFiltering(
            "dateNaissance.greaterThan=" + SMALLER_DATE_NAISSANCE,
            "dateNaissance.greaterThan=" + DEFAULT_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAyantDroitsByLienIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where lien equals to
        defaultAyantDroitFiltering("lien.equals=" + DEFAULT_LIEN, "lien.equals=" + UPDATED_LIEN);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByLienIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where lien in
        defaultAyantDroitFiltering("lien.in=" + DEFAULT_LIEN + "," + UPDATED_LIEN, "lien.in=" + UPDATED_LIEN);
    }

    @Test
    @Transactional
    void getAllAyantDroitsByLienIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        // Get all the ayantDroitList where lien is not null
        defaultAyantDroitFiltering("lien.specified=true", "lien.specified=false");
    }

    @Test
    @Transactional
    void getAllAyantDroitsByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            ayantDroitRepository.saveAndFlush(ayantDroit);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        ayantDroit.setAgent(agent);
        ayantDroitRepository.saveAndFlush(ayantDroit);
        Long agentId = agent.getId();
        // Get all the ayantDroitList where agent equals to agentId
        defaultAyantDroitShouldBeFound("agentId.equals=" + agentId);

        // Get all the ayantDroitList where agent equals to (agentId + 1)
        defaultAyantDroitShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    private void defaultAyantDroitFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAyantDroitShouldBeFound(shouldBeFound);
        defaultAyantDroitShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAyantDroitShouldBeFound(String filter) throws Exception {
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ayantDroit.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].lien").value(hasItem(DEFAULT_LIEN.toString())));

        // Check, that the count call also returns 1
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAyantDroitShouldNotBeFound(String filter) throws Exception {
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAyantDroitMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAyantDroit() throws Exception {
        // Get the ayantDroit
        restAyantDroitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAyantDroit() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ayantDroit
        AyantDroit updatedAyantDroit = ayantDroitRepository.findById(ayantDroit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAyantDroit are not directly saved in db
        em.detach(updatedAyantDroit);
        updatedAyantDroit.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).dateNaissance(UPDATED_DATE_NAISSANCE).lien(UPDATED_LIEN);
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(updatedAyantDroit);

        restAyantDroitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ayantDroitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ayantDroitDTO))
            )
            .andExpect(status().isOk());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAyantDroitToMatchAllProperties(updatedAyantDroit);
    }

    @Test
    @Transactional
    void putNonExistingAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, ayantDroitDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ayantDroitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(ayantDroitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAyantDroitWithPatch() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ayantDroit using partial update
        AyantDroit partialUpdatedAyantDroit = new AyantDroit();
        partialUpdatedAyantDroit.setId(ayantDroit.getId());

        partialUpdatedAyantDroit.nom(UPDATED_NOM).prenom(UPDATED_PRENOM);

        restAyantDroitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAyantDroit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAyantDroit))
            )
            .andExpect(status().isOk());

        // Validate the AyantDroit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAyantDroitUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAyantDroit, ayantDroit),
            getPersistedAyantDroit(ayantDroit)
        );
    }

    @Test
    @Transactional
    void fullUpdateAyantDroitWithPatch() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the ayantDroit using partial update
        AyantDroit partialUpdatedAyantDroit = new AyantDroit();
        partialUpdatedAyantDroit.setId(ayantDroit.getId());

        partialUpdatedAyantDroit.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).dateNaissance(UPDATED_DATE_NAISSANCE).lien(UPDATED_LIEN);

        restAyantDroitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAyantDroit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAyantDroit))
            )
            .andExpect(status().isOk());

        // Validate the AyantDroit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAyantDroitUpdatableFieldsEquals(partialUpdatedAyantDroit, getPersistedAyantDroit(partialUpdatedAyantDroit));
    }

    @Test
    @Transactional
    void patchNonExistingAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, ayantDroitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ayantDroitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(ayantDroitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAyantDroit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        ayantDroit.setId(longCount.incrementAndGet());

        // Create the AyantDroit
        AyantDroitDTO ayantDroitDTO = ayantDroitMapper.toDto(ayantDroit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAyantDroitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(ayantDroitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AyantDroit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAyantDroit() throws Exception {
        // Initialize the database
        insertedAyantDroit = ayantDroitRepository.saveAndFlush(ayantDroit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the ayantDroit
        restAyantDroitMockMvc
            .perform(delete(ENTITY_API_URL_ID, ayantDroit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return ayantDroitRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected AyantDroit getPersistedAyantDroit(AyantDroit ayantDroit) {
        return ayantDroitRepository.findById(ayantDroit.getId()).orElseThrow();
    }

    protected void assertPersistedAyantDroitToMatchAllProperties(AyantDroit expectedAyantDroit) {
        assertAyantDroitAllPropertiesEquals(expectedAyantDroit, getPersistedAyantDroit(expectedAyantDroit));
    }

    protected void assertPersistedAyantDroitToMatchUpdatableProperties(AyantDroit expectedAyantDroit) {
        assertAyantDroitAllUpdatablePropertiesEquals(expectedAyantDroit, getPersistedAyantDroit(expectedAyantDroit));
    }
}
