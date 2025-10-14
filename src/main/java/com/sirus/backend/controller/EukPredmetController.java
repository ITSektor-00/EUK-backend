package com.sirus.backend.controller;

import com.sirus.backend.dto.EukPredmetDto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.service.EukPredmetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/euk/predmeti")
public class EukPredmetController {
    
    private static final Logger logger = LoggerFactory.getLogger(EukPredmetController.class);
    
    @Autowired
    private EukPredmetService predmetService;
    
    
    @GetMapping
    public ResponseEntity<PaginatedResponse<EukPredmetDto>> getAllPredmeti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String prioritet,
            @RequestParam(required = false) Integer kategorijaId,
            @RequestParam(required = false) String odgovornaOsoba,
            @RequestParam(required = false) String nazivPredmeta) {
        
        logger.info("GET /api/euk/predmeti - Fetching predmeti with filters - page: {}, size: {}, status: {}, prioritet: {}, kategorijaId: {}, odgovornaOsoba: {}, nazivPredmeta: {}", 
                   page, size, status, prioritet, kategorijaId, odgovornaOsoba, nazivPredmeta);
        
        try {
            Page<EukPredmetDto> predmeti;
            
            if (status != null || prioritet != null || kategorijaId != null || odgovornaOsoba != null || nazivPredmeta != null) {
                // Use filtered search
                EukPredmet.Status statusEnum = status != null ? EukPredmet.Status.valueOf(status.toUpperCase()) : null;
                EukPredmet.Prioritet prioritetEnum = prioritet != null ? EukPredmet.Prioritet.valueOf(prioritet.toUpperCase()) : null;
                
                predmeti = predmetService.findAllWithFilters(statusEnum, prioritetEnum, kategorijaId, odgovornaOsoba, nazivPredmeta, page, size);
            } else {
                // Use regular search
                predmeti = predmetService.findAll(page, size);
            }
            
            return ResponseEntity.ok(PaginatedResponse.from(predmeti));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid filter parameter: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error fetching predmeti: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EukPredmetDto> getPredmetById(@PathVariable Integer id) {
        logger.info("GET /api/euk/predmeti/{} - Fetching predmet by ID", id);
        try {
            EukPredmetDto predmet = predmetService.findById(id);
            return ResponseEntity.ok(predmet);
        } catch (EukException e) {
            logger.warn("Predmet not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching predmet by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/nazivi")
    public ResponseEntity<List<String>> getAllNaziviPredmeta() {
        logger.info("GET /api/euk/predmeti/nazivi - Fetching all predmet nazivi");
        try {
            List<String> nazivi = predmetService.findAllNaziviPredmeta();
            return ResponseEntity.ok(nazivi);
        } catch (Exception e) {
            logger.error("Error fetching predmet nazivi: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<EukPredmetDto> createPredmet(@Valid @RequestBody EukPredmetDto predmetDto) {
        logger.info("POST /api/euk/predmeti - Creating new predmet: {}", predmetDto.getNazivPredmeta());
        try {
            EukPredmetDto createdPredmet = predmetService.create(predmetDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPredmet);
        } catch (EukException e) {
            logger.warn("Error creating predmet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid input while creating predmet: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("Error creating predmet: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EukPredmetDto> updatePredmet(@PathVariable Integer id, @Valid @RequestBody EukPredmetDto predmetDto) {
        logger.info("PUT /api/euk/predmeti/{} - Updating predmet", id);
        try {
            EukPredmetDto updatedPredmet = predmetService.update(id, predmetDto);
            return ResponseEntity.ok(updatedPredmet);
        } catch (EukException e) {
            logger.warn("Error updating predmet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating predmet with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePredmet(@PathVariable Integer id) {
        logger.info("DELETE /api/euk/predmeti/{} - Deleting predmet", id);
        try {
            predmetService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EukException e) {
            logger.warn("Error deleting predmet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting predmet with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
