package com.sirus.backend.controller;

import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.dto.EukUgrozenoLiceT2Dto;
import com.sirus.backend.dto.PaginatedResponse;
import com.sirus.backend.entity.EukObrasciVrste;
import com.sirus.backend.entity.EukOrganizacionaStruktura;
import com.sirus.backend.service.EukUgrozenoLiceT1Service;
import com.sirus.backend.service.EukUgrozenoLiceT2Service;
import com.sirus.backend.service.EukObrasciVrsteService;
import com.sirus.backend.service.EukOrganizacionaStrukturaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AliasController {
    
    @Autowired
    private EukUgrozenoLiceT1Service ugrozenoLiceT1Service;
    
    @Autowired
    private EukUgrozenoLiceT2Service ugrozenoLiceT2Service;
    
    @Autowired
    private EukObrasciVrsteService obrasciVrsteService;
    
    @Autowired
    private EukOrganizacionaStrukturaService organizacionaStrukturaService;
    
    @GetMapping("/t1-lice")
    public ResponseEntity<PaginatedResponse<EukUgrozenoLiceT1Dto>> getT1Lice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Page<EukUgrozenoLiceT1Dto> lice = ugrozenoLiceT1Service.findAll(page, size);
            return ResponseEntity.ok(PaginatedResponse.from(lice));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/t2-lice")
    public ResponseEntity<PaginatedResponse<EukUgrozenoLiceT2Dto>> getT2Lice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<EukUgrozenoLiceT2Dto> lice = ugrozenoLiceT2Service.getAllUgrozenaLica(pageable);
            return ResponseEntity.ok(PaginatedResponse.from(lice));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/obrasci-vrste")
    public ResponseEntity<List<EukObrasciVrste>> getObrasciVrste() {
        try {
            List<EukObrasciVrste> obrasciVrste = obrasciVrsteService.getAllObrasciVrste();
            return ResponseEntity.ok(obrasciVrste);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/organizaciona-struktura")
    public ResponseEntity<List<EukOrganizacionaStruktura>> getOrganizacionaStruktura() {
        try {
            List<EukOrganizacionaStruktura> organizacionaStruktura = organizacionaStrukturaService.getAllOrganizacionaStruktura();
            return ResponseEntity.ok(organizacionaStruktura);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
