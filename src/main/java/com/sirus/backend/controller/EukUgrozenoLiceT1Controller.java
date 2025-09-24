package com.sirus.backend.controller;

import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.service.EukUgrozenoLiceT1Service;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/euk/ugrozena-lica-t1")
@CrossOrigin(origins = "*")
public class EukUgrozenoLiceT1Controller {
    
    private static final Logger logger = LoggerFactory.getLogger(EukUgrozenoLiceT1Controller.class);
    
    @Autowired
    private EukUgrozenoLiceT1Service ugrozenoLiceT1Service;
    
    // GET /api/euk/ugrozena-lica-t1 - Lista svih ugroženih lica sa paginacijom
    @GetMapping
    public ResponseEntity<?> getAllUgrozenaLicaT1(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/euk/ugrozena-lica-t1 - Fetching ugrožena lica T1 with pagination - page: {}, size: {}", page, size);
        
        try {
            // Validacija parametara
            if (page < 0) {
                logger.warn("Invalid page parameter: {}", page);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_PAGE",
                    "message", "Page number must be non-negative",
                    "path", "/api/euk/ugrozena-lica-t1"
                ));
            }
            if (size <= 0 || size > 50000) {
                logger.warn("Invalid size parameter: {}", size);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_SIZE", 
                    "message", "Size must be between 1 and 50000",
                    "path", "/api/euk/ugrozena-lica-t1"
                ));
            }
            
            logger.info("Calling ugrozenoLiceT1Service.findAll with page: {}, size: {}", page, size);
            Page<EukUgrozenoLiceT1Dto> ugrozenaLica = ugrozenoLiceT1Service.findAll(page, size);
            logger.info("Successfully fetched {} ugrožena lica T1 for page {}", ugrozenaLica.getContent().size(), page);
            return ResponseEntity.ok(PaginatedResponse.from(ugrozenaLica));
        } catch (Exception e) {
            logger.error("DETAILED ERROR fetching ugrožena lica T1: {}", e.getMessage(), e);
            logger.error("Error class: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri dohvatanju podataka: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1",
                "details", e.getClass().getSimpleName()
            ));
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/{id} - Dohvatanje ugroženog lica po ID-u
    @GetMapping("/{id}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> getUgrozenoLiceT1ById(@PathVariable Integer id) {
        logger.info("GET /api/euk/ugrozena-lica-t1/{} - Fetching ugroženo lice T1 by ID", id);
        try {
            EukUgrozenoLiceT1Dto ugrozenoLice = ugrozenoLiceT1Service.findById(id);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (EukException e) {
            logger.warn("Ugroženo lice T1 not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching ugroženo lice T1 by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // POST /api/euk/ugrozena-lica-t1 - Kreiranje novog ugroženog lica
    @PostMapping
    public ResponseEntity<?> createUgrozenoLiceT1(@Valid @RequestBody EukUgrozenoLiceT1Dto ugrozenoLiceDto) {
        logger.info("POST /api/euk/ugrozena-lica-t1 - Creating new ugroženo lice T1: {} {}", 
                   ugrozenoLiceDto.getIme(), ugrozenoLiceDto.getPrezime());
        try {
            EukUgrozenoLiceT1Dto createdUgrozenoLice = ugrozenoLiceT1Service.create(ugrozenoLiceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUgrozenoLice);
        } catch (EukException e) {
            logger.warn("Error creating ugroženo lice T1: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1"
            ));
        } catch (Exception e) {
            logger.error("Error creating ugroženo lice T1: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri kreiranju ugroženog lica: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1"
            ));
        }
    }
    
    // POST /api/euk/ugrozena-lica-t1/batch - Masovni import podataka
    @PostMapping("/batch")
    public ResponseEntity<?> createBatch(@Valid @RequestBody List<EukUgrozenoLiceT1Dto> ugrozenaLicaDtos) {
        logger.info("POST /api/euk/ugrozena-lica-t1/batch - Creating batch of {} ugrožena lica T1", ugrozenaLicaDtos.size());
        
        try {
            // Validacija veličine batch-a
            if (ugrozenaLicaDtos.size() > 1000) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "BATCH_TOO_LARGE",
                    "message", "Batch size cannot exceed 1000 records",
                    "path", "/api/euk/ugrozena-lica-t1/batch"
                ));
            }
            
            List<EukUgrozenoLiceT1Dto> createdLica = ugrozenoLiceT1Service.createBatch(ugrozenaLicaDtos);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Batch import completed successfully",
                "totalProcessed", createdLica.size(),
                "totalSubmitted", ugrozenaLicaDtos.size(),
                "skippedCount", ugrozenaLicaDtos.size() - createdLica.size(),
                "data", createdLica
            ));
        } catch (Exception e) {
            logger.error("Error creating batch: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri batch importu: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/batch"
            ));
        }
    }
    
    // POST /api/euk/ugrozena-lica-t1/batch-progress - Batch import sa progress tracking-om
    @PostMapping("/batch-progress")
    public ResponseEntity<?> createBatchWithProgress(@Valid @RequestBody List<EukUgrozenoLiceT1Dto> ugrozenaLicaDtos) {
        logger.info("POST /api/euk/ugrozena-lica-t1/batch-progress - Creating batch of {} ugrožena lica T1 with progress tracking", ugrozenaLicaDtos.size());
        
        try {
            // Validacija veličine batch-a
            if (ugrozenaLicaDtos.size() > 1000) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "BATCH_TOO_LARGE",
                    "message", "Batch size cannot exceed 1000 records",
                    "path", "/api/euk/ugrozena-lica-t1/batch-progress"
                ));
            }
            
            // Kreiranje batch ID-a za tracking
            String batchId = "batch_" + System.currentTimeMillis();
            
            // Asinhrono procesiranje sa progress tracking-om
            ugrozenoLiceT1Service.createBatchWithProgress(ugrozenaLicaDtos, batchId);
            
            return ResponseEntity.accepted().body(Map.of(
                "message", "Batch import started",
                "batchId", batchId,
                "totalRecords", ugrozenaLicaDtos.size(),
                "status", "PROCESSING"
            ));
        } catch (Exception e) {
            logger.error("Error starting batch with progress: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri pokretanju batch importa: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/batch-progress"
            ));
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/batch-progress/{batchId} - Proveri status batch-a
    @GetMapping("/batch-progress/{batchId}")
    public ResponseEntity<?> getBatchProgress(@PathVariable String batchId) {
        logger.info("GET /api/euk/ugrozena-lica-t1/batch-progress/{} - Checking batch progress", batchId);
        
        try {
            Map<String, Object> progress = ugrozenoLiceT1Service.getBatchProgress(batchId);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            logger.error("Error getting batch progress: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri dohvatanju progress-a: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/batch-progress/" + batchId
            ));
        }
    }
    
    // PUT /api/euk/ugrozena-lica-t1/{id} - Ažuriranje ugroženog lica
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUgrozenoLiceT1(@PathVariable Integer id, @Valid @RequestBody EukUgrozenoLiceT1Dto ugrozenoLiceDto) {
        logger.info("PUT /api/euk/ugrozena-lica-t1/{} - Updating ugroženo lice T1", id);
        try {
            EukUgrozenoLiceT1Dto updatedUgrozenoLice = ugrozenoLiceT1Service.update(id, ugrozenoLiceDto);
            return ResponseEntity.ok(updatedUgrozenoLice);
        } catch (EukException e) {
            logger.warn("Error updating ugroženo lice T1: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/" + id
            ));
        } catch (Exception e) {
            logger.error("Error updating ugroženo lice T1 with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri ažuriranju ugroženog lica: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/" + id
            ));
        }
    }
    
    // DELETE /api/euk/ugrozena-lica-t1/{id} - Brisanje ugroženog lica
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUgrozenoLiceT1(@PathVariable Integer id) {
        logger.info("DELETE /api/euk/ugrozena-lica-t1/{} - Deleting ugroženo lice T1", id);
        try {
            ugrozenoLiceT1Service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EukException e) {
            logger.warn("Error deleting ugroženo lice T1: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "VALIDATION_ERROR",
                "message", e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/" + id
            ));
        } catch (Exception e) {
            logger.error("Error deleting ugroženo lice T1 with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri brisanju ugroženog lica: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/" + id
            ));
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/search/jmbg/{jmbg} - Pretraga po JMBG-u
    @GetMapping("/search/jmbg/{jmbg}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> searchByJmbg(@PathVariable String jmbg) {
        logger.info("GET /api/euk/ugrozena-lica-t1/search/jmbg/{} - Searching ugroženo lice T1 by JMBG", jmbg);
        try {
            EukUgrozenoLiceT1Dto ugrozenoLice = ugrozenoLiceT1Service.findByJmbg(jmbg);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (EukException e) {
            logger.warn("Ugroženo lice T1 not found by JMBG: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error searching ugroženo lice T1 by JMBG {}: {}", jmbg, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/search/redni-broj/{redniBroj} - Pretraga po rednom broju
    @GetMapping("/search/redni-broj/{redniBroj}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> searchByRedniBroj(@PathVariable String redniBroj) {
        logger.info("GET /api/euk/ugrozena-lica-t1/search/redni-broj/{} - Searching ugroženo lice T1 by redni broj", redniBroj);
        try {
            EukUgrozenoLiceT1Dto ugrozenoLice = ugrozenoLiceT1Service.findByRedniBroj(redniBroj);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (EukException e) {
            logger.warn("Ugroženo lice T1 not found by redni broj: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error searching ugroženo lice T1 by redni broj {}: {}", redniBroj, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/search/name - Pretraga po imenu i prezimenu
    @GetMapping("/search/name")
    public ResponseEntity<?> searchByName(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/euk/ugrozena-lica-t1/search/name - Searching by name: {} {}", ime, prezime);
        
        try {
            if ((ime == null || ime.trim().isEmpty()) && (prezime == null || prezime.trim().isEmpty())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_SEARCH",
                    "message", "Ime ili prezime mora biti uneto za pretragu",
                    "path", "/api/euk/ugrozena-lica-t1/search/name"
                ));
            }
            
            Page<EukUgrozenoLiceT1Dto> results = ugrozenoLiceT1Service.searchByName(ime, prezime, page, size);
            return ResponseEntity.ok(PaginatedResponse.from(results));
        } catch (Exception e) {
            logger.error("Error searching by name: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri pretrazi po imenu: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/search/name"
            ));
        }
    }
    
    // POST /api/euk/ugrozena-lica-t1/search/filters - Kompleksna pretraga sa filterima
    @PostMapping("/search/filters")
    public ResponseEntity<?> searchWithFilters(@RequestBody Map<String, Object> filters,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        
        logger.info("POST /api/euk/ugrozena-lica-t1/search/filters - Searching with filters: {}", filters);
        
        try {
            Page<EukUgrozenoLiceT1Dto> results = ugrozenoLiceT1Service.searchWithFilters(filters, page, size);
            return ResponseEntity.ok(PaginatedResponse.from(results));
        } catch (Exception e) {
            logger.error("Error searching with filters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri pretrazi sa filterima: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica-t1/search/filters"
            ));
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/statistics - Statistike
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("GET /api/euk/ugrozena-lica-t1/statistics - Fetching statistics");
        try {
            Map<String, Object> statistics = ugrozenoLiceT1Service.getStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            logger.error("Error fetching statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/count - Broj zapisa
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCount() {
        logger.info("GET /api/euk/ugrozena-lica-t1/count - Fetching count");
        try {
            long count = ugrozenoLiceT1Service.countAll();
            return ResponseEntity.ok(Map.of("totalCount", count));
        } catch (Exception e) {
            logger.error("Error fetching count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // GET /api/euk/ugrozena-lica-t1/test - Test endpoint
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("EUK Ugrožena Lica T1 test endpoint called");
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("service", "EUK Ugrožena Lica T1 API");
        test.put("timestamp", java.time.LocalDateTime.now());
        
        // Test database connection
        try {
            long count = ugrozenoLiceT1Service.countAll();
            test.put("database_status", "CONNECTED");
            test.put("total_records", count);
        } catch (Exception e) {
            test.put("database_status", "ERROR");
            test.put("database_error", e.getMessage());
            logger.error("Database test failed: {}", e.getMessage(), e);
        }
        
        test.put("endpoints", new String[]{
            "GET /api/euk/ugrozena-lica-t1",
            "GET /api/euk/ugrozena-lica-t1/{id}",
            "POST /api/euk/ugrozena-lica-t1",
            "POST /api/euk/ugrozena-lica-t1/batch",
            "PUT /api/euk/ugrozena-lica-t1/{id}",
            "DELETE /api/euk/ugrozena-lica-t1/{id}",
            "GET /api/euk/ugrozena-lica-t1/search/jmbg/{jmbg}",
            "GET /api/euk/ugrozena-lica-t1/search/redni-broj/{redniBroj}",
            "GET /api/euk/ugrozena-lica-t1/search/name",
            "POST /api/euk/ugrozena-lica-t1/search/filters",
            "GET /api/euk/ugrozena-lica-t1/statistics",
            "GET /api/euk/ugrozena-lica-t1/count",
            "GET /api/euk/ugrozena-lica-t1/test"
        });
        test.put("cors_enabled", true);
        test.put("rate_limiting_enabled", true);
        
        return ResponseEntity.ok(test);
    }
}
