package com.sirus.backend.service;

import com.sirus.backend.dto.EukFormularDto;
import com.sirus.backend.dto.EukFormularPoljeDto;
import com.sirus.backend.entity.EukFormular;
import com.sirus.backend.entity.EukFormularIstorija;
import com.sirus.backend.entity.EukFormularPolje;
import com.sirus.backend.entity.EukKategorija;
import com.sirus.backend.entity.User;
import com.sirus.backend.repository.EukFormularRepository;
import com.sirus.backend.repository.EukKategorijaRepository;
import com.sirus.backend.repository.UserRepository;
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
public class EukFormularService {
    
    @Autowired
    private EukFormularRepository formularRepository;
    
    @Autowired
    private EukKategorijaRepository kategorijaRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // =====================================================
    // CRUD OPERACIJE
    // =====================================================
    
    public EukFormularDto create(EukFormularDto formularDto, Integer userId) {
        // Validacija
        if (formularRepository.existsByNazivAndKategorijaKategorijaIdAndAktivnaTrue(formularDto.getNaziv(), formularDto.getKategorijaId())) {
            throw new IllegalArgumentException("Formular sa nazivom '" + formularDto.getNaziv() + "' već postoji u ovoj kategoriji");
        }
        
        // Pronađi kategoriju
        EukKategorija kategorija = kategorijaRepository.findById(formularDto.getKategorijaId())
                .orElseThrow(() -> new IllegalArgumentException("Kategorija sa ID " + formularDto.getKategorijaId() + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Kreiraj formular
        EukFormular formular = new EukFormular();
        formular.setNaziv(formularDto.getNaziv());
        formular.setOpis(formularDto.getOpis());
        formular.setKategorija(kategorija);
        formular.setAktivna(true);
        formular.setVerzija(1);
        formular.setCreatedBy(user);
        formular.setUpdatedBy(user);
        
        // Sačuvaj formular
        EukFormular savedFormular = formularRepository.save(formular);
        
        // Dodaj u istoriju
        addToIstorija(savedFormular, EukFormularIstorija.Akcija.CREATED, "Formular kreiran", user);
        
        return convertToDto(savedFormular);
    }
    
    public EukFormularDto update(Integer formularId, EukFormularDto formularDto, Integer userId) {
        // Pronađi formular
        EukFormular formular = formularRepository.findById(formularId)
                .orElseThrow(() -> new IllegalArgumentException("Formular sa ID " + formularId + " ne postoji"));
        
        // Validacija
        if (formularRepository.existsByNazivAndKategorijaKategorijaIdAndAktivnaTrueAndFormularIdNot(formularDto.getNaziv(), formularDto.getKategorijaId(), formularId)) {
            throw new IllegalArgumentException("Formular sa nazivom '" + formularDto.getNaziv() + "' već postoji u ovoj kategoriji");
        }
        
        // Pronađi kategoriju
        EukKategorija kategorija = kategorijaRepository.findById(formularDto.getKategorijaId())
                .orElseThrow(() -> new IllegalArgumentException("Kategorija sa ID " + formularDto.getKategorijaId() + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Sačuvaj stare vrednosti za istoriju
        String staraVrednost = "Naziv: " + formular.getNaziv() + ", Opis: " + formular.getOpis() + ", Kategorija: " + formular.getKategorija().getNaziv();
        
        // Ažuriraj formular
        formular.setNaziv(formularDto.getNaziv());
        formular.setOpis(formularDto.getOpis());
        formular.setKategorija(kategorija);
        formular.setUpdatedBy(user);
        formular.incrementVerzija();
        
        // Sačuvaj formular
        EukFormular savedFormular = formularRepository.save(formular);
        
        // Dodaj u istoriju
        String novaVrednost = "Naziv: " + savedFormular.getNaziv() + ", Opis: " + savedFormular.getOpis() + ", Kategorija: " + savedFormular.getKategorija().getNaziv();
        addToIstorija(savedFormular, EukFormularIstorija.Akcija.UPDATED, "Formular ažuriran", user, staraVrednost, novaVrednost);
        
        return convertToDto(savedFormular);
    }
    
    public void delete(Integer formularId, Integer userId) {
        // Pronađi formular
        EukFormular formular = formularRepository.findById(formularId)
                .orElseThrow(() -> new IllegalArgumentException("Formular sa ID " + formularId + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Deaktiviraj formular
        formular.setAktivna(false);
        formular.setUpdatedBy(user);
        
        // Sačuvaj formular
        formularRepository.save(formular);
        
        // Dodaj u istoriju
        addToIstorija(formular, EukFormularIstorija.Akcija.DEACTIVATED, "Formular deaktiviran", user);
    }
    
    public EukFormularDto getById(Integer formularId) {
        EukFormular formular = formularRepository.findById(formularId)
                .orElseThrow(() -> new IllegalArgumentException("Formular sa ID " + formularId + " ne postoji"));
        
        return convertToDto(formular);
    }
    
    public EukFormularDto getByIdWithPolja(Integer formularId) {
        EukFormular formular = formularRepository.findByIdWithPolja(formularId)
                .orElseThrow(() -> new IllegalArgumentException("Formular sa ID " + formularId + " ne postoji"));
        
        return convertToDtoWithPolja(formular);
    }
    
    public List<EukFormularDto> getAll() {
        List<EukFormular> formulari = formularRepository.findByAktivnaTrueOrderByDatumKreiranjaDesc();
        return formulari.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public Page<EukFormularDto> getAll(Pageable pageable) {
        Page<EukFormular> formulari = formularRepository.findByAktivnaTrueOrderByDatumKreiranjaDesc(pageable);
        return formulari.map(this::convertToDto);
    }
    
    public List<EukFormularDto> getByKategorija(Integer kategorijaId) {
        List<EukFormular> formulari = formularRepository.findByKategorijaKategorijaIdAndAktivnaTrueOrderByDatumKreiranjaDesc(kategorijaId);
        return formulari.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public Page<EukFormularDto> getByKategorija(Integer kategorijaId, Pageable pageable) {
        Page<EukFormular> formulari = formularRepository.findByKategorijaKategorijaIdAndAktivnaTrueOrderByDatumKreiranjaDesc(kategorijaId, pageable);
        return formulari.map(this::convertToDto);
    }
    
    public List<EukFormularDto> getByKategorijaWithPolja(Integer kategorijaId) {
        List<EukFormular> formulari = formularRepository.findByKategorijaWithPolja(kategorijaId);
        return formulari.stream().map(this::convertToDtoWithPolja).collect(Collectors.toList());
    }
    
    public List<EukFormularDto> searchByNaziv(String naziv) {
        List<EukFormular> formulari = formularRepository.findByNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(naziv);
        return formulari.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public Page<EukFormularDto> searchByNaziv(String naziv, Pageable pageable) {
        Page<EukFormular> formulari = formularRepository.findByNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(naziv, pageable);
        return formulari.map(this::convertToDto);
    }
    
    public List<EukFormularDto> searchByKategorijaAndNaziv(Integer kategorijaId, String naziv) {
        List<EukFormular> formulari = formularRepository.findByKategorijaKategorijaIdAndNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(kategorijaId, naziv);
        return formulari.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public Page<EukFormularDto> searchByKategorijaAndNaziv(Integer kategorijaId, String naziv, Pageable pageable) {
        Page<EukFormular> formulari = formularRepository.findByKategorijaKategorijaIdAndNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(kategorijaId, naziv, pageable);
        return formulari.map(this::convertToDto);
    }
    
    // =====================================================
    // POMOĆNE METODE
    // =====================================================
    
    private EukFormularDto convertToDto(EukFormular formular) {
        EukFormularDto dto = new EukFormularDto(formular);
        return dto;
    }
    
    private EukFormularDto convertToDtoWithPolja(EukFormular formular) {
        EukFormularDto dto = new EukFormularDto(formular);
        
        // Konvertuj polja
        List<EukFormularPoljeDto> poljaDto = formular.getPolja().stream()
                .map(this::convertPoljeToDto)
                .collect(Collectors.toList());
        
        dto.setPolja(poljaDto);
        return dto;
    }
    
    private EukFormularPoljeDto convertPoljeToDto(EukFormularPolje polje) {
        EukFormularPoljeDto dto = new EukFormularPoljeDto(polje);
        return dto;
    }
    
    private void addToIstorija(EukFormular formular, EukFormularIstorija.Akcija akcija, String opis, User korisnik) {
        addToIstorija(formular, akcija, opis, korisnik, null, null);
    }
    
    private void addToIstorija(EukFormular formular, EukFormularIstorija.Akcija akcija, String opis, User korisnik, String staraVrednost, String novaVrednost) {
        EukFormularIstorija istorija = new EukFormularIstorija(formular, akcija, opis, korisnik, staraVrednost, novaVrednost);
        formular.getIstorija().add(istorija);
    }
    
    // =====================================================
    // STATISTIKE
    // =====================================================
    
    public long countByKategorija(Integer kategorijaId) {
        return formularRepository.countByKategorija(kategorijaId);
    }
    
    public long countAktivnih() {
        return formularRepository.countAktivnih();
    }
    
    public List<Object[]> getFormulariWithBrojPolja() {
        return formularRepository.findFormulariWithBrojPolja();
    }
    
    public List<Object[]> getFormulariWithBrojPoljaByKategorija(Integer kategorijaId) {
        return formularRepository.findFormulariWithBrojPoljaByKategorija(kategorijaId);
    }
}
