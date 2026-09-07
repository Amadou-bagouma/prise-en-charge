package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EtablissementSanteAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.repository.EtablissementSanteRepository;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import com.mycompany.myapp.service.mapper.EtablissementSanteMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link EtablissementSanteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EtablissementSanteResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_ADRESSE = "AAAAAAAAAA";
    private static final String UPDATED_ADRESSE = "BBBBBBBBBB";

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/etablissement-santes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EtablissementSanteRepository etablissementSanteRepository;

    @Autowired
    private EtablissementSanteMapper etablissementSanteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEtablissementSanteMockMvc;

    private EtablissementSante etablissementSante;

    private EtablissementSante insertedEtablissementSante;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtablissementSante createEntity() {
        return new EtablissementSante()
            .code(DEFAULT_CODE)
            .nom(DEFAULT_NOM)
            .adresse(DEFAULT_ADRESSE)
            .telephone(DEFAULT_TELEPHONE)
            .actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EtablissementSante createUpdatedEntity() {
        return new EtablissementSante()
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        etablissementSante = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEtablissementSante != null) {
            etablissementSanteRepository.delete(insertedEtablissementSante);
            insertedEtablissementSante = null;
        }
    }

    @Test
    @Transactional
    void createEtablissementSante() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);
        var returnedEtablissementSanteDTO = om.readValue(
            restEtablissementSanteMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EtablissementSanteDTO.class
        );

        // Validate the EtablissementSante in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEtablissementSante = etablissementSanteMapper.toEntity(returnedEtablissementSanteDTO);
        assertEtablissementSanteUpdatableFieldsEquals(
            returnedEtablissementSante,
            getPersistedEtablissementSante(returnedEtablissementSante)
        );

        insertedEtablissementSante = returnedEtablissementSante;
    }

    @Test
    @Transactional
    void createEtablissementSanteWithExistingId() throws Exception {
        // Create the EtablissementSante with an existing ID
        etablissementSante.setId(1L);
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEtablissementSanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etablissementSante.setCode(null);

        // Create the EtablissementSante, which fails.
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        restEtablissementSanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etablissementSante.setNom(null);

        // Create the EtablissementSante, which fails.
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        restEtablissementSanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        etablissementSante.setActif(null);

        // Create the EtablissementSante, which fails.
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        restEtablissementSanteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEtablissementSantes() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etablissementSante.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getEtablissementSante() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get the etablissementSante
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL_ID, etablissementSante.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(etablissementSante.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.adresse").value(DEFAULT_ADRESSE))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getEtablissementSantesByIdFiltering() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        Long id = etablissementSante.getId();

        defaultEtablissementSanteFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEtablissementSanteFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEtablissementSanteFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where code equals to
        defaultEtablissementSanteFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where code in
        defaultEtablissementSanteFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where code is not null
        defaultEtablissementSanteFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where code contains
        defaultEtablissementSanteFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where code does not contain
        defaultEtablissementSanteFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where nom equals to
        defaultEtablissementSanteFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where nom in
        defaultEtablissementSanteFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where nom is not null
        defaultEtablissementSanteFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where nom contains
        defaultEtablissementSanteFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where nom does not contain
        defaultEtablissementSanteFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByAdresseIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where adresse equals to
        defaultEtablissementSanteFiltering("adresse.equals=" + DEFAULT_ADRESSE, "adresse.equals=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByAdresseIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where adresse in
        defaultEtablissementSanteFiltering("adresse.in=" + DEFAULT_ADRESSE + "," + UPDATED_ADRESSE, "adresse.in=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByAdresseIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where adresse is not null
        defaultEtablissementSanteFiltering("adresse.specified=true", "adresse.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByAdresseContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where adresse contains
        defaultEtablissementSanteFiltering("adresse.contains=" + DEFAULT_ADRESSE, "adresse.contains=" + UPDATED_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByAdresseNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where adresse does not contain
        defaultEtablissementSanteFiltering("adresse.doesNotContain=" + UPDATED_ADRESSE, "adresse.doesNotContain=" + DEFAULT_ADRESSE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where telephone equals to
        defaultEtablissementSanteFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where telephone in
        defaultEtablissementSanteFiltering(
            "telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE,
            "telephone.in=" + UPDATED_TELEPHONE
        );
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where telephone is not null
        defaultEtablissementSanteFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where telephone contains
        defaultEtablissementSanteFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where telephone does not contain
        defaultEtablissementSanteFiltering(
            "telephone.doesNotContain=" + UPDATED_TELEPHONE,
            "telephone.doesNotContain=" + DEFAULT_TELEPHONE
        );
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByActifIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where actif equals to
        defaultEtablissementSanteFiltering("actif.equals=" + DEFAULT_ACTIF, "actif.equals=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByActifIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where actif in
        defaultEtablissementSanteFiltering("actif.in=" + DEFAULT_ACTIF + "," + UPDATED_ACTIF, "actif.in=" + UPDATED_ACTIF);
    }

    @Test
    @Transactional
    void getAllEtablissementSantesByActifIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        // Get all the etablissementSanteList where actif is not null
        defaultEtablissementSanteFiltering("actif.specified=true", "actif.specified=false");
    }

    private void defaultEtablissementSanteFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEtablissementSanteShouldBeFound(shouldBeFound);
        defaultEtablissementSanteShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEtablissementSanteShouldBeFound(String filter) throws Exception {
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(etablissementSante.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].adresse").value(hasItem(DEFAULT_ADRESSE)))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));

        // Check, that the count call also returns 1
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEtablissementSanteShouldNotBeFound(String filter) throws Exception {
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEtablissementSanteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingEtablissementSante() throws Exception {
        // Get the etablissementSante
        restEtablissementSanteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEtablissementSante() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etablissementSante
        EtablissementSante updatedEtablissementSante = etablissementSanteRepository.findById(etablissementSante.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEtablissementSante are not directly saved in db
        em.detach(updatedEtablissementSante);
        updatedEtablissementSante
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .actif(UPDATED_ACTIF);
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(updatedEtablissementSante);

        restEtablissementSanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etablissementSanteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etablissementSanteDTO))
            )
            .andExpect(status().isOk());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEtablissementSanteToMatchAllProperties(updatedEtablissementSante);
    }

    @Test
    @Transactional
    void putNonExistingEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, etablissementSanteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etablissementSanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(etablissementSanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEtablissementSanteWithPatch() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etablissementSante using partial update
        EtablissementSante partialUpdatedEtablissementSante = new EtablissementSante();
        partialUpdatedEtablissementSante.setId(etablissementSante.getId());

        partialUpdatedEtablissementSante.nom(UPDATED_NOM).adresse(UPDATED_ADRESSE).actif(UPDATED_ACTIF);

        restEtablissementSanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtablissementSante.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtablissementSante))
            )
            .andExpect(status().isOk());

        // Validate the EtablissementSante in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtablissementSanteUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEtablissementSante, etablissementSante),
            getPersistedEtablissementSante(etablissementSante)
        );
    }

    @Test
    @Transactional
    void fullUpdateEtablissementSanteWithPatch() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the etablissementSante using partial update
        EtablissementSante partialUpdatedEtablissementSante = new EtablissementSante();
        partialUpdatedEtablissementSante.setId(etablissementSante.getId());

        partialUpdatedEtablissementSante
            .code(UPDATED_CODE)
            .nom(UPDATED_NOM)
            .adresse(UPDATED_ADRESSE)
            .telephone(UPDATED_TELEPHONE)
            .actif(UPDATED_ACTIF);

        restEtablissementSanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEtablissementSante.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEtablissementSante))
            )
            .andExpect(status().isOk());

        // Validate the EtablissementSante in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEtablissementSanteUpdatableFieldsEquals(
            partialUpdatedEtablissementSante,
            getPersistedEtablissementSante(partialUpdatedEtablissementSante)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, etablissementSanteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etablissementSanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(etablissementSanteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEtablissementSante() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        etablissementSante.setId(longCount.incrementAndGet());

        // Create the EtablissementSante
        EtablissementSanteDTO etablissementSanteDTO = etablissementSanteMapper.toDto(etablissementSante);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEtablissementSanteMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(etablissementSanteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EtablissementSante in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEtablissementSante() throws Exception {
        // Initialize the database
        insertedEtablissementSante = etablissementSanteRepository.saveAndFlush(etablissementSante);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the etablissementSante
        restEtablissementSanteMockMvc
            .perform(delete(ENTITY_API_URL_ID, etablissementSante.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return etablissementSanteRepository.count();
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

    protected EtablissementSante getPersistedEtablissementSante(EtablissementSante etablissementSante) {
        return etablissementSanteRepository.findById(etablissementSante.getId()).orElseThrow();
    }

    protected void assertPersistedEtablissementSanteToMatchAllProperties(EtablissementSante expectedEtablissementSante) {
        assertEtablissementSanteAllPropertiesEquals(expectedEtablissementSante, getPersistedEtablissementSante(expectedEtablissementSante));
    }

    protected void assertPersistedEtablissementSanteToMatchUpdatableProperties(EtablissementSante expectedEtablissementSante) {
        assertEtablissementSanteAllUpdatablePropertiesEquals(
            expectedEtablissementSante,
            getPersistedEtablissementSante(expectedEtablissementSante)
        );
    }
}
