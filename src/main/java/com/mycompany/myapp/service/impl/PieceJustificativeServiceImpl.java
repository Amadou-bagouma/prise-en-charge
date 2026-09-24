package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.PieceJustificative;
import com.mycompany.myapp.repository.PieceJustificativeRepository;
import com.mycompany.myapp.service.PieceJustificativeService;
import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
import com.mycompany.myapp.service.mapper.PieceJustificativeMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PieceJustificative}.
 */
@Service
@Transactional
public class PieceJustificativeServiceImpl implements PieceJustificativeService {

    private static final Logger LOG = LoggerFactory.getLogger(PieceJustificativeServiceImpl.class);

    private final PieceJustificativeRepository pieceJustificativeRepository;

    private final PieceJustificativeMapper pieceJustificativeMapper;

    public PieceJustificativeServiceImpl(
        PieceJustificativeRepository pieceJustificativeRepository,
        PieceJustificativeMapper pieceJustificativeMapper
    ) {
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.pieceJustificativeMapper = pieceJustificativeMapper;
    }

    @Override
    public PieceJustificativeDTO save(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to save PieceJustificative : {}", pieceJustificativeDTO);
        PieceJustificative pieceJustificative = pieceJustificativeMapper.toEntity(pieceJustificativeDTO);
        pieceJustificative = pieceJustificativeRepository.save(pieceJustificative);
        return pieceJustificativeMapper.toDto(pieceJustificative);
    }

    @Override
    public PieceJustificativeDTO update(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to update PieceJustificative : {}", pieceJustificativeDTO);
        PieceJustificative pieceJustificative = pieceJustificativeMapper.toEntity(pieceJustificativeDTO);
        pieceJustificative = pieceJustificativeRepository.save(pieceJustificative);
        return pieceJustificativeMapper.toDto(pieceJustificative);
    }

    @Override
    public Optional<PieceJustificativeDTO> partialUpdate(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to partially update PieceJustificative : {}", pieceJustificativeDTO);

        return pieceJustificativeRepository
            .findById(pieceJustificativeDTO.getId())
            .map(existingPieceJustificative -> {
                pieceJustificativeMapper.partialUpdate(existingPieceJustificative, pieceJustificativeDTO);

                return existingPieceJustificative;
            })
            .map(pieceJustificativeRepository::save)
            .map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PieceJustificativeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PieceJustificatives");
        return pieceJustificativeRepository.findAll(pageable).map(pieceJustificativeMapper::toDto);
    }

    public Page<PieceJustificativeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pieceJustificativeRepository.findAllWithEagerRelationships(pageable).map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PieceJustificativeDTO> findAllByDemande(Long demandeId, Pageable pageable) {
        LOG.debug("Request to get PieceJustificatives of demande : {}", demandeId);
        return pieceJustificativeRepository.findAllByDemandeId(demandeId, pageable).map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PieceJustificativeDTO> findOne(Long id) {
        LOG.debug("Request to get PieceJustificative : {}", id);
        return pieceJustificativeRepository.findOneWithEagerRelationships(id).map(pieceJustificativeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PieceJustificative : {}", id);
        pieceJustificativeRepository.deleteById(id);
    }
}
