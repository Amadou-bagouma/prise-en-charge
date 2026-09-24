package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TypeSoin;
import com.mycompany.myapp.repository.TypeSoinRepository;
import com.mycompany.myapp.service.TypeSoinService;
import com.mycompany.myapp.service.dto.TypeSoinDTO;
import com.mycompany.myapp.service.mapper.TypeSoinMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.TypeSoin}.
 */
@Service
@Transactional
public class TypeSoinServiceImpl implements TypeSoinService {

    private static final String ENTITY_NAME = "typeSoin";

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
        refuserCodeDejaPris(typeSoinDTO);
        TypeSoin typeSoin = typeSoinMapper.toEntity(typeSoinDTO);
        typeSoin = typeSoinRepository.save(typeSoin);
        return typeSoinMapper.toDto(typeSoin);
    }

    @Override
    public TypeSoinDTO update(TypeSoinDTO typeSoinDTO) {
        LOG.debug("Request to update TypeSoin : {}", typeSoinDTO);
        refuserCodeDejaPris(typeSoinDTO);
        TypeSoin typeSoin = typeSoinMapper.toEntity(typeSoinDTO);
        typeSoin = typeSoinRepository.save(typeSoin);
        return typeSoinMapper.toDto(typeSoin);
    }

    @Override
    public Optional<TypeSoinDTO> partialUpdate(TypeSoinDTO typeSoinDTO) {
        LOG.debug("Request to partially update TypeSoin : {}", typeSoinDTO);

        return typeSoinRepository
            .findById(typeSoinDTO.getId())
            .map(existing -> {
                typeSoinMapper.partialUpdate(existing, typeSoinDTO);
                return existing;
            })
            .map(typeSoinRepository::save)
            .map(typeSoinMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypeSoinDTO> findOne(Long id) {
        LOG.debug("Request to get TypeSoin : {}", id);
        return typeSoinRepository.findById(id).map(typeSoinMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TypeSoinDTO> findAllActifs() {
        LOG.debug("Request to get all actifs TypeSoin");
        return typeSoinRepository.findAllByActifIsTrueOrderByOrdreAscLibelleAsc().stream().map(typeSoinMapper::toDto).toList();
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete TypeSoin : {}", id);
        if (typeSoinRepository.isUtiliseParUneDemande(id)) {
            throw new BadRequestAlertException(
                "Ce type de soin figure sur des demandes : il ne peut pas etre supprime. Desactivez-le pour qu'il ne soit plus propose.",
                ENTITY_NAME,
                "typesoin.utilise"
            );
        }
        typeSoinRepository.deleteById(id);
    }

    /**
     * Le code est la cle stable du referentiel, celle que le rapport PDF interroge : deux types
     * qui le partagent rendraient l'imprime officiel imprevisible. La contrainte existe en base,
     * mais l'intercepter ici donne un message que l'agent comprend plutot qu'une erreur 500.
     */
    private void refuserCodeDejaPris(TypeSoinDTO typeSoinDTO) {
        typeSoinRepository
            .findOneByCode(typeSoinDTO.getCode())
            .filter(existant -> !existant.getId().equals(typeSoinDTO.getId()))
            .ifPresent(existant -> {
                throw new BadRequestAlertException("Un autre type de soin porte deja ce code.", ENTITY_NAME, "typesoin.codeexiste");
            });
    }
}
