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
           "LEFT JOIN u.predmet p")
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
}
