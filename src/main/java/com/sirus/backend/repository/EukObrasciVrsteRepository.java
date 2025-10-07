package com.sirus.backend.repository;

import com.sirus.backend.entity.EukObrasciVrste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EukObrasciVrsteRepository extends JpaRepository<EukObrasciVrste, Long> {
    
    Optional<EukObrasciVrste> findByNaziv(String naziv);
    
    List<EukObrasciVrste> findAllByOrderByNazivAsc();
    
    boolean existsByNaziv(String naziv);
}
