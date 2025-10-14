package com.sirus.backend.repository;

import com.sirus.backend.entity.EukPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EukPredmetRepository extends JpaRepository<EukPredmet, Integer> {
    
    @Query("SELECT p, k.naziv as kategorijaNaziv, 0 as brojUgrozenihLica " +
           "FROM EukPredmet p " +
           "LEFT JOIN FETCH p.kategorija k " +
           "GROUP BY p.predmetId, p.datumKreiranja, p.nazivPredmeta, p.status, p.odgovornaOsoba, p.prioritet, p.rokZaZavrsetak, p.kategorija.kategorijaId, k.naziv")
    Page<Object[]> findAllWithKategorijaAndUgrozenaLicaCount(Pageable pageable);
    
    @Query("SELECT p, k.naziv as kategorijaNaziv, 0 as brojUgrozenihLica " +
           "FROM EukPredmet p " +
           "LEFT JOIN FETCH p.kategorija k " +
           "WHERE (:status IS NULL OR p.status = :status) " +
           "AND (:prioritet IS NULL OR p.prioritet = :prioritet) " +
           "AND (:kategorijaId IS NULL OR p.kategorija.kategorijaId = :kategorijaId) " +
           "AND (:odgovornaOsoba IS NULL OR LOWER(p.odgovornaOsoba) LIKE LOWER(CONCAT('%', :odgovornaOsoba, '%'))) " +
           "AND (:nazivPredmeta IS NULL OR LOWER(p.nazivPredmeta) LIKE LOWER(CONCAT('%', :nazivPredmeta, '%'))) " +
           "GROUP BY p.predmetId, p.datumKreiranja, p.nazivPredmeta, p.status, p.odgovornaOsoba, p.prioritet, p.rokZaZavrsetak, p.kategorija.kategorijaId, k.naziv")
    Page<Object[]> findAllWithFilters(
            @Param("status") EukPredmet.Status status,
            @Param("prioritet") EukPredmet.Prioritet prioritet,
            @Param("kategorijaId") Integer kategorijaId,
            @Param("odgovornaOsoba") String odgovornaOsoba,
            @Param("nazivPredmeta") String nazivPredmeta,
            Pageable pageable);
    
    @Query("SELECT p FROM EukPredmet p LEFT JOIN FETCH p.kategorija WHERE p.predmetId = :id")
    EukPredmet findByIdWithKategorijaAndUgrozenaLica(@Param("id") Integer id);
    
    List<EukPredmet> findByKategorijaKategorijaId(Integer kategorijaId);
    
    List<EukPredmet> findByStatus(EukPredmet.Status status);
    
    List<EukPredmet> findByPrioritet(EukPredmet.Prioritet prioritet);
    
    List<EukPredmet> findByOdgovornaOsobaContainingIgnoreCase(String odgovornaOsoba);
    
    List<EukPredmet> findByRokZaZavrsetakBefore(LocalDate date);
    
    @Query("SELECT DISTINCT p.nazivPredmeta FROM EukPredmet p ORDER BY p.nazivPredmeta")
    List<String> findAllNaziviPredmeta();
}
