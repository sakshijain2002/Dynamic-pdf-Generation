package com.pdf.controller;


import com.pdf.model.InvoiceRequest;
import com.pdf.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;


@RestController
@RequestMapping("/api/pdf")
public class PDFController {


    @Autowired
    private PDFService pdfService;

    @RestController
    @RequestMapping("/api/pdf")
    public class PdfController {

        @PostMapping("/generate")
        public ResponseEntity<InputStreamResource> downloadInvoicePdf(@RequestBody InvoiceRequest invoiceRequest) throws IOException, NoSuchAlgorithmException {
            // Generate or retrieve the PDF file
            File pdfFile = pdfService.generateOrRetrieveInvoicePdf(invoiceRequest);

            // Prepare the file for download
            InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfFile.getName() + "\"")  // Force file download
                    .contentType(MediaType.APPLICATION_PDF)  // Set content type as PDF
                    .contentLength(pdfFile.length())  // Set file length
                    .body(resource);  // Send file in the response
        }


    }
}
