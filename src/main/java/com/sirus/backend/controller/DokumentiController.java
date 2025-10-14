package com.sirus.backend.controller;

import com.sirus.backend.dto.OdbijaSeNSPRequestDTO;
import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.entity.EukUgrozenoLiceT1;
import com.sirus.backend.repository.EukPredmetRepository;
import com.sirus.backend.service.OdbijaSeNSPDocumentService;
import com.sirus.backend.service.EukUgrozenoLiceT1Service;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Controller za generisanje Word dokumenata
 */
@RestController
@RequestMapping("/api/dokumenti")
@CrossOrigin(origins = "*")
public class DokumentiController {
    
    private static final Logger log = LoggerFactory.getLogger(DokumentiController.class);
    
    @Autowired
    private OdbijaSeNSPDocumentService odbijaSeNSPDocumentService;
    
    @Autowired
    private EukPredmetRepository predmetRepository;
    
    @Autowired
    private EukUgrozenoLiceT1Service ugrozenoLiceT1Service;
    
    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Dokumenti service is working!");
    }
    
    /**
     * Endpoint za čuvanje podataka o ugroženom licu u ugrozeno_lice_t1 tabelu
     * 
     * @param request DTO sa podacima o ugroženom licu
     * @return Success/Error response
     */
    @PostMapping("/odbija-se-nsp/sacuvaj-podatke")
    public ResponseEntity<?> sacuvajPodatkeUgrozenoLice(@Valid @RequestBody OdbijaSeNSPRequestDTO request) {
        try {
            log.info("Čuvanje podataka o ugroženom licu za JMBG: {}", request.getJmbg());
            
            // Kreiraj DTO za ugrozeno lice T1
            EukUgrozenoLiceT1Dto ugrozenoLiceDto = new EukUgrozenoLiceT1Dto();
            
            // Mapiraj podatke iz request-a u DTO
            ugrozenoLiceDto.setRedniBroj(generateRedniBroj()); // Generiši redni broj
            ugrozenoLiceDto.setIme(extractIme(request.getImeIPrezimePodnosioca()));
            ugrozenoLiceDto.setPrezime(extractPrezime(request.getImeIPrezimePodnosioca()));
            ugrozenoLiceDto.setJmbg(request.getJmbg());
            ugrozenoLiceDto.setPttBroj(request.getPttBroj());
            ugrozenoLiceDto.setGradOpstina(request.getOpstina());
            ugrozenoLiceDto.setMesto(request.getMestoStanovanja());
            ugrozenoLiceDto.setUlicaIBroj(request.getUlica() + " " + request.getBrojStana());
            ugrozenoLiceDto.setBrojClanovaDomacinstva(Integer.parseInt(request.getBrojClanovaDomacinstava()));
            ugrozenoLiceDto.setOsnovSticanjaStatusa(request.getOsnovPrava());
            ugrozenoLiceDto.setEdBrojBrojMernogUredjaja(""); // Prazno po default-u
            
            // Sačuvaj u bazu
            EukUgrozenoLiceT1 savedEntity = ugrozenoLiceT1Service.save(ugrozenoLiceDto);
            
            log.info("Podaci uspešno sačuvani u ugrozeno_lice_t1 sa ID: {}", savedEntity.getUgrozenoLiceId());
            
            return ResponseEntity.ok().body("Podaci uspešno sačuvani u bazu");
            
        } catch (Exception e) {
            log.error("Greška pri čuvanju podataka o ugroženom licu", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri čuvanju podataka: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint za generisanje dokumenta "OДБИЈА СЕ NSP,UNSP,DD,UDTNP"
     * 
     * @param request DTO sa podacima za generisanje dokumenta
     * @return Word dokument (.doc format)
     */
    @PostMapping("/odbija-se-nsp/generisi")
    public ResponseEntity<byte[]> generisiOdbijaSeNSP(@Valid @RequestBody OdbijaSeNSPRequestDTO request) {
        try {
            log.info("Generisanje dokumenta ODBIJA SE NSP za predmet: {}", request.getBrojPredmeta());
            
            // Automatski sačuvaj podatke u ugrozeno_lice_t1 tabelu
            try {
                EukUgrozenoLiceT1Dto ugrozenoLiceDto = new EukUgrozenoLiceT1Dto();
                
                // Mapiraj podatke iz request-a u DTO
                ugrozenoLiceDto.setRedniBroj(generateRedniBroj()); // Generiši redni broj
                ugrozenoLiceDto.setIme(extractIme(request.getImeIPrezimePodnosioca()));
                ugrozenoLiceDto.setPrezime(extractPrezime(request.getImeIPrezimePodnosioca()));
                ugrozenoLiceDto.setJmbg(request.getJmbg());
                ugrozenoLiceDto.setPttBroj(request.getPttBroj());
                ugrozenoLiceDto.setGradOpstina(request.getOpstina());
                ugrozenoLiceDto.setMesto(request.getMestoStanovanja());
                ugrozenoLiceDto.setUlicaIBroj(request.getUlica() + " " + request.getBrojStana());
                ugrozenoLiceDto.setBrojClanovaDomacinstva(Integer.parseInt(request.getBrojClanovaDomacinstava()));
                ugrozenoLiceDto.setOsnovSticanjaStatusa(request.getOsnovPrava());
                ugrozenoLiceDto.setEdBrojBrojMernogUredjaja(""); // Prazno po default-u
                
                // Sačuvaj u bazu
                EukUgrozenoLiceT1 savedEntity = ugrozenoLiceT1Service.save(ugrozenoLiceDto);
                log.info("Podaci automatski sačuvani u ugrozeno_lice_t1 sa ID: {}", savedEntity.getUgrozenoLiceId());
                
            } catch (Exception e) {
                log.warn("Greška pri automatskom čuvanju podataka u bazu: {}", e.getMessage());
                // Nastavi sa generisanjem dokumenta čak i ako čuvanje u bazu ne uspe
            }
            
            // Generiši dokument
            byte[] documentBytes = odbijaSeNSPDocumentService.generateDocument(request);
            
            // Kreiraj ime fajla (sanitize broj predmeta)
            String fileName = String.format("ODBIJA_SE_NSP_%s.docx", 
                sanitizeFileName(request.getBrojPredmeta()));
            
            // Postavi headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            // Content-Disposition header sa UTF-8 encoding za ćirilične karaktere
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
            headers.setContentDisposition(contentDisposition);
            
            log.info("Dokument uspešno generisan: {}", fileName);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(documentBytes);
                
        } catch (Exception e) {
            log.error("Greška pri generisanju dokumenta ODBIJA SE NSP", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    /**
     * Upload "ODBIJA SE NSP" dokumenta za postojeći predmet
     * 
     * @param predmetId ID predmeta
     * @param file Uploadovani Word dokument
     * @return Success/Error response
     */
    @PostMapping("/odbija-se-nsp/upload/{predmetId}")
    public ResponseEntity<?> uploadOdbijaSeNSP(
            @PathVariable Integer predmetId,
            @RequestParam("file") MultipartFile file) {
        try {
            log.info("Upload ODBIJA SE NSP dokumenta za predmet ID: {}", predmetId);
            
            // Validacija
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Fajl je prazan");
            }
            
            // Proveri tip fajla
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") &&
                 !contentType.equals("application/msword"))) {
                return ResponseEntity.badRequest().body("Dozvoljeni su samo Word dokumenti (.doc ili .docx)");
            }
            
            // Pronađi predmet
            Optional<EukPredmet> predmetOpt = predmetRepository.findById(predmetId);
            if (predmetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            EukPredmet predmet = predmetOpt.get();
            
            // Sačuvaj dokument
            predmet.setOdbijaSeNspDokument(file.getBytes());
            predmet.setOdbijaSeNspDokumentNaziv(file.getOriginalFilename());
            predmet.setOdbijaSeNspDokumentDatum(LocalDateTime.now());
            
            predmetRepository.save(predmet);
            
            log.info("Dokument uspešno uploadovan: {}", file.getOriginalFilename());
            
            return ResponseEntity.ok().body("Dokument uspešno sačuvan");
            
        } catch (Exception e) {
            log.error("Greška pri upload-u dokumenta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Greška pri upload-u dokumenta");
        }
    }
    
    /**
     * Preuzmi sačuvani "ODBIJA SE NSP" dokument iz baze za određeni predmet
     * 
     * @param predmetId ID predmeta
     * @return Word dokument (.docx format)
     */
    @GetMapping("/odbija-se-nsp/preuzmi/{predmetId}")
    public ResponseEntity<byte[]> preuzimiOdbijaSeNSP(@PathVariable Integer predmetId) {
        try {
            log.info("Preuzimanje ODBIJA SE NSP dokumenta za predmet ID: {}", predmetId);
            
            Optional<EukPredmet> predmetOpt = predmetRepository.findById(predmetId);
            
            if (predmetOpt.isEmpty()) {
                log.warn("Predmet sa ID {} nije pronađen", predmetId);
                return ResponseEntity.notFound().build();
            }
            
            EukPredmet predmet = predmetOpt.get();
            
            if (predmet.getOdbijaSeNspDokument() == null || predmet.getOdbijaSeNspDokument().length == 0) {
                log.warn("Dokument za predmet ID {} nije generisan", predmetId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
            }
            
            // Postavi headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            String fileName = predmet.getOdbijaSeNspDokumentNaziv() != null 
                ? predmet.getOdbijaSeNspDokumentNaziv() 
                : "ODBIJA_SE_NSP.docx";
            
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build();
            headers.setContentDisposition(contentDisposition);
            
            log.info("Dokument uspešno preuzet: {}", fileName);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(predmet.getOdbijaSeNspDokument());
                
        } catch (Exception e) {
            log.error("Greška pri preuzimanju ODBIJA SE NSP dokumenta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
    
    /**
     * Proveri da li dokument postoji i vrati metadata
     * 
     * @param predmetId ID predmeta
     * @return Metadata o dokumentu (naziv, datum) ili 404
     */
    @GetMapping("/odbija-se-nsp/info/{predmetId}")
    public ResponseEntity<?> infoOdbijaSeNSP(@PathVariable Integer predmetId) {
        try {
            log.info("Provera ODBIJA SE NSP dokumenta za predmet ID: {}", predmetId);
            
            Optional<EukPredmet> predmetOpt = predmetRepository.findById(predmetId);
            
            if (predmetOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            EukPredmet predmet = predmetOpt.get();
            
            if (predmet.getOdbijaSeNspDokument() == null || predmet.getOdbijaSeNspDokument().length == 0) {
                return ResponseEntity.ok().body(new DokumentInfo(false, null, null));
            }
            
            return ResponseEntity.ok().body(new DokumentInfo(
                true, 
                predmet.getOdbijaSeNspDokumentNaziv(),
                predmet.getOdbijaSeNspDokumentDatum()
            ));
            
        } catch (Exception e) {
            log.error("Greška pri proveri dokumenta", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * Helper klasa za info response
     */
    public static class DokumentInfo {
        private boolean postoji;
        private String naziv;
        private LocalDateTime datum;
        
        public DokumentInfo(boolean postoji, String naziv, LocalDateTime datum) {
            this.postoji = postoji;
            this.naziv = naziv;
            this.datum = datum;
        }
        
        public boolean isPostoji() { return postoji; }
        public void setPostoji(boolean postoji) { this.postoji = postoji; }
        
        public String getNaziv() { return naziv; }
        public void setNaziv(String naziv) { this.naziv = naziv; }
        
        public LocalDateTime getDatum() { return datum; }
        public void setDatum(LocalDateTime datum) { this.datum = datum; }
    }
    
    /**
     * Sanitizuje ime fajla zamenjujući nedozvoljene karaktere
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null) return "dokument";
        
        // Zameni / sa - (npr. 123/2025 -> 123-2025)
        return fileName.replace("/", "-")
                      .replace("\\", "-")
                      .replace(":", "-")
                      .replace("*", "-")
                      .replace("?", "-")
                      .replace("\"", "-")
                      .replace("<", "-")
                      .replace(">", "-")
                      .replace("|", "-")
                      .trim();
    }
    
    /**
     * Ekstraktuje ime iz "Ime Prezime" stringa
     */
    private String extractIme(String imePrezime) {
        if (imePrezime == null || imePrezime.trim().isEmpty()) {
            return "";
        }
        
        String[] parts = imePrezime.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }
    
    /**
     * Ekstraktuje prezime iz "Ime Prezime" stringa
     */
    private String extractPrezime(String imePrezime) {
        if (imePrezime == null || imePrezime.trim().isEmpty()) {
            return "";
        }
        
        String[] parts = imePrezime.trim().split("\\s+");
        if (parts.length > 1) {
            // Spoji sve delove osim prvog (ime) kao prezime
            StringBuilder prezime = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (prezime.length() > 0) {
                    prezime.append(" ");
                }
                prezime.append(parts[i]);
            }
            return prezime.toString();
        }
        return "";
    }
    
    /**
     * Generiše redni broj na osnovu trenutnog vremena
     */
    private String generateRedniBroj() {
        return "RB-" + System.currentTimeMillis();
    }
}

