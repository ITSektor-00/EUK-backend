package com.sirus.backend.controller;

import com.sirus.backend.dto.EukPredmetDto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.service.EukPredmetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PredmetiAliasController {
    
    @Autowired
    private EukPredmetService predmetService;
    
    @GetMapping("/predmeti")
    public ResponseEntity<PaginatedResponse<EukPredmetDto>> getAllPredmeti(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String prioritet,
            @RequestParam(required = false) Integer kategorijaId,
            @RequestParam(required = false) String odgovornaOsoba) {
        
        try {
            Page<EukPredmetDto> predmeti = predmetService.findAll(page, size);
            return ResponseEntity.ok(PaginatedResponse.from(predmeti));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
