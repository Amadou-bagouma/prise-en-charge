package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.BoiteReceptionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.BoiteReceptionService;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.mapper.BoiteReceptionMapper;
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
 * Integration tests for the {@link BoiteReceptionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BoiteReceptionResourceIT {

    private static final Instant DEFAULT_DATE_CREATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATION = Instant.ofEpochMilli(1703483747250L);

    private static final Instant DEFAULT_DATE_DERNIERE_LECTURE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_DERNIERE_LECTURE = Instant.ofEpochMilli(1703483747250L);

    private static final Integer DEFAULT_NOMBRE_NON_LUS = 1;
    private static final Integer UPDATED_NOMBRE_NON_LUS = 2;

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/boite-receptions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BoiteReceptionRepository boiteReceptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BoiteReceptionRepository boiteReceptionRepositoryMock;

    @Autowired
    private BoiteReceptionMapper boiteReceptionMapper;

    @Mock
    private BoiteReceptionService boiteReceptionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBoiteReceptionMockMvc;

    private BoiteReception boiteReception;

    private BoiteReception insertedBoiteReception;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BoiteReception createEntity(EntityManager em) {
        BoiteReception boiteReception = new BoiteReception()
            .dateCreation(DEFAULT_DATE_CREATION)
            .dateDerniereLecture(DEFAULT_DATE_DERNIERE_LECTURE)
            .nombreNonLus(DEFAULT_NOMBRE_NON_LUS)
            .actif(DEFAULT_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        boiteReception.setUtilisateur(user);
        return boiteReception;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BoiteReception createUpdatedEntity(EntityManager em) {
        BoiteReception updatedBoiteReception = new BoiteReception()
            .dateCreation(UPDATED_DATE_CREATION)
            .dateDerniereLecture(UPDATED_DATE_DERNIERE_LECTURE)
            .nombreNonLus(UPDATED_NOMBRE_NON_LUS)
            .actif(UPDATED_ACTIF);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedBoiteReception.setUtilisateur(user);
        return updatedBoiteReception;
    }

    @BeforeEach
    void initTest() {
        boiteReception = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedBoiteReception != null) {
            boiteReceptionRepository.delete(insertedBoiteReception);
            insertedBoiteReception = null;
        }
    }

    @Test
    @Transactional
    void createBoiteReception() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);
        var returnedBoiteReceptionDTO = om.readValue(
            restBoiteReceptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BoiteReceptionDTO.class
        );

        // Validate the BoiteReception in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBoiteReception = boiteReceptionMapper.toEntity(returnedBoiteReceptionDTO);
        assertBoiteReceptionUpdatableFieldsEquals(returnedBoiteReception, getPersistedBoiteReception(returnedBoiteReception));

        insertedBoiteReception = returnedBoiteReception;
    }

    @Test
    @Transactional
    void createBoiteReceptionWithExistingId() throws Exception {
        // Create the BoiteReception with an existing ID
        boiteReception.setId(1L);
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateCreationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boiteReception.setDateCreation(null);

        // Create the BoiteReception, which fails.
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNombreNonLusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boiteReception.setNombreNonLus(null);

        // Create the BoiteReception, which fails.
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        boiteReception.setActif(null);

        // Create the BoiteReception, which fails.
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        restBoiteReceptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBoiteReceptions() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        // Get all the boiteReceptionList
        restBoiteReceptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(boiteReception.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateCreation").value(hasItem(DEFAULT_DATE_CREATION.toString())))
            .andExpect(jsonPath("$.[*].dateDerniereLecture").value(hasItem(DEFAULT_DATE_DERNIERE_LECTURE.toString())))
            .andExpect(jsonPath("$.[*].nombreNonLus").value(hasItem(DEFAULT_NOMBRE_NON_LUS)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBoiteReceptionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(boiteReceptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBoiteReceptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(boiteReceptionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBoiteReceptionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(boiteReceptionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBoiteReceptionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(boiteReceptionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBoiteReception() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        // Get the boiteReception
        restBoiteReceptionMockMvc
            .perform(get(ENTITY_API_URL_ID, boiteReception.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(boiteReception.getId().intValue()))
            .andExpect(jsonPath("$.dateCreation").value(DEFAULT_DATE_CREATION.toString()))
            .andExpect(jsonPath("$.dateDerniereLecture").value(DEFAULT_DATE_DERNIERE_LECTURE.toString()))
            .andExpect(jsonPath("$.nombreNonLus").value(DEFAULT_NOMBRE_NON_LUS))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getNonExistingBoiteReception() throws Exception {
        // Get the boiteReception
        restBoiteReceptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBoiteReception() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boiteReception
        BoiteReception updatedBoiteReception = boiteReceptionRepository.findById(boiteReception.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBoiteReception are not directly saved in db
        em.detach(updatedBoiteReception);
        updatedBoiteReception
            .dateCreation(UPDATED_DATE_CREATION)
            .dateDerniereLecture(UPDATED_DATE_DERNIERE_LECTURE)
            .nombreNonLus(UPDATED_NOMBRE_NON_LUS)
            .actif(UPDATED_ACTIF);
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(updatedBoiteReception);

        restBoiteReceptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boiteReceptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boiteReceptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBoiteReceptionToMatchAllProperties(updatedBoiteReception);
    }

    @Test
    @Transactional
    void putNonExistingBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, boiteReceptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boiteReceptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(boiteReceptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBoiteReceptionWithPatch() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boiteReception using partial update
        BoiteReception partialUpdatedBoiteReception = new BoiteReception();
        partialUpdatedBoiteReception.setId(boiteReception.getId());

        partialUpdatedBoiteReception
            .dateDerniereLecture(UPDATED_DATE_DERNIERE_LECTURE)
            .nombreNonLus(UPDATED_NOMBRE_NON_LUS)
            .actif(UPDATED_ACTIF);

        restBoiteReceptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoiteReception.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoiteReception))
            )
            .andExpect(status().isOk());

        // Validate the BoiteReception in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoiteReceptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBoiteReception, boiteReception),
            getPersistedBoiteReception(boiteReception)
        );
    }

    @Test
    @Transactional
    void fullUpdateBoiteReceptionWithPatch() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the boiteReception using partial update
        BoiteReception partialUpdatedBoiteReception = new BoiteReception();
        partialUpdatedBoiteReception.setId(boiteReception.getId());

        partialUpdatedBoiteReception
            .dateCreation(UPDATED_DATE_CREATION)
            .dateDerniereLecture(UPDATED_DATE_DERNIERE_LECTURE)
            .nombreNonLus(UPDATED_NOMBRE_NON_LUS)
            .actif(UPDATED_ACTIF);

        restBoiteReceptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBoiteReception.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBoiteReception))
            )
            .andExpect(status().isOk());

        // Validate the BoiteReception in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBoiteReceptionUpdatableFieldsEquals(partialUpdatedBoiteReception, getPersistedBoiteReception(partialUpdatedBoiteReception));
    }

    @Test
    @Transactional
    void patchNonExistingBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, boiteReceptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boiteReceptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(boiteReceptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBoiteReception() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        boiteReception.setId(longCount.incrementAndGet());

        // Create the BoiteReception
        BoiteReceptionDTO boiteReceptionDTO = boiteReceptionMapper.toDto(boiteReception);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBoiteReceptionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(boiteReceptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BoiteReception in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBoiteReception() throws Exception {
        // Initialize the database
        insertedBoiteReception = boiteReceptionRepository.saveAndFlush(boiteReception);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the boiteReception
        restBoiteReceptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, boiteReception.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return boiteReceptionRepository.count();
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

    protected BoiteReception getPersistedBoiteReception(BoiteReception boiteReception) {
        return boiteReceptionRepository.findById(boiteReception.getId()).orElseThrow();
    }

    protected void assertPersistedBoiteReceptionToMatchAllProperties(BoiteReception expectedBoiteReception) {
        assertBoiteReceptionAllPropertiesEquals(expectedBoiteReception, getPersistedBoiteReception(expectedBoiteReception));
    }

    protected void assertPersistedBoiteReceptionToMatchUpdatableProperties(BoiteReception expectedBoiteReception) {
        assertBoiteReceptionAllUpdatablePropertiesEquals(expectedBoiteReception, getPersistedBoiteReception(expectedBoiteReception));
    }
}
