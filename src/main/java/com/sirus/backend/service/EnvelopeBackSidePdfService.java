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
public class EnvelopeBackSidePdfService {
    
    // Portrait dimenzije koverte: 175mm x 246mm
    private static final float ENVELOPE_WIDTH_MM = 175f;  // 17.5cm (širina u portrait)
    private static final float ENVELOPE_HEIGHT_MM = 246f; // 24.6cm (visina u portrait)
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
    
    public byte[] generateEnvelopeBackSidePdf(String template, List<UgrozenoLiceDto> ugrozenaLica) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Kreiranje PDF dokumenta
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        
        // Postavljanje veličine stranice za koverat (175mm x 246mm) - PORTRAIT
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
        System.out.println("Bold font loaded: " + boldFont.getClass().getSimpleName());
        
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
            
            generateEnvelopeBackSideContent(document, font, boldFont, lice, template, currentPage);
        }
        
        document.close();
        return outputStream.toByteArray();
    }
    
    private void generateEnvelopeBackSideContent(Document document, PdfFont font, PdfFont boldFont, UgrozenoLiceDto lice, String template, int currentPage) {
        
        // Pozicije prema specifikaciji (u mm)
        // 27mm od gornje ivice i 30mm od leve ivice - broj-resenja
        float brojResenjaTop = 27f * MM_TO_POINTS;
        float brojResenjaLeft = 30f * MM_TO_POINTS;
        
        // 55mm od leve ivice i 32.5mm od gornje ivice - resenje
        float resenjeTop = 32.5f * MM_TO_POINTS;
        float resenjeLeft = 55f * MM_TO_POINTS;
        
        // 40mm od gornje ivice i 30mm od leve ivice - prezime i ime
        float imeTop = 40f * MM_TO_POINTS;
        float imeLeft = 30f * MM_TO_POINTS;
        
        // 30mm od leve ivice i 46mm od gornje ivice - ulica_i_broj
        float ulicaTop = 46f * MM_TO_POINTS;
        float ulicaLeft = 30f * MM_TO_POINTS;
        
        // 60mm od gornje ivice i 43mm od leve ivice - СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ
        float sekretarijatTop = 60f * MM_TO_POINTS;
        float sekretarijatLeft = 43f * MM_TO_POINTS;
        
        // 43mm od leve ivice i 73mm od gornje ivice - Ул. 27. март бр. 43-45
        float adresaTop = 73f * MM_TO_POINTS;
        float adresaLeft = 43f * MM_TO_POINTS;
        
        // 85mm od gornje ivice i 42.5mm od leve ivice - 11000 (svaki broj odmaknut za 6mm)
        float pttTop = 85f * MM_TO_POINTS;
        float pttLeft = 42.5f * MM_TO_POINTS;
        
        // 80mm od leve ivice i 85mm od gornje ivice - Београд
        float gradTop = 85f * MM_TO_POINTS;
        float gradLeft = 80f * MM_TO_POINTS;
        
        // Broj-resenja (27mm od gornje, 30mm od leve)
        Paragraph brojResenja = new Paragraph("број-решења")
                .setFont(boldFont)
                .setFontSize(8)
                .setFixedPosition(currentPage, brojResenjaLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - brojResenjaTop, 100f)
                .setWidth(100f);
        document.add(brojResenja);
        
        // Rešenje (55mm od leve, 32.5mm od gornje)
        Paragraph resenje = new Paragraph("решење")
                .setFont(boldFont)
                .setFontSize(8)
                .setFixedPosition(currentPage, resenjeLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - resenjeTop, 100f)
                .setWidth(100f);
        document.add(resenje);
        
        // Prezime i ime (40mm od gornje, 30mm od leve) - konvertovano u ćirilicu velikim slovima
        String imePrezime = convertToCyrillic(lice.getIme() + " " + lice.getPrezime()).toUpperCase();
        Paragraph ime = new Paragraph(imePrezime)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, imeLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - imeTop, 200f)
                .setWidth(200f);
        document.add(ime);
        
        // Ulica i broj (46mm od gornje, 30mm od leve) - konvertovano u ćirilicu velikim slovima
        String ulicaIBroj = convertToCyrillic(lice.getUlicaIBroj()).toUpperCase();
        Paragraph ulica = new Paragraph(ulicaIBroj)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, ulicaLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - ulicaTop, 200f)
                .setWidth(200f);
        document.add(ulica);
        
        // СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ (60mm od gornje, 43mm od leve)
        Paragraph sekretarijat = new Paragraph("СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, sekretarijatLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - sekretarijatTop, 300f)
                .setWidth(300f);
        document.add(sekretarijat);
        
        // Ул. 27. март бр. 43-45 (73mm od gornje, 43mm od leve)
        Paragraph adresa = new Paragraph("Ул. 27. март бр. 43-45")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, adresaLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - adresaTop, 200f)
                .setWidth(200f);
        document.add(adresa);
        
        // 11000 sa razmakom kao između prva dva broja (85mm od gornje, 42.5mm od leve) - fiksno, ne vuče iz baze
        String pttBroj = "11000";
        StringBuilder pttWithSpaces = new StringBuilder();
        for (int i = 0; i < pttBroj.length(); i++) {
            if (i > 0) {
                pttWithSpaces.append(" "); // Smanjen razmak između cifara
            }
            pttWithSpaces.append(pttBroj.charAt(i));
        }
        
        Paragraph ptt = new Paragraph(pttWithSpaces.toString())
                .setFont(boldFont)
                .setFontSize(18) // Još veća veličina fonta za PTT broj
                .setFixedPosition(currentPage, pttLeft, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - pttTop, 100f)
                .setWidth(100f);
        document.add(ptt);
        
        // Београд (85mm od gornje, 100mm od leve) - fiksno, ne vuče iz baze, ista visina kao PTT broj, pomerio dalje od leve
        float gradLeftMoved = 100f * MM_TO_POINTS; // Pomerio sa 80mm na 100mm od leve ivice
        Paragraph gradPara = new Paragraph("Београд")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, gradLeftMoved, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - pttTop, 100f) // Koristi pttTop umesto gradTop
                .setWidth(100f);
        document.add(gradPara);
        
        // NOVE POZICIJE - dodatni tekst na zadnjoj strani
        
        // 142mm od gornje ivice i 25mm od leve ivice - број-решења
        float brojResenja2Top = 142f * MM_TO_POINTS;
        float brojResenja2Left = 25f * MM_TO_POINTS;
        Paragraph brojResenja2 = new Paragraph("број-решења")
                .setFont(boldFont)
                .setFontSize(8)
                .setFixedPosition(currentPage, brojResenja2Left, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - brojResenja2Top, 100f)
                .setWidth(100f);
        document.add(brojResenja2);
        
        // 93mm od leve ivice i 142mm od gornje ivice - решење
        float resenje2Top = 142f * MM_TO_POINTS;
        float resenje2Left = 93f * MM_TO_POINTS;
        Paragraph resenje2 = new Paragraph("решење")
                .setFont(boldFont)
                .setFontSize(8)
                .setFixedPosition(currentPage, resenje2Left, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - resenje2Top, 100f)
                .setWidth(100f);
        document.add(resenje2);
        
        // 163mm od gornje ivice i 25mm od leve ivice - ime i prezime iz baze
        float ime2Top = 163f * MM_TO_POINTS;
        float ime2Left = 25f * MM_TO_POINTS;
        String imePrezime2 = convertToCyrillic(lice.getIme() + " " + lice.getPrezime()).toUpperCase();
        Paragraph ime2 = new Paragraph(imePrezime2)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, ime2Left, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - ime2Top, 200f)
                .setWidth(200f);
        document.add(ime2);
        
        // 175mm od gornje ivice i 25mm od leve ivice - ulica i broj
        float ulica2Top = 175f * MM_TO_POINTS;
        float ulica2Left = 25f * MM_TO_POINTS;
        String ulicaIBroj2 = convertToCyrillic(lice.getUlicaIBroj()).toUpperCase();
        Paragraph ulica2 = new Paragraph(ulicaIBroj2)
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, ulica2Left, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - ulica2Top, 200f)
                .setWidth(200f);
        document.add(ulica2);
        
        // 210mm od gornje ivice i 37mm od leve ivice - СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ
        float sekretarijat2Top = 210f * MM_TO_POINTS;
        float sekretarijat2Left = 37f * MM_TO_POINTS;
        Paragraph sekretarijat2 = new Paragraph("СЕКРЕТАРИЈАТ ЗА СОЦИЈАЛНУ ЗАШТИТУ")
                .setFont(boldFont)
                .setFontSize(10)
                .setFixedPosition(currentPage, sekretarijat2Left, ENVELOPE_HEIGHT_MM * MM_TO_POINTS - sekretarijat2Top, 300f)
                .setWidth(300f);
        document.add(sekretarijat2);
    }
}
