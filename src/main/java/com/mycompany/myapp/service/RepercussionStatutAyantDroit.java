package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.AyantDroit;
import com.mycompany.myapp.domain.enumeration.StatutAgent;
import com.mycompany.myapp.domain.enumeration.StatutAyantDroit;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Repercussion du statut d'un agent sur ses ayants droit.
 *
 * <p>La couverture d'un ayant droit derive de celle de son agent : un agent radie ne peut pas
 * laisser derriere lui des ayants droit encore couverts. La repercussion est donc automatique.
 *
 * <p>Elle est aussi reversible. Le statut propre de chaque ayant droit est mis de cote dans
 * {@code statutAvantCascade} avant d'etre recouvert, et rendu tel quel quand l'agent redevient
 * actif : un enfant radie pour depassement d'age ne redevient pas couvert parce que son pere a
 * repris du service.
 *
 * <p>Un ayant droit decede n'est jamais touche - ni recouvert, ni retabli. Le deces n'est pas une
 * mesure administrative que l'on leve.
 */
@Service
public class RepercussionStatutAyantDroit {

    private static final Logger LOG = LoggerFactory.getLogger(RepercussionStatutAyantDroit.class);

    /** Préfixe des motifs posés par la répercussion, pour qu'on les distingue d'une décision prise sur l'ayant droit. */
    static final String PREFIXE_REPERCUSSION = "Répercuté depuis l'agent : ";

    static final String MOTIF_RETABLISSEMENT = "Statut rétabli : l'agent est de nouveau actif.";

    /**
     * Le statut a appliquer aux ayants droit quand l'agent prend ce statut.
     *
     * <p>Une suspension se repercute en suspension : elle est temporaire des deux cotes. Les
     * autres sorties d'activite retirent le droit, sans prejuger de la situation propre de
     * l'ayant droit - c'est pourquoi l'ancienne est conservee.
     *
     * @return le statut a appliquer, ou {@code null} si l'agent est actif (rien a repercuter).
     */
    public StatutAyantDroit statutRepercute(StatutAgent statutAgent) {
        return switch (statutAgent) {
            case ACTIF -> null;
            case SUSPENDU -> StatutAyantDroit.SUSPENDU;
            case RETRAITE, RADIE, DECEDE -> StatutAyantDroit.RADIE;
        };
    }

    /**
     * Applique le nouveau statut de l'agent a ses ayants droit.
     *
     * @param ayantsDroit les ayants droit de l'agent, modifies sur place.
     * @param statutAgent le nouveau statut de l'agent.
     * @param motifAgent le motif saisi sur l'agent, repris dans celui des ayants droit.
     * @return le nombre d'ayants droit dont le statut a change.
     */
    public int appliquer(List<AyantDroit> ayantsDroit, StatutAgent statutAgent, String motifAgent) {
        StatutAyantDroit aAppliquer = statutRepercute(statutAgent);
        int touches = aAppliquer == null ? retablir(ayantsDroit) : recouvrir(ayantsDroit, aAppliquer, motifAgent);
        if (touches > 0) {
            LOG.debug("Statut agent {} répercuté sur {} ayant(s) droit", statutAgent, touches);
        }
        return touches;
    }

    /** Met de cote le statut propre de chaque ayant droit, puis le recouvre. */
    private int recouvrir(List<AyantDroit> ayantsDroit, StatutAyantDroit aAppliquer, String motifAgent) {
        Instant maintenant = Instant.now();
        int touches = 0;
        for (AyantDroit ayantDroit : ayantsDroit) {
            if (ayantDroit.getStatut() == StatutAyantDroit.DECEDE) {
                continue;
            }
            // Deux sorties d'activite de suite ne doivent pas ecraser la memoire du statut
            // propre : seule la premiere repercussion l'enregistre.
            if (ayantDroit.getStatutAvantCascade() == null) {
                ayantDroit.setStatutAvantCascade(ayantDroit.getStatut());
                ayantDroit.setMotifAvantCascade(ayantDroit.getMotifStatut());
            }
            if (ayantDroit.getStatut() == aAppliquer) {
                continue;
            }
            ayantDroit.setStatut(aAppliquer);
            ayantDroit.setDateStatut(maintenant);
            ayantDroit.setMotifStatut(PREFIXE_REPERCUSSION + (motifAgent == null || motifAgent.isBlank() ? "—" : motifAgent));
            touches++;
        }
        return touches;
    }

    /** Rend a chaque ayant droit le statut qu'il avait avant la repercussion. */
    private int retablir(List<AyantDroit> ayantsDroit) {
        Instant maintenant = Instant.now();
        int touches = 0;
        for (AyantDroit ayantDroit : ayantsDroit) {
            StatutAyantDroit propre = ayantDroit.getStatutAvantCascade();
            if (propre == null) {
                // Statut decide sur l'ayant droit lui-meme : la reactivation de l'agent ne le
                // remet pas en cause.
                continue;
            }
            String motifPropre = ayantDroit.getMotifAvantCascade();
            ayantDroit.setStatutAvantCascade(null);
            ayantDroit.setMotifAvantCascade(null);
            boolean statutInchange = ayantDroit.getStatut() == propre;
            ayantDroit.setStatut(propre);
            // Le motif propre est rendu meme quand le statut ne bouge pas : sans cela, un enfant
            // radie de son propre chef garderait la raison de la radiation de son pere.
            ayantDroit.setMotifStatut(motifPropre != null && !motifPropre.isBlank() ? motifPropre : MOTIF_RETABLISSEMENT);
            if (statutInchange) {
                continue;
            }
            ayantDroit.setDateStatut(maintenant);
            touches++;
        }
        return touches;
    }
}
