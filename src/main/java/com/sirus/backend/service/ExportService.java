package com.sirus.backend.service;

import com.sirus.backend.entity.EukUgrozenoLiceT1;
import com.sirus.backend.entity.EukUgrozenoLiceT2;
import com.sirus.backend.repository.EukUgrozenoLiceT1Repository;
import com.sirus.backend.repository.EukUgrozenoLiceT2Repository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    
    @Autowired
    private EukUgrozenoLiceT1Repository ugrozenoLiceT1Repository;
    
    @Autowired
    private EukUgrozenoLiceT2Repository ugrozenoLiceT2Repository;
    
    // Template lokacije
    private static final String T1_TEMPLATE_PATH = "excelTemplate/ЕУК-T1.xlsx";
    private static final String T2_TEMPLATE_PATH = "excelTemplate/ЕУК-T2.xlsx";
    
    // Template konstante
    private static final int DATA_START_ROW = 10; // Red 10 (indeks 9)
    
    /**
     * Izvoz podataka iz ugrozeno_lice_t1 tabele u Excel sa template-om
     */
    public byte[] exportUgrozenoLiceT1ToExcel() {
        logger.info("Starting export of ugrozeno_lice_t1 to Excel with template");
        
        try {
            // Učitaj template
            ClassPathResource templateResource = new ClassPathResource(T1_TEMPLATE_PATH);
            InputStream templateStream = templateResource.getInputStream();
            Workbook workbook = new XSSFWorkbook(templateStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Dohvati podatke iz baze
            List<EukUgrozenoLiceT1> ugrozenaLica = ugrozenoLiceT1Repository.findAll();
            logger.info("Found {} records to export", ugrozenaLica.size());
            
            // Upisuj podatke počevši od reda 10
            int currentRow = DATA_START_ROW;
            for (EukUgrozenoLiceT1 lice : ugrozenaLica) {
                Row row = sheet.createRow(currentRow);
                populateT1Row(row, lice);
                currentRow++;
            }
            
            // Footer se automatski pomera naniže jer dodajemo nove redove
            logger.info("Successfully exported {} records to Excel", ugrozenaLica.size());
            
            // Konvertuj u byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            templateStream.close();
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error exporting ugrozeno_lice_t1 to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Greška pri izvozu podataka: " + e.getMessage(), e);
        }
    }
    
    /**
     * Izvoz podataka iz ugrozeno_lice_t2 tabele u Excel sa template-om
     */
    public byte[] exportUgrozenoLiceT2ToExcel() {
        logger.info("Starting export of ugrozeno_lice_t2 to Excel with template");
        
        try {
            // Učitaj template
            ClassPathResource templateResource = new ClassPathResource(T2_TEMPLATE_PATH);
            InputStream templateStream = templateResource.getInputStream();
            Workbook workbook = new XSSFWorkbook(templateStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Dohvati podatke iz baze
            List<EukUgrozenoLiceT2> ugrozenaLica = ugrozenoLiceT2Repository.findAll();
            logger.info("Found {} records to export", ugrozenaLica.size());
            
            // Upisuj podatke počevši od reda 10
            int currentRow = DATA_START_ROW;
            for (EukUgrozenoLiceT2 lice : ugrozenaLica) {
                Row row = sheet.createRow(currentRow);
                populateT2Row(row, lice);
                currentRow++;
            }
            
            // Footer se automatski pomera naniže jer dodajemo nove redove
            logger.info("Successfully exported {} records to Excel", ugrozenaLica.size());
            
            // Konvertuj u byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            templateStream.close();
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            logger.error("Error exporting ugrozeno_lice_t2 to Excel: {}", e.getMessage(), e);
            throw new RuntimeException("Greška pri izvozu podataka: " + e.getMessage(), e);
        }
    }
    
    /**
     * Popunjava red sa podacima iz EukUgrozenoLiceT1 entiteta
     * Mapiranje prema template-u:
     * A10 - Redni broj, B10 - Ime, C10 - Prezime, D10 - JMBG, E10 - PTT broj
     * F10 - Grad/Opština, G10 - Mesto, H10 - Ulica i broj, I10 - Broj članova domaćinstva
     * J10 - Osnov sticanja statusa, K10 - ED broj, L10 - Potrošnja i površina
     * M10 - Iznos umanjenja sa PDV, N10 - Broj računa, O10 - Datum izdavanja računa
     */
    private void populateT1Row(Row row, EukUgrozenoLiceT1 lice) {
        // A10 - Redni broj
        setCellValue(row, 0, lice.getRedniBroj());
        
        // B10 - Ime
        setCellValue(row, 1, lice.getIme());
        
        // C10 - Prezime
        setCellValue(row, 2, lice.getPrezime());
        
        // D10 - JMBG
        setCellValue(row, 3, lice.getJmbg());
        
        // E10 - PTT broj
        setCellValue(row, 4, lice.getPttBroj());
        
        // F10 - Grad/Opština
        setCellValue(row, 5, lice.getGradOpstina());
        
        // G10 - Mesto
        setCellValue(row, 6, lice.getMesto());
        
        // H10 - Ulica i broj
        setCellValue(row, 7, lice.getUlicaIBroj());
        
        // I10 - Broj članova domaćinstva
        setCellValue(row, 8, lice.getBrojClanovaDomacinstva());
        
        // J10 - Osnov sticanja statusa
        setCellValue(row, 9, lice.getOsnovSticanjaStatusa());
        
        // K10 - ED broj/broj mernog uređaja
        setCellValue(row, 10, lice.getEdBrojBrojMernogUredjaja());
        
        // L10 - Potrošnja i površina kombinovano
        setCellValue(row, 11, lice.getPotrosnjaIPovrsinaCombined());
        
        // M10 - Iznos umanjenja sa PDV
        setCellValue(row, 12, lice.getIznosUmanjenjaSaPdv());
        
        // N10 - Broj računa
        setCellValue(row, 13, lice.getBrojRacuna());
        
        // O10 - Datum izdavanja računa
        setCellValue(row, 14, formatDate(lice.getDatumIzdavanjaRacuna()));
    }
    
    /**
     * Popunjava red sa podacima iz EukUgrozenoLiceT2 entiteta
     */
    private void populateT2Row(Row row, EukUgrozenoLiceT2 lice) {
        // Kolona A (0) - Redni broj
        setCellValue(row, 0, lice.getRedniBroj());
        
        // Kolona B (1) - Ime
        setCellValue(row, 1, lice.getIme());
        
        // Kolona C (2) - Prezime
        setCellValue(row, 2, lice.getPrezime());
        
        // Kolona D (3) - JMBG
        setCellValue(row, 3, lice.getJmbg());
        
        // Kolona E (4) - PTT broj
        setCellValue(row, 4, lice.getPttBroj());
        
        // Kolona F (5) - Grad/Opština
        setCellValue(row, 5, lice.getGradOpstina());
        
        // Kolona G (6) - Mesto
        setCellValue(row, 6, lice.getMesto());
        
        // Kolona H (7) - Ulica i broj
        setCellValue(row, 7, lice.getUlicaIBroj());
        
        // Kolona I (8) - ED broj
        setCellValue(row, 8, lice.getEdBroj());
        
        // Kolona J (9) - Period važenja rešenja o statusu
        setCellValue(row, 9, lice.getPokVazenjaResenjaOStatusu());
        
        // Ostale kolone ostaju prazne jer T2 entitet nema te polja
        // Kolona K (10) - Prazno
        setCellValue(row, 10, "");
        
        // Kolona L (11) - Prazno
        setCellValue(row, 11, "");
        
        // Kolona M (12) - Prazno
        setCellValue(row, 12, "");
        
        // Kolona N (13) - Prazno
        setCellValue(row, 13, "");
        
        // Kolona O (14) - Prazno
        setCellValue(row, 14, "");
    }
    
    /**
     * Postavlja vrednost ćelije sa proverom tipa
     */
    private void setCellValue(Row row, int columnIndex, Object value) {
        Cell cell = row.createCell(columnIndex);
        
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof java.math.BigDecimal) {
            cell.setCellValue(((java.math.BigDecimal) value).doubleValue());
        } else if (value instanceof LocalDate) {
            cell.setCellValue(((LocalDate) value).toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    /**
     * Formatira datum za Excel
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
