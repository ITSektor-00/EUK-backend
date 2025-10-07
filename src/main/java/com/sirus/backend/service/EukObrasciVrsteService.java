package com.sirus.backend.service;

import com.sirus.backend.entity.EukObrasciVrste;
import com.sirus.backend.repository.EukObrasciVrsteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EukObrasciVrsteService {
    
    @Autowired
    private EukObrasciVrsteRepository obrasciVrsteRepository;
    
    public List<EukObrasciVrste> getAllObrasciVrste() {
        return obrasciVrsteRepository.findAllByOrderByNazivAsc();
    }
    
    public Optional<EukObrasciVrste> getObrasciVrsteById(Long id) {
        return obrasciVrsteRepository.findById(id);
    }
    
    public Optional<EukObrasciVrste> getObrasciVrsteByNaziv(String naziv) {
        return obrasciVrsteRepository.findByNaziv(naziv);
    }
    
    public EukObrasciVrste saveObrasciVrste(EukObrasciVrste obrasciVrste) {
        return obrasciVrsteRepository.save(obrasciVrste);
    }
    
    public void deleteObrasciVrste(Long id) {
        obrasciVrsteRepository.deleteById(id);
    }
    
    public boolean existsByNaziv(String naziv) {
        return obrasciVrsteRepository.existsByNaziv(naziv);
    }
}
