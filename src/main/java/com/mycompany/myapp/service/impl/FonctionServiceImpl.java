package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Fonction;
import com.mycompany.myapp.repository.FonctionRepository;
import com.mycompany.myapp.service.FonctionService;
import com.mycompany.myapp.service.dto.FonctionDTO;
import com.mycompany.myapp.service.mapper.FonctionMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Fonction}.
 */
@Service
@Transactional
public class FonctionServiceImpl implements FonctionService {

    private static final String ENTITY_NAME = "fonction";

    private static final Logger LOG = LoggerFactory.getLogger(FonctionServiceImpl.class);

    private final FonctionRepository fonctionRepository;

    private final FonctionMapper fonctionMapper;

    public FonctionServiceImpl(FonctionRepository fonctionRepository, FonctionMapper fonctionMapper) {
        this.fonctionRepository = fonctionRepository;
        this.fonctionMapper = fonctionMapper;
    }

    @Override
    public FonctionDTO save(FonctionDTO fonctionDTO) {
        LOG.debug("Request to save Fonction : {}", fonctionDTO);
        nettoyerEtRefuserDoublon(fonctionDTO);
        Fonction fonction = fonctionMapper.toEntity(fonctionDTO);
        fonction = fonctionRepository.save(fonction);
        return fonctionMapper.toDto(fonction);
    }

    @Override
    public FonctionDTO update(FonctionDTO fonctionDTO) {
        LOG.debug("Request to update Fonction : {}", fonctionDTO);
        nettoyerEtRefuserDoublon(fonctionDTO);
        Fonction fonction = fonctionMapper.toEntity(fonctionDTO);
        fonction = fonctionRepository.save(fonction);
        return fonctionMapper.toDto(fonction);
    }

    @Override
    public Optional<FonctionDTO> partialUpdate(FonctionDTO fonctionDTO) {
        LOG.debug("Request to partially update Fonction : {}", fonctionDTO);
        if (fonctionDTO.getLibelle() != null) {
            nettoyerEtRefuserDoublon(fonctionDTO);
        }

        return fonctionRepository
            .findById(fonctionDTO.getId())
            .map(existingFonction -> {
                fonctionMapper.partialUpdate(existingFonction, fonctionDTO);

                return existingFonction;
            })
            .map(fonctionRepository::save)
            .map(fonctionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FonctionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Fonctions");
        return fonctionRepository.findAll(pageable).map(fonctionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FonctionDTO> findOne(Long id) {
        LOG.debug("Request to get Fonction : {}", id);
        return fonctionRepository.findById(id).map(fonctionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Fonction : {}", id);
        if (fonctionRepository.isUtiliseeParUnAgent(id)) {
            throw new BadRequestAlertException(
                "Cette fonction est occupee par des agents : changez d'abord leur fonction avant de la supprimer.",
                ENTITY_NAME,
                "fonction.utilisee"
            );
        }
        fonctionRepository.deleteById(id);
    }

    /**
     * « Comptable » et « comptable » sont la meme fonction : la contrainte d'unicite en base ne
     * le voit pas, d'ou ce controle insensible a la casse, qui donne en outre un message lisible
     * plutot qu'une erreur 500.
     */
    private void nettoyerEtRefuserDoublon(FonctionDTO fonctionDTO) {
        fonctionDTO.setLibelle(fonctionDTO.getLibelle().trim());
        fonctionRepository
            .findOneByLibelleIgnoreCase(fonctionDTO.getLibelle())
            .filter(existante -> !existante.getId().equals(fonctionDTO.getId()))
            .ifPresent(existante -> {
                throw new BadRequestAlertException("Cette fonction existe deja.", ENTITY_NAME, "fonction.existe");
            });
    }
}
