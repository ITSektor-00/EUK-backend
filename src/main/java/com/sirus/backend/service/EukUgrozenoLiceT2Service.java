package com.sirus.backend.service;

import com.sirus.backend.dto.EukUgrozenoLiceT2Dto;
import com.sirus.backend.entity.EukUgrozenoLiceT2;
import com.sirus.backend.repository.EukUgrozenoLiceT2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EukUgrozenoLiceT2Service {
    
    @Autowired
    private EukUgrozenoLiceT2Repository ugrozenoLiceT2Repository;
    
    // Konvertovanje Entity u DTO
    private EukUgrozenoLiceT2Dto convertToDto(EukUgrozenoLiceT2 entity) {
        EukUgrozenoLiceT2Dto dto = new EukUgrozenoLiceT2Dto();
        dto.setUgrozenoLiceId(entity.getUgrozenoLiceId());
        dto.setRedniBroj(entity.getRedniBroj());
        dto.setIme(entity.getIme());
        dto.setPrezime(entity.getPrezime());
        dto.setJmbg(entity.getJmbg());
        dto.setPttBroj(entity.getPttBroj());
        dto.setGradOpstina(entity.getGradOpstina());
        dto.setMesto(entity.getMesto());
        dto.setUlicaIBroj(entity.getUlicaIBroj());
        dto.setEdBroj(entity.getEdBroj());
        dto.setPokVazenjaResenjaOStatusu(entity.getPokVazenjaResenjaOStatusu());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    // Konvertovanje DTO u Entity
    private EukUgrozenoLiceT2 convertToEntity(EukUgrozenoLiceT2Dto dto) {
        EukUgrozenoLiceT2 entity = new EukUgrozenoLiceT2();
        entity.setUgrozenoLiceId(dto.getUgrozenoLiceId());
        entity.setRedniBroj(dto.getRedniBroj());
        entity.setIme(dto.getIme());
        entity.setPrezime(dto.getPrezime());
        entity.setJmbg(dto.getJmbg());
        entity.setPttBroj(dto.getPttBroj());
        entity.setGradOpstina(dto.getGradOpstina());
        entity.setMesto(dto.getMesto());
        entity.setUlicaIBroj(dto.getUlicaIBroj());
        entity.setEdBroj(dto.getEdBroj());
        entity.setPokVazenjaResenjaOStatusu(dto.getPokVazenjaResenjaOStatusu());
        
        // Postavljanje audit kolona
        if (dto.getUgrozenoLiceId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        } else {
            entity.setCreatedAt(dto.getCreatedAt());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        
        return entity;
    }
    
    // Kreiranje novog ugroženog lica
    public EukUgrozenoLiceT2Dto createUgrozenoLice(EukUgrozenoLiceT2Dto dto) {
        // Provera da li već postoji JMBG
        if (ugrozenoLiceT2Repository.existsByJmbg(dto.getJmbg())) {
            throw new RuntimeException("Ugroženo lice sa JMBG " + dto.getJmbg() + " već postoji");
        }
        
        // Provera da li već postoji redni broj
        if (ugrozenoLiceT2Repository.existsByRedniBroj(dto.getRedniBroj())) {
            throw new RuntimeException("Ugroženo lice sa rednim brojem " + dto.getRedniBroj() + " već postoji");
        }
        
        EukUgrozenoLiceT2 entity = convertToEntity(dto);
        EukUgrozenoLiceT2 savedEntity = ugrozenoLiceT2Repository.save(entity);
        return convertToDto(savedEntity);
    }
    
    // Ažuriranje postojećeg ugroženog lica
    public EukUgrozenoLiceT2Dto updateUgrozenoLice(Integer id, EukUgrozenoLiceT2Dto dto) {
        Optional<EukUgrozenoLiceT2> existingEntity = ugrozenoLiceT2Repository.findById(id);
        if (existingEntity.isEmpty()) {
            throw new RuntimeException("Ugroženo lice sa ID " + id + " nije pronađeno");
        }
        
        EukUgrozenoLiceT2 entity = existingEntity.get();
        
        // Provera JMBG-a ako se menja
        if (!entity.getJmbg().equals(dto.getJmbg()) && ugrozenoLiceT2Repository.existsByJmbg(dto.getJmbg())) {
            throw new RuntimeException("Ugroženo lice sa JMBG " + dto.getJmbg() + " već postoji");
        }
        
        // Provera rednog broja ako se menja
        if (!entity.getRedniBroj().equals(dto.getRedniBroj()) && ugrozenoLiceT2Repository.existsByRedniBroj(dto.getRedniBroj())) {
            throw new RuntimeException("Ugroženo lice sa rednim brojem " + dto.getRedniBroj() + " već postoji");
        }
        
        // Ažuriranje polja
        entity.setRedniBroj(dto.getRedniBroj());
        entity.setIme(dto.getIme());
        entity.setPrezime(dto.getPrezime());
        entity.setJmbg(dto.getJmbg());
        entity.setPttBroj(dto.getPttBroj());
        entity.setGradOpstina(dto.getGradOpstina());
        entity.setMesto(dto.getMesto());
        entity.setUlicaIBroj(dto.getUlicaIBroj());
        entity.setEdBroj(dto.getEdBroj());
        entity.setPokVazenjaResenjaOStatusu(dto.getPokVazenjaResenjaOStatusu());
        entity.setUpdatedAt(LocalDateTime.now());
        
        EukUgrozenoLiceT2 savedEntity = ugrozenoLiceT2Repository.save(entity);
        return convertToDto(savedEntity);
    }
    
    // Brisanje ugroženog lica
    public void deleteUgrozenoLice(Integer id) {
        if (!ugrozenoLiceT2Repository.existsById(id)) {
            throw new RuntimeException("Ugroženo lice sa ID " + id + " nije pronađeno");
        }
        ugrozenoLiceT2Repository.deleteById(id);
    }
    
    // Pronalaženje ugroženog lica po ID
    public EukUgrozenoLiceT2Dto getUgrozenoLiceById(Integer id) {
        Optional<EukUgrozenoLiceT2> entity = ugrozenoLiceT2Repository.findById(id);
        if (entity.isEmpty()) {
            throw new RuntimeException("Ugroženo lice sa ID " + id + " nije pronađeno");
        }
        return convertToDto(entity.get());
    }
    
    // Pronalaženje ugroženog lica po JMBG
    public EukUgrozenoLiceT2Dto getUgrozenoLiceByJmbg(String jmbg) {
        Optional<EukUgrozenoLiceT2> entity = ugrozenoLiceT2Repository.findByJmbg(jmbg);
        if (entity.isEmpty()) {
            throw new RuntimeException("Ugroženo lice sa JMBG " + jmbg + " nije pronađeno");
        }
        return convertToDto(entity.get());
    }
    
    // Pronalaženje ugroženog lica po rednom broju
    public EukUgrozenoLiceT2Dto getUgrozenoLiceByRedniBroj(String redniBroj) {
        Optional<EukUgrozenoLiceT2> entity = ugrozenoLiceT2Repository.findByRedniBroj(redniBroj);
        if (entity.isEmpty()) {
            throw new RuntimeException("Ugroženo lice sa rednim brojem " + redniBroj + " nije pronađeno");
        }
        return convertToDto(entity.get());
    }
    
    // Pronalaženje svih ugroženih lica sa paginacijom
    public Page<EukUgrozenoLiceT2Dto> getAllUgrozenaLica(Pageable pageable) {
        Page<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findAll(pageable);
        return entities.map(this::convertToDto);
    }
    
    // Pretraga ugroženih lica po kriterijumu sa paginacijom
    public Page<EukUgrozenoLiceT2Dto> searchUgrozenaLica(
            String ime, String prezime, String jmbg, 
            String gradOpstina, String mesto, String edBroj, 
            Pageable pageable) {
        Page<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findBySearchCriteria(
                ime, prezime, jmbg, gradOpstina, mesto, edBroj, pageable);
        return entities.map(this::convertToDto);
    }
    
    // Pretraga po imenu i prezimenu sa paginacijom
    public Page<EukUgrozenoLiceT2Dto> searchByImeAndPrezime(String ime, String prezime, Pageable pageable) {
        Page<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByImeAndPrezimeContaining(ime, prezime, pageable);
        return entities.map(this::convertToDto);
    }
    
    // Pretraga po adresnim podacima
    public Page<EukUgrozenoLiceT2Dto> searchByAddress(String gradOpstina, String mesto, String pttBroj, Pageable pageable) {
        Page<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByAddressCriteria(gradOpstina, mesto, pttBroj, pageable);
        return entities.map(this::convertToDto);
    }
    
    // Pretraga po energetskim podacima
    public Page<EukUgrozenoLiceT2Dto> searchByEnergyData(String edBroj, String pokVazenja, Pageable pageable) {
        Page<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByEnergyCriteria(edBroj, pokVazenja, pageable);
        return entities.map(this::convertToDto);
    }
    
    // Pronalaženje svih ugroženih lica (bez paginacije)
    public List<EukUgrozenoLiceT2Dto> getAllUgrozenaLicaList() {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findAll();
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po imenu
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByIme(String ime) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByImeIgnoreCase(ime);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po prezimenu
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByPrezime(String prezime) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByPrezimeIgnoreCase(prezime);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po gradu/opštini
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByGradOpstina(String gradOpstina) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByGradOpstinaIgnoreCase(gradOpstina);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po mestu
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByMesto(String mesto) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByMestoIgnoreCase(mesto);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po ED broju
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByEdBroj(String edBroj) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByEdBroj(edBroj);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Brojanje ukupnog broja ugroženih lica
    public long countUgrozenaLica() {
        return ugrozenoLiceT2Repository.count();
    }
    
    // Brojanje ugroženih lica po kriterijumu
    public long countUgrozenaLicaByCriteria(String ime, String prezime, String jmbg) {
        return ugrozenoLiceT2Repository.countBySearchCriteria(ime, prezime, jmbg);
    }
    
    // Provera da li postoji ugroženo lice sa određenim JMBG-om
    public boolean existsByJmbg(String jmbg) {
        return ugrozenoLiceT2Repository.existsByJmbg(jmbg);
    }
    
    // Provera da li postoji ugroženo lice sa određenim rednim brojem
    public boolean existsByRedniBroj(String redniBroj) {
        return ugrozenoLiceT2Repository.existsByRedniBroj(redniBroj);
    }
    
    // Bulk operacije
    public List<EukUgrozenoLiceT2Dto> createMultipleUgrozenaLica(List<EukUgrozenoLiceT2Dto> dtoList) {
        List<EukUgrozenoLiceT2> entities = dtoList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
        
        List<EukUgrozenoLiceT2> savedEntities = ugrozenoLiceT2Repository.saveAll(entities);
        return savedEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po više JMBG-ova
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByJmbgList(List<String> jmbgList) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByJmbgIn(jmbgList);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Pronalaženje ugroženih lica po više rednih brojeva
    public List<EukUgrozenoLiceT2Dto> getUgrozenaLicaByRedniBrojList(List<String> redniBrojList) {
        List<EukUgrozenoLiceT2> entities = ugrozenoLiceT2Repository.findByRedniBrojIn(redniBrojList);
        return entities.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
