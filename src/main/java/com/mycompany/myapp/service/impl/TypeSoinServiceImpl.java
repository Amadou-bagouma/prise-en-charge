package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.service.TypeSoinService;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.mapper.TypeSoinMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 */
@Service
@Transactional
public class TypeSoinServiceImpl implements TypeSoinService {

    private static final Logger LOG = LoggerFactory.getLogger(TypeSoinServiceImpl.class);

    private final TypeSoinRepository typeSoinRepository;

    private final TypeSoinMapper typeSoinMapper;

    public TypeSoinServiceImpl(TypeSoinRepository typeSoinRepository, TypeSoinMapper typeSoinMapper) {
        this.typeSoinRepository = typeSoinRepository;
        this.typeSoinMapper = typeSoinMapper;
    }

    @Override
    public TypeSoinDTO save(TypeSoinDTO typeSoinDTO) {
        LOG.debug("Request to save TypeSoin : {}", typeSoinDTO);
        TypeSoin typeSoin = typeSoinMapper.toEntity(typeSoinDTO);
        typeSoin = typeSoinRepository.save(typeSoin);
        return typeSoinMapper.toDto(typeSoin);
    }

    @Override
    public TypeSoinDTO update(TypeSoinDTO typeSoinDTO) {
        LOG.debug("Request to update TypeSoin : {}", typeSoinDTO);
        TypeSoin typeSoin = typeSoinMapper.toEntity(typeSoinDTO);
        typeSoin = typeSoinRepository.save(typeSoin);
        return typeSoinMapper.toDto(typeSoin);
    }

    @Override
    public Optional<TypeSoinDTO> partialUpdate(TypeSoinDTO typeSoinDTO) {
        LOG.debug("Request to partially update TypeSoin : {}", typeSoinDTO);

        return typeSoinRepository
            .findById(typeSoinDTO.getId())
            .map(existingTypeSoin -> {
                typeSoinMapper.partialUpdate(existingTypeSoin, typeSoinDTO);

                return existingTypeSoin;
            })
            .map(typeSoinRepository::save)
            .map(typeSoinMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypeSoinDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all TypeSoins");
        return typeSoinRepository.findAll(pageable).map(typeSoinMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypeSoinDTO> findOne(Long id) {
        LOG.debug("Request to get TypeSoin : {}", id);
        return typeSoinRepository.findById(id).map(typeSoinMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TypeSoin : {}", id);
        typeSoinRepository.deleteById(id);
    }
}
