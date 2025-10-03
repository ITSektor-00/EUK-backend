package com.sirus.backend.repository;

import com.sirus.backend.entity.EukFormularDokumenti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EukFormularDokumentiRepository extends JpaRepository<EukFormularDokumenti, Integer> {
    
    // Pronađi dokumente po predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdOrderByDatumUploadaDesc(Integer predmetId);
    
    // Pronađi dokumente po polju
    List<EukFormularDokumenti> findByPoljePoljeIdOrderByDatumUploadaDesc(Integer poljeId);
    
    // Pronađi dokumente po predmetu i polju
    List<EukFormularDokumenti> findByPredmetPredmetIdAndPoljePoljeIdOrderByDatumUploadaDesc(Integer predmetId, Integer poljeId);
    
    // Pronađi dokumente po predmetu i formularu
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormular(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi dokumente po predmetu i kategoriji
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.kategorija.kategorijaId = :kategorijaId ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndKategorija(@Param("predmetId") Integer predmetId, @Param("kategorijaId") Integer kategorijaId);
    
    // Pronađi dokumente po korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdOrderByDatumUploadaDesc(Integer korisnikId);
    
    // Pronađi dokumente po datumu uploada
    List<EukFormularDokumenti> findByDatumUploadaBetweenOrderByDatumUploadaDesc(LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi dokumente po datumu uploada i predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndDatumUploadaBetweenOrderByDatumUploadaDesc(Integer predmetId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi dokumente po datumu uploada i polju
    List<EukFormularDokumenti> findByPoljePoljeIdAndDatumUploadaBetweenOrderByDatumUploadaDesc(Integer poljeId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi dokumente po datumu uploada i korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdAndDatumUploadaBetweenOrderByDatumUploadaDesc(Integer korisnikId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Pronađi dokumente po predmetu i korisniku
    List<EukFormularDokumenti> findByPredmetPredmetIdAndUploadKorisnikIdOrderByDatumUploadaDesc(Integer predmetId, Integer korisnikId);
    
    // Pronađi dokumente po polju i korisniku
    List<EukFormularDokumenti> findByPoljePoljeIdAndUploadKorisnikIdOrderByDatumUploadaDesc(Integer poljeId, Integer korisnikId);
    
    // Pronađi dokumente po predmetu i datumu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndDatumUploadaOrderByDatumUploadaDesc(Integer predmetId, LocalDateTime datum);
    
    // Pronađi dokumente po polju i datumu
    List<EukFormularDokumenti> findByPoljePoljeIdAndDatumUploadaOrderByDatumUploadaDesc(Integer poljeId, LocalDateTime datum);
    
    // Pronađi dokumente po korisniku i datumu
    List<EukFormularDokumenti> findByUploadKorisnikIdAndDatumUploadaOrderByDatumUploadaDesc(Integer korisnikId, LocalDateTime datum);
    
    // Pronađi dokumente po predmetu i formularu i korisniku
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.uploadKorisnik.id = :korisnikId ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularAndKorisnik(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("korisnikId") Integer korisnikId);
    
    // Pronađi dokumente po predmetu i formularu i datumu
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.datumUploada BETWEEN :startDate AND :endDate ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularAndDatum(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Pronađi dokumente po predmetu i formularu i korisniku i datumu
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.uploadKorisnik.id = :korisnikId AND d.datumUploada BETWEEN :startDate AND :endDate ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularAndKorisnikAndDatum(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId, @Param("korisnikId") Integer korisnikId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Pronađi dokumente po tipu fajla
    List<EukFormularDokumenti> findByTipFajlaOrderByDatumUploadaDesc(String tipFajla);
    
    // Pronađi dokumente po tipu fajla i predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndTipFajlaOrderByDatumUploadaDesc(Integer predmetId, String tipFajla);
    
    // Pronađi dokumente po tipu fajla i polju
    List<EukFormularDokumenti> findByPoljePoljeIdAndTipFajlaOrderByDatumUploadaDesc(Integer poljeId, String tipFajla);
    
    // Pronađi dokumente po tipu fajla i korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdAndTipFajlaOrderByDatumUploadaDesc(Integer korisnikId, String tipFajla);
    
    // Pronađi dokumente po veličini fajla
    List<EukFormularDokumenti> findByVelicinaFajlaBetweenOrderByDatumUploadaDesc(Long minSize, Long maxSize);
    
    // Pronađi dokumente po veličini fajla i predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndVelicinaFajlaBetweenOrderByDatumUploadaDesc(Integer predmetId, Long minSize, Long maxSize);
    
    // Pronađi dokumente po veličini fajla i polju
    List<EukFormularDokumenti> findByPoljePoljeIdAndVelicinaFajlaBetweenOrderByDatumUploadaDesc(Integer poljeId, Long minSize, Long maxSize);
    
    // Pronađi dokumente po veličini fajla i korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdAndVelicinaFajlaBetweenOrderByDatumUploadaDesc(Integer korisnikId, Long minSize, Long maxSize);
    
    // Pronađi dokumente po originalnom imenu
    List<EukFormularDokumenti> findByOriginalnoImeContainingIgnoreCaseOrderByDatumUploadaDesc(String originalnoIme);
    
    // Pronađi dokumente po originalnom imenu i predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndOriginalnoImeContainingIgnoreCaseOrderByDatumUploadaDesc(Integer predmetId, String originalnoIme);
    
    // Pronađi dokumente po originalnom imenu i polju
    List<EukFormularDokumenti> findByPoljePoljeIdAndOriginalnoImeContainingIgnoreCaseOrderByDatumUploadaDesc(Integer poljeId, String originalnoIme);
    
    // Pronađi dokumente po originalnom imenu i korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdAndOriginalnoImeContainingIgnoreCaseOrderByDatumUploadaDesc(Integer korisnikId, String originalnoIme);
    
    // Pronađi dokumente po putanji
    List<EukFormularDokumenti> findByPutanjaContainingIgnoreCaseOrderByDatumUploadaDesc(String putanja);
    
    // Pronađi dokumente po putanji i predmetu
    List<EukFormularDokumenti> findByPredmetPredmetIdAndPutanjaContainingIgnoreCaseOrderByDatumUploadaDesc(Integer predmetId, String putanja);
    
    // Pronađi dokumente po putanji i polju
    List<EukFormularDokumenti> findByPoljePoljeIdAndPutanjaContainingIgnoreCaseOrderByDatumUploadaDesc(Integer poljeId, String putanja);
    
    // Pronađi dokumente po putanji i korisniku
    List<EukFormularDokumenti> findByUploadKorisnikIdAndPutanjaContainingIgnoreCaseOrderByDatumUploadaDesc(Integer korisnikId, String putanja);
    
    // Broj dokumenata po predmetu
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId")
    long countByPredmet(@Param("predmetId") Integer predmetId);
    
    // Broj dokumenata po polju
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.polje.poljeId = :poljeId")
    long countByPolje(@Param("poljeId") Integer poljeId);
    
    // Broj dokumenata po korisniku
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.uploadKorisnik.id = :korisnikId")
    long countByKorisnik(@Param("korisnikId") Integer korisnikId);
    
    // Broj dokumenata po datumu
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.datumUploada BETWEEN :startDate AND :endDate")
    long countByDatum(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Broj dokumenata po predmetu i formularu
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId")
    long countByPredmetAndFormular(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Broj dokumenata po predmetu i kategoriji
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.kategorija.kategorijaId = :kategorijaId")
    long countByPredmetAndKategorija(@Param("predmetId") Integer predmetId, @Param("kategorijaId") Integer kategorijaId);
    
    // Broj dokumenata po tipu fajla
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.tipFajla = :tipFajla")
    long countByTipFajla(@Param("tipFajla") String tipFajla);
    
    // Broj dokumenata po veličini fajla
    @Query("SELECT COUNT(d) FROM EukFormularDokumenti d WHERE d.velicinaFajla BETWEEN :minSize AND :maxSize")
    long countByVelicinaFajla(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
    
    // Proveri da li postoji dokument za predmet i polje
    boolean existsByPredmetPredmetIdAndPoljePoljeId(Integer predmetId, Integer poljeId);
    
    // Proveri da li postoji dokument za predmet i polje sa originalnim imenom
    boolean existsByPredmetPredmetIdAndPoljePoljeIdAndOriginalnoIme(Integer predmetId, Integer poljeId, String originalnoIme);
    
    // Proveri da li postoji dokument za predmet i polje sa originalnim imenom (izuzev trenutnog)
    boolean existsByPredmetPredmetIdAndPoljePoljeIdAndOriginalnoImeAndDokumentIdNot(Integer predmetId, Integer poljeId, String originalnoIme, Integer dokumentId);
    
    // Pronađi dokumente po predmetu sa veličinom fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.velicinaFajla IS NOT NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetWithVelicinaFajla(@Param("predmetId") Integer predmetId);
    
    // Pronađi dokumente po predmetu bez veličine fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.velicinaFajla IS NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetWithoutVelicinaFajla(@Param("predmetId") Integer predmetId);
    
    // Pronađi dokumente po predmetu sa tipom fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.tipFajla IS NOT NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetWithTipFajla(@Param("predmetId") Integer predmetId);
    
    // Pronađi dokumente po predmetu bez tipa fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.tipFajla IS NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetWithoutTipFajla(@Param("predmetId") Integer predmetId);
    
    // Pronađi dokumente po predmetu i formularu sa veličinom fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.velicinaFajla IS NOT NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularWithVelicinaFajla(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi dokumente po predmetu i formularu bez veličine fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.velicinaFajla IS NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularWithoutVelicinaFajla(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi dokumente po predmetu i formularu sa tipom fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.tipFajla IS NOT NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularWithTipFajla(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
    
    // Pronađi dokumente po predmetu i formularu bez tipa fajla
    @Query("SELECT d FROM EukFormularDokumenti d WHERE d.predmet.predmetId = :predmetId AND d.polje.formular.formularId = :formularId AND d.tipFajla IS NULL ORDER BY d.datumUploada DESC")
    List<EukFormularDokumenti> findByPredmetAndFormularWithoutTipFajla(@Param("predmetId") Integer predmetId, @Param("formularId") Integer formularId);
}
