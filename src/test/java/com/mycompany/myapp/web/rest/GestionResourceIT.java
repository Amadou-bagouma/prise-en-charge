package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.GestionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.domain.enumeration.TypeGestion;
import com.mycompany.myapp.repository.GestionRepository;
import com.mycompany.myapp.service.dto.GestionDTO;
import com.mycompany.myapp.service.mapper.GestionMapper;
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
 * Integration tests for the {@link GestionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GestionResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final TypeGestion DEFAULT_TYPE = TypeGestion.AS;
    private static final TypeGestion UPDATED_TYPE = TypeGestion.OA;

    private static final String ENTITY_API_URL = "/api/gestions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private GestionRepository gestionRepository;

    @Autowired
    private GestionMapper gestionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGestionMockMvc;

    private Gestion gestion;

    private Gestion insertedGestion;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestion createEntity() {
        return new Gestion().nom(DEFAULT_NOM).type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestion createUpdatedEntity() {
        return new Gestion().nom(UPDATED_NOM).type(UPDATED_TYPE);
    }

    @BeforeEach
    void initTest() {
        gestion = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedGestion != null) {
            gestionRepository.delete(insertedGestion);
            insertedGestion = null;
        }
    }

    @Test
    @Transactional
    void createGestion() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);
        var returnedGestionDTO = om.readValue(
            restGestionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            GestionDTO.class
        );

        // Validate the Gestion in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedGestion = gestionMapper.toEntity(returnedGestionDTO);
        assertGestionUpdatableFieldsEquals(returnedGestion, getPersistedGestion(returnedGestion));

        insertedGestion = returnedGestion;
    }

    @Test
    @Transactional
    void createGestionWithExistingId() throws Exception {
        // Create the Gestion with an existing ID
        gestion.setId(1L);
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gestion.setNom(null);

        // Create the Gestion, which fails.
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        restGestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        gestion.setType(null);

        // Create the Gestion, which fails.
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        restGestionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGestions() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        // Get all the gestionList
        restGestionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(gestion.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getGestion() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        // Get the gestion
        restGestionMockMvc
            .perform(get(ENTITY_API_URL_ID, gestion.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(gestion.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGestion() throws Exception {
        // Get the gestion
        restGestionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGestion() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gestion
        Gestion updatedGestion = gestionRepository.findById(gestion.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedGestion are not directly saved in db
        em.detach(updatedGestion);
        updatedGestion.nom(UPDATED_NOM).type(UPDATED_TYPE);
        GestionDTO gestionDTO = gestionMapper.toDto(updatedGestion);

        restGestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gestionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedGestionToMatchAllProperties(updatedGestion);
    }

    @Test
    @Transactional
    void putNonExistingGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, gestionDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(gestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(gestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGestionWithPatch() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gestion using partial update
        Gestion partialUpdatedGestion = new Gestion();
        partialUpdatedGestion.setId(gestion.getId());

        partialUpdatedGestion.nom(UPDATED_NOM);

        restGestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGestion))
            )
            .andExpect(status().isOk());

        // Validate the Gestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGestionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedGestion, gestion), getPersistedGestion(gestion));
    }

    @Test
    @Transactional
    void fullUpdateGestionWithPatch() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the gestion using partial update
        Gestion partialUpdatedGestion = new Gestion();
        partialUpdatedGestion.setId(gestion.getId());

        partialUpdatedGestion.nom(UPDATED_NOM).type(UPDATED_TYPE);

        restGestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGestion.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedGestion))
            )
            .andExpect(status().isOk());

        // Validate the Gestion in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertGestionUpdatableFieldsEquals(partialUpdatedGestion, getPersistedGestion(partialUpdatedGestion));
    }

    @Test
    @Transactional
    void patchNonExistingGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, gestionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(gestionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGestion() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        gestion.setId(longCount.incrementAndGet());

        // Create the Gestion
        GestionDTO gestionDTO = gestionMapper.toDto(gestion);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGestionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(gestionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Gestion in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGestion() throws Exception {
        // Initialize the database
        insertedGestion = gestionRepository.saveAndFlush(gestion);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the gestion
        restGestionMockMvc
            .perform(delete(ENTITY_API_URL_ID, gestion.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return gestionRepository.count();
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

    protected Gestion getPersistedGestion(Gestion gestion) {
        return gestionRepository.findById(gestion.getId()).orElseThrow();
    }

    protected void assertPersistedGestionToMatchAllProperties(Gestion expectedGestion) {
        assertGestionAllPropertiesEquals(expectedGestion, getPersistedGestion(expectedGestion));
    }

    protected void assertPersistedGestionToMatchUpdatableProperties(Gestion expectedGestion) {
        assertGestionAllUpdatablePropertiesEquals(expectedGestion, getPersistedGestion(expectedGestion));
    }
}
