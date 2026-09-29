package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.Tache;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.StatutTache;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.TacheRepository;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bascule un dossier en {@code EXPIREE} et clot ce qui l'attendait.
 *
 * <p>Classe a part, et transaction propre : le constat d'expiration est fait juste avant de
 * refuser l'action demandee, donc juste avant une exception. Ecrire dans la transaction courante
 * reviendrait a tout perdre au retour arriere - le refus serait bien rendu a l'appelant, mais le
 * dossier resterait indefiniment « en saisie », et chacun le reprendrait pour se faire refuser a
 * son tour.
 *
 * <p>{@code REQUIRES_NEW} suspend la transaction appelante et valide celle-ci separement : le
 * constat survit au refus.
 */
@Service
public class ExpirationDemande {

    private static final Logger LOG = LoggerFactory.getLogger(ExpirationDemande.class);

    /** Les taches deja closes ne sont pas retouchees : elles temoignent du travail fait. */
    private static final List<StatutTache> STATUTS_TACHE_CLOTURES = List.of(StatutTache.TERMINEE, StatutTache.ANNULEE);

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final TacheRepository tacheRepository;

    public ExpirationDemande(DemandePriseEnChargeRepository demandePriseEnChargeRepository, TacheRepository tacheRepository) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.tacheRepository = tacheRepository;
    }

    /**
     * Constate l'expiration d'un dossier et l'enregistre.
     *
     * @param demandeId le dossier a faire expirer.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void constater(Long demandeId) {
        DemandePriseEnCharge demande = demandePriseEnChargeRepository.findById(demandeId).orElse(null);
        if (demande == null) {
            return;
        }
        Instant maintenant = Instant.now();
        demande.setStatut(StatutDemande.EXPIREE);
        demande.setDateModification(maintenant);
        demandePriseEnChargeRepository.save(demande);

        // Ce qui attendait n'attend plus : laisser les taches ouvertes ferait revenir le dossier
        // dans les boites de reception alors qu'il ne peut plus avancer.
        List<Tache> taches = tacheRepository.findByDemandeIdAndStatutNotIn(demandeId, STATUTS_TACHE_CLOTURES);
        for (Tache tache : taches) {
            tache.setStatut(StatutTache.ANNULEE);
            tache.setDateTerminaison(maintenant);
        }
        tacheRepository.saveAll(taches);

        LOG.info("Dossier {} expire : {} tache(s) close(s)", demande.getReference(), taches.size());
    }
}
