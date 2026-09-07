package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Direction;
import com.mycompany.myapp.repository.DirectionRepository;
import com.mycompany.myapp.service.DirectionService;
import com.mycompany.myapp.service.dto.DirectionDTO;
import com.mycompany.myapp.service.mapper.DirectionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Direction}.
 */
@Service
@Transactional
public class DirectionServiceImpl implements DirectionService {

    private static final Logger LOG = LoggerFactory.getLogger(DirectionServiceImpl.class);

    private final DirectionRepository directionRepository;

    private final DirectionMapper directionMapper;

    public DirectionServiceImpl(DirectionRepository directionRepository, DirectionMapper directionMapper) {
        this.directionRepository = directionRepository;
        this.directionMapper = directionMapper;
    }

    @Override
    public DirectionDTO save(DirectionDTO directionDTO) {
        LOG.debug("Request to save Direction : {}", directionDTO);
        Direction direction = directionMapper.toEntity(directionDTO);
        direction = directionRepository.save(direction);
        return directionMapper.toDto(direction);
    }

    @Override
    public DirectionDTO update(DirectionDTO directionDTO) {
        LOG.debug("Request to update Direction : {}", directionDTO);
        Direction direction = directionMapper.toEntity(directionDTO);
        direction = directionRepository.save(direction);
        return directionMapper.toDto(direction);
    }

    @Override
    public Optional<DirectionDTO> partialUpdate(DirectionDTO directionDTO) {
        LOG.debug("Request to partially update Direction : {}", directionDTO);

        return directionRepository
            .findById(directionDTO.getId())
            .map(existingDirection -> {
                directionMapper.partialUpdate(existingDirection, directionDTO);

                return existingDirection;
            })
            .map(directionRepository::save)
            .map(directionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DirectionDTO> findOne(Long id) {
        LOG.debug("Request to get Direction : {}", id);
        return directionRepository.findById(id).map(directionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Direction : {}", id);
        directionRepository.deleteById(id);
    }
}
