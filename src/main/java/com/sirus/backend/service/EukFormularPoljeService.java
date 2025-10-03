package com.sirus.backend.service;

import com.sirus.backend.dto.EukFormularPoljeDto;
import com.sirus.backend.entity.EukFormular;
import com.sirus.backend.entity.EukFormularIstorija;
import com.sirus.backend.entity.EukFormularPolje;
import com.sirus.backend.entity.User;
import com.sirus.backend.repository.EukFormularPoljeRepository;
import com.sirus.backend.repository.EukFormularRepository;
import com.sirus.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EukFormularPoljeService {
    
    @Autowired
    private EukFormularPoljeRepository poljeRepository;
    
    @Autowired
    private EukFormularRepository formularRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // =====================================================
    // CRUD OPERACIJE
    // =====================================================
    
    public EukFormularPoljeDto create(EukFormularPoljeDto poljeDto, Integer userId) {
        // Validacija
        if (poljeRepository.existsByNazivPoljaAndFormularFormularId(poljeDto.getNazivPolja(), poljeDto.getFormularId())) {
            throw new IllegalArgumentException("Polje sa nazivom '" + poljeDto.getNazivPolja() + "' već postoji u ovom formularu");
        }
        
        // Pronađi formular
        EukFormular formular = formularRepository.findById(poljeDto.getFormularId())
                .orElseThrow(() -> new IllegalArgumentException("Formular sa ID " + poljeDto.getFormularId() + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Kreiraj polje
        EukFormularPolje polje = new EukFormularPolje();
        polje.setFormular(formular);
        polje.setNazivPolja(poljeDto.getNazivPolja());
        polje.setLabel(poljeDto.getLabel());
        polje.setTipPolja(EukFormularPolje.TipPolja.fromValue(poljeDto.getTipPolja()));
        polje.setObavezno(poljeDto.getObavezno());
        polje.setRedosled(poljeDto.getRedosled());
        polje.setPlaceholder(poljeDto.getPlaceholder());
        polje.setOpis(poljeDto.getOpis());
        polje.setValidacija(poljeDto.getValidacija());
        polje.setOpcije(poljeDto.getOpcije());
        polje.setDefaultVrednost(poljeDto.getDefaultVrednost());
        polje.setReadonly(poljeDto.getReadonly());
        polje.setVisible(poljeDto.getVisible());
        
        // Sačuvaj polje
        EukFormularPolje savedPolje = poljeRepository.save(polje);
        
        // Dodaj u istoriju formulara
        addToFormularIstorija(formular, EukFormularIstorija.Akcija.FIELD_ADDED, "Dodato polje: " + savedPolje.getLabel(), user);
        
        return convertToDto(savedPolje);
    }
    
    public EukFormularPoljeDto update(Integer poljeId, EukFormularPoljeDto poljeDto, Integer userId) {
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        // Validacija
        if (poljeRepository.existsByNazivPoljaAndFormularFormularIdAndPoljeIdNot(poljeDto.getNazivPolja(), polje.getFormular().getFormularId(), poljeId)) {
            throw new IllegalArgumentException("Polje sa nazivom '" + poljeDto.getNazivPolja() + "' već postoji u ovom formularu");
        }
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Sačuvaj stare vrednosti za istoriju
        String staraVrednost = "Naziv: " + polje.getNazivPolja() + ", Label: " + polje.getLabel() + ", Tip: " + polje.getTipPolja().getValue() + ", Obavezno: " + polje.getObavezno();
        
        // Ažuriraj polje
        polje.setNazivPolja(poljeDto.getNazivPolja());
        polje.setLabel(poljeDto.getLabel());
        polje.setTipPolja(EukFormularPolje.TipPolja.fromValue(poljeDto.getTipPolja()));
        polje.setObavezno(poljeDto.getObavezno());
        polje.setRedosled(poljeDto.getRedosled());
        polje.setPlaceholder(poljeDto.getPlaceholder());
        polje.setOpis(poljeDto.getOpis());
        polje.setValidacija(poljeDto.getValidacija());
        polje.setOpcije(poljeDto.getOpcije());
        polje.setDefaultVrednost(poljeDto.getDefaultVrednost());
        polje.setReadonly(poljeDto.getReadonly());
        polje.setVisible(poljeDto.getVisible());
        
        // Sačuvaj polje
        EukFormularPolje savedPolje = poljeRepository.save(polje);
        
        // Dodaj u istoriju formulara
        String novaVrednost = "Naziv: " + savedPolje.getNazivPolja() + ", Label: " + savedPolje.getLabel() + ", Tip: " + savedPolje.getTipPolja().getValue() + ", Obavezno: " + savedPolje.getObavezno();
        addToFormularIstorija(polje.getFormular(), EukFormularIstorija.Akcija.FIELD_UPDATED, "Ažurirano polje: " + savedPolje.getLabel(), user, staraVrednost, novaVrednost);
        
        return convertToDto(savedPolje);
    }
    
    public void delete(Integer poljeId, Integer userId) {
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Dodaj u istoriju formulara
        addToFormularIstorija(polje.getFormular(), EukFormularIstorija.Akcija.FIELD_REMOVED, "Uklonjeno polje: " + polje.getLabel(), user);
        
        // Obriši polje
        poljeRepository.delete(polje);
    }
    
    public EukFormularPoljeDto getById(Integer poljeId) {
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        return convertToDto(polje);
    }
    
    public EukFormularPoljeDto getByIdWithPodaci(Integer poljeId) {
        EukFormularPolje polje = poljeRepository.findByIdWithPodaci(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        return convertToDto(polje);
    }
    
    public EukFormularPoljeDto getByIdWithDokumenti(Integer poljeId) {
        EukFormularPolje polje = poljeRepository.findByIdWithDokumenti(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        return convertToDto(polje);
    }
    
    public List<EukFormularPoljeDto> getByFormular(Integer formularId) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdOrderByRedosledAsc(formularId);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByFormularWithPodaci(Integer formularId) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularWithPodaci(formularId);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByFormularWithDokumenti(Integer formularId) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularWithDokumenti(formularId);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByFormularAndVisible(Integer formularId, Boolean visible) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdAndVisibleOrderByRedosledAsc(formularId, visible);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByFormularAndObavezno(Integer formularId, Boolean obavezno) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdAndObaveznoOrderByRedosledAsc(formularId, obavezno);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByFormularAndTip(Integer formularId, String tipPolja) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdAndTipPoljaOrderByRedosledAsc(formularId, EukFormularPolje.TipPolja.fromValue(tipPolja));
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> getByTip(String tipPolja) {
        List<EukFormularPolje> polja = poljeRepository.findByTipPoljaAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(EukFormularPolje.TipPolja.fromValue(tipPolja));
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> searchByNaziv(String nazivPolja) {
        List<EukFormularPolje> polja = poljeRepository.findByNazivPoljaContainingIgnoreCaseAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(nazivPolja);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> searchByLabel(String label) {
        List<EukFormularPolje> polja = poljeRepository.findByLabelContainingIgnoreCaseAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(label);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> searchByFormularAndNaziv(Integer formularId, String nazivPolja) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdAndNazivPoljaContainingIgnoreCaseOrderByRedosledAsc(formularId, nazivPolja);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<EukFormularPoljeDto> searchByFormularAndLabel(Integer formularId, String label) {
        List<EukFormularPolje> polja = poljeRepository.findByFormularFormularIdAndLabelContainingIgnoreCaseOrderByRedosledAsc(formularId, label);
        return polja.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // =====================================================
    // POMOĆNE METODE
    // =====================================================
    
    private EukFormularPoljeDto convertToDto(EukFormularPolje polje) {
        EukFormularPoljeDto dto = new EukFormularPoljeDto(polje);
        return dto;
    }
    
    private void addToFormularIstorija(EukFormular formular, EukFormularIstorija.Akcija akcija, String opis, User korisnik) {
        addToFormularIstorija(formular, akcija, opis, korisnik, null, null);
    }
    
    private void addToFormularIstorija(EukFormular formular, EukFormularIstorija.Akcija akcija, String opis, User korisnik, String staraVrednost, String novaVrednost) {
        EukFormularIstorija istorija = new EukFormularIstorija(formular, akcija, opis, korisnik, staraVrednost, novaVrednost);
        formular.getIstorija().add(istorija);
    }
    
    // =====================================================
    // STATISTIKE
    // =====================================================
    
    public long countByFormular(Integer formularId) {
        return poljeRepository.countByFormular(formularId);
    }
    
    public long countObaveznihByFormular(Integer formularId) {
        return poljeRepository.countObaveznihByFormular(formularId);
    }
    
    public long countByTipPolja(String tipPolja) {
        return poljeRepository.countByTipPolja(EukFormularPolje.TipPolja.fromValue(tipPolja));
    }
    
    // =====================================================
    // REDOSLED POLJA
    // =====================================================
    
    public void updateRedosled(Integer poljeId, Integer noviRedosled, Integer userId) {
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        // Pronađi korisnika
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new IllegalArgumentException("Korisnik sa ID " + userId + " ne postoji"));
        
        // Sačuvaj stari redosled
        Integer stariRedosled = polje.getRedosled();
        
        // Ažuriraj redosled
        polje.setRedosled(noviRedosled);
        
        // Sačuvaj polje
        poljeRepository.save(polje);
        
        // Dodaj u istoriju formulara
        addToFormularIstorija(polje.getFormular(), EukFormularIstorija.Akcija.FIELD_UPDATED, 
                "Promenjen redosled polja '" + polje.getLabel() + "' sa " + stariRedosled + " na " + noviRedosled, user);
    }
    
    public void movePoljeUp(Integer poljeId, Integer userId) {
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        // Pronađi prethodno polje
        List<EukFormularPolje> prethodnaPolja = poljeRepository.findByFormularFormularIdAndRedosledLessThanOrderByRedosledDesc(polje.getFormular().getFormularId(), polje.getRedosled());
        
        if (!prethodnaPolja.isEmpty()) {
            EukFormularPolje prethodnoPolje = prethodnaPolja.get(0);
            
            // Zameni redosled
            Integer tempRedosled = polje.getRedosled();
            polje.setRedosled(prethodnoPolje.getRedosled());
            prethodnoPolje.setRedosled(tempRedosled);
            
            // Sačuvaj oba polja
            poljeRepository.save(polje);
            poljeRepository.save(prethodnoPolje);
        }
    }
    
    public void movePoljeDown(Integer poljeId, Integer userId) {
        // Pronađi polje
        EukFormularPolje polje = poljeRepository.findById(poljeId)
                .orElseThrow(() -> new IllegalArgumentException("Polje sa ID " + poljeId + " ne postoji"));
        
        // Pronađi sledeće polje
        List<EukFormularPolje> sledecaPolja = poljeRepository.findByFormularFormularIdAndRedosledGreaterThanOrderByRedosledAsc(polje.getFormular().getFormularId(), polje.getRedosled());
        
        if (!sledecaPolja.isEmpty()) {
            EukFormularPolje sledecePolje = sledecaPolja.get(0);
            
            // Zameni redosled
            Integer tempRedosled = polje.getRedosled();
            polje.setRedosled(sledecePolje.getRedosled());
            sledecePolje.setRedosled(tempRedosled);
            
            // Sačuvaj oba polja
            poljeRepository.save(polje);
            poljeRepository.save(sledecePolje);
        }
    }
}
