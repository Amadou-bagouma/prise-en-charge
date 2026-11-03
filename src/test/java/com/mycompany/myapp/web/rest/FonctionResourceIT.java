package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.FonctionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.Fonction;
import com.mycompany.myapp.repository.FonctionRepository;
import com.mycompany.myapp.service.dto.FonctionDTO;
import com.mycompany.myapp.service.mapper.FonctionMapper;
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
 * Integration tests for the {@link FonctionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FonctionResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/fonctions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FonctionRepository fonctionRepository;

    @Autowired
    private FonctionMapper fonctionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFonctionMockMvc;

    private Fonction fonction;

    private Fonction insertedFonction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fonction createEntity() {
        return new Fonction().libelle(DEFAULT_LIBELLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Fonction createUpdatedEntity() {
        return new Fonction().libelle(UPDATED_LIBELLE);
    }

    @BeforeEach
    void initTest() {
        fonction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFonction != null) {
            fonctionRepository.delete(insertedFonction);
            insertedFonction = null;
        }
    }

    @Test
    @Transactional
    void createFonction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);
        var returnedFonctionDTO = om.readValue(
            restFonctionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fonctionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FonctionDTO.class
        );

        // Validate the Fonction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFonction = fonctionMapper.toEntity(returnedFonctionDTO);
        assertFonctionUpdatableFieldsEquals(returnedFonction, getPersistedFonction(returnedFonction));

        insertedFonction = returnedFonction;
    }

    @Test
    @Transactional
    void createFonctionWithExistingId() throws Exception {
        // Create the Fonction with an existing ID
        fonction.setId(1L);
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFonctionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fonctionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        fonction.setLibelle(null);

        // Create the Fonction, which fails.
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        restFonctionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fonctionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFonctions() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        // Get all the fonctionList
        restFonctionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(fonction.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)));
    }

    @Test
    @Transactional
    void getFonction() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        // Get the fonction
        restFonctionMockMvc
            .perform(get(ENTITY_API_URL_ID, fonction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(fonction.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE));
    }

    @Test
    @Transactional
    void getNonExistingFonction() throws Exception {
        // Get the fonction
        restFonctionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFonction() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fonction
        Fonction updatedFonction = fonctionRepository.findById(fonction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFonction are not directly saved in db
        em.detach(updatedFonction);
        updatedFonction.libelle(UPDATED_LIBELLE);
        FonctionDTO fonctionDTO = fonctionMapper.toDto(updatedFonction);

        restFonctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fonctionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fonctionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFonctionToMatchAllProperties(updatedFonction);
    }

    @Test
    @Transactional
    void putNonExistingFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, fonctionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fonctionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(fonctionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(fonctionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFonctionWithPatch() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fonction using partial update
        Fonction partialUpdatedFonction = new Fonction();
        partialUpdatedFonction.setId(fonction.getId());

        partialUpdatedFonction.libelle(UPDATED_LIBELLE);

        restFonctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFonction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFonction))
            )
            .andExpect(status().isOk());

        // Validate the Fonction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFonctionUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFonction, fonction), getPersistedFonction(fonction));
    }

    @Test
    @Transactional
    void fullUpdateFonctionWithPatch() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the fonction using partial update
        Fonction partialUpdatedFonction = new Fonction();
        partialUpdatedFonction.setId(fonction.getId());

        partialUpdatedFonction.libelle(UPDATED_LIBELLE);

        restFonctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFonction.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFonction))
            )
            .andExpect(status().isOk());

        // Validate the Fonction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFonctionUpdatableFieldsEquals(partialUpdatedFonction, getPersistedFonction(partialUpdatedFonction));
    }

    @Test
    @Transactional
    void patchNonExistingFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, fonctionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fonctionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(fonctionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFonction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        fonction.setId(longCount.incrementAndGet());

        // Create the Fonction
        FonctionDTO fonctionDTO = fonctionMapper.toDto(fonction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFonctionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(fonctionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Fonction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFonction() throws Exception {
        // Initialize the database
        insertedFonction = fonctionRepository.saveAndFlush(fonction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the fonction
        restFonctionMockMvc
            .perform(delete(ENTITY_API_URL_ID, fonction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void createFonctionWithExistingLibelleIgnoringCaseIsRefused() throws Exception {
        insertedFonction = fonctionRepository.saveAndFlush(fonction);
        long databaseSizeBeforeCreate = getRepositoryCount();

        // Meme fonction, autre casse et espaces de bord : c'est un doublon
        FonctionDTO doublon = new FonctionDTO();
        doublon.setLibelle("  " + DEFAULT_LIBELLE.toLowerCase() + " ");

        restFonctionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(doublon)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void deleteFonctionOccupiedByAnAgentIsRefused() throws Exception {
        insertedFonction = fonctionRepository.saveAndFlush(fonction);
        Agent agent = AgentResourceIT.createEntity().fonction(fonction);
        em.persist(agent);
        em.flush();

        long databaseSizeBeforeDelete = getRepositoryCount();

        restFonctionMockMvc
            .perform(delete(ENTITY_API_URL_ID, fonction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeDelete);
        em.remove(agent);
    }

    protected long getRepositoryCount() {
        return fonctionRepository.count();
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

    protected Fonction getPersistedFonction(Fonction fonction) {
        return fonctionRepository.findById(fonction.getId()).orElseThrow();
    }

    protected void assertPersistedFonctionToMatchAllProperties(Fonction expectedFonction) {
        assertFonctionAllPropertiesEquals(expectedFonction, getPersistedFonction(expectedFonction));
    }

    protected void assertPersistedFonctionToMatchUpdatableProperties(Fonction expectedFonction) {
        assertFonctionAllUpdatablePropertiesEquals(expectedFonction, getPersistedFonction(expectedFonction));
    }
}
