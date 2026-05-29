package com.academic.service;

import com.academic.database.DatabaseManager;
import com.academic.model.User;
import com.academic.model.Mark;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.util.List;

public class PDFService {

    public static boolean generateResultPDF(User user, String filePath) {
        List<Mark> marks = DatabaseManager.getUserMarks(user.getId());
        if (marks.isEmpty()) return false;

        // ── Compute CWA, total credits, pass rate ─────────────────────
        int    totalCredits = 0;
        double weightedSum  = 0;
        int    passed       = 0;

        for (Mark m : marks) {
            totalCredits += m.getCreditHours();
            weightedSum  += m.getScore() * m.getCreditHours();
            if (m.getScore() >= 50) passed++;
        }

        double cwa      = totalCredits > 0 ? weightedSum / totalCredits : 0;
        double passRate = marks.isEmpty() ? 0 : (passed * 100.0) / marks.size();
        String classification = getClassification(cwa);

        // ── Colours ───────────────────────────────────────────────────
        BaseColor seaBlue   = new BaseColor(0, 105, 148);
        BaseColor darkGray  = new BaseColor(50, 50, 50);
        BaseColor lightGray = new BaseColor(245, 245, 245);
        BaseColor white     = BaseColor.WHITE;
        BaseColor classColor = classificationColor(cwa);

        Document document = new Document(PageSize.A4, 40, 40, 50, 50);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // ── Header banner ─────────────────────────────────────────
            PdfPTable banner = new PdfPTable(1);
            banner.setWidthPercentage(100);
            banner.setSpacingAfter(18f);

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, white);
            Font subFont   = FontFactory.getFont(FontFactory.HELVETICA,      12, new BaseColor(200, 230, 255));

            Paragraph bannerContent = new Paragraph();
            bannerContent.add(new Chunk("IS  —  Information System\n", titleFont));
            bannerContent.add(new Chunk("Academic Performance Report", subFont));
            bannerContent.setAlignment(Element.ALIGN_CENTER);

            PdfPCell bannerCell = new PdfPCell();
            bannerCell.setBackgroundColor(seaBlue);
            bannerCell.setPadding(18);
            bannerCell.setBorder(Rectangle.NO_BORDER);
            bannerCell.addElement(bannerContent);
            banner.addCell(bannerCell);
            document.add(banner);

            // ── Student information ───────────────────────────────────
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, seaBlue);
            Font labelFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, darkGray);
            Font valueFont   = FontFactory.getFont(FontFactory.HELVETICA,      10, darkGray);

            Paragraph infoHeading = new Paragraph("STUDENT INFORMATION", sectionFont);
            infoHeading.setSpacingAfter(6f);
            document.add(infoHeading);

            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1.5f, 2.5f, 1.5f, 2.5f});
            infoTable.setSpacingAfter(16f);

            String dateStr = new java.text.SimpleDateFormat("dd MMM yyyy").format(new java.util.Date());
            addInfoRow(infoTable, "Full Name",  user.getUsername().toUpperCase(), labelFont, valueFont, lightGray, white);
            addInfoRow(infoTable, "Index No.",  user.getIndexNumber(),            labelFont, valueFont, lightGray, white);
            addInfoRow(infoTable, "Programme",  user.getProgramme(),              labelFont, valueFont, lightGray, white);
            addInfoRow(infoTable, "Level",      "Level " + user.getLevel(),       labelFont, valueFont, lightGray, white);
            addInfoRow(infoTable, "Semester",   "Semester " + user.getSemester(), labelFont, valueFont, lightGray, white);
            addInfoRow(infoTable, "Date",       dateStr,                          labelFont, valueFont, lightGray, white);
            document.add(infoTable);

            // ── Marks table ───────────────────────────────────────────
            Paragraph resultsHeading = new Paragraph("SEMESTER RESULTS", sectionFont);
            resultsHeading.setSpacingAfter(6f);
            document.add(resultsHeading);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1.8f, 3.5f, 1.2f, 1.2f, 1.3f, 1.8f});
            table.setSpacingAfter(16f);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, white);
            for (String h : new String[]{"Course Code", "Course Name", "Credits", "Grade", "Score", "Remark"}) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(seaBlue);
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderColor(seaBlue);
                table.addCell(cell);
            }

            boolean shade = false;
            for (Mark mark : marks) {
                BaseColor rowBg = shade ? new BaseColor(240, 248, 255) : white;
                table.addCell(styledCell(mark.getCourseCode(),                    10, false, Element.ALIGN_CENTER, rowBg));
                table.addCell(styledCell(mark.getCourseName(),                    10, false, Element.ALIGN_LEFT,   rowBg));
                table.addCell(styledCell(String.valueOf(mark.getCreditHours()),   10, false, Element.ALIGN_CENTER, rowBg));
                table.addCell(gradeCell(mark.getGrade(), rowBg));
                table.addCell(styledCell(String.valueOf(mark.getScore()),         10, false, Element.ALIGN_CENTER, rowBg));
                table.addCell(styledCell(mark.getRemark(),                        10, false, Element.ALIGN_CENTER, rowBg));
                shade = !shade;
            }
            document.add(table);

            // ── Academic summary ──────────────────────────────────────
            Paragraph summaryHeading = new Paragraph("ACADEMIC SUMMARY", sectionFont);
            summaryHeading.setSpacingAfter(6f);
            document.add(summaryHeading);

            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(65);
            summary.setHorizontalAlignment(Element.ALIGN_LEFT);
            summary.setWidths(new float[]{2.5f, 1.5f});
            summary.setSpacingAfter(16f);

            addSummaryRow(summary, "Total Credit Hours",               String.valueOf(totalCredits),        labelFont, valueFont, lightGray, white);
            addSummaryRow(summary, "Cumulative Weighted Average (CWA)", String.format("%.2f", cwa),        labelFont, valueFont, lightGray, white);
            addSummaryRow(summary, "Pass Rate",                        String.format("%.0f%%", passRate),  labelFont, valueFont, lightGray, white);
            addSummaryRow(summary, "Courses Registered",               String.valueOf(marks.size()),       labelFont, valueFont, lightGray, white);
            document.add(summary);

            // ── Classification banner ─────────────────────────────────
            PdfPTable classBanner = new PdfPTable(1);
            classBanner.setWidthPercentage(100);
            classBanner.setSpacingAfter(20f);

            Font classFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, white);
            Font cwaSubFont = FontFactory.getFont(FontFactory.HELVETICA,      10, new BaseColor(220, 240, 255));

            Paragraph classText = new Paragraph(user.getUsername().toUpperCase() + "  —  " + classification, classFont);
            classText.setAlignment(Element.ALIGN_CENTER);

            Paragraph cwaSub = new Paragraph(
                "CWA: " + String.format("%.2f", cwa) + "   |   Total Credit Hours: " + totalCredits, cwaSubFont);
            cwaSub.setAlignment(Element.ALIGN_CENTER);

            PdfPCell classCell = new PdfPCell();
            classCell.setBackgroundColor(classColor);
            classCell.setPadding(14);
            classCell.setBorder(Rectangle.NO_BORDER);
            classCell.addElement(classText);
            classCell.addElement(cwaSub);
            classBanner.addCell(classCell);
            document.add(classBanner);

            // ── Footer ────────────────────────────────────────────────
            String timestamp = new java.text.SimpleDateFormat("dd MMM yyyy, HH:mm").format(new java.util.Date());
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, BaseColor.GRAY);
            Paragraph footer = new Paragraph(
                "Generated by IS – Information System  on  " + timestamp +
                "\nThis is an unofficial document for personal reference only.", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private static String getClassification(double cwa) {
        if (cwa >= 80) return "First Class";
        if (cwa >= 70) return "Second Class Upper";
        if (cwa >= 60) return "Second Class Lower";
        if (cwa >= 50) return "Pass";
        return "Standard Too Low — No Certificate";
    }

    private static BaseColor classificationColor(double cwa) {
        if (cwa >= 80) return new BaseColor(27,  94,  32);
        if (cwa >= 70) return new BaseColor(13,  71, 161);
        if (cwa >= 60) return new BaseColor(230, 81,   0);
        if (cwa >= 50) return new BaseColor(69,  90, 100);
        return             new BaseColor(183, 28,  28);
    }

    private static void addInfoRow(PdfPTable table,
                                   String label, String value,
                                   Font labelFont, Font valueFont,
                                   BaseColor labelBg, BaseColor valueBg) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, labelFont));
        lbl.setBackgroundColor(labelBg);
        lbl.setPadding(7);
        lbl.setBorderColor(new BaseColor(220, 220, 220));
        table.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, valueFont));
        val.setBackgroundColor(valueBg);
        val.setPadding(7);
        val.setBorderColor(new BaseColor(220, 220, 220));
        table.addCell(val);
    }

    private static void addSummaryRow(PdfPTable table,
                                      String label, String value,
                                      Font labelFont, Font valueFont,
                                      BaseColor labelBg, BaseColor valueBg) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, labelFont));
        lbl.setBackgroundColor(labelBg);
        lbl.setPadding(8);
        lbl.setBorderColor(new BaseColor(220, 220, 220));
        table.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, valueFont));
        val.setBackgroundColor(valueBg);
        val.setPadding(8);
        val.setBorderColor(new BaseColor(220, 220, 220));
        table.addCell(val);
    }

    private static PdfPCell styledCell(String text, int fontSize, boolean bold,
                                        int alignment, BaseColor bg) {
        Font f = bold
            ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, fontSize)
            : FontFactory.getFont(FontFactory.HELVETICA,      fontSize);
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(7);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }

    private static PdfPCell gradeCell(String grade, BaseColor rowBg) {
        BaseColor gradeBg = switch (grade) {
            case "A" -> new BaseColor(209, 250, 229);
            case "B" -> new BaseColor(219, 234, 254);
            case "C" -> new BaseColor(254, 249, 195);
            case "D" -> new BaseColor(255, 237, 213);
            default  -> new BaseColor(254, 226, 226);
        };
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        PdfPCell cell = new PdfPCell(new Phrase(grade, f));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(7);
        cell.setBackgroundColor(gradeBg);
        cell.setBorderColor(new BaseColor(220, 220, 220));
        return cell;
    }
}
