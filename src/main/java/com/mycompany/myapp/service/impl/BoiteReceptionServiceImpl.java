package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.service.BoiteReceptionService;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.mapper.BoiteReceptionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 */
@Service
@Transactional
public class BoiteReceptionServiceImpl implements BoiteReceptionService {

    private static final Logger LOG = LoggerFactory.getLogger(BoiteReceptionServiceImpl.class);

    private final BoiteReceptionRepository boiteReceptionRepository;

    private final BoiteReceptionMapper boiteReceptionMapper;

    public BoiteReceptionServiceImpl(BoiteReceptionRepository boiteReceptionRepository, BoiteReceptionMapper boiteReceptionMapper) {
        this.boiteReceptionRepository = boiteReceptionRepository;
        this.boiteReceptionMapper = boiteReceptionMapper;
    }

    @Override
    public BoiteReceptionDTO save(BoiteReceptionDTO boiteReceptionDTO) {
        LOG.debug("Request to save BoiteReception : {}", boiteReceptionDTO);
        BoiteReception boiteReception = boiteReceptionMapper.toEntity(boiteReceptionDTO);
        boiteReception = boiteReceptionRepository.save(boiteReception);
        return boiteReceptionMapper.toDto(boiteReception);
    }

    @Override
    public BoiteReceptionDTO update(BoiteReceptionDTO boiteReceptionDTO) {
        LOG.debug("Request to update BoiteReception : {}", boiteReceptionDTO);
        BoiteReception boiteReception = boiteReceptionMapper.toEntity(boiteReceptionDTO);
        boiteReception = boiteReceptionRepository.save(boiteReception);
        return boiteReceptionMapper.toDto(boiteReception);
    }

    @Override
    public Optional<BoiteReceptionDTO> partialUpdate(BoiteReceptionDTO boiteReceptionDTO) {
        LOG.debug("Request to partially update BoiteReception : {}", boiteReceptionDTO);

        return boiteReceptionRepository
            .findById(boiteReceptionDTO.getId())
            .map(existingBoiteReception -> {
                boiteReceptionMapper.partialUpdate(existingBoiteReception, boiteReceptionDTO);

                return existingBoiteReception;
            })
            .map(boiteReceptionRepository::save)
            .map(boiteReceptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoiteReceptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all BoiteReceptions");
        return boiteReceptionRepository.findAll(pageable).map(boiteReceptionMapper::toDto);
    }

    public Page<BoiteReceptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return boiteReceptionRepository.findAllWithEagerRelationships(pageable).map(boiteReceptionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BoiteReceptionDTO> findOne(Long id) {
        LOG.debug("Request to get BoiteReception : {}", id);
        return boiteReceptionRepository.findOneWithEagerRelationships(id).map(boiteReceptionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete BoiteReception : {}", id);
        boiteReceptionRepository.deleteById(id);
    }
}
