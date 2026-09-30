package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.ActionsConstants;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ce qu'un agent voit de l'application : son propre dossier, et rien d'autre.
 *
 * <p>Un agent connecte a son espace n'instruit pas les dossiers des autres : il consulte les
 * siens, ceux de ses ayants droit, et sa carte. Les ecrans sont les memes que ceux du personnel
 * administratif - refaire une liste d'ayants droit pour lui seul voudrait dire la maintenir deux
 * fois - mais les donnees sont bornees a son perimetre avant d'etre rendues.
 *
 * <p>Le bornage est fait au serveur et non a l'ecran. Retirer un bouton n'empeche personne
 * d'appeler un point d'entree directement, et un agent qui verrait la liste complete des agents
 * de l'institution y lirait des situations - suspension, deces - qui ne le regardent pas.
 *
 * <p>Le perimetre ne s'applique qu'a qui ne porte que l'habilitation d'espace personnel : le
 * personnel administratif, qui porte les habilitations de consultation, n'est pas borne.
 */
@Service
@Transactional(readOnly = true)
public class PerimetreAgent {

    private final UserRepository userRepository;

    public PerimetreAgent(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Un identifiant qui ne designe aucun agent.
     *
     * <p>Sert de valeur de filtre pour un compte borne qui n'est rattache a personne : sans lui,
     * ce compte echapperait au bornage et verrait tout. Rendre une liste vide dit la verite -
     * son espace ne contient rien - la ou un refus laisserait croire a une panne.
     */
    private static final long AUCUN_AGENT = -1L;

    /**
     * L'agent auquel borner la reponse, present des que le compte courant est borne.
     *
     * <p>Vide seulement pour le personnel administratif, qui instruit les dossiers de tous. Un
     * compte borne rend toujours une valeur, {@link #AUCUN_AGENT} s'il n'est rattache a aucun
     * agent : l'appelant applique donc le filtre sans avoir a distinguer les deux cas, et ne peut
     * pas l'oublier.
     */
    public Optional<Long> agentDuCompteCourant() {
        if (!estBorne()) {
            return Optional.empty();
        }
        return Optional.of(
            SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .map(User::getAgent)
                .map(Agent::getId)
                .orElse(AUCUN_AGENT)
        );
    }

    /**
     * Vrai quand le compte courant n'a acces qu'a son propre espace.
     *
     * <p>Porter l'habilitation d'espace personnel ne suffit pas : un administrateur pourrait la
     * recevoir sans cesser d'etre administrateur. C'est l'absence des habilitations de
     * consultation d'ensemble qui borne.
     */
    public boolean estBorne() {
        if (!SecurityUtils.hasCurrentUserThisAuthority(ActionsConstants.ESPACE_AGENT)) {
            return false;
        }
        return !SecurityUtils.hasCurrentUserAnyOfAuthorities(
            AuthoritiesConstants.ADMIN,
            AuthoritiesConstants.VERIFICATEUR_RH,
            AuthoritiesConstants.VALIDATEUR_DRH,
            AuthoritiesConstants.VALIDATEUR_INFIRMERIE
        );
    }

    /**
     * Refuse l'acces a ce qui ne releve pas du perimetre du compte courant.
     *
     * <p>Le refus vaut aussi quand le compte est borne mais rattache a aucun agent : son espace
     * est alors vide, et lui rendre le dossier d'un autre serait pire que de ne rien rendre.
     *
     * @param agentConcerne l'agent auquel se rapporte ce qui est demande.
     */
    public void exigerDansLePerimetre(Long agentConcerne) {
        if (!estBorne()) {
            return;
        }
        Long sien = agentDuCompteCourant().orElse(AUCUN_AGENT);
        if (agentConcerne == null || !sien.equals(agentConcerne)) {
            throw new AccessDeniedException("Cet element ne releve pas de votre espace personnel");
        }
    }
}
