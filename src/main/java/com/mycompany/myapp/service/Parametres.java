package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Parametre;
import com.mycompany.myapp.repository.ParametreRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lit les reglages de l'application.
 *
 * <p>Chaque lecture prend une valeur par defaut. Ce n'est pas une precaution de style : un
 * reglage supprime a la main, ou pas encore pose sur une base ancienne, ne doit pas empecher
 * d'ouvrir un dossier. L'application continue sur la valeur d'origine, et le journal le dit.
 *
 * <p>Aucune mise en cache : ces lectures sont rares - une par ouverture de dossier, une par
 * carte etablie - et un cache ferait trainer l'ancienne valeur apres un changement, ce qui est
 * exactement ce qu'on cherche a eviter en rendant le reglage modifiable.
 */
@Service
@Transactional(readOnly = true)
public class Parametres {

    private static final Logger LOG = LoggerFactory.getLogger(Parametres.class);

    // ----------------------------------------------------------------- codes
    /** Duree de validite d'une prise en charge, en jours. */
    public static final String VALIDITE_PRISE_EN_CHARGE_JOURS = "VALIDITE_PRISE_EN_CHARGE_JOURS";

    /** Duree de validite d'une carte de beneficiaire, en annees. */
    public static final String VALIDITE_CARTE_ANNEES = "VALIDITE_CARTE_ANNEES";

    /** Delai avant echeance a partir duquel une carte est signalee comme a renouveler, en jours. */
    public static final String ALERTE_CARTE_JOURS = "ALERTE_CARTE_JOURS";

    /** Taille maximale d'une piece justificative deposee, en mega-octets. */
    public static final String TAILLE_MAX_PIECE_MO = "TAILLE_MAX_PIECE_MO";

    /** Nom du directeur general, imprime sous sa signature. */
    public static final String NOM_DIRECTEUR_GENERAL = "NOM_DIRECTEUR_GENERAL";

    /** Signature du directeur general, apposee sur les cartes et les imprimes. */
    public static final String SIGNATURE_DIRECTEUR_GENERAL = "SIGNATURE_DIRECTEUR_GENERAL";

    /** Nom de l'institution, tel qu'il s'imprime. */
    public static final String NOM_INSTITUTION = "NOM_INSTITUTION";

    /** Pays de l'institution, tel qu'il s'imprime. */
    public static final String PAYS_INSTITUTION = "PAYS_INSTITUTION";

    private final ParametreRepository parametreRepository;

    public Parametres(ParametreRepository parametreRepository) {
        this.parametreRepository = parametreRepository;
    }

    /**
     * La valeur entiere du reglage, ou {@code defaut} s'il manque ou ne se lit pas.
     *
     * <p>Une valeur illisible ne fait pas echouer l'appel : elle est signalee au journal et
     * ignoree. Refuser d'ouvrir un dossier parce qu'un delai a ete mal saisi punirait l'agent
     * pour une erreur d'administration.
     */
    public int entier(String code, int defaut) {
        return texteBrut(code)
            .map(valeur -> {
                try {
                    return Integer.parseInt(valeur.trim());
                } catch (NumberFormatException e) {
                    LOG.warn("Le parametre {} ne se lit pas comme un entier ({}) : {} est utilise", code, valeur, defaut);
                    return defaut;
                }
            })
            .orElse(defaut);
    }

    /** La valeur textuelle du reglage, ou {@code defaut} s'il manque ou est vide. */
    public String texte(String code, String defaut) {
        return texteBrut(code).orElse(defaut);
    }

    /** Vrai, faux, ou {@code defaut} si le reglage manque. */
    public boolean booleen(String code, boolean defaut) {
        return texteBrut(code)
            .map(valeur -> "true".equalsIgnoreCase(valeur.trim()) || "oui".equalsIgnoreCase(valeur.trim()))
            .orElse(defaut);
    }

    /** L'image du reglage, si elle a ete deposee. */
    public Optional<Parametre> image(String code) {
        return parametreRepository.findOneByCode(code).filter(parametre -> parametre.getValeurBinaire() != null);
    }

    private Optional<String> texteBrut(String code) {
        return parametreRepository
            .findOneByCode(code)
            .map(Parametre::getValeur)
            .filter(valeur -> !valeur.isBlank());
    }
}
