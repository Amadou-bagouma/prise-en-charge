package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TacheAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.PrioriteTache;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.TacheService;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.TacheMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

/**
 * Integration tests for the {@link TacheResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
// Most tests below only exercise field filtering and are unrelated to task ownership,
// so they run as an admin to keep the pre-existing "see everything" behavior.
// The task-ownership restriction itself is covered by getAllTachesAsNonAdminOnlyReturnsOwnTasks().
@WithMockUser(authorities = AuthoritiesConstants.ADMIN)
class TacheResourceIT {

    private static final String DEFAULT_TITRE = "AAAAAAAAAA";
    private static final String UPDATED_TITRE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.ofEpochMilli(1703483747250L);

    private static final Instant DEFAULT_DATE_ASSIGNATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ASSIGNATION = Instant.ofEpochMilli(1703483747250L);

    private static final Instant DEFAULT_DATE_ECHEANCE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ECHEANCE = Instant.ofEpochMilli(1703483747250L);

    private static final Instant DEFAULT_DATE_TERMINAISON = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_TERMINAISON = Instant.ofEpochMilli(1703483747250L);

    private static final StatutTache DEFAULT_STATUT = StatutTache.A_FAIRE;
    private static final StatutTache UPDATED_STATUT = StatutTache.EN_COURS;

    private static final PrioriteTache DEFAULT_PRIORITE = PrioriteTache.NORMALE;
    private static final PrioriteTache UPDATED_PRIORITE = PrioriteTache.IMPORTANTE;

    private static final Boolean DEFAULT_LU = false;
    private static final Boolean UPDATED_LU = true;

    private static final String DEFAULT_COMMENTAIRE = "AAAAAAAAAA";
    private static final String UPDATED_COMMENTAIRE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/taches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TacheRepository tacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private TacheRepository tacheRepositoryMock;

    @Autowired
    private TacheMapper tacheMapper;

    @Mock
    private TacheService tacheServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTacheMockMvc;

    private Tache tache;

    private Tache insertedTache;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createEntity(EntityManager em) {
        Tache tache = new Tache()
            .titre(DEFAULT_TITRE)
            .description(DEFAULT_DESCRIPTION)
            .dateCreation(DEFAULT_DATE_CREATION)
            .dateAssignation(DEFAULT_DATE_ASSIGNATION)
            .dateEcheance(DEFAULT_DATE_ECHEANCE)
            .dateTerminaison(DEFAULT_DATE_TERMINAISON)
            .statut(DEFAULT_STATUT)
            .priorite(DEFAULT_PRIORITE)
            .lu(DEFAULT_LU)
            .commentaire(DEFAULT_COMMENTAIRE);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        tache.setDemande(demandePriseEnCharge);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        tache.setUtilisateur(user);
        return tache;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tache createUpdatedEntity(EntityManager em) {
        Tache updatedTache = new Tache()
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateAssignation(UPDATED_DATE_ASSIGNATION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .dateTerminaison(UPDATED_DATE_TERMINAISON)
            .statut(UPDATED_STATUT)
            .priorite(UPDATED_PRIORITE)
            .lu(UPDATED_LU)
            .commentaire(UPDATED_COMMENTAIRE);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createUpdatedEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        updatedTache.setDemande(demandePriseEnCharge);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTache.setUtilisateur(user);
        return updatedTache;
    }

    @BeforeEach
    void initTest() {
        tache = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTache != null) {
            tacheRepository.delete(insertedTache);
            insertedTache = null;
        }
    }

    @Test
    @Transactional
    void createTache() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);
        var returnedTacheDTO = om.readValue(
            restTacheMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TacheDTO.class
        );

        // Validate the Tache in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTache = tacheMapper.toEntity(returnedTacheDTO);
        assertTacheUpdatableFieldsEquals(returnedTache, getPersistedTache(returnedTache));

        insertedTache = returnedTache;
    }

    @Test
    @Transactional
    void createTacheWithExistingId() throws Exception {
        // Create the Tache with an existing ID
        tache.setId(1L);
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setTitre(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setDateCreation(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setStatut(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrioriteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setPriorite(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLuIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tache.setLu(null);

        // Create the Tache, which fails.
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        restTacheMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaches() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateAssignation").value(hasItem(DEFAULT_DATE_ASSIGNATION.toString())))
            .andExpect(jsonPath("$.[*].dateEcheance").value(hasItem(DEFAULT_DATE_ECHEANCE.toString())))
            .andExpect(jsonPath("$.[*].dateTerminaison").value(hasItem(DEFAULT_DATE_TERMINAISON.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].priorite").value(hasItem(DEFAULT_PRIORITE.toString())))
            .andExpect(jsonPath("$.[*].lu").value(hasItem(DEFAULT_LU)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTachesWithEagerRelationshipsIsEnabled() throws Exception {
        when(tacheServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTacheMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tacheServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTachesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(tacheServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTacheMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(tacheRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTache() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get the tache
        restTacheMockMvc
            .perform(get(ENTITY_API_URL_ID, tache.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tache.getId().intValue()))
            .andExpect(jsonPath("$.titre").value(DEFAULT_TITRE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.dateAssignation").value(DEFAULT_DATE_ASSIGNATION.toString()))
            .andExpect(jsonPath("$.dateEcheance").value(DEFAULT_DATE_ECHEANCE.toString()))
            .andExpect(jsonPath("$.dateTerminaison").value(DEFAULT_DATE_TERMINAISON.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.priorite").value(DEFAULT_PRIORITE.toString()))
            .andExpect(jsonPath("$.lu").value(DEFAULT_LU))
            .andExpect(jsonPath("$.commentaire").value(DEFAULT_COMMENTAIRE));
    }

    @Test
    @Transactional
    void getTachesByIdFiltering() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        Long id = tache.getId();

        defaultTacheFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTacheFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTacheFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre equals to
        defaultTacheFiltering("titre.equals=" + DEFAULT_TITRE, "titre.equals=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre in
        defaultTacheFiltering("titre.in=" + DEFAULT_TITRE + "," + UPDATED_TITRE, "titre.in=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre is not null
        defaultTacheFiltering("titre.specified=true", "titre.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByTitreContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre contains
        defaultTacheFiltering("titre.contains=" + DEFAULT_TITRE, "titre.contains=" + UPDATED_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByTitreNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where titre does not contain
        defaultTacheFiltering("titre.doesNotContain=" + UPDATED_TITRE, "titre.doesNotContain=" + DEFAULT_TITRE);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description equals to
        defaultTacheFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description in
        defaultTacheFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description is not null
        defaultTacheFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description contains
        defaultTacheFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where description does not contain
        defaultTacheFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTachesByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateCreation equals to
        defaultTacheFiltering("dateCreation.equals=" + DEFAULT_DATE_CREATION, "dateCreation.equals=" + UPDATED_DATE_CREATION);
    }

    @Test
    @Transactional
    void getAllTachesByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateCreation in
        defaultTacheFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllTachesByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateCreation is not null
        defaultTacheFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByDateAssignationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateAssignation equals to
        defaultTacheFiltering("dateAssignation.equals=" + DEFAULT_DATE_ASSIGNATION, "dateAssignation.equals=" + UPDATED_DATE_ASSIGNATION);
    }

    @Test
    @Transactional
    void getAllTachesByDateAssignationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateAssignation in
        defaultTacheFiltering(
            "dateAssignation.in=" + DEFAULT_DATE_ASSIGNATION + "," + UPDATED_DATE_ASSIGNATION,
            "dateAssignation.in=" + UPDATED_DATE_ASSIGNATION
        );
    }

    @Test
    @Transactional
    void getAllTachesByDateAssignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateAssignation is not null
        defaultTacheFiltering("dateAssignation.specified=true", "dateAssignation.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByDateEcheanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateEcheance equals to
        defaultTacheFiltering("dateEcheance.equals=" + DEFAULT_DATE_ECHEANCE, "dateEcheance.equals=" + UPDATED_DATE_ECHEANCE);
    }

    @Test
    @Transactional
    void getAllTachesByDateEcheanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateEcheance in
        defaultTacheFiltering(
            "dateEcheance.in=" + DEFAULT_DATE_ECHEANCE + "," + UPDATED_DATE_ECHEANCE,
            "dateEcheance.in=" + UPDATED_DATE_ECHEANCE
        );
    }

    @Test
    @Transactional
    void getAllTachesByDateEcheanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateEcheance is not null
        defaultTacheFiltering("dateEcheance.specified=true", "dateEcheance.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByDateTerminaisonIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateTerminaison equals to
        defaultTacheFiltering("dateTerminaison.equals=" + DEFAULT_DATE_TERMINAISON, "dateTerminaison.equals=" + UPDATED_DATE_TERMINAISON);
    }

    @Test
    @Transactional
    void getAllTachesByDateTerminaisonIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateTerminaison in
        defaultTacheFiltering(
            "dateTerminaison.in=" + DEFAULT_DATE_TERMINAISON + "," + UPDATED_DATE_TERMINAISON,
            "dateTerminaison.in=" + UPDATED_DATE_TERMINAISON
        );
    }

    @Test
    @Transactional
    void getAllTachesByDateTerminaisonIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where dateTerminaison is not null
        defaultTacheFiltering("dateTerminaison.specified=true", "dateTerminaison.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statut equals to
        defaultTacheFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllTachesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statut in
        defaultTacheFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllTachesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where statut is not null
        defaultTacheFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByPrioriteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where priorite equals to
        defaultTacheFiltering("priorite.equals=" + DEFAULT_PRIORITE, "priorite.equals=" + UPDATED_PRIORITE);
    }

    @Test
    @Transactional
    void getAllTachesByPrioriteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where priorite in
        defaultTacheFiltering("priorite.in=" + DEFAULT_PRIORITE + "," + UPDATED_PRIORITE, "priorite.in=" + UPDATED_PRIORITE);
    }

    @Test
    @Transactional
    void getAllTachesByPrioriteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where priorite is not null
        defaultTacheFiltering("priorite.specified=true", "priorite.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByLuIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where lu equals to
        defaultTacheFiltering("lu.equals=" + DEFAULT_LU, "lu.equals=" + UPDATED_LU);
    }

    @Test
    @Transactional
    void getAllTachesByLuIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where lu in
        defaultTacheFiltering("lu.in=" + DEFAULT_LU + "," + UPDATED_LU, "lu.in=" + UPDATED_LU);
    }

    @Test
    @Transactional
    void getAllTachesByLuIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where lu is not null
        defaultTacheFiltering("lu.specified=true", "lu.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByCommentaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where commentaire equals to
        defaultTacheFiltering("commentaire.equals=" + DEFAULT_COMMENTAIRE, "commentaire.equals=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllTachesByCommentaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where commentaire in
        defaultTacheFiltering("commentaire.in=" + DEFAULT_COMMENTAIRE + "," + UPDATED_COMMENTAIRE, "commentaire.in=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllTachesByCommentaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where commentaire is not null
        defaultTacheFiltering("commentaire.specified=true", "commentaire.specified=false");
    }

    @Test
    @Transactional
    void getAllTachesByCommentaireContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where commentaire contains
        defaultTacheFiltering("commentaire.contains=" + DEFAULT_COMMENTAIRE, "commentaire.contains=" + UPDATED_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllTachesByCommentaireNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        // Get all the tacheList where commentaire does not contain
        defaultTacheFiltering("commentaire.doesNotContain=" + UPDATED_COMMENTAIRE, "commentaire.doesNotContain=" + DEFAULT_COMMENTAIRE);
    }

    @Test
    @Transactional
    void getAllTachesByDemandeIsEqualToSomething() throws Exception {
        DemandePriseEnCharge demande;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            tacheRepository.saveAndFlush(tache);
            demande = DemandePriseEnChargeResourceIT.createEntity(em);
        } else {
            demande = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        em.persist(demande);
        em.flush();
        tache.setDemande(demande);
        tacheRepository.saveAndFlush(tache);
        Long demandeId = demande.getId();
        // Get all the tacheList where demande equals to demandeId
        defaultTacheShouldBeFound("demandeId.equals=" + demandeId);

        // Get all the tacheList where demande equals to (demandeId + 1)
        defaultTacheShouldNotBeFound("demandeId.equals=" + (demandeId + 1));
    }

    @Test
    @Transactional
    void getAllTachesByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            tacheRepository.saveAndFlush(tache);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        tache.setUtilisateur(utilisateur);
        tacheRepository.saveAndFlush(tache);
        Long utilisateurId = utilisateur.getId();
        // Get all the tacheList where utilisateur equals to utilisateurId
        defaultTacheShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the tacheList where utilisateur equals to (utilisateurId + 1)
        defaultTacheShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    @Test
    @Transactional
    void getAllTachesByBoiteReceptionIsEqualToSomething() throws Exception {
        BoiteReception boiteReception;
        if (TestUtil.findAll(em, BoiteReception.class).isEmpty()) {
            tacheRepository.saveAndFlush(tache);
            boiteReception = BoiteReceptionResourceIT.createEntity(em);
        } else {
            boiteReception = TestUtil.findAll(em, BoiteReception.class).get(0);
        }
        em.persist(boiteReception);
        em.flush();
        tache.setBoiteReception(boiteReception);
        tacheRepository.saveAndFlush(tache);
        Long boiteReceptionId = boiteReception.getId();
        // Get all the tacheList where boiteReception equals to boiteReceptionId
        defaultTacheShouldBeFound("boiteReceptionId.equals=" + boiteReceptionId);

        // Get all the tacheList where boiteReception equals to (boiteReceptionId + 1)
        defaultTacheShouldNotBeFound("boiteReceptionId.equals=" + (boiteReceptionId + 1));
    }

    private void defaultTacheFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTacheShouldBeFound(shouldBeFound);
        defaultTacheShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTacheShouldBeFound(String filter) throws Exception {
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tache.getId().intValue())))
            .andExpect(jsonPath("$.[*].titre").value(hasItem(DEFAULT_TITRE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateAssignation").value(hasItem(DEFAULT_DATE_ASSIGNATION.toString())))
            .andExpect(jsonPath("$.[*].dateEcheance").value(hasItem(DEFAULT_DATE_ECHEANCE.toString())))
            .andExpect(jsonPath("$.[*].dateTerminaison").value(hasItem(DEFAULT_DATE_TERMINAISON.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].priorite").value(hasItem(DEFAULT_PRIORITE.toString())))
            .andExpect(jsonPath("$.[*].lu").value(hasItem(DEFAULT_LU)))
            .andExpect(jsonPath("$.[*].commentaire").value(hasItem(DEFAULT_COMMENTAIRE)));

        // Check, that the count call also returns 1
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTacheShouldNotBeFound(String filter) throws Exception {
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTacheMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTache() throws Exception {
        // Get the tache
        restTacheMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTache() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache
        Tache updatedTache = tacheRepository.findById(tache.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTache are not directly saved in db
        em.detach(updatedTache);
        updatedTache
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateAssignation(UPDATED_DATE_ASSIGNATION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .dateTerminaison(UPDATED_DATE_TERMINAISON)
            .statut(UPDATED_STATUT)
            .priorite(UPDATED_PRIORITE)
            .lu(UPDATED_LU)
            .commentaire(UPDATED_COMMENTAIRE);
        TacheDTO tacheDTO = tacheMapper.toDto(updatedTache);

        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tacheDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTacheToMatchAllProperties(updatedTache);
    }

    @Test
    @Transactional
    void putNonExistingTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tacheDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .titre(UPDATED_TITRE)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateTerminaison(UPDATED_DATE_TERMINAISON)
            .lu(UPDATED_LU);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTacheUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTache, tache), getPersistedTache(tache));
    }

    @Test
    @Transactional
    void fullUpdateTacheWithPatch() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tache using partial update
        Tache partialUpdatedTache = new Tache();
        partialUpdatedTache.setId(tache.getId());

        partialUpdatedTache
            .titre(UPDATED_TITRE)
            .description(UPDATED_DESCRIPTION)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateAssignation(UPDATED_DATE_ASSIGNATION)
            .dateEcheance(UPDATED_DATE_ECHEANCE)
            .dateTerminaison(UPDATED_DATE_TERMINAISON)
            .statut(UPDATED_STATUT)
            .priorite(UPDATED_PRIORITE)
            .lu(UPDATED_LU)
            .commentaire(UPDATED_COMMENTAIRE);

        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTache.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTache))
            )
            .andExpect(status().isOk());

        // Validate the Tache in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTacheUpdatableFieldsEquals(partialUpdatedTache, getPersistedTache(partialUpdatedTache));
    }

    @Test
    @Transactional
    void patchNonExistingTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tacheDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tacheDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTache() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tache.setId(longCount.incrementAndGet());

        // Create the Tache
        TacheDTO tacheDTO = tacheMapper.toDto(tache);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTacheMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tacheDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tache in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTache() throws Exception {
        // Initialize the database
        insertedTache = tacheRepository.saveAndFlush(tache);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tache
        restTacheMockMvc
            .perform(delete(ENTITY_API_URL_ID, tache.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    @WithMockUser(username = "nonadmintaskowner")
    void getAllTachesAsNonAdminOnlyReturnsOwnTasks() throws Exception {
        // A task assigned to the currently authenticated (non-admin) user
        User currentUser = UserResourceIT.createEntity();
        currentUser.setLogin("nonadmintaskowner");
        em.persist(currentUser);
        Tache ownTache = createEntity(em);
        ownTache.setUtilisateur(currentUser);
        insertedTache = tacheRepository.saveAndFlush(ownTache);

        // A task assigned to a different user
        Tache otherTache = tacheRepository.saveAndFlush(createEntity(em));

        try {
            // The list only contains the current user's own task
            restTacheMockMvc
                .perform(get(ENTITY_API_URL + "?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id").value(hasItem(ownTache.getId().intValue())))
                .andExpect(jsonPath("$.[*].id").value(not(hasItem(otherTache.getId().intValue()))));

            // Fetching another user's task directly by id is not found either
            restTacheMockMvc.perform(get(ENTITY_API_URL_ID, otherTache.getId())).andExpect(status().isNotFound());

            // The current user's own task remains reachable by id
            restTacheMockMvc.perform(get(ENTITY_API_URL_ID, ownTache.getId())).andExpect(status().isOk());
        } finally {
            tacheRepository.delete(otherTache);
        }
    }

    protected long getRepositoryCount() {
        return tacheRepository.count();
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

    protected Tache getPersistedTache(Tache tache) {
        return tacheRepository.findById(tache.getId()).orElseThrow();
    }

    protected void assertPersistedTacheToMatchAllProperties(Tache expectedTache) {
        assertTacheAllPropertiesEquals(expectedTache, getPersistedTache(expectedTache));
    }

    protected void assertPersistedTacheToMatchUpdatableProperties(Tache expectedTache) {
        assertTacheAllUpdatablePropertiesEquals(expectedTache, getPersistedTache(expectedTache));
    }
}
