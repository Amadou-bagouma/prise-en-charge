package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.CarteBeneficiaire;
import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.domain.enumeration.StatutValidationAyantDroit;
import com.mycompany.myapp.domain.enumeration.TypeBeneficiaire;
import com.mycompany.myapp.repository.AgentRepository;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.repository.CarteBeneficiaireRepository;
import com.mycompany.myapp.service.CarteBeneficiaireService;
import com.mycompany.myapp.service.JournalActions;
import com.mycompany.myapp.service.Parametres;
import com.mycompany.myapp.service.dto.CarteBeneficiaireDTO;
import com.mycompany.myapp.service.mapper.CarteBeneficiaireMapper;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.CarteBeneficiaire}.
 */
@Service
@Transactional
public class CarteBeneficiaireServiceImpl implements CarteBeneficiaireService {

    private static final Logger LOG = LoggerFactory.getLogger(CarteBeneficiaireServiceImpl.class);

    private final CarteBeneficiaireRepository carteBeneficiaireRepository;

    private final CarteBeneficiaireMapper carteBeneficiaireMapper;

    private final AgentRepository agentRepository;

    private final AyantDroitRepository ayantDroitRepository;

    private final JournalActions journal;

    private final Parametres parametres;

    private static final String ENTITY_NAME = "carteBeneficiaire";

    public CarteBeneficiaireServiceImpl(
        CarteBeneficiaireRepository carteBeneficiaireRepository,
        CarteBeneficiaireMapper carteBeneficiaireMapper,
        AgentRepository agentRepository,
        AyantDroitRepository ayantDroitRepository,
        JournalActions journal,
        Parametres parametres
    ) {
        this.carteBeneficiaireRepository = carteBeneficiaireRepository;
        this.carteBeneficiaireMapper = carteBeneficiaireMapper;
        this.agentRepository = agentRepository;
        this.ayantDroitRepository = ayantDroitRepository;
        this.journal = journal;
        this.parametres = parametres;
    }

    /**
     * Duree de validite d'une carte, en annees.
     *
     * <p>Trois ans : assez pour ne pas refaire le geste chaque annee, assez court pour que la
     * photo et la situation du titulaire restent a peu pres justes.
     */
    public static final int VALIDITE_ANNEES_PAR_DEFAUT = 3;

    /** La duree en vigueur, lue au referentiel : c'est une decision de service, revisable. */
    private int validiteAnnees() {
        return parametres.entier(Parametres.VALIDITE_CARTE_ANNEES, VALIDITE_ANNEES_PAR_DEFAUT);
    }

    /**
     * Le rang de l'ayant droit suit le matricule sans separateur : 1234201. Les matricules
     * ont tous quatre chiffres, la frontiere entre les deux ne peut donc pas preter a confusion.
     */
    private static final String SEPARATEUR_RANG = "";

    @Override
    public CarteBeneficiaireDTO generer(TypeBeneficiaire typeBeneficiaire, Long beneficiaireId) {
        LOG.debug("Request to generate CarteBeneficiaire for {} {}", typeBeneficiaire, beneficiaireId);
        if (typeBeneficiaire == null || beneficiaireId == null) {
            throw new BadRequestAlertException("Le beneficiaire est obligatoire", ENTITY_NAME, "beneficiaire.obligatoire");
        }

        CarteBeneficiaire carte = new CarteBeneficiaire();
        carte.setTypeBeneficiaire(typeBeneficiaire);
        if (typeBeneficiaire == TypeBeneficiaire.AGENT) {
            Agent agent = agentRepository
                .findById(beneficiaireId)
                .orElseThrow(() -> new BadRequestAlertException("Cet agent n'existe pas", ENTITY_NAME, "agent.introuvable"));
            // Les cartes de ses ayants droit sont aussi rattachees a l'agent : seules les siennes comptent.
            exigerAucuneCarteValide(
                carteBeneficiaireRepository
                    .findByAgentIdOrderByDateFinValiditeDesc(beneficiaireId)
                    .stream()
                    .filter(existante -> existante.getAyantDroit() == null)
                    .toList()
            );
            carte.setAgent(agent);
        } else {
            AyantDroit ayantDroit = ayantDroitRepository
                .findById(beneficiaireId)
                .orElseThrow(() -> new BadRequestAlertException("Cet ayant droit n'existe pas", ENTITY_NAME, "ayantdroit.introuvable"));
            // Un rattachement non verifie ne fonde aucun droit : lui etablir une carte
            // reviendrait a attester d'une couverture que personne n'a encore controlee.
            if (ayantDroit.getStatutValidation() != StatutValidationAyantDroit.VALIDE) {
                throw new BadRequestAlertException(
                    "Le rattachement de cet ayant droit n'a pas ete verifie",
                    ENTITY_NAME,
                    "ayantdroit.nonvalide"
                );
            }
            exigerAucuneCarteValide(carteBeneficiaireRepository.findByAyantDroitIdOrderByDateFinValiditeDesc(beneficiaireId));
            carte.setAyantDroit(ayantDroit);
            carte.setAgent(ayantDroit.getAgent());
        }

        LocalDate aujourdhui = LocalDate.now(ZoneId.systemDefault());
        carte.setNumeroCarte(genererNumero(carte));
        carte.setDateEmission(aujourdhui);
        carte.setDateDebutValidite(aujourdhui);
        carte.setDateFinValidite(aujourdhui.plusYears(validiteAnnees()));
        return carteBeneficiaireMapper.toDto(carteBeneficiaireRepository.save(carte));
    }

    @Override
    public CarteBeneficiaireDTO dupliquer(Long carteId, String motif) {
        LOG.debug("Request to duplicate CarteBeneficiaire {}", carteId);
        if (motif == null || motif.isBlank()) {
            // Le motif est exige : « duplicata » sans raison ne se distingue pas d'une erreur de
            // saisie, et c'est la seule trace de ce qui est arrive a la carte precedente.
            throw new BadRequestAlertException("Le motif est obligatoire", ENTITY_NAME, "duplicata.motif");
        }
        CarteBeneficiaire perdue = carteBeneficiaireRepository
            .findById(carteId)
            .orElseThrow(() -> new BadRequestAlertException("Cette carte n'existe pas", ENTITY_NAME, "idnotfound"));

        LocalDate aujourdhui = LocalDate.now(ZoneId.systemDefault());
        LocalDate terme = perdue.getDateFinValidite();
        if (terme != null && terme.isBefore(aujourdhui)) {
            throw new BadRequestAlertException(
                "Cette carte n'est plus valable : etablissez-en une nouvelle plutot qu'un duplicata",
                ENTITY_NAME,
                "duplicata.expiree"
            );
        }

        // La carte perdue cesse d'etre valable aujourd'hui. Elle n'est pas supprimee : elle a pu
        // servir, et son numero doit rester reconnaissable si elle reapparait.
        perdue.setDateFinValidite(aujourdhui);
        carteBeneficiaireRepository.save(perdue);

        CarteBeneficiaire duplicata = new CarteBeneficiaire();
        duplicata.setTypeBeneficiaire(perdue.getTypeBeneficiaire());
        duplicata.setAgent(perdue.getAgent());
        duplicata.setAyantDroit(perdue.getAyantDroit());
        // Meme titulaire, meme numero : seule la periode de validite distingue le duplicata.
        duplicata.setNumeroCarte(perdue.getNumeroCarte());
        duplicata.setDateEmission(aujourdhui);
        duplicata.setDateDebutValidite(aujourdhui);
        duplicata.setDateFinValidite(terme);
        duplicata = carteBeneficiaireRepository.save(duplicata);

        boolean surAyantDroit = perdue.getAyantDroit() != null;
        journal.surBeneficiaire(
            surAyantDroit ? JournalActions.CIBLE_AYANT_DROIT : JournalActions.CIBLE_AGENT,
            surAyantDroit ? perdue.getAyantDroit().getId() : perdue.getAgent() == null ? null : perdue.getAgent().getId(),
            "DUPLICATA_CARTE",
            "Carte %s remplacee par %s. Motif : %s".formatted(perdue.getNumeroCarte(), duplicata.getNumeroCarte(), motif)
        );
        return carteBeneficiaireMapper.toDto(duplicata);
    }

    /**
     * Refuse d'etablir une seconde carte tant que la precedente court.
     *
     * <p>Deux cartes valides pour la meme personne, c'est un doublon qui circule : celle qu'on
     * presente au guichet n'est plus forcement celle que l'application connait. Une carte
     * perdue se remplace en abregeant la premiere, geste qui laisse une trace.
     */
    private void exigerAucuneCarteValide(List<CarteBeneficiaire> existantes) {
        LocalDate aujourdhui = LocalDate.now(ZoneId.systemDefault());
        existantes
            .stream()
            .filter(carte -> carte.getDateFinValidite() != null && !carte.getDateFinValidite().isBefore(aujourdhui))
            .findFirst()
            .ifPresent(carte -> {
                throw new BadRequestAlertException(
                    "Une carte valide existe deja : " + carte.getNumeroCarte() + ", jusqu'au " + carte.getDateFinValidite(),
                    ENTITY_NAME,
                    "carte.dejavalide"
                );
            });
    }

    /**
     * Le numero : le matricule de l'agent, suivi pour un ayant droit d'un rang dans sa famille.
     *
     * <p>Agent : 1234. Conjoints : 1234201, 1234202... Enfants, adoptes compris : 1234301, 1234302...
     * Autres ayants droit : 1234401... Un ayant droit qui a deja eu une carte garde son
     * numero ; un nouveau prend le rang suivant le plus haut deja attribue, pour qu'un numero
     * ne passe jamais d'une personne a une autre.
     */
    private String genererNumero(CarteBeneficiaire carte) {
        Agent agent = carte.getAgent();
        String matricule = agent == null ? null : agent.getMatricule();
        if (matricule == null || matricule.isBlank()) {
            throw new BadRequestAlertException("L'agent n'a pas de matricule", ENTITY_NAME, "agent.matricule");
        }
        matricule = matricule.trim();
        AyantDroit ayantDroit = carte.getAyantDroit();
        if (ayantDroit == null) {
            return matricule;
        }

        Optional<String> numeroAttribue = carteBeneficiaireRepository
            .findByAyantDroitIdOrderByDateFinValiditeDesc(ayantDroit.getId())
            .stream()
            .map(CarteBeneficiaire::getNumeroCarte)
            .filter(numero -> numero != null && !numero.isBlank())
            .findFirst();
        if (numeroAttribue.isPresent()) {
            return numeroAttribue.orElseThrow();
        }

        LienParente lien = ayantDroit.getLien();
        int base = lien == LienParente.CONJOINT ? 200 : lien == LienParente.ENFANT || lien == LienParente.ADOPTE ? 300 : 400;
        String prefixe = matricule + SEPARATEUR_RANG;
        int plusHaut = carteBeneficiaireRepository
            .findNumerosCommencantPar(prefixe)
            .stream()
            .map(numero -> numero.substring(prefixe.length()))
            .filter(rang -> rang.matches("\\d{3}"))
            .mapToInt(Integer::parseInt)
            .filter(rang -> rang > base && rang < base + 100)
            .max()
            .orElse(base);
        if (plusHaut >= base + 99) {
            throw new BadRequestAlertException("Plus aucun rang libre pour cet ayant droit", ENTITY_NAME, "carte.rang");
        }
        return prefixe + (plusHaut + 1);
    }

    @Override
    public CarteBeneficiaireDTO save(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to save CarteBeneficiaire : {}", carteBeneficiaireDTO);
        CarteBeneficiaire carteBeneficiaire = carteBeneficiaireMapper.toEntity(carteBeneficiaireDTO);
        carteBeneficiaire = carteBeneficiaireRepository.save(carteBeneficiaire);
        return carteBeneficiaireMapper.toDto(carteBeneficiaire);
    }

    @Override
    public CarteBeneficiaireDTO update(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to update CarteBeneficiaire : {}", carteBeneficiaireDTO);
        CarteBeneficiaire carteBeneficiaire = carteBeneficiaireMapper.toEntity(carteBeneficiaireDTO);
        carteBeneficiaire = carteBeneficiaireRepository.save(carteBeneficiaire);
        return carteBeneficiaireMapper.toDto(carteBeneficiaire);
    }

    @Override
    public Optional<CarteBeneficiaireDTO> partialUpdate(CarteBeneficiaireDTO carteBeneficiaireDTO) {
        LOG.debug("Request to partially update CarteBeneficiaire : {}", carteBeneficiaireDTO);

        return carteBeneficiaireRepository
            .findById(carteBeneficiaireDTO.getId())
            .map(existingCarteBeneficiaire -> {
                carteBeneficiaireMapper.partialUpdate(existingCarteBeneficiaire, carteBeneficiaireDTO);

                return existingCarteBeneficiaire;
            })
            .map(carteBeneficiaireRepository::save)
            .map(carteBeneficiaireMapper::toDto);
    }

    public Page<CarteBeneficiaireDTO> findAllWithEagerRelationships(Pageable pageable) {
        return carteBeneficiaireRepository.findAllWithEagerRelationships(pageable).map(carteBeneficiaireMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarteBeneficiaireDTO> findOne(Long id) {
        LOG.debug("Request to get CarteBeneficiaire : {}", id);
        return carteBeneficiaireRepository.findOneWithEagerRelationships(id).map(carteBeneficiaireMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete CarteBeneficiaire : {}", id);
        carteBeneficiaireRepository.deleteById(id);
    }
}
