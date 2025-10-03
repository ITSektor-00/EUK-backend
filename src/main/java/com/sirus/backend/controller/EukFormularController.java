package com.sirus.backend.controller;

import com.sirus.backend.dto.EukFormularDto;
import com.sirus.backend.dto.EukFormularPoljeDto;
import com.sirus.backend.service.EukFormularService;
import com.sirus.backend.service.EukFormularPoljeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/euk/formulari")
public class EukFormularController {
    
    @Autowired
    private EukFormularService formularService;
    
    @Autowired
    private EukFormularPoljeService poljeService;
    
    // =====================================================
    // CRUD OPERACIJE
    // =====================================================
    
    @PostMapping
    public ResponseEntity<EukFormularDto> createFormular(@RequestBody EukFormularDto formularDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            EukFormularDto createdFormular = formularService.create(formularDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFormular);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{formularId}")
    public ResponseEntity<EukFormularDto> updateFormular(@PathVariable Integer formularId, @RequestBody EukFormularDto formularDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            EukFormularDto updatedFormular = formularService.update(formularId, formularDto, userId);
            return ResponseEntity.ok(updatedFormular);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{formularId}")
    public ResponseEntity<Void> deleteFormular(@PathVariable Integer formularId, @RequestHeader("X-User-Id") Integer userId) {
        try {
            formularService.delete(formularId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{formularId}")
    public ResponseEntity<EukFormularDto> getFormularById(@PathVariable Integer formularId) {
        try {
            EukFormularDto formular = formularService.getById(formularId);
            return ResponseEntity.ok(formular);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{formularId}/polja")
    public ResponseEntity<EukFormularDto> getFormularWithPolja(@PathVariable Integer formularId) {
        try {
            EukFormularDto formular = formularService.getByIdWithPolja(formularId);
            return ResponseEntity.ok(formular);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<EukFormularDto>> getAllFormulari() {
        List<EukFormularDto> formulari = formularService.getAll();
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<EukFormularDto>> getAllFormulari(Pageable pageable) {
        Page<EukFormularDto> formulari = formularService.getAll(pageable);
        return ResponseEntity.ok(formulari);
    }
    
    // =====================================================
    // FILTRIRANJE PO KATEGORIJI
    // =====================================================
    
    @GetMapping("/kategorija/{kategorijaId}")
    public ResponseEntity<List<EukFormularDto>> getFormulariByKategorija(@PathVariable Integer kategorijaId) {
        List<EukFormularDto> formulari = formularService.getByKategorija(kategorijaId);
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/kategorija/{kategorijaId}/page")
    public ResponseEntity<Page<EukFormularDto>> getFormulariByKategorija(@PathVariable Integer kategorijaId, Pageable pageable) {
        Page<EukFormularDto> formulari = formularService.getByKategorija(kategorijaId, pageable);
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/kategorija/{kategorijaId}/polja")
    public ResponseEntity<List<EukFormularDto>> getFormulariByKategorijaWithPolja(@PathVariable Integer kategorijaId) {
        List<EukFormularDto> formulari = formularService.getByKategorijaWithPolja(kategorijaId);
        return ResponseEntity.ok(formulari);
    }
    
    // =====================================================
    // PRETRAGA
    // =====================================================
    
    @GetMapping("/search")
    public ResponseEntity<List<EukFormularDto>> searchFormulariByNaziv(@RequestParam String naziv) {
        List<EukFormularDto> formulari = formularService.searchByNaziv(naziv);
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/search/page")
    public ResponseEntity<Page<EukFormularDto>> searchFormulariByNaziv(@RequestParam String naziv, Pageable pageable) {
        Page<EukFormularDto> formulari = formularService.searchByNaziv(naziv, pageable);
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/search/kategorija/{kategorijaId}")
    public ResponseEntity<List<EukFormularDto>> searchFormulariByKategorijaAndNaziv(@PathVariable Integer kategorijaId, @RequestParam String naziv) {
        List<EukFormularDto> formulari = formularService.searchByKategorijaAndNaziv(kategorijaId, naziv);
        return ResponseEntity.ok(formulari);
    }
    
    @GetMapping("/search/kategorija/{kategorijaId}/page")
    public ResponseEntity<Page<EukFormularDto>> searchFormulariByKategorijaAndNaziv(@PathVariable Integer kategorijaId, @RequestParam String naziv, Pageable pageable) {
        Page<EukFormularDto> formulari = formularService.searchByKategorijaAndNaziv(kategorijaId, naziv, pageable);
        return ResponseEntity.ok(formulari);
    }
    
    // =====================================================
    // POLJA FORMULARE
    // =====================================================
    
    @PostMapping("/{formularId}/polja")
    public ResponseEntity<EukFormularPoljeDto> createPolje(@PathVariable Integer formularId, @RequestBody EukFormularPoljeDto poljeDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            poljeDto.setFormularId(formularId);
            EukFormularPoljeDto createdPolje = poljeService.create(poljeDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPolje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/polja/{poljeId}")
    public ResponseEntity<EukFormularPoljeDto> updatePolje(@PathVariable Integer poljeId, @RequestBody EukFormularPoljeDto poljeDto, @RequestHeader("X-User-Id") Integer userId) {
        try {
            EukFormularPoljeDto updatedPolje = poljeService.update(poljeId, poljeDto, userId);
            return ResponseEntity.ok(updatedPolje);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/polja/{poljeId}")
    public ResponseEntity<Void> deletePolje(@PathVariable Integer poljeId, @RequestHeader("X-User-Id") Integer userId) {
        try {
            poljeService.delete(poljeId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{formularId}/polja/list")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormular(@PathVariable Integer formularId) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormular(formularId);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/podaci")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormularWithPodaci(@PathVariable Integer formularId) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormularWithPodaci(formularId);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/dokumenti")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormularWithDokumenti(@PathVariable Integer formularId) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormularWithDokumenti(formularId);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/visible/{visible}")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormularAndVisible(@PathVariable Integer formularId, @PathVariable Boolean visible) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormularAndVisible(formularId, visible);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/obavezno/{obavezno}")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormularAndObavezno(@PathVariable Integer formularId, @PathVariable Boolean obavezno) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormularAndObavezno(formularId, obavezno);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/tip/{tipPolja}")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByFormularAndTip(@PathVariable Integer formularId, @PathVariable String tipPolja) {
        List<EukFormularPoljeDto> polja = poljeService.getByFormularAndTip(formularId, tipPolja);
        return ResponseEntity.ok(polja);
    }
    
    // =====================================================
    // REDOSLED POLJA
    // =====================================================
    
    @PutMapping("/polja/{poljeId}/redosled")
    public ResponseEntity<Void> updateRedosledPolja(@PathVariable Integer poljeId, @RequestParam Integer noviRedosled, @RequestHeader("X-User-Id") Integer userId) {
        try {
            poljeService.updateRedosled(poljeId, noviRedosled, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/polja/{poljeId}/move-up")
    public ResponseEntity<Void> movePoljeUp(@PathVariable Integer poljeId, @RequestHeader("X-User-Id") Integer userId) {
        try {
            poljeService.movePoljeUp(poljeId, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/polja/{poljeId}/move-down")
    public ResponseEntity<Void> movePoljeDown(@PathVariable Integer poljeId, @RequestHeader("X-User-Id") Integer userId) {
        try {
            poljeService.movePoljeDown(poljeId, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // =====================================================
    // STATISTIKE
    // =====================================================
    
    @GetMapping("/stats/kategorija/{kategorijaId}")
    public ResponseEntity<Map<String, Object>> getStatsByKategorija(@PathVariable Integer kategorijaId) {
        long count = formularService.countByKategorija(kategorijaId);
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/aktivni")
    public ResponseEntity<Map<String, Object>> getStatsAktivnih() {
        long count = formularService.countAktivnih();
        return ResponseEntity.ok(Map.of("count", count));
    }
    
    @GetMapping("/stats/broj-polja")
    public ResponseEntity<List<Object[]>> getStatsBrojPolja() {
        List<Object[]> stats = formularService.getFormulariWithBrojPolja();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/stats/broj-polja/kategorija/{kategorijaId}")
    public ResponseEntity<List<Object[]>> getStatsBrojPoljaByKategorija(@PathVariable Integer kategorijaId) {
        List<Object[]> stats = formularService.getFormulariWithBrojPoljaByKategorija(kategorijaId);
        return ResponseEntity.ok(stats);
    }
    
    // =====================================================
    // PRETRAGA POLJA
    // =====================================================
    
    @GetMapping("/polja/search/naziv")
    public ResponseEntity<List<EukFormularPoljeDto>> searchPoljaByNaziv(@RequestParam String nazivPolja) {
        List<EukFormularPoljeDto> polja = poljeService.searchByNaziv(nazivPolja);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/polja/search/label")
    public ResponseEntity<List<EukFormularPoljeDto>> searchPoljaByLabel(@RequestParam String label) {
        List<EukFormularPoljeDto> polja = poljeService.searchByLabel(label);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/search/naziv")
    public ResponseEntity<List<EukFormularPoljeDto>> searchPoljaByFormularAndNaziv(@PathVariable Integer formularId, @RequestParam String nazivPolja) {
        List<EukFormularPoljeDto> polja = poljeService.searchByFormularAndNaziv(formularId, nazivPolja);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/{formularId}/polja/search/label")
    public ResponseEntity<List<EukFormularPoljeDto>> searchPoljaByFormularAndLabel(@PathVariable Integer formularId, @RequestParam String label) {
        List<EukFormularPoljeDto> polja = poljeService.searchByFormularAndLabel(formularId, label);
        return ResponseEntity.ok(polja);
    }
    
    @GetMapping("/polja/tip/{tipPolja}")
    public ResponseEntity<List<EukFormularPoljeDto>> getPoljaByTip(@PathVariable String tipPolja) {
        List<EukFormularPoljeDto> polja = poljeService.getByTip(tipPolja);
        return ResponseEntity.ok(polja);
    }
    
    // =====================================================
    // STATISTIKE POLJA
    // =====================================================
    
    @GetMapping("/{formularId}/polja/stats")
    public ResponseEntity<Map<String, Object>> getStatsPoljaByFormular(@PathVariable Integer formularId) {
        long count = poljeService.countByFormular(formularId);
        long obaveznih = poljeService.countObaveznihByFormular(formularId);
        return ResponseEntity.ok(Map.of("count", count, "obaveznih", obaveznih));
    }
    
    @GetMapping("/polja/stats/tip/{tipPolja}")
    public ResponseEntity<Map<String, Object>> getStatsPoljaByTip(@PathVariable String tipPolja) {
        long count = poljeService.countByTipPolja(tipPolja);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
