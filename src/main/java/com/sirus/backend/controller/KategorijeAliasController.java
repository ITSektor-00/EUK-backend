package com.sirus.backend.controller;

import com.sirus.backend.dto.EukKategorijaDto;
import com.sirus.backend.service.EukKategorijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class KategorijeAliasController {
    
    @Autowired
    private EukKategorijaService kategorijaService;
    
    @GetMapping("/kategorije")
    public ResponseEntity<List<EukKategorijaDto>> getAllKategorije() {
        try {
            List<EukKategorijaDto> kategorije = kategorijaService.findAll();
            return ResponseEntity.ok(kategorije);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
