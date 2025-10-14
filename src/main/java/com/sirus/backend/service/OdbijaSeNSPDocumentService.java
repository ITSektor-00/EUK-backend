package com.sirus.backend.service;

import com.sirus.backend.dto.OdbijaSeNSPRequestDTO;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.repository.EukPredmetRepository;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Servis za generisanje Word dokumenta "OДБИЈА СЕ NSP,UNSP,DD,UDTNP"
 */
@Service
public class OdbijaSeNSPDocumentService {
    
    private static final Logger log = LoggerFactory.getLogger(OdbijaSeNSPDocumentService.class);
    private static final String TEMPLATE_RESOURCE_PATH = "/obrasci/obrasci NEGATIVNO sa potpisom podsekretara/OДБИЈА СЕ NSP,UNSP,DD,UDTNP.docx";
    private static final String OUTPUT_DIR = "generated_templates";
    
    @Autowired
    private EukPredmetRepository predmetRepository;
    
    /**
     * Generiše Word dokument na osnovu template-a i podataka.
     */
    public byte[] generateDocument(OdbijaSeNSPRequestDTO request) throws IOException {
        log.info("Generisanje dokumenta ODBIJA SE NSP za predmet: {}", request.getBrojPredmeta());
        
        // 1. Učitaj template iz resources (radi i u JAR-u i u development-u)
        InputStream templateStream = getClass().getResourceAsStream(TEMPLATE_RESOURCE_PATH);
        if (templateStream == null) {
            throw new IOException("Template fajl nije pronađen: " + TEMPLATE_RESOURCE_PATH);
        }
        
        XWPFDocument document = new XWPFDocument(templateStream);
        
        // 2. Zameni placeholdere
        replacePlaceholders(document, request);
        
        // 3. Ukloni opcione delove teksta
        removeOptionalSections(document, request);
        
        // 4. Konvertuj u byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();
        templateStream.close();
        
        byte[] documentBytes = outputStream.toByteArray();
        
        // 5. Sačuvaj dokument u bazu ako je predmetId prosleđen
        if (request.getPredmetId() != null) {
            saveDocumentToDatabase(documentBytes, request);
        }
        
        log.info("Dokument uspešno generisan");
        return documentBytes;
    }
    
    /**
     * Čuva generisani dokument u bazu podataka.
     */
    private void saveDocumentToDatabase(byte[] documentBytes, OdbijaSeNSPRequestDTO request) {
        try {
            Optional<EukPredmet> predmetOpt = predmetRepository.findById(request.getPredmetId());
            
            if (predmetOpt.isPresent()) {
                EukPredmet predmet = predmetOpt.get();
                
                // Generiši naziv fajla
                String sanitizedBrojPredmeta = request.getBrojPredmeta().replace("/", "-");
                String fileName = String.format("ODBIJA_SE_NSP_%s.docx", sanitizedBrojPredmeta);
                
                // Sačuvaj dokument u predmet
                predmet.setOdbijaSeNspDokument(documentBytes);
                predmet.setOdbijaSeNspDokumentNaziv(fileName);
                predmet.setOdbijaSeNspDokumentDatum(LocalDateTime.now());
                
                predmetRepository.save(predmet);
            } else {
                log.warn("Predmet sa ID {} nije pronađen - dokument nije sačuvan u bazu", request.getPredmetId());
            }
            
        } catch (Exception e) {
            log.error("Greška pri čuvanju dokumenta u bazu za predmet ID: {}", request.getPredmetId(), e);
            // Ne bacaj exception - dokument će svakako biti preuzet
        }
    }
    
    /**
     * Snima generisani dokument na server u generated_templates/ folder.
     */
    private void saveDocumentToServer(byte[] documentBytes, String brojPredmeta) {
        try {
            // Kreiraj output folder ako ne postoji
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            
            // Generiši ime fajla sa timestamp-om
            String timestamp = String.valueOf(System.currentTimeMillis());
            String sanitizedBrojPredmeta = brojPredmeta.replace("/", "-");
            String fileName = String.format("ODBIJA_SE_NSP_%s_%s.docx", sanitizedBrojPredmeta, timestamp);
            String filePath = OUTPUT_DIR + File.separator + fileName;
            
            // Snimi fajl
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(documentBytes);
            }
        } catch (Exception e) {
            log.error("Greška pri snimanju dokumenta na server (ali dokument će biti preuzet)", e);
            // Ne bacam exception jer dokument može da se preuzme i bez snimanja na server
        }
    }
    
    /**
     * Zamenjuje sve placeholdere u dokumentu.
     */
    private void replacePlaceholders(XWPFDocument document, OdbijaSeNSPRequestDTO request) {
        Map<String, String> placeholders = buildPlaceholderMap(request);
        
        // Zameni u header-ima
        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, placeholders);
            }
            for (XWPFTable table : header.getTables()) {
                replacePlaceholdersInTable(table, placeholders);
            }
        }
        
        // Zameni u footer-ima
        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, placeholders);
            }
            for (XWPFTable table : footer.getTables()) {
                replacePlaceholdersInTable(table, placeholders);
            }
        }
        
        // Zameni u paragrafima
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replacePlaceholdersInParagraph(paragraph, placeholders);
        }
        
        // Zameni u tabelama
        for (XWPFTable table : document.getTables()) {
            replacePlaceholdersInTable(table, placeholders);
        }
    }
    
    /**
     * Zamenjuje placeholdere u tabeli.
     */
    private void replacePlaceholdersInTable(XWPFTable table, Map<String, String> placeholders) {
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                // Paragrafi u ćeliji
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    replacePlaceholdersInParagraph(paragraph, placeholders);
                }
                // Ugneždene tabele
                for (XWPFTable nestedTable : cell.getTables()) {
                    replacePlaceholdersInTable(nestedTable, placeholders);
                }
            }
        }
    }
    
    /**
     * Zamenjuje placeholdere u paragrafu - SPAJA SVE RUN-OVE jer Word razbija {{}} na više run-ova.
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> placeholders) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null || runs.isEmpty()) return;
        
        // 1. SAČUVAJ formatiranje iz prvog run-a (koristimo kao template)
        XWPFRun firstRun = runs.get(0);
        boolean originalBold = firstRun.isBold();
        boolean originalItalic = firstRun.isItalic();
        int originalFontSize = firstRun.getFontSize();
        String originalFontFamily = firstRun.getFontFamily();
        
        // 2. SPOJI sve run-ove u jedan tekst
        StringBuilder fullText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                fullText.append(text);
            }
        }
        
        String originalText = fullText.toString();
        if (originalText.isEmpty()) return;
        
        // 3. ZAMENI placeholdere u spojenom tekstu
        String modifiedText = originalText;
        boolean wasModified = false;
        
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            if (modifiedText.contains(entry.getKey())) {
                modifiedText = modifiedText.replace(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
                wasModified = true;
            }
        }
        
        // 4. UKLONI SAMO ZAGRADE {{}} koje NISU {{с.р.}} ili {{в.д.}} - ovi se obrađuju u removeOptionalSections
        String beforeCleanup = modifiedText;
        // Obriši samo zagrade koje nisu {{с.р.}} ili {{в.д.}}
        modifiedText = modifiedText.replaceAll("\\{\\{(?!(с\\.р\\.|в\\.д\\.))[^}]*\\}\\}", "");
        if (!modifiedText.equals(beforeCleanup)) {
            wasModified = true;
        }
        
        // Ako ništa nije zamenjeno, zadrži originalne run-ove
        if (!wasModified) {
            return;
        }
        
        // 5. OBRIŠI sve stare run-ove
        for (int i = runs.size() - 1; i >= 0; i--) {
            paragraph.removeRun(i);
        }
        
        // 6. KREIRAJ jedan novi run sa zamenjenim tekstom i ORIGINALNIM formatiranjem
        XWPFRun newRun = paragraph.createRun();
        newRun.setText(modifiedText);
        
        // Primeni ORIGINALNO formatiranje iz template-a
        if (originalFontFamily != null) {
            newRun.setFontFamily(originalFontFamily);
        } else {
            newRun.setFontFamily("Times New Roman");
        }
        
        if (originalFontSize > 0) {
            newRun.setFontSize(originalFontSize);
        } else {
            newRun.setFontSize(12);
        }
        
        newRun.setBold(originalBold);
        newRun.setItalic(originalItalic);
    }
    
    /**
     * Kopira formatiranje iz jednog run-a u drugi.
     * UVEK postavlja Times New Roman font.
     */
    private void copyRunFormatting(XWPFRun source, XWPFRun target) {
        try {
            // UVEK koristi Times New Roman
            target.setFontFamily("Times New Roman");
            
            int fontSize = source.getFontSize();
            if (fontSize > 0) {
                target.setFontSize(fontSize);
            } else {
                target.setFontSize(12); // Default 12pt
            }
            
            // Čuvaj formatiranje
            target.setBold(source.isBold());
            target.setItalic(source.isItalic());
            target.setStrikeThrough(source.isStrikeThrough());
            if (source.getUnderline() != null) {
                target.setUnderline(source.getUnderline());
            }
            if (source.getColor() != null) {
                target.setColor(source.getColor());
            }
        } catch (Exception e) {
            log.debug("Greška pri kopiranju formatiranja: {}", e.getMessage());
        }
    }
    
    /**
     * Pravi mapu placeholder → vrednost.
     */
    private Map<String, String> buildPlaceholderMap(OdbijaSeNSPRequestDTO request) {
        Map<String, String> map = new HashMap<>();
        
        // Osnovno zaglavlje - SA DONJOM CRTOM (_)
        map.put("{{БРОЈ_ПРЕДМЕТА}}", request.getBrojPredmeta());
        map.put("{{ДАТУМ_ДОНОШЕЊА_РЕШЕЊА}}", formatDatum(request.getDatumDonosenja()));
        map.put("{{БРОЈ_ОВЛАШЋЕЊА}}", request.getBrojOvlascenja());
        map.put("{{ДАТУМ_ОВЛАШЋЕЊА}}", formatDatum(request.getDatumOvlascenja()));
        map.put("{{ИМЕ_И_ПРЕЗИМЕ_ОВЛАШЋЕНОГ_ЛИЦА}}", request.getImeIPrezimeOvlascenog());
        
        // Podaci o podnosiocu - SA DONJOM CRTOM (_)
        map.put("{{ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА}}", request.getImeIPrezimePodnosioca());
        map.put("{{ЈМБГ}}", request.getJmbg());
        map.put("{{ГРАД}}", request.getGrad());
        map.put("{{УЛИЦА}}", request.getUlica());
        map.put("{{БРОЈ_УЛИЦЕ}}", request.getBrojStana());
        map.put("{{ОПШТИНА}}", request.getOpstina());
        map.put("{{ПТТ_БРОЈ}}", request.getPttBroj());
        map.put("{{МЕСТО_СТАНОВАЊА}}", request.getMestoStanovanja());
        map.put("{{МЕСТО}}", request.getMestoStanovanja());
        
        // Kombinovani placeholderi
        map.put("{{УЛИЦА_И_БРОЈ}}", request.getUlica() + " " + request.getBrojStana());
        map.put("{{ГРАД_ОПШТИНА}}", request.getGrad() + ", " + request.getOpstina());
        
        // Obrazloženje - SA DONJOM CRTOM (_)
        map.put("{{ДАТУМ_ПОДНОШЕЊА}}", formatDatum(request.getDatumPodnosenja()));
        map.put("{{БРОЈ_ЧЛАНОВА_ДОМАЋИНСТВА}}", request.getBrojClanovaDomacinstava());
        
        // Mapiranje osnova prava - samo vrednost BEZ "оствареног права на"
        String osnovPravaGenitiv = mapOsnovPravaToGenitiv(request.getOsnovPrava());
        
        // Pun tekst osnova prava sa negacijom - SAMO JEDAN IZBOR
        String osnovPravaPuniTekst = mapOsnovPravaPuniTekst(request.getOsnovPrava());
        
        // ========== PLACEHOLDER 1: Genitiv sa svim opcijama ==========
        // Template: {{оствареног права на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица}}
        // Zameni sa: samo "новчану социјалну помоћ" (BEZ {{}} i BEZ "оствареног права на")
        map.put("{{оствареног права на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица}}", osnovPravaGenitiv);
        
        // ========== PLACEHOLDER 2: Kategorija osnova prava (šalje frontend) ==========
        // Template: {{на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица, односно није корисник права на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица}}
        // Frontend šalje specifičnu kategoriju u kategorijaOsnovaPrava polju
        String kategorijaTekst = request.getKategorijaOsnovaPrava();
        if (kategorijaTekst == null || kategorijaTekst.trim().isEmpty()) {
            // Fallback na staru logiku ako frontend ne pošalje kategoriju
            kategorijaTekst = osnovPravaPuniTekst;
        }
        map.put("{{на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица, односно није корисник права на новчану социјалну помоћ/увећану новчану социјалну помоћ/дечији додатaк/увећани додатак за помоћ и негу другог лица}}", kategorijaTekst);
        
        // Opcioni tekst za službeno pribavljanje dokumenata - BEZ guillemets «»
        String sluzbeniTekst = "Службеним путем, овај орган је у поступку по Захтеву, на основу претходно дате сагласности подносиоца Захтева и člана 103. Закона о општем управном поступку поступку (\"Службени гласник РС\", бр. бр. 18/2016 и 2/2023-одлука УС РС) прибавио доказ о:";
        
        if (Boolean.TRUE.equals(request.getPribavljaDokumentacijuSluzbeno())) {
            // Postavi tekst BEZ {{}}
            map.put("{{СЛУЖБЕНИМ_ПУТЕМ_ТЕКСТ}}", sluzbeniTekst);
            // Zameni i ceo tekst u zagradama (ako postoji u template-u)
            map.put("{{" + sluzbeniTekst + "}}", sluzbeniTekst);
        } else {
            // Kompletno obriši
            map.put("{{СЛУЖБЕНИМ_ПУТЕМ_ТЕКСТ}}", "");
            map.put("{{" + sluzbeniTekst + "}}", "");
        }
        
        // Opcioni tekst za dodatak za pomoć - BEZ guillemets «»
        String dodatakTekst = "Додатак за помоћ и негу другог лица, а које право је остварено по прописима из области социјалне заштите/Новчана накнада за помоћ и негу другог лица а које право је остварено преко РФ ПИО/ није основ за стицање статуса енергетски угроженог купца.";
        
        if (Boolean.TRUE.equals(request.getDodatakZaPomocOdnosiSe())) {
            // Postavi tekst BEZ {{}}
            map.put("{{ДОДАТАК_ЗА_ПОМОЋ_ТЕКСТ}}", dodatakTekst);
            // Zameni i ceo tekst u zagradama (ako postoji u template-u)
            map.put("{{" + dodatakTekst + "}}", dodatakTekst);
        } else {
            // Kompletno obriši
            map.put("{{ДОДАТАК_ЗА_ПОМОЋ_ТЕКСТ}}", "");
            map.put("{{" + dodatakTekst + "}}", "");
        }
        
        return map;
    }
    
    /**
     * Uklanja opcione delove teksta na osnovu boolean vrednosti.
     */
    private void removeOptionalSections(XWPFDocument document, OdbijaSeNSPRequestDTO request) {
        List<XWPFParagraph> paragraphsToRemove = new ArrayList<>();
        
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            String text = paragraph.getText();
            if (text == null || text.isEmpty()) continue;
            
            boolean shouldRemove = false;
            String modifiedText = text;
            
            // 1. Zameni {{в.д.}} sa "в.д." ili obriši
            if (modifiedText.contains("{{в.д.}}")) {
                if (Boolean.TRUE.equals(request.getVrsilacDuznosti())) {
                    modifiedText = modifiedText.replace("{{в.д.}}", "в.д.");
                } else {
                    modifiedText = modifiedText.replace("{{в.д.}} ", "").replace("{{в.д.}}", "");
                }
            }
            
            // 2. Zameni {{с.р.}} sa "с.р." ili obriši
            if (modifiedText.contains("{{с.р.}}")) {
                if (Boolean.TRUE.equals(request.getSopstveneRuke())) {
                    modifiedText = modifiedText.replace("{{с.р.}}", "с.р.");
                } else {
                    modifiedText = modifiedText.replace("{{с.р.}} ", "").replace("{{с.р.}}", "");
                }
            }
            
            // 3. Proveri da li treba ukloniti paragraf o "Додатак за помоћ и негу..."
            if (Boolean.FALSE.equals(request.getDodatakZaPomocOdnosiSe()) 
                && text.contains("Додатак за помоћ и негу другог лица")) {
                shouldRemove = true;
            }
            
            // 4. Proveri da li treba ukloniti paragraf o "Службеним путем..."
            if (Boolean.FALSE.equals(request.getPribavljaDokumentacijuSluzbeno()) 
                && text.contains("Службеним путем, овај орган је")) {
                shouldRemove = true;
            }
            
            // Ažuriraj tekst paragrafa ili ga obeleži za brisanje
            if (shouldRemove) {
                paragraphsToRemove.add(paragraph);
            } else if (!modifiedText.equals(text)) {
                // Obriši sve runs
                while (paragraph.getRuns().size() > 0) {
                    paragraph.removeRun(0);
                }
                // Dodaj novi tekst sa Times New Roman fontom
                XWPFRun run = paragraph.createRun();
                run.setText(modifiedText);
                run.setFontFamily("Times New Roman");
            }
        }
        
        // Obriši označene paragrafe
        for (XWPFParagraph p : paragraphsToRemove) {
            int pos = document.getPosOfParagraph(p);
            if (pos >= 0) {
                document.removeBodyElement(pos);
            }
        }
    }
    
    /**
     * Mapira osnov prava na GENITIV oblik ("оствареног права на...").
     * 
     * @param osnovPrava Skraćenica (NSP/UNSP/DD/UDTNP ili ćirilično НСП/УНСП/ДД/УДТНП)
     * @return Genitiv tekst ("оствареног права на новчану социјалну помоћ")
     */
    private String mapOsnovPravaToGenitiv(String osnovPrava) {
        if (osnovPrava == null || osnovPrava.isEmpty()) return "";
        
        String tekst;
        switch (osnovPrava.toUpperCase()) {
            // Latinični oblici
            case "NSP":
            case "НСП":
                tekst = "новчану социјалну помоћ";
                break;
            case "UNSP":
            case "УНСП":
                tekst = "увећану новчану социјалну помоћ";
                break;
            case "DD":
            case "ДД":
                tekst = "дечији додатaк";
                break;
            case "UDTNP":
            case "UDДНЛ":
            case "УДДНЛ":
            case "УДТНП":
                tekst = "увећани додатак за помоћ и негу другог лица";
                break;
            default:
                log.warn("Nepoznat osnov prava: {}", osnovPrava);
                tekst = osnovPrava;
        }
        
        return tekst;
    }
    
    /**
     * Mapira osnov prava na PUN TEKST sa negacijom.
     * 
     * Vraća samo JEDAN izbor, ne sve sa "/".
     * Primer: "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ"
     * 
     * @param osnovPrava Skraćenica (NSP/UNSP/DD/UDTNP ili ćirilično НСП/УНСП/ДД/УДТНП)
     * @return Pun tekst sa oba dela (pozitivno i negacija) - SAMO JEDAN IZBOR
     */
    private String mapOsnovPravaPuniTekst(String osnovPrava) {
        if (osnovPrava == null || osnovPrava.isEmpty()) return "";
        
        String tekst;
        switch (osnovPrava.toUpperCase()) {
            // Latinični oblici
            case "NSP":
            case "НСП":
                tekst = "новчану социјалну помоћ";
                break;
            case "UNSP":
            case "УНСП":
                tekst = "увећану новчану социјалну помоћ";
                break;
            case "DD":
            case "ДД":
                tekst = "дечији додатaк";
                break;
            case "UDTNP":
            case "UDДНЛ":
            case "УДДНЛ":
            case "УДТНП":
                tekst = "увећани додатак за помоћ и негу другог лица";
                break;
            default:
                log.warn("Nepoznat osnov prava: {}", osnovPrava);
                tekst = osnovPrava;
        }
        
        // Vraća samo jedan izbor, ne sve opcije sa "/"
        return "на " + tekst + ", односно није корисник права на " + tekst;
    }
    
    /**
     * Formatira datum iz yyyy-MM-dd u srpski format (dd.MM.yyyy.)
     */
    private String formatDatum(String datum) {
        if (datum == null || datum.isEmpty()) return "";
        
        try {
            LocalDate date = LocalDate.parse(datum);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
            return date.format(formatter);
        } catch (Exception e) {
            log.error("Greška pri formatiranju datuma: {}", datum, e);
            return datum;
        }
    }
    
    /**
     * Proverava da li je string prazan.
     */
    private boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
}


