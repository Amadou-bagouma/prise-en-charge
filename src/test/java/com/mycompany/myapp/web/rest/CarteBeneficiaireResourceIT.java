package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CarteBeneficiaireAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.CarteBeneficiaire;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.repository.CarteBeneficiaireRepository;
import com.mycompany.myapp.service.CarteBeneficiaireService;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import com.mycompany.myapp.service.mapper.CarteBeneficiaireMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
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
 * Integration tests for the {@link CarteBeneficiaireResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class CarteBeneficiaireResourceIT {

    private static final String DEFAULT_NUMERO_CARTE = "AAAAAAAAAA";
    private static final String UPDATED_NUMERO_CARTE = "BBBBBBBBBB";

    private static final TypeBeneficiaire DEFAULT_TYPE_BENEFICIAIRE = TypeBeneficiaire.AGENT;
    private static final TypeBeneficiaire UPDATED_TYPE_BENEFICIAIRE = TypeBeneficiaire.AYANT_DROIT;

    private static final LocalDate DEFAULT_DATE_DEBUT_VALIDITE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_DEBUT_VALIDITE = LocalDate.parse("2023-12-25");
    private static final LocalDate SMALLER_DATE_DEBUT_VALIDITE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_FIN_VALIDITE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FIN_VALIDITE = LocalDate.parse("2023-12-25");
    private static final LocalDate SMALLER_DATE_FIN_VALIDITE = LocalDate.ofEpochDay(-1L);

    private static final LocalDate DEFAULT_DATE_EMISSION = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_EMISSION = LocalDate.parse("2023-12-25");
    private static final LocalDate SMALLER_DATE_EMISSION = LocalDate.ofEpochDay(-1L);

    private static final String ENTITY_API_URL = "/api/carte-beneficiaires";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarteBeneficiaireRepository carteBeneficiaireRepository;

    @Mock
    private CarteBeneficiaireRepository carteBeneficiaireRepositoryMock;

    @Autowired
    private CarteBeneficiaireMapper carteBeneficiaireMapper;

    @Mock
    private CarteBeneficiaireService carteBeneficiaireServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarteBeneficiaireMockMvc;

    private CarteBeneficiaire carteBeneficiaire;

    private CarteBeneficiaire insertedCarteBeneficiaire;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarteBeneficiaire createEntity() {
        return new CarteBeneficiaire()
            .numeroCarte(DEFAULT_NUMERO_CARTE)
            .typeBeneficiaire(DEFAULT_TYPE_BENEFICIAIRE)
            .dateDebutValidite(DEFAULT_DATE_DEBUT_VALIDITE)
            .dateFinValidite(DEFAULT_DATE_FIN_VALIDITE)
            .dateEmission(DEFAULT_DATE_EMISSION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarteBeneficiaire createUpdatedEntity() {
        return new CarteBeneficiaire()
            .numeroCarte(UPDATED_NUMERO_CARTE)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .dateDebutValidite(UPDATED_DATE_DEBUT_VALIDITE)
            .dateFinValidite(UPDATED_DATE_FIN_VALIDITE)
            .dateEmission(UPDATED_DATE_EMISSION);
    }

    @BeforeEach
    void initTest() {
        carteBeneficiaire = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCarteBeneficiaire != null) {
            carteBeneficiaireRepository.delete(insertedCarteBeneficiaire);
            insertedCarteBeneficiaire = null;
        }
    }

    @Test
    @Transactional
    void createCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);
        var returnedCarteBeneficiaireDTO = om.readValue(
            restCarteBeneficiaireMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarteBeneficiaireDTO.class
        );

        // Validate the CarteBeneficiaire in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarteBeneficiaire = carteBeneficiaireMapper.toEntity(returnedCarteBeneficiaireDTO);
        assertCarteBeneficiaireUpdatableFieldsEquals(returnedCarteBeneficiaire, getPersistedCarteBeneficiaire(returnedCarteBeneficiaire));

        insertedCarteBeneficiaire = returnedCarteBeneficiaire;
    }

    @Test
    @Transactional
    void createCarteBeneficiaireWithExistingId() throws Exception {
        // Create the CarteBeneficiaire with an existing ID
        carteBeneficiaire.setId(1L);
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNumeroCarteIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carteBeneficiaire.setNumeroCarte(null);

        // Create the CarteBeneficiaire, which fails.
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeBeneficiaireIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carteBeneficiaire.setTypeBeneficiaire(null);

        // Create the CarteBeneficiaire, which fails.
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateDebutValiditeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carteBeneficiaire.setDateDebutValidite(null);

        // Create the CarteBeneficiaire, which fails.
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateFinValiditeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carteBeneficiaire.setDateFinValidite(null);

        // Create the CarteBeneficiaire, which fails.
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateEmissionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        carteBeneficiaire.setDateEmission(null);

        // Create the CarteBeneficiaire, which fails.
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCarteBeneficiaires() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carteBeneficiaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroCarte").value(hasItem(DEFAULT_NUMERO_CARTE)))
            .andExpect(jsonPath("$.[*].typeBeneficiaire").value(hasItem(DEFAULT_TYPE_BENEFICIAIRE.toString())))
            .andExpect(jsonPath("$.[*].dateDebutValidite").value(hasItem(DEFAULT_DATE_DEBUT_VALIDITE.toString())))
            .andExpect(jsonPath("$.[*].dateFinValidite").value(hasItem(DEFAULT_DATE_FIN_VALIDITE.toString())))
            .andExpect(jsonPath("$.[*].dateEmission").value(hasItem(DEFAULT_DATE_EMISSION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarteBeneficiairesWithEagerRelationshipsIsEnabled() throws Exception {
        when(carteBeneficiaireServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarteBeneficiaireMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(carteBeneficiaireServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCarteBeneficiairesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(carteBeneficiaireServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restCarteBeneficiaireMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(carteBeneficiaireRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getCarteBeneficiaire() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get the carteBeneficiaire
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL_ID, carteBeneficiaire.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carteBeneficiaire.getId().intValue()))
            .andExpect(jsonPath("$.numeroCarte").value(DEFAULT_NUMERO_CARTE))
            .andExpect(jsonPath("$.typeBeneficiaire").value(DEFAULT_TYPE_BENEFICIAIRE.toString()))
            .andExpect(jsonPath("$.dateDebutValidite").value(DEFAULT_DATE_DEBUT_VALIDITE.toString()))
            .andExpect(jsonPath("$.dateFinValidite").value(DEFAULT_DATE_FIN_VALIDITE.toString()))
            .andExpect(jsonPath("$.dateEmission").value(DEFAULT_DATE_EMISSION.toString()));
    }

    @Test
    @Transactional
    void getCarteBeneficiairesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        Long id = carteBeneficiaire.getId();

        defaultCarteBeneficiaireFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCarteBeneficiaireFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCarteBeneficiaireFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByNumeroCarteIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where numeroCarte equals to
        defaultCarteBeneficiaireFiltering("numeroCarte.equals=" + DEFAULT_NUMERO_CARTE, "numeroCarte.equals=" + UPDATED_NUMERO_CARTE);
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByNumeroCarteIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where numeroCarte in
        defaultCarteBeneficiaireFiltering(
            "numeroCarte.in=" + DEFAULT_NUMERO_CARTE + "," + UPDATED_NUMERO_CARTE,
            "numeroCarte.in=" + UPDATED_NUMERO_CARTE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByNumeroCarteIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where numeroCarte is not null
        defaultCarteBeneficiaireFiltering("numeroCarte.specified=true", "numeroCarte.specified=false");
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByNumeroCarteContainsSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where numeroCarte contains
        defaultCarteBeneficiaireFiltering("numeroCarte.contains=" + DEFAULT_NUMERO_CARTE, "numeroCarte.contains=" + UPDATED_NUMERO_CARTE);
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByNumeroCarteNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where numeroCarte does not contain
        defaultCarteBeneficiaireFiltering(
            "numeroCarte.doesNotContain=" + UPDATED_NUMERO_CARTE,
            "numeroCarte.doesNotContain=" + DEFAULT_NUMERO_CARTE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByTypeBeneficiaireIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where typeBeneficiaire equals to
        defaultCarteBeneficiaireFiltering(
            "typeBeneficiaire.equals=" + DEFAULT_TYPE_BENEFICIAIRE,
            "typeBeneficiaire.equals=" + UPDATED_TYPE_BENEFICIAIRE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByTypeBeneficiaireIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where typeBeneficiaire in
        defaultCarteBeneficiaireFiltering(
            "typeBeneficiaire.in=" + DEFAULT_TYPE_BENEFICIAIRE + "," + UPDATED_TYPE_BENEFICIAIRE,
            "typeBeneficiaire.in=" + UPDATED_TYPE_BENEFICIAIRE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByTypeBeneficiaireIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where typeBeneficiaire is not null
        defaultCarteBeneficiaireFiltering("typeBeneficiaire.specified=true", "typeBeneficiaire.specified=false");
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite equals to
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.equals=" + DEFAULT_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.equals=" + UPDATED_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite in
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.in=" + DEFAULT_DATE_DEBUT_VALIDITE + "," + UPDATED_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.in=" + UPDATED_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite is not null
        defaultCarteBeneficiaireFiltering("dateDebutValidite.specified=true", "dateDebutValidite.specified=false");
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite is greater than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.greaterThanOrEqual=" + DEFAULT_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.greaterThanOrEqual=" + UPDATED_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite is less than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.lessThanOrEqual=" + DEFAULT_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.lessThanOrEqual=" + SMALLER_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite is less than
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.lessThan=" + UPDATED_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.lessThan=" + DEFAULT_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateDebutValiditeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateDebutValidite is greater than
        defaultCarteBeneficiaireFiltering(
            "dateDebutValidite.greaterThan=" + SMALLER_DATE_DEBUT_VALIDITE,
            "dateDebutValidite.greaterThan=" + DEFAULT_DATE_DEBUT_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite equals to
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.equals=" + DEFAULT_DATE_FIN_VALIDITE,
            "dateFinValidite.equals=" + UPDATED_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite in
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.in=" + DEFAULT_DATE_FIN_VALIDITE + "," + UPDATED_DATE_FIN_VALIDITE,
            "dateFinValidite.in=" + UPDATED_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite is not null
        defaultCarteBeneficiaireFiltering("dateFinValidite.specified=true", "dateFinValidite.specified=false");
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite is greater than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.greaterThanOrEqual=" + DEFAULT_DATE_FIN_VALIDITE,
            "dateFinValidite.greaterThanOrEqual=" + UPDATED_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite is less than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.lessThanOrEqual=" + DEFAULT_DATE_FIN_VALIDITE,
            "dateFinValidite.lessThanOrEqual=" + SMALLER_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite is less than
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.lessThan=" + UPDATED_DATE_FIN_VALIDITE,
            "dateFinValidite.lessThan=" + DEFAULT_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateFinValiditeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateFinValidite is greater than
        defaultCarteBeneficiaireFiltering(
            "dateFinValidite.greaterThan=" + SMALLER_DATE_FIN_VALIDITE,
            "dateFinValidite.greaterThan=" + DEFAULT_DATE_FIN_VALIDITE
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission equals to
        defaultCarteBeneficiaireFiltering("dateEmission.equals=" + DEFAULT_DATE_EMISSION, "dateEmission.equals=" + UPDATED_DATE_EMISSION);
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission in
        defaultCarteBeneficiaireFiltering(
            "dateEmission.in=" + DEFAULT_DATE_EMISSION + "," + UPDATED_DATE_EMISSION,
            "dateEmission.in=" + UPDATED_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission is not null
        defaultCarteBeneficiaireFiltering("dateEmission.specified=true", "dateEmission.specified=false");
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission is greater than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateEmission.greaterThanOrEqual=" + DEFAULT_DATE_EMISSION,
            "dateEmission.greaterThanOrEqual=" + UPDATED_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission is less than or equal to
        defaultCarteBeneficiaireFiltering(
            "dateEmission.lessThanOrEqual=" + DEFAULT_DATE_EMISSION,
            "dateEmission.lessThanOrEqual=" + SMALLER_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission is less than
        defaultCarteBeneficiaireFiltering(
            "dateEmission.lessThan=" + UPDATED_DATE_EMISSION,
            "dateEmission.lessThan=" + DEFAULT_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByDateEmissionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        // Get all the carteBeneficiaireList where dateEmission is greater than
        defaultCarteBeneficiaireFiltering(
            "dateEmission.greaterThan=" + SMALLER_DATE_EMISSION,
            "dateEmission.greaterThan=" + DEFAULT_DATE_EMISSION
        );
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByAgentIsEqualToSomething() throws Exception {
        Agent agent;
        if (TestUtil.findAll(em, Agent.class).isEmpty()) {
            carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);
            agent = AgentResourceIT.createEntity();
        } else {
            agent = TestUtil.findAll(em, Agent.class).get(0);
        }
        em.persist(agent);
        em.flush();
        carteBeneficiaire.setAgent(agent);
        carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);
        Long agentId = agent.getId();
        // Get all the carteBeneficiaireList where agent equals to agentId
        defaultCarteBeneficiaireShouldBeFound("agentId.equals=" + agentId);

        // Get all the carteBeneficiaireList where agent equals to (agentId + 1)
        defaultCarteBeneficiaireShouldNotBeFound("agentId.equals=" + (agentId + 1));
    }

    @Test
    @Transactional
    void getAllCarteBeneficiairesByAyantDroitIsEqualToSomething() throws Exception {
        AyantDroit ayantDroit;
        if (TestUtil.findAll(em, AyantDroit.class).isEmpty()) {
            carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);
            ayantDroit = AyantDroitResourceIT.createEntity();
        } else {
            ayantDroit = TestUtil.findAll(em, AyantDroit.class).get(0);
        }
        em.persist(ayantDroit);
        em.flush();
        carteBeneficiaire.setAyantDroit(ayantDroit);
        carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);
        Long ayantDroitId = ayantDroit.getId();
        // Get all the carteBeneficiaireList where ayantDroit equals to ayantDroitId
        defaultCarteBeneficiaireShouldBeFound("ayantDroitId.equals=" + ayantDroitId);

        // Get all the carteBeneficiaireList where ayantDroit equals to (ayantDroitId + 1)
        defaultCarteBeneficiaireShouldNotBeFound("ayantDroitId.equals=" + (ayantDroitId + 1));
    }

    private void defaultCarteBeneficiaireFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCarteBeneficiaireShouldBeFound(shouldBeFound);
        defaultCarteBeneficiaireShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCarteBeneficiaireShouldBeFound(String filter) throws Exception {
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carteBeneficiaire.getId().intValue())))
            .andExpect(jsonPath("$.[*].numeroCarte").value(hasItem(DEFAULT_NUMERO_CARTE)))
            .andExpect(jsonPath("$.[*].typeBeneficiaire").value(hasItem(DEFAULT_TYPE_BENEFICIAIRE.toString())))
            .andExpect(jsonPath("$.[*].dateDebutValidite").value(hasItem(DEFAULT_DATE_DEBUT_VALIDITE.toString())))
            .andExpect(jsonPath("$.[*].dateFinValidite").value(hasItem(DEFAULT_DATE_FIN_VALIDITE.toString())))
            .andExpect(jsonPath("$.[*].dateEmission").value(hasItem(DEFAULT_DATE_EMISSION.toString())));

        // Check, that the count call also returns 1
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCarteBeneficiaireShouldNotBeFound(String filter) throws Exception {
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCarteBeneficiaireMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCarteBeneficiaire() throws Exception {
        // Get the carteBeneficiaire
        restCarteBeneficiaireMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarteBeneficiaire() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carteBeneficiaire
        CarteBeneficiaire updatedCarteBeneficiaire = carteBeneficiaireRepository.findById(carteBeneficiaire.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarteBeneficiaire are not directly saved in db
        em.detach(updatedCarteBeneficiaire);
        updatedCarteBeneficiaire
            .numeroCarte(UPDATED_NUMERO_CARTE)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .dateDebutValidite(UPDATED_DATE_DEBUT_VALIDITE)
            .dateFinValidite(UPDATED_DATE_FIN_VALIDITE)
            .dateEmission(UPDATED_DATE_EMISSION);
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(updatedCarteBeneficiaire);

        restCarteBeneficiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carteBeneficiaireDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carteBeneficiaireDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarteBeneficiaireToMatchAllProperties(updatedCarteBeneficiaire);
    }

    @Test
    @Transactional
    void putNonExistingCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carteBeneficiaireDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carteBeneficiaireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carteBeneficiaireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCarteBeneficiaireWithPatch() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carteBeneficiaire using partial update
        CarteBeneficiaire partialUpdatedCarteBeneficiaire = new CarteBeneficiaire();
        partialUpdatedCarteBeneficiaire.setId(carteBeneficiaire.getId());

        partialUpdatedCarteBeneficiaire
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .dateDebutValidite(UPDATED_DATE_DEBUT_VALIDITE)
            .dateFinValidite(UPDATED_DATE_FIN_VALIDITE)
            .dateEmission(UPDATED_DATE_EMISSION);

        restCarteBeneficiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarteBeneficiaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarteBeneficiaire))
            )
            .andExpect(status().isOk());

        // Validate the CarteBeneficiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarteBeneficiaireUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarteBeneficiaire, carteBeneficiaire),
            getPersistedCarteBeneficiaire(carteBeneficiaire)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarteBeneficiaireWithPatch() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carteBeneficiaire using partial update
        CarteBeneficiaire partialUpdatedCarteBeneficiaire = new CarteBeneficiaire();
        partialUpdatedCarteBeneficiaire.setId(carteBeneficiaire.getId());

        partialUpdatedCarteBeneficiaire
            .numeroCarte(UPDATED_NUMERO_CARTE)
            .typeBeneficiaire(UPDATED_TYPE_BENEFICIAIRE)
            .dateDebutValidite(UPDATED_DATE_DEBUT_VALIDITE)
            .dateFinValidite(UPDATED_DATE_FIN_VALIDITE)
            .dateEmission(UPDATED_DATE_EMISSION);

        restCarteBeneficiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarteBeneficiaire.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarteBeneficiaire))
            )
            .andExpect(status().isOk());

        // Validate the CarteBeneficiaire in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarteBeneficiaireUpdatableFieldsEquals(
            partialUpdatedCarteBeneficiaire,
            getPersistedCarteBeneficiaire(partialUpdatedCarteBeneficiaire)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carteBeneficiaireDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carteBeneficiaireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carteBeneficiaireDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarteBeneficiaire() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        carteBeneficiaire.setId(longCount.incrementAndGet());

        // Create the CarteBeneficiaire
        CarteBeneficiaireDTO carteBeneficiaireDTO = carteBeneficiaireMapper.toDto(carteBeneficiaire);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarteBeneficiaireMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carteBeneficiaireDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarteBeneficiaire in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCarteBeneficiaire() throws Exception {
        // Initialize the database
        insertedCarteBeneficiaire = carteBeneficiaireRepository.saveAndFlush(carteBeneficiaire);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the carteBeneficiaire
        restCarteBeneficiaireMockMvc
            .perform(delete(ENTITY_API_URL_ID, carteBeneficiaire.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return carteBeneficiaireRepository.count();
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

    protected CarteBeneficiaire getPersistedCarteBeneficiaire(CarteBeneficiaire carteBeneficiaire) {
        return carteBeneficiaireRepository.findById(carteBeneficiaire.getId()).orElseThrow();
    }

    protected void assertPersistedCarteBeneficiaireToMatchAllProperties(CarteBeneficiaire expectedCarteBeneficiaire) {
        assertCarteBeneficiaireAllPropertiesEquals(expectedCarteBeneficiaire, getPersistedCarteBeneficiaire(expectedCarteBeneficiaire));
    }

    protected void assertPersistedCarteBeneficiaireToMatchUpdatableProperties(CarteBeneficiaire expectedCarteBeneficiaire) {
        assertCarteBeneficiaireAllUpdatablePropertiesEquals(
            expectedCarteBeneficiaire,
            getPersistedCarteBeneficiaire(expectedCarteBeneficiaire)
        );
    }
}
