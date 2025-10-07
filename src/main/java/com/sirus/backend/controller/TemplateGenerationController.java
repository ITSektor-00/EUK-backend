package com.sirus.backend.controller;

import com.sirus.backend.dto.TemplateGenerationRequestDto;
import com.sirus.backend.dto.TemplateGenerationResponseDto;
import com.sirus.backend.entity.EukObrasciVrste;
import com.sirus.backend.entity.EukOrganizacionaStruktura;
import com.sirus.backend.service.EukObrasciVrsteService;
import com.sirus.backend.service.EukOrganizacionaStrukturaService;
import com.sirus.backend.service.TemplateGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api/template")
@CrossOrigin(origins = "*")
public class TemplateGenerationController {
    
    @Autowired
    private TemplateGenerationService templateGenerationService;
    
    @Autowired
    private EukObrasciVrsteService obrasciVrsteService;
    
    @Autowired
    private EukOrganizacionaStrukturaService organizacionaStrukturaService;
    
    // Test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Template generation service is working!");
    }
    
    // Endpoint za generisanje template-a
    @PostMapping("/generate")
    public ResponseEntity<TemplateGenerationResponseDto> generateTemplate(
            @RequestBody TemplateGenerationRequestDto request) {
        
        try {
            // Debug logovanje
            System.out.println("=== DEBUG REQUEST ===");
            System.out.println("Received request: " + request);
            System.out.println("LiceId: " + request.getLiceId() + " (type: " + (request.getLiceId() != null ? request.getLiceId().getClass().getSimpleName() : "null") + ")");
            System.out.println("LiceTip: " + request.getLiceTip());
            System.out.println("KategorijaId: " + request.getKategorijaId() + " (type: " + (request.getKategorijaId() != null ? request.getKategorijaId().getClass().getSimpleName() : "null") + ")");
            System.out.println("ObrasciVrsteId: " + request.getObrasciVrsteId());
            System.out.println("OrganizacionaStrukturaId: " + request.getOrganizacionaStrukturaId());
            System.out.println("PredmetId: " + request.getPredmetId());
            System.out.println("ManualData: " + request.getManualData());
            System.out.println("=== END DEBUG ===");
            
            // Validacija request-a
            if (request.getLiceId() == null || request.getLiceId() <= 0) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Lice ID je obavezno", false));
            }
            
            if (request.getLiceTip() == null || (!request.getLiceTip().equals("t1") && !request.getLiceTip().equals("t2"))) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Tip lice mora biti 't1' ili 't2'", false));
            }
            
            if (request.getKategorijaId() == null || request.getKategorijaId() <= 0) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Kategorija ID je obavezan (trenutno: " + request.getKategorijaId() + ")", false));
            }
            
            if (request.getObrasciVrsteId() == null || request.getObrasciVrsteId() <= 0) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Obrasci vrste ID je obavezan", false));
            }
            
            if (request.getOrganizacionaStrukturaId() == null || request.getOrganizacionaStrukturaId() <= 0) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Organizaciona struktura ID je obavezan", false));
            }
            
            if (request.getPredmetId() == null || request.getPredmetId() <= 0) {
                return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                    request.getPredmetId(), null, "error", null, "Predmet ID je obavezan", false));
            }
            
            TemplateGenerationResponseDto response = templateGenerationService.generateTemplate(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new TemplateGenerationResponseDto(
                request.getPredmetId(), null, "error", null, "Greška: " + e.getMessage(), false));
        }
    }
    
    // Endpoint za dobijanje svih obrasci vrste
    @GetMapping("/obrasci-vrste")
    public ResponseEntity<List<EukObrasciVrste>> getAllObrasciVrste() {
        List<EukObrasciVrste> obrasciVrste = obrasciVrsteService.getAllObrasciVrste();
        return ResponseEntity.ok(obrasciVrste);
    }
    
    // Endpoint za dobijanje obrasci vrste po ID
    @GetMapping("/obrasci-vrste/{id}")
    public ResponseEntity<EukObrasciVrste> getObrasciVrsteById(@PathVariable Long id) {
        return obrasciVrsteService.getObrasciVrsteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Endpoint za dobijanje svih organizaciona struktura
    @GetMapping("/organizaciona-struktura")
    public ResponseEntity<List<EukOrganizacionaStruktura>> getAllOrganizacionaStruktura() {
        List<EukOrganizacionaStruktura> organizacionaStruktura = organizacionaStrukturaService.getAllOrganizacionaStruktura();
        return ResponseEntity.ok(organizacionaStruktura);
    }
    
    // Endpoint za dobijanje organizaciona struktura po ID
    @GetMapping("/organizaciona-struktura/{id}")
    public ResponseEntity<EukOrganizacionaStruktura> getOrganizacionaStrukturaById(@PathVariable Long id) {
        return organizacionaStrukturaService.getOrganizacionaStrukturaById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Endpoint za kreiranje nove obrasci vrste
    @PostMapping("/obrasci-vrste")
    public ResponseEntity<EukObrasciVrste> createObrasciVrste(@RequestBody EukObrasciVrste obrasciVrste) {
        EukObrasciVrste saved = obrasciVrsteService.saveObrasciVrste(obrasciVrste);
        return ResponseEntity.ok(saved);
    }
    
    // Endpoint za kreiranje nove organizaciona struktura
    @PostMapping("/organizaciona-struktura")
    public ResponseEntity<EukOrganizacionaStruktura> createOrganizacionaStruktura(@RequestBody EukOrganizacionaStruktura organizacionaStruktura) {
        EukOrganizacionaStruktura saved = organizacionaStrukturaService.saveOrganizacionaStruktura(organizacionaStruktura);
        return ResponseEntity.ok(saved);
    }
    
    // Endpoint za ažuriranje obrasci vrste
    @PutMapping("/obrasci-vrste/{id}")
    public ResponseEntity<EukObrasciVrste> updateObrasciVrste(@PathVariable Long id, @RequestBody EukObrasciVrste obrasciVrste) {
        if (!obrasciVrsteService.getObrasciVrsteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        obrasciVrste.setId(id);
        EukObrasciVrste updated = obrasciVrsteService.saveObrasciVrste(obrasciVrste);
        return ResponseEntity.ok(updated);
    }
    
    // Endpoint za ažuriranje organizaciona struktura
    @PutMapping("/organizaciona-struktura/{id}")
    public ResponseEntity<EukOrganizacionaStruktura> updateOrganizacionaStruktura(@PathVariable Long id, @RequestBody EukOrganizacionaStruktura organizacionaStruktura) {
        if (!organizacionaStrukturaService.getOrganizacionaStrukturaById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        organizacionaStruktura.setId(id);
        EukOrganizacionaStruktura updated = organizacionaStrukturaService.saveOrganizacionaStruktura(organizacionaStruktura);
        return ResponseEntity.ok(updated);
    }
    
    // Endpoint za brisanje obrasci vrste
    @DeleteMapping("/obrasci-vrste/{id}")
    public ResponseEntity<Void> deleteObrasciVrste(@PathVariable Long id) {
        if (!obrasciVrsteService.getObrasciVrsteById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        obrasciVrsteService.deleteObrasciVrste(id);
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint za brisanje organizaciona struktura
    @DeleteMapping("/organizaciona-struktura/{id}")
    public ResponseEntity<Void> deleteOrganizacionaStruktura(@PathVariable Long id) {
        if (!organizacionaStrukturaService.getOrganizacionaStrukturaById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        organizacionaStrukturaService.deleteOrganizacionaStruktura(id);
        return ResponseEntity.noContent().build();
    }
    
    // Endpoint za download generisanog template-a
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadTemplate(@RequestParam String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(file.toPath());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
