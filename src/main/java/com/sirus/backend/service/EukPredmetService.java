package com.sirus.backend.service;

import com.sirus.backend.dto.EukPredmetDto;
import com.sirus.backend.entity.EukKategorija;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.repository.EukKategorijaRepository;
import com.sirus.backend.repository.EukPredmetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EukPredmetService {
    
    private static final Logger logger = LoggerFactory.getLogger(EukPredmetService.class);
    
    @Autowired
    private EukPredmetRepository predmetRepository;
    
    @Autowired
    private EukKategorijaRepository kategorijaRepository;
    
    public Page<EukPredmetDto> findAll(int page, int size) {
        logger.info("Fetching EUK predmeti with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = predmetRepository.findAllWithKategorijaAndUgrozenaLicaCount(pageable);
        return results.map(this::convertToDtoFromObjectArray);
    }
    
    public Page<EukPredmetDto> findAllWithFilters(
            EukPredmet.Status status,
            EukPredmet.Prioritet prioritet,
            Integer kategorijaId,
            String odgovornaOsoba,
            String nazivPredmeta,
            int page,
            int size) {
        logger.info("Fetching EUK predmeti with filters - status: {}, prioritet: {}, kategorijaId: {}, odgovornaOsoba: {}, nazivPredmeta: {}", 
                   status, prioritet, kategorijaId, odgovornaOsoba, nazivPredmeta);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = predmetRepository.findAllWithFilters(status, prioritet, kategorijaId, odgovornaOsoba, nazivPredmeta, pageable);
        return results.map(this::convertToDtoFromObjectArray);
    }
    
    public EukPredmetDto findById(Integer id) {
        logger.info("Fetching EUK predmet with ID: {}", id);
        EukPredmet predmet = predmetRepository.findByIdWithKategorijaAndUgrozenaLica(id);
        if (predmet == null) {
            throw new EukException("Predmet sa ID " + id + " nije pronađen");
        }
        return convertToDto(predmet);
    }
    
    public List<String> findAllNaziviPredmeta() {
        logger.info("Fetching all EUK predmet nazivi");
        return predmetRepository.findAllNaziviPredmeta();
    }
    
    @Transactional
    public EukPredmetDto create(EukPredmetDto dto) {
        logger.info("Creating new EUK predmet: {}", dto.getNazivPredmeta());
        
        validatePredmetData(dto);
        
        EukKategorija kategorija = kategorijaRepository.findById(dto.getKategorijaId())
                .orElseThrow(() -> new EukException("Kategorija sa ID " + dto.getKategorijaId() + " nije pronađena"));
        
        EukPredmet predmet = new EukPredmet();
        predmet.setDatumKreiranja(LocalDate.now());
        predmet.setNazivPredmeta(dto.getNazivPredmeta());
        predmet.setStatus(dto.getStatus());
        predmet.setOdgovornaOsoba(dto.getOdgovornaOsoba());
        predmet.setPrioritet(dto.getPrioritet());
        predmet.setRokZaZavrsetak(dto.getRokZaZavrsetak());
        predmet.setKategorija(kategorija);
        predmet.setKategorijaSkracenica(kategorija.getSkracenica());
        
        EukPredmet savedPredmet = predmetRepository.save(predmet);
        logger.info("Created EUK predmet with ID: {}", savedPredmet.getPredmetId());
        
        return convertToDto(savedPredmet);
    }
    
    @Transactional
    public EukPredmetDto update(Integer id, EukPredmetDto dto) {
        logger.info("Updating EUK predmet with ID: {}", id);
        
        EukPredmet predmet = predmetRepository.findById(id)
                .orElseThrow(() -> new EukException("Predmet sa ID " + id + " nije pronađen"));
        
        validatePredmetData(dto);
        
        EukKategorija kategorija = kategorijaRepository.findById(dto.getKategorijaId())
                .orElseThrow(() -> new EukException("Kategorija sa ID " + dto.getKategorijaId() + " nije pronađena"));
        
        predmet.setNazivPredmeta(dto.getNazivPredmeta());
        predmet.setStatus(dto.getStatus());
        predmet.setOdgovornaOsoba(dto.getOdgovornaOsoba());
        predmet.setPrioritet(dto.getPrioritet());
        predmet.setRokZaZavrsetak(dto.getRokZaZavrsetak());
        predmet.setKategorija(kategorija);
        predmet.setKategorijaSkracenica(kategorija.getSkracenica());
        
        EukPredmet savedPredmet = predmetRepository.save(predmet);
        logger.info("Updated EUK predmet with ID: {}", id);
        
        return convertToDto(savedPredmet);
    }
    
    @Transactional
    public void delete(Integer id) {
        logger.info("Deleting EUK predmet with ID: {}", id);
        
        EukPredmet predmet = predmetRepository.findById(id)
                .orElseThrow(() -> new EukException("Predmet sa ID " + id + " nije pronađen"));
        
        // Note: Since ugrozena lica are now in separate table (ugrozeno_lice_t1), 
        // we can't check for associated records here anymore
        // This check would need to be implemented differently if needed
        
        predmetRepository.delete(predmet);
        logger.info("Deleted EUK predmet with ID: {}", id);
    }
    
    private void validatePredmetData(EukPredmetDto dto) {
        if (dto.getRokZaZavrsetak() != null && dto.getRokZaZavrsetak().isBefore(LocalDate.now())) {
            throw new EukException("Rok za završetak ne može biti u prošlosti");
        }
    }
    
    private EukPredmetDto convertToDto(EukPredmet predmet) {
        EukPredmetDto dto = new EukPredmetDto();
        dto.setPredmetId(predmet.getPredmetId());
        dto.setDatumKreiranja(predmet.getDatumKreiranja());
        dto.setNazivPredmeta(predmet.getNazivPredmeta());
        dto.setStatus(predmet.getStatus());
        dto.setOdgovornaOsoba(predmet.getOdgovornaOsoba());
        dto.setPrioritet(predmet.getPrioritet());
        dto.setRokZaZavrsetak(predmet.getRokZaZavrsetak());
        dto.setKategorijaId(predmet.getKategorija() != null ? predmet.getKategorija().getKategorijaId() : null);
        dto.setKategorijaNaziv(predmet.getKategorija() != null ? predmet.getKategorija().getNaziv() : null);
        dto.setKategorijaSkracenica(predmet.getKategorijaSkracenica());
        // Note: brojUgrozenihLica is now calculated from separate ugrozeno_lice_t1 table
        // This would need to be implemented differently if needed
        dto.setBrojUgrozenihLica(0);
        return dto;
    }
    
    private EukPredmetDto convertToDtoFromObjectArray(Object[] result) {
        EukPredmet predmet = (EukPredmet) result[0];
        String kategorijaNaziv = (String) result[1];
        // Handle both Integer and Long types for brojUgrozenihLica
        Number brojUgrozenihLicaNumber = (Number) result[2];
        int brojUgrozenihLica = brojUgrozenihLicaNumber != null ? brojUgrozenihLicaNumber.intValue() : 0;
        
        EukPredmetDto dto = new EukPredmetDto();
        dto.setPredmetId(predmet.getPredmetId());
        dto.setDatumKreiranja(predmet.getDatumKreiranja());
        dto.setNazivPredmeta(predmet.getNazivPredmeta());
        dto.setStatus(predmet.getStatus());
        dto.setOdgovornaOsoba(predmet.getOdgovornaOsoba());
        dto.setPrioritet(predmet.getPrioritet());
        dto.setRokZaZavrsetak(predmet.getRokZaZavrsetak());
        dto.setKategorijaId(predmet.getKategorija() != null ? predmet.getKategorija().getKategorijaId() : null);
        dto.setKategorijaNaziv(kategorijaNaziv);
        dto.setKategorijaSkracenica(predmet.getKategorijaSkracenica());
        dto.setBrojUgrozenihLica(brojUgrozenihLica);
        return dto;
    }
}
