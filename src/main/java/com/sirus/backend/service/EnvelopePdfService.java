package com.sirus.backend.service;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.font.PdfEncodings;
import com.sirus.backend.dto.UgrozenoLiceDto;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class EnvelopePdfService {
    
    private static final float ENVELOPE_WIDTH_MM = 245f;  // 24.5cm
    private static final float ENVELOPE_HEIGHT_MM = 175f; // 17.5cm
    private static final float MM_TO_POINTS = 2.834645669f; // 1mm = 2.834645669 points
    
    // Mapa za konverziju latinice u ćirilicu
    private static final Map<String, String> LATIN_TO_CYRILLIC = new HashMap<>();
    
    static {
        // Osnovna slova
        LATIN_TO_CYRILLIC.put("A", "А"); LATIN_TO_CYRILLIC.put("a", "а");
        LATIN_TO_CYRILLIC.put("B", "Б"); LATIN_TO_CYRILLIC.put("b", "б");
        LATIN_TO_CYRILLIC.put("V", "В"); LATIN_TO_CYRILLIC.put("v", "в");
        LATIN_TO_CYRILLIC.put("G", "Г"); LATIN_TO_CYRILLIC.put("g", "г");
        LATIN_TO_CYRILLIC.put("D", "Д"); LATIN_TO_CYRILLIC.put("d", "д");
        LATIN_TO_CYRILLIC.put("Đ", "Ђ"); LATIN_TO_CYRILLIC.put("đ", "ђ");
        LATIN_TO_CYRILLIC.put("E", "Е"); LATIN_TO_CYRILLIC.put("e", "е");
        LATIN_TO_CYRILLIC.put("Ž", "Ж"); LATIN_TO_CYRILLIC.put("ž", "ж");
        LATIN_TO_CYRILLIC.put("Z", "З"); LATIN_TO_CYRILLIC.put("z", "з");
        LATIN_TO_CYRILLIC.put("I", "И"); LATIN_TO_CYRILLIC.put("i", "и");
        LATIN_TO_CYRILLIC.put("J", "Ј"); LATIN_TO_CYRILLIC.put("j", "ј");
        LATIN_TO_CYRILLIC.put("K", "К"); LATIN_TO_CYRILLIC.put("k", "к");
        LATIN_TO_CYRILLIC.put("L", "Л"); LATIN_TO_CYRILLIC.put("l", "л");
        LATIN_TO_CYRILLIC.put("Lj", "Љ"); LATIN_TO_CYRILLIC.put("lj", "љ");
        LATIN_TO_CYRILLIC.put("M", "М"); LATIN_TO_CYRILLIC.put("m", "м");
        LATIN_TO_CYRILLIC.put("N", "Н"); LATIN_TO_CYRILLIC.put("n", "н");
        LATIN_TO_CYRILLIC.put("Nj", "Њ"); LATIN_TO_CYRILLIC.put("nj", "њ");
        LATIN_TO_CYRILLIC.put("O", "О"); LATIN_TO_CYRILLIC.put("o", "о");
        LATIN_TO_CYRILLIC.put("P", "П"); LATIN_TO_CYRILLIC.put("p", "п");
        LATIN_TO_CYRILLIC.put("R", "Р"); LATIN_TO_CYRILLIC.put("r", "р");
        LATIN_TO_CYRILLIC.put("S", "С"); LATIN_TO_CYRILLIC.put("s", "с");
        LATIN_TO_CYRILLIC.put("T", "Т"); LATIN_TO_CYRILLIC.put("t", "т");
        LATIN_TO_CYRILLIC.put("Ć", "Ћ"); LATIN_TO_CYRILLIC.put("ć", "ћ");
        LATIN_TO_CYRILLIC.put("U", "У"); LATIN_TO_CYRILLIC.put("u", "у");
        LATIN_TO_CYRILLIC.put("F", "Ф"); LATIN_TO_CYRILLIC.put("f", "ф");
        LATIN_TO_CYRILLIC.put("H", "Х"); LATIN_TO_CYRILLIC.put("h", "х");
        LATIN_TO_CYRILLIC.put("C", "Ц"); LATIN_TO_CYRILLIC.put("c", "ц");
        LATIN_TO_CYRILLIC.put("Č", "Ч"); LATIN_TO_CYRILLIC.put("č", "ч");
        LATIN_TO_CYRILLIC.put("Dž", "Џ"); LATIN_TO_CYRILLIC.put("dž", "џ");
        LATIN_TO_CYRILLIC.put("Š", "Ш"); LATIN_TO_CYRILLIC.put("š", "ш");
        
        // Specijalni slučajevi
        LATIN_TO_CYRILLIC.put("Beograd", "Београд");
        LATIN_TO_CYRILLIC.put("beograd", "београд");
        LATIN_TO_CYRILLIC.put("Novi Sad", "Нови Сад");
        LATIN_TO_CYRILLIC.put("novi sad", "нови сад");
        LATIN_TO_CYRILLIC.put("Niš", "Ниш");
        LATIN_TO_CYRILLIC.put("niš", "ниш");
        LATIN_TO_CYRILLIC.put("Kragujevac", "Крагујевац");
        LATIN_TO_CYRILLIC.put("kragujevac", "крагујевац");
        LATIN_TO_CYRILLIC.put("Subotica", "Суботица");
        LATIN_TO_CYRILLIC.put("subotica", "суботица");
    }
    
    /**
     * Konvertuje latinicu u ćirilicu
     */
    private String convertToCyrillic(String latinText) {
        if (latinText == null || latinText.trim().isEmpty()) {
            return latinText;
        }
        
        String result = latinText;
        
        // Prvo proveri specijalne slučajeve (celi gradovi)
        for (Map.Entry<String, String> entry : LATIN_TO_CYRILLIC.entrySet()) {
            if (entry.getKey().length() > 1) { // samo gradovi, ne slova
                result = result.replace(entry.getKey(), entry.getValue());
            }
        }
        
        // Zatim konvertuj slovo po slovo
        StringBuilder cyrillic = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            String charStr = String.valueOf(result.charAt(i));
            String cyrillicChar = LATIN_TO_CYRILLIC.get(charStr);
            if (cyrillicChar != null) {
                cyrillic.append(cyrillicChar);
            } else {
                cyrillic.append(charStr);
            }
        }
        
        return cyrillic.toString();
    }
    
    public byte[] generateEnvelopePdf(String template, List<UgrozenoLiceDto> ugrozenaLica) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Kreiranje PDF dokumenta
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        
        // Postavljanje veličine stranice za koverat (246mm x 175mm)
        PageSize envelopeSize = new PageSize(ENVELOPE_WIDTH_MM * MM_TO_POINTS, ENVELOPE_HEIGHT_MM * MM_TO_POINTS);
        pdfDoc.addNewPage(envelopeSize);
        
        Document document = new Document(pdfDoc);
        
        // Učitavanje fonta - MORA FORCE_EMBEDDED!
        PdfFont font;
        PdfFont boldFont;

        try {
            // Pokušaj sa DejaVu Sans iz resources
            String regularPath = "/fonts/DejaVuSans.ttf";
            String boldPath = "/fonts/DejaVuSans-Bold.ttf"; // Poseban bold font!
            
            font = PdfFontFactory.createFont(
                getClass().getResourceAsStream(regularPath).readAllBytes(),
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
            );
            
            boldFont = PdfFontFactory.createFont(
                getClass().getResourceAsStream(boldPath).readAllBytes(),
                PdfEncodings.IDENTITY_H,
                PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
            );
            
            System.out.println("✅ Fonts loaded from resources with FORCE_EMBEDDED");
        } catch (Exception e) {
            System.out.println("❌ Resources fonts failed: " + e.getMessage());
            // Fallback na Linux system fonts (Render.com)
            try {
                // Pokušaj sa Linux system fonts
                font = PdfFontFactory.createFont(
                    "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                );
                boldFont = PdfFontFactory.createFont(
                    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                );
                System.out.println("✅ Linux system fonts loaded with FORCE_EMBEDDED");
            } catch (Exception e2) {
                System.out.println("❌ Linux fonts failed: " + e2.getMessage());
                // Fallback na Windows fonts (development)
                try {
                    font = PdfFontFactory.createFont(
                        "C:/Windows/Fonts/arial.ttf",
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                    );
                    boldFont = PdfFontFactory.createFont(
                        "C:/Windows/Fonts/arialbd.ttf",
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED
                    );
                    System.out.println("✅ Windows fonts loaded with FORCE_EMBEDDED");
                } catch (Exception e3) {
                    System.out.println("❌ Windows fonts failed: " + e3.getMessage());
                    // Final fallback na standardni font
                    font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                    boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                    System.out.println("⚠️ WARNING: Fallback to Helvetica - ćirilica neće raditi!");
                }
            }
        }
        
        // Debug - proveri da li font radi
        System.out.println("Font loaded: " + font.getClass().getSimpleName());
        
        // Generisanje sadržaja za svako ugroženo lice
        System.out.println("Generating PDF for " + ugrozenaLica.size() + " ugrožena lica");
        
        for (int i = 0; i < ugrozenaLica.size(); i++) {
            UgrozenoLiceDto lice = ugrozenaLica.get(i);
            int currentPage = i + 1; // Stranice počinju od 1
            
            System.out.println("Processing lice " + currentPage + "/" + ugrozenaLica.size() + ": " 
                + lice.getIme() + " " + lice.getPrezime());
            
            // Ako nije prvo lice, dodaj novu stranicu
            if (i > 0) {
                System.out.println("Adding new page " + currentPage);
                pdfDoc.addNewPage(envelopeSize);
            }
            
            generateEnvelopeContent(document, font, boldFont, lice, template, currentPage);
        }
        
        document.close();
        return outputStream.toByteArray();
    }
    
    private void generateEnvelopeContent(Document document, PdfFont font, PdfFont boldFont, UgrozenoLiceDto lice, String template, int currentPage) {
        
        // Konverzija cm u points za pozicioniranje
        // 1cm = 28.3464567 points
        float CM_TO_POINTS = 28.3464567f;
        
        // Pozicije prema specifikaciji (u mm)
        float leftMargin = 15f * MM_TO_POINTS; // 15mm od leve ivice
        float naslovTop = 20f * MM_TO_POINTS;  // 20mm od vrha
        float adresaTop = 30f * MM_TO_POINTS;  // 30mm od vrha
        float gradTop = 40f * MM_TO_POINTS;    // 40mm od vrha
        
        // Naslov - СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ (20mm od vrha, 15mm od leve)
        Paragraph naslov = new Paragraph("СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, leftMargin, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - naslovTop, 300f) // KORISTI currentPage
                .setWidth(300f) // Povećana širina sa 200f na 300f
                .setKeepTogether(true); // Sprečava prelom teksta
        document.add(naslov);
        
        // Adresa - 27 МАРТА БР. 43-45 (30mm od vrha, 15mm od leve)
        Paragraph adresa = new Paragraph("27 МАРТА БР. 43-45")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, leftMargin, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - adresaTop, 200f); // KORISTI currentPage
        document.add(adresa);
        
        // Grad - 11 БЕОГРАД (40mm od vrha, 15mm od leve)
        Paragraph grad = new Paragraph("11000 БЕОГРАД")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, leftMargin, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - gradTop, 200f); // KORISTI currentPage
        document.add(grad);
        
        // Pozicije prema specifikaciji (u mm)
        float imeLeft = 110f * MM_TO_POINTS;  // 110mm od leve ivice
        float imeTop = 90f * MM_TO_POINTS;     // 90mm od gornje ivice
        float ulicaTop = 104f * MM_TO_POINTS;  // 104mm od gornje ivice
        float gradOpstinaTop = 118f * MM_TO_POINTS;    // 118mm od gornje ivice
        float mestoTop = 135f * MM_TO_POINTS;   // 135mm od gornje ivice (isto kao PTT broj)
        float mestoLeft = 165f * MM_TO_POINTS;  // 165mm od leve ivice
        float pttTop = 135f * MM_TO_POINTS;     // 135mm od gornje ivice
        float pttLeft = 110f * MM_TO_POINTS;    // 110mm od leve ivice
        
        // Ime i prezime ugroženog lica - konvertuj u ćirilicu i velika slova (90mm od gornje, 110mm od leve)
        String imePrezime = convertToCyrillic(lice.getIme()).toUpperCase() + " " + convertToCyrillic(lice.getPrezime()).toUpperCase();
        Paragraph imePrezimePara = new Paragraph(imePrezime)
                .setFont(boldFont)
                .setFontSize(12)
                .setFixedPosition(currentPage, imeLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - imeTop, 200f); // KORISTI currentPage
        document.add(imePrezimePara);
        
        // Ulica i broj - konvertuj u ćirilicu i velika slova (104mm od gornje, 110mm od leve)
        String adresaLica = convertToCyrillic(lice.getUlicaIBroj()).toUpperCase();
        Paragraph adresaLicaPara = new Paragraph(adresaLica)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, imeLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - ulicaTop, 200f); // KORISTI currentPage
        document.add(adresaLicaPara);
        
        // Grad/Opština - konvertuj u ćirilicu i velika slova (118mm od gornje, 110mm od leve)
        String gradCyrillic = convertToCyrillic(lice.getGradOpstina()).toUpperCase();
        Paragraph gradPara = new Paragraph(gradCyrillic)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, imeLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - gradOpstinaTop, 200f); // KORISTI currentPage
        document.add(gradPara);
        
        // Mesto - konvertuj u ćirilicu i velika slova (135mm od gornje, 165mm od leve)
        String mestoCyrillic = convertToCyrillic(lice.getMesto()).toUpperCase();
        Paragraph mestoPara = new Paragraph(mestoCyrillic)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, mestoLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - mestoTop, 200f); // KORISTI currentPage
        document.add(mestoPara);
        
        // PTT broj - svaki broj odmaknut 11mm od levog susednog (135mm od gornje, 110mm od leve)
        String pttBroj = lice.getPttBroj();
        if (pttBroj != null && !pttBroj.trim().isEmpty()) {
            for (int i = 0; i < pttBroj.length(); i++) {
                char broj = pttBroj.charAt(i);
                if (Character.isDigit(broj)) {
                    float pttX = pttLeft + (i * 11f * MM_TO_POINTS); // Svaki broj 11mm od levog susednog
                    Paragraph pttPara = new Paragraph(String.valueOf(broj))
                            .setFont(boldFont) // Boldovan PTT broj
                            .setFontSize(10)
                            .setFixedPosition(currentPage, pttX, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - pttTop, 20f); // KORISTI currentPage
                    document.add(pttPara);
                }
            }
        }
    }
}
