package com.sirus.backend.service;

import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.entity.EukUgrozenoLiceT1;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.repository.EukUgrozenoLiceT1Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class EukUgrozenoLiceT1Service {
    
    private static final Logger logger = LoggerFactory.getLogger(EukUgrozenoLiceT1Service.class);
    
    @Autowired
    private EukUgrozenoLiceT1Repository ugrozenoLiceT1Repository;
    
    // Osnovne CRUD operacije
    public Page<EukUgrozenoLiceT1Dto> findAll(int page, int size) {
        logger.info("Fetching EUK ugrožena lica T1 with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<EukUgrozenoLiceT1> results = ugrozenoLiceT1Repository.findLatestRecords(pageable);
        return results.map(this::convertToDto);
    }
    
    public EukUgrozenoLiceT1Dto findById(Integer id) {
        logger.info("Fetching EUK ugroženo lice T1 with ID: {}", id);
        EukUgrozenoLiceT1 ugrozenoLice = ugrozenoLiceT1Repository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        return convertToDto(ugrozenoLice);
    }
    
    @Transactional
    public EukUgrozenoLiceT1Dto create(EukUgrozenoLiceT1Dto dto) {
        logger.info("Creating new EUK ugroženo lice T1: {} {}", dto.getIme(), dto.getPrezime());
        
        validateUgrozenoLiceT1Data(dto);
        
        // Check if JMBG already exists
        if (ugrozenoLiceT1Repository.existsByJmbg(dto.getJmbg())) {
            throw new EukException("Osoba sa JMBG " + dto.getJmbg() + " već postoji u sistemu");
        }
        
        // Check if redni broj already exists
        if (ugrozenoLiceT1Repository.existsByRedniBroj(dto.getRedniBroj())) {
            throw new EukException("Redni broj " + dto.getRedniBroj() + " već postoji u sistemu");
        }
        
        EukUgrozenoLiceT1 ugrozenoLice = convertToEntity(dto);
        EukUgrozenoLiceT1 savedUgrozenoLice = ugrozenoLiceT1Repository.save(ugrozenoLice);
        logger.info("Created EUK ugroženo lice T1 with ID: {}", savedUgrozenoLice.getUgrozenoLiceId());
        
        return convertToDto(savedUgrozenoLice);
    }
    
    
    @Transactional
    public EukUgrozenoLiceT1Dto update(Integer id, EukUgrozenoLiceT1Dto dto) {
        logger.info("Updating EUK ugroženo lice T1 with ID: {}", id);
        
        EukUgrozenoLiceT1 ugrozenoLice = ugrozenoLiceT1Repository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        
        validateUgrozenoLiceT1Data(dto);
        
        // Check if JMBG already exists for different person
        if (!ugrozenoLice.getJmbg().equals(dto.getJmbg()) && 
            ugrozenoLiceT1Repository.existsByJmbg(dto.getJmbg())) {
            throw new EukException("Osoba sa JMBG " + dto.getJmbg() + " već postoji u sistemu");
        }
        
        // Check if redni broj already exists for different person
        if (!ugrozenoLice.getRedniBroj().equals(dto.getRedniBroj()) && 
            ugrozenoLiceT1Repository.existsByRedniBroj(dto.getRedniBroj())) {
            throw new EukException("Redni broj " + dto.getRedniBroj() + " već postoji u sistemu");
        }
        
        updateEntityFromDto(ugrozenoLice, dto);
        EukUgrozenoLiceT1 savedUgrozenoLice = ugrozenoLiceT1Repository.save(ugrozenoLice);
        logger.info("Updated EUK ugroženo lice T1 with ID: {}", id);
        
        return convertToDto(savedUgrozenoLice);
    }
    
    @Transactional
    public void delete(Integer id) {
        logger.info("Deleting EUK ugroženo lice T1 with ID: {}", id);
        
        EukUgrozenoLiceT1 ugrozenoLice = ugrozenoLiceT1Repository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        
        ugrozenoLiceT1Repository.delete(ugrozenoLice);
        logger.info("Deleted EUK ugroženo lice T1 with ID: {}", id);
    }
    
    // Pretrage
    public EukUgrozenoLiceT1Dto findByJmbg(String jmbg) {
        logger.info("Searching EUK ugroženo lice T1 by JMBG: {}", jmbg);
        EukUgrozenoLiceT1 ugrozenoLice = ugrozenoLiceT1Repository.findByJmbg(jmbg)
                .orElseThrow(() -> new EukException("Osoba sa JMBG " + jmbg + " nije pronađena"));
        return convertToDto(ugrozenoLice);
    }
    
    public EukUgrozenoLiceT1Dto findByRedniBroj(String redniBroj) {
        logger.info("Searching EUK ugroženo lice T1 by redni broj: {}", redniBroj);
        EukUgrozenoLiceT1 ugrozenoLice = ugrozenoLiceT1Repository.findByRedniBroj(redniBroj)
                .orElseThrow(() -> new EukException("Osoba sa rednim brojem " + redniBroj + " nije pronađena"));
        return convertToDto(ugrozenoLice);
    }
    
    public Page<EukUgrozenoLiceT1Dto> searchByName(String ime, String prezime, int page, int size) {
        logger.info("Searching EUK ugrožena lica T1 by name: {} {}", ime, prezime);
        Pageable pageable = PageRequest.of(page, size);
        List<EukUgrozenoLiceT1> results = ugrozenoLiceT1Repository.findByImeOrPrezimeContaining(ime, prezime);
        // Convert to page manually since repository returns List
        return convertListToPage(results, pageable);
    }
    
    public Page<EukUgrozenoLiceT1Dto> searchWithFilters(Map<String, Object> filters, int page, int size) {
        logger.info("Searching EUK ugrožena lica T1 with filters: {}", filters);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<EukUgrozenoLiceT1> results = ugrozenoLiceT1Repository.findWithFilters(
            (String) filters.get("jmbg"),
            (String) filters.get("redniBroj"),
            (String) filters.get("ime"),
            (String) filters.get("prezime"),
            (String) filters.get("gradOpstina"),
            (String) filters.get("mesto"),
            (String) filters.get("pttBroj"),
            (String) filters.get("osnovStatusa"),
            (String) filters.get("edBroj"),
            (String) filters.get("brojRacuna"),
            (LocalDate) filters.get("datumOd"),
            (LocalDate) filters.get("datumDo"),
            (BigDecimal) filters.get("iznosOd"),
            (BigDecimal) filters.get("iznosDo"),
            pageable
        );
        
        return results.map(this::convertToDto);
    }
    
    // Statistike
    public Map<String, Object> getStatistics() {
        logger.info("Fetching EUK ugrožena lica T1 statistics");
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalRecords", ugrozenoLiceT1Repository.count());
        stats.put("sumIznosUmanjenjaSaPdv", ugrozenoLiceT1Repository.sumIznosUmanjenjaSaPdv());
        // Try new queries first, fallback to old queries if needed
        try {
            stats.put("avgPotrosnjaKwh", ugrozenoLiceT1Repository.avgPotrosnjaKwh());
            stats.put("avgZagrevanaPovrsinaM2", ugrozenoLiceT1Repository.avgZagrevanaPovrsinaM2());
        } catch (Exception e) {
            logger.warn("New energy queries failed, using fallback queries: {}", e.getMessage());
            stats.put("avgPotrosnjaKwh", ugrozenoLiceT1Repository.avgPotrosnjaKwhOld());
            stats.put("avgZagrevanaPovrsinaM2", ugrozenoLiceT1Repository.avgZagrevanaPovrsinaM2Old());
        }
        
        return stats;
    }
    
    public long countAll() {
        logger.info("Counting total EUK ugrožena lica T1");
        return ugrozenoLiceT1Repository.count();
    }
    
    // Pretraga po kategoriji (osnov sticanja statusa)
    public Page<EukUgrozenoLiceT1Dto> findByKategorijaSkracenica(String kategorijaSkracenica, int page, int size) {
        logger.info("Searching EUK ugrožena lica T1 by kategorija skracenica: {}", kategorijaSkracenica);
        Pageable pageable = PageRequest.of(page, size);
        Page<EukUgrozenoLiceT1> results = ugrozenoLiceT1Repository.findByKategorijaSkracenica(kategorijaSkracenica, pageable);
        return results.map(this::convertToDto);
    }
    
    // Statistike po kategorijama
    public Map<String, Long> getStatisticsByKategorija() {
        logger.info("Fetching EUK ugrožena lica T1 statistics by kategorija");
        List<Object[]> results = ugrozenoLiceT1Repository.countByKategorijaSkracenica();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] result : results) {
            String kategorijaSkracenica = (String) result[0];
            Long count = (Long) result[1];
            stats.put(kategorijaSkracenica, count);
        }
        return stats;
    }
    
    
    // Validacije
    private void validateUgrozenoLiceT1Data(EukUgrozenoLiceT1Dto dto) {
        if (dto.getDatumIzdavanjaRacuna() != null && dto.getDatumIzdavanjaRacuna().isAfter(LocalDate.now())) {
            throw new EukException("Datum izdavanja računa ne može biti u budućnosti");
        }
        
        if (dto.getJmbg() == null || !dto.getJmbg().matches("^\\d{13}$")) {
            throw new EukException("JMBG mora sadržati tačno 13 cifara");
        }
        
        if (dto.getRedniBroj() == null || dto.getRedniBroj().trim().isEmpty()) {
            throw new EukException("Redni broj je obavezan");
        }
        
        // Validacija osnova sticanja statusa - fleksibilnija validacija
        if (dto.getOsnovSticanjaStatusa() != null && !dto.getOsnovSticanjaStatusa().trim().isEmpty()) {
            String status = dto.getOsnovSticanjaStatusa().trim().toUpperCase();
            // Prihvati osnovne vrednosti
            if (!status.equals("MP") && !status.equals("NSP") && !status.equals("DD") && !status.equals("UDTNP")) {
                // Loguj upozorenje ali ne blokiraj
                logger.warn("Osnov sticanja statusa '{}' nije u standardnim vrednostima (MP, NSP, DD, UDTNP), ali se prihvata", dto.getOsnovSticanjaStatusa());
            }
        }
    }
    
    // Konverzije
    private EukUgrozenoLiceT1Dto convertToDto(EukUgrozenoLiceT1 entity) {
        EukUgrozenoLiceT1Dto dto = new EukUgrozenoLiceT1Dto();
        dto.setUgrozenoLiceId(entity.getUgrozenoLiceId());
        dto.setRedniBroj(entity.getRedniBroj());
        dto.setIme(entity.getIme());
        dto.setPrezime(entity.getPrezime());
        dto.setJmbg(entity.getJmbg());
        dto.setPttBroj(entity.getPttBroj());
        dto.setGradOpstina(entity.getGradOpstina());
        dto.setMesto(entity.getMesto());
        dto.setUlicaIBroj(entity.getUlicaIBroj());
        dto.setBrojClanovaDomacinstva(entity.getBrojClanovaDomacinstva());
        dto.setOsnovSticanjaStatusa(entity.getOsnovSticanjaStatusa());
        dto.setEdBrojBrojMernogUredjaja(entity.getEdBrojBrojMernogUredjaja());
        dto.setPotrosnjaIPovrsinaCombined(entity.getPotrosnjaIPovrsinaCombined());
        dto.setPotrosnjaKwh(entity.getPotrosnjaKwh());
        dto.setZagrevanaPovrsinaM2(entity.getZagrevanaPovrsinaM2());
        dto.setIznosUmanjenjaSaPdv(entity.getIznosUmanjenjaSaPdv());
        dto.setBrojRacuna(entity.getBrojRacuna());
        dto.setDatumIzdavanjaRacuna(entity.getDatumIzdavanjaRacuna());
        dto.setDatumTrajanjaPrava(entity.getDatumTrajanjaPrava());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
    
    private EukUgrozenoLiceT1 convertToEntity(EukUgrozenoLiceT1Dto dto) {
        EukUgrozenoLiceT1 entity = new EukUgrozenoLiceT1();
        entity.setRedniBroj(dto.getRedniBroj());
        entity.setIme(dto.getIme());
        entity.setPrezime(dto.getPrezime());
        entity.setJmbg(dto.getJmbg());
        entity.setPttBroj(dto.getPttBroj());
        entity.setGradOpstina(dto.getGradOpstina());
        entity.setMesto(dto.getMesto());
        entity.setUlicaIBroj(dto.getUlicaIBroj());
        entity.setBrojClanovaDomacinstva(dto.getBrojClanovaDomacinstva());
        entity.setOsnovSticanjaStatusa(dto.getOsnovSticanjaStatusa());
        entity.setEdBrojBrojMernogUredjaja(dto.getEdBrojBrojMernogUredjaja());
        // Handle combined field - if provided, use it; otherwise use individual fields
        if (dto.getPotrosnjaIPovrsinaCombined() != null && !dto.getPotrosnjaIPovrsinaCombined().isEmpty()) {
            entity.setPotrosnjaIPovrsinaCombined(dto.getPotrosnjaIPovrsinaCombined());
        } else {
            // Use individual fields to build combined field
            entity.setPotrosnjaKwh(dto.getPotrosnjaKwh());
            entity.setZagrevanaPovrsinaM2(dto.getZagrevanaPovrsinaM2());
        }
        entity.setIznosUmanjenjaSaPdv(dto.getIznosUmanjenjaSaPdv());
        entity.setBrojRacuna(dto.getBrojRacuna());
        entity.setDatumIzdavanjaRacuna(dto.getDatumIzdavanjaRacuna());
        entity.setDatumTrajanjaPrava(dto.getDatumTrajanjaPrava());
        return entity;
    }
    
    private void updateEntityFromDto(EukUgrozenoLiceT1 entity, EukUgrozenoLiceT1Dto dto) {
        entity.setRedniBroj(dto.getRedniBroj());
        entity.setIme(dto.getIme());
        entity.setPrezime(dto.getPrezime());
        entity.setJmbg(dto.getJmbg());
        entity.setPttBroj(dto.getPttBroj());
        entity.setGradOpstina(dto.getGradOpstina());
        entity.setMesto(dto.getMesto());
        entity.setUlicaIBroj(dto.getUlicaIBroj());
        entity.setBrojClanovaDomacinstva(dto.getBrojClanovaDomacinstva());
        entity.setOsnovSticanjaStatusa(dto.getOsnovSticanjaStatusa());
        entity.setEdBrojBrojMernogUredjaja(dto.getEdBrojBrojMernogUredjaja());
        // Handle combined field - if provided, use it; otherwise use individual fields
        if (dto.getPotrosnjaIPovrsinaCombined() != null && !dto.getPotrosnjaIPovrsinaCombined().isEmpty()) {
            entity.setPotrosnjaIPovrsinaCombined(dto.getPotrosnjaIPovrsinaCombined());
        } else {
            // Use individual fields to build combined field
            entity.setPotrosnjaKwh(dto.getPotrosnjaKwh());
            entity.setZagrevanaPovrsinaM2(dto.getZagrevanaPovrsinaM2());
        }
        entity.setIznosUmanjenjaSaPdv(dto.getIznosUmanjenjaSaPdv());
        entity.setBrojRacuna(dto.getBrojRacuna());
        entity.setDatumIzdavanjaRacuna(dto.getDatumIzdavanjaRacuna());
        entity.setDatumTrajanjaPrava(dto.getDatumTrajanjaPrava());
    }
    
    private Page<EukUgrozenoLiceT1Dto> convertListToPage(List<EukUgrozenoLiceT1> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        
        if (start >= list.size()) {
            return Page.empty(pageable);
        }
        
        List<EukUgrozenoLiceT1> pageContent = list.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(
            pageContent.stream().map(this::convertToDto).collect(java.util.stream.Collectors.toList()),
            pageable,
            list.size()
        );
    }
}
