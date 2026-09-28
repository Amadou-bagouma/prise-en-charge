package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.ProfilService;
import com.mycompany.myapp.service.dto.ProfilDTO;
import com.mycompany.myapp.service.mapper.ProfilMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.Profil}.
 */
@Service
@Transactional
public class ProfilServiceImpl implements ProfilService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfilServiceImpl.class);

    private static final String ENTITY_NAME = "profil";

    private final ProfilRepository profilRepository;

    private final ProfilMapper profilMapper;

    private final UserRepository userRepository;

    public ProfilServiceImpl(ProfilRepository profilRepository, ProfilMapper profilMapper, UserRepository userRepository) {
        this.profilRepository = profilRepository;
        this.profilMapper = profilMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ProfilDTO save(ProfilDTO profilDTO) {
        LOG.debug("Request to save Profil : {}", profilDTO);
        Profil profil = profilMapper.toEntity(profilDTO);
        profil = profilRepository.save(profil);
        return profilMapper.toDto(profil);
    }

    @Override
    public ProfilDTO update(ProfilDTO profilDTO) {
        LOG.debug("Request to update Profil : {}", profilDTO);
        Profil profil = profilMapper.toEntity(profilDTO);
        profil = profilRepository.save(profil);
        return profilMapper.toDto(profil);
    }

    @Override
    public Optional<ProfilDTO> partialUpdate(ProfilDTO profilDTO) {
        LOG.debug("Request to partially update Profil : {}", profilDTO);

        return profilRepository
            .findById(profilDTO.getId())
            .map(existingProfil -> {
                profilMapper.partialUpdate(existingProfil, profilDTO);

                return existingProfil;
            })
            .map(profilRepository::save)
            .map(profilMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProfilDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Profils");
        return profilRepository.findAll(pageable).map(profilMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfilDTO> findOne(Long id) {
        LOG.debug("Request to get Profil : {}", id);
        return profilRepository.findById(id).map(profilMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Profil : {}", id);
        if (userRepository.existsByProfilId(id)) {
            throw new BadRequestAlertException(
                "Ce profil est encore attribue a au moins un utilisateur et ne peut pas etre supprime",
                ENTITY_NAME,
                "profil.enusage"
            );
        }
        profilRepository.deleteById(id);
    }
}
