package com.sirus.backend.service;

import com.sirus.backend.dto.EukKategorijaDto;
import com.sirus.backend.entity.EukKategorija;
import com.sirus.backend.exception.EukException;
import com.sirus.backend.repository.EukKategorijaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EukKategorijaService {
    
    private static final Logger logger = LoggerFactory.getLogger(EukKategorijaService.class);
    
    @Autowired
    private EukKategorijaRepository kategorijaRepository;
    
    public List<EukKategorijaDto> findAll() {
        logger.info("Fetching all EUK kategorije");
        List<EukKategorija> kategorije = kategorijaRepository.findAllOrderByNaziv();
        return kategorije.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<EukKategorijaDto> search(String naziv, String skracenica) {
        logger.info("Searching EUK kategorije with filters - naziv: {}, skracenica: {}", naziv, skracenica);
        List<EukKategorija> kategorije = kategorijaRepository.search(naziv, skracenica);
        return kategorije.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public EukKategorijaDto findById(Integer id) {
        logger.info("Fetching EUK kategorija with ID: {}", id);
        EukKategorija kategorija = kategorijaRepository.findById(id)
                .orElseThrow(() -> new EukException("Kategorija sa ID " + id + " nije pronađena"));
        return convertToDto(kategorija);
    }
    
    @Transactional
    public EukKategorijaDto create(EukKategorijaDto dto) {
        logger.info("Creating new EUK kategorija: {}", dto.getNaziv());
        
        if (kategorijaRepository.existsByNaziv(dto.getNaziv())) {
            throw new EukException("Kategorija sa nazivom '" + dto.getNaziv() + "' već postoji");
        }
        
        if (kategorijaRepository.existsBySkracenica(dto.getSkracenica())) {
            throw new EukException("Kategorija sa skraćenicom '" + dto.getSkracenica() + "' već postoji");
        }
        
        EukKategorija kategorija = new EukKategorija();
        kategorija.setNaziv(dto.getNaziv());
        kategorija.setSkracenica(dto.getSkracenica());
        
        EukKategorija savedKategorija = kategorijaRepository.save(kategorija);
        logger.info("Created EUK kategorija with ID: {}", savedKategorija.getKategorijaId());
        
        return convertToDto(savedKategorija);
    }
    
    @Transactional
    public EukKategorijaDto update(Integer id, EukKategorijaDto dto) {
        logger.info("Updating EUK kategorija with ID: {}", id);
        
        EukKategorija kategorija = kategorijaRepository.findById(id)
                .orElseThrow(() -> new EukException("Kategorija sa ID " + id + " nije pronađena"));
        
        // Check if name already exists for different category
        if (!kategorija.getNaziv().equals(dto.getNaziv()) && 
            kategorijaRepository.existsByNaziv(dto.getNaziv())) {
            throw new EukException("Kategorija sa nazivom '" + dto.getNaziv() + "' već postoji");
        }
        
        // Check if skracenica already exists for different category
        if (!kategorija.getSkracenica().equals(dto.getSkracenica()) && 
            kategorijaRepository.existsBySkracenica(dto.getSkracenica())) {
            throw new EukException("Kategorija sa skraćenicom '" + dto.getSkracenica() + "' već postoji");
        }
        
        kategorija.setNaziv(dto.getNaziv());
        kategorija.setSkracenica(dto.getSkracenica());
        EukKategorija savedKategorija = kategorijaRepository.save(kategorija);
        
        logger.info("Updated EUK kategorija with ID: {}", id);
        return convertToDto(savedKategorija);
    }
    
    @Transactional
    public void delete(Integer id) {
        logger.info("Deleting EUK kategorija with ID: {}", id);
        
        EukKategorija kategorija = kategorijaRepository.findById(id)
                .orElseThrow(() -> new EukException("Kategorija sa ID " + id + " nije pronađena"));
        
        // Check if category has associated predmeti
        if (!kategorija.getPredmeti().isEmpty()) {
            throw new EukException("Ne možete obrisati kategoriju koja ima povezane predmete");
        }
        
        kategorijaRepository.delete(kategorija);
        logger.info("Deleted EUK kategorija with ID: {}", id);
    }
    
    private EukKategorijaDto convertToDto(EukKategorija kategorija) {
        EukKategorijaDto dto = new EukKategorijaDto();
        dto.setKategorijaId(kategorija.getKategorijaId());
        dto.setNaziv(kategorija.getNaziv());
        dto.setSkracenica(kategorija.getSkracenica());
        return dto;
    }
}
