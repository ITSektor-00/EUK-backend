package com.sirus.backend.service;

import com.sirus.backend.dto.EukUgrozenoLiceDto;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.entity.EukUgrozenoLice;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.repository.EukPredmetRepository;
import com.sirus.backend.repository.EukUgrozenoLiceRepository;
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
import java.util.stream.Collectors;

@Service
public class EukUgrozenoLiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(EukUgrozenoLiceService.class);
    
    @Autowired
    private EukUgrozenoLiceRepository ugrozenoLiceRepository;
    
    @Autowired
    private EukPredmetRepository predmetRepository;
    
    public Page<EukUgrozenoLiceDto> findAll(int page, int size) {
        logger.info("Fetching EUK ugrožena lica with pagination - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> results = ugrozenoLiceRepository.findAllWithPredmetInfo(pageable);
        return results.map(this::convertToDtoFromObjectArray);
    }
    
    public EukUgrozenoLiceDto findById(Integer id) {
        logger.info("Fetching EUK ugroženo lice with ID: {}", id);
        EukUgrozenoLice ugrozenoLice = ugrozenoLiceRepository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        return convertToDto(ugrozenoLice);
    }
    
    @Transactional
    public EukUgrozenoLiceDto create(EukUgrozenoLiceDto dto) {
        logger.info("Creating new EUK ugroženo lice: {} {}", dto.getIme(), dto.getPrezime());
        
        validateUgrozenoLiceData(dto);
        
        // Check if JMBG already exists
        if (ugrozenoLiceRepository.existsByJmbg(dto.getJmbg())) {
            throw new EukException("Osoba sa JMBG " + dto.getJmbg() + " već postoji u sistemu");
        }
        
        EukPredmet predmet = predmetRepository.findById(dto.getPredmetId())
                .orElseThrow(() -> new EukException("Predmet sa ID " + dto.getPredmetId() + " nije pronađen"));
        
        EukUgrozenoLice ugrozenoLice = new EukUgrozenoLice();
        ugrozenoLice.setIme(dto.getIme());
        ugrozenoLice.setPrezime(dto.getPrezime());
        ugrozenoLice.setJmbg(dto.getJmbg());
        ugrozenoLice.setDatumRodjenja(dto.getDatumRodjenja());
        ugrozenoLice.setDrzavaRodjenja(dto.getDrzavaRodjenja());
        ugrozenoLice.setMestoRodjenja(dto.getMestoRodjenja());
        ugrozenoLice.setOpstinaRodjenja(dto.getOpstinaRodjenja());
        ugrozenoLice.setPredmet(predmet);
        
        EukUgrozenoLice savedUgrozenoLice = ugrozenoLiceRepository.save(ugrozenoLice);
        logger.info("Created EUK ugroženo lice with ID: {}", savedUgrozenoLice.getUgrozenoLiceId());
        
        return convertToDto(savedUgrozenoLice);
    }
    
    @Transactional
    public EukUgrozenoLiceDto update(Integer id, EukUgrozenoLiceDto dto) {
        logger.info("Updating EUK ugroženo lice with ID: {}", id);
        
        EukUgrozenoLice ugrozenoLice = ugrozenoLiceRepository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        
        validateUgrozenoLiceData(dto);
        
        // Check if JMBG already exists for different person
        if (!ugrozenoLice.getJmbg().equals(dto.getJmbg()) && 
            ugrozenoLiceRepository.existsByJmbg(dto.getJmbg())) {
            throw new EukException("Osoba sa JMBG " + dto.getJmbg() + " već postoji u sistemu");
        }
        
        EukPredmet predmet = predmetRepository.findById(dto.getPredmetId())
                .orElseThrow(() -> new EukException("Predmet sa ID " + dto.getPredmetId() + " nije pronađen"));
        
        ugrozenoLice.setIme(dto.getIme());
        ugrozenoLice.setPrezime(dto.getPrezime());
        ugrozenoLice.setJmbg(dto.getJmbg());
        ugrozenoLice.setDatumRodjenja(dto.getDatumRodjenja());
        ugrozenoLice.setDrzavaRodjenja(dto.getDrzavaRodjenja());
        ugrozenoLice.setMestoRodjenja(dto.getMestoRodjenja());
        ugrozenoLice.setOpstinaRodjenja(dto.getOpstinaRodjenja());
        ugrozenoLice.setPredmet(predmet);
        
        EukUgrozenoLice savedUgrozenoLice = ugrozenoLiceRepository.save(ugrozenoLice);
        logger.info("Updated EUK ugroženo lice with ID: {}", id);
        
        return convertToDto(savedUgrozenoLice);
    }
    
    @Transactional
    public void delete(Integer id) {
        logger.info("Deleting EUK ugroženo lice with ID: {}", id);
        
        EukUgrozenoLice ugrozenoLice = ugrozenoLiceRepository.findById(id)
                .orElseThrow(() -> new EukException("Ugroženo lice sa ID " + id + " nije pronađeno"));
        
        ugrozenoLiceRepository.delete(ugrozenoLice);
        logger.info("Deleted EUK ugroženo lice with ID: {}", id);
    }
    
    public List<EukUgrozenoLiceDto> findByPredmetId(Integer predmetId) {
        logger.info("Fetching EUK ugrožena lica for predmet ID: {}", predmetId);
        List<Object[]> results = ugrozenoLiceRepository.findByPredmetIdWithPredmetInfo(predmetId);
        return results.stream()
                .map(this::convertToDtoFromObjectArray)
                .collect(Collectors.toList());
    }
    
    public EukUgrozenoLiceDto findByJmbg(String jmbg) {
        logger.info("Searching EUK ugroženo lice by JMBG: {}", jmbg);
        EukUgrozenoLice ugrozenoLice = ugrozenoLiceRepository.findByJmbg(jmbg)
                .orElseThrow(() -> new EukException("Osoba sa JMBG " + jmbg + " nije pronađena"));
        return convertToDto(ugrozenoLice);
    }
    
    private void validateUgrozenoLiceData(EukUgrozenoLiceDto dto) {
        if (dto.getDatumRodjenja() != null && dto.getDatumRodjenja().isAfter(LocalDate.now())) {
            throw new EukException("Datum rođenja ne može biti u budućnosti");
        }
        
        if (dto.getJmbg() == null || !dto.getJmbg().matches("^\\d{13}$")) {
            throw new EukException("JMBG mora sadržati tačno 13 cifara");
        }
    }
    
    private EukUgrozenoLiceDto convertToDto(EukUgrozenoLice ugrozenoLice) {
        EukUgrozenoLiceDto dto = new EukUgrozenoLiceDto();
        dto.setUgrozenoLiceId(ugrozenoLice.getUgrozenoLiceId());
        dto.setIme(ugrozenoLice.getIme());
        dto.setPrezime(ugrozenoLice.getPrezime());
        dto.setJmbg(ugrozenoLice.getJmbg());
        dto.setDatumRodjenja(ugrozenoLice.getDatumRodjenja());
        dto.setDrzavaRodjenja(ugrozenoLice.getDrzavaRodjenja());
        dto.setMestoRodjenja(ugrozenoLice.getMestoRodjenja());
        dto.setOpstinaRodjenja(ugrozenoLice.getOpstinaRodjenja());
        dto.setPredmetId(ugrozenoLice.getPredmet().getPredmetId());
        dto.setPredmetNaziv(ugrozenoLice.getPredmet().getNazivPredmeta());
        dto.setPredmetStatus(ugrozenoLice.getPredmet().getStatus().getValue());
        return dto;
    }
    
    private EukUgrozenoLiceDto convertToDtoFromObjectArray(Object[] result) {
        EukUgrozenoLice ugrozenoLice = (EukUgrozenoLice) result[0];
        String predmetNaziv = (String) result[1];
        EukPredmet.Status predmetStatus = (EukPredmet.Status) result[2];
        
        EukUgrozenoLiceDto dto = new EukUgrozenoLiceDto();
        dto.setUgrozenoLiceId(ugrozenoLice.getUgrozenoLiceId());
        dto.setIme(ugrozenoLice.getIme());
        dto.setPrezime(ugrozenoLice.getPrezime());
        dto.setJmbg(ugrozenoLice.getJmbg());
        dto.setDatumRodjenja(ugrozenoLice.getDatumRodjenja());
        dto.setDrzavaRodjenja(ugrozenoLice.getDrzavaRodjenja());
        dto.setMestoRodjenja(ugrozenoLice.getMestoRodjenja());
        dto.setOpstinaRodjenja(ugrozenoLice.getOpstinaRodjenja());
        dto.setPredmetId(ugrozenoLice.getPredmet().getPredmetId());
        dto.setPredmetNaziv(predmetNaziv);
        dto.setPredmetStatus(predmetStatus != null ? predmetStatus.getValue() : null);
        return dto;
    }
}
