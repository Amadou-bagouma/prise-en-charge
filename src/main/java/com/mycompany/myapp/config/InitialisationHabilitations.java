package com.mycompany.myapp.config;

import com.mycompany.myapp.domain.Authority;
import com.mycompany.myapp.domain.Profil;
import com.mycompany.myapp.repository.AuthorityRepository;
import com.mycompany.myapp.repository.ProfilRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Cree au demarrage les habilitations de l'application et le profil qui les porte toutes.
 *
 * <p>Les habilitations vivaient jusqu'ici dans des changelogs Liquibase separes, sans texte
 * explicatif : l'ecran d'administration presentait une liste de {@code ROLE_*} que seul un
 * developpeur pouvait interpreter. Les rassembler ici avec leur description met la liste de
 * reference a un seul endroit, a cote de {@link AuthoritiesConstants} qui les nomme.
 *
 * <p>Le traitement est idempotent et n'enleve jamais rien : il cree ce qui manque et complete
 * les descriptions absentes. Une habilitation retiree du code n'est pas supprimee en base - elle
 * peut etre attribuee a des profils en production, et la faire disparaitre au redemarrage
 * retirerait des droits sans que personne l'ait decide.
 *
 * <p>Une erreur ici n'empeche pas l'application de demarrer : refuser le demarrage bloquerait
 * tout le monde pour un defaut qui ne concerne que le referentiel des droits.
 */
@Component
@Order(InitialisationHabilitations.ORDRE)
public class InitialisationHabilitations {

    /** Avant la synchronisation des boites : le profil cree ici doit recevoir la sienne. */
    public static final int ORDRE = 10;

    private static final Logger LOG = LoggerFactory.getLogger(InitialisationHabilitations.class);

    /** Le profil qui porte toutes les habilitations, cree et tenu a jour automatiquement. */
    public static final String PROFIL_SUPER_ADMIN = "Super administrateur";

    private static final String DESCRIPTION_SUPER_ADMIN =
        "Toutes les habilitations de l'application. Tenu a jour automatiquement au demarrage.";

    /**
     * Les habilitations de l'application et ce qu'elles autorisent, en francais administratif :
     * ce texte est lu par un gestionnaire au moment de composer un profil, pas par un
     * developpeur.
     *
     * <p>L'ordre est celui de l'ecran : du droit le plus large au plus specialise.
     */
    private static final Map<String, String> HABILITATIONS = new LinkedHashMap<>();

    static {
        HABILITATIONS.put(
            AuthoritiesConstants.ADMIN,
            "Administration complete : comptes, profils, referentiels, et acces a tous les dossiers."
        );
        HABILITATIONS.put(AuthoritiesConstants.USER, "Acces a l'application. Habilitation minimale, requise par tous les profils.");
        HABILITATIONS.put(
            AuthoritiesConstants.VERIFICATEUR_RH,
            "Controle d'une demande de prise en charge avant validation : completude du dossier et conformite des pieces."
        );
        HABILITATIONS.put(
            AuthoritiesConstants.VALIDATEUR_DRH,
            "Premiere validation d'une demande de prise en charge, au titre de la direction des ressources humaines."
        );
        HABILITATIONS.put(
            AuthoritiesConstants.VALIDATEUR_INFIRMERIE,
            "Seconde validation d'une demande de prise en charge, au titre de l'infirmerie du personnel."
        );
    }

    private final AuthorityRepository authorityRepository;

    private final ProfilRepository profilRepository;

    public InitialisationHabilitations(AuthorityRepository authorityRepository, ProfilRepository profilRepository) {
        this.authorityRepository = authorityRepository;
        this.profilRepository = profilRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initialiser() {
        try {
            Set<Authority> habilitations = creerLesHabilitationsManquantes();
            creerOuCompleterLeProfilSuperAdmin(habilitations);
        } catch (RuntimeException e) {
            LOG.error("Le referentiel des habilitations n'a pas pu etre initialise au demarrage", e);
        }
    }

    /**
     * Cree les habilitations absentes et renseigne les descriptions vides.
     *
     * <p>Une description deja saisie n'est pas ecrasee : elle a pu etre precisee en base pour
     * coller au vocabulaire de l'institution, et un redemarrage ne doit pas defaire cela.
     */
    private Set<Authority> creerLesHabilitationsManquantes() {
        Set<Authority> toutes = new LinkedHashSet<>();
        int creees = 0;
        int completees = 0;

        for (Map.Entry<String, String> entree : HABILITATIONS.entrySet()) {
            Authority habilitation = authorityRepository.findById(entree.getKey()).orElse(null);
            if (habilitation == null) {
                habilitation = new Authority();
                habilitation.setName(entree.getKey());
                habilitation.setDescription(entree.getValue());
                habilitation = authorityRepository.save(habilitation);
                creees++;
            } else if (habilitation.getDescription() == null || habilitation.getDescription().isBlank()) {
                habilitation.setDescription(entree.getValue());
                habilitation = authorityRepository.save(habilitation);
                completees++;
            }
            toutes.add(habilitation);
        }

        if (creees > 0 || completees > 0) {
            LOG.info("Habilitations : {} creee(s), {} description(s) completee(s)", creees, completees);
        }
        return toutes;
    }

    /**
     * Cree le profil super administrateur, ou lui ajoute les habilitations qui lui manquent.
     *
     * <p>Les habilitations deja presentes ne sont jamais retirees : ce profil peut avoir ete
     * complete a la main, et le redemarrage n'a pas a arbitrer cela.
     */
    private void creerOuCompleterLeProfilSuperAdmin(Set<Authority> habilitations) {
        Profil profil = profilRepository.findOneWithAuthoritiesByNom(PROFIL_SUPER_ADMIN).orElse(null);
        if (profil == null) {
            profil = new Profil();
            profil.setNom(PROFIL_SUPER_ADMIN);
            profil.setDescription(DESCRIPTION_SUPER_ADMIN);
            profil.setAuthorities(new LinkedHashSet<>(habilitations));
            profilRepository.save(profil);
            LOG.info("Profil « {} » cree avec {} habilitation(s)", PROFIL_SUPER_ADMIN, habilitations.size());
            return;
        }

        Set<Authority> ajoutees = new LinkedHashSet<>(habilitations);
        ajoutees.removeAll(profil.getAuthorities());
        if (!ajoutees.isEmpty()) {
            profil.getAuthorities().addAll(ajoutees);
            profilRepository.save(profil);
            LOG.info("Profil « {} » complete de {} habilitation(s)", PROFIL_SUPER_ADMIN, ajoutees.size());
        }
    }
}
