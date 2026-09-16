package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.Agent;
import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.LienParente;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.domain.enumeration.TypeSoin;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import org.openpdf.text.BadElementException;
import org.openpdf.text.Chunk;
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
 * Generates the PDF "Prise en charge des soins medicaux" for a fully validated
 * {@link DemandePriseEnCharge}, reproducing the official CNSS paper form.
 */
@Service
@Transactional(readOnly = true)
public class RapportDemandeService {

    private static final Logger LOG = LoggerFactory.getLogger(RapportDemandeService.class);

    private static final String ENTITY_NAME = "demandePriseEnCharge";

    /**
     * Optional CNSS logo, printed top-left of the report. Drop a PNG/JPG file at this
     * classpath location (src/main/resources/static/content/images/logo-cnss.png) to
     * have it appear automatically; the report renders fine without it.
     */
    private static final String LOGO_CLASSPATH = "static/content/images/logo-cnss.png";

    private final DemandePriseEnChargeRepository demandePriseEnChargeRepository;

    private final HistoriqueActionRepository historiqueActionRepository;

    public RapportDemandeService(
        DemandePriseEnChargeRepository demandePriseEnChargeRepository,
        HistoriqueActionRepository historiqueActionRepository
    ) {
        this.demandePriseEnChargeRepository = demandePriseEnChargeRepository;
        this.historiqueActionRepository = historiqueActionRepository;
    }

    /**
     * Builds the PDF report for the given demande.
     *
     * @param demandeId the id of the demande, which must be in the {@code VALIDEE} state.
     * @return the PDF file content.
     */
    public byte[] genererRapport(Long demandeId) {
        DemandePriseEnCharge demande = demandePriseEnChargeRepository
            .findOneWithEagerRelationships(demandeId)
            .orElseThrow(() -> new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
        if (demande.getStatut() != StatutDemande.VALIDEE) {
            throw new BadRequestAlertException(
                "Le rapport n'est disponible que pour une demande validee",
                ENTITY_NAME,
                "rapport.notvalidated"
            );
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 40, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD | Font.UNDERLINE);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Font bodyBoldFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            Font noteFont = new Font(Font.HELVETICA, 9, Font.ITALIC);
            Font underlineFont = new Font(Font.HELVETICA, 10, Font.BOLD | Font.UNDERLINE);
            Font footerFont = new Font(Font.HELVETICA, 7, Font.NORMAL, Color.DARK_GRAY);

            document.add(header(demande, headerFont));

            Paragraph title = new Paragraph("PRISE EN CHARGE DES SOINS MEDICAUX", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(10);
            title.setSpacingAfter(20);
            document.add(title);

            Paragraph body = new Paragraph();
            body.setFont(bodyFont);
            body.setAlignment(Element.ALIGN_JUSTIFIED);
            body.setLeading(18);
            corpsAttestation(body, demande, bodyFont, bodyBoldFont);
            document.add(body);

            document.add(Chunk.NEWLINE);
            document.add(casesTypeSoin(demande.getTypeSoins(), bodyFont));

            Paragraph note = new Paragraph(
                "N/B Cette prise en charge ne concerne en aucun cas l'achat d'appareillage ou autres protheses.",
                noteFont
            );
            note.setSpacingBefore(20);
            document.add(note);

            Paragraph joindre = new Paragraph("Joindre l'original de prise en charge a la facture", underlineFont);
            joindre.setAlignment(Element.ALIGN_CENTER);
            joindre.setSpacingBefore(30);
            joindre.setSpacingAfter(40);
            document.add(joindre);

            List<HistoriqueAction> historique = historiqueActionRepository.findByDemandeIdOrderByDateActionAsc(demandeId);
            User validateurDrh = dernierValidateur(historique, "VALIDATION_DRH");
            User validateurInfirmerie = dernierValidateur(historique, "VALIDATION_INFIRMERIE");
            document.add(signatures(underlineFont, validateurInfirmerie, validateurDrh));

            document.add(pied(footerFont));

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Impossible de generer le rapport PDF", e);
        }
    }

    private PdfPTable header(DemandePriseEnCharge demande, Font headerFont) {
        PdfPTable table = new PdfPTable(new float[] { 1, 2, 1 });
        table.setWidthPercentage(100);

        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(PdfPCell.NO_BORDER);
        Image logo = logoImage();
        if (logo != null) {
            logo.scaleToFit(70, 70);
            logoCell.addElement(logo);
        }
        table.addCell(logoCell);

        PdfPCell titleCell = new PdfPCell(new Phrase("CAISSE NATIONALE DE SECURITE SOCIALE", headerFont));
        titleCell.setBorder(PdfPCell.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(titleCell);

        PdfPCell refCell = new PdfPCell(new Phrase("N° " + demande.getReference() + " /", headerFont));
        refCell.setBorder(PdfPCell.NO_BORDER);
        refCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        refCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(refCell);

        return table;
    }

    private Image logoImage() {
        try (InputStream in = new ClassPathResource(LOGO_CLASSPATH).getInputStream()) {
            return Image.getInstance(in.readAllBytes());
        } catch (IOException | BadElementException e) {
            LOG.debug("No CNSS logo found at classpath:{} - printing the report without it", LOGO_CLASSPATH);
            return null;
        }
    }

    /**
     * The user who most recently performed the given workflow action (e.g. {@code VALIDATION_DRH})
     * on this demande, or {@code null} if it hasn't reached that step (or was resoumise before
     * anyone validated it again).
     */
    private User dernierValidateur(List<HistoriqueAction> historique, String action) {
        for (int i = historique.size() - 1; i >= 0; i--) {
            HistoriqueAction entry = historique.get(i);
            if (action.equals(entry.getAction())) {
                return entry.getUtilisateur();
            }
        }
        return null;
    }

    private Image signatureImage(User validateur) {
        if (validateur == null || validateur.getSignature() == null) {
            return null;
        }
        try {
            return Image.getInstance(validateur.getSignature());
        } catch (IOException | BadElementException e) {
            LOG.warn("Could not render the signature of user {} on the rapport", validateur.getLogin(), e);
            return null;
        }
    }

    private void corpsAttestation(Paragraph body, DemandePriseEnCharge demande, Font normal, Font bold) {
        Agent agent = demande.getAgent() != null ? demande.getAgent() : demande.getAyantDroit().getAgent();

        body.add(
            new Chunk("Je Soussigne le Directeur des Ressources Humaines de la Caisse Nationale de Securite Sociale atteste que ", normal)
        );
        body.add(new Chunk(agent.getNom() + " " + agent.getPrenom(), bold));
        body.add(new Chunk(" Mle ", normal));
        body.add(new Chunk(agent.getMatricule(), bold));
        body.add(new Chunk(" est en service a la Caisse Nationale de Securite Sociale", normal));
        if (agent.getFonction() != null && !agent.getFonction().isBlank()) {
            body.add(new Chunk(" en qualite de ", normal));
            body.add(new Chunk(agent.getFonction(), bold));
        }
        body.add(
            new Chunk(
                ". Les soins medicaux occasionnes par lui ou sa famille sont pris en charge par la Caisse Nationale de Securite Sociale.\n\n",
                normal
            )
        );

        body.add(new Chunk("Cette prise en charge couvre les prestations medicales fournies par", normal));
        if (demande.getEtablissementSante() != null) {
            body.add(new Chunk(" (", normal));
            body.add(new Chunk(demande.getEtablissementSante().getNom(), bold));
            body.add(new Chunk(")", normal));
        }
        if (demande.getAyantDroit() != null) {
            body.add(new Chunk(" au benefice de " + lienBeneficiaire(demande.getAyantDroit().getLien()) + " ", normal));
            body.add(new Chunk(demande.getAyantDroit().getNom() + " " + demande.getAyantDroit().getPrenom(), bold));
        }
        body.add(new Chunk(".", normal));
    }

    private String lienBeneficiaire(LienParente lien) {
        if (lien == null) {
            return "son proche";
        }
        return switch (lien) {
            case ENFANT -> "son enfant";
            case CONJOINT -> "son (sa) conjoint(e)";
            case AUTRE -> "son proche";
        };
    }

    private PdfPTable casesTypeSoin(Set<TypeSoin> typeSoins, Font font) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
        table.getDefaultCell().setPaddingBottom(12);

        caseACocher(table, "CONSULTATIONS", typeSoins.contains(TypeSoin.CONSULTATIONS), font);
        caseACocher(table, "EXAMENS MEDICAUX", typeSoins.contains(TypeSoin.EXAMENS_MEDICAUX), font);
        caseACocher(table, "INTERVENTION CHIRURGICALE", typeSoins.contains(TypeSoin.INTERVENTION_CHIRURGICALE), font);
        caseACocher(table, "HOSPITALISATION", typeSoins.contains(TypeSoin.HOSPITALISATION), font);

        return table;
    }

    private void caseACocher(PdfPTable table, String libelle, boolean cochee, Font font) {
        Phrase phrase = new Phrase();
        phrase.add(new Chunk(cochee ? "[X] " : "[ ] ", new Font(Font.HELVETICA, 12, Font.BOLD)));
        phrase.add(new Chunk(libelle, font));
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(12);
        table.addCell(cell);
    }

    private PdfPTable signatures(Font font, User validateurInfirmerie, User validateurDrh) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        table.addCell(signatureCell("VISA DU MEDECIN CNSS", validateurInfirmerie, font));
        table.addCell(signatureCell("LE DIRECTEUR DES RESSOURCES HUMAINES", validateurDrh, font));

        return table;
    }

    private PdfPCell signatureCell(String libelle, User validateur, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setMinimumHeight(70);
        cell.addElement(new Phrase(libelle, font));
        Image signature = signatureImage(validateur);
        if (signature != null) {
            signature.scaleToFit(120, 50);
            signature.setSpacingBefore(4);
            cell.addElement(signature);
        }
        return cell;
    }

    private Paragraph pied(Font footerFont) {
        Paragraph footer = new Paragraph();
        footer.setFont(footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        footer.add("BP: 225 Niamey - Niger - tel : +227 20 73 35 17 / +227 20 73 35 18 - Fax : +227 20 73 42 44 - Email: cnss@intnet.ne\n");
        footer.add("Comptes Bancaires - BCEAO : 002618200 / 00120001 - ECOBANK : 01000029043012\n");
        footer.add("SONIBANK : 006401001 0251 4021 30 - BOA : 0115953002");
        return footer;
    }
}
