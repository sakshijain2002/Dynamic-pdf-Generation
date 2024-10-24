package com.pdf.service;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.pdf.model.InvoiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Service
public class PDFService {

    public static final String OUTPUT_FOLDER = "pdf-files/";

    public File generateOrRetrieveInvoicePdf(InvoiceRequest invoiceRequest) throws IOException, NoSuchAlgorithmException {
        // Generate a unique hash for the invoice data
        String hash = generateHash(invoiceRequest);
        String fileName = "invoice_" + hash + ".pdf";
        String filePath = OUTPUT_FOLDER + fileName;

        File file = new File(filePath);

        // Check if the PDF already exists
        if (file.exists()) {
            // If the PDF exists, return the existing file
            return file;
        }

        // Create directories if they don't exist
        File folder = new File(OUTPUT_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // If it doesn't exist, generate the PDF
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Add the Seller and Buyer info
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        table.setHeight(130);

        Paragraph sellerParagraph = new Paragraph()
                .add("Seller: \n")
                .add(invoiceRequest.getSeller() + "\n")
                .add(invoiceRequest.getSellerAddress() + "\n")
                .add("GSTIN: " + invoiceRequest.getSellerGstin());

        table.addCell(new Cell().add(sellerParagraph).setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(1)));

        Paragraph buyerParagraph = new Paragraph()
                .add("Buyer: \n")
                .add(invoiceRequest.getBuyer() + "\n")
                .add(invoiceRequest.getBuyerAddress() + "\n")
                .add("GSTIN: " + invoiceRequest.getBuyerGstin());

        table.addCell(new Cell().add(buyerParagraph).setTextAlignment(TextAlignment.LEFT).setBorder(new SolidBorder(1)));

        document.add(table);

        // Add items table
        Table itemTable = new Table(4);
        itemTable.setWidth(UnitValue.createPercentValue(100));

        itemTable.addCell(new Cell().add(new Paragraph("Item")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
        itemTable.addCell(new Cell().add(new Paragraph("Quantity")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
        itemTable.addCell(new Cell().add(new Paragraph("Rate")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
        itemTable.addCell(new Cell().add(new Paragraph("Amount")).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));

        for (InvoiceRequest.Item item : invoiceRequest.getItems()) {
            itemTable.addCell(new Cell().add(new Paragraph(item.getName())).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            itemTable.addCell(new Cell().add(new Paragraph(item.getQuantity())).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate()))).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
            itemTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount()))).setTextAlignment(TextAlignment.CENTER).setBorder(new SolidBorder(1)));
        }

        document.add(itemTable);

        // Add the blank box (if needed)
        Table emptyBoxTable = new Table(1);
        emptyBoxTable.setWidth(UnitValue.createPercentValue(100));
        emptyBoxTable.setHeight(50);
        emptyBoxTable.addCell(new Cell().add(new Paragraph("")).setBorder(new SolidBorder(1)));
        document.add(emptyBoxTable);

        // Close the document
        document.close();

        // Return the generated file
        return file;
    }

    public String generateHash(InvoiceRequest invoiceRequest) throws NoSuchAlgorithmException {
        StringBuilder dataToHash = new StringBuilder();
        // Concatenate seller, buyer, and item details in a consistent format
        dataToHash.append(Objects.toString(invoiceRequest.getSeller(), "").trim())
                .append(Objects.toString(invoiceRequest.getSellerAddress(), "").trim())
                .append(Objects.toString(invoiceRequest.getSellerGstin(), "").trim())
                .append(Objects.toString(invoiceRequest.getBuyer(), "").trim())
                .append(Objects.toString(invoiceRequest.getBuyerAddress(), "").trim())
                .append(Objects.toString(invoiceRequest.getBuyerGstin(), "").trim());

        // Include item details consistently
        if (invoiceRequest.getItems() != null) {
            for (InvoiceRequest.Item item : invoiceRequest.getItems()) {
                dataToHash.append(Objects.toString(item.getName(), "").trim())
                        .append(Objects.toString(item.getQuantity(), "").trim())
                        .append(Objects.toString(item.getRate(), "").trim())
                        .append(Objects.toString(item.getAmount(), "").trim());
            }
        }

        // Create a SHA-256 hash
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(dataToHash.toString().getBytes(StandardCharsets.UTF_8));

        // Encode the hash using Base64
        String hash = Base64.getEncoder().encodeToString(hashBytes);

        // Sanitize the hash for use in filenames
        hash = hash.replace("/", "_")  // Replace forward slashes
                .replace("+", "-")  // Replace plus signs
                .replace("=", "");  // Remove equal signs (padding)

        return hash;
    }

}
