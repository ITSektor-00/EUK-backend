package com.sirus.backend.controller;

import com.sirus.backend.dto.EukPredmetFormularPodaciDto;
import com.sirus.backend.service.EukPredmetFormularPodaciService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/euk/predmet-formular-podaci")
@CrossOrigin(origins = "*")
public class EukPredmetFormularPodaciController {
    
    @Autowired
    private EukPredmetFormularPodaciService podaciService;
    
    // =====================================================
    // CRUD OPERACIJE
    // =====================================================
    
    @PostMapping
    public ResponseEntity<EukPredmetFormularPodaciDto> createPodatak(@RequestBody EukPredmetFormularPodaciDto podaciDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            EukPredmetFormularPodaciDto createdPodatak = podaciService.create(podaciDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPodatak);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{podatakId}")
    public ResponseEntity<EukPredmetFormularPodaciDto> updatePodatak(@PathVariable Integer podatakId, @RequestBody EukPredmetFormularPodaciDto podaciDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            EukPredmetFormularPodaciDto updatedPodatak = podaciService.update(podatakId, podaciDto, userId);
            return ResponseEntity.ok(updatedPodatak);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{podatakId}")
    public ResponseEntity<Void> deletePodatak(@PathVariable Integer podatakId) {
        try {
            podaciService.delete(podatakId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{podatakId}")
    public ResponseEntity<EukPredmetFormularPodaciDto> getPodatakById(@PathVariable Integer podatakId) {
        try {
            EukPredmetFormularPodaciDto podatak = podaciService.getById(podatakId);
            return ResponseEntity.ok(podatak);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // =====================================================
    // FILTRIRANJE PO PREDMETU
    // =====================================================
    
    @GetMapping("/predmet/{predmetId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmet(@PathVariable Integer predmetId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmet(predmetId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormular(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormular(predmetId, formularId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/kategorija/{kategorijaId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndKategorija(@PathVariable Integer predmetId, @PathVariable Integer kategorijaId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndKategorija(predmetId, kategorijaId);
        return ResponseEntity.ok(podaci);
    }
    
    // =====================================================
    // FILTRIRANJE PO POLJU
    // =====================================================
    
    @GetMapping("/polje/{poljeId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPolje(@PathVariable Integer poljeId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPolje(poljeId);
        return ResponseEntity.ok(podaci);
    }
    
    // =====================================================
    // FILTRIRANJE PO KORISNIKU
    // =====================================================
    
    @GetMapping("/korisnik/{korisnikId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByKorisnik(@PathVariable Integer korisnikId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByKorisnik(korisnikId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/korisnik/{korisnikId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndKorisnik(@PathVariable Integer predmetId, @PathVariable Integer korisnikId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndKorisnik(predmetId, korisnikId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/polje/{poljeId}/korisnik/{korisnikId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPoljeAndKorisnik(@PathVariable Integer poljeId, @PathVariable Integer korisnikId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPoljeAndKorisnik(poljeId, korisnikId);
        return ResponseEntity.ok(podaci);
    }
    
    // =====================================================
    // FILTRIRANJE PO DATUMU
    // =====================================================
    
    @GetMapping("/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByDatum(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByDatum(start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/predmet/{predmetId}/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndDatum(@PathVariable Integer predmetId, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndDatum(predmetId, start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/polje/{poljeId}/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPoljeAndDatum(@PathVariable Integer poljeId, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPoljeAndDatum(poljeId, start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/korisnik/{korisnikId}/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByKorisnikAndDatum(@PathVariable Integer korisnikId, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByKorisnikAndDatum(korisnikId, start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // =====================================================
    // KOMBINOVANO FILTRIRANJE
    // =====================================================
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/korisnik/{korisnikId}")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularAndKorisnik(@PathVariable Integer predmetId, @PathVariable Integer formularId, @PathVariable Integer korisnikId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularAndKorisnik(predmetId, formularId, korisnikId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularAndDatum(@PathVariable Integer predmetId, @PathVariable Integer formularId, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularAndDatum(predmetId, formularId, start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/korisnik/{korisnikId}/datum")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularAndKorisnikAndDatum(@PathVariable Integer predmetId, @PathVariable Integer formularId, @PathVariable Integer korisnikId, @RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularAndKorisnikAndDatum(predmetId, formularId, korisnikId, start, end);
            return ResponseEntity.ok(podaci);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // =====================================================
    // PRETRAGA
    // =====================================================
    
    @GetMapping("/search/vrednost")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> searchPodaciByVrednost(@RequestParam String vrednost) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.searchByVrednost(vrednost);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/search/vrednost")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> searchPodaciByPredmetAndVrednost(@PathVariable Integer predmetId, @RequestParam String vrednost) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.searchByPredmetAndVrednost(predmetId, vrednost);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/polje/{poljeId}/search/vrednost")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> searchPodaciByPoljeAndVrednost(@PathVariable Integer poljeId, @RequestParam String vrednost) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.searchByPoljeAndVrednost(poljeId, vrednost);
        return ResponseEntity.ok(podaci);
    }
    
    // =====================================================
    // BATCH OPERACIJE
    // =====================================================
    
    @PostMapping("/batch")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> saveBatch(@RequestBody Map<String, Object> podaci, @RequestParam Integer predmetId, @RequestParam Integer formularId, @RequestHeader("X-User-Id") Integer userId) {
        try {
            List<EukPredmetFormularPodaciDto> savedPodaci = podaciService.saveBatch(podaci, predmetId, formularId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPodaci);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/predmet/{predmetId}")
    public ResponseEntity<Void> deletePodaciByPredmet(@PathVariable Integer predmetId) {
        try {
            podaciService.deleteByPredmet(predmetId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/predmet/{predmetId}/formular/{formularId}")
    public ResponseEntity<Void> deletePodaciByPredmetAndFormular(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        try {
            podaciService.deleteByPredmetAndFormular(predmetId, formularId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/polje/{poljeId}")
    public ResponseEntity<Void> deletePodaciByPolje(@PathVariable Integer poljeId) {
        try {
            podaciService.deleteByPolje(poljeId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // =====================================================
    // FILTERI
    // =====================================================
    
    @GetMapping("/predmet/{predmetId}/vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetWithVrednosti(@PathVariable Integer predmetId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetWithVrednosti(predmetId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/bez-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetWithoutVrednosti(@PathVariable Integer predmetId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetWithoutVrednosti(predmetId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/prazne-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetWithEmptyVrednosti(@PathVariable Integer predmetId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetWithEmptyVrednosti(predmetId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/ne-prazne-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetWithNonEmptyVrednosti(@PathVariable Integer predmetId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetWithNonEmptyVrednosti(predmetId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularWithVrednosti(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularWithVrednosti(predmetId, formularId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/bez-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularWithoutVrednosti(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularWithoutVrednosti(predmetId, formularId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/prazne-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularWithEmptyVrednosti(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularWithEmptyVrednosti(predmetId, formularId);
        return ResponseEntity.ok(podaci);
    }
    
    @GetMapping("/predmet/{predmetId}/formular/{formularId}/ne-prazne-vrednosti")
    public ResponseEntity<List<EukPredmetFormularPodaciDto>> getPodaciByPredmetAndFormularWithNonEmptyVrednosti(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        List<EukPredmetFormularPodaciDto> podaci = podaciService.getByPredmetAndFormularWithNonEmptyVrednosti(predmetId, formularId);
        return ResponseEntity.ok(podaci);
    }
    
    // =====================================================
    // STATISTIKE
    // =====================================================
    
    @GetMapping("/stats/predmet/{predmetId}")
    public ResponseEntity<Map<String, Object>> getStatsByPredmet(@PathVariable Integer predmetId) {
        long count = podaciService.countByPredmet(predmetId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/polje/{poljeId}")
    public ResponseEntity<Map<String, Object>> getStatsByPolje(@PathVariable Integer poljeId) {
        long count = podaciService.countByPolje(poljeId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/korisnik/{korisnikId}")
    public ResponseEntity<Map<String, Object>> getStatsByKorisnik(@PathVariable Integer korisnikId) {
        long count = podaciService.countByKorisnik(korisnikId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/datum")
    public ResponseEntity<Map<String, Object>> getStatsByDatum(@RequestParam String startDate, @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            long count = podaciService.countByDatum(start, end);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/stats/predmet/{predmetId}/formular/{formularId}")
    public ResponseEntity<Map<String, Object>> getStatsByPredmetAndFormular(@PathVariable Integer predmetId, @PathVariable Integer formularId) {
        long count = podaciService.countByPredmetAndFormular(predmetId, formularId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/predmet/{predmetId}/kategorija/{kategorijaId}")
    public ResponseEntity<Map<String, Object>> getStatsByPredmetAndKategorija(@PathVariable Integer predmetId, @PathVariable Integer kategorijaId) {
        long count = podaciService.countByPredmetAndKategorija(predmetId, kategorijaId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    // =====================================================
    // VALIDACIJA
    // =====================================================
    
    @GetMapping("/exists/predmet/{predmetId}/polje/{poljeId}")
    public ResponseEntity<Map<String, Object>> existsByPredmetAndPolje(@PathVariable Integer predmetId, @PathVariable Integer poljeId) {
        boolean exists = podaciService.existsByPredmetAndPolje(predmetId, poljeId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/exists/predmet/{predmetId}/polje/{poljeId}/vrednost")
    public ResponseEntity<Map<String, Object>> existsByPredmetAndPoljeAndVrednost(@PathVariable Integer predmetId, @PathVariable Integer poljeId, @RequestParam String vrednost) {
        boolean exists = podaciService.existsByPredmetAndPoljeAndVrednost(predmetId, poljeId, vrednost);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/exists/predmet/{predmetId}/polje/{poljeId}/vrednost/{vrednost}/podatak/{podatakId}")
    public ResponseEntity<Map<String, Object>> existsByPredmetAndPoljeAndVrednostAndPodatakIdNot(@PathVariable Integer predmetId, @PathVariable Integer poljeId, @PathVariable String vrednost, @PathVariable Integer podatakId) {
        boolean exists = podaciService.existsByPredmetAndPoljeAndVrednostAndPodatakIdNot(predmetId, poljeId, vrednost, podatakId);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
