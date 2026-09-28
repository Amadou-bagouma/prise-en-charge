package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * La notification de decision remise a l'agent : le document qui lui dit ce qui a ete decide sur
 * son dossier, et pourquoi.
 *
 * <p>Distincte de l'imprime de prise en charge. Celui-ci ne s'edite que pour un dossier valide,
 * et sert a se presenter chez le prestataire ; la notification s'edite aussi pour un refus, et
 * sert a informer l'interesse. Un refus sans document opposable ne se conteste pas : l'agent ne
 * dispose d'aucune trace de ce qu'on lui a dit, ni de la raison.
 *
 * <p>Le motif du refus est repris tel qu'il a ete saisi par le validateur. C'est ce qui sera lu
 * au guichet, et c'est pour cela que le motif est exige au moment du rejet.
 */
@Service
@Transactional(readOnly = true)
public class NotificationDecisionService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationDecisionService.class);

    private static final String ENTITY_NAME = "demandePriseEnCharge";

    private static final String LOGO_CLASSPATH = "static/content/images/logo-cnss.png";

    private static final DateTimeFormatter JOUR = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH);

    /** Les issues qui se notifient : une decision a ete prise, favorable ou non. */
    private static final List<StatutDemande> STATUTS_NOTIFIABLES = List.of(
        StatutDemande.VALIDEE,
        StatutDemande.REJETEE,
        StatutDemande.RETOURNEE
    );

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final HistoriqueActionRepository historiqueActionRepository;

    public NotificationDecisionService(
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        HistoriqueActionRepository historiqueActionRepository
    ) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.historiqueActionRepository = historiqueActionRepository;
    }

    /**
     * Compose la notification de decision d'un dossier.
     *
     * @param demandeId le dossier, qui doit porter une decision.
     * @return le PDF.
     */
    public byte[] genererNotification(Long demandeId) {
        DemandePriseEnCharge demande = demandePriseEnChargeRepository
            .findOneWithEagerRelationships(demandeId)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        if (!STATUTS_NOTIFIABLES.contains(demande.getStatut())) {
            throw new BadRequestAlertException(
                "Aucune decision n'a encore ete prise sur ce dossier (%s)".formatted(demande.getStatut()),
                ENTITY_NAME,
                "notification.sansdecision"
            );
        }
        Agent agent = agentDuDossier(demande);

        try {
            ByteArrayOutputStream sortie = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 55, 55, 45, 45);
            PdfWriter.getInstance(document, sortie);
            document.open();

            Font titre = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font corps = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font gras = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font petit = new Font(Font.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);

            document.add(entete(gras, petit));
            document.add(espace(18));

            Paragraph objet = new Paragraph("NOTIFICATION DE DÉCISION", titre);
            objet.setAlignment(Element.ALIGN_CENTER);
            document.add(objet);
            document.add(espace(6));

            Paragraph reference = new Paragraph("Dossier n° " + demande.getReference(), gras);
            reference.setAlignment(Element.ALIGN_CENTER);
            document.add(reference);
            document.add(espace(20));

            document.add(destinataire(agent, corps, gras));
            document.add(espace(16));
            document.add(decision(demande, corps, gras));
            document.add(espace(16));
            document.add(voiesDeRecours(demande, corps));
            document.add(espace(28));
            document.add(signature(corps));

            document.close();
            LOG.debug("Notification de decision composee pour le dossier {}", demande.getReference());
            return sortie.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("La notification de decision n'a pas pu etre composee", e);
        }
    }

    private PdfPTable entete(Font gras, Font petit) {
        PdfPTable table = new PdfPTable(new float[] { 1f, 3f });
        table.setWidthPercentage(100);

        PdfPCell celluleLogo = new PdfPCell();
        celluleLogo.setBorder(PdfPCell.NO_BORDER);
        Image logo = logo();
        if (logo != null) {
            logo.scaleToFit(65, 65);
            celluleLogo.addElement(logo);
        }
        table.addCell(celluleLogo);

        Paragraph institution = new Paragraph();
        institution.add(new Phrase("CAISSE NATIONALE DE SÉCURITÉ SOCIALE\n", gras));
        institution.add(new Phrase("Direction des Ressources Humaines\n", petit));
        institution.add(new Phrase("République du Niger", petit));
        PdfPCell celluleTexte = new PdfPCell(institution);
        celluleTexte.setBorder(PdfPCell.NO_BORDER);
        celluleTexte.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(celluleTexte);
        return table;
    }

    private Paragraph destinataire(Agent agent, Font corps, Font gras) {
        Paragraph p = new Paragraph();
        p.add(new Phrase("À l'attention de ", corps));
        p.add(new Phrase(agent.getPrenom() + " " + agent.getNom(), gras));
        p.add(new Phrase(", matricule ", corps));
        p.add(new Phrase(agent.getMatricule() == null ? "—" : agent.getMatricule(), gras));
        p.add(new Phrase(".", corps));
        return p;
    }

    private Paragraph decision(DemandePriseEnCharge demande, Font corps, Font gras) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_JUSTIFIED);
        switch (demande.getStatut()) {
            case VALIDEE -> {
                p.add(new Phrase("Votre demande de prise en charge médicale a été ", corps));
                p.add(new Phrase("ACCEPTÉE", gras));
                p.add(
                    new Phrase(
                        ". L'imprimé de prise en charge correspondant vous est délivré ; il est à présenter à " +
                            "l'établissement de soins retenu.",
                        corps
                    )
                );
            }
            case REJETEE -> {
                p.add(new Phrase("Votre demande de prise en charge médicale a été ", corps));
                p.add(new Phrase("REJETÉE", gras));
                p.add(new Phrase(", pour le motif suivant : ", corps));
                p.add(new Phrase(motif(demande), gras));
            }
            case RETOURNEE -> {
                p.add(new Phrase("Votre demande de prise en charge médicale vous est ", corps));
                p.add(new Phrase("RETOURNÉE POUR CORRECTION", gras));
                p.add(new Phrase(", pour le motif suivant : ", corps));
                p.add(new Phrase(motif(demande), gras));
                p.add(new Phrase(" Elle pourra être resoumise une fois ce point régularisé.", corps));
            }
            default -> throw new IllegalStateException("Statut non notifiable : " + demande.getStatut());
        }
        return p;
    }

    /**
     * Le motif de la decision.
     *
     * <p>Repris de la demande, ou a defaut de la derniere trace de l'historique : un dossier
     * repris apres coup pourrait avoir perdu son motif courant, et une notification qui dit
     * « motif non precise » est exactement ce qu'il ne faut pas remettre a un agent.
     */
    private String motif(DemandePriseEnCharge demande) {
        if (demande.getMotifRejet() != null && !demande.getMotifRejet().isBlank()) {
            return demande.getMotifRejet();
        }
        return historiqueActionRepository
            .findByDemandeIdOrderByDateActionAsc(demande.getId())
            .stream()
            .filter(action -> "RETOUR".equals(action.getAction()) || "REJET".equals(action.getAction()))
            .map(HistoriqueAction::getDescription)
            .filter(description -> description != null && !description.isBlank())
            .reduce((premier, dernier) -> dernier)
            .orElse("motif non consigné");
    }

    private Paragraph voiesDeRecours(DemandePriseEnCharge demande, Font corps) {
        if (demande.getStatut() == StatutDemande.VALIDEE) {
            return new Paragraph(
                "Cette notification ne vaut pas engagement de dépense au-delà des garanties prévues par le régime.",
                corps
            );
        }
        return new Paragraph(
            "Vous pouvez demander le réexamen de ce dossier auprès de la Direction des Ressources Humaines, " +
                "en produisant les éléments qui justifieraient une nouvelle appréciation.",
            corps
        );
    }

    private Paragraph signature(Font corps) {
        Paragraph p = new Paragraph();
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(new Phrase("Niamey, le " + JOUR.format(LocalDate.now(ZoneId.systemDefault())) + "\n\n", corps));
        p.add(new Phrase("Le Directeur des Ressources Humaines", corps));
        return p;
    }

    private Paragraph espace(float hauteur) {
        Paragraph p = new Paragraph(" ");
        p.setSpacingAfter(hauteur);
        return p;
    }

    /** L'agent titulaire du droit, que le dossier le vise directement ou via un ayant droit. */
    private Agent agentDuDossier(DemandePriseEnCharge demande) {
        if (demande.getAgent() != null) {
            return demande.getAgent();
        }
        if (demande.getAyantDroit() != null && demande.getAyantDroit().getAgent() != null) {
            return demande.getAyantDroit().getAgent();
        }
        throw new BadRequestAlertException(
            "Impossible de determiner l'agent destinataire de cette notification",
            ENTITY_NAME,
            "notification.agentintrouvable"
        );
    }

    /** Le logo est facultatif : la notification se compose sans lui. */
    private Image logo() {
        try (InputStream flux = new ClassPathResource(LOGO_CLASSPATH).getInputStream()) {
            return Image.getInstance(flux.readAllBytes());
        } catch (IOException | RuntimeException e) {
            LOG.debug("Logo absent, la notification est composee sans lui");
            return null;
        }
    }
}
