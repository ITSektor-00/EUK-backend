package com.sirus.backend.controller;

import com.sirus.backend.dto.EukUgrozenoLiceDto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.service.EukUgrozenoLiceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/euk/ugrozena-lica")
@CrossOrigin(origins = "*")
public class EukUgrozenoLiceController {
    
    private static final Logger logger = LoggerFactory.getLogger(EukUgrozenoLiceController.class);
    
    @Autowired
    private EukUgrozenoLiceService ugrozenoLiceService;
    
    @GetMapping
    public ResponseEntity<?> getAllUgrozenaLica(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        logger.info("GET /api/euk/ugrozena-lica - Fetching ugrožena lica with pagination - page: {}, size: {}", page, size);
        
        try {
            // Validacija parametara
            if (page < 0) {
                logger.warn("Invalid page parameter: {}", page);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_PAGE",
                    "message", "Page number must be non-negative",
                    "path", "/api/euk/ugrozena-lica"
                ));
            }
            if (size <= 0 || size > 1000) {
                logger.warn("Invalid size parameter: {}", size);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "INVALID_SIZE", 
                    "message", "Size must be between 1 and 1000",
                    "path", "/api/euk/ugrozena-lica"
                ));
            }
            
            Page<EukUgrozenoLiceDto> ugrozenaLica = ugrozenoLiceService.findAll(page, size);
            logger.info("Successfully fetched {} ugrožena lica for page {}", ugrozenaLica.getContent().size(), page);
            return ResponseEntity.ok(PaginatedResponse.from(ugrozenaLica));
        } catch (Exception e) {
            logger.error("Error fetching ugrožena lica: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "INTERNAL_ERROR",
                "message", "Greška pri dohvatanju podataka",
                "path", "/api/euk/ugrozena-lica"
            ));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EukUgrozenoLiceDto> getUgrozenoLiceById(@PathVariable Integer id) {
        logger.info("GET /api/euk/ugrozena-lica/{} - Fetching ugroženo lice by ID", id);
        try {
            EukUgrozenoLiceDto ugrozenoLice = ugrozenoLiceService.findById(id);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (EukException e) {
            logger.warn("Ugroženo lice not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching ugroženo lice by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<EukUgrozenoLiceDto> createUgrozenoLice(@Valid @RequestBody EukUgrozenoLiceDto ugrozenoLiceDto) {
        logger.info("POST /api/euk/ugrozena-lica - Creating new ugroženo lice: {} {}", 
                   ugrozenoLiceDto.getIme(), ugrozenoLiceDto.getPrezime());
        try {
            EukUgrozenoLiceDto createdUgrozenoLice = ugrozenoLiceService.create(ugrozenoLiceDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUgrozenoLice);
        } catch (EukException e) {
            logger.warn("Error creating ugroženo lice: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating ugroženo lice: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EukUgrozenoLiceDto> updateUgrozenoLice(@PathVariable Integer id, @Valid @RequestBody EukUgrozenoLiceDto ugrozenoLiceDto) {
        logger.info("PUT /api/euk/ugrozena-lica/{} - Updating ugroženo lice", id);
        try {
            EukUgrozenoLiceDto updatedUgrozenoLice = ugrozenoLiceService.update(id, ugrozenoLiceDto);
            return ResponseEntity.ok(updatedUgrozenoLice);
        } catch (EukException e) {
            logger.warn("Error updating ugroženo lice: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating ugroženo lice with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUgrozenoLice(@PathVariable Integer id) {
        logger.info("DELETE /api/euk/ugrozena-lica/{} - Deleting ugroženo lice", id);
        try {
            ugrozenoLiceService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EukException e) {
            logger.warn("Error deleting ugroženo lice: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting ugroženo lice with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search/{jmbg}")
    public ResponseEntity<EukUgrozenoLiceDto> searchByJmbg(@PathVariable String jmbg) {
        logger.info("GET /api/euk/ugrozena-lica/search/{} - Searching ugroženo lice by JMBG", jmbg);
        try {
            EukUgrozenoLiceDto ugrozenoLice = ugrozenoLiceService.findByJmbg(jmbg);
            return ResponseEntity.ok(ugrozenoLice);
        } catch (EukException e) {
            logger.warn("Ugroženo lice not found by JMBG: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error searching ugroženo lice by JMBG {}: {}", jmbg, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        logger.info("EUK Ugrožena Lica test endpoint called");
        Map<String, Object> test = new HashMap<>();
        test.put("status", "OK");
        test.put("service", "EUK Ugrožena Lica API");
        test.put("timestamp", java.time.LocalDateTime.now());
        test.put("endpoints", new String[]{
            "GET /api/euk/ugrozena-lica",
            "GET /api/euk/ugrozena-lica/{id}",
            "POST /api/euk/ugrozena-lica",
            "PUT /api/euk/ugrozena-lica/{id}",
            "DELETE /api/euk/ugrozena-lica/{id}",
            "GET /api/euk/ugrozena-lica/search/{jmbg}",
            "GET /api/euk/ugrozena-lica/test"
        });
        test.put("cors_enabled", true);
        test.put("rate_limiting_enabled", true);
        
        return ResponseEntity.ok(test);
    }
}
