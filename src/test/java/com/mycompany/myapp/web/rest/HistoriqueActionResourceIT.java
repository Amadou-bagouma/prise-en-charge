package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.HistoriqueActionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.HistoriqueActionService;
import com.mycompany.myapp.service.dto.HistoriqueActionDTO;
import com.mycompany.myapp.service.mapper.HistoriqueActionMapper;
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
 * Integration tests for the {@link HistoriqueActionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class HistoriqueActionResourceIT {

    private static final String DEFAULT_ACTION = "AAAAAAAAAA";
    private static final String UPDATED_ACTION = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_ACTION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_ACTION = Instant.ofEpochMilli(1703483747250L);

    private static final String ENTITY_API_URL = "/api/historique-actions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private HistoriqueActionRepository historiqueActionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private HistoriqueActionRepository historiqueActionRepositoryMock;

    @Autowired
    private HistoriqueActionMapper historiqueActionMapper;

    @Mock
    private HistoriqueActionService historiqueActionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restHistoriqueActionMockMvc;

    private HistoriqueAction historiqueAction;

    private HistoriqueAction insertedHistoriqueAction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoriqueAction createEntity(EntityManager em) {
        HistoriqueAction historiqueAction = new HistoriqueAction()
            .action(DEFAULT_ACTION)
            .description(DEFAULT_DESCRIPTION)
            .dateAction(DEFAULT_DATE_ACTION);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        historiqueAction.setDemande(demandePriseEnCharge);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        historiqueAction.setUtilisateur(user);
        return historiqueAction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static HistoriqueAction createUpdatedEntity(EntityManager em) {
        HistoriqueAction updatedHistoriqueAction = new HistoriqueAction()
            .action(UPDATED_ACTION)
            .description(UPDATED_DESCRIPTION)
            .dateAction(UPDATED_DATE_ACTION);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createUpdatedEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        updatedHistoriqueAction.setDemande(demandePriseEnCharge);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedHistoriqueAction.setUtilisateur(user);
        return updatedHistoriqueAction;
    }

    @BeforeEach
    void initTest() {
        historiqueAction = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedHistoriqueAction != null) {
            historiqueActionRepository.delete(insertedHistoriqueAction);
            insertedHistoriqueAction = null;
        }
    }

    @Test
    @Transactional
    void createHistoriqueAction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);
        var returnedHistoriqueActionDTO = om.readValue(
            restHistoriqueActionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueActionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            HistoriqueActionDTO.class
        );

        // Validate the HistoriqueAction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedHistoriqueAction = historiqueActionMapper.toEntity(returnedHistoriqueActionDTO);
        assertHistoriqueActionUpdatableFieldsEquals(returnedHistoriqueAction, getPersistedHistoriqueAction(returnedHistoriqueAction));

        insertedHistoriqueAction = returnedHistoriqueAction;
    }

    @Test
    @Transactional
    void createHistoriqueActionWithExistingId() throws Exception {
        // Create the HistoriqueAction with an existing ID
        historiqueAction.setId(1L);
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restHistoriqueActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueActionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        historiqueAction.setAction(null);

        // Create the HistoriqueAction, which fails.
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        restHistoriqueActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateActionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        historiqueAction.setDateAction(null);

        // Create the HistoriqueAction, which fails.
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        restHistoriqueActionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueActionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllHistoriqueActions() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historiqueAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateAction").value(hasItem(DEFAULT_DATE_ACTION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHistoriqueActionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(historiqueActionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHistoriqueActionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(historiqueActionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllHistoriqueActionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(historiqueActionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restHistoriqueActionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(historiqueActionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getHistoriqueAction() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get the historiqueAction
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL_ID, historiqueAction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(historiqueAction.getId().intValue()))
            .andExpect(jsonPath("$.action").value(DEFAULT_ACTION))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.dateAction").value(DEFAULT_DATE_ACTION.toString()));
    }

    @Test
    @Transactional
    void getHistoriqueActionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        Long id = historiqueAction.getId();

        defaultHistoriqueActionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultHistoriqueActionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultHistoriqueActionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where action equals to
        defaultHistoriqueActionFiltering("action.equals=" + DEFAULT_ACTION, "action.equals=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where action in
        defaultHistoriqueActionFiltering("action.in=" + DEFAULT_ACTION + "," + UPDATED_ACTION, "action.in=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where action is not null
        defaultHistoriqueActionFiltering("action.specified=true", "action.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByActionContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where action contains
        defaultHistoriqueActionFiltering("action.contains=" + DEFAULT_ACTION, "action.contains=" + UPDATED_ACTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByActionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where action does not contain
        defaultHistoriqueActionFiltering("action.doesNotContain=" + UPDATED_ACTION, "action.doesNotContain=" + DEFAULT_ACTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where description equals to
        defaultHistoriqueActionFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where description in
        defaultHistoriqueActionFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where description is not null
        defaultHistoriqueActionFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where description contains
        defaultHistoriqueActionFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where description does not contain
        defaultHistoriqueActionFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDateActionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where dateAction equals to
        defaultHistoriqueActionFiltering("dateAction.equals=" + DEFAULT_DATE_ACTION, "dateAction.equals=" + UPDATED_DATE_ACTION);
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDateActionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where dateAction in
        defaultHistoriqueActionFiltering(
            "dateAction.in=" + DEFAULT_DATE_ACTION + "," + UPDATED_DATE_ACTION,
            "dateAction.in=" + UPDATED_DATE_ACTION
        );
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDateActionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        // Get all the historiqueActionList where dateAction is not null
        defaultHistoriqueActionFiltering("dateAction.specified=true", "dateAction.specified=false");
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByDemandeIsEqualToSomething() throws Exception {
        DemandePriseEnCharge demande;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            historiqueActionRepository.saveAndFlush(historiqueAction);
            demande = DemandePriseEnChargeResourceIT.createEntity(em);
        } else {
            demande = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        em.persist(demande);
        em.flush();
        historiqueAction.setDemande(demande);
        historiqueActionRepository.saveAndFlush(historiqueAction);
        Long demandeId = demande.getId();
        // Get all the historiqueActionList where demande equals to demandeId
        defaultHistoriqueActionShouldBeFound("demandeId.equals=" + demandeId);

        // Get all the historiqueActionList where demande equals to (demandeId + 1)
        defaultHistoriqueActionShouldNotBeFound("demandeId.equals=" + (demandeId + 1));
    }

    @Test
    @Transactional
    void getAllHistoriqueActionsByUtilisateurIsEqualToSomething() throws Exception {
        User utilisateur;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            historiqueActionRepository.saveAndFlush(historiqueAction);
            utilisateur = UserResourceIT.createEntity();
        } else {
            utilisateur = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(utilisateur);
        em.flush();
        historiqueAction.setUtilisateur(utilisateur);
        historiqueActionRepository.saveAndFlush(historiqueAction);
        Long utilisateurId = utilisateur.getId();
        // Get all the historiqueActionList where utilisateur equals to utilisateurId
        defaultHistoriqueActionShouldBeFound("utilisateurId.equals=" + utilisateurId);

        // Get all the historiqueActionList where utilisateur equals to (utilisateurId + 1)
        defaultHistoriqueActionShouldNotBeFound("utilisateurId.equals=" + (utilisateurId + 1));
    }

    private void defaultHistoriqueActionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultHistoriqueActionShouldBeFound(shouldBeFound);
        defaultHistoriqueActionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultHistoriqueActionShouldBeFound(String filter) throws Exception {
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(historiqueAction.getId().intValue())))
            .andExpect(jsonPath("$.[*].action").value(hasItem(DEFAULT_ACTION)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].dateAction").value(hasItem(DEFAULT_DATE_ACTION.toString())));

        // Check, that the count call also returns 1
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultHistoriqueActionShouldNotBeFound(String filter) throws Exception {
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restHistoriqueActionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingHistoriqueAction() throws Exception {
        // Get the historiqueAction
        restHistoriqueActionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingHistoriqueAction() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueAction
        HistoriqueAction updatedHistoriqueAction = historiqueActionRepository.findById(historiqueAction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedHistoriqueAction are not directly saved in db
        em.detach(updatedHistoriqueAction);
        updatedHistoriqueAction.action(UPDATED_ACTION).description(UPDATED_DESCRIPTION).dateAction(UPDATED_DATE_ACTION);
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(updatedHistoriqueAction);

        restHistoriqueActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historiqueActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueActionDTO))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedHistoriqueActionToMatchAllProperties(updatedHistoriqueAction);
    }

    @Test
    @Transactional
    void putNonExistingHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, historiqueActionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(historiqueActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(historiqueActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateHistoriqueActionWithPatch() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueAction using partial update
        HistoriqueAction partialUpdatedHistoriqueAction = new HistoriqueAction();
        partialUpdatedHistoriqueAction.setId(historiqueAction.getId());

        partialUpdatedHistoriqueAction.action(UPDATED_ACTION).dateAction(UPDATED_DATE_ACTION);

        restHistoriqueActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoriqueAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHistoriqueAction))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHistoriqueActionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedHistoriqueAction, historiqueAction),
            getPersistedHistoriqueAction(historiqueAction)
        );
    }

    @Test
    @Transactional
    void fullUpdateHistoriqueActionWithPatch() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the historiqueAction using partial update
        HistoriqueAction partialUpdatedHistoriqueAction = new HistoriqueAction();
        partialUpdatedHistoriqueAction.setId(historiqueAction.getId());

        partialUpdatedHistoriqueAction.action(UPDATED_ACTION).description(UPDATED_DESCRIPTION).dateAction(UPDATED_DATE_ACTION);

        restHistoriqueActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedHistoriqueAction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedHistoriqueAction))
            )
            .andExpect(status().isOk());

        // Validate the HistoriqueAction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertHistoriqueActionUpdatableFieldsEquals(
            partialUpdatedHistoriqueAction,
            getPersistedHistoriqueAction(partialUpdatedHistoriqueAction)
        );
    }

    @Test
    @Transactional
    void patchNonExistingHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, historiqueActionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(historiqueActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(historiqueActionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamHistoriqueAction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        historiqueAction.setId(longCount.incrementAndGet());

        // Create the HistoriqueAction
        HistoriqueActionDTO historiqueActionDTO = historiqueActionMapper.toDto(historiqueAction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restHistoriqueActionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(historiqueActionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the HistoriqueAction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteHistoriqueAction() throws Exception {
        // Initialize the database
        insertedHistoriqueAction = historiqueActionRepository.saveAndFlush(historiqueAction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the historiqueAction
        restHistoriqueActionMockMvc
            .perform(delete(ENTITY_API_URL_ID, historiqueAction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return historiqueActionRepository.count();
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

    protected HistoriqueAction getPersistedHistoriqueAction(HistoriqueAction historiqueAction) {
        return historiqueActionRepository.findById(historiqueAction.getId()).orElseThrow();
    }

    protected void assertPersistedHistoriqueActionToMatchAllProperties(HistoriqueAction expectedHistoriqueAction) {
        assertHistoriqueActionAllPropertiesEquals(expectedHistoriqueAction, getPersistedHistoriqueAction(expectedHistoriqueAction));
    }

    protected void assertPersistedHistoriqueActionToMatchUpdatableProperties(HistoriqueAction expectedHistoriqueAction) {
        assertHistoriqueActionAllUpdatablePropertiesEquals(
            expectedHistoriqueAction,
            getPersistedHistoriqueAction(expectedHistoriqueAction)
        );
    }
}
