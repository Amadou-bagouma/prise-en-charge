package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.BoiteReception;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.repository.BoiteReceptionRepository;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.repository.TacheRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.BoiteReceptionService;
import com.mycompany.myapp.service.RoutageBoiteReception;
import com.mycompany.myapp.service.RoutageBoiteReception.Portee;
import com.mycompany.myapp.service.dto.BoiteReceptionDTO;
import com.mycompany.myapp.service.dto.TacheDTO;
import com.mycompany.myapp.service.mapper.BoiteReceptionMapper;
import com.mycompany.myapp.service.mapper.TacheMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.BoiteReception}.
 */
@Service
@Transactional
public class BoiteReceptionServiceImpl implements BoiteReceptionService {

    private static final Logger LOG = LoggerFactory.getLogger(BoiteReceptionServiceImpl.class);

    /** Une boite sert a savoir ce qui reste : terminee et annulee n'y attendent plus personne. */
    private static final Set<StatutTache> STATUTS_OUVERTS = EnumSet.of(StatutTache.A_FAIRE, StatutTache.EN_COURS, StatutTache.EN_ATTENTE);

    private final BoiteReceptionRepository boiteReceptionRepository;
    private final BoiteReceptionMapper boiteReceptionMapper;
    private final ProfilRepository profilRepository;
    private final UserRepository userRepository;
    private final TacheRepository tacheRepository;
    private final TacheMapper tacheMapper;
    private final RoutageBoiteReception routage;

    public BoiteReceptionServiceImpl(
        BoiteReceptionRepository boiteReceptionRepository,
        BoiteReceptionMapper boiteReceptionMapper,
        ProfilRepository profilRepository,
        UserRepository userRepository,
        TacheRepository tacheRepository,
        TacheMapper tacheMapper,
        RoutageBoiteReception routage
    ) {
        this.boiteReceptionRepository = boiteReceptionRepository;
        this.boiteReceptionMapper = boiteReceptionMapper;
        this.profilRepository = profilRepository;
        this.userRepository = userRepository;
        this.tacheRepository = tacheRepository;
        this.tacheMapper = tacheMapper;
        this.routage = routage;
    }

    @Override
    public int synchroniser() {
        List<Long> profilsSansBoite = boiteReceptionRepository.trouverProfilsSansBoite();
        for (Long profilId : profilsSansBoite) {
            profilRepository.findById(profilId).ifPresent(this::creerBoite);
        }
        if (!profilsSansBoite.isEmpty()) {
            LOG.info("Boites de reception creees pour {} profil(s) qui n'en avaient pas", profilsSansBoite.size());
        }

        // Le compteur stocke ne sert qu'a la console d'administration : les ecrans des
        // utilisateurs le recalculent a chaque lecture.
        for (BoiteReception boite : boiteReceptionRepository.findAllWithEagerRelationships()) {
            boite.setNombreNonLus((int) compterNonLues(boite.getProfil()));
        }
        return profilsSansBoite.size();
    }

    /**
     * Cree la boite d'un profil. Appelee a la creation d'un profil et par la synchronisation :
     * c'est le seul endroit qui cree une boite.
     */
    public BoiteReception creerBoite(Profil profil) {
        BoiteReception boite = new BoiteReception().profil(profil).dateCreation(Instant.now()).nombreNonLus(0).actif(Boolean.TRUE);
        LOG.debug("Creation automatique de la boite de reception du profil {}", profil.getNom());
        return boiteReceptionRepository.save(boite);
    }

    @Override
    public BoiteReceptionDTO maBoite() {
        return enrichir(boiteDuProfilCourant());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TacheDTO> mesTaches(Pageable pageable, boolean ouvertesSeulement) {
        Portee portee = routage.porteeDe(profilCourant());
        if (portee.rienAVoir()) {
            return Page.empty(pageable);
        }
        Page<Tache> page;
        if (portee.tout()) {
            page = ouvertesSeulement ? tacheRepository.trouverOuvertes(STATUTS_OUVERTS, pageable) : tacheRepository.trouverToutes(pageable);
        } else {
            page = ouvertesSeulement
                ? tacheRepository.trouverOuvertesPourDroits(portee.droits(), STATUTS_OUVERTS, pageable)
                : tacheRepository.trouverPourDroits(portee.droits(), pageable);
        }
        return page.map(tacheMapper::toDto);
    }

    @Override
    public BoiteReceptionDTO marquerConsultee() {
        BoiteReception boite = boiteDuProfilCourant();
        boite.setDateDerniereLecture(Instant.now());
        return enrichir(boite);
    }

    @Override
    public Optional<TacheDTO> marquerTacheLue(Long tacheId) {
        Portee portee = routage.porteeDe(profilCourant());
        return tacheRepository.findById(tacheId).map(tache -> {
            exigerAdresseeAMaBoite(portee, tache);
            tache.setLu(Boolean.TRUE);
            return tacheMapper.toDto(tacheRepository.save(tache));
        });
    }

    @Override
    public Optional<TacheDTO> prendreEnCharge(Long tacheId) {
        Portee portee = routage.porteeDe(profilCourant());
        return tacheRepository.findById(tacheId).map(tache -> {
            exigerAdresseeAMaBoite(portee, tache);
            User moi = utilisateurCourant();
            // Une tache deja prise par quelqu'un d'autre n'est pas reprise en silence : dans
            // une file partagee, deux personnes qui croient traiter le meme dossier en
            // traitent deux fois un et zero fois l'autre.
            if (tache.getUtilisateur() != null && !tache.getUtilisateur().getId().equals(moi.getId())) {
                throw new BadRequestAlertException(
                    "Cette tache est deja prise en charge par " + tache.getUtilisateur().getLogin(),
                    "tache",
                    "tache.dejaprise"
                );
            }
            tache.setUtilisateur(moi);
            tache.setDateAssignation(Instant.now());
            tache.setLu(Boolean.TRUE);
            if (tache.getStatut() == StatutTache.A_FAIRE) {
                tache.setStatut(StatutTache.EN_COURS);
            }
            return tacheMapper.toDto(tacheRepository.save(tache));
        });
    }

    @Override
    public Optional<TacheDTO> relacher(Long tacheId) {
        Portee portee = routage.porteeDe(profilCourant());
        return tacheRepository.findById(tacheId).map(tache -> {
            exigerAdresseeAMaBoite(portee, tache);
            tache.setUtilisateur(null);
            tache.setDateAssignation(null);
            if (tache.getStatut() == StatutTache.EN_COURS) {
                tache.setStatut(StatutTache.A_FAIRE);
            }
            return tacheMapper.toDto(tacheRepository.save(tache));
        });
    }

    /** Une tache adressee a un autre droit n'est pas consultable depuis sa propre boite. */
    private void exigerAdresseeAMaBoite(Portee portee, Tache tache) {
        if (!portee.tout() && !portee.droits().contains(tache.getDroitRequis())) {
            throw new AccessDeniedException("Cette tache n'est pas adressee a votre boite de reception");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoiteReceptionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all BoiteReceptions");
        return boiteReceptionRepository.findAllWithEagerRelationships(pageable).map(this::enrichirEnLecture);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BoiteReceptionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BoiteReceptionDTO> findOne(Long id) {
        LOG.debug("Request to get BoiteReception : {}", id);
        return boiteReceptionRepository.findOneWithEagerRelationships(id).map(this::enrichirEnLecture);
    }

    /**
     * La boite du profil de l'utilisateur courant, creee a la volee si elle manque : un
     * utilisateur ne doit jamais tomber sur une boite absente parce qu'un profil a ete ajoute
     * entre deux synchronisations.
     */
    private BoiteReception boiteDuProfilCourant() {
        Profil profil = profilCourant();
        return boiteReceptionRepository.trouverParProfil(profil.getId()).orElseGet(() -> creerBoite(profil));
    }

    /** Le profil de l'utilisateur courant, avec ses droits charges. */
    private Profil profilCourant() {
        User utilisateur = SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneWithAuthoritiesByLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
        Profil profil = utilisateur.getProfil();
        if (profil == null) {
            // Un utilisateur sans profil ne porte aucun droit : il ne peut donc voir aucune tache.
            throw new AccessDeniedException("Aucun profil n'est attribue a votre compte");
        }
        return profilRepository.findOneWithAuthoritiesById(profil.getId()).orElse(profil);
    }

    /** Recalcule les compteurs a partir des taches en portee et les reporte sur la boite. */
    private BoiteReceptionDTO enrichir(BoiteReception boite) {
        boite.setNombreNonLus((int) compterNonLues(boite.getProfil()));
        return enrichirEnLecture(boite);
    }

    /** Les memes compteurs, sans ecrire sur la boite : utilise par les lectures seules. */
    private BoiteReceptionDTO enrichirEnLecture(BoiteReception boite) {
        Portee portee = routage.porteeDe(boite.getProfil());
        BoiteReceptionDTO dto = boiteReceptionMapper.toDto(boite);
        if (portee.rienAVoir()) {
            dto.setNombreNonLus(0);
            dto.setNombreTaches(0L);
            dto.setNombreOuvertes(0L);
            return dto;
        }
        if (portee.tout()) {
            dto.setNombreNonLus((int) tacheRepository.countByLuFalse());
            dto.setNombreTaches(tacheRepository.count());
            dto.setNombreOuvertes(tacheRepository.countByStatutIn(STATUTS_OUVERTS));
        } else {
            dto.setNombreNonLus((int) tacheRepository.countByDroitRequisInAndLuFalse(portee.droits()));
            dto.setNombreTaches(tacheRepository.trouverPourDroits(portee.droits(), Pageable.ofSize(1)).getTotalElements());
            dto.setNombreOuvertes(tacheRepository.countByDroitRequisInAndStatutIn(portee.droits(), STATUTS_OUVERTS));
        }
        return dto;
    }

    private long compterNonLues(Profil profil) {
        Portee portee = routage.porteeDe(profil);
        if (portee.rienAVoir()) {
            return 0L;
        }
        return portee.tout() ? tacheRepository.countByLuFalse() : tacheRepository.countByDroitRequisInAndLuFalse(portee.droits());
    }

    /** L'utilisateur authentifie, pour l'attribution d'une tache. */
    private User utilisateurCourant() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current user could not be found"));
    }
}
