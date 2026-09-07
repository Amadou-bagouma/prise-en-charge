package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.service.AyantDroitService;
import com.mycompany.myapp.service.dto.AyantDroitDTO;
import com.mycompany.myapp.service.mapper.AyantDroitMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.AyantDroit}.
 */
@Service
@Transactional
public class AyantDroitServiceImpl implements AyantDroitService {

    private static final Logger LOG = LoggerFactory.getLogger(AyantDroitServiceImpl.class);

    private final AyantDroitRepository ayantDroitRepository;

    private final AyantDroitMapper ayantDroitMapper;

    public AyantDroitServiceImpl(AyantDroitRepository ayantDroitRepository, AyantDroitMapper ayantDroitMapper) {
        this.ayantDroitRepository = ayantDroitRepository;
        this.ayantDroitMapper = ayantDroitMapper;
    }

    @Override
    public AyantDroitDTO save(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to save AyantDroit : {}", ayantDroitDTO);
        AyantDroit ayantDroit = ayantDroitMapper.toEntity(ayantDroitDTO);
        ayantDroit = ayantDroitRepository.save(ayantDroit);
        return ayantDroitMapper.toDto(ayantDroit);
    }

    @Override
    public AyantDroitDTO update(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to update AyantDroit : {}", ayantDroitDTO);
        AyantDroit ayantDroit = ayantDroitMapper.toEntity(ayantDroitDTO);
        ayantDroit = ayantDroitRepository.save(ayantDroit);
        return ayantDroitMapper.toDto(ayantDroit);
    }

    @Override
    public Optional<AyantDroitDTO> partialUpdate(AyantDroitDTO ayantDroitDTO) {
        LOG.debug("Request to partially update AyantDroit : {}", ayantDroitDTO);

        return ayantDroitRepository
            .findById(ayantDroitDTO.getId())
            .map(existingAyantDroit -> {
                ayantDroitMapper.partialUpdate(existingAyantDroit, ayantDroitDTO);

                return existingAyantDroit;
            })
            .map(ayantDroitRepository::save)
            .map(ayantDroitMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AyantDroitDTO> findOne(Long id) {
        LOG.debug("Request to get AyantDroit : {}", id);
        return ayantDroitRepository.findById(id).map(ayantDroitMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete AyantDroit : {}", id);
        ayantDroitRepository.deleteById(id);
    }
}
