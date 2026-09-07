package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.PieceJustificativeAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.PieceJustificative;
import com.mycompany.myapp.repository.PieceJustificativeRepository;
import com.mycompany.myapp.service.PieceJustificativeService;
import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
import com.mycompany.myapp.service.mapper.PieceJustificativeMapper;
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
 * Integration tests for the {@link PieceJustificativeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PieceJustificativeResourceIT {

    private static final String DEFAULT_NOM_FICHIER = "AAAAAAAAAA";
    private static final String UPDATED_NOM_FICHIER = "BBBBBBBBBB";

    private static final String DEFAULT_CHEMIN_FICHIER = "AAAAAAAAAA";
    private static final String UPDATED_CHEMIN_FICHIER = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_AJOUT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_AJOUT = Instant.ofEpochMilli(1703483747250L);

    private static final String ENTITY_API_URL = "/api/piece-justificatives";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PieceJustificativeRepository pieceJustificativeRepository;

    @Mock
    private PieceJustificativeRepository pieceJustificativeRepositoryMock;

    @Autowired
    private PieceJustificativeMapper pieceJustificativeMapper;

    @Mock
    private PieceJustificativeService pieceJustificativeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPieceJustificativeMockMvc;

    private PieceJustificative pieceJustificative;

    private PieceJustificative insertedPieceJustificative;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PieceJustificative createEntity(EntityManager em) {
        PieceJustificative pieceJustificative = new PieceJustificative()
            .nomFichier(DEFAULT_NOM_FICHIER)
            .cheminFichier(DEFAULT_CHEMIN_FICHIER)
            .dateAjout(DEFAULT_DATE_AJOUT);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        pieceJustificative.setDemande(demandePriseEnCharge);
        return pieceJustificative;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PieceJustificative createUpdatedEntity(EntityManager em) {
        PieceJustificative updatedPieceJustificative = new PieceJustificative()
            .nomFichier(UPDATED_NOM_FICHIER)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateAjout(UPDATED_DATE_AJOUT);
        // Add required entity
        DemandePriseEnCharge demandePriseEnCharge;
        if (TestUtil.findAll(em, DemandePriseEnCharge.class).isEmpty()) {
            demandePriseEnCharge = DemandePriseEnChargeResourceIT.createUpdatedEntity(em);
            em.persist(demandePriseEnCharge);
            em.flush();
        } else {
            demandePriseEnCharge = TestUtil.findAll(em, DemandePriseEnCharge.class).get(0);
        }
        updatedPieceJustificative.setDemande(demandePriseEnCharge);
        return updatedPieceJustificative;
    }

    @BeforeEach
    void initTest() {
        pieceJustificative = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedPieceJustificative != null) {
            pieceJustificativeRepository.delete(insertedPieceJustificative);
            insertedPieceJustificative = null;
        }
    }

    @Test
    @Transactional
    void createPieceJustificative() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);
        var returnedPieceJustificativeDTO = om.readValue(
            restPieceJustificativeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PieceJustificativeDTO.class
        );

        // Validate the PieceJustificative in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPieceJustificative = pieceJustificativeMapper.toEntity(returnedPieceJustificativeDTO);
        assertPieceJustificativeUpdatableFieldsEquals(
            returnedPieceJustificative,
            getPersistedPieceJustificative(returnedPieceJustificative)
        );

        insertedPieceJustificative = returnedPieceJustificative;
    }

    @Test
    @Transactional
    void createPieceJustificativeWithExistingId() throws Exception {
        // Create the PieceJustificative with an existing ID
        pieceJustificative.setId(1L);
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPieceJustificativeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNomFichierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pieceJustificative.setNomFichier(null);

        // Create the PieceJustificative, which fails.
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        restPieceJustificativeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCheminFichierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pieceJustificative.setCheminFichier(null);

        // Create the PieceJustificative, which fails.
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        restPieceJustificativeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateAjoutIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pieceJustificative.setDateAjout(null);

        // Create the PieceJustificative, which fails.
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        restPieceJustificativeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPieceJustificatives() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        // Get all the pieceJustificativeList
        restPieceJustificativeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pieceJustificative.getId().intValue())))
            .andExpect(jsonPath("$.[*].nomFichier").value(hasItem(DEFAULT_NOM_FICHIER)))
            .andExpect(jsonPath("$.[*].cheminFichier").value(hasItem(DEFAULT_CHEMIN_FICHIER)))
            .andExpect(jsonPath("$.[*].dateAjout").value(hasItem(DEFAULT_DATE_AJOUT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPieceJustificativesWithEagerRelationshipsIsEnabled() throws Exception {
        when(pieceJustificativeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPieceJustificativeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pieceJustificativeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPieceJustificativesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pieceJustificativeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPieceJustificativeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pieceJustificativeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPieceJustificative() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        // Get the pieceJustificative
        restPieceJustificativeMockMvc
            .perform(get(ENTITY_API_URL_ID, pieceJustificative.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pieceJustificative.getId().intValue()))
            .andExpect(jsonPath("$.nomFichier").value(DEFAULT_NOM_FICHIER))
            .andExpect(jsonPath("$.cheminFichier").value(DEFAULT_CHEMIN_FICHIER))
            .andExpect(jsonPath("$.dateAjout").value(DEFAULT_DATE_AJOUT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingPieceJustificative() throws Exception {
        // Get the pieceJustificative
        restPieceJustificativeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPieceJustificative() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pieceJustificative
        PieceJustificative updatedPieceJustificative = pieceJustificativeRepository.findById(pieceJustificative.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPieceJustificative are not directly saved in db
        em.detach(updatedPieceJustificative);
        updatedPieceJustificative.nomFichier(UPDATED_NOM_FICHIER).cheminFichier(UPDATED_CHEMIN_FICHIER).dateAjout(UPDATED_DATE_AJOUT);
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(updatedPieceJustificative);

        restPieceJustificativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pieceJustificativeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pieceJustificativeDTO))
            )
            .andExpect(status().isOk());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPieceJustificativeToMatchAllProperties(updatedPieceJustificative);
    }

    @Test
    @Transactional
    void putNonExistingPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pieceJustificativeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pieceJustificativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pieceJustificativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePieceJustificativeWithPatch() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pieceJustificative using partial update
        PieceJustificative partialUpdatedPieceJustificative = new PieceJustificative();
        partialUpdatedPieceJustificative.setId(pieceJustificative.getId());

        partialUpdatedPieceJustificative
            .nomFichier(UPDATED_NOM_FICHIER)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateAjout(UPDATED_DATE_AJOUT);

        restPieceJustificativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPieceJustificative.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPieceJustificative))
            )
            .andExpect(status().isOk());

        // Validate the PieceJustificative in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPieceJustificativeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPieceJustificative, pieceJustificative),
            getPersistedPieceJustificative(pieceJustificative)
        );
    }

    @Test
    @Transactional
    void fullUpdatePieceJustificativeWithPatch() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pieceJustificative using partial update
        PieceJustificative partialUpdatedPieceJustificative = new PieceJustificative();
        partialUpdatedPieceJustificative.setId(pieceJustificative.getId());

        partialUpdatedPieceJustificative
            .nomFichier(UPDATED_NOM_FICHIER)
            .cheminFichier(UPDATED_CHEMIN_FICHIER)
            .dateAjout(UPDATED_DATE_AJOUT);

        restPieceJustificativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPieceJustificative.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPieceJustificative))
            )
            .andExpect(status().isOk());

        // Validate the PieceJustificative in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPieceJustificativeUpdatableFieldsEquals(
            partialUpdatedPieceJustificative,
            getPersistedPieceJustificative(partialUpdatedPieceJustificative)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pieceJustificativeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pieceJustificativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pieceJustificativeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPieceJustificative() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pieceJustificative.setId(longCount.incrementAndGet());

        // Create the PieceJustificative
        PieceJustificativeDTO pieceJustificativeDTO = pieceJustificativeMapper.toDto(pieceJustificative);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPieceJustificativeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pieceJustificativeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PieceJustificative in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePieceJustificative() throws Exception {
        // Initialize the database
        insertedPieceJustificative = pieceJustificativeRepository.saveAndFlush(pieceJustificative);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pieceJustificative
        restPieceJustificativeMockMvc
            .perform(delete(ENTITY_API_URL_ID, pieceJustificative.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pieceJustificativeRepository.count();
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

    protected PieceJustificative getPersistedPieceJustificative(PieceJustificative pieceJustificative) {
        return pieceJustificativeRepository.findById(pieceJustificative.getId()).orElseThrow();
    }

    protected void assertPersistedPieceJustificativeToMatchAllProperties(PieceJustificative expectedPieceJustificative) {
        assertPieceJustificativeAllPropertiesEquals(expectedPieceJustificative, getPersistedPieceJustificative(expectedPieceJustificative));
    }

    protected void assertPersistedPieceJustificativeToMatchUpdatableProperties(PieceJustificative expectedPieceJustificative) {
        assertPieceJustificativeAllUpdatablePropertiesEquals(
            expectedPieceJustificative,
            getPersistedPieceJustificative(expectedPieceJustificative)
        );
    }
}
