package com.mycompany.myapp.service;

import com.mycompany.myapp.config.WebsocketConfiguration;
import com.mycompany.myapp.domain.Notification;
import com.mycompany.myapp.repository.UserRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Previent le destinataire d'un avis sans attendre qu'il change d'ecran.
 *
 * <p>L'envoi est differe jusqu'a la validation de la transaction : prevenir avant reviendrait a
 * annoncer une decision qui peut encore etre annulee, et le destinataire ouvrirait un dossier
 * qui n'a pas bouge.
 *
 * <p>Un echec d'envoi n'interrompt rien. Le canal ne fait que prevenir plus tot : l'avis est
 * deja enregistre, et la cloche le montrera au prochain chargement. Faire echouer
 * l'enregistrement d'une decision parce qu'un navigateur s'est ferme serait absurde.
 */
@Service
public class AvisTempsReel {

    private static final Logger LOG = LoggerFactory.getLogger(AvisTempsReel.class);

    private final SimpMessageSendingOperations messagerie;

    private final UserRepository userRepository;

    public AvisTempsReel(SimpMessageSendingOperations messagerie, UserRepository userRepository) {
        this.messagerie = messagerie;
        this.userRepository = userRepository;
    }

    /**
     * Ce qui part sur le canal : de quoi afficher l'avis, pas de quoi instruire le dossier.
     *
     * <p>Le message ne porte que ce qui s'affiche dans une alerte. Le dossier lui-meme se
     * recharge par les points d'entree habituels, qui verifient les habilitations - un canal
     * qui porterait le dossier contournerait ce controle.
     */
    public record AvisDiffuse(Long id, String titre, String message, String type, Long demandeId, String reference, Instant dateCreation) {}

    /** Remet l'avis a son destinataire, une fois la transaction validee. */
    public void diffuser(Notification notification) {
        String destinataire = loginDu(notification);
        if (destinataire == null) {
            return;
        }
        AvisDiffuse charge = new AvisDiffuse(
            notification.getId(),
            notification.getTitre(),
            notification.getMessage(),
            notification.getType() == null ? null : notification.getType().name(),
            notification.getDemande() == null ? null : notification.getDemande().getId(),
            notification.getDemande() == null ? null : notification.getDemande().getReference(),
            notification.getDateCreation()
        );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        envoyer(destinataire, charge);
                    }
                }
            );
        } else {
            envoyer(destinataire, charge);
        }
    }

    /**
     * Le login du destinataire, resolu tant que la transaction est ouverte.
     *
     * <p>Un avis enregistre depuis un DTO ne porte qu'un identifiant d'utilisateur : l'entite
     * que rend le mapper n'a pas de login. Sans cette resolution l'avis etait simplement
     * abandonne, sans erreur - le canal restait ouvert, et rien n'arrivait jamais.
     */
    private String loginDu(Notification notification) {
        if (notification.getUtilisateur() == null) {
            return null;
        }
        String login = notification.getUtilisateur().getLogin();
        if (login != null) {
            return login;
        }
        Long id = notification.getUtilisateur().getId();
        return id == null
            ? null
            : userRepository
                  .findById(id)
                  .map(utilisateur -> utilisateur.getLogin())
                  .orElse(null);
    }

    private void envoyer(String destinataire, AvisDiffuse charge) {
        try {
            messagerie.convertAndSendToUser(destinataire, WebsocketConfiguration.FILE_AVIS, charge);
            LOG.debug("Avis {} remis a {}", charge.id(), destinataire);
        } catch (RuntimeException e) {
            // Volontairement avale : l'avis est enregistre, la cloche le montrera. Voir la
            // documentation de la classe.
            LOG.warn("L'avis {} n'a pas pu etre remis a {} en direct : {}", charge.id(), destinataire, e.getMessage());
        }
    }
}
