package com.sirus.backend.controller;

import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.service.EukUgrozenoLiceT1Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * Legacy controller for backward compatibility with old frontend.
 * This controller redirects requests to the new EukUgrozenoLiceT1 endpoints.
 * 
 * @deprecated Use EukUgrozenoLiceT1Controller instead
 */
@RestController
@RequestMapping("/api/euk/ugrozena-lica")
@CrossOrigin(origins = "*")
@Deprecated
public class EukUgrozenoLiceController {

    private static final Logger logger = LoggerFactory.getLogger(EukUgrozenoLiceController.class);

    @Autowired
    private EukUgrozenoLiceT1Service ugrozenoLiceT1Service;

    @GetMapping
    public ResponseEntity<?> getAllUgrozenaLica(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("GET /api/euk/ugrozena-lica (LEGACY) - Redirecting to T1 endpoint with pagination - page: {}, size: {}", page, size);

        try {
            if (page < 0) {
                logger.warn("Invalid page parameter: {}", page);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_PAGE",
                    "message", "Page number must be non-negative",
                    "path", "/api/euk/ugrozena-lica"
                ));
            }
            if (size <= 0 || size > 50000) {
                logger.warn("Invalid size parameter: {}", size);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_SIZE",
                    "message", "Size must be between 1 and 50000",
                    "path", "/api/euk/ugrozena-lica"
                ));
            }

            Page<EukUgrozenoLiceT1Dto> ugrozenaLica = ugrozenoLiceT1Service.findAll(page, size);
            logger.info("Successfully fetched {} ugrožena lica via legacy endpoint for page {}", ugrozenaLica.getContent().size(), page);
            return ResponseEntity.ok(PaginatedResponse.from(ugrozenaLica));
        } catch (Exception e) {
            logger.error("DETAILED ERROR fetching ugrožena lica via legacy endpoint: {}", e.getMessage(), e);
            logger.error("Error class: {}", e.getClass().getSimpleName());
            if (e.getCause() != null) {
                logger.error("Root cause: {}", e.getCause().getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri dohvatanju podataka: " + e.getMessage(),
                "path", "/api/euk/ugrozena-lica",
                "details", e.getClass().getSimpleName()
            ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> getUgrozenoLiceById(@PathVariable Integer id) {
        logger.info("GET /api/euk/ugrozena-lica/{} (LEGACY) - Redirecting to T1 endpoint", id);
        try {
            EukUgrozenoLiceT1Dto ugrozenoLice = ugrozenoLiceT1Service.findById(id);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (Exception e) {
            logger.error("Error fetching ugroženo lice by ID {} via legacy endpoint: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<EukUgrozenoLiceT1Dto> createUgrozenoLice(@RequestBody EukUgrozenoLiceT1Dto ugrozenoLiceDto) {
        logger.info("POST /api/euk/ugrozena-lica (LEGACY) - Redirecting to T1 endpoint");
        try {
            EukUgrozenoLiceT1Dto createdUgrozenoLice = ugrozenoLiceT1Service.create(ugrozenoLiceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUgrozenoLice);
        } catch (Exception e) {
            logger.error("Error creating ugroženo lice via legacy endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> updateUgrozenoLice(@PathVariable Integer id, @RequestBody EukUgrozenoLiceT1Dto ugrozenoLiceDto) {
        logger.info("PUT /api/euk/ugrozena-lica/{} (LEGACY) - Redirecting to T1 endpoint", id);
        try {
            EukUgrozenoLiceT1Dto updatedUgrozenoLice = ugrozenoLiceT1Service.update(id, ugrozenoLiceDto);
            return ResponseEntity.ok(updatedUgrozenoLice);
        } catch (Exception e) {
            logger.error("Error updating ugroženo lice via legacy endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUgrozenoLice(@PathVariable Integer id) {
        logger.info("DELETE /api/euk/ugrozena-lica/{} (LEGACY) - Redirecting to T1 endpoint", id);
        try {
            ugrozenoLiceT1Service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting ugroženo lice via legacy endpoint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search/{jmbg}")
    public ResponseEntity<EukUgrozenoLiceT1Dto> searchByJmbg(@PathVariable String jmbg) {
        logger.info("GET /api/euk/ugrozena-lica/search/{} (LEGACY) - Redirecting to T1 endpoint", jmbg);
        try {
            EukUgrozenoLiceT1Dto ugrozenoLice = ugrozenoLiceT1Service.findByJmbg(jmbg);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (Exception e) {
            logger.error("Error searching ugroženo lice by JMBG {} via legacy endpoint: {}", jmbg, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("EUK Ugrožena Lica (LEGACY) test endpoint called");
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("service", "EUK Ugrožena Lica API (LEGACY - DEPRECATED)");
        test.put("timestamp", java.time.LocalDateTime.now());
        test.put("note", "This endpoint is deprecated. Please use /api/euk/ugrozena-lica-t1 instead");

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
            "GET /api/euk/ugrozena-lica (LEGACY - DEPRECATED)",
            "GET /api/euk/ugrozena-lica/{id} (LEGACY - DEPRECATED)",
            "POST /api/euk/ugrozena-lica (LEGACY - DEPRECATED)",
            "PUT /api/euk/ugrozena-lica/{id} (LEGACY - DEPRECATED)",
            "DELETE /api/euk/ugrozena-lica/{id} (LEGACY - DEPRECATED)",
            "GET /api/euk/ugrozena-lica/search/{jmbg} (LEGACY - DEPRECATED)",
            "GET /api/euk/ugrozena-lica/test (LEGACY - DEPRECATED)"
        });
        test.put("migration_note", "Please migrate to /api/euk/ugrozena-lica-t1 endpoints");
        test.put("cors_enabled", true);

        return ResponseEntity.ok(test);
    }
}
