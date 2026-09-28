package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.ProfilService;
import com.mycompany.myapp.service.dto.ProfilDTO;
import com.mycompany.myapp.service.mapper.ProfilMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

    private final BoiteReceptionRepository boiteReceptionRepository;

    private final BoiteReceptionServiceImpl boiteReceptionService;

    private final CacheManager cacheManager;

    public ProfilServiceImpl(
        ProfilRepository profilRepository,
        ProfilMapper profilMapper,
        UserRepository userRepository,
        BoiteReceptionRepository boiteReceptionRepository,
        BoiteReceptionServiceImpl boiteReceptionService,
        CacheManager cacheManager
    ) {
        this.profilRepository = profilRepository;
        this.profilMapper = profilMapper;
        this.userRepository = userRepository;
        this.boiteReceptionRepository = boiteReceptionRepository;
        this.boiteReceptionService = boiteReceptionService;
        this.cacheManager = cacheManager;
    }

    @Override
    public ProfilDTO save(ProfilDTO profilDTO) {
        LOG.debug("Request to save Profil : {}", profilDTO);
        Profil profil = profilMapper.toEntity(profilDTO);
        profil = profilRepository.save(profil);
        // Un profil sans boite serait un role dont les taches ne parviennent nulle part : la
        // boite nait avec lui, elle n'est jamais creee a la main.
        if (!boiteReceptionRepository.existsByProfilId(profil.getId())) {
            boiteReceptionService.creerBoite(profil);
        }
        return profilMapper.toDto(profil);
    }

    @Override
    public ProfilDTO update(ProfilDTO profilDTO) {
        LOG.debug("Request to update Profil : {}", profilDTO);
        Profil profil = profilMapper.toEntity(profilDTO);
        profil = profilRepository.save(profil);
        repercuterDroits(profil);
        return profilMapper.toDto(profil);
    }

    /**
     * Recopie les droits du profil sur les comptes qui le portent.
     *
     * <p>Les autorites sont dupliquees sur l'utilisateur, parce que c'est de la qu'elles sont
     * lues a l'authentification et placees dans le jeton. Sans cette repercussion, accorder un
     * droit a un profil ne produisait aucun effet tant que chaque compte n'avait pas ete rouvert
     * et re-enregistre a la main - et l'administrateur, voyant le droit coche a l'ecran, n'avait
     * aucune raison de soupconner qu'il ne s'appliquait pas.
     *
     * <p>Les jetons deja emis gardent les anciens droits jusqu'a leur expiration : un changement
     * de droits prend effet a la prochaine authentification.
     */
    private void repercuterDroits(Profil profil) {
        List<User> titulaires = userRepository.findAllByProfilId(profil.getId());
        for (User titulaire : titulaires) {
            Set<Authority> droits = titulaire.getAuthorities();
            droits.clear();
            droits.addAll(profil.getAuthorities());
        }
        if (!titulaires.isEmpty()) {
            userRepository.saveAll(titulaires);
            // Le compte est servi depuis un cache a l'authentification : sans purge, le jeton
            // continuerait de porter les anciens droits jusqu'a l'expiration de l'entree, et le
            // changement paraitrait sans effet.
            titulaires.forEach(this::purgerCache);
            LOG.info("Droits du profil {} repercutes sur {} compte(s)", profil.getNom(), titulaires.size());
        }
    }

    private void purgerCache(User utilisateur) {
        Cache parLogin = cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE);
        if (parLogin != null) {
            parLogin.evictIfPresent(utilisateur.getLogin());
        }
        Cache parEmail = cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE);
        if (parEmail != null && utilisateur.getEmail() != null) {
            parEmail.evictIfPresent(utilisateur.getEmail());
        }
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
            .map(profil -> {
                repercuterDroits(profil);
                return profil;
            })
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
        // La boite suit le profil : la laisser derriere ferait echouer la suppression sur la
        // contrainte de cle etrangere.
        boiteReceptionRepository.trouverParProfil(id).ifPresent(boiteReceptionRepository::delete);
        profilRepository.deleteById(id);
    }
}
