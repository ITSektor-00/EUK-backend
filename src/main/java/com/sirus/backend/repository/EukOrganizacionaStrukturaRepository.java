package com.sirus.backend.repository;

import com.sirus.backend.entity.EukOrganizacionaStruktura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EukOrganizacionaStrukturaRepository extends JpaRepository<EukOrganizacionaStruktura, Long> {
    
    Optional<EukOrganizacionaStruktura> findByNaziv(String naziv);
    
    List<EukOrganizacionaStruktura> findAllByOrderByNazivAsc();
    
    boolean existsByNaziv(String naziv);
}
