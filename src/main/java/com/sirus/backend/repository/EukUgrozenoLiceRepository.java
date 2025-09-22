package com.sirus.backend.repository;

import com.sirus.backend.entity.EukUgrozenoLice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EukUgrozenoLiceRepository extends JpaRepository<EukUgrozenoLice, Integer> {
    
    @Query("SELECT u, p.nazivPredmeta, p.status " +
           "FROM EukUgrozenoLice u " +
           "LEFT JOIN u.predmet p " +
           "ORDER BY u.ugrozenoLiceId DESC")
    Page<Object[]> findAllWithPredmetInfo(Pageable pageable);
    
    @Query("SELECT u, p.nazivPredmeta, p.status " +
           "FROM EukUgrozenoLice u " +
           "LEFT JOIN u.predmet p " +
           "WHERE u.predmet.predmetId = :predmetId")
    List<Object[]> findByPredmetIdWithPredmetInfo(@Param("predmetId") Integer predmetId);
    
    Optional<EukUgrozenoLice> findByJmbg(String jmbg);
    
    boolean existsByJmbg(String jmbg);
    
    List<EukUgrozenoLice> findByPredmetPredmetId(Integer predmetId);
    
    @Query("SELECT u FROM EukUgrozenoLice u WHERE u.ime LIKE %:ime% OR u.prezime LIKE %:prezime%")
    List<EukUgrozenoLice> findByImeOrPrezimeContaining(@Param("ime") String ime, @Param("prezime") String prezime);
    
    // OPTIMIZED search queries for large datasets
    @Query("SELECT u, p.nazivPredmeta, p.status " +
           "FROM EukUgrozenoLice u " +
           "LEFT JOIN u.predmet p " +
           "WHERE (:jmbg IS NULL OR u.jmbg = :jmbg) " +
           "AND (:ime IS NULL OR LOWER(u.ime) LIKE LOWER(CONCAT('%', :ime, '%'))) " +
           "AND (:prezime IS NULL OR LOWER(u.prezime) LIKE LOWER(CONCAT('%', :prezime, '%'))) " +
           "AND (:predmetId IS NULL OR u.predmet.predmetId = :predmetId) " +
           "ORDER BY u.ugrozenoLiceId DESC")
    Page<Object[]> findWithFilters(
        @Param("jmbg") String jmbg,
        @Param("ime") String ime, 
        @Param("prezime") String prezime,
        @Param("predmetId") Integer predmetId,
        Pageable pageable);
        
    // Count query for performance
    @Query("SELECT COUNT(u) FROM EukUgrozenoLice u WHERE u.predmet.predmetId = :predmetId")
    Long countByPredmetId(@Param("predmetId") Integer predmetId);
}
