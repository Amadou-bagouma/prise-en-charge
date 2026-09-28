package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.security.SecurityUtils;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Ecrit dans l'historique ce qui arrive aux dossiers et aux beneficiaires.
 *
 * <p>Un seul point d'ecriture, pour que toutes les traces aient la meme forme : qui, quand, quoi,
 * sur quoi. Les trois objets voisins se repartissent les roles sans se recouvrir - l'historique
 * dit ce qui s'est passe, la tache ce qui reste a faire, la notification ce qui a change depuis
 * la derniere visite.
 *
 * <p>Les traces ne sont jamais modifiees ni supprimees en dehors de la suppression du dossier
 * lui-meme : une trace que l'on peut reecrire ne prouve rien.
 */
@Service
public class JournalActions {

    private static final Logger LOG = LoggerFactory.getLogger(JournalActions.class);

    /** Les cibles suivies en dehors des demandes. */
    public static final String CIBLE_AGENT = "AGENT";

    public static final String CIBLE_AYANT_DROIT = "AYANT_DROIT";

    private final HistoriqueActionRepository historiqueActionRepository;

    private final UserRepository userRepository;

    public JournalActions(HistoriqueActionRepository historiqueActionRepository, UserRepository userRepository) {
        this.historiqueActionRepository = historiqueActionRepository;
        this.userRepository = userRepository;
    }

    /** Trace une action portant sur un dossier. */
    public void surDemande(DemandePriseEnCharge demande, User utilisateur, String action, String description) {
        HistoriqueAction trace = base(utilisateur, action, description);
        trace.setDemande(demande);
        historiqueActionRepository.save(trace);
    }

    /**
     * Trace une action portant sur un agent ou un ayant droit.
     *
     * <p>Sans auteur identifiable - un traitement de demarrage, par exemple - la trace est tout de
     * meme ecrite : mieux vaut savoir qu'une radiation a eu lieu sans savoir par qui, que de ne
     * pas savoir qu'elle a eu lieu.
     */
    public void surBeneficiaire(String cibleType, Long cibleId, String action, String description) {
        HistoriqueAction trace = base(utilisateurCourant(), action, description);
        trace.setCibleType(cibleType);
        trace.setCibleId(cibleId);
        historiqueActionRepository.save(trace);
        LOG.debug("Trace {} sur {} {}", action, cibleType, cibleId);
    }

    private HistoriqueAction base(User utilisateur, String action, String description) {
        HistoriqueAction trace = new HistoriqueAction();
        trace.setAction(action);
        trace.setDescription(description);
        trace.setDateAction(Instant.now());
        trace.setUtilisateur(utilisateur);
        return trace;
    }

    private User utilisateurCourant() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneByLogin).orElse(null);
    }
}
