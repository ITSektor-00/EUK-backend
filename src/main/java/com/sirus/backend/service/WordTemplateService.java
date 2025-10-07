package com.sirus.backend.service;

import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.dto.EukUgrozenoLiceT2Dto;
import com.sirus.backend.dto.TemplateGenerationRequestDto;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Section;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WordTemplateService {
    
    private static final String TEMPLATE_DIR = "src/main/resources/obrasci";
    private static final String OUTPUT_DIR = "generated_templates";
    
    /**
     * Generiše Word dokument na osnovu template-a i podataka
     */
    public String generateWordDocument(TemplateGenerationRequestDto request, 
                                     EukUgrozenoLiceT1Dto liceT1, 
                                     EukUgrozenoLiceT2Dto liceT2,
                                     Map<String, String> manualData) throws Exception {
        
        // Određivanje template fajla na osnovu obrasci_vrste i organizaciona_struktura
        String templatePath = determineTemplatePath(request);
        
        // Provera da li je .doc ili .docx fajl
        if (templatePath.toLowerCase().endsWith(".doc")) {
            return generateDocDocument(templatePath, request, liceT1, liceT2, manualData);
        } else {
            return generateDocxDocument(templatePath, request, liceT1, liceT2, manualData);
        }
    }
    
    /**
     * Generiše .doc dokument
     */
    private String generateDocDocument(String templatePath, TemplateGenerationRequestDto request,
                                     EukUgrozenoLiceT1Dto liceT1, EukUgrozenoLiceT2Dto liceT2,
                                     Map<String, String> manualData) throws Exception {
        
        // Čitanje template dokumenta
        FileInputStream templateStream = new FileInputStream(templatePath);
        HWPFDocument document = new HWPFDocument(templateStream);
        
        // Priprema podataka za zamenu - koristi specijalnu logiku za OДБИЈА СЕ template
        Map<String, String> replacementData = prepareReplacementDataForOdbijaSe(request, liceT1, liceT2, manualData);
        
        // Zamena crvenih slova sa podacima - koristi specijalnu logiku za OДБИЈА СЕ template
        replaceOdbijaSeTemplateText(document, replacementData);
        
        // Brisanje komentara iz template-a
        removeCommentsFromDocument(document);
        
        // Generisanje output fajla
        String outputFileName = generateOutputFileName(request);
        String outputPath = OUTPUT_DIR + File.separator + outputFileName;
        
        // Kreiranje direktorijuma ako ne postoji
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        // Čuvanje generisanog dokumenta
        FileOutputStream outputStream = new FileOutputStream(outputPath);
        document.write(outputStream);
        document.close();
        outputStream.close();
        templateStream.close();
        
        return outputPath;
    }
    
    /**
     * Generiše .docx dokument
     */
    private String generateDocxDocument(String templatePath, TemplateGenerationRequestDto request,
                                       EukUgrozenoLiceT1Dto liceT1, EukUgrozenoLiceT2Dto liceT2,
                                       Map<String, String> manualData) throws Exception {
        
        // Čitanje template dokumenta
        FileInputStream templateStream = new FileInputStream(templatePath);
        XWPFDocument document = new XWPFDocument(templateStream);
        
        // Priprema podataka za zamenu
        Map<String, String> replacementData = prepareReplacementData(request, liceT1, liceT2, manualData);
        
        // Zamena crvenih slova sa podacima
        replaceRedTextWithDataDocx(document, replacementData);
        
        // Generisanje output fajla
        String outputFileName = generateOutputFileName(request);
        String outputPath = OUTPUT_DIR + File.separator + outputFileName;
        
        // Kreiranje direktorijuma ako ne postoji
        Files.createDirectories(Paths.get(OUTPUT_DIR));
        
        // Čuvanje generisanog dokumenta
        FileOutputStream outputStream = new FileOutputStream(outputPath);
        document.write(outputStream);
        document.close();
        outputStream.close();
        templateStream.close();
        
        return outputPath;
    }
    
    /**
     * Određuje putanju template fajla na osnovu obrasci_vrste i organizaciona_struktura
     */
    private String determineTemplatePath(TemplateGenerationRequestDto request) {
        // Ovo će biti implementirano na osnovu vaših potreba
        // Za sada koristim postojeći template
        return TEMPLATE_DIR + File.separator + "obrasci NEGATIVNO sa potpisom podsekretara" + 
               File.separator + "OДБИЈА СЕ NSP,UNSP,DD,UDTNP.doc";
    }
    
    /**
     * Priprema podatke za zamenu u template-u
     */
    private Map<String, String> prepareReplacementData(TemplateGenerationRequestDto request,
                                                      EukUgrozenoLiceT1Dto liceT1,
                                                      EukUgrozenoLiceT2Dto liceT2,
                                                      Map<String, String> manualData) {
        Map<String, String> data = new HashMap<>();
        
        // Podaci iz T1 tabele - pravi podaci za rešenje
        if (liceT1 != null) {
            data.put("IME", liceT1.getIme() != null ? liceT1.getIme() : "");
            data.put("PREZIME", liceT1.getPrezime() != null ? liceT1.getPrezime() : "");
            data.put("JMBG", liceT1.getJmbg() != null ? liceT1.getJmbg() : "");
            data.put("ADRESA", buildAddress(liceT1));
            data.put("ED_BROJ", liceT1.getEdBrojBrojMernogUredjaja() != null ? liceT1.getEdBrojBrojMernogUredjaja() : "");
            data.put("POTROSNJA", liceT1.getPotrosnjaKwh() != null ? liceT1.getPotrosnjaKwh().toString() : "");
            data.put("POVRSINA", liceT1.getZagrevanaPovrsinaM2() != null ? liceT1.getZagrevanaPovrsinaM2().toString() : "");
            data.put("BROJ_RACUNA", liceT1.getBrojRacuna() != null ? liceT1.getBrojRacuna() : "");
            data.put("DATUM_RACUNA", liceT1.getDatumIzdavanjaRacuna() != null ? liceT1.getDatumIzdavanjaRacuna().toString() : "");
            data.put("DATUM_TRAJANJA", liceT1.getDatumTrajanjaPrava() != null ? liceT1.getDatumTrajanjaPrava().toString() : "");
            data.put("IZNOS", liceT1.getIznosUmanjenjaSaPdv() != null ? liceT1.getIznosUmanjenjaSaPdv().toString() : "");
        }
        
        // Podaci iz T2 tabele
        if (liceT2 != null) {
            data.put("IME", liceT2.getIme() != null ? liceT2.getIme() : "");
            data.put("PREZIME", liceT2.getPrezime() != null ? liceT2.getPrezime() : "");
            data.put("JMBG", liceT2.getJmbg() != null ? liceT2.getJmbg() : "");
            data.put("ADRESA", buildAddress(liceT2));
            data.put("ED_BROJ", liceT2.getEdBroj() != null ? liceT2.getEdBroj() : "");
        }
        
        // Ručno uneti podaci (zaglavlje, obrazloženje, dodatni podaci)
        if (manualData != null) {
            data.putAll(manualData);
        }
        
        // Sistemski podaci
        data.put("DATUM_GENERISANJA", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        data.put("DATUM_GENERISANJA_FULL", LocalDate.now().format(DateTimeFormatter.ofPattern("dd. MMMM yyyy.")));
        
        return data;
    }
    
    /**
     * Priprema podatke za zamenu u template-u za rešenje OДБИЈА СЕ NSP,UNSP,DD,UDTNP
     */
    private Map<String, String> prepareReplacementDataForOdbijaSe(TemplateGenerationRequestDto request,
                                                                 EukUgrozenoLiceT1Dto liceT1,
                                                                 EukUgrozenoLiceT2Dto liceT2,
                                                                 Map<String, String> manualData) {
        Map<String, String> data = new HashMap<>();
        
        // Ručno uneti podaci iz frontend-a
        if (manualData != null) {
            // Osnovni podaci
            data.put("PREDMET", manualData.getOrDefault("PREDMET", ""));
            data.put("DATUM_DONOSENJA_RESENJA", manualData.getOrDefault("DATUM_DONOSENJA_RESENJA", ""));
            data.put("BROJ_RESENJA", manualData.getOrDefault("BROJ_RESENJA", ""));
            data.put("DATUM_OVLASTENJA", manualData.getOrDefault("DATUM_OVLASTENJA", ""));
            data.put("DATUM_PODNOSENJA", manualData.getOrDefault("DATUM_PODNOSENJA", ""));
            data.put("DODATNI_TEKST", manualData.getOrDefault("DODATNI_TEKST", ""));
            
            // Podaci iz baze (lice iz T1 ili T2)
            if (liceT1 != null) {
                data.put("IME_PREZIME_LICA", (liceT1.getIme() != null ? liceT1.getIme() : "") + 
                         (liceT1.getPrezime() != null ? " " + liceT1.getPrezime() : ""));
                data.put("ULICA_I_BROJ", liceT1.getUlicaIBroj() != null ? liceT1.getUlicaIBroj() : "");
                data.put("IME_PREZIME_PRAVNOG_LICA", (liceT1.getIme() != null ? liceT1.getIme() : "") + 
                         (liceT1.getPrezime() != null ? " " + liceT1.getPrezime() : ""));
                data.put("JMBG_PRAVNOG_LICA", liceT1.getJmbg() != null ? liceT1.getJmbg() : "");
                data.put("ADRESA_PRAVNOG_LICA", buildFullAddress(liceT1));
                data.put("IME_PREZIME_PODNOSIOCA", (liceT1.getIme() != null ? liceT1.getIme() : "") + 
                         (liceT1.getPrezime() != null ? " " + liceT1.getPrezime() : ""));
                data.put("JMBG_PODNOSIOCA", liceT1.getJmbg() != null ? liceT1.getJmbg() : "");
                data.put("ADRESA_PODNOSIOCA", buildFullAddress(liceT1));
            } else if (liceT2 != null) {
                data.put("IME_PREZIME_LICA", (liceT2.getIme() != null ? liceT2.getIme() : "") + 
                         (liceT2.getPrezime() != null ? " " + liceT2.getPrezime() : ""));
                data.put("ULICA_I_BROJ", liceT2.getUlicaIBroj() != null ? liceT2.getUlicaIBroj() : "");
                data.put("IME_PREZIME_PRAVNOG_LICA", (liceT2.getIme() != null ? liceT2.getIme() : "") + 
                         (liceT2.getPrezime() != null ? " " + liceT2.getPrezime() : ""));
                data.put("JMBG_PRAVNOG_LICA", liceT2.getJmbg() != null ? liceT2.getJmbg() : "");
                data.put("ADRESA_PRAVNOG_LICA", buildFullAddress(liceT2));
                data.put("IME_PREZIME_PODNOSIOCA", (liceT2.getIme() != null ? liceT2.getIme() : "") + 
                         (liceT2.getPrezime() != null ? " " + liceT2.getPrezime() : ""));
                data.put("JMBG_PODNOSIOCA", liceT2.getJmbg() != null ? liceT2.getJmbg() : "");
                data.put("ADRESA_PODNOSIOCA", buildFullAddress(liceT2));
            }
            
            // Logički podaci
            Boolean pribavljaDokumentaciju = Boolean.valueOf(manualData.getOrDefault("PRIBAVLJA_DOKUMENTACIJU", "false"));
            Boolean vdPotpis = Boolean.valueOf(manualData.getOrDefault("VD_POTPIS", "false"));
            Boolean srPotpis = Boolean.valueOf(manualData.getOrDefault("SR_POTPIS", "false"));
            
            // Tekst za pribavljanje dokumentacije
            if (pribavljaDokumentaciju) {
                data.put("PRIBAVLJA_DOKUMENTACIJU_TEKST", 
                    "Службеним путем, овај орган је у поступку по Захтеву, на основу претходно дате сагласности подносиоца Захтева и члана 103. Закона о општем управном поступку поступку („Службени гласник РС\", бр. бр. 18/2016 и 2/2023-одлука УС РС) прибавио доказ о:");
            } else {
                data.put("PRIBAVLJA_DOKUMENTACIJU_TEKST", "");
            }
            
            // Tekst za в.д. potpis
            if (vdPotpis) {
                data.put("VD_POTPIS_TEKST", "в.д.");
            } else {
                data.put("VD_POTPIS_TEKST", "");
            }
            
            // Tekst za с.р. potpis
            if (srPotpis) {
                data.put("SR_POTPIS_TEKST", "с.р.");
            } else {
                data.put("SR_POTPIS_TEKST", "");
            }
        }
        
        return data;
    }
    
    /**
     * Gradi kompletnu adresu od podataka iz T1 ili T2
     */
    private String buildFullAddress(Object lice) {
        StringBuilder address = new StringBuilder();
        
        if (lice instanceof EukUgrozenoLiceT1Dto) {
            EukUgrozenoLiceT1Dto t1 = (EukUgrozenoLiceT1Dto) lice;
            if (t1.getGradOpstina() != null) address.append(t1.getGradOpstina());
            if (t1.getUlicaIBroj() != null) address.append(", ").append(t1.getUlicaIBroj());
            if (t1.getMesto() != null) address.append(", ").append(t1.getMesto());
            if (t1.getPttBroj() != null) address.append(", ").append(t1.getPttBroj());
        } else if (lice instanceof EukUgrozenoLiceT2Dto) {
            EukUgrozenoLiceT2Dto t2 = (EukUgrozenoLiceT2Dto) lice;
            if (t2.getGradOpstina() != null) address.append(t2.getGradOpstina());
            if (t2.getUlicaIBroj() != null) address.append(", ").append(t2.getUlicaIBroj());
            if (t2.getMesto() != null) address.append(", ").append(t2.getMesto());
            if (t2.getPttBroj() != null) address.append(", ").append(t2.getPttBroj());
        }
        
        return address.toString();
    }
    
    /**
     * Gradi adresu od podataka iz T1 ili T2
     */
    private String buildAddress(Object lice) {
        StringBuilder address = new StringBuilder();
        
        if (lice instanceof EukUgrozenoLiceT1Dto) {
            EukUgrozenoLiceT1Dto t1 = (EukUgrozenoLiceT1Dto) lice;
            if (t1.getUlicaIBroj() != null) address.append(t1.getUlicaIBroj());
            if (t1.getPttBroj() != null) address.append(", ").append(t1.getPttBroj());
            if (t1.getMesto() != null) address.append(" ").append(t1.getMesto());
            if (t1.getGradOpstina() != null) address.append(", ").append(t1.getGradOpstina());
        } else if (lice instanceof EukUgrozenoLiceT2Dto) {
            EukUgrozenoLiceT2Dto t2 = (EukUgrozenoLiceT2Dto) lice;
            if (t2.getUlicaIBroj() != null) address.append(t2.getUlicaIBroj());
            if (t2.getPttBroj() != null) address.append(", ").append(t2.getPttBroj());
            if (t2.getMesto() != null) address.append(" ").append(t2.getMesto());
            if (t2.getGradOpstina() != null) address.append(", ").append(t2.getGradOpstina());
        }
        
        return address.toString();
    }
    
    /**
     * Zamenjuje crvena slova sa podacima u .doc dokumentu
     */
    private void replaceRedTextWithDataDoc(HWPFDocument document, Map<String, String> replacementData) {
        Range range = document.getRange();
        
        // Prolazak kroz sve sekcije
        for (int i = 0; i < range.numSections(); i++) {
            Section section = range.getSection(i);
            for (int j = 0; j < section.numParagraphs(); j++) {
                Paragraph paragraph = section.getParagraph(j);
                for (int k = 0; k < paragraph.numCharacterRuns(); k++) {
                    CharacterRun run = paragraph.getCharacterRun(k);
                    String text = run.text();
                    if (text != null) {
                        // Proverava da li je crven tekst ili sadrži crvena slova
                        if (isRedTextDoc(run) || containsRedTextPattern(text)) {
                            String newText = replaceRedTextAndLines(text, replacementData);
                            if (!newText.equals(text)) {
                                run.replaceText(text, newText);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Specijalna logika za zamenu teksta u OДБИЈА СЕ template-u
     */
    private void replaceOdbijaSeTemplateText(HWPFDocument document, Map<String, String> replacementData) {
        Range range = document.getRange();
        
        // Prolazak kroz sve sekcije
        for (int i = 0; i < range.numSections(); i++) {
            Section section = range.getSection(i);
            for (int j = 0; j < section.numParagraphs(); j++) {
                Paragraph paragraph = section.getParagraph(j);
                for (int k = 0; k < paragraph.numCharacterRuns(); k++) {
                    CharacterRun run = paragraph.getCharacterRun(k);
                    String text = run.text();
                    if (text != null) {
                        String newText = replaceOdbijaSePatterns(text, replacementData);
                        if (!newText.equals(text)) {
                            run.replaceText(text, newText);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Zamenjuje specifične pattern-e za OДБИЈА СЕ template
     */
    private String replaceOdbijaSePatterns(String text, Map<String, String> replacementData) {
        if (text == null) return text;
        
        String result = text;
        
        // Zamena specifičnih pattern-a
        result = result.replaceAll("\\(предмета\\)_+", replacementData.getOrDefault("PREDMET", ""));
        result = result.replaceAll("\\(доношења решења\\)_+године", replacementData.getOrDefault("DATUM_DONOSENJA_RESENJA", "") + " године");
        result = result.replaceAll("\\(број\\)_+", replacementData.getOrDefault("BROJ_RESENJA", ""));
        result = result.replaceAll("\\(датум овлашћења\\)_+", replacementData.getOrDefault("DATUM_OVLASTENJA", ""));
        result = result.replaceAll("\\(име и презиме\\)_+", replacementData.getOrDefault("IME_PREZIME_LICA", ""));
        result = result.replaceAll("Ул\\.\\s*_+\\s*бр\\.\\s*_+", "Ул. " + replacementData.getOrDefault("ULICA_I_BROJ", "") + " бр. " + replacementData.getOrDefault("ULICA_I_BROJ", ""));
        result = result.replaceAll("\\(име и презиме\\)_+", replacementData.getOrDefault("IME_PREZIME_PRAVNOG_LICA", ""));
        result = result.replaceAll("ЈМБГ\\s*_+", "ЈМБГ " + replacementData.getOrDefault("JMBG_PRAVNOG_LICA", ""));
        result = result.replaceAll("_+\\(град, улица и број, општина, ПТТ број\\)", replacementData.getOrDefault("ADRESA_PRAVNOG_LICA", ""));
        result = result.replaceAll("_+\\(датум подношења\\)", replacementData.getOrDefault("DATUM_PODNOSENJA", ""));
        result = result.replaceAll("\\(име и презиме\\)_+", replacementData.getOrDefault("IME_PREZIME_PODNOSIOCA", ""));
        result = result.replaceAll("ЈМБГ\\s*_+", "ЈМБГ " + replacementData.getOrDefault("JMBG_PODNOSIOCA", ""));
        result = result.replaceAll("из\\s*_+,Ул\\.\\s*_+\\s*бр\\.\\s*_+", "из " + replacementData.getOrDefault("ADRESA_PODNOSIOCA", "") + ", Ул. " + replacementData.getOrDefault("ADRESA_PODNOSIOCA", "") + " бр. " + replacementData.getOrDefault("ADRESA_PODNOSIOCA", ""));
        
        // Zamena logičkih tekstova
        if (Boolean.valueOf(replacementData.getOrDefault("PRIBAVLJA_DOKUMENTACIJU", "false"))) {
            result = result.replaceAll("Службеним путем, овај орган је у поступку по Захтеву, на основу претходно дате сагласности подносиоца Захтева и члана 103\\. Закона о општем управном поступку поступку.*?прибавио доказ о:", 
                replacementData.getOrDefault("PRIBAVLJA_DOKUMENTACIJU_TEKST", ""));
        } else {
            result = result.replaceAll("Службеним путем, овај орган је у поступку по Захтеву, на основу претходно дате сагласности подносиоца Захтева и члана 103\\. Закона о општем управном поступку поступку.*?прибавио доказ о:", "");
        }
        
        // Zamena в.д. teksta - ako je vdPotpis true, ostavi "в.д.", ako je false, obriši
        if (Boolean.valueOf(replacementData.getOrDefault("VD_POTPIS", "false"))) {
            // Ostavi "в.д." u tekstu
            result = result.replaceAll("в\\.д\\.", "в.д.");
        } else {
            // Obriši "в.д." iz teksta
            result = result.replaceAll("в\\.д\\.", "");
        }
        
        // Zamena с.р. teksta - ako je srPotpis true, ostavi "с.р.", ako je false, obriši
        if (Boolean.valueOf(replacementData.getOrDefault("SR_POTPIS", "false"))) {
            // Ostavi "с.р." u tekstu
            result = result.replaceAll("с\\.р\\.", "с.р.");
        } else {
            // Obriši "с.р." iz teksta
            result = result.replaceAll("с\\.р\\.", "");
        }
        
        // Dodavanje dodatnog teksta
        if (replacementData.containsKey("DODATNI_TEKST") && !replacementData.get("DODATNI_TEKST").isEmpty()) {
            result = result.replaceAll("Оставити простор да се, уколико има потребе, убаци још неки део текста", 
                replacementData.get("DODATNI_TEKST"));
        } else {
            result = result.replaceAll("Оставити простор да се, уколико има потребе, убаци још неки део текста", "\n\n");
        }
        
        return result;
    }
    
    /**
     * Zamenjuje crvena slova sa podacima u .docx dokumentu
     */
    private void replaceRedTextWithDataDocx(XWPFDocument document, Map<String, String> replacementData) {
        // Prolazak kroz sve paragraphe
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceInParagraph(paragraph, replacementData);
        }
        
        // Prolazak kroz sve tabele
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph cellParagraph : cell.getParagraphs()) {
                        replaceInParagraph(cellParagraph, replacementData);
                    }
                }
            }
        }
    }
    
    /**
     * Zamenjuje tekst u paragrafu
     */
    private void replaceInParagraph(XWPFParagraph paragraph, Map<String, String> replacementData) {
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                // Provera da li je tekst crven (simplified - u realnoj implementaciji bi trebalo proveriti boju)
                if (isRedText(run)) {
                    // Pronalaženje pattern-a za zamenu (crvena slova + linija)
                    String newText = replaceRedTextPattern(text, replacementData);
                    run.setText(newText, 0);
                }
            }
        }
    }
    
    /**
     * Proverava da li je tekst crven u .doc fajlu (simplified implementacija)
     */
    private boolean isRedTextDoc(CharacterRun run) {
        // U realnoj implementaciji bi trebalo proveriti boju teksta
        // Za sada koristim pattern matching
        String text = run.text();
        return text != null && (text.contains("___") || text.matches(".*[A-Z]{2,}.*"));
    }
    
    /**
     * Proverava da li tekst sadrži pattern crvenih slova
     */
    private boolean containsRedTextPattern(String text) {
        if (text == null) return false;
        
        // Pattern za crvena slova (velika slova sa linijama)
        return text.matches(".*[A-Z]{2,}.*") || 
               text.contains("___") || 
               text.matches(".*[A-Z]+\\s*_+.*") ||
               text.matches(".*_+\\s*[A-Z]+.*");
    }
    
    /**
     * Zamenjuje crvena slova i linije sa podacima
     */
    private String replaceRedTextAndLines(String text, Map<String, String> replacementData) {
        if (text == null) return text;
        
        // Pattern za pronalaženje crvenih slova sa linijama
        // Traži velika slova (možda sa linijama) i zamenjuje ih sa podacima
        Pattern redTextPattern = Pattern.compile("([A-Z]{2,})\\s*_+");
        Matcher matcher = redTextPattern.matcher(text);
        
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = replacementData.getOrDefault(placeholder, "");
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        
        // Pattern za linije koje nisu uz crvena slova
        Pattern linePattern = Pattern.compile("_+");
        String finalResult = linePattern.matcher(result.toString()).replaceAll("");
        
        return finalResult;
    }
    
    /**
     * Briše komentare iz dokumenta
     */
    private void removeCommentsFromDocument(HWPFDocument document) {
        Range range = document.getRange();
        
        // Prolazak kroz sve sekcije
        for (int i = 0; i < range.numSections(); i++) {
            Section section = range.getSection(i);
            for (int j = 0; j < section.numParagraphs(); j++) {
                Paragraph paragraph = section.getParagraph(j);
                for (int k = 0; k < paragraph.numCharacterRuns(); k++) {
                    CharacterRun run = paragraph.getCharacterRun(k);
                    String text = run.text();
                    if (text != null && isComment(text)) {
                        run.replaceText(text, "");
                    }
                }
            }
        }
    }
    
    /**
     * Proverava da li je tekst komentar
     */
    private boolean isComment(String text) {
        if (text == null) return false;
        
        // Pattern za komentare (obično u zagradama ili sa //)
        return text.trim().startsWith("//") || 
               text.trim().startsWith("/*") || 
               text.trim().startsWith("<!--") ||
               text.trim().startsWith("(") && text.trim().endsWith(")") ||
               text.trim().startsWith("[") && text.trim().endsWith("]");
    }
    
    /**
     * Proverava da li je tekst crven u .docx fajlu (simplified implementacija)
     */
    private boolean isRedText(XWPFRun run) {
        // U realnoj implementaciji bi trebalo proveriti boju teksta
        // Za sada koristim pattern matching
        String text = run.getText(0);
        return text != null && (text.contains("___") || text.matches(".*[A-Z]{2,}.*"));
    }
    
    /**
     * Zamenjuje pattern crvenih slova sa podacima
     */
    private String replaceRedTextPattern(String text, Map<String, String> replacementData) {
        // Pattern za pronalaženje crvenih slova sa linijama
        Pattern pattern = Pattern.compile("([A-Z_]+)\\s*_+");
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String placeholder = matcher.group(1);
            String replacement = replacementData.getOrDefault(placeholder, placeholder);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Generiše ime output fajla
     */
    private String generateOutputFileName(TemplateGenerationRequestDto request) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        return "resenje_" + request.getPredmetId() + "_" + timestamp + ".doc";
    }
}
