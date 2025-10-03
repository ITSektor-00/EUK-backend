package com.sirus.backend.service;

import com.sirus.backend.dto.EukPredmetFormularPodaciDto;
import com.sirus.backend.entity.EukPredmet;
import com.sirus.backend.entity.EukPredmetFormularPodaci;
import com.sirus.backend.entity.EukFormularPolje;
import com.sirus.backend.entity.User;
import com.sirus.backend.repository.EukPredmetFormularPodaciRepository;
import com.sirus.backend.repository.EukPredmetRepository;
import com.sirus.backend.repository.EukFormularPoljeRepository;
import com.sirus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EukPredmetFormularPodaciService {
    
    @Autowired
    private EukPredmetFormularPodaciRepository podaciRepository;
    
    @Autowired
    private EukPredmetRepository predmetRepository;
    
    @Autowired
    private EukFormularPoljeRepository poljeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // =====================================================
    // CRUD OPERACIJE
    // =====================================================
    
    public EukPredmetFormularPodaciDto create(EukPredmetFormularPodaciDto podaciDto, Integer userId) {
        // Pronađi predmet
        EukPredmet predmet = predmetRepository.findById(podaciDto.getPredmetId())
                .orElseThrow(() -> new IllegalArgumentException("Predmet sa ID " + podaciDto.getPredmetId() + " ne postoji"));
        
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(podaciDto.getPoljeId())
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + podaciDto.getPoljeId() + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Proveri da li već postoji podatak za ovo polje
        Optional<EukPredmetFormularPodaci> existingPodatak = podaciRepository.findByPredmetPredmetIdAndPoljePoljeId(predmet.getPredmetId(), polje.getPoljeId());
        
        EukPredmetFormularPodaci podatak;
        if (existingPodatak.isPresent()) {
            // Ažuriraj postojeći podatak
            podatak = existingPodatak.get();
            podatak.setVrednost(podaciDto.getVrednost());
            podatak.setUnioKorisnik(user);
        } else {
            // Kreiraj novi podatak
            podatak = new EukPredmetFormularPodaci();
            podatak.setPredmet(predmet);
            podatak.setPolje(polje);
            podatak.setVrednost(podaciDto.getVrednost());
            podatak.setUnioKorisnik(user);
        }
        
        // Sačuvaj podatak
        EukPredmetFormularPodaci savedPodatak = podaciRepository.save(podatak);
        
        return convertToDto(savedPodatak);
    }
    
    public EukPredmetFormularPodaciDto update(Integer podatakId, EukPredmetFormularPodaciDto podaciDto, Integer userId) {
        // Pronađi podatak
        EukPredmetFormularPodaci podatak = podaciRepository.findById(podatakId)
                .orElseThrow(() -> new IllegalArgumentException("Podatak sa ID " + podatakId + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Ažuriraj podatak
        podatak.setVrednost(podaciDto.getVrednost());
        podatak.setUnioKorisnik(user);
        
        // Sačuvaj podatak
        EukPredmetFormularPodaci savedPodatak = podaciRepository.save(podatak);
        
        return convertToDto(savedPodatak);
    }
    
    public void delete(Integer podatakId) {
        // Pronađi podatak
        EukPredmetFormularPodaci podatak = podaciRepository.findById(podatakId)
                .orElseThrow(() -> new IllegalArgumentException("Podatak sa ID " + podatakId + " ne postoji"));
        
        // Obriši podatak
        podaciRepository.delete(podatak);
    }
    
    public EukPredmetFormularPodaciDto getById(Integer podatakId) {
        EukPredmetFormularPodaci podatak = podaciRepository.findById(podatakId)
                .orElseThrow(() -> new IllegalArgumentException("Podatak sa ID " + podatakId + " ne postoji"));
        
        return convertToDto(podatak);
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmet(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetPredmetIdOrderByPoljeRedosledAsc(predmetId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPolje(Integer poljeId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPoljePoljeIdOrderByDatumUnosaDesc(poljeId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormular(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormular(predmetId, formularId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndKategorija(Integer predmetId, Integer kategorijaId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndKategorija(predmetId, kategorijaId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByKorisnik(Integer korisnikId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByUnioKorisnikIdOrderByDatumUnosaDesc(korisnikId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByDatum(LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByDatumUnosaBetweenOrderByDatumUnosaDesc(startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndDatum(Integer predmetId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetPredmetIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(predmetId, startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPoljeAndDatum(Integer poljeId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPoljePoljeIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(poljeId, startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByKorisnikAndDatum(Integer korisnikId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByUnioKorisnikIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(korisnikId, startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndKorisnik(Integer predmetId, Integer korisnikId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetPredmetIdAndUnioKorisnikIdOrderByDatumUnosaDesc(predmetId, korisnikId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPoljeAndKorisnik(Integer poljeId, Integer korisnikId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPoljePoljeIdAndUnioKorisnikIdOrderByDatumUnosaDesc(poljeId, korisnikId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularAndKorisnik(Integer predmetId, Integer formularId, Integer korisnikId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularAndKorisnik(predmetId, formularId, korisnikId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularAndDatum(Integer predmetId, Integer formularId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularAndDatum(predmetId, formularId, startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularAndKorisnikAndDatum(Integer predmetId, Integer formularId, Integer korisnikId, LocalDateTime startDate, LocalDateTime endDate) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularAndKorisnikAndDatum(predmetId, formularId, korisnikId, startDate, endDate);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> searchByVrednost(String vrednost) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(vrednost);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> searchByPredmetAndVrednost(Integer predmetId, String vrednost) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetPredmetIdAndVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(predmetId, vrednost);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> searchByPoljeAndVrednost(Integer poljeId, String vrednost) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPoljePoljeIdAndVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(poljeId, vrednost);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // =====================================================
    // BATCH OPERACIJE
    // =====================================================
    
    public List<EukPredmetFormularPodaciDto> saveBatch(Map<String, Object> podaci, Integer predmetId, Integer formularId, Integer userId) {
        // Pronađi predmet
        EukPredmet predmet = predmetRepository.findById(predmetId)
                .orElseThrow(() -> new IllegalArgumentException("Predmet sa ID " + predmetId + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Pronađi polja za formular
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdOrderByRedosledAsc(formularId);
        
        List<EukPredmetFormularPodaci> savedPodaci = new java.util.ArrayList<>();
        
        for (EukFormularPolje polje : polja) {
            String vrednost = (String) podaci.get(polje.getNazivPolja());
            
            if (vrednost != null) {
                // Proveri da li već postoji podatak za ovo polje
                Optional<EukPredmetFormularPodaci> existingPodatak = podaciRepository.findByPredmetPredmetIdAndPoljePoljeId(predmetId, polje.getPoljeId());
                
                EukPredmetFormularPodaci podatak;
                if (existingPodatak.isPresent()) {
                    // Ažuriraj postojeći podatak
                    podatak = existingPodatak.get();
                    podatak.setVrednost(vrednost);
                    podatak.setUnioKorisnik(user);
                } else {
                    // Kreiraj novi podatak
                    podatak = new EukPredmetFormularPodaci();
                    podatak.setPredmet(predmet);
                    podatak.setPolje(polje);
                    podatak.setVrednost(vrednost);
                    podatak.setUnioKorisnik(user);
                }
                
                // Sačuvaj podatak
                EukPredmetFormularPodaci savedPodatak = podaciRepository.save(podatak);
                savedPodaci.add(savedPodatak);
            }
        }
        
        return savedPodaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public void deleteByPredmet(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetPredmetIdOrderByPoljeRedosledAsc(predmetId);
        podaciRepository.deleteAll(podaci);
    }
    
    public void deleteByPredmetAndFormular(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormular(predmetId, formularId);
        podaciRepository.deleteAll(podaci);
    }
    
    public void deleteByPolje(Integer poljeId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPoljePoljeIdOrderByDatumUnosaDesc(poljeId);
        podaciRepository.deleteAll(podaci);
    }
    
    // =====================================================
    // POMOĆNE METODE
    // =====================================================
    
    private EukPredmetFormularPodaciDto convertToDto(EukPredmetFormularPodaci podatak) {
        EukPredmetFormularPodaciDto dto = new EukPredmetFormularPodaciDto(podatak);
        return dto;
    }
    
    // =====================================================
    // STATISTIKE
    // =====================================================
    
    public long countByPredmet(Integer predmetId) {
        return podaciRepository.countByPredmet(predmetId);
    }
    
    public long countByPolje(Integer poljeId) {
        return podaciRepository.countByPolje(poljeId);
    }
    
    public long countByKorisnik(Integer korisnikId) {
        return podaciRepository.countByKorisnik(korisnikId);
    }
    
    public long countByDatum(LocalDateTime startDate, LocalDateTime endDate) {
        return podaciRepository.countByDatum(startDate, endDate);
    }
    
    public long countByPredmetAndFormular(Integer predmetId, Integer formularId) {
        return podaciRepository.countByPredmetAndFormular(predmetId, formularId);
    }
    
    public long countByPredmetAndKategorija(Integer predmetId, Integer kategorijaId) {
        return podaciRepository.countByPredmetAndKategorija(predmetId, kategorijaId);
    }
    
    // =====================================================
    // VALIDACIJA
    // =====================================================
    
    public boolean existsByPredmetAndPolje(Integer predmetId, Integer poljeId) {
        return podaciRepository.existsByPredmetPredmetIdAndPoljePoljeId(predmetId, poljeId);
    }
    
    public boolean existsByPredmetAndPoljeAndVrednost(Integer predmetId, Integer poljeId, String vrednost) {
        return podaciRepository.existsByPredmetPredmetIdAndPoljePoljeIdAndVrednost(predmetId, poljeId, vrednost);
    }
    
    public boolean existsByPredmetAndPoljeAndVrednostAndPodatakIdNot(Integer predmetId, Integer poljeId, String vrednost, Integer podatakId) {
        return podaciRepository.existsByPredmetPredmetIdAndPoljePoljeIdAndVrednostAndPodatakIdNot(predmetId, poljeId, vrednost, podatakId);
    }
    
    // =====================================================
    // FILTERI
    // =====================================================
    
    public List<EukPredmetFormularPodaciDto> getByPredmetWithVrednosti(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetWithVrednosti(predmetId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetWithoutVrednosti(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetWithoutVrednosti(predmetId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetWithEmptyVrednosti(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetWithEmptyVrednosti(predmetId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetWithNonEmptyVrednosti(Integer predmetId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetWithNonEmptyVrednosti(predmetId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularWithVrednosti(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularWithVrednosti(predmetId, formularId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularWithoutVrednosti(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularWithoutVrednosti(predmetId, formularId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularWithEmptyVrednosti(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularWithEmptyVrednosti(predmetId, formularId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukPredmetFormularPodaciDto> getByPredmetAndFormularWithNonEmptyVrednosti(Integer predmetId, Integer formularId) {
        List<EukPredmetFormularPodaci> podaci = podaciRepository.findByPredmetAndFormularWithNonEmptyVrednosti(predmetId, formularId);
        return podaci.stream().map(this::convertToDto).collect(Collectors.toList());
    }
}
