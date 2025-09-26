package com.sirus.backend.controller;

import com.sirus.backend.dto.EukKategorijaDto;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.service.EukKategorijaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/euk/kategorije")
@CrossOrigin(origins = "*")
public class EukKategorijaController {
    
    private static final Logger logger = LoggerFactory.getLogger(EukKategorijaController.class);
    
    @Autowired
    private EukKategorijaService kategorijaService;
    
    @GetMapping
    public ResponseEntity<List<EukKategorijaDto>> getAllKategorije() {
        logger.info("GET /api/euk/kategorije - Fetching all kategorije");
        try {
            List<EukKategorijaDto> kategorije = kategorijaService.findAll();
            return ResponseEntity.ok(kategorije);
        } catch (Exception e) {
            logger.error("Error fetching kategorije: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<EukKategorijaDto>> searchKategorije(
            @RequestParam(required = false) String naziv,
            @RequestParam(required = false) String skracenica) {
        logger.info("GET /api/euk/kategorije/search - Searching kategorije with filters - naziv: {}, skracenica: {}", naziv, skracenica);
        try {
            List<EukKategorijaDto> kategorije = kategorijaService.search(naziv, skracenica);
            return ResponseEntity.ok(kategorije);
        } catch (Exception e) {
            logger.error("Error searching kategorije: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EukKategorijaDto> getKategorijaById(@PathVariable Integer id) {
        logger.info("GET /api/euk/kategorije/{} - Fetching kategorija by ID", id);
        try {
            EukKategorijaDto kategorija = kategorijaService.findById(id);
            return ResponseEntity.ok(kategorija);
        } catch (EukException e) {
            logger.warn("Kategorija not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching kategorija by ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<EukKategorijaDto> createKategorija(@Valid @RequestBody EukKategorijaDto kategorijaDto) {
        logger.info("POST /api/euk/kategorije - Creating new kategorija: {}", kategorijaDto.getNaziv());
        try {
            EukKategorijaDto createdKategorija = kategorijaService.create(kategorijaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdKategorija);
        } catch (EukException e) {
            logger.warn("Error creating kategorija: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating kategorija: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EukKategorijaDto> updateKategorija(@PathVariable Integer id, @Valid @RequestBody EukKategorijaDto kategorijaDto) {
        logger.info("PUT /api/euk/kategorije/{} - Updating kategorija", id);
        try {
            EukKategorijaDto updatedKategorija = kategorijaService.update(id, kategorijaDto);
            return ResponseEntity.ok(updatedKategorija);
        } catch (EukException e) {
            logger.warn("Error updating kategorija: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating kategorija with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKategorija(@PathVariable Integer id) {
        logger.info("DELETE /api/euk/kategorije/{} - Deleting kategorija", id);
        try {
            kategorijaService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EukException e) {
            logger.warn("Error deleting kategorija: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error deleting kategorija with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
