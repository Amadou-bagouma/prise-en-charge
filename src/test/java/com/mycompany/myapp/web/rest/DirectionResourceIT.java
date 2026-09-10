package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.DirectionAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.domain.Region;
import com.mycompany.myapp.repository.DirectionRepository;
import com.mycompany.myapp.service.DirectionService;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.mapper.DirectionMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link DirectionResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class DirectionResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/directions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DirectionRepository directionRepository;

    @Mock
    private DirectionRepository directionRepositoryMock;

    @Autowired
    private DirectionMapper directionMapper;

    @Mock
    private DirectionService directionServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDirectionMockMvc;

    private Direction direction;

    private Direction insertedDirection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Direction createEntity() {
        return new Direction().code(DEFAULT_CODE).nom(DEFAULT_NOM);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Direction createUpdatedEntity() {
        return new Direction().code(UPDATED_CODE).nom(UPDATED_NOM);
    }

    @BeforeEach
    void initTest() {
        direction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedDirection != null) {
            directionRepository.delete(insertedDirection);
            insertedDirection = null;
        }
    }

    @Test
    @Transactional
    void createDirection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);
        var returnedDirectionDTO = om.readValue(
            restDirectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(directionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DirectionDTO.class
        );

        // Validate the Direction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDirection = directionMapper.toEntity(returnedDirectionDTO);
        assertDirectionUpdatableFieldsEquals(returnedDirection, getPersistedDirection(returnedDirection));

        insertedDirection = returnedDirection;
    }

    @Test
    @Transactional
    void createDirectionWithExistingId() throws Exception {
        // Create the Direction with an existing ID
        direction.setId(1L);
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDirectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(directionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        direction.setCode(null);

        // Create the Direction, which fails.
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        restDirectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(directionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNomIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        direction.setNom(null);

        // Create the Direction, which fails.
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        restDirectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(directionDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDirections() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(direction.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDirectionsWithEagerRelationshipsIsEnabled() throws Exception {
        when(directionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDirectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(directionServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllDirectionsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(directionServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restDirectionMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(directionRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getDirection() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get the direction
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL_ID, direction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(direction.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM));
    }

    @Test
    @Transactional
    void getDirectionsByIdFiltering() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        Long id = direction.getId();

        defaultDirectionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultDirectionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultDirectionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDirectionsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where code equals to
        defaultDirectionFiltering("code.equals=" + DEFAULT_CODE, "code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDirectionsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where code in
        defaultDirectionFiltering("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE, "code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDirectionsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where code is not null
        defaultDirectionFiltering("code.specified=true", "code.specified=false");
    }

    @Test
    @Transactional
    void getAllDirectionsByCodeContainsSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where code contains
        defaultDirectionFiltering("code.contains=" + DEFAULT_CODE, "code.contains=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    void getAllDirectionsByCodeNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where code does not contain
        defaultDirectionFiltering("code.doesNotContain=" + UPDATED_CODE, "code.doesNotContain=" + DEFAULT_CODE);
    }

    @Test
    @Transactional
    void getAllDirectionsByNomIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where nom equals to
        defaultDirectionFiltering("nom.equals=" + DEFAULT_NOM, "nom.equals=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllDirectionsByNomIsInShouldWork() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where nom in
        defaultDirectionFiltering("nom.in=" + DEFAULT_NOM + "," + UPDATED_NOM, "nom.in=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllDirectionsByNomIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where nom is not null
        defaultDirectionFiltering("nom.specified=true", "nom.specified=false");
    }

    @Test
    @Transactional
    void getAllDirectionsByNomContainsSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where nom contains
        defaultDirectionFiltering("nom.contains=" + DEFAULT_NOM, "nom.contains=" + UPDATED_NOM);
    }

    @Test
    @Transactional
    void getAllDirectionsByNomNotContainsSomething() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        // Get all the directionList where nom does not contain
        defaultDirectionFiltering("nom.doesNotContain=" + UPDATED_NOM, "nom.doesNotContain=" + DEFAULT_NOM);
    }

    @Test
    @Transactional
    void getAllDirectionsByRegionIsEqualToSomething() throws Exception {
        Region region;
        if (TestUtil.findAll(em, Region.class).isEmpty()) {
            directionRepository.saveAndFlush(direction);
            region = RegionResourceIT.createEntity();
        } else {
            region = TestUtil.findAll(em, Region.class).get(0);
        }
        em.persist(region);
        em.flush();
        direction.setRegion(region);
        directionRepository.saveAndFlush(direction);
        Long regionId = region.getId();
        // Get all the directionList where region equals to regionId
        defaultDirectionShouldBeFound("regionId.equals=" + regionId);

        // Get all the directionList where region equals to (regionId + 1)
        defaultDirectionShouldNotBeFound("regionId.equals=" + (regionId + 1));
    }

    private void defaultDirectionFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultDirectionShouldBeFound(shouldBeFound);
        defaultDirectionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDirectionShouldBeFound(String filter) throws Exception {
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(direction.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));

        // Check, that the count call also returns 1
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDirectionShouldNotBeFound(String filter) throws Exception {
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDirectionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDirection() throws Exception {
        // Get the direction
        restDirectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDirection() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the direction
        Direction updatedDirection = directionRepository.findById(direction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDirection are not directly saved in db
        em.detach(updatedDirection);
        updatedDirection.code(UPDATED_CODE).nom(UPDATED_NOM);
        DirectionDTO directionDTO = directionMapper.toDto(updatedDirection);

        restDirectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(directionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDirectionToMatchAllProperties(updatedDirection);
    }

    @Test
    @Transactional
    void putNonExistingDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, directionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(directionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(directionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(directionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDirectionWithPatch() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the direction using partial update
        Direction partialUpdatedDirection = new Direction();
        partialUpdatedDirection.setId(direction.getId());

        partialUpdatedDirection.code(UPDATED_CODE).nom(UPDATED_NOM);

        restDirectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDirection))
            )
            .andExpect(status().isOk());

        // Validate the Direction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDirectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDirection, direction),
            getPersistedDirection(direction)
        );
    }

    @Test
    @Transactional
    void fullUpdateDirectionWithPatch() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the direction using partial update
        Direction partialUpdatedDirection = new Direction();
        partialUpdatedDirection.setId(direction.getId());

        partialUpdatedDirection.code(UPDATED_CODE).nom(UPDATED_NOM);

        restDirectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDirection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDirection))
            )
            .andExpect(status().isOk());

        // Validate the Direction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDirectionUpdatableFieldsEquals(partialUpdatedDirection, getPersistedDirection(partialUpdatedDirection));
    }

    @Test
    @Transactional
    void patchNonExistingDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, directionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(directionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(directionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDirection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        direction.setId(longCount.incrementAndGet());

        // Create the Direction
        DirectionDTO directionDTO = directionMapper.toDto(direction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDirectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(directionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Direction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDirection() throws Exception {
        // Initialize the database
        insertedDirection = directionRepository.saveAndFlush(direction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the direction
        restDirectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, direction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return directionRepository.count();
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

    protected Direction getPersistedDirection(Direction direction) {
        return directionRepository.findById(direction.getId()).orElseThrow();
    }

    protected void assertPersistedDirectionToMatchAllProperties(Direction expectedDirection) {
        assertDirectionAllPropertiesEquals(expectedDirection, getPersistedDirection(expectedDirection));
    }

    protected void assertPersistedDirectionToMatchUpdatableProperties(Direction expectedDirection) {
        assertDirectionAllUpdatablePropertiesEquals(expectedDirection, getPersistedDirection(expectedDirection));
    }
}
