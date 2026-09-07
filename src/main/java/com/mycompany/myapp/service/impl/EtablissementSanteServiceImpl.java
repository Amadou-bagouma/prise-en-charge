package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.EtablissementSante;
import com.mycompany.myapp.repository.EtablissementSanteRepository;
import com.mycompany.myapp.service.EtablissementSanteService;
import com.mycompany.myapp.service.dto.EtablissementSanteDTO;
import com.mycompany.myapp.service.mapper.EtablissementSanteMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.EtablissementSante}.
 */
@Service
@Transactional
public class EtablissementSanteServiceImpl implements EtablissementSanteService {

    private static final Logger LOG = LoggerFactory.getLogger(EtablissementSanteServiceImpl.class);

    private final EtablissementSanteRepository etablissementSanteRepository;

    private final EtablissementSanteMapper etablissementSanteMapper;

    public EtablissementSanteServiceImpl(
        EtablissementSanteRepository etablissementSanteRepository,
        EtablissementSanteMapper etablissementSanteMapper
    ) {
        this.etablissementSanteRepository = etablissementSanteRepository;
        this.etablissementSanteMapper = etablissementSanteMapper;
    }

    @Override
    public EtablissementSanteDTO save(EtablissementSanteDTO etablissementSanteDTO) {
        LOG.debug("Request to save EtablissementSante : {}", etablissementSanteDTO);
        EtablissementSante etablissementSante = etablissementSanteMapper.toEntity(etablissementSanteDTO);
        etablissementSante = etablissementSanteRepository.save(etablissementSante);
        return etablissementSanteMapper.toDto(etablissementSante);
    }

    @Override
    public EtablissementSanteDTO update(EtablissementSanteDTO etablissementSanteDTO) {
        LOG.debug("Request to update EtablissementSante : {}", etablissementSanteDTO);
        EtablissementSante etablissementSante = etablissementSanteMapper.toEntity(etablissementSanteDTO);
        etablissementSante = etablissementSanteRepository.save(etablissementSante);
        return etablissementSanteMapper.toDto(etablissementSante);
    }

    @Override
    public Optional<EtablissementSanteDTO> partialUpdate(EtablissementSanteDTO etablissementSanteDTO) {
        LOG.debug("Request to partially update EtablissementSante : {}", etablissementSanteDTO);

        return etablissementSanteRepository
            .findById(etablissementSanteDTO.getId())
            .map(existingEtablissementSante -> {
                etablissementSanteMapper.partialUpdate(existingEtablissementSante, etablissementSanteDTO);

                return existingEtablissementSante;
            })
            .map(etablissementSanteRepository::save)
            .map(etablissementSanteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EtablissementSanteDTO> findOne(Long id) {
        LOG.debug("Request to get EtablissementSante : {}", id);
        return etablissementSanteRepository.findById(id).map(etablissementSanteMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete EtablissementSante : {}", id);
        etablissementSanteRepository.deleteById(id);
    }
}
