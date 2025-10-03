package com.sirus.backend.controller;

import com.sirus.backend.service.ExportService;
import com.sirus.backend.service.ExcelExportService;
import com.sirus.backend.service.ExcelExportServiceT2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
@CrossOrigin(origins = "*")
public class ExportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);
    
    @Autowired
    private ExportService exportService;
    
    @Autowired
    private ExcelExportService excelExportService;
    
    @Autowired
    private ExcelExportServiceT2 excelExportServiceT2;
    
    /**
     * GET /api/export/excel/t1 - Izvoz ugrozeno_lice_t1 u Excel sa template-om
     */
    @GetMapping("/excel/t1")
    public ResponseEntity<byte[]> exportUgrozenoLiceT1ToExcel() {
        logger.info("GET /api/export/excel/t1 - Starting export of ugrozeno_lice_t1");
        
        try {
            byte[] excelData = exportService.exportUgrozenoLiceT1ToExcel();
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "ugrozena_lica_t1_" + timestamp + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported ugrozeno_lice_t1 to Excel: {} bytes", excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting ugrozeno_lice_t1: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/export/excel/t2 - Izvoz ugrozeno_lice_t2 u Excel sa template-om
     */
    @GetMapping("/excel/t2")
    public ResponseEntity<byte[]> exportUgrozenoLiceT2ToExcel() {
        logger.info("GET /api/export/excel/t2 - Starting export of ugrozeno_lice_t2");
        
        try {
            byte[] excelData = exportService.exportUgrozenoLiceT2ToExcel();
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "ugrozena_lica_t2_" + timestamp + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported ugrozeno_lice_t2 to Excel: {} bytes", excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting ugrozeno_lice_t2: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/export/excel - Jedinstveni endpoint za izvoz Excel fajla
     * Prima parametar 'type' (t1 ili t2) za određivanje tipa izvoza
     */
    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(value = "type", defaultValue = "t1") String type) {
        logger.info("GET /api/export/excel - Export request for type: {}", type);
        
        try {
            byte[] excelData;
            String filename;
            
            if ("t2".equalsIgnoreCase(type)) {
                excelData = exportService.exportUgrozenoLiceT2ToExcel();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                filename = "ugrozena_lica_t2_" + timestamp + ".xlsx";
            } else {
                // Default je t1
                excelData = exportService.exportUgrozenoLiceT1ToExcel();
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                filename = "ugrozena_lica_t1_" + timestamp + ".xlsx";
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported {} to Excel: {} bytes", type, excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting {}: {}", type, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/export/status - Status izvoza (za buduće asinhrone operacije)
     */
    @GetMapping("/status")
    public ResponseEntity<Object> getExportStatus() {
        logger.info("GET /api/export/status - Export status check");
        
        // Za sada je sinhrone, ali može se proširiti za asinhrone operacije
        return ResponseEntity.ok().body("Export operations are synchronous");
    }
    
    /**
     * GET /api/export/dynamic - Dinamički izvoz sa template-om (SVE podatke)
     */
    @GetMapping("/dynamic")
    public ResponseEntity<byte[]> exportDynamic() {
        logger.info("GET /api/export/dynamic - Starting dynamic export with template");
        
        try {
            byte[] excelData = excelExportService.generateExcelWithAllData();
            String filename = excelExportService.generateFileName();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            // Dodaj CORS headers za download
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition, Content-Type, Content-Length");
            
            logger.info("Successfully exported dynamic Excel: {} bytes, filename: {}", excelData.length, filename);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting dynamic Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST /api/export/dynamic/filtered - Dinamički izvoz sa filterovanim podacima
     */
    @PostMapping("/dynamic/filtered")
    public ResponseEntity<byte[]> exportDynamicFiltered(@RequestBody Map<String, Object> request) {
        logger.info("POST /api/export/dynamic/filtered - Starting filtered dynamic export");
        
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            
            if (ids == null || ids.isEmpty()) {
                logger.warn("No IDs provided for filtered export");
                return ResponseEntity.badRequest().build();
            }
            
            byte[] excelData = excelExportService.generateExcelWithFilteredData(ids);
            String filename = excelExportService.generateFileName();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported {} filtered records: {} bytes", ids.size(), excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting filtered dynamic Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * GET /api/export/dynamic/t2 - Dinamički izvoz T2 sa template-om (SVE podatke)
     */
    @GetMapping("/dynamic/t2")
    public ResponseEntity<byte[]> exportDynamicT2() {
        logger.info("GET /api/export/dynamic/t2 - Starting dynamic T2 export with template");
        
        try {
            byte[] excelData = excelExportServiceT2.generateExcelWithAllData();
            String filename = excelExportServiceT2.generateFileName();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported dynamic T2 Excel: {} bytes, filename: {}", excelData.length, filename);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting dynamic T2 Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * POST /api/export/dynamic/t2/filtered - Dinamički izvoz T2 sa filterovanim podacima
     */
    @PostMapping("/dynamic/t2/filtered")
    public ResponseEntity<byte[]> exportDynamicT2Filtered(@RequestBody Map<String, Object> request) {
        logger.info("POST /api/export/dynamic/t2/filtered - Starting filtered dynamic T2 export");
        
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) request.get("ids");
            
            if (ids == null || ids.isEmpty()) {
                logger.warn("No IDs provided for filtered T2 export");
                return ResponseEntity.badRequest().build();
            }
            
            byte[] excelData = excelExportServiceT2.generateExcelWithFilteredData(ids);
            String filename = excelExportServiceT2.generateFileName();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);
            
            logger.info("Successfully exported {} filtered T2 records: {} bytes", ids.size(), excelData.length);
            
            return new ResponseEntity<>(excelData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting filtered dynamic T2 Excel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
