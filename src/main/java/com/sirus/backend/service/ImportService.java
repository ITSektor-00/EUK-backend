package com.sirus.backend.service;

import com.sirus.backend.config.TableImportConfig;
import com.sirus.backend.entity.*;
import com.sirus.backend.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
// import java.util.Optional; // Removed unused import

@Service
public class ImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);
    
    @Autowired
    private EukUgrozenoLiceT1Repository ugrozenoLiceT1Repository;
    
    @Autowired
    private EukUgrozenoLiceT2Repository ugrozenoLiceT2Repository;
    
    @Autowired
    private EukKategorijaRepository eukKategorijaRepository;
    
    @Autowired
    private EukPredmetRepository eukPredmetRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // GlobalLicenseRepository uklonjen jer nije potreban
    
    // Status tracking
    private final AtomicInteger processedRows = new AtomicInteger(0);
    private final AtomicInteger totalRows = new AtomicInteger(0);
    private final AtomicLong startTime = new AtomicLong(0);
    private volatile String currentStatus = "IDLE";
    private volatile String lastError = null;
    
    /**
     * Sinhrona obrada Excel fajla za određenu tabelu
     */
    public Map<String, Object> processExcelSync(byte[] fileContent, String filename, String tableName) {
        logger.info("Starting async Excel processing for file: {} to table: {}", filename, tableName);
        
        startTime.set(System.currentTimeMillis());
        currentStatus = "PROCESSING";
        processedRows.set(0);
        totalRows.set(0);
        lastError = null;
        
        // Dohvati mapiranje tabele
        TableImportConfig.TableMapping tableMapping = TableImportConfig.getTableMapping(tableName);
        if (tableMapping == null) {
            currentStatus = "ERROR";
            lastError = "Invalid table name: " + tableName;
            logger.error("Invalid table name: {}", tableName);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("status", "ERROR");
            errorResult.put("message", "Invalid table name: " + tableName);
            return errorResult;
        }
        
        try (InputStream is = new ByteArrayInputStream(fileContent)) {
            Workbook workbook = createWorkbook(filename, is);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Prebrojavanje redova (preskačemo header)
            int totalRowCount = sheet.getLastRowNum();
            totalRows.set(totalRowCount);
            logger.info("Total rows to process: {}", totalRowCount);
            
            
            // Batch processing
            List<Object> batch = new ArrayList<>();
            int batchSize = 1000;
            int consecutiveEmptyRows = 0;
            
            for (int i = 9; i <= totalRowCount; i++) { // Počinjemo od 9 (podaci počinju od A9 - redni broj 1)
                Row row = sheet.getRow(i);
                if (row == null) {
                    consecutiveEmptyRows++;
                    if (consecutiveEmptyRows >= 2) {
                        logger.info("2 uzastopna prazna reda. Zaustavljamo import.");
                        break;
                    }
                    continue;
                }
                
                try {
                    Object entity = parseRowToEntity(row, tableMapping);
                    
                    // Debug za prvi red (A9)
                    if (i == 9 && entity != null) {
                        logger.info("=== PRVI RED (A9 - Redni broj 1) DEBUG ===");
                        if (entity instanceof EukUgrozenoLiceT1) {
                            EukUgrozenoLiceT1 lice = (EukUgrozenoLiceT1) entity;
                            logger.info("RedniBroj: '{}', Ime: '{}', Prezime: '{}', JMBG: '{}'", 
                                lice.getRedniBroj(), lice.getIme(), lice.getPrezime(), lice.getJmbg());
                        }
                    }
                    
                    if (entity != null) {
                        batch.add(entity);
                        consecutiveEmptyRows = 0; // Resetuj brojač
                    } else {
                        // Prazan red ili footer
                        if (i == 9) {
                            logger.warn("UPOZORENJE: Prvi red (A9) je tretiran kao prazan!");
                        }
                        consecutiveEmptyRows++;
                        if (consecutiveEmptyRows >= 2) {
                            logger.info("2 uzastopna prazna reda na liniji {}. Zaustavljamo import.", i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing row {}: {}", i, e.getMessage());
                    consecutiveEmptyRows = 0; // Resetuj brojač jer je bila greška, ne prazan red
                }
                
                // Batch insert
                if (batch.size() >= batchSize) {
                    saveBatch(batch, tableName);
                    batch.clear();
                }
                
                processedRows.incrementAndGet();
                
            }
            
            // Poslednji batch
            if (!batch.isEmpty()) {
                saveBatch(batch, tableName);
            }
            
            currentStatus = "COMPLETED";
            logger.info("Excel processing completed successfully. Processed {} rows", processedRows.get());
            
            // Vrati rezultat
            Map<String, Object> result = new HashMap<>();
            result.put("status", "SUCCESS");
            result.put("message", "Import završen");
            result.put("processedRecords", processedRows.get());
            result.put("totalRecords", totalRows.get());
            result.put("filename", filename);
            result.put("table", tableName);
            result.put("processingTimeMs", System.currentTimeMillis() - startTime.get());
            
            return result;
            
        } catch (Exception e) {
            currentStatus = "ERROR";
            lastError = e.getMessage();
            logger.error("Error processing Excel file: {}", e.getMessage(), e);
            
            // Vrati grešku
            Map<String, Object> result = new HashMap<>();
            result.put("status", "ERROR");
            result.put("message", "Greška pri importu: " + e.getMessage());
            result.put("processedRecords", processedRows.get());
            result.put("totalRecords", totalRows.get());
            result.put("filename", filename);
            result.put("table", tableName);
            
            return result;
        } finally {
            // Workbook se automatski zatvara u try-with-resources bloku
        }
    }
    
    /**
     * Kreiranje Workbook objekta na osnovu tipa fajla
     */
    private Workbook createWorkbook(String filename, InputStream is) throws Exception {
        if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(is);
        } else {
            return new HSSFWorkbook(is);
        }
    }
    
    /**
     * Parsiranje reda Excel-a u odgovarajući entitet na osnovu tabele
     */
    private Object parseRowToEntity(Row row, TableImportConfig.TableMapping tableMapping) {
        try {
            String tableName = tableMapping.getTableName();
            String[] columnNames = tableMapping.getColumnNames();
            
            // Kreiranje entiteta na osnovu tabele
            switch (tableName) {
                case "euk.ugrozeno_lice_t1":
                    return parseUgrozenoLiceT1(row, columnNames);
                case "euk.ugrozeno_lice_t2":
                    return parseUgrozenoLiceT2(row, columnNames);
                case "euk.kategorija":
                    return parseEukKategorija(row, columnNames);
                case "euk.predmet":
                    return parseEukPredmet(row, columnNames);
                case "users":
                    return parseUser(row, columnNames);
                default:
                    logger.warn("Unknown table: {}", tableName);
                    return null;
            }
            
        } catch (Exception e) {
            logger.warn("Error parsing row: {}", e.getMessage());
            return null;
        }
    }
    
    private EukUgrozenoLiceT1 parseUgrozenoLiceT1(Row row, String[] columnNames) {
        String redniBroj = getCellValueAsString(row.getCell(0));
        String ime = getCellValueAsString(row.getCell(1));
        String prezime = getCellValueAsString(row.getCell(2));
        String jmbg = getCellValueAsString(row.getCell(3));
        String pttBroj = getCellValueAsString(row.getCell(4));
        
        // Proveri da li je footer red
        if ((redniBroj != null && (redniBroj.contains("М.П.") || redniBroj.contains("Потпис"))) ||
            (ime != null && (ime.contains("М.П.") || ime.contains("Потпис"))) ||
            (prezime != null && (prezime.contains("М.П.") || prezime.contains("Потпис")))) {
            return null; // Footer red, ne parsiramo
        }
        
        // Proveri da li su IME, PREZIME i JMBG svi prazni (prazan red)
        // Red je validan čak i ako nema redni broj ili druge podatke, ali mora imati bar jedno od ova 3 polja
        boolean isEmpty = (ime == null || ime.trim().isEmpty()) &&
                         (prezime == null || prezime.trim().isEmpty()) &&
                         (jmbg == null || jmbg.trim().isEmpty());
        
        if (isEmpty) {
            return null; // Prazan red, ne parsiramo
        }
        
        EukUgrozenoLiceT1 entity = new EukUgrozenoLiceT1();
        
        // Postavi default vrednosti za prazne polja
        if (redniBroj == null || redniBroj.trim().isEmpty()) redniBroj = "";
        if (ime == null || ime.trim().isEmpty()) ime = "";
        if (prezime == null || prezime.trim().isEmpty()) prezime = "";
        if (jmbg == null || jmbg.trim().isEmpty()) jmbg = "";
        if (pttBroj == null || pttBroj.trim().isEmpty()) pttBroj = "";
        
        if (columnNames.length > 0) entity.setRedniBroj(redniBroj);
        if (columnNames.length > 1) entity.setIme(ime);
        if (columnNames.length > 2) entity.setPrezime(prezime);
        if (columnNames.length > 3) entity.setJmbg(jmbg);
        if (columnNames.length > 4) {
            String pttBrojValue = getCellValueAsString(row.getCell(4));
            entity.setPttBroj(pttBrojValue != null ? pttBrojValue : "");
        }
        if (columnNames.length > 5) {
            String gradOpstinaValue = getCellValueAsString(row.getCell(5));
            entity.setGradOpstina(gradOpstinaValue != null ? gradOpstinaValue : "");
        }
        if (columnNames.length > 6) {
            String mestoValue = getCellValueAsString(row.getCell(6));
            entity.setMesto(mestoValue != null ? mestoValue : "");
        }
        if (columnNames.length > 7) {
            String ulicaIBrojValue = getCellValueAsString(row.getCell(7));
            entity.setUlicaIBroj(ulicaIBrojValue != null ? ulicaIBrojValue : "");
        }
        if (columnNames.length > 8) entity.setBrojClanovaDomacinstva(getCellValueAsInteger(row.getCell(8)));
        if (columnNames.length > 9) {
            String osnovSticanjaValue = getCellValueAsString(row.getCell(9));
            entity.setOsnovSticanjaStatusa(osnovSticanjaValue != null ? osnovSticanjaValue : "");
        }
        if (columnNames.length > 10) {
            String edBrojValue = getCellValueAsString(row.getCell(10));
            entity.setEdBrojBrojMernogUredjaja(edBrojValue != null ? edBrojValue : "");
        }
        if (columnNames.length > 11) {
            String potrosnjaValue = getCellValueAsString(row.getCell(11));
            entity.setPotrosnjaIPovrsinaCombined(potrosnjaValue != null ? potrosnjaValue : "");
        }
        if (columnNames.length > 12) entity.setIznosUmanjenjaSaPdv(getCellValueAsBigDecimal(row.getCell(12)));
        if (columnNames.length > 13) {
            String brojRacunaValue = getCellValueAsString(row.getCell(13));
            entity.setBrojRacuna(brojRacunaValue != null ? brojRacunaValue : "");
        }
        if (columnNames.length > 14) entity.setDatumIzdavanjaRacuna(getCellValueAsLocalDate(row.getCell(14)));
        if (columnNames.length > 15) entity.setDatumTrajanjaPrava(getCellValueAsLocalDate(row.getCell(15)));
        
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
    
    private EukUgrozenoLiceT2 parseUgrozenoLiceT2(Row row, String[] columnNames) {
        String ime = getCellValueAsString(row.getCell(1));
        String prezime = getCellValueAsString(row.getCell(2));
        String jmbg = getCellValueAsString(row.getCell(3));
        
        // Proveri da li je footer red
        if ((ime != null && (ime.contains("М.П.") || ime.contains("Потпис"))) ||
            (prezime != null && (prezime.contains("М.П.") || prezime.contains("Потпис")))) {
            return null; // Footer red
        }
        
        // Proveri da li su IME, PREZIME i JMBG svi prazni (prazan red)
        boolean isEmpty = (ime == null || ime.trim().isEmpty()) &&
                         (prezime == null || prezime.trim().isEmpty()) &&
                         (jmbg == null || jmbg.trim().isEmpty());
        
        if (isEmpty) {
            return null; // Prazan red
        }
        
        EukUgrozenoLiceT2 entity = new EukUgrozenoLiceT2();
        
        if (columnNames.length > 0) entity.setRedniBroj(getCellValueAsString(row.getCell(0)));
        if (columnNames.length > 1) entity.setIme(ime);
        if (columnNames.length > 2) entity.setPrezime(prezime);
        if (columnNames.length > 3) entity.setJmbg(jmbg);
        if (columnNames.length > 4) entity.setPttBroj(getCellValueAsString(row.getCell(4)));
        if (columnNames.length > 5) entity.setGradOpstina(getCellValueAsString(row.getCell(5)));
        if (columnNames.length > 6) entity.setMesto(getCellValueAsString(row.getCell(6)));
        if (columnNames.length > 7) entity.setUlicaIBroj(getCellValueAsString(row.getCell(7)));
        if (columnNames.length > 8) entity.setEdBroj(getCellValueAsString(row.getCell(8)));
        if (columnNames.length > 9) entity.setPokVazenjaResenjaOStatusu(getCellValueAsString(row.getCell(9)));
        
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
    
    private EukKategorija parseEukKategorija(Row row, String[] columnNames) {
        EukKategorija entity = new EukKategorija();
        if (columnNames.length > 0) entity.setNaziv(getCellValueAsString(row.getCell(0)));
        return entity;
    }
    
    private EukPredmet parseEukPredmet(Row row, String[] columnNames) {
        EukPredmet entity = new EukPredmet();
        if (columnNames.length > 0) entity.setNazivPredmeta(getCellValueAsString(row.getCell(0)));
        // EukPredmet nema opis polje, preskačemo drugu kolonu
        return entity;
    }
    
    private User parseUser(Row row, String[] columnNames) {
        User entity = new User();
        if (columnNames.length > 0) entity.setUsername(getCellValueAsString(row.getCell(0)));
        if (columnNames.length > 1) entity.setEmail(getCellValueAsString(row.getCell(1)));
        if (columnNames.length > 2) entity.setFirstName(getCellValueAsString(row.getCell(2)));
        if (columnNames.length > 3) entity.setLastName(getCellValueAsString(row.getCell(3)));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }
    
    /**
     * Batch čuvanje u bazu - uvek kreira nove zapise, bez provere duplikata
     */
    private void saveBatch(List<Object> batch, String tableName) {
        try {
            switch (tableName) {
                case "euk.ugrozeno_lice_t1":
                    ugrozenoLiceT1Repository.saveAll((List<EukUgrozenoLiceT1>) (List<?>) batch);
                    break;
                case "euk.ugrozeno_lice_t2":
                    ugrozenoLiceT2Repository.saveAll((List<EukUgrozenoLiceT2>) (List<?>) batch);
                    break;
                case "euk.kategorija":
                    eukKategorijaRepository.saveAll((List<EukKategorija>) (List<?>) batch);
                    break;
                case "euk.predmet":
                    eukPredmetRepository.saveAll((List<EukPredmet>) (List<?>) batch);
                    break;
                case "users":
                    userRepository.saveAll((List<User>) (List<?>) batch);
                    break;
                default:
                    logger.warn("Unknown table for saving: {}", tableName);
                    return;
            }
        } catch (Exception e) {
            logger.error("Error saving batch to table {}: {}", tableName, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Validacija Excel fajla bez uvoza u bazu
     * Vraća detaljne greške za svaki red
     */
    public Map<String, Object> validateExcelFile(MultipartFile file, String tableName) {
        logger.info("Validating Excel file: {} for table: {}", file.getOriginalFilename(), tableName);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();
        List<Map<String, Object>> warnings = new ArrayList<>();
        
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = createWorkbook(file.getOriginalFilename(), is);
            Sheet sheet = workbook.getSheetAt(0);
            
            TableImportConfig.TableMapping tableMapping = TableImportConfig.getTableMapping(tableName);
            if (tableMapping == null) {
                result.put("status", "ERROR");
                result.put("message", "Invalid table name: " + tableName);
                return result;
            }
            
            int totalRows = sheet.getLastRowNum();
            int validRows = 0;
            int errorRows = 0;
            
            // Validacija svakog reda
            for (int i = 9; i <= totalRows; i++) { // Počinjemo od 9 (podaci počinju od A9 - redni broj 1)
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Map<String, Object> rowValidation = validateRow(row, i, tableMapping);
                
                if ((Boolean) rowValidation.get("hasErrors")) {
                    errorRows++;
                    errors.add(rowValidation);
                } else if ((Boolean) rowValidation.get("hasWarnings")) {
                    warnings.add(rowValidation);
                } else {
                    validRows++;
                }
            }
            
            result.put("status", "SUCCESS");
            result.put("totalRows", totalRows);
            result.put("validRows", validRows);
            result.put("errorRows", errorRows);
            result.put("warningRows", warnings.size());
            result.put("errors", errors);
            result.put("warnings", warnings);
            result.put("canImport", errorRows == 0);
            
            if (errorRows > 0) {
                result.put("message", "Pronađene greške u " + errorRows + " redova. Uvoz nije moguć.");
            } else if (warnings.size() > 0) {
                result.put("message", "Uvoz je moguć sa " + warnings.size() + " upozorenja.");
            } else {
                result.put("message", "Fajl je validan. Uvoz je moguć.");
            }
            
        } catch (Exception e) {
            logger.error("Error validating Excel file: {}", e.getMessage(), e);
            result.put("status", "ERROR");
            result.put("message", "Greška pri validaciji fajla: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Validacija jednog reda Excel-a
     */
    private Map<String, Object> validateRow(Row row, int rowNumber, TableImportConfig.TableMapping tableMapping) {
        Map<String, Object> validation = new HashMap<>();
        List<String> rowErrors = new ArrayList<>();
        List<String> rowWarnings = new ArrayList<>();
        
        validation.put("rowNumber", rowNumber);
        validation.put("hasErrors", false);
        validation.put("hasWarnings", false);
        
        try {
            String tableName = tableMapping.getTableName();
            String[] columnNames = tableMapping.getColumnNames();
            
            // Validacija na osnovu tabele
            switch (tableName) {
                case "euk.ugrozeno_lice_t1":
                    validateUgrozenoLiceT1Row(row, rowNumber, columnNames, rowErrors, rowWarnings);
                    break;
                case "euk.ugrozeno_lice_t2":
                    validateUgrozenoLiceT2Row(row, rowNumber, columnNames, rowErrors, rowWarnings);
                    break;
                case "euk.kategorija":
                    validateEukKategorijaRow(row, rowNumber, columnNames, rowErrors, rowWarnings);
                    break;
                case "euk.predmet":
                    validateEukPredmetRow(row, rowNumber, columnNames, rowErrors, rowWarnings);
                    break;
                case "users":
                    validateUserRow(row, rowNumber, columnNames, rowErrors, rowWarnings);
                    break;
                default:
                    rowErrors.add("Nepoznata tabela: " + tableName);
            }
            
        } catch (Exception e) {
            rowErrors.add("Greška pri validaciji reda: " + e.getMessage());
        }
        
        validation.put("errors", rowErrors);
        validation.put("warnings", rowWarnings);
        validation.put("hasErrors", !rowErrors.isEmpty());
        validation.put("hasWarnings", !rowWarnings.isEmpty());
        
        return validation;
    }
    
    /**
     * Validacija reda za ugrozeno_lice_t1 - NEMA VALIDACIJE
     */
    private void validateUgrozenoLiceT1Row(Row row, int rowNumber, String[] columnNames, 
                                         List<String> errors, List<String> warnings) {
        // Nema validacije - prihvata sve podatke
    }
    
    /**
     * Validacija reda za ugrozeno_lice_t2 - NEMA VALIDACIJE
     */
    private void validateUgrozenoLiceT2Row(Row row, int rowNumber, String[] columnNames, 
                                         List<String> errors, List<String> warnings) {
        // Nema validacije - prihvata sve podatke
    }
    
    /**
     * Validacija reda za euk.kategorija - NEMA VALIDACIJE
     */
    private void validateEukKategorijaRow(Row row, int rowNumber, String[] columnNames, 
                                        List<String> errors, List<String> warnings) {
        // Nema validacije - prihvata sve podatke
    }
    
    /**
     * Validacija reda za euk.predmet - NEMA VALIDACIJE
     */
    private void validateEukPredmetRow(Row row, int rowNumber, String[] columnNames, 
                                    List<String> errors, List<String> warnings) {
        // Nema validacije - prihvata sve podatke
    }
    
    /**
     * Validacija reda za users - NEMA VALIDACIJE
     */
    private void validateUserRow(Row row, int rowNumber, String[] columnNames, 
                              List<String> errors, List<String> warnings) {
        // Nema validacije - prihvata sve podatke
    }
    
    /**
     * Dohvatanje statusa uvoza
     */
    public Map<String, Object> getImportStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", currentStatus);
        status.put("processedRows", processedRows.get());
        status.put("totalRows", totalRows.get());
        status.put("progress", totalRows.get() > 0 ? 
            (double) processedRows.get() / totalRows.get() * 100 : 0);
        
        if (startTime.get() > 0) {
            long elapsedTime = System.currentTimeMillis() - startTime.get();
            status.put("elapsedTimeMs", elapsedTime);
            status.put("elapsedTimeSeconds", elapsedTime / 1000.0);
        }
        
        if (lastError != null) {
            status.put("lastError", lastError);
        }
        
        return status;
    }
    
    
    // Helper methods za čitanje ćelija
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Integer.parseInt(value);
            }
        } catch (Exception e) {
            logger.warn("Error parsing integer from cell: {}", e.getMessage());
        }
        return null;
    }
    
    private BigDecimal getCellValueAsBigDecimal(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return BigDecimal.valueOf(cell.getNumericCellValue());
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : new BigDecimal(value);
            }
        } catch (Exception e) {
            logger.warn("Error parsing BigDecimal from cell: {}", e.getMessage());
        }
        return null;
    }
    
    private LocalDate getCellValueAsLocalDate(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    return LocalDate.parse(value);
                }
            }
        } catch (Exception e) {
            logger.warn("Error parsing LocalDate from cell: {}", e.getMessage());
        }
        return null;
    }
    
    private Boolean getCellValueAsBoolean(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.BOOLEAN) {
                return cell.getBooleanCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim().toLowerCase();
                return "true".equals(value) || "1".equals(value) || "yes".equals(value);
            } else if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue() != 0;
            }
        } catch (Exception e) {
            logger.warn("Error parsing Boolean from cell: {}", e.getMessage());
        }
        return null;
    }
    
    private LocalDateTime getCellValueAsLocalDateTime(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    return LocalDateTime.parse(value);
                }
            }
        } catch (Exception e) {
            logger.warn("Error parsing LocalDateTime from cell: {}", e.getMessage());
        }
        return null;
    }
}
