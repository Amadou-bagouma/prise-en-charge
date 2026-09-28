package com.mycompany.myapp.config;

import com.mycompany.myapp.service.BoiteReceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Cree au demarrage les boites de reception des profils qui n'en ont pas encore.
 *
 * <p>Sans ce rattrapage, les profils deja en base avant que la boite ne devienne automatique
 * resteraient sans boite, et leurs titulaires ouvriraient un ecran vide sans savoir pourquoi.
 *
 * <p>Declenche sur {@link ApplicationReadyEvent}, donc apres Liquibase : la colonne
 * {@code profil_id} existe forcement quand ce code s'execute.
 *
 * <p>Une erreur ici n'empeche pas l'application de demarrer - une boite manquante se rattrape a
 * la premiere consultation, alors qu'un refus de demarrage bloquerait tout le monde.
 */
@Component
@Order(InitialisationHabilitations.ORDRE + 10)
public class BoiteReceptionInitialiseur {

    private static final Logger LOG = LoggerFactory.getLogger(BoiteReceptionInitialiseur.class);

    private final BoiteReceptionService boiteReceptionService;

    public BoiteReceptionInitialiseur(BoiteReceptionService boiteReceptionService) {
        this.boiteReceptionService = boiteReceptionService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void creerLesBoitesManquantes() {
        try {
            int creees = boiteReceptionService.synchroniser();
            LOG.debug("Synchronisation des boites de reception terminee ({} creee(s))", creees);
        } catch (RuntimeException e) {
            LOG.error("Les boites de reception n'ont pas pu etre synchronisees au demarrage", e);
        }
    }
}
