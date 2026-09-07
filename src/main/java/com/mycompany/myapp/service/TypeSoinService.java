package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TypeSoinDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 */
public interface TypeSoinService {
    /**
     * Save a typeSoin.
     *
     * @param typeSoinDTO the entity to save.
     * @return the persisted entity.
     */
    TypeSoinDTO save(TypeSoinDTO typeSoinDTO);

    /**
     * Updates a typeSoin.
     *
     * @param typeSoinDTO the entity to update.
     * @return the persisted entity.
     */
    TypeSoinDTO update(TypeSoinDTO typeSoinDTO);

    /**
     * Partially updates a typeSoin.
     *
     * @param typeSoinDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TypeSoinDTO> partialUpdate(TypeSoinDTO typeSoinDTO);

    /**
     * Get all the typeSoins.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TypeSoinDTO> findAll(Pageable pageable);

    /**
     * Get the "id" typeSoin.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TypeSoinDTO> findOne(Long id);

    /**
     * Delete the "id" typeSoin.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
