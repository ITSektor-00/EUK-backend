package com.sirus.backend.repository;

import com.sirus.backend.entity.EukPredmetFormularPodaci;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EukPredmetFormularPodaciRepository extends JpaRepository<EukPredmetFormularPodaci, Integer> {
    
    // Pronađi podatke po predmetu
    List<EukPredmetFormularPodaci> findByPredmetPredmetIdOrderByPoljeRedosledAsc(Integer predmetId);
    
    // Pronađi podatke po polju
    List<EukPredmetFormularPodaci> findByPoljePoljeIdOrderByDatumUnosaDesc(Integer poljeId);
    
    // Pronađi podatke po predmetu i polju
    Optional<EukPredmetFormularPodaci> findByPredmetPredmetIdAndPoljePoljeId(Integer predmetId, Integer poljeId);
    
    // Pronađi podatke po predmetu i formularu
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormular(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi podatke po predmetu i kategoriji
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.kategorija.kategorijaId = :kategorijaId ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndKategorija(@Param("predmetId") Integer predmetId, @Param("kategorijaId") Integer kategorijaId);
    
    // Pronađi podatke po vrednosti
    List<EukPredmetFormularPodaci> findByVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(String vrednost);
    
    // Pronađi podatke po vrednosti i predmetu
    List<EukPredmetFormularPodaci> findByPredmetPredmetIdAndVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(Integer predmetId, String vrednost);
    
    // Pronađi podatke po vrednosti i polju
    List<EukPredmetFormularPodaci> findByPoljePoljeIdAndVrednostContainingIgnoreCaseOrderByDatumUnosaDesc(Integer poljeId, String vrednost);
    
    // Pronađi podatke po korisniku
    List<EukPredmetFormularPodaci> findByUnioKorisnikIdOrderByDatumUnosaDesc(Integer korisnikId);
    
    // Pronađi podatke po datumu unosa
    List<EukPredmetFormularPodaci> findByDatumUnosaBetweenOrderByDatumUnosaDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi podatke po datumu unosa i predmetu
    List<EukPredmetFormularPodaci> findByPredmetPredmetIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(Integer predmetId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi podatke po datumu unosa i polju
    List<EukPredmetFormularPodaci> findByPoljePoljeIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(Integer poljeId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi podatke po datumu unosa i korisniku
    List<EukPredmetFormularPodaci> findByUnioKorisnikIdAndDatumUnosaBetweenOrderByDatumUnosaDesc(Integer korisnikId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi podatke po predmetu i korisniku
    List<EukPredmetFormularPodaci> findByPredmetPredmetIdAndUnioKorisnikIdOrderByDatumUnosaDesc(Integer predmetId, Integer korisnikId);
    
    // Pronađi podatke po polju i korisniku
    List<EukPredmetFormularPodaci> findByPoljePoljeIdAndUnioKorisnikIdOrderByDatumUnosaDesc(Integer poljeId, Integer korisnikId);
    
    // Pronađi podatke po predmetu i datumu
    List<EukPredmetFormularPodaci> findByPredmetPredmetIdAndDatumUnosaOrderByDatumUnosaDesc(Integer predmetId, LocalDateTime datum);
    
    // Pronađi podatke po polju i datumu
    List<EukPredmetFormularPodaci> findByPoljePoljeIdAndDatumUnosaOrderByDatumUnosaDesc(Integer poljeId, LocalDateTime datum);
    
    // Pronađi podatke po korisniku i datumu
    List<EukPredmetFormularPodaci> findByUnioKorisnikIdAndDatumUnosaOrderByDatumUnosaDesc(Integer korisnikId, LocalDateTime datum);
    
    // Pronađi podatke po predmetu i formularu i korisniku
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.unioKorisnik.id = :korisnikId ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularAndKorisnik(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("korisnikId") Integer korisnikId);
    
    // Pronađi podatke po predmetu i formularu i datumu
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.datumUnosa BETWEEN :startDate AND :endDate ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularAndDatum(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Pronađi podatke po predmetu i formularu i korisniku i datumu
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.unioKorisnik.id = :korisnikId AND p.datumUnosa BETWEEN :startDate AND :endDate ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularAndKorisnikAndDatum(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("korisnikId") Integer korisnikId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Broj podataka po predmetu
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId")
    long countByPredmet(@Param("predmetId") Integer predmetId);
    
    // Broj podataka po polju
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.polje.poljeId = :poljeId")
    long countByPolje(@Param("poljeId") Integer poljeId);
    
    // Broj podataka po korisniku
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.unioKorisnik.id = :korisnikId")
    long countByKorisnik(@Param("korisnikId") Integer korisnikId);
    
    // Broj podataka po datumu
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.datumUnosa BETWEEN :startDate AND :endDate")
    long countByDatum(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Broj podataka po predmetu i formularu
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId")
    long countByPredmetAndFormular(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Broj podataka po predmetu i kategoriji
    @Query("SELECT COUNT(p) FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.kategorija.kategorijaId = :kategorijaId")
    long countByPredmetAndKategorija(@Param("predmetId") Integer predmetId, @Param("kategorijaId") Integer kategorijaId);
    
    // Proveri da li postoji podatak za predmet i polje
    boolean existsByPredmetPredmetIdAndPoljePoljeId(Integer predmetId, Integer poljeId);
    
    // Proveri da li postoji podatak za predmet i polje sa vrednošću
    boolean existsByPredmetPredmetIdAndPoljePoljeIdAndVrednost(Integer predmetId, Integer poljeId, String vrednost);
    
    // Proveri da li postoji podatak za predmet i polje sa vrednošću (izuzev trenutnog)
    boolean existsByPredmetPredmetIdAndPoljePoljeIdAndVrednostAndPodatakIdNot(Integer predmetId, Integer poljeId, String vrednost, Integer podatakId);
    
    // Pronađi podatke po predmetu sa vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.vrednost IS NOT NULL ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetWithVrednosti(@Param("predmetId") Integer predmetId);
    
    // Pronađi podatke po predmetu bez vrednosti
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.vrednost IS NULL ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetWithoutVrednosti(@Param("predmetId") Integer predmetId);
    
    // Pronađi podatke po predmetu sa praznim vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND (p.vrednost IS NULL OR p.vrednost = '') ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetWithEmptyVrednosti(@Param("predmetId") Integer predmetId);
    
    // Pronađi podatke po predmetu sa ne-praznim vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.vrednost IS NOT NULL AND p.vrednost != '' ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetWithNonEmptyVrednosti(@Param("predmetId") Integer predmetId);
    
    // Pronađi podatke po predmetu i formularu sa vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.vrednost IS NOT NULL ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularWithVrednosti(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi podatke po predmetu i formularu bez vrednosti
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.vrednost IS NULL ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularWithoutVrednosti(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi podatke po predmetu i formularu sa praznim vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND (p.vrednost IS NULL OR p.vrednost = '') ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularWithEmptyVrednosti(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi podatke po predmetu i formularu sa ne-praznim vrednostima
    @Query("SELECT p FROM EukPredmetFormularPodaci p WHERE p.predmet.predmetId = :predmetId AND p.polje.formular.formularId = :formularId AND p.vrednost IS NOT NULL AND p.vrednost != '' ORDER BY p.polje.redosled ASC")
    List<EukPredmetFormularPodaci> findByPredmetAndFormularWithNonEmptyVrednosti(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
}
