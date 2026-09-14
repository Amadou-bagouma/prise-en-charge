package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.DemandePriseEnCharge;
import com.mycompany.myapp.domain.HistoriqueAction;
import com.mycompany.myapp.domain.enumeration.StatutDemande;
import com.mycompany.myapp.repository.DemandePriseEnChargeRepository;
import com.mycompany.myapp.repository.HistoriqueActionRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Generates the PDF "etat" (report) for a fully validated {@link DemandePriseEnCharge}.
 */
@Service
@Transactional(readOnly = true)
public class RapportDemandeService {

    private static final String ENTITY_NAME = "demandePriseEnCharge";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(
        ZoneId.systemDefault()
    );

    private static final Color ACCENT_COLOR = new Color(74, 154, 95);

    private static final Color ACCENT_SOFT_COLOR = new Color(232, 247, 238);

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
        List<HistoriqueAction> historique = historiqueActionRepository.findByDemandeIdOrderByDateActionAsc(demandeId);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 60, 40);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font subtitleFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Font sectionFont = new Font(Font.HELVETICA, 12, Font.BOLD, ACCENT_COLOR);
            Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font valueFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            Paragraph title = new Paragraph("Etat de prise en charge medicale", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Caisse Nationale de Securite Sociale (CNSS)", subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            document.add(sectionTitle("Identification", sectionFont));
            PdfPTable idTable = infoTable();
            addRow(idTable, "Reference", demande.getReference(), labelFont, valueFont);
            addRow(idTable, "Beneficiaire", beneficiaire(demande), labelFont, valueFont);
            addRow(idTable, "Type de soin", demande.getTypeSoin() != null ? demande.getTypeSoin().getLibelle() : "", labelFont, valueFont);
            addRow(
                idTable,
                "Etablissement de sante",
                demande.getEtablissementSante() != null ? demande.getEtablissementSante().getNom() : "",
                labelFont,
                valueFont
            );
            addRow(idTable, "Priorite", demande.getPriorite() != null ? demande.getPriorite().name() : "", labelFont, valueFont);
            addRow(idTable, "Statut", "Validee", labelFont, valueFont);
            document.add(idTable);

            document.add(sectionTitle("Historique de validation", sectionFont));
            PdfPTable histTable = new PdfPTable(new float[] { 2, 2, 5 });
            histTable.setWidthPercentage(100);
            histTable.setSpacingBefore(6);
            addHeaderCell(histTable, "Date", labelFont);
            addHeaderCell(histTable, "Par", labelFont);
            addHeaderCell(histTable, "Action", labelFont);
            for (HistoriqueAction action : historique) {
                histTable.addCell(
                    new Phrase(action.getDateAction() != null ? DATE_FORMATTER.format(action.getDateAction()) : "", valueFont)
                );
                histTable.addCell(new Phrase(action.getUtilisateur() != null ? action.getUtilisateur().getLogin() : "", valueFont));
                histTable.addCell(new Phrase(libelleAction(action), valueFont));
            }
            document.add(histTable);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            throw new IllegalStateException("Impossible de generer le rapport PDF", e);
        }
    }

    private String beneficiaire(DemandePriseEnCharge demande) {
        if (demande.getAgent() != null) {
            return demande.getAgent().getNom() + " " + demande.getAgent().getPrenom() + " (" + demande.getAgent().getMatricule() + ")";
        }
        if (demande.getAyantDroit() != null) {
            return demande.getAyantDroit().getNom() + " " + demande.getAyantDroit().getPrenom();
        }
        return "";
    }

    private String libelleAction(HistoriqueAction action) {
        StringBuilder sb = new StringBuilder(action.getAction());
        if (action.getDescription() != null && !action.getDescription().isBlank()) {
            sb.append(" - ").append(action.getDescription());
        }
        return sb.toString();
    }

    private Paragraph sectionTitle(String text, Font font) {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(14);
        p.setSpacingAfter(6);
        return p;
    }

    private PdfPTable infoTable() {
        PdfPTable table = new PdfPTable(new float[] { 3, 7 });
        table.setWidthPercentage(100);
        return table;
    }

    private void addRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        table.addCell(borderlessCell(new Phrase(label, labelFont)));
        table.addCell(borderlessCell(new Phrase(value != null ? value : "", valueFont)));
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(ACCENT_SOFT_COLOR);
        table.addCell(cell);
    }

    private PdfPCell borderlessCell(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(4);
        return cell;
    }
}
