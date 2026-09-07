package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.TypeSoinAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.mapper.TypeSoinMapper;
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
 * Integration tests for the {@link TypeSoinResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TypeSoinResourceIT {

    private static final String DEFAULT_LIBELLE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIF = false;
    private static final Boolean UPDATED_ACTIF = true;

    private static final String ENTITY_API_URL = "/api/type-soins";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TypeSoinRepository typeSoinRepository;

    @Autowired
    private TypeSoinMapper typeSoinMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTypeSoinMockMvc;

    private TypeSoin typeSoin;

    private TypeSoin insertedTypeSoin;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeSoin createEntity() {
        return new TypeSoin().libelle(DEFAULT_LIBELLE).description(DEFAULT_DESCRIPTION).actif(DEFAULT_ACTIF);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TypeSoin createUpdatedEntity() {
        return new TypeSoin().libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
    }

    @BeforeEach
    void initTest() {
        typeSoin = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTypeSoin != null) {
            typeSoinRepository.delete(insertedTypeSoin);
            insertedTypeSoin = null;
        }
    }

    @Test
    @Transactional
    void createTypeSoin() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);
        var returnedTypeSoinDTO = om.readValue(
            restTypeSoinMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeSoinDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TypeSoinDTO.class
        );

        // Validate the TypeSoin in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTypeSoin = typeSoinMapper.toEntity(returnedTypeSoinDTO);
        assertTypeSoinUpdatableFieldsEquals(returnedTypeSoin, getPersistedTypeSoin(returnedTypeSoin));

        insertedTypeSoin = returnedTypeSoin;
    }

    @Test
    @Transactional
    void createTypeSoinWithExistingId() throws Exception {
        // Create the TypeSoin with an existing ID
        typeSoin.setId(1L);
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTypeSoinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeSoinDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLibelleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        typeSoin.setLibelle(null);

        // Create the TypeSoin, which fails.
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        restTypeSoinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeSoinDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActifIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        typeSoin.setActif(null);

        // Create the TypeSoin, which fails.
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        restTypeSoinMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeSoinDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTypeSoins() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        // Get all the typeSoinList
        restTypeSoinMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(typeSoin.getId().intValue())))
            .andExpect(jsonPath("$.[*].libelle").value(hasItem(DEFAULT_LIBELLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].actif").value(hasItem(DEFAULT_ACTIF)));
    }

    @Test
    @Transactional
    void getTypeSoin() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        // Get the typeSoin
        restTypeSoinMockMvc
            .perform(get(ENTITY_API_URL_ID, typeSoin.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(typeSoin.getId().intValue()))
            .andExpect(jsonPath("$.libelle").value(DEFAULT_LIBELLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.actif").value(DEFAULT_ACTIF));
    }

    @Test
    @Transactional
    void getNonExistingTypeSoin() throws Exception {
        // Get the typeSoin
        restTypeSoinMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTypeSoin() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeSoin
        TypeSoin updatedTypeSoin = typeSoinRepository.findById(typeSoin.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTypeSoin are not directly saved in db
        em.detach(updatedTypeSoin);
        updatedTypeSoin.libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(updatedTypeSoin);

        restTypeSoinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeSoinDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeSoinDTO))
            )
            .andExpect(status().isOk());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTypeSoinToMatchAllProperties(updatedTypeSoin);
    }

    @Test
    @Transactional
    void putNonExistingTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, typeSoinDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeSoinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(typeSoinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(typeSoinDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTypeSoinWithPatch() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeSoin using partial update
        TypeSoin partialUpdatedTypeSoin = new TypeSoin();
        partialUpdatedTypeSoin.setId(typeSoin.getId());

        partialUpdatedTypeSoin.actif(UPDATED_ACTIF);

        restTypeSoinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeSoin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTypeSoin))
            )
            .andExpect(status().isOk());

        // Validate the TypeSoin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeSoinUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTypeSoin, typeSoin), getPersistedTypeSoin(typeSoin));
    }

    @Test
    @Transactional
    void fullUpdateTypeSoinWithPatch() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the typeSoin using partial update
        TypeSoin partialUpdatedTypeSoin = new TypeSoin();
        partialUpdatedTypeSoin.setId(typeSoin.getId());

        partialUpdatedTypeSoin.libelle(UPDATED_LIBELLE).description(UPDATED_DESCRIPTION).actif(UPDATED_ACTIF);

        restTypeSoinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTypeSoin.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTypeSoin))
            )
            .andExpect(status().isOk());

        // Validate the TypeSoin in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTypeSoinUpdatableFieldsEquals(partialUpdatedTypeSoin, getPersistedTypeSoin(partialUpdatedTypeSoin));
    }

    @Test
    @Transactional
    void patchNonExistingTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, typeSoinDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(typeSoinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(typeSoinDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTypeSoin() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        typeSoin.setId(longCount.incrementAndGet());

        // Create the TypeSoin
        TypeSoinDTO typeSoinDTO = typeSoinMapper.toDto(typeSoin);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTypeSoinMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(typeSoinDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TypeSoin in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTypeSoin() throws Exception {
        // Initialize the database
        insertedTypeSoin = typeSoinRepository.saveAndFlush(typeSoin);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the typeSoin
        restTypeSoinMockMvc
            .perform(delete(ENTITY_API_URL_ID, typeSoin.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return typeSoinRepository.count();
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

    protected TypeSoin getPersistedTypeSoin(TypeSoin typeSoin) {
        return typeSoinRepository.findById(typeSoin.getId()).orElseThrow();
    }

    protected void assertPersistedTypeSoinToMatchAllProperties(TypeSoin expectedTypeSoin) {
        assertTypeSoinAllPropertiesEquals(expectedTypeSoin, getPersistedTypeSoin(expectedTypeSoin));
    }

    protected void assertPersistedTypeSoinToMatchUpdatableProperties(TypeSoin expectedTypeSoin) {
        assertTypeSoinAllUpdatablePropertiesEquals(expectedTypeSoin, getPersistedTypeSoin(expectedTypeSoin));
    }
}
