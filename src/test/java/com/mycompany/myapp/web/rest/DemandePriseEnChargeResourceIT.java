package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DemandePriseEnChargeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.DemandePriseEnChargeService;
import com.mycompany.myapp.service.dto.DemandePriseEnChargeDTO;
import com.mycompany.myapp.service.mapper.DemandePriseEnChargeMapper;
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
 * Integration tests for the {@link DemandePriseEnChargeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DemandePriseEnChargeResourceIT {

    private static final String DEFAULT_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_REFERENCE = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.ofEpochMilli(1703483747250L);

    private static final Instant DEFAULT_DATE_MODIFICATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_MODIFICATION = Instant.ofEpochMilli(1703483747250L);

    private static final TypeBeneficiaire DEFAULT_TYPE_BENEFICIAIRE = TypeBeneficiaire.AGENT;
    private static final TypeBeneficiaire UPDATED_TYPE_BENEFICIAIRE = TypeBeneficiaire.AYANT_DROIT;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final StatutDemande DEFAULT_STATUT = StatutDemande.NOUVELLE;
    private static final StatutDemande UPDATED_STATUT = StatutDemande.EN_ATTENTE_PIECES;

    private static final Instant DEFAULT_DATE_ASSIGNATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ASSIGNATION = Instant.ofEpochMilli(1703483747250L);

    private static final String ENTITY_API_URL = "/api/demande-prise-en-charges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private DemandePriseEnChargeRepository demandePriseEnChargeRepositoryMock;

    @Autowired
    private DemandePriseEnChargeMapper demandePriseEnChargeMapper;

    @Mock
    private DemandePriseEnChargeService demandePriseEnChargeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDemandePriseEnChargeMockMvc;

    private DemandePriseEnCharge demandePriseEnCharge;

    private DemandePriseEnCharge insertedDemandePriseEnCharge;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandePriseEnCharge createEntity(EntityManager em) {
        DemandePriseEnCharge demandePriseEnCharge = new DemandePriseEnCharge()
            .reference(DEFAULT_REFERENCE)
            .dateCreation(DEFAULT_DATE_CREATION)
            .dateModification(DEFAULT_DATE_MODIFICATION)
            .typeBeneficiaire(DEFAULT_TYPE_BENEFICIAIRE)
            .description(DEFAULT_DESCRIPTION)
            .statut(DEFAULT_STATUT)
            .dateAssignation(DEFAULT_DATE_ASSIGNATION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        demandePriseEnCharge.setGestionnaireCreateur(user);
        return demandePriseEnCharge;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DemandePriseEnCharge createUpdatedEntity(EntityManager em) {
        DemandePriseEnCharge updatedDemandePriseEnCharge = new DemandePriseEnCharge()
            .reference(UPDATED_REFERENCE)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateModification(UPDATED_DATE_MODIFICATION)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateAssignation(UPDATED_DATE_ASSIGNATION);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedDemandePriseEnCharge.setGestionnaireCreateur(user);
        return updatedDemandePriseEnCharge;
    }

    @BeforeEach
    void initTest() {
        demandePriseEnCharge = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedDemandePriseEnCharge != null) {
            demandePriseEnChargeRepository.delete(insertedDemandePriseEnCharge);
            insertedDemandePriseEnCharge = null;
        }
    }

    @Test
    @Transactional
    void createDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);
        var returnedDemandePriseEnChargeDTO = om.readValue(
            restDemandePriseEnChargeMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DemandePriseEnChargeDTO.class
        );

        // Validate the DemandePriseEnCharge in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDemandePriseEnCharge = demandePriseEnChargeMapper.toEntity(returnedDemandePriseEnChargeDTO);
        assertDemandePriseEnChargeUpdatableFieldsEquals(
            returnedDemandePriseEnCharge,
            getPersistedDemandePriseEnCharge(returnedDemandePriseEnCharge)
        );

        insertedDemandePriseEnCharge = returnedDemandePriseEnCharge;
    }

    @Test
    @Transactional
    void createDemandePriseEnChargeWithExistingId() throws Exception {
        // Create the DemandePriseEnCharge with an existing ID
        demandePriseEnCharge.setId(1L);
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDemandePriseEnChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReferenceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandePriseEnCharge.setReference(null);

        // Create the DemandePriseEnCharge, which fails.
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        restDemandePriseEnChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandePriseEnCharge.setDateCreation(null);

        // Create the DemandePriseEnCharge, which fails.
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        restDemandePriseEnChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeBeneficiaireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandePriseEnCharge.setTypeBeneficiaire(null);

        // Create the DemandePriseEnCharge, which fails.
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        restDemandePriseEnChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        demandePriseEnCharge.setStatut(null);

        // Create the DemandePriseEnCharge, which fails.
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        restDemandePriseEnChargeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnCharges() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demandePriseEnCharge.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(DEFAULT_DATE_MODIFICATION.toString())))
            .andExpect(jsonPath("$.[*].typeBeneficiaire").value(hasItem(DEFAULT_TYPE_BENEFICIAIRE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateAssignation").value(hasItem(DEFAULT_DATE_ASSIGNATION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandePriseEnChargesWithEagerRelationshipsIsEnabled() throws Exception {
        when(demandePriseEnChargeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandePriseEnChargeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(demandePriseEnChargeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDemandePriseEnChargesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(demandePriseEnChargeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDemandePriseEnChargeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(demandePriseEnChargeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDemandePriseEnCharge() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get the demandePriseEnCharge
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL_ID, demandePriseEnCharge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(demandePriseEnCharge.getId().intValue()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.dateModification").value(DEFAULT_DATE_MODIFICATION.toString()))
            .andExpect(jsonPath("$.typeBeneficiaire").value(DEFAULT_TYPE_BENEFICIAIRE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT.toString()))
            .andExpect(jsonPath("$.dateAssignation").value(DEFAULT_DATE_ASSIGNATION.toString()));
    }

    @Test
    @Transactional
    void getDemandePriseEnChargesByIdFiltering() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        Long id = demandePriseEnCharge.getId();

        defaultDemandePriseEnChargeFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDemandePriseEnChargeFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDemandePriseEnChargeFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where reference equals to
        defaultDemandePriseEnChargeFiltering("reference.equals=" + DEFAULT_REFERENCE, "reference.equals=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where reference in
        defaultDemandePriseEnChargeFiltering(
            "reference.in=" + DEFAULT_REFERENCE + "," + UPDATED_REFERENCE,
            "reference.in=" + UPDATED_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where reference is not null
        defaultDemandePriseEnChargeFiltering("reference.specified=true", "reference.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByReferenceContainsSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where reference contains
        defaultDemandePriseEnChargeFiltering("reference.contains=" + DEFAULT_REFERENCE, "reference.contains=" + UPDATED_REFERENCE);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where reference does not contain
        defaultDemandePriseEnChargeFiltering(
            "reference.doesNotContain=" + UPDATED_REFERENCE,
            "reference.doesNotContain=" + DEFAULT_REFERENCE
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateCreationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateCreation equals to
        defaultDemandePriseEnChargeFiltering(
            "dateCreation.equals=" + DEFAULT_DATE_CREATION,
            "dateCreation.equals=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateCreationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateCreation in
        defaultDemandePriseEnChargeFiltering(
            "dateCreation.in=" + DEFAULT_DATE_CREATION + "," + UPDATED_DATE_CREATION,
            "dateCreation.in=" + UPDATED_DATE_CREATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateCreationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateCreation is not null
        defaultDemandePriseEnChargeFiltering("dateCreation.specified=true", "dateCreation.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateModificationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateModification equals to
        defaultDemandePriseEnChargeFiltering(
            "dateModification.equals=" + DEFAULT_DATE_MODIFICATION,
            "dateModification.equals=" + UPDATED_DATE_MODIFICATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateModificationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateModification in
        defaultDemandePriseEnChargeFiltering(
            "dateModification.in=" + DEFAULT_DATE_MODIFICATION + "," + UPDATED_DATE_MODIFICATION,
            "dateModification.in=" + UPDATED_DATE_MODIFICATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateModificationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateModification is not null
        defaultDemandePriseEnChargeFiltering("dateModification.specified=true", "dateModification.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByTypeBeneficiaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where typeBeneficiaire equals to
        defaultDemandePriseEnChargeFiltering(
            "typeBeneficiaire.equals=" + DEFAULT_TYPE_BENEFICIAIRE,
            "typeBeneficiaire.equals=" + UPDATED_TYPE_BENEFICIAIRE
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByTypeBeneficiaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where typeBeneficiaire in
        defaultDemandePriseEnChargeFiltering(
            "typeBeneficiaire.in=" + DEFAULT_TYPE_BENEFICIAIRE + "," + UPDATED_TYPE_BENEFICIAIRE,
            "typeBeneficiaire.in=" + UPDATED_TYPE_BENEFICIAIRE
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByTypeBeneficiaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where typeBeneficiaire is not null
        defaultDemandePriseEnChargeFiltering("typeBeneficiaire.specified=true", "typeBeneficiaire.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where description equals to
        defaultDemandePriseEnChargeFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where description in
        defaultDemandePriseEnChargeFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where description is not null
        defaultDemandePriseEnChargeFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where description contains
        defaultDemandePriseEnChargeFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where description does not contain
        defaultDemandePriseEnChargeFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where statut equals to
        defaultDemandePriseEnChargeFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where statut in
        defaultDemandePriseEnChargeFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where statut is not null
        defaultDemandePriseEnChargeFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateAssignationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateAssignation equals to
        defaultDemandePriseEnChargeFiltering(
            "dateAssignation.equals=" + DEFAULT_DATE_ASSIGNATION,
            "dateAssignation.equals=" + UPDATED_DATE_ASSIGNATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateAssignationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateAssignation in
        defaultDemandePriseEnChargeFiltering(
            "dateAssignation.in=" + DEFAULT_DATE_ASSIGNATION + "," + UPDATED_DATE_ASSIGNATION,
            "dateAssignation.in=" + UPDATED_DATE_ASSIGNATION
        );
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByDateAssignationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        // Get all the demandePriseEnChargeList where dateAssignation is not null
        defaultDemandePriseEnChargeFiltering("dateAssignation.specified=true", "dateAssignation.specified=false");
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        demandePriseEnCharge.setAgent(agent);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long agentId = agent.getId();
        // Get all the demandePriseEnChargeList where agent equals to agentId
        defaultDemandePriseEnChargeShouldBeFound("agentId.equals=" + agentId);

        // Get all the demandePriseEnChargeList where agent equals to (agentId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByAyantDroitIsEqualToSomething() throws Exception {
        AyantDroit ayantDroit;
        if (TestUtil.findAll(em, AyantDroit.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            ayantDroit = AyantDroitResourceIT.createEntity();
        } else {
            ayantDroit = TestUtil.findAll(em, AyantDroit.class).get(0);
        }
        em.persist(ayantDroit);
        em.flush();
        demandePriseEnCharge.setAyantDroit(ayantDroit);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long ayantDroitId = ayantDroit.getId();
        // Get all the demandePriseEnChargeList where ayantDroit equals to ayantDroitId
        defaultDemandePriseEnChargeShouldBeFound("ayantDroitId.equals=" + ayantDroitId);

        // Get all the demandePriseEnChargeList where ayantDroit equals to (ayantDroitId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("ayantDroitId.equals=" + (ayantDroitId + 1));
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByTypeSoinIsEqualToSomething() throws Exception {
        TypeSoin typeSoin;
        if (TestUtil.findAll(em, TypeSoin.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            typeSoin = TypeSoinResourceIT.createEntity();
        } else {
            typeSoin = TestUtil.findAll(em, TypeSoin.class).get(0);
        }
        em.persist(typeSoin);
        em.flush();
        demandePriseEnCharge.setTypeSoin(typeSoin);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long typeSoinId = typeSoin.getId();
        // Get all the demandePriseEnChargeList where typeSoin equals to typeSoinId
        defaultDemandePriseEnChargeShouldBeFound("typeSoinId.equals=" + typeSoinId);

        // Get all the demandePriseEnChargeList where typeSoin equals to (typeSoinId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("typeSoinId.equals=" + (typeSoinId + 1));
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByEtablissementSanteIsEqualToSomething() throws Exception {
        EtablissementSante etablissementSante;
        if (TestUtil.findAll(em, EtablissementSante.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            etablissementSante = EtablissementSanteResourceIT.createEntity();
        } else {
            etablissementSante = TestUtil.findAll(em, EtablissementSante.class).get(0);
        }
        em.persist(etablissementSante);
        em.flush();
        demandePriseEnCharge.setEtablissementSante(etablissementSante);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long etablissementSanteId = etablissementSante.getId();
        // Get all the demandePriseEnChargeList where etablissementSante equals to etablissementSanteId
        defaultDemandePriseEnChargeShouldBeFound("etablissementSanteId.equals=" + etablissementSanteId);

        // Get all the demandePriseEnChargeList where etablissementSante equals to (etablissementSanteId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("etablissementSanteId.equals=" + (etablissementSanteId + 1));
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByGestionnaireCreateurIsEqualToSomething() throws Exception {
        User gestionnaireCreateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            gestionnaireCreateur = UserResourceIT.createEntity();
        } else {
            gestionnaireCreateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(gestionnaireCreateur);
        em.flush();
        demandePriseEnCharge.setGestionnaireCreateur(gestionnaireCreateur);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long gestionnaireCreateurId = gestionnaireCreateur.getId();
        // Get all the demandePriseEnChargeList where gestionnaireCreateur equals to gestionnaireCreateurId
        defaultDemandePriseEnChargeShouldBeFound("gestionnaireCreateurId.equals=" + gestionnaireCreateurId);

        // Get all the demandePriseEnChargeList where gestionnaireCreateur equals to (gestionnaireCreateurId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("gestionnaireCreateurId.equals=" + (gestionnaireCreateurId + 1));
    }

    @Test
    @Transactional
    void getAllDemandePriseEnChargesByAssigneAIsEqualToSomething() throws Exception {
        User assigneA;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
            assigneA = UserResourceIT.createEntity();
        } else {
            assigneA = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(assigneA);
        em.flush();
        demandePriseEnCharge.setAssigneA(assigneA);
        demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);
        Long assigneAId = assigneA.getId();
        // Get all the demandePriseEnChargeList where assigneA equals to assigneAId
        defaultDemandePriseEnChargeShouldBeFound("assigneAId.equals=" + assigneAId);

        // Get all the demandePriseEnChargeList where assigneA equals to (assigneAId + 1)
        defaultDemandePriseEnChargeShouldNotBeFound("assigneAId.equals=" + (assigneAId + 1));
    }

    private void defaultDemandePriseEnChargeFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDemandePriseEnChargeShouldBeFound(shouldBeFound);
        defaultDemandePriseEnChargeShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDemandePriseEnChargeShouldBeFound(String filter) throws Exception {
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(demandePriseEnCharge.getId().intValue())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateModification").value(hasItem(DEFAULT_DATE_MODIFICATION.toString())))
            .andExpect(jsonPath("$.[*].typeBeneficiaire").value(hasItem(DEFAULT_TYPE_BENEFICIAIRE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT.toString())))
            .andExpect(jsonPath("$.[*].dateAssignation").value(hasItem(DEFAULT_DATE_ASSIGNATION.toString())));

        // Check, that the count call also returns 1
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDemandePriseEnChargeShouldNotBeFound(String filter) throws Exception {
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDemandePriseEnChargeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDemandePriseEnCharge() throws Exception {
        // Get the demandePriseEnCharge
        restDemandePriseEnChargeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDemandePriseEnCharge() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandePriseEnCharge
        DemandePriseEnCharge updatedDemandePriseEnCharge = demandePriseEnChargeRepository
            .findById(demandePriseEnCharge.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedDemandePriseEnCharge are not directly saved in db
        em.detach(updatedDemandePriseEnCharge);
        updatedDemandePriseEnCharge
            .reference(UPDATED_REFERENCE)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateModification(UPDATED_DATE_MODIFICATION)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateAssignation(UPDATED_DATE_ASSIGNATION);
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(updatedDemandePriseEnCharge);

        restDemandePriseEnChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandePriseEnChargeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isOk());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDemandePriseEnChargeToMatchAllProperties(updatedDemandePriseEnCharge);
    }

    @Test
    @Transactional
    void putNonExistingDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, demandePriseEnChargeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(demandePriseEnChargeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDemandePriseEnChargeWithPatch() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandePriseEnCharge using partial update
        DemandePriseEnCharge partialUpdatedDemandePriseEnCharge = new DemandePriseEnCharge();
        partialUpdatedDemandePriseEnCharge.setId(demandePriseEnCharge.getId());

        partialUpdatedDemandePriseEnCharge
            .reference(UPDATED_REFERENCE)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .description(UPDATED_DESCRIPTION);

        restDemandePriseEnChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemandePriseEnCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemandePriseEnCharge))
            )
            .andExpect(status().isOk());

        // Validate the DemandePriseEnCharge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandePriseEnChargeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDemandePriseEnCharge, demandePriseEnCharge),
            getPersistedDemandePriseEnCharge(demandePriseEnCharge)
        );
    }

    @Test
    @Transactional
    void fullUpdateDemandePriseEnChargeWithPatch() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the demandePriseEnCharge using partial update
        DemandePriseEnCharge partialUpdatedDemandePriseEnCharge = new DemandePriseEnCharge();
        partialUpdatedDemandePriseEnCharge.setId(demandePriseEnCharge.getId());

        partialUpdatedDemandePriseEnCharge
            .reference(UPDATED_REFERENCE)
            .dateCreation(UPDATED_DATE_CREATION)
            .dateModification(UPDATED_DATE_MODIFICATION)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .description(UPDATED_DESCRIPTION)
            .statut(UPDATED_STATUT)
            .dateAssignation(UPDATED_DATE_ASSIGNATION);

        restDemandePriseEnChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDemandePriseEnCharge.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDemandePriseEnCharge))
            )
            .andExpect(status().isOk());

        // Validate the DemandePriseEnCharge in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDemandePriseEnChargeUpdatableFieldsEquals(
            partialUpdatedDemandePriseEnCharge,
            getPersistedDemandePriseEnCharge(partialUpdatedDemandePriseEnCharge)
        );
    }

    @Test
    @Transactional
    void patchNonExistingDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, demandePriseEnChargeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDemandePriseEnCharge() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        demandePriseEnCharge.setId(longCount.incrementAndGet());

        // Create the DemandePriseEnCharge
        DemandePriseEnChargeDTO demandePriseEnChargeDTO = demandePriseEnChargeMapper.toDto(demandePriseEnCharge);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDemandePriseEnChargeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(demandePriseEnChargeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DemandePriseEnCharge in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDemandePriseEnCharge() throws Exception {
        // Initialize the database
        insertedDemandePriseEnCharge = demandePriseEnChargeRepository.saveAndFlush(demandePriseEnCharge);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the demandePriseEnCharge
        restDemandePriseEnChargeMockMvc
            .perform(delete(ENTITY_API_URL_ID, demandePriseEnCharge.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return demandePriseEnChargeRepository.count();
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

    protected DemandePriseEnCharge getPersistedDemandePriseEnCharge(DemandePriseEnCharge demandePriseEnCharge) {
        return demandePriseEnChargeRepository.findById(demandePriseEnCharge.getId()).orElseThrow();
    }

    protected void assertPersistedDemandePriseEnChargeToMatchAllProperties(DemandePriseEnCharge expectedDemandePriseEnCharge) {
        assertDemandePriseEnChargeAllPropertiesEquals(
            expectedDemandePriseEnCharge,
            getPersistedDemandePriseEnCharge(expectedDemandePriseEnCharge)
        );
    }

    protected void assertPersistedDemandePriseEnChargeToMatchUpdatableProperties(DemandePriseEnCharge expectedDemandePriseEnCharge) {
        assertDemandePriseEnChargeAllUpdatablePropertiesEquals(
            expectedDemandePriseEnCharge,
            getPersistedDemandePriseEnCharge(expectedDemandePriseEnCharge)
        );
    }
}
