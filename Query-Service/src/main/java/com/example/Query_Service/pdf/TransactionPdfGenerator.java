package com.example.Query_Service.pdf;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionPdfGenerator {

    public static ByteArrayInputStream generatePdf(
            List<TransferTransactionResponse> transactions,
            String reportTitle) {

        Document document = new Document();

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);

            document.open();

            Font titleFont =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18);

            Paragraph title =
                    new Paragraph(
                            reportTitle,
                            titleFont);

            title.setAlignment(Element.ALIGN_CENTER);

            document.add(title);

            document.add(Chunk.NEWLINE);

            Paragraph generatedOn =
                    new Paragraph(
                            "Generated On : "
                                    + LocalDateTime.now());

            document.add(generatedOn);

            document.add(Chunk.NEWLINE);

            PdfPTable table =
                    new PdfPTable(6);

            table.setWidthPercentage(100);

            table.addCell("Ref No");
            table.addCell("Amount");
            table.addCell("Type");
            table.addCell("Status");
            table.addCell("Sender");
            table.addCell("Receiver");

            for (TransferTransactionResponse tx : transactions) {

                table.addCell(
                        String.valueOf(
                                tx.getTransactionRefNo()));

                table.addCell(
                        String.valueOf(
                                tx.getAmount()));

                table.addCell(
                        String.valueOf(
                                tx.getTransactionType()));

                table.addCell(
                        String.valueOf(
                                tx.getTransactionStatus()));

                table.addCell(
                        String.valueOf(
                                tx.getSenderUserId()));

                table.addCell(
                        String.valueOf(
                                tx.getReceiverUserId()));
            }

            document.add(table);

            document.close();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generating PDF",
                    e);
        }

        return new ByteArrayInputStream(
                out.toByteArray());
    }
}