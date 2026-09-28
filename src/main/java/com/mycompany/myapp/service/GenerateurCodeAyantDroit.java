package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.repository.AyantDroitRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Attribue le code d'identification d'un ayant droit.
 *
 * <p>Le code fait sept caracteres : les quatre du matricule de l'agent, un chiffre disant la
 * nature du lien, puis une sequence sur deux chiffres. Ainsi 1234201 se lit « premier conjoint
 * de l'agent 1234 », et 1234302 « deuxieme enfant ou proche du meme agent ».
 *
 * <p>Enfants et « autres » partagent le chiffre 3, donc la meme serie : deux personnes rattachees
 * au meme agent ne peuvent pas recevoir le meme numero sous pretexte qu'elles n'ont pas le meme
 * lien. C'est voulu, et c'est ce qui rend le code utilisable comme identifiant.
 *
 * <p>L'attribution se fait au serveur, jamais au client : le code porte une contrainte d'unicite,
 * et deux saisies simultanees se termineraient autrement par un echec cote agent.
 */
@Service
@Transactional(readOnly = true)
public class GenerateurCodeAyantDroit {

    private static final Logger LOG = LoggerFactory.getLogger(GenerateurCodeAyantDroit.class);

    private static final String ENTITY_NAME = "ayantDroit";

    /** Le matricule occupe les quatre premiers caracteres du code. */
    public static final int LONGUEUR_MATRICULE = 4;

    /** Deux chiffres de sequence, soit 99 ayants droit par agent et par famille de lien. */
    private static final int SEQUENCE_MAX = 99;

    private final AyantDroitRepository ayantDroitRepository;

    public GenerateurCodeAyantDroit(AyantDroitRepository ayantDroitRepository) {
        this.ayantDroitRepository = ayantDroitRepository;
    }

    /**
     * Genere le prochain code libre pour un agent et un lien de parente.
     *
     * @param matricule le matricule de l'agent, sur quatre caracteres.
     * @param lien le lien de parente, qui determine le chiffre de type.
     * @return le code sur sept caracteres.
     */
    public String genererPour(String matricule, LienParente lien) {
        String prefixe = prefixe(matricule, lien);
        int suivant = prochaineSequence(prefixe);
        String code = "%s%02d".formatted(prefixe, suivant);
        LOG.debug("Code genere pour le matricule {} et le lien {} : {}", matricule, lien, code);
        return code;
    }

    /** Matricule + chiffre de type : ce qui identifie une serie. */
    private String prefixe(String matricule, LienParente lien) {
        if (matricule == null || matricule.strip().length() != LONGUEUR_MATRICULE) {
            throw new BadRequestAlertException(
                "Le matricule de l'agent doit faire exactement " +
                    LONGUEUR_MATRICULE +
                    " caracteres pour qu'un code d'ayant droit puisse etre attribue.",
                ENTITY_NAME,
                "matricule.format"
            );
        }
        return matricule.strip().toUpperCase() + chiffreDuLien(lien);
    }

    /**
     * Le chiffre qui dit la nature du lien.
     *
     * <p>2 pour le conjoint, 3 pour les enfants et les autres proches. Le {@code switch} est
     * exhaustif sur l'enumeration : ajouter un lien sans decider de son chiffre ne compilera pas,
     * ce qui vaut mieux qu'un code attribue au hasard.
     */
    private char chiffreDuLien(LienParente lien) {
        if (lien == null) {
            throw new BadRequestAlertException(
                "Le lien de parente est necessaire pour attribuer un code.",
                ENTITY_NAME,
                "lien.obligatoire"
            );
        }
        return switch (lien) {
            case CONJOINT -> '2';
            case ENFANT, AUTRE -> '3';
        };
    }

    private int prochaineSequence(String prefixe) {
        int dernier = ayantDroitRepository
            .trouverDernierCode(prefixe)
            .map(code -> code.substring(prefixe.length()))
            .map(this::enNombre)
            .orElse(0);
        if (dernier >= SEQUENCE_MAX) {
            throw new BadRequestAlertException(
                "Cet agent a atteint le maximum de " + SEQUENCE_MAX + " ayants droit pour ce type de lien.",
                ENTITY_NAME,
                "code.sequenceepuisee"
            );
        }
        return dernier + 1;
    }

    /**
     * Un code dont la fin n'est pas numerique ne vient pas de ce generateur : il a ete saisi a la
     * main ou repris d'une migration. On l'ignore pour le calcul de la sequence plutot que
     * d'echouer, la contrainte d'unicite restant le garde-fou final.
     */
    private int enNombre(String sequence) {
        try {
            return Integer.parseInt(sequence);
        } catch (NumberFormatException e) {
            LOG.warn("Code d'ayant droit hors format ignore pour le calcul de sequence : suffixe '{}'", sequence);
            return 0;
        }
    }
}
