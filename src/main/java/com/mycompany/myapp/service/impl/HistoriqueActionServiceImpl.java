package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.service.HistoriqueActionService;
import com.mycompany.myapp.service.dto.HistoriqueActionDTO;
import com.mycompany.myapp.service.mapper.HistoriqueActionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.HistoriqueAction}.
 */
@Service
@Transactional
public class HistoriqueActionServiceImpl implements HistoriqueActionService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueActionServiceImpl.class);

    private final HistoriqueActionRepository historiqueActionRepository;

    private final HistoriqueActionMapper historiqueActionMapper;

    public HistoriqueActionServiceImpl(
        HistoriqueActionRepository historiqueActionRepository,
        HistoriqueActionMapper historiqueActionMapper
    ) {
        this.historiqueActionRepository = historiqueActionRepository;
        this.historiqueActionMapper = historiqueActionMapper;
    }

    @Override
    public HistoriqueActionDTO save(HistoriqueActionDTO historiqueActionDTO) {
        LOG.debug("Request to save HistoriqueAction : {}", historiqueActionDTO);
        HistoriqueAction historiqueAction = historiqueActionMapper.toEntity(historiqueActionDTO);
        historiqueAction = historiqueActionRepository.save(historiqueAction);
        return historiqueActionMapper.toDto(historiqueAction);
    }

    @Override
    public HistoriqueActionDTO update(HistoriqueActionDTO historiqueActionDTO) {
        LOG.debug("Request to update HistoriqueAction : {}", historiqueActionDTO);
        HistoriqueAction historiqueAction = historiqueActionMapper.toEntity(historiqueActionDTO);
        historiqueAction = historiqueActionRepository.save(historiqueAction);
        return historiqueActionMapper.toDto(historiqueAction);
    }

    @Override
    public Optional<HistoriqueActionDTO> partialUpdate(HistoriqueActionDTO historiqueActionDTO) {
        LOG.debug("Request to partially update HistoriqueAction : {}", historiqueActionDTO);

        return historiqueActionRepository
            .findById(historiqueActionDTO.getId())
            .map(existingHistoriqueAction -> {
                historiqueActionMapper.partialUpdate(existingHistoriqueAction, historiqueActionDTO);

                return existingHistoriqueAction;
            })
            .map(historiqueActionRepository::save)
            .map(historiqueActionMapper::toDto);
    }

    public Page<HistoriqueActionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return historiqueActionRepository.findAllWithEagerRelationships(pageable).map(historiqueActionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistoriqueActionDTO> findOne(Long id) {
        LOG.debug("Request to get HistoriqueAction : {}", id);
        return historiqueActionRepository.findOneWithEagerRelationships(id).map(historiqueActionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete HistoriqueAction : {}", id);
        historiqueActionRepository.deleteById(id);
    }
}
