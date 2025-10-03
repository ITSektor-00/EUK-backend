package com.sirus.backend.service;

import com.sirus.backend.entity.EukUgrozenoLiceT1;
import com.sirus.backend.repository.EukUgrozenoLiceT1Repository;
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
public class ExcelExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelExportService.class);
    private static final String TEMPLATE_PATH = "excelTemplate/ЕУК-T1.xlsx";
    private static final int DATA_START_ROW = 9; // A10 je red index 9 (0-based)
    
    // Cache za stilove (reuse umesto kreiranje novih)
    private CellStyle[] templateStyles;
    
    @Autowired
    private EukUgrozenoLiceT1Repository ugrozenoLiceT1Repository;
    
    /**
     * Generiše Excel fajl sa svim podacima
     */
    public byte[] generateExcelWithAllData() throws Exception {
        logger.info("Starting Excel export with all data");
        
        // Učitaj sve podatke
        List<EukUgrozenoLiceT1> allData = ugrozenoLiceT1Repository.findAll();
        logger.info("Loaded {} records for export", allData.size());
        
        return generateExcel(allData);
    }
    
    /**
     * Generiše Excel fajl sa filterovanim podacima
     */
    public byte[] generateExcelWithFilteredData(List<Integer> ids) throws Exception {
        logger.info("Starting Excel export with {} filtered records", ids.size());
        
        List<EukUgrozenoLiceT1> filteredData = ugrozenoLiceT1Repository.findAllById(ids);
        logger.info("Loaded {} records for export", filteredData.size());
        
        return generateExcel(filteredData);
    }
    
    /**
     * Glavna metoda za generisanje Excel-a
     */
    private byte[] generateExcel(List<EukUgrozenoLiceT1> data) throws Exception {
        // Učitaj template
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        InputStream templateStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(templateStream);
        Sheet sheet = workbook.getSheetAt(0);
        
        logger.info("Template loaded successfully");
        
        // Keširaj stilove iz template reda (A10)
        Row templateRow = sheet.getRow(DATA_START_ROW);
        cacheTemplateStyles(templateRow);
        
        // Upiši podatke
        int currentRow = DATA_START_ROW;
        for (int i = 0; i < data.size(); i++) {
            EukUgrozenoLiceT1 entity = data.get(i);
            Row row = sheet.getRow(currentRow);
            
            // Ako red ne postoji, kreiraj ga
            if (row == null) {
                row = sheet.createRow(currentRow);
            }
            
            // Primeni keširane stilove
            if (currentRow > DATA_START_ROW) {
                applyTemplateStyles(row);
            }
            
            // Popuni podatke
            fillRowData(row, entity, i + 1); // i+1 jer je redni broj od 1
            currentRow++;
        }
        
        logger.info("Data written to {} rows", data.size());
        
        // Obriši sve redove nakon podataka
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum >= currentRow) {
            logger.info("Clearing rows from {} to {}", currentRow, lastRowNum);
            for (int i = currentRow; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    sheet.removeRow(row);
                }
            }
            logger.info("Cleared {} rows after data", (lastRowNum - currentRow + 1));
        }
        
        // Konvertuj workbook u byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        logger.info("Excel export completed successfully");
        return outputStream.toByteArray();
    }
    
    /**
     * Keširaj stilove iz template reda
     */
    private void cacheTemplateStyles(Row templateRow) {
        int cellCount = templateRow.getLastCellNum();
        templateStyles = new CellStyle[cellCount];
        
        for (int i = 0; i < cellCount; i++) {
            Cell cell = templateRow.getCell(i);
            if (cell != null) {
                templateStyles[i] = cell.getCellStyle();
            }
        }
        
        logger.info("Cached {} cell styles from template", cellCount);
    }
    
    /**
     * Primeni keširane stilove na red
     */
    private void applyTemplateStyles(Row targetRow) {
        if (templateStyles == null) return;
        
        for (int i = 0; i < templateStyles.length; i++) {
            if (templateStyles[i] != null) {
                Cell cell = targetRow.getCell(i);
                if (cell == null) {
                    cell = targetRow.createCell(i);
                }
                cell.setCellStyle(templateStyles[i]);
            }
        }
    }
    
    /**
     * Popunjava jedan red podacima
     */
    private void fillRowData(Row row, EukUgrozenoLiceT1 entity, int redniBroj) {
        setCellValue(row, 0, String.valueOf(redniBroj)); // A - Redni broj
        setCellValue(row, 1, entity.getIme()); // B - Ime
        setCellValue(row, 2, entity.getPrezime()); // C - Prezime
        setCellValue(row, 3, entity.getJmbg()); // D - JMBG
        setCellValue(row, 4, entity.getPttBroj()); // E - PTT broj
        setCellValue(row, 5, entity.getGradOpstina()); // F - Grad/Opština
        setCellValue(row, 6, entity.getMesto()); // G - Mesto
        setCellValue(row, 7, entity.getUlicaIBroj()); // H - Ulica i broj
        setCellValue(row, 8, entity.getBrojClanovaDomacinstva()); // I - Broj članova
        setCellValue(row, 9, entity.getOsnovSticanjaStatusa()); // J - Osnov sticanja statusa
        setCellValue(row, 10, entity.getEdBrojBrojMernogUredjaja()); // K - ED broj
        setCellValue(row, 11, entity.getPotrosnjaIPovrsinaCombined()); // L - Potrošnja
        setCellValue(row, 12, entity.getIznosUmanjenjaSaPdv()); // M - Iznos
        setCellValue(row, 13, entity.getBrojRacuna()); // N - Broj računa
        setCellValue(row, 14, formatDate(entity.getDatumIzdavanjaRacuna())); // O - Datum izdavanja
    }
    
    /**
     * Postavlja vrednost ćelije
     */
    private void setCellValue(Row row, int cellIndex, Object value) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            cell = row.createCell(cellIndex);
        }
        
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof java.math.BigDecimal) {
            cell.setCellValue(((java.math.BigDecimal) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }
    
    /**
     * Formatira datum
     */
    private String formatDate(LocalDate date) {
        if (date == null) return "";
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
    
    
    /**
     * Generiše ime fajla sa datumom (ASCII safe)
     */
    public String generateFileName() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return "EUK_Izvestaj_" + date + ".xlsx";
    }
}

