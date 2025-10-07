package com.sirus.backend.service;

import com.sirus.backend.entity.EukOrganizacionaStruktura;
import com.sirus.backend.repository.EukOrganizacionaStrukturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EukOrganizacionaStrukturaService {
    
    @Autowired
    private EukOrganizacionaStrukturaRepository organizacionaStrukturaRepository;
    
    public List<EukOrganizacionaStruktura> getAllOrganizacionaStruktura() {
        return organizacionaStrukturaRepository.findAllByOrderByNazivAsc();
    }
    
    public Optional<EukOrganizacionaStruktura> getOrganizacionaStrukturaById(Long id) {
        return organizacionaStrukturaRepository.findById(id);
    }
    
    public Optional<EukOrganizacionaStruktura> getOrganizacionaStrukturaByNaziv(String naziv) {
        return organizacionaStrukturaRepository.findByNaziv(naziv);
    }
    
    public EukOrganizacionaStruktura saveOrganizacionaStruktura(EukOrganizacionaStruktura organizacionaStruktura) {
        return organizacionaStrukturaRepository.save(organizacionaStruktura);
    }
    
    public void deleteOrganizacionaStruktura(Long id) {
        organizacionaStrukturaRepository.deleteById(id);
    }
    
    public boolean existsByNaziv(String naziv) {
        return organizacionaStrukturaRepository.existsByNaziv(naziv);
    }
}
