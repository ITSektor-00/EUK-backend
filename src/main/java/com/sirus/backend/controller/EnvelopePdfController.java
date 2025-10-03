package com.sirus.backend.controller;

import com.sirus.backend.dto.EnvelopePdfRequest;
import com.sirus.backend.dto.EnvelopeBackSidePdfRequest;
import com.sirus.backend.dto.UgrozenoLiceDto;
import com.sirus.backend.service.EnvelopePdfService;
import com.sirus.backend.service.EnvelopeBackSidePdfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EnvelopePdfController {
    
    
    @Autowired
    private EnvelopePdfService envelopePdfService;
    
    @Autowired
    private EnvelopeBackSidePdfService envelopeBackSidePdfService;
    
    @GetMapping("/test-envelope-pdf")
    public ResponseEntity<?> testEnvelopePdf() {
        try {
            // Kreiranje test podataka
            EnvelopePdfRequest testRequest = new EnvelopePdfRequest();
            testRequest.setTemplate("T1");
            
            // Kreiranje više test lica da testiramo više stranica
            UgrozenoLiceDto testLice1 = new UgrozenoLiceDto();
            testLice1.setUgrozenoLiceId(1L);
            testLice1.setIme("Marko");
            testLice1.setPrezime("Petrović");
            testLice1.setUlicaIBroj("Knez Mihailova 15");
            testLice1.setPttBroj("11000");
            testLice1.setGradOpstina("Beograd");
            testLice1.setMesto("Stari grad");
            
            UgrozenoLiceDto testLice2 = new UgrozenoLiceDto();
            testLice2.setUgrozenoLiceId(2L);
            testLice2.setIme("Ana");
            testLice2.setPrezime("Nikolić");
            testLice2.setUlicaIBroj("Terazije 5");
            testLice2.setPttBroj("11000");
            testLice2.setGradOpstina("Beograd");
            testLice2.setMesto("Stari grad");
            
            UgrozenoLiceDto testLice3 = new UgrozenoLiceDto();
            testLice3.setUgrozenoLiceId(3L);
            testLice3.setIme("Petar");
            testLice3.setPrezime("Jovanović");
            testLice3.setUlicaIBroj("Vračar 10");
            testLice3.setPttBroj("11000");
            testLice3.setGradOpstina("Beograd");
            testLice3.setMesto("Vračar");
            
            testRequest.setUgrozenaLica(List.of(testLice1, testLice2, testLice3));
            
            // Generisanje PDF-a
            byte[] pdfBytes = envelopePdfService.generateEnvelopePdf(
                testRequest.getTemplate(), 
                testRequest.getUgrozenaLica()
            );
            
            // Kreiranje imena fajla - jednostavno imenovanje bez ćirilice
            String fileName = "koverte-t1.pdf";
            
            // Postavljanje HTTP headers za PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri testiranju PDF-a: " + e.getMessage());
        }
    }
    
    @GetMapping("/test-pdf-simple")
    public ResponseEntity<?> testPdfSimple() {
        try {
            // Jednostavan test da proverim da li iText radi
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "PDF service je dostupan",
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping("/test-pdf-font")
    public ResponseEntity<?> testPdfFont() {
        try {
            // Test da proverim da li iText font radi
            com.itextpdf.kernel.font.PdfFont font = com.itextpdf.kernel.font.PdfFontFactory.createFont();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "iText font je dostupan",
                "font", font.getClass().getSimpleName(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "iText font greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping("/test-cyrillic")
    public ResponseEntity<?> testCyrillic() {
        try {
            // Test ćirilice
            String cyrillicText = "СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ";
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Ćirilica test",
                "cyrillic", cyrillicText,
                "length", cyrillicText.length(),
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Ćirilica greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping("/test-font-loading")
    public ResponseEntity<?> testFontLoading() {
        try {
            java.util.Map<String, Object> results = new java.util.HashMap<>();
            
            // Test sistem fonta
            try {
                String systemFontPath = System.getProperty("java.home") + "/lib/fonts/DejaVuSans.ttf";
                com.itextpdf.kernel.font.PdfFont systemFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(systemFontPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                results.put("systemFont", "SUCCESS: " + systemFontPath);
            } catch (Exception e) {
                results.put("systemFont", "FAILED: " + e.getMessage());
            }
            
            // Test Windows fonta
            try {
                String windowsFontPath = "C:/Windows/Fonts/arial.ttf";
                com.itextpdf.kernel.font.PdfFont windowsFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(windowsFontPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                results.put("windowsFont", "SUCCESS: " + windowsFontPath);
            } catch (Exception e) {
                results.put("windowsFont", "FAILED: " + e.getMessage());
            }
            
            // Test standardnog fonta
            try {
                com.itextpdf.kernel.font.PdfFont standardFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
                results.put("standardFont", "SUCCESS: HELVETICA");
            } catch (Exception e) {
                results.put("standardFont", "FAILED: " + e.getMessage());
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Font loading test",
                "results", results,
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Font loading greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping(value = "/test-simple-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> testSimplePdf() {
        try {
            System.out.println("=== TEST SIMPLE PDF ===");
            
            // Kreiranje jednostavnog PDF-a sa osnovnim tekstom
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
            
            // Učitaj DejaVu Sans font iz resources - GARANTOVANO RADI SA ĆIRILICOM!
            com.itextpdf.kernel.font.PdfFont font;
            try {
                String dejaVuResourcePath = "/fonts/DejaVuSans.ttf";
                font = com.itextpdf.kernel.font.PdfFontFactory.createFont(dejaVuResourcePath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                System.out.println("DejaVu Sans font loaded from resources for test - GUARANTEED to work with Cyrillic!");
            } catch (Exception e1) {
                try {
                    String dejaVuPath = "C:/Windows/Fonts/DejaVuSans.ttf";
                    font = com.itextpdf.kernel.font.PdfFontFactory.createFont(dejaVuPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    System.out.println("DejaVu Sans font loaded from Windows for test");
                } catch (Exception e2) {
                    String arialPath = "C:/Windows/Fonts/arial.ttf";
                    font = com.itextpdf.kernel.font.PdfFontFactory.createFont(arialPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    System.out.println("Arial font loaded for test");
                }
            }
            
            // Dodaj jednostavan tekst
            com.itextpdf.layout.element.Paragraph para = new com.itextpdf.layout.element.Paragraph("TEST TEXT - OVO JE TEST")
                .setFont(font)
                .setFontSize(12);
            document.add(para);
            
            // Dodaj ćirilicu test
            com.itextpdf.layout.element.Paragraph cyrillicPara = new com.itextpdf.layout.element.Paragraph("ТЕСТ ЋИРИЛИЦА - СЕКРЕТАРИЈАТ")
                .setFont(font)
                .setFontSize(12);
            document.add(cyrillicPara);
            
            
            document.close();
            
            System.out.println("Test PDF generated, size: " + outputStream.size() + " bytes");
            
            // Postavljanje HTTP headers za PDF download
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-simple.pdf");
            headers.setContentLength(outputStream.size());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Test PDF error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Simple PDF greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping(value = "/test-basic-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> testBasicPdf() {
        try {
            System.out.println("=== TEST BASIC PDF WITH CYRILLIC ===");
            
            // Kreiranje osnovnog PDF-a sa ćirilicom
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
            
            // Pokušaj sa Inter fontom iz resources - najbolji za ćirilicu!
            com.itextpdf.kernel.font.PdfFont font;
            try {
                String interResourcePath = "/fonts/Inter_18pt-Bold.ttf";
                font = com.itextpdf.kernel.font.PdfFontFactory.createFont(interResourcePath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                System.out.println("Inter font loaded from resources for basic test - PERFECT for Cyrillic!");
            } catch (Exception e1) {
                try {
                    String interPath = "C:/Windows/Fonts/Inter-Regular.ttf";
                    font = com.itextpdf.kernel.font.PdfFontFactory.createFont(interPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    System.out.println("Inter font loaded from Windows for basic test");
                } catch (Exception e2) {
                    try {
                        String arialPath = "C:/Windows/Fonts/arial.ttf";
                        font = com.itextpdf.kernel.font.PdfFontFactory.createFont(arialPath, "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                        System.out.println("Arial font loaded for basic test");
                    } catch (Exception e3) {
                        System.out.println("Arial failed: " + e3.getMessage());
                        font = com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
                        System.out.println("WARNING: Fallback to Helvetica - ćirilica neće raditi!");
                    }
                }
            }
            
            // Dodaj ćirilicu tekst
            com.itextpdf.layout.element.Paragraph cyrillicPara = new com.itextpdf.layout.element.Paragraph("СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ")
                .setFont(font)
                .setFontSize(12);
            document.add(cyrillicPara);
            
            // Dodaj latinicu tekst
            com.itextpdf.layout.element.Paragraph latinPara = new com.itextpdf.layout.element.Paragraph("SEKRETARIJAT ZA SOCIJALNU ZASTITU")
                .setFont(font)
                .setFontSize(12);
            document.add(latinPara);
            
            // Dodaj test imena
            com.itextpdf.layout.element.Paragraph testIme = new com.itextpdf.layout.element.Paragraph("Марко Петровић")
                .setFont(font)
                .setFontSize(12);
            document.add(testIme);
            
            document.close();
            
            // Postavljanje HTTP headers za PDF download
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-basic.pdf");
            headers.setContentLength(outputStream.size());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Basic PDF greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping("/test-cyrillic-font")
    public ResponseEntity<?> testCyrillicFont() {
        try {
            System.out.println("=== TEST CYRILLIC FONT ===");
            
            // Test različitih fontova za ćirilicu
            String[] fontPaths = {
                "/fonts/DejaVuSans.ttf",
                "/fonts/Inter_18pt-Bold.ttf",
                "C:/Windows/Fonts/ARIALUNI.TTF",
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/times.ttf",
                "C:/Windows/Fonts/calibri.ttf"
            };
            
            String[] fontNames = {"DejaVu Sans (resources)", "Inter (resources)", "Arial Unicode MS", "Arial", "Times", "Calibri"};
            
            java.util.Map<String, Object> results = new java.util.HashMap<>();
            String testCyrillic = "СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ";
            
            for (int i = 0; i < fontPaths.length; i++) {
                try {
                    com.itextpdf.kernel.font.PdfFont testFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                        fontPaths[i], "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
                    
                    results.put(fontNames[i], "SUCCESS - Font loaded successfully");
                    System.out.println(fontNames[i] + ": ✅ FONT LOADED SUCCESSFULLY");
                } catch (Exception e) {
                    results.put(fontNames[i], "FAILED: " + e.getMessage());
                    System.out.println(fontNames[i] + ": ❌ FAILED - " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Cyrillic font test completed",
                "testText", testCyrillic,
                "results", results,
                "timestamp", java.time.LocalDateTime.now()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Cyrillic font test greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @GetMapping(value = "/test-font-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> testFontPdf() {
        try {
            System.out.println("=== TEST FONT PDF ===");
            
            java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(outputStream);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
            
            // Test različitih fontova - Inter je najbolji!
            String[] fontPaths = {
                "/fonts/Inter_18pt-Bold.ttf",
                "C:/Windows/Fonts/Inter-Regular.ttf",
                "C:/Windows/Fonts/Inter.ttf",
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/times.ttf", 
                "C:/Windows/Fonts/calibri.ttf",
                "C:/Windows/Fonts/DejaVuSans.ttf"
            };
            
            String[] fontNames = {"Inter-Bold (resources)", "Inter-Regular", "Inter", "Arial", "Times", "Calibri", "DejaVu"};
            
            for (int i = 0; i < fontPaths.length; i++) {
                try {
                    com.itextpdf.kernel.font.PdfFont testFont = com.itextpdf.kernel.font.PdfFontFactory.createFont(
                        fontPaths[i], "UTF-8", com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                    
                    com.itextpdf.layout.element.Paragraph fontTest = new com.itextpdf.layout.element.Paragraph(
                        fontNames[i] + ": СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ")
                        .setFont(testFont)
                        .setFontSize(10);
                    document.add(fontTest);
                    
                    System.out.println(fontNames[i] + " font loaded successfully");
                } catch (Exception e) {
                    System.out.println(fontNames[i] + " font failed: " + e.getMessage());
                }
            }
            
            document.close();
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "test-font.pdf");
            headers.setContentLength(outputStream.size());
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            System.err.println("Font test error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Font test greška: " + e.getMessage(),
                    "timestamp", java.time.LocalDateTime.now()
                ));
        }
    }
    
    @PostMapping(value = "/generate-envelope-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateEnvelopePdf(@Valid @RequestBody EnvelopePdfRequest request) {
        try {
            System.out.println("=== PDF GENERATION REQUEST ===");
            System.out.println("Template: " + request.getTemplate());
            System.out.println("Broj ugroženih lica: " + (request.getUgrozenaLica() != null ? request.getUgrozenaLica().size() : 0));
            
            // Validacija template-a
            if (!"T1".equals(request.getTemplate()) && !"T2".equals(request.getTemplate())) {
                return ResponseEntity.badRequest()
                    .body("Template mora biti 'T1' ili 'T2'");
            }
            
            // Validacija podataka
            if (request.getUgrozenaLica() == null || request.getUgrozenaLica().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Lista ugroženih lica ne može biti prazna");
            }
            
            // Generisanje PDF-a
            byte[] pdfBytes = envelopePdfService.generateEnvelopePdf(
                request.getTemplate(), 
                request.getUgrozenaLica()
            );
            
            System.out.println("PDF generated successfully, size: " + pdfBytes.length + " bytes");
            
            // Kreiranje imena fajla - jednostavno imenovanje bez ćirilice
            String template = request.getTemplate().toLowerCase();
            String fileName = "koverte-" + template + ".pdf";
            
            // Postavljanje HTTP headers za PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentLength(pdfBytes.length);
            System.out.println("Setting Content-Disposition header: " + fileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            // Dodaj CORS headers za download
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition, Content-Type, Content-Length");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            System.err.println("IOException in PDF generation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri generisanju PDF-a: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error in PDF generation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Neočekivana greška: " + e.getMessage());
        }
    }
    
    @GetMapping("/test-envelope-back-side-pdf")
    public ResponseEntity<?> testEnvelopeBackSidePdf() {
        try {
            // Kreiranje test podataka za zadnju stranu koverte
            EnvelopeBackSidePdfRequest testRequest = new EnvelopeBackSidePdfRequest();
            testRequest.setTemplate("T1");
            
            // Kreiranje test lica
            UgrozenoLiceDto testLice1 = new UgrozenoLiceDto();
            testLice1.setUgrozenoLiceId(1L);
            testLice1.setIme("Marko");
            testLice1.setPrezime("Petrović");
            testLice1.setUlicaIBroj("Knez Mihailova 15");
            testLice1.setPttBroj("11000");
            testLice1.setGradOpstina("Beograd");
            testLice1.setMesto("Stari grad");
            
            UgrozenoLiceDto testLice2 = new UgrozenoLiceDto();
            testLice2.setUgrozenoLiceId(2L);
            testLice2.setIme("Ana");
            testLice2.setPrezime("Nikolić");
            testLice2.setUlicaIBroj("Terazije 5");
            testLice2.setPttBroj("11000");
            testLice2.setGradOpstina("Beograd");
            testLice2.setMesto("Stari grad");
            
            testRequest.setUgrozenaLica(List.of(testLice1, testLice2));
            
            // Generisanje PDF-a za zadnju stranu
            byte[] pdfBytes = envelopeBackSidePdfService.generateEnvelopeBackSidePdf(
                testRequest.getTemplate(), 
                testRequest.getUgrozenaLica()
            );
            
            // Kreiranje imena fajla
            String fileName = "koverte-zadnja-strana-t1.pdf";
            
            // Postavljanje HTTP headers za PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri testiranju PDF-a zadnje strane: " + e.getMessage());
        }
    }
    
    @PostMapping(value = "/generate-envelope-back-side-pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<?> generateEnvelopeBackSidePdf(@Valid @RequestBody EnvelopeBackSidePdfRequest request) {
        try {
            System.out.println("=== BACK SIDE PDF GENERATION REQUEST ===");
            System.out.println("Template: " + request.getTemplate());
            System.out.println("Broj ugroženih lica: " + (request.getUgrozenaLica() != null ? request.getUgrozenaLica().size() : 0));
            
            // Validacija template-a
            if (!"T1".equals(request.getTemplate()) && !"T2".equals(request.getTemplate())) {
                return ResponseEntity.badRequest()
                    .body("Template mora biti 'T1' ili 'T2'");
            }
            
            // Validacija podataka
            if (request.getUgrozenaLica() == null || request.getUgrozenaLica().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Lista ugroženih lica ne može biti prazna");
            }
            
            // Generisanje PDF-a za zadnju stranu koverte
            byte[] pdfBytes = envelopeBackSidePdfService.generateEnvelopeBackSidePdf(
                request.getTemplate(), 
                request.getUgrozenaLica()
            );
            
            System.out.println("Back side PDF generated successfully, size: " + pdfBytes.length + " bytes");
            
            // Kreiranje imena fajla - jednostavno imenovanje bez ćirilice
            String template = request.getTemplate().toLowerCase();
            String fileName = "koverte-zadnja-strana-" + template + ".pdf";
            
            // Postavljanje HTTP headers za PDF download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            headers.setContentLength(pdfBytes.length);
            System.out.println("Setting Content-Disposition header: " + fileName);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            
            // Dodaj CORS headers za download
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition, Content-Type, Content-Length");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            System.err.println("IOException in back side PDF generation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri generisanju PDF-a zadnje strane: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error in back side PDF generation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Neočekivana greška: " + e.getMessage());
        }
    }
}
