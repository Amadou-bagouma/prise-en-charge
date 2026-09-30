package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.PieceJustificative;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.PieceJustificativeRepository;
import com.mycompany.myapp.service.Parametres;
import com.mycompany.myapp.service.PieceJustificativeService;
import com.mycompany.myapp.service.dto.PieceJustificativeDTO;
import com.mycompany.myapp.service.mapper.PieceJustificativeMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.PieceJustificative}.
 */
@Service
@Transactional
public class PieceJustificativeServiceImpl implements PieceJustificativeService {

    private static final Logger LOG = LoggerFactory.getLogger(PieceJustificativeServiceImpl.class);

    private final PieceJustificativeRepository pieceJustificativeRepository;

    private final PieceJustificativeMapper pieceJustificativeMapper;

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final Parametres parametres;

    private static final String ENTITY_NAME = "pieceJustificative";

    /**
     * Les etapes ou les pieces d'un dossier peuvent encore changer.
     *
     * <p>Au-dela, le dossier est soumis a l'examen de quelqu'un : en modifier les pieces
     * reviendrait a changer sous ses yeux ce sur quoi il se prononce.
     */
    private static final List<StatutDemande> STATUTS_MODIFIABLES = List.of(StatutDemande.EN_SAISIE, StatutDemande.NOUVELLE);

    /**
     * Les types de fichier acceptes comme piece justificative.
     *
     * <p>Une liste fermee, et non une liste d'extensions interdites : ce qui n'est pas
     * explicitement prevu est refuse, plutot que de laisser passer tout ce a quoi on n'a pas
     * pense. Les formats retenus sont ceux qu'un guichet recoit reellement - une photographie
     * d'ordonnance, un PDF scanne, un document bureautique.
     */
    private static final Set<String> TYPES_ACCEPTES = Set.of(
        "image/jpeg",
        "image/png",
        "image/webp",
        "image/heic",
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /** Taille maximale d'une piece. Au-dela, c'est un scan a reprendre, pas une piece a joindre. */
    private static final long TAILLE_MAX_MO_PAR_DEFAUT = 10L;

    /** La taille maximale en vigueur, lue au referentiel. */
    private long tailleMaxOctets() {
        return parametres.entier(Parametres.TAILLE_MAX_PIECE_MO, (int) TAILLE_MAX_MO_PAR_DEFAUT) * 1024L * 1024L;
    }

    /**
     * Refuse un fichier dont le type n'est pas prevu, ou trop lourd.
     *
     * <p>Le controle est ici et pas seulement dans le navigateur : l'attribut {@code accept}
     * d'un champ de fichier est une commodite d'affichage, il n'empeche rien.
     */
    private void exigerFichierRecevable(PieceJustificativeDTO piece) {
        if (piece.getContenu() == null || piece.getContenu().length == 0) {
            return;
        }
        String type = piece.getContenuContentType();
        if (type == null || !TYPES_ACCEPTES.contains(type.toLowerCase(Locale.ROOT))) {
            throw new BadRequestAlertException(
                "Ce type de fichier n'est pas accepte (%s). Formats admis : image, PDF ou document Word.".formatted(
                    type == null ? "type inconnu" : type
                ),
                ENTITY_NAME,
                "piece.typerefuse"
            );
        }
        if (piece.getContenu().length > tailleMaxOctets()) {
            throw new BadRequestAlertException(
                "Ce fichier depasse la taille maximale de %d Mo.".formatted(tailleMaxOctets() / (1024 * 1024)),
                ENTITY_NAME,
                "piece.troplourd"
            );
        }
        // La taille est calculee ici et non prise du client : elle sert a afficher les listes
        // sans rapatrier les contenus, et une valeur annoncee ne vaut rien.
        piece.setTailleFichier((long) piece.getContenu().length);
    }

    public PieceJustificativeServiceImpl(
        PieceJustificativeRepository pieceJustificativeRepository,
        PieceJustificativeMapper pieceJustificativeMapper,
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        Parametres parametres
    ) {
        this.parametres = parametres;
        this.pieceJustificativeRepository = pieceJustificativeRepository;
        this.pieceJustificativeMapper = pieceJustificativeMapper;
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
    }

    /**
     * Refuse de toucher aux pieces d'un dossier qui n'est plus en saisie.
     *
     * <p>Le dossier est relu en base : le client envoie l'objet complet, et rien ne l'empecherait
     * d'y declarer un statut complaisant.
     */
    private void exigerDossierEnSaisie(Long demandeId) {
        if (demandeId == null) {
            throw new BadRequestAlertException("Une piece doit etre rattachee a un dossier", ENTITY_NAME, "demande.requise");
        }
        DemandePriseEnCharge demande = demandePriseEnChargeRepository
            .findById(demandeId)
            .orElseThrow(() -> new BadRequestAlertException("Dossier introuvable", ENTITY_NAME, "demande.introuvable"));
        if (!STATUTS_MODIFIABLES.contains(demande.getStatut())) {
            throw new BadRequestAlertException(
                "Le dossier %s n'est plus en saisie (%s) : ses pieces ne peuvent plus etre modifiees.".formatted(
                    demande.getReference(),
                    demande.getStatut()
                ),
                ENTITY_NAME,
                "piece.dossierverrouille"
            );
        }
    }

    @Override
    public PieceJustificativeDTO save(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to save PieceJustificative : {}", pieceJustificativeDTO);
        exigerDossierEnSaisie(pieceJustificativeDTO.getDemande() == null ? null : pieceJustificativeDTO.getDemande().getId());
        exigerFichierRecevable(pieceJustificativeDTO);
        PieceJustificative pieceJustificative = pieceJustificativeMapper.toEntity(pieceJustificativeDTO);
        pieceJustificative = pieceJustificativeRepository.save(pieceJustificative);
        return pieceJustificativeMapper.toDto(pieceJustificative);
    }

    @Override
    public PieceJustificativeDTO update(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to update PieceJustificative : {}", pieceJustificativeDTO);
        // Le dossier d'origine est verifie, pas seulement celui que le client annonce : sinon il
        // suffirait de rattacher la piece a un autre dossier pour contourner le verrou.
        pieceJustificativeRepository
            .findById(pieceJustificativeDTO.getId())
            .ifPresent(existante -> exigerDossierEnSaisie(existante.getDemande() == null ? null : existante.getDemande().getId()));
        exigerDossierEnSaisie(pieceJustificativeDTO.getDemande() == null ? null : pieceJustificativeDTO.getDemande().getId());
        exigerFichierRecevable(pieceJustificativeDTO);
        PieceJustificative pieceJustificative = pieceJustificativeMapper.toEntity(pieceJustificativeDTO);
        pieceJustificative = pieceJustificativeRepository.save(pieceJustificative);
        return pieceJustificativeMapper.toDto(pieceJustificative);
    }

    @Override
    public Optional<PieceJustificativeDTO> partialUpdate(PieceJustificativeDTO pieceJustificativeDTO) {
        LOG.debug("Request to partially update PieceJustificative : {}", pieceJustificativeDTO);

        return pieceJustificativeRepository
            .findById(pieceJustificativeDTO.getId())
            .map(existingPieceJustificative -> {
                exigerDossierEnSaisie(
                    existingPieceJustificative.getDemande() == null ? null : existingPieceJustificative.getDemande().getId()
                );
                pieceJustificativeMapper.partialUpdate(existingPieceJustificative, pieceJustificativeDTO);

                return existingPieceJustificative;
            })
            .map(pieceJustificativeRepository::save)
            .map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PieceJustificativeDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all PieceJustificatives");
        return pieceJustificativeRepository.findAll(pageable).map(pieceJustificativeMapper::toDto);
    }

    public Page<PieceJustificativeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pieceJustificativeRepository.findAllWithEagerRelationships(pageable).map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PieceJustificativeDTO> findAllByDemande(Long demandeId, Pageable pageable) {
        LOG.debug("Request to get PieceJustificatives of demande : {}", demandeId);
        return pieceJustificativeRepository.findAllByDemandeId(demandeId, pageable).map(pieceJustificativeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PieceJustificativeDTO> findOne(Long id) {
        LOG.debug("Request to get PieceJustificative : {}", id);
        return pieceJustificativeRepository.findOneWithEagerRelationships(id).map(pieceJustificativeMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PieceJustificative : {}", id);
        // Retirer une piece d'un dossier deja soumis reviendrait a retirer au controleur une
        // partie de ce sur quoi il se prononce, sans qu'il en soit averti.
        pieceJustificativeRepository
            .findById(id)
            .ifPresent(piece -> exigerDossierEnSaisie(piece.getDemande() == null ? null : piece.getDemande().getId()));
        pieceJustificativeRepository.deleteById(id);
    }
}
