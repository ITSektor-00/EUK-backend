package com.sirus.backend.repository;

import com.sirus.backend.entity.EukUgrozenoLiceT1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EukUgrozenoLiceT1Repository extends JpaRepository<EukUgrozenoLiceT1, Integer> {
    
    // Osnovne pretrage
    Optional<EukUgrozenoLiceT1> findByJmbg(String jmbg);
    
    boolean existsByJmbg(String jmbg);
    
    Optional<EukUgrozenoLiceT1> findByRedniBroj(String redniBroj);
    
    boolean existsByRedniBroj(String redniBroj);
    
    // Pretraga po imenu i prezimenu
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "LOWER(u.ime) LIKE LOWER(CONCAT('%', :ime, '%')) OR " +
           "LOWER(u.prezime) LIKE LOWER(CONCAT('%', :prezime, '%'))")
    List<EukUgrozenoLiceT1> findByImeOrPrezimeContaining(@Param("ime") String ime, @Param("prezime") String prezime);
    
    // Pretraga po adresi
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "(:gradOpstina IS NULL OR LOWER(u.gradOpstina) LIKE LOWER(CONCAT('%', :gradOpstina, '%'))) AND " +
           "(:mesto IS NULL OR LOWER(u.mesto) LIKE LOWER(CONCAT('%', :mesto, '%'))) AND " +
           "(:pttBroj IS NULL OR u.pttBroj = :pttBroj)")
    Page<EukUgrozenoLiceT1> findByAddress(@Param("gradOpstina") String gradOpstina, 
                                         @Param("mesto") String mesto, 
                                         @Param("pttBroj") String pttBroj, 
                                         Pageable pageable);
    
    // Pretraga po energetskim podacima
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "(:osnovStatusa IS NULL OR LOWER(u.osnovSticanjaStatusa) LIKE LOWER(CONCAT('%', :osnovStatusa, '%'))) AND " +
           "(:edBroj IS NULL OR LOWER(u.edBrojBrojMernogUredjaja) LIKE LOWER(CONCAT('%', :edBroj, '%')))")
    Page<EukUgrozenoLiceT1> findByEnergetskiPodaci(@Param("osnovStatusa") String osnovStatusa,
                                                   @Param("edBroj") String edBroj,
                                                   Pageable pageable);
    
    // Pretraga po finansijskim podacima
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "(:brojRacuna IS NULL OR LOWER(u.brojRacuna) LIKE LOWER(CONCAT('%', :brojRacuna, '%'))) AND " +
           "(:datumOd IS NULL OR u.datumIzdavanjaRacuna >= :datumOd) AND " +
           "(:datumDo IS NULL OR u.datumIzdavanjaRacuna <= :datumDo) AND " +
           "(:iznosOd IS NULL OR u.iznosUmanjenjaSaPdv >= :iznosOd) AND " +
           "(:iznosDo IS NULL OR u.iznosUmanjenjaSaPdv <= :iznosDo)")
    Page<EukUgrozenoLiceT1> findByFinansijskiPodaci(@Param("brojRacuna") String brojRacuna,
                                                    @Param("datumOd") LocalDate datumOd,
                                                    @Param("datumDo") LocalDate datumDo,
                                                    @Param("iznosOd") BigDecimal iznosOd,
                                                    @Param("iznosDo") BigDecimal iznosDo,
                                                    Pageable pageable);
    
    // Kompleksna pretraga sa svim filterima - Native query zbog problema sa Hibernate metadata caching
    @Query(value = "SELECT * FROM euk.ugrozeno_lice_t1 u WHERE " +
           "(:jmbg IS NULL OR u.jmbg = :jmbg) AND " +
           "(:redniBroj IS NULL OR u.redni_broj = :redniBroj) AND " +
           "(:ime IS NULL OR LOWER(u.ime::TEXT) LIKE LOWER(CONCAT('%', :ime, '%'))) AND " +
           "(:prezime IS NULL OR LOWER(u.prezime::TEXT) LIKE LOWER(CONCAT('%', :prezime, '%'))) AND " +
           "(:gradOpstina IS NULL OR LOWER(u.grad_opstina::TEXT) LIKE LOWER(CONCAT('%', :gradOpstina, '%'))) AND " +
           "(:mesto IS NULL OR LOWER(u.mesto::TEXT) LIKE LOWER(CONCAT('%', :mesto, '%'))) AND " +
           "(:pttBroj IS NULL OR u.ptt_broj = :pttBroj) AND " +
           "(:osnovStatusa IS NULL OR LOWER(u.osnov_sticanja_statusa::TEXT) LIKE LOWER(CONCAT('%', :osnovStatusa, '%'))) AND " +
           "(:edBroj IS NULL OR LOWER(u.ed_broj_broj_mernog_uredjaja::TEXT) LIKE LOWER(CONCAT('%', :edBroj, '%'))) AND " +
           "(:brojRacuna IS NULL OR LOWER(u.broj_racuna::TEXT) LIKE LOWER(CONCAT('%', :brojRacuna, '%'))) AND " +
           "(:datumOd IS NULL OR u.datum_izdavanja_racuna >= :datumOd) AND " +
           "(:datumDo IS NULL OR u.datum_izdavanja_racuna <= :datumDo) AND " +
           "(:iznosOd IS NULL OR u.iznos_umanjenja_sa_pdv >= :iznosOd) AND " +
           "(:iznosDo IS NULL OR u.iznos_umanjenja_sa_pdv <= :iznosDo) " +
           "ORDER BY u.ugrozeno_lice_id DESC",
           countQuery = "SELECT COUNT(*) FROM euk.ugrozeno_lice_t1 u WHERE " +
           "(:jmbg IS NULL OR u.jmbg = :jmbg) AND " +
           "(:redniBroj IS NULL OR u.redni_broj = :redniBroj) AND " +
           "(:ime IS NULL OR LOWER(u.ime::TEXT) LIKE LOWER(CONCAT('%', :ime, '%'))) AND " +
           "(:prezime IS NULL OR LOWER(u.prezime::TEXT) LIKE LOWER(CONCAT('%', :prezime, '%'))) AND " +
           "(:gradOpstina IS NULL OR LOWER(u.grad_opstina::TEXT) LIKE LOWER(CONCAT('%', :gradOpstina, '%'))) AND " +
           "(:mesto IS NULL OR LOWER(u.mesto::TEXT) LIKE LOWER(CONCAT('%', :mesto, '%'))) AND " +
           "(:pttBroj IS NULL OR u.ptt_broj = :pttBroj) AND " +
           "(:osnovStatusa IS NULL OR LOWER(u.osnov_sticanja_statusa::TEXT) LIKE LOWER(CONCAT('%', :osnovStatusa, '%'))) AND " +
           "(:edBroj IS NULL OR LOWER(u.ed_broj_broj_mernog_uredjaja::TEXT) LIKE LOWER(CONCAT('%', :edBroj, '%'))) AND " +
           "(:brojRacuna IS NULL OR LOWER(u.broj_racuna::TEXT) LIKE LOWER(CONCAT('%', :brojRacuna, '%'))) AND " +
           "(:datumOd IS NULL OR u.datum_izdavanja_racuna >= :datumOd) AND " +
           "(:datumDo IS NULL OR u.datum_izdavanja_racuna <= :datumDo) AND " +
           "(:iznosOd IS NULL OR u.iznos_umanjenja_sa_pdv >= :iznosOd) AND " +
           "(:iznosDo IS NULL OR u.iznos_umanjenja_sa_pdv <= :iznosDo)",
           nativeQuery = true)
    Page<EukUgrozenoLiceT1> findWithFilters(
            @Param("jmbg") String jmbg,
            @Param("redniBroj") String redniBroj,
            @Param("ime") String ime,
            @Param("prezime") String prezime,
            @Param("gradOpstina") String gradOpstina,
            @Param("mesto") String mesto,
            @Param("pttBroj") String pttBroj,
            @Param("osnovStatusa") String osnovStatusa,
            @Param("edBroj") String edBroj,
            @Param("brojRacuna") String brojRacuna,
            @Param("datumOd") LocalDate datumOd,
            @Param("datumDo") LocalDate datumDo,
            @Param("iznosOd") BigDecimal iznosOd,
            @Param("iznosDo") BigDecimal iznosDo,
            Pageable pageable);
    
    // Statistike i agregacije
    @Query("SELECT COUNT(u) FROM EukUgrozenoLiceT1 u WHERE u.gradOpstina = :gradOpstina")
    Long countByGradOpstina(@Param("gradOpstina") String gradOpstina);
    
    @Query("SELECT COUNT(u) FROM EukUgrozenoLiceT1 u WHERE u.osnovSticanjaStatusa = :osnovStatusa")
    Long countByOsnovSticanjaStatusa(@Param("osnovStatusa") String osnovStatusa);
    
    // Pretraga po kategoriji (osnov sticanja statusa povezan sa kategorija.skracenica)
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE u.osnovSticanjaStatusa = :kategorijaSkracenica")
    Page<EukUgrozenoLiceT1> findByKategorijaSkracenica(@Param("kategorijaSkracenica") String kategorijaSkracenica, Pageable pageable);
    
    // Statistike po kategorijama
    @Query("SELECT u.osnovSticanjaStatusa, COUNT(u) FROM EukUgrozenoLiceT1 u WHERE u.osnovSticanjaStatusa IS NOT NULL GROUP BY u.osnovSticanjaStatusa")
    List<Object[]> countByKategorijaSkracenica();
    
    @Query("SELECT SUM(u.iznosUmanjenjaSaPdv) FROM EukUgrozenoLiceT1 u WHERE u.iznosUmanjenjaSaPdv IS NOT NULL")
    BigDecimal sumIznosUmanjenjaSaPdv();
    
    // Note: These queries use native SQL since HQL doesn't support PostgreSQL-specific functions
    // Using native queries for PostgreSQL SPLIT_PART and regex functions
    // These queries will work after the database migration is complete
    @Query(value = "SELECT AVG(CAST(SPLIT_PART(SPLIT_PART(u.potrosnja_i_povrsina_combined, 'Потрошња у kWh/', 2), '/', 1) AS DECIMAL)) " +
                   "FROM euk.ugrozeno_lice_t1 u WHERE u.potrosnja_i_povrsina_combined IS NOT NULL " +
                   "AND u.potrosnja_i_povrsina_combined ~ 'Потрошња у kWh/[^/]+/'", nativeQuery = true)
    BigDecimal avgPotrosnjaKwh();
    
    @Query(value = "SELECT AVG(CAST(SPLIT_PART(SPLIT_PART(u.potrosnja_i_povrsina_combined, 'загревана површина у m2/', 2), '/', 1) AS DECIMAL)) " +
                   "FROM euk.ugrozeno_lice_t1 u WHERE u.potrosnja_i_povrsina_combined IS NOT NULL " +
                   "AND u.potrosnja_i_povrsina_combined ~ 'загревана површина у m2/[^/]*$'", nativeQuery = true)
    BigDecimal avgZagrevanaPovrsinaM2();
    
    // Fallback queries for before migration (using old column names)
    @Query(value = "SELECT AVG(u.potrosnja_kwh) FROM euk.ugrozeno_lice_t1 u WHERE u.potrosnja_kwh IS NOT NULL", nativeQuery = true)
    BigDecimal avgPotrosnjaKwhOld();
    
    @Query(value = "SELECT AVG(u.zagrevana_povrsina_m2) FROM euk.ugrozeno_lice_t1 u WHERE u.zagrevana_povrsina_m2 IS NOT NULL", nativeQuery = true)
    BigDecimal avgZagrevanaPovrsinaM2Old();
    
    // Pretraga po datumu kreiranja
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "(:datumOd IS NULL OR DATE(u.createdAt) >= :datumOd) AND " +
           "(:datumDo IS NULL OR DATE(u.createdAt) <= :datumDo) " +
           "ORDER BY u.createdAt DESC")
    Page<EukUgrozenoLiceT1> findByCreatedAtBetween(@Param("datumOd") LocalDate datumOd,
                                                   @Param("datumDo") LocalDate datumDo,
                                                   Pageable pageable);
    
    // Najnoviji zapisi
    @Query("SELECT u FROM EukUgrozenoLiceT1 u ORDER BY u.createdAt DESC")
    Page<EukUgrozenoLiceT1> findLatestRecords(Pageable pageable);
    
    // Najstariji zapisi
    @Query("SELECT u FROM EukUgrozenoLiceT1 u ORDER BY u.createdAt ASC")
    Page<EukUgrozenoLiceT1> findOldestRecords(Pageable pageable);
    
    // Pretraga po broju članova domaćinstva
    @Query("SELECT u FROM EukUgrozenoLiceT1 u WHERE " +
           "(:brojOd IS NULL OR u.brojClanovaDomacinstva >= :brojOd) AND " +
           "(:brojDo IS NULL OR u.brojClanovaDomacinstva <= :brojDo)")
    Page<EukUgrozenoLiceT1> findByBrojClanovaDomacinstvaBetween(@Param("brojOd") Integer brojOd,
                                                                @Param("brojDo") Integer brojDo,
                                                                Pageable pageable);
}
