package com.sirus.backend.repository;

import com.sirus.backend.entity.EukUgrozenoLiceT2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EukUgrozenoLiceT2Repository extends JpaRepository<EukUgrozenoLiceT2, Integer> {
    
    // Osnovne pretrage po JMBG
    Optional<EukUgrozenoLiceT2> findByJmbg(String jmbg);
    
    // Pretraga po rednom broju
    Optional<EukUgrozenoLiceT2> findByRedniBroj(String redniBroj);
    
    // Pretraga po imenu i prezimenu
    List<EukUgrozenoLiceT2> findByImeAndPrezime(String ime, String prezime);
    
    // Pretraga po imenu (case insensitive)
    List<EukUgrozenoLiceT2> findByImeIgnoreCase(String ime);
    
    // Pretraga po prezimenu (case insensitive)
    List<EukUgrozenoLiceT2> findByPrezimeIgnoreCase(String prezime);
    
    // Pretraga po gradu/opštini
    List<EukUgrozenoLiceT2> findByGradOpstinaIgnoreCase(String gradOpstina);
    
    // Pretraga po mestu
    List<EukUgrozenoLiceT2> findByMestoIgnoreCase(String mesto);
    
    // Pretraga po PTT broju
    List<EukUgrozenoLiceT2> findByPttBroj(String pttBroj);
    
    // Pretraga po ED broju
    List<EukUgrozenoLiceT2> findByEdBroj(String edBroj);
    
    // Pretraga po periodu važenja rešenja
    List<EukUgrozenoLiceT2> findByPokVazenjaResenjaOStatusuContainingIgnoreCase(String period);
    
    // Kompleksne pretrage sa paginacijom
    @Query("SELECT u FROM EukUgrozenoLiceT2 u WHERE " +
           "(:ime IS NULL OR LOWER(u.ime) LIKE LOWER(CONCAT('%', :ime, '%'))) AND " +
           "(:prezime IS NULL OR LOWER(u.prezime) LIKE LOWER(CONCAT('%', :prezime, '%'))) AND " +
           "(:jmbg IS NULL OR u.jmbg = :jmbg) AND " +
           "(:gradOpstina IS NULL OR LOWER(u.gradOpstina) LIKE LOWER(CONCAT('%', :gradOpstina, '%'))) AND " +
           "(:mesto IS NULL OR LOWER(u.mesto) LIKE LOWER(CONCAT('%', :mesto, '%'))) AND " +
           "(:edBroj IS NULL OR LOWER(u.edBroj) LIKE LOWER(CONCAT('%', :edBroj, '%')))")
    Page<EukUgrozenoLiceT2> findBySearchCriteria(
            @Param("ime") String ime,
            @Param("prezime") String prezime,
            @Param("jmbg") String jmbg,
            @Param("gradOpstina") String gradOpstina,
            @Param("mesto") String mesto,
            @Param("edBroj") String edBroj,
            Pageable pageable);
    
    // Pretraga po imenu i prezimenu sa paginacijom
    @Query("SELECT u FROM EukUgrozenoLiceT2 u WHERE " +
           "LOWER(u.ime) LIKE LOWER(CONCAT('%', :ime, '%')) AND " +
           "LOWER(u.prezime) LIKE LOWER(CONCAT('%', :prezime, '%'))")
    Page<EukUgrozenoLiceT2> findByImeAndPrezimeContaining(
            @Param("ime") String ime,
            @Param("prezime") String prezime,
            Pageable pageable);
    
    // Pretraga po adresnim podacima
    @Query("SELECT u FROM EukUgrozenoLiceT2 u WHERE " +
           "(:gradOpstina IS NULL OR LOWER(u.gradOpstina) LIKE LOWER(CONCAT('%', :gradOpstina, '%'))) AND " +
           "(:mesto IS NULL OR LOWER(u.mesto) LIKE LOWER(CONCAT('%', :mesto, '%'))) AND " +
           "(:pttBroj IS NULL OR u.pttBroj = :pttBroj)")
    Page<EukUgrozenoLiceT2> findByAddressCriteria(
            @Param("gradOpstina") String gradOpstina,
            @Param("mesto") String mesto,
            @Param("pttBroj") String pttBroj,
            Pageable pageable);
    
    // Pretraga po energetskim podacima
    @Query("SELECT u FROM EukUgrozenoLiceT2 u WHERE " +
           "(:edBroj IS NULL OR LOWER(u.edBroj) LIKE LOWER(CONCAT('%', :edBroj, '%'))) AND " +
           "(:pokVazenja IS NULL OR LOWER(u.pokVazenjaResenjaOStatusu) LIKE LOWER(CONCAT('%', :pokVazenja, '%')))")
    Page<EukUgrozenoLiceT2> findByEnergyCriteria(
            @Param("edBroj") String edBroj,
            @Param("pokVazenja") String pokVazenja,
            Pageable pageable);
    
    // Brojanje zapisa po kriterijumu
    @Query("SELECT COUNT(u) FROM EukUgrozenoLiceT2 u WHERE " +
           "(:ime IS NULL OR LOWER(u.ime) LIKE LOWER(CONCAT('%', :ime, '%'))) AND " +
           "(:prezime IS NULL OR LOWER(u.prezime) LIKE LOWER(CONCAT('%', :prezime, '%'))) AND " +
           "(:jmbg IS NULL OR u.jmbg = :jmbg)")
    long countBySearchCriteria(
            @Param("ime") String ime,
            @Param("prezime") String prezime,
            @Param("jmbg") String jmbg);
    
    // Provera da li postoji JMBG
    boolean existsByJmbg(String jmbg);
    
    // Provera da li postoji redni broj
    boolean existsByRedniBroj(String redniBroj);
    
    // Pretraga po više JMBG-ova
    List<EukUgrozenoLiceT2> findByJmbgIn(List<String> jmbgList);
    
    // Pretraga po više rednih brojeva
    List<EukUgrozenoLiceT2> findByRedniBrojIn(List<String> redniBrojList);
    
    // Sortiranje po datumu kreiranja
    List<EukUgrozenoLiceT2> findAllByOrderByCreatedAtDesc();
    
    // Sortiranje po datumu ažuriranja
    List<EukUgrozenoLiceT2> findAllByOrderByUpdatedAtDesc();
    
    // Pretraga sa sortiranjem po imenu
    List<EukUgrozenoLiceT2> findAllByOrderByImeAsc();
    
    // Pretraga sa sortiranjem po prezimenu
    List<EukUgrozenoLiceT2> findAllByOrderByPrezimeAsc();
    
    // Pretraga sa sortiranjem po rednom broju
    List<EukUgrozenoLiceT2> findAllByOrderByRedniBrojAsc();
}
