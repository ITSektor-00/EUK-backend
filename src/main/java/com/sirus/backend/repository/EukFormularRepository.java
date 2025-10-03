package com.sirus.backend.repository;

import com.sirus.backend.entity.EukFormular;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EukFormularRepository extends JpaRepository<EukFormular, Integer> {
    
    // Pronađi formulare po kategoriji
    List<EukFormular> findByKategorijaKategorijaIdAndAktivnaTrueOrderByDatumKreiranjaDesc(Integer kategorijaId);
    
    // Pronađi formulare po kategoriji sa paginacijom
    Page<EukFormular> findByKategorijaKategorijaIdAndAktivnaTrueOrderByDatumKreiranjaDesc(Integer kategorijaId, Pageable pageable);
    
    // Pronađi sve aktivne formulare
    List<EukFormular> findByAktivnaTrueOrderByDatumKreiranjaDesc();
    
    // Pronađi formulare sa paginacijom
    Page<EukFormular> findByAktivnaTrueOrderByDatumKreiranjaDesc(Pageable pageable);
    
    // Pronađi formulare po nazivu (pretraga)
    List<EukFormular> findByNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(String naziv);
    
    // Pronađi formulare po nazivu sa paginacijom
    Page<EukFormular> findByNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(String naziv, Pageable pageable);
    
    // Pronađi formulare po kategoriji i nazivu
    List<EukFormular> findByKategorijaKategorijaIdAndNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(
            Integer kategorijaId, String naziv);
    
    // Pronađi formulare po kategoriji i nazivu sa paginacijom
    Page<EukFormular> findByKategorijaKategorijaIdAndNazivContainingIgnoreCaseAndAktivnaTrueOrderByDatumKreiranjaDesc(
            Integer kategorijaId, String naziv, Pageable pageable);
    
    // Proveri da li postoji formular sa istim nazivom u kategoriji
    boolean existsByNazivAndKategorijaKategorijaIdAndAktivnaTrue(String naziv, Integer kategorijaId);
    
    // Proveri da li postoji formular sa istim nazivom u kategoriji (izuzev trenutnog)
    boolean existsByNazivAndKategorijaKategorijaIdAndAktivnaTrueAndFormularIdNot(String naziv, Integer kategorijaId, Integer formularId);
    
    // Pronađi formular sa poljima
    @Query("SELECT f FROM EukFormular f LEFT JOIN FETCH f.polja WHERE f.formularId = :formularId AND f.aktivna = true")
    Optional<EukFormular> findByIdWithPolja(@Param("formularId") Integer formularId);
    
    // Pronađi formulare sa poljima po kategoriji
    @Query("SELECT f FROM EukFormular f LEFT JOIN FETCH f.polja WHERE f.kategorija.kategorijaId = :kategorijaId AND f.aktivna = true ORDER BY f.datumKreiranja DESC")
    List<EukFormular> findByKategorijaWithPolja(@Param("kategorijaId") Integer kategorijaId);
    
    // Pronađi formular sa istorijom
    @Query("SELECT f FROM EukFormular f LEFT JOIN FETCH f.istorija WHERE f.formularId = :formularId")
    Optional<EukFormular> findByIdWithIstorija(@Param("formularId") Integer formularId);
    
    // Broj formulare po kategoriji
    @Query("SELECT COUNT(f) FROM EukFormular f WHERE f.kategorija.kategorijaId = :kategorijaId AND f.aktivna = true")
    long countByKategorija(@Param("kategorijaId") Integer kategorijaId);
    
    // Broj aktivnih formulare
    @Query("SELECT COUNT(f) FROM EukFormular f WHERE f.aktivna = true")
    long countAktivnih();
    
    // Pronađi formulare sa brojem polja
    @Query("SELECT f, COUNT(p) as brojPolja FROM EukFormular f LEFT JOIN f.polja p WHERE f.aktivna = true GROUP BY f ORDER BY f.datumKreiranja DESC")
    List<Object[]> findFormulariWithBrojPolja();
    
    // Pronađi formulare sa brojem polja po kategoriji
    @Query("SELECT f, COUNT(p) as brojPolja FROM EukFormular f LEFT JOIN f.polja p WHERE f.kategorija.kategorijaId = :kategorijaId AND f.aktivna = true GROUP BY f ORDER BY f.datumKreiranja DESC")
    List<Object[]> findFormulariWithBrojPoljaByKategorija(@Param("kategorijaId") Integer kategorijaId);
    
    // Pronađi formulare kreirane od strane korisnika
    List<EukFormular> findByCreatedByIdAndAktivnaTrueOrderByDatumKreiranjaDesc(Integer createdById);
    
    // Pronađi formulare ažurirane od strane korisnika
    List<EukFormular> findByUpdatedByIdAndAktivnaTrueOrderByDatumAzuriranjaDesc(Integer updatedById);
    
    // Pronađi formulare po verziji
    List<EukFormular> findByVerzijaAndAktivnaTrueOrderByDatumKreiranjaDesc(Integer verzija);
    
    // Pronađi formulare starije od datuma
    List<EukFormular> findByDatumKreiranjaBeforeAndAktivnaTrue(java.time.LocalDateTime datum);
    
    // Pronađi formulare novije od datuma
    List<EukFormular> findByDatumKreiranjaAfterAndAktivnaTrue(java.time.LocalDateTime datum);
    
    // Pronađi formulare po opsegu datuma
    List<EukFormular> findByDatumKreiranjaBetweenAndAktivnaTrueOrderByDatumKreiranjaDesc(
            java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}
