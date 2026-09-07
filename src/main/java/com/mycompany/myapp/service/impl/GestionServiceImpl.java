package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Gestion;
import com.mycompany.myapp.repository.GestionRepository;
import com.mycompany.myapp.service.GestionService;
import com.mycompany.myapp.service.dto.GestionDTO;
import com.mycompany.myapp.service.mapper.GestionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Gestion}.
 */
@Service
@Transactional
public class GestionServiceImpl implements GestionService {

    private static final Logger LOG = LoggerFactory.getLogger(GestionServiceImpl.class);

    private final GestionRepository gestionRepository;

    private final GestionMapper gestionMapper;

    public GestionServiceImpl(GestionRepository gestionRepository, GestionMapper gestionMapper) {
        this.gestionRepository = gestionRepository;
        this.gestionMapper = gestionMapper;
    }

    @Override
    public GestionDTO save(GestionDTO gestionDTO) {
        LOG.debug("Request to save Gestion : {}", gestionDTO);
        Gestion gestion = gestionMapper.toEntity(gestionDTO);
        gestion = gestionRepository.save(gestion);
        return gestionMapper.toDto(gestion);
    }

    @Override
    public GestionDTO update(GestionDTO gestionDTO) {
        LOG.debug("Request to update Gestion : {}", gestionDTO);
        Gestion gestion = gestionMapper.toEntity(gestionDTO);
        gestion = gestionRepository.save(gestion);
        return gestionMapper.toDto(gestion);
    }

    @Override
    public Optional<GestionDTO> partialUpdate(GestionDTO gestionDTO) {
        LOG.debug("Request to partially update Gestion : {}", gestionDTO);

        return gestionRepository
            .findById(gestionDTO.getId())
            .map(existingGestion -> {
                gestionMapper.partialUpdate(existingGestion, gestionDTO);

                return existingGestion;
            })
            .map(gestionRepository::save)
            .map(gestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GestionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Gestions");
        return gestionRepository.findAll(pageable).map(gestionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GestionDTO> findOne(Long id) {
        LOG.debug("Request to get Gestion : {}", id);
        return gestionRepository.findById(id).map(gestionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Gestion : {}", id);
        gestionRepository.deleteById(id);
    }
}
