package com.sirus.backend.controller;

import com.sirus.backend.config.TableImportConfig;
import com.sirus.backend.service.ImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);
    
    @Autowired
    private ImportService importService;
    
    /**
     * POST /api/import/excel - Endpoint za uvoz Excel fajla
     * Prima MultipartFile i naziv tabele, pokreće asinhronu obradu
     */
    @PostMapping("/excel")
    public ResponseEntity<Map<String, Object>> uploadExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "table", required = false) String table,
            @RequestParam(value = "tableName", required = false) String tableName) {
        
        // Kompatibilnost - prihvata i 'table' i 'tableName' parametre
        String finalTableName = table != null ? table : tableName;
        
        logger.info("POST /api/import/excel - Received Excel file: {} ({} bytes) for table: {}", 
                   file.getOriginalFilename(), file.getSize(), finalTableName);
        
        try {
            // Validacija fajla
            if (file.isEmpty()) {
                logger.warn("Empty file received");
                return ResponseEntity.badRequest().body(createErrorResponse("EMPTY_FILE", "Fajl je prazan"));
            }
            
            if (!isValidExcelFile(file)) {
                logger.warn("Invalid file type: {}", file.getContentType());
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_FILE_TYPE", 
                    "Podržani su samo Excel fajlovi (.xlsx, .xls)"));
            }
            
            // Validacija tabele
            if (finalTableName == null || finalTableName.trim().isEmpty()) {
                logger.warn("Missing table parameter");
                return ResponseEntity.badRequest().body(createErrorResponse("MISSING_TABLE", 
                    "Nedostaje parametar 'table' ili 'tableName'"));
            }
            
            TableImportConfig.TableMapping tableMapping = TableImportConfig.getTableMapping(finalTableName);
            if (tableMapping == null) {
                logger.warn("Invalid table name: {}", finalTableName);
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_TABLE", 
                    "Neispravan naziv tabele. Dostupne tabele: " + String.join(", ", TableImportConfig.getAvailableTables())));
            }
            
            // Kopiranje sadržaja fajla u memoriju pre @Async poziva
            byte[] fileContent;
            try {
                fileContent = file.getBytes();
                logger.info("File content copied to memory: {} bytes", fileContent.length);
            } catch (IOException e) {
                logger.error("Error reading file content: {}", e.getMessage(), e);
                return ResponseEntity.status(500).body(createErrorResponse("FILE_READ_ERROR", 
                    "Greška pri čitanju fajla: " + e.getMessage()));
            }
            
            // Pokretanje sinhronizovane obrade
            Map<String, Object> result = importService.processExcelSync(fileContent, file.getOriginalFilename(), finalTableName);
            
            logger.info("Excel import completed for file: {} to table: {}", file.getOriginalFilename(), finalTableName);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error starting Excel import: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("INTERNAL_ERROR", 
                "Greška pri pokretanju uvoza: " + e.getMessage()));
        }
    }
    
    /**
     * POST /api/import/excel/validate - Validacija Excel fajla bez uvoza
     * Vraća detaljne greške za svaki red
     */
    @PostMapping("/excel/validate")
    public ResponseEntity<Map<String, Object>> validateExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "table", required = false) String table,
            @RequestParam(value = "tableName", required = false) String tableName) {
        
        // Kompatibilnost - prihvata i 'table' i 'tableName' parametre
        String finalTableName = table != null ? table : tableName;
        
        logger.info("POST /api/import/excel/validate - Validating Excel file: {} for table: {}", 
                   file.getOriginalFilename(), finalTableName);
        
        try {
            // Validacija fajla
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("EMPTY_FILE", "Fajl je prazan"));
            }
            
            if (!isValidExcelFile(file)) {
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_FILE_TYPE", 
                    "Podržani su samo Excel fajlovi (.xlsx, .xls)"));
            }
            
            // Validacija tabele
            if (finalTableName == null || finalTableName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("MISSING_TABLE", 
                    "Nedostaje parametar 'table' ili 'tableName'"));
            }
            
            TableImportConfig.TableMapping tableMapping = TableImportConfig.getTableMapping(finalTableName);
            if (tableMapping == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("INVALID_TABLE", 
                    "Neispravan naziv tabele. Dostupne tabele: " + String.join(", ", TableImportConfig.getAvailableTables())));
            }
            
            // Validacija Excel fajla
            Map<String, Object> validationResult = importService.validateExcelFile(file, finalTableName);
            
            return ResponseEntity.ok(validationResult);
            
        } catch (Exception e) {
            logger.error("Error validating Excel file: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("INTERNAL_ERROR", 
                "Greška pri validaciji fajla: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/import/status - Provera statusa uvoza
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getImportStatus() {
        logger.info("GET /api/import/status - Checking import status");
        
        try {
            Map<String, Object> status = importService.getImportStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("Error getting import status: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("INTERNAL_ERROR", 
                "Greška pri dohvatanju statusa: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/import/tables - Lista dostupnih tabela za import
     */
    @GetMapping("/tables")
    public ResponseEntity<Map<String, Object>> getAvailableTables() {
        logger.info("GET /api/import/tables - Getting available tables");
        
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("tables", TableImportConfig.getAvailableTables());
            
            // Dodaj detalje za svaku tabelu
            Map<String, Object> tableDetails = new HashMap<>();
            for (String tableName : TableImportConfig.getAvailableTables()) {
                TableImportConfig.TableMapping mapping = TableImportConfig.getTableMapping(tableName);
                Map<String, Object> details = new HashMap<>();
                details.put("tableName", mapping.getTableName());
                details.put("entityName", mapping.getEntityName());
                details.put("columnCount", mapping.getColumnCount());
                details.put("columns", mapping.getColumnNames());
                details.put("displayNames", mapping.getDisplayNames());
                tableDetails.put(tableName, details);
            }
            response.put("tableDetails", tableDetails);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting available tables: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("INTERNAL_ERROR", 
                "Greška pri dohvatanju tabela: " + e.getMessage()));
        }
    }
    
    /**
     * Validacija da li je fajl Excel format
     */
    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        
        return (contentType != null && (
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                contentType.equals("application/vnd.ms-excel")
            )) ||
            (filename != null && (
                filename.toLowerCase().endsWith(".xlsx") ||
                filename.toLowerCase().endsWith(".xls")
            ));
    }
    
    /**
     * Kreiranje error response
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ERROR");
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
