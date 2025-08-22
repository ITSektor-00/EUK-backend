package com.sirus.backend.repository;

import com.sirus.backend.entity.EukKategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EukKategorijaRepository extends JpaRepository<EukKategorija, Integer> {
    
    @Query("SELECT k FROM EukKategorija k ORDER BY k.naziv")
    List<EukKategorija> findAllOrderByNaziv();
    
    boolean existsByNaziv(String naziv);
}
