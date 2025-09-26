package com.sirus.backend.repository;

import com.sirus.backend.entity.EukKategorija;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EukKategorijaRepository extends JpaRepository<EukKategorija, Integer> {
    
    @Query("SELECT k FROM EukKategorija k ORDER BY k.naziv")
    List<EukKategorija> findAllOrderByNaziv();
    
    boolean existsByNaziv(String naziv);
    
    boolean existsBySkracenica(String skracenica);
    
    @Query("SELECT k FROM EukKategorija k WHERE " +
           "(:naziv IS NULL OR LOWER(k.naziv) LIKE LOWER(CONCAT('%', :naziv, '%'))) " +
           "AND (:skracenica IS NULL OR LOWER(k.skracenica) LIKE LOWER(CONCAT('%', :skracenica, '%'))) " +
           "ORDER BY k.naziv")
    List<EukKategorija> search(@Param("naziv") String naziv, @Param("skracenica") String skracenica);
}
