package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.PieceJustificative}.
 */
public interface PieceJustificativeService {
    /**
     * Save a pieceJustificative.
     *
     * @param pieceJustificativeDTO the entity to save.
     * @return the persisted entity.
     */
    PieceJustificativeDTO save(PieceJustificativeDTO pieceJustificativeDTO);

    /**
     * Updates a pieceJustificative.
     *
     * @param pieceJustificativeDTO the entity to update.
     * @return the persisted entity.
     */
    PieceJustificativeDTO update(PieceJustificativeDTO pieceJustificativeDTO);

    /**
     * Partially updates a pieceJustificative.
     *
     * @param pieceJustificativeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PieceJustificativeDTO> partialUpdate(PieceJustificativeDTO pieceJustificativeDTO);

    /**
     * Get all the pieceJustificatives.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PieceJustificativeDTO> findAll(Pageable pageable);

    /**
     * Get all the pieceJustificatives with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PieceJustificativeDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Les pieces jointes a une demande.
     *
     * @param demandeId le dossier concerne.
     * @param pageable la pagination.
     * @return les pieces de ce dossier.
     */
    Page<PieceJustificativeDTO> findAllByDemande(Long demandeId, Pageable pageable);

    /**
     * Get the "id" pieceJustificative.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PieceJustificativeDTO> findOne(Long id);

    /**
     * Delete the "id" pieceJustificative.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
