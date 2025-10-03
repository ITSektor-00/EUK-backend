package com.sirus.backend.controller;

import com.sirus.backend.dto.EukUgrozenoLiceT2Dto;
import com.sirus.backend.service.EukUgrozenoLiceT2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/ugrozeno-lice-t2")
public class EukUgrozenoLiceT2Controller {
    
    @Autowired
    private EukUgrozenoLiceT2Service ugrozenoLiceT2Service;
    
    // Kreiranje novog ugroženog lica
    @PostMapping
    public ResponseEntity<?> createUgrozenoLice(@Valid @RequestBody EukUgrozenoLiceT2Dto dto) {
        try {
            EukUgrozenoLiceT2Dto created = ugrozenoLiceT2Service.createUgrozenoLice(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Ažuriranje postojećeg ugroženog lica
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUgrozenoLice(@PathVariable Integer id, @Valid @RequestBody EukUgrozenoLiceT2Dto dto) {
        try {
            EukUgrozenoLiceT2Dto updated = ugrozenoLiceT2Service.updateUgrozenoLice(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Brisanje ugroženog lica
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUgrozenoLice(@PathVariable Integer id) {
        try {
            ugrozenoLiceT2Service.deleteUgrozenoLice(id);
            return ResponseEntity.ok("Ugroženo lice je uspešno obrisano");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Pronalaženje ugroženog lica po ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUgrozenoLiceById(@PathVariable Integer id) {
        try {
            EukUgrozenoLiceT2Dto dto = ugrozenoLiceT2Service.getUgrozenoLiceById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Pronalaženje ugroženog lica po JMBG
    @GetMapping("/jmbg/{jmbg}")
    public ResponseEntity<?> getUgrozenoLiceByJmbg(@PathVariable String jmbg) {
        try {
            EukUgrozenoLiceT2Dto dto = ugrozenoLiceT2Service.getUgrozenoLiceByJmbg(jmbg);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Pronalaženje ugroženog lica po rednom broju
    @GetMapping("/redni-broj/{redniBroj}")
    public ResponseEntity<?> getUgrozenoLiceByRedniBroj(@PathVariable String redniBroj) {
        try {
            EukUgrozenoLiceT2Dto dto = ugrozenoLiceT2Service.getUgrozenoLiceByRedniBroj(redniBroj);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Pronalaženje svih ugroženih lica sa paginacijom
    @GetMapping
    public ResponseEntity<Page<EukUgrozenoLiceT2Dto>> getAllUgrozenaLica(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ugrozenoLiceId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getAllUgrozenaLica(pageable);
        return ResponseEntity.ok(result);
    }
    
    // Pretraga ugroženih lica po kriterijumu sa paginacijom
    @GetMapping("/search")
    public ResponseEntity<Page<EukUgrozenoLiceT2Dto>> searchUgrozenaLica(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime,
            @RequestParam(required = false) String jmbg,
            @RequestParam(required = false) String gradOpstina,
            @RequestParam(required = false) String mesto,
            @RequestParam(required = false) String edBroj,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ugrozenoLiceId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.searchUgrozenaLica(
                ime, prezime, jmbg, gradOpstina, mesto, edBroj, pageable);
        return ResponseEntity.ok(result);
    }
    
    // Pretraga po imenu i prezimenu sa paginacijom
    @GetMapping("/search/ime-prezime")
    public ResponseEntity<Page<EukUgrozenoLiceT2Dto>> searchByImeAndPrezime(
            @RequestParam String ime,
            @RequestParam String prezime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ugrozenoLiceId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.searchByImeAndPrezime(ime, prezime, pageable);
        return ResponseEntity.ok(result);
    }
    
    // Pretraga po adresnim podacima
    @GetMapping("/search/adresa")
    public ResponseEntity<Page<EukUgrozenoLiceT2Dto>> searchByAddress(
            @RequestParam(required = false) String gradOpstina,
            @RequestParam(required = false) String mesto,
            @RequestParam(required = false) String pttBroj,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ugrozenoLiceId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.searchByAddress(gradOpstina, mesto, pttBroj, pageable);
        return ResponseEntity.ok(result);
    }
    
    // Pretraga po energetskim podacima
    @GetMapping("/search/energetski")
    public ResponseEntity<Page<EukUgrozenoLiceT2Dto>> searchByEnergyData(
            @RequestParam(required = false) String edBroj,
            @RequestParam(required = false) String pokVazenja,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ugrozenoLiceId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.searchByEnergyData(edBroj, pokVazenja, pageable);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje svih ugroženih lica (bez paginacije)
    @GetMapping("/all")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getAllUgrozenaLicaList() {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getAllUgrozenaLicaList();
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po imenu
    @GetMapping("/ime/{ime}")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByIme(@PathVariable String ime) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByIme(ime);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po prezimenu
    @GetMapping("/prezime/{prezime}")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByPrezime(@PathVariable String prezime) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByPrezime(prezime);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po gradu/opštini
    @GetMapping("/grad-opstina/{gradOpstina}")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByGradOpstina(@PathVariable String gradOpstina) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByGradOpstina(gradOpstina);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po mestu
    @GetMapping("/mesto/{mesto}")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByMesto(@PathVariable String mesto) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByMesto(mesto);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po ED broju
    @GetMapping("/ed-broj/{edBroj}")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByEdBroj(@PathVariable String edBroj) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByEdBroj(edBroj);
        return ResponseEntity.ok(result);
    }
    
    // Brojanje ukupnog broja ugroženih lica
    @GetMapping("/count")
    public ResponseEntity<Long> countUgrozenaLica() {
        long count = ugrozenoLiceT2Service.countUgrozenaLica();
        return ResponseEntity.ok(count);
    }
    
    // Brojanje ugroženih lica po kriterijumu
    @GetMapping("/count/search")
    public ResponseEntity<Long> countUgrozenaLicaByCriteria(
            @RequestParam(required = false) String ime,
            @RequestParam(required = false) String prezime,
            @RequestParam(required = false) String jmbg) {
        long count = ugrozenoLiceT2Service.countUgrozenaLicaByCriteria(ime, prezime, jmbg);
        return ResponseEntity.ok(count);
    }
    
    // Provera da li postoji ugroženo lice sa određenim JMBG-om
    @GetMapping("/exists/jmbg/{jmbg}")
    public ResponseEntity<Boolean> existsByJmbg(@PathVariable String jmbg) {
        boolean exists = ugrozenoLiceT2Service.existsByJmbg(jmbg);
        return ResponseEntity.ok(exists);
    }
    
    // Provera da li postoji ugroženo lice sa određenim rednim brojem
    @GetMapping("/exists/redni-broj/{redniBroj}")
    public ResponseEntity<Boolean> existsByRedniBroj(@PathVariable String redniBroj) {
        boolean exists = ugrozenoLiceT2Service.existsByRedniBroj(redniBroj);
        return ResponseEntity.ok(exists);
    }
    
    // Bulk kreiranje ugroženih lica
    @PostMapping("/bulk")
    public ResponseEntity<?> createMultipleUgrozenaLica(@Valid @RequestBody List<EukUgrozenoLiceT2Dto> dtoList) {
        try {
            List<EukUgrozenoLiceT2Dto> created = ugrozenoLiceT2Service.createMultipleUgrozenaLica(dtoList);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Greška: " + e.getMessage());
        }
    }
    
    // Pronalaženje ugroženih lica po više JMBG-ova
    @PostMapping("/search/jmbg-list")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByJmbgList(@RequestBody List<String> jmbgList) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByJmbgList(jmbgList);
        return ResponseEntity.ok(result);
    }
    
    // Pronalaženje ugroženih lica po više rednih brojeva
    @PostMapping("/search/redni-broj-list")
    public ResponseEntity<List<EukUgrozenoLiceT2Dto>> getUgrozenaLicaByRedniBrojList(@RequestBody List<String> redniBrojList) {
        List<EukUgrozenoLiceT2Dto> result = ugrozenoLiceT2Service.getUgrozenaLicaByRedniBrojList(redniBrojList);
        return ResponseEntity.ok(result);
    }
}
