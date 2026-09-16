package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.AgentAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.service.AgentService;
import com.mycompany.myapp.service.dto.AgentDTO;
import com.mycompany.myapp.service.mapper.AgentMapper;
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
 * Integration tests for the {@link AgentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AgentResourceIT {

    private static final String DEFAULT_MATRICULE = "AAAAAAAAAA";
    private static final String UPDATED_MATRICULE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_NAISSANCE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_NAISSANCE = LocalDate.parse("2023-12-25");
    private static final LocalDate SMALLER_DATE_NAISSANCE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_TELEPHONE = "AAAAAAAAAA";
    private static final String UPDATED_TELEPHONE = "BBBBBBBBBB";

    private static final String DEFAULT_FONCTION = "AAAAAAAAAA";
    private static final String UPDATED_FONCTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/agents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AgentRepository agentRepository;

    @Mock
    private AgentRepository agentRepositoryMock;

    @Autowired
    private AgentMapper agentMapper;

    @Mock
    private AgentService agentServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAgentMockMvc;

    private Agent agent;

    private Agent insertedAgent;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createEntity() {
        return new Agent()
            .matricule(DEFAULT_MATRICULE)
            .nom(DEFAULT_NOM)
            .prenom(DEFAULT_PRENOM)
            .dateNaissance(DEFAULT_DATE_NAISSANCE)
            .telephone(DEFAULT_TELEPHONE)
            .fonction(DEFAULT_FONCTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Agent createUpdatedEntity() {
        return new Agent()
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);
    }

    @BeforeEach
    void initTest() {
        agent = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAgent != null) {
            agentRepository.delete(insertedAgent);
            insertedAgent = null;
        }
    }

    @Test
    @Transactional
    void createAgent() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);
        var returnedAgentDTO = om.readValue(
            restAgentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AgentDTO.class
        );

        // Validate the Agent in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAgent = agentMapper.toEntity(returnedAgentDTO);
        assertAgentUpdatableFieldsEquals(returnedAgent, getPersistedAgent(returnedAgent));

        insertedAgent = returnedAgent;
    }

    @Test
    @Transactional
    void createAgentWithExistingId() throws Exception {
        // Create the Agent with an existing ID
        agent.setId(1L);
        AgentDTO agentDTO = agentMapper.toDto(agent);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMatriculeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setMatricule(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setNom(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPrenomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        agent.setPrenom(null);

        // Create the Agent, which fails.
        AgentDTO agentDTO = agentMapper.toDto(agent);

        restAgentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAgents() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agent.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(agentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(agentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAgentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(agentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAgentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(agentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getAgent() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get the agent
        restAgentMockMvc
            .perform(get(ENTITY_API_URL_ID, agent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(agent.getId().intValue()))
            .andExpect(jsonPath("$.matricule").value(DEFAULT_MATRICULE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.prenom").value(DEFAULT_PRENOM))
            .andExpect(jsonPath("$.dateNaissance").value(DEFAULT_DATE_NAISSANCE.toString()))
            .andExpect(jsonPath("$.telephone").value(DEFAULT_TELEPHONE))
            .andExpect(jsonPath("$.fonction").value(DEFAULT_FONCTION));
    }

    @Test
    @Transactional
    void getAgentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        Long id = agent.getId();

        defaultAgentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultAgentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultAgentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAgentsByMatriculeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where matricule equals to
        defaultAgentFiltering("matricule.equals=" + DEFAULT_MATRICULE, "matricule.equals=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    void getAllAgentsByMatriculeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where matricule in
        defaultAgentFiltering("matricule.in=" + DEFAULT_MATRICULE + "," + UPDATED_MATRICULE, "matricule.in=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    void getAllAgentsByMatriculeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where matricule is not null
        defaultAgentFiltering("matricule.specified=true", "matricule.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByMatriculeContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where matricule contains
        defaultAgentFiltering("matricule.contains=" + DEFAULT_MATRICULE, "matricule.contains=" + UPDATED_MATRICULE);
    }

    @Test
    @Transactional
    void getAllAgentsByMatriculeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where matricule does not contain
        defaultAgentFiltering("matricule.doesNotContain=" + UPDATED_MATRICULE, "matricule.doesNotContain=" + DEFAULT_MATRICULE);
    }

    @Test
    @Transactional
    void getAllAgentsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where nom equals to
        defaultAgentFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAgentsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where nom in
        defaultAgentFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAgentsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where nom is not null
        defaultAgentFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where nom contains
        defaultAgentFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllAgentsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where nom does not contain
        defaultAgentFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllAgentsByPrenomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where prenom equals to
        defaultAgentFiltering("prenom.equals=" + DEFAULT_PRENOM, "prenom.equals=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAgentsByPrenomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where prenom in
        defaultAgentFiltering("prenom.in=" + DEFAULT_PRENOM + "," + UPDATED_PRENOM, "prenom.in=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAgentsByPrenomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where prenom is not null
        defaultAgentFiltering("prenom.specified=true", "prenom.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByPrenomContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where prenom contains
        defaultAgentFiltering("prenom.contains=" + DEFAULT_PRENOM, "prenom.contains=" + UPDATED_PRENOM);
    }

    @Test
    @Transactional
    void getAllAgentsByPrenomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where prenom does not contain
        defaultAgentFiltering("prenom.doesNotContain=" + UPDATED_PRENOM, "prenom.doesNotContain=" + DEFAULT_PRENOM);
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance equals to
        defaultAgentFiltering("dateNaissance.equals=" + DEFAULT_DATE_NAISSANCE, "dateNaissance.equals=" + UPDATED_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance in
        defaultAgentFiltering(
            "dateNaissance.in=" + DEFAULT_DATE_NAISSANCE + "," + UPDATED_DATE_NAISSANCE,
            "dateNaissance.in=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance is not null
        defaultAgentFiltering("dateNaissance.specified=true", "dateNaissance.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance is greater than or equal to
        defaultAgentFiltering(
            "dateNaissance.greaterThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.greaterThanOrEqual=" + UPDATED_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance is less than or equal to
        defaultAgentFiltering(
            "dateNaissance.lessThanOrEqual=" + DEFAULT_DATE_NAISSANCE,
            "dateNaissance.lessThanOrEqual=" + SMALLER_DATE_NAISSANCE
        );
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance is less than
        defaultAgentFiltering("dateNaissance.lessThan=" + UPDATED_DATE_NAISSANCE, "dateNaissance.lessThan=" + DEFAULT_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAgentsByDateNaissanceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where dateNaissance is greater than
        defaultAgentFiltering("dateNaissance.greaterThan=" + SMALLER_DATE_NAISSANCE, "dateNaissance.greaterThan=" + DEFAULT_DATE_NAISSANCE);
    }

    @Test
    @Transactional
    void getAllAgentsByTelephoneIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where telephone equals to
        defaultAgentFiltering("telephone.equals=" + DEFAULT_TELEPHONE, "telephone.equals=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllAgentsByTelephoneIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where telephone in
        defaultAgentFiltering("telephone.in=" + DEFAULT_TELEPHONE + "," + UPDATED_TELEPHONE, "telephone.in=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllAgentsByTelephoneIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where telephone is not null
        defaultAgentFiltering("telephone.specified=true", "telephone.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByTelephoneContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where telephone contains
        defaultAgentFiltering("telephone.contains=" + DEFAULT_TELEPHONE, "telephone.contains=" + UPDATED_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllAgentsByTelephoneNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where telephone does not contain
        defaultAgentFiltering("telephone.doesNotContain=" + UPDATED_TELEPHONE, "telephone.doesNotContain=" + DEFAULT_TELEPHONE);
    }

    @Test
    @Transactional
    void getAllAgentsByFonctionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where fonction equals to
        defaultAgentFiltering("fonction.equals=" + DEFAULT_FONCTION, "fonction.equals=" + UPDATED_FONCTION);
    }

    @Test
    @Transactional
    void getAllAgentsByFonctionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where fonction in
        defaultAgentFiltering("fonction.in=" + DEFAULT_FONCTION + "," + UPDATED_FONCTION, "fonction.in=" + UPDATED_FONCTION);
    }

    @Test
    @Transactional
    void getAllAgentsByFonctionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where fonction is not null
        defaultAgentFiltering("fonction.specified=true", "fonction.specified=false");
    }

    @Test
    @Transactional
    void getAllAgentsByFonctionContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where fonction contains
        defaultAgentFiltering("fonction.contains=" + DEFAULT_FONCTION, "fonction.contains=" + UPDATED_FONCTION);
    }

    @Test
    @Transactional
    void getAllAgentsByFonctionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        // Get all the agentList where fonction does not contain
        defaultAgentFiltering("fonction.doesNotContain=" + UPDATED_FONCTION, "fonction.doesNotContain=" + DEFAULT_FONCTION);
    }

    @Test
    @Transactional
    void getAllAgentsByDirectionIsEqualToSomething() throws Exception {
        Direction direction;
        if (TestUtil.findAll(em, Direction.class).isEmpty()) {
            agentRepository.saveAndFlush(agent);
            direction = DirectionResourceIT.createEntity();
        } else {
            direction = TestUtil.findAll(em, Direction.class).get(0);
        }
        em.persist(direction);
        em.flush();
        agent.setDirection(direction);
        agentRepository.saveAndFlush(agent);
        Long directionId = direction.getId();
        // Get all the agentList where direction equals to directionId
        defaultAgentShouldBeFound("directionId.equals=" + directionId);

        // Get all the agentList where direction equals to (directionId + 1)
        defaultAgentShouldNotBeFound("directionId.equals=" + (directionId + 1));
    }

    @Test
    @Transactional
    void getAllAgentsByGestionIsEqualToSomething() throws Exception {
        Gestion gestion;
        if (TestUtil.findAll(em, Gestion.class).isEmpty()) {
            agentRepository.saveAndFlush(agent);
            gestion = GestionResourceIT.createEntity();
        } else {
            gestion = TestUtil.findAll(em, Gestion.class).get(0);
        }
        em.persist(gestion);
        em.flush();
        agent.setGestion(gestion);
        agentRepository.saveAndFlush(agent);
        Long gestionId = gestion.getId();
        // Get all the agentList where gestion equals to gestionId
        defaultAgentShouldBeFound("gestionId.equals=" + gestionId);

        // Get all the agentList where gestion equals to (gestionId + 1)
        defaultAgentShouldNotBeFound("gestionId.equals=" + (gestionId + 1));
    }

    private void defaultAgentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultAgentShouldBeFound(shouldBeFound);
        defaultAgentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAgentShouldBeFound(String filter) throws Exception {
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(agent.getId().intValue())))
            .andExpect(jsonPath("$.[*].matricule").value(hasItem(DEFAULT_MATRICULE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].prenom").value(hasItem(DEFAULT_PRENOM)))
            .andExpect(jsonPath("$.[*].dateNaissance").value(hasItem(DEFAULT_DATE_NAISSANCE.toString())))
            .andExpect(jsonPath("$.[*].telephone").value(hasItem(DEFAULT_TELEPHONE)))
            .andExpect(jsonPath("$.[*].fonction").value(hasItem(DEFAULT_FONCTION)));

        // Check, that the count call also returns 1
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAgentShouldNotBeFound(String filter) throws Exception {
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAgentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAgent() throws Exception {
        // Get the agent
        restAgentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAgent() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent
        Agent updatedAgent = agentRepository.findById(agent.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAgent are not directly saved in db
        em.detach(updatedAgent);
        updatedAgent
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);
        AgentDTO agentDTO = agentMapper.toDto(updatedAgent);

        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAgentToMatchAllProperties(updatedAgent);
    }

    @Test
    @Transactional
    void putNonExistingAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, agentDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent.matricule(UPDATED_MATRICULE).prenom(UPDATED_PRENOM).telephone(UPDATED_TELEPHONE);

        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgent))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAgent, agent), getPersistedAgent(agent));
    }

    @Test
    @Transactional
    void fullUpdateAgentWithPatch() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the agent using partial update
        Agent partialUpdatedAgent = new Agent();
        partialUpdatedAgent.setId(agent.getId());

        partialUpdatedAgent
            .matricule(UPDATED_MATRICULE)
            .nom(UPDATED_NOM)
            .prenom(UPDATED_PRENOM)
            .dateNaissance(UPDATED_DATE_NAISSANCE)
            .telephone(UPDATED_TELEPHONE)
            .fonction(UPDATED_FONCTION);

        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAgent.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAgent))
            )
            .andExpect(status().isOk());

        // Validate the Agent in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAgentUpdatableFieldsEquals(partialUpdatedAgent, getPersistedAgent(partialUpdatedAgent));
    }

    @Test
    @Transactional
    void patchNonExistingAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, agentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(agentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAgent() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        agent.setId(longCount.incrementAndGet());

        // Create the Agent
        AgentDTO agentDTO = agentMapper.toDto(agent);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAgentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(agentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Agent in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAgent() throws Exception {
        // Initialize the database
        insertedAgent = agentRepository.saveAndFlush(agent);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the agent
        restAgentMockMvc
            .perform(delete(ENTITY_API_URL_ID, agent.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return agentRepository.count();
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

    protected Agent getPersistedAgent(Agent agent) {
        return agentRepository.findById(agent.getId()).orElseThrow();
    }

    protected void assertPersistedAgentToMatchAllProperties(Agent expectedAgent) {
        assertAgentAllPropertiesEquals(expectedAgent, getPersistedAgent(expectedAgent));
    }

    protected void assertPersistedAgentToMatchUpdatableProperties(Agent expectedAgent) {
        assertAgentAllUpdatablePropertiesEquals(expectedAgent, getPersistedAgent(expectedAgent));
    }
}
