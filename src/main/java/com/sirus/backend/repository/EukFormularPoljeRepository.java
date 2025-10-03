package com.sirus.backend.repository;

import com.sirus.backend.entity.EukFormularPolje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EukFormularPoljeRepository extends JpaRepository<EukFormularPolje, Integer> {
    
    // Pronađi polja po formularu
    List<EukFormularPolje> findByFormularFormularIdOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja po formularu i vidljivosti
    List<EukFormularPolje> findByFormularFormularIdAndVisibleTrueOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja po tipu
    List<EukFormularPolje> findByTipPoljaAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(EukFormularPolje.TipPolja tipPolja);
    
    // Pronađi polja po formularu i tipu
    List<EukFormularPolje> findByFormularFormularIdAndTipPoljaOrderByRedosledAsc(Integer formularId, EukFormularPolje.TipPolja tipPolja);
    
    // Pronađi obavezna polja po formularu
    List<EukFormularPolje> findByFormularFormularIdAndObaveznoTrueOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja po nazivu
    List<EukFormularPolje> findByNazivPoljaContainingIgnoreCaseAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(String nazivPolja);
    
    // Pronađi polja po labelu
    List<EukFormularPolje> findByLabelContainingIgnoreCaseAndFormularAktivnaTrueOrderByFormularDatumKreiranjaDesc(String label);
    
    // Pronađi polja po formularu i nazivu
    List<EukFormularPolje> findByFormularFormularIdAndNazivPoljaContainingIgnoreCaseOrderByRedosledAsc(Integer formularId, String nazivPolja);
    
    // Pronađi polja po formularu i labelu
    List<EukFormularPolje> findByFormularFormularIdAndLabelContainingIgnoreCaseOrderByRedosledAsc(Integer formularId, String label);
    
    // Proveri da li postoji polje sa istim nazivom u formularu
    boolean existsByNazivPoljaAndFormularFormularId(String nazivPolja, Integer formularId);
    
    // Proveri da li postoji polje sa istim nazivom u formularu (izuzev trenutnog)
    boolean existsByNazivPoljaAndFormularFormularIdAndPoljeIdNot(String nazivPolja, Integer formularId, Integer poljeId);
    
    // Pronađi polje sa podacima
    @Query("SELECT p FROM EukFormularPolje p LEFT JOIN FETCH p.podaci WHERE p.poljeId = :poljeId")
    Optional<EukFormularPolje> findByIdWithPodaci(@Param("poljeId") Integer poljeId);
    
    // Pronađi polje sa dokumentima
    @Query("SELECT p FROM EukFormularPolje p LEFT JOIN FETCH p.dokumenti WHERE p.poljeId = :poljeId")
    Optional<EukFormularPolje> findByIdWithDokumenti(@Param("poljeId") Integer poljeId);
    
    // Pronađi polja sa podacima po formularu
    @Query("SELECT p FROM EukFormularPolje p LEFT JOIN FETCH p.podaci WHERE p.formular.formularId = :formularId ORDER BY p.redosled")
    List<EukFormularPolje> findByFormularWithPodaci(@Param("formularId") Integer formularId);
    
    // Pronađi polja sa dokumentima po formularu
    @Query("SELECT p FROM EukFormularPolje p LEFT JOIN FETCH p.dokumenti WHERE p.formular.formularId = :formularId ORDER BY p.redosled")
    List<EukFormularPolje> findByFormularWithDokumenti(@Param("formularId") Integer formularId);
    
    // Broj polja po formularu
    @Query("SELECT COUNT(p) FROM EukFormularPolje p WHERE p.formular.formularId = :formularId")
    long countByFormular(@Param("formularId") Integer formularId);
    
    // Broj obaveznih polja po formularu
    @Query("SELECT COUNT(p) FROM EukFormularPolje p WHERE p.formular.formularId = :formularId AND p.obavezno = true")
    long countObaveznihByFormular(@Param("formularId") Integer formularId);
    
    // Broj polja po tipu
    @Query("SELECT COUNT(p) FROM EukFormularPolje p WHERE p.tipPolja = :tipPolja AND p.formular.aktivna = true")
    long countByTipPolja(@Param("tipPolja") EukFormularPolje.TipPolja tipPolja);
    
    // Pronađi polja po redosledu
    List<EukFormularPolje> findByFormularFormularIdAndRedosledOrderByRedosledAsc(Integer formularId, Integer redosled);
    
    // Pronađi polja sa redosledom većim od zadatog
    List<EukFormularPolje> findByFormularFormularIdAndRedosledGreaterThanOrderByRedosledAsc(Integer formularId, Integer redosled);
    
    // Pronađi polja sa redosledom manjim od zadatog
    List<EukFormularPolje> findByFormularFormularIdAndRedosledLessThanOrderByRedosledDesc(Integer formularId, Integer redosled);
    
    // Pronađi polja po formularu i vidljivosti
    List<EukFormularPolje> findByFormularFormularIdAndVisibleOrderByRedosledAsc(Integer formularId, Boolean visible);
    
    // Pronađi polja po formularu i readonly statusu
    List<EukFormularPolje> findByFormularFormularIdAndReadonlyOrderByRedosledAsc(Integer formularId, Boolean readonly);
    
    // Pronađi polja po formularu i obaveznosti
    List<EukFormularPolje> findByFormularFormularIdAndObaveznoOrderByRedosledAsc(Integer formularId, Boolean obavezno);
    
    // Pronađi polja sa default vrednostima
    List<EukFormularPolje> findByFormularFormularIdAndDefaultVrednostIsNotNullOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja sa validacijom
    List<EukFormularPolje> findByFormularFormularIdAndValidacijaIsNotNullOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja sa opcijama
    List<EukFormularPolje> findByFormularFormularIdAndOpcijeIsNotNullOrderByRedosledAsc(Integer formularId);
    
    // Pronađi polja po formularu sa maksimalnim redosledom
    @Query("SELECT p FROM EukFormularPolje p WHERE p.formular.formularId = :formularId ORDER BY p.redosled DESC")
    List<EukFormularPolje> findTopByFormularOrderByRedosledDesc(@Param("formularId") Integer formularId);
    
    // Pronađi polja po formularu sa minimalnim redosledom
    @Query("SELECT p FROM EukFormularPolje p WHERE p.formular.formularId = :formularId ORDER BY p.redosled ASC")
    List<EukFormularPolje> findTopByFormularOrderByRedosledAsc(@Param("formularId") Integer formularId);
    
    // Pronađi polja po formularu sa srednjim redosledom
    @Query("SELECT p FROM EukFormularPolje p WHERE p.formular.formularId = :formularId ORDER BY p.redosled ASC")
    List<EukFormularPolje> findByFormularOrderByRedosledAsc(@Param("formularId") Integer formularId);
    
    // Pronađi polja po formularu sa redosledom između
    List<EukFormularPolje> findByFormularFormularIdAndRedosledBetweenOrderByRedosledAsc(Integer formularId, Integer startRedosled, Integer endRedosled);
    
    // Pronađi polja po formularu sa redosledom većim ili jednakim
    List<EukFormularPolje> findByFormularFormularIdAndRedosledGreaterThanEqualOrderByRedosledAsc(Integer formularId, Integer redosled);
    
    // Pronađi polja po formularu sa redosledom manjim ili jednakim
    List<EukFormularPolje> findByFormularFormularIdAndRedosledLessThanEqualOrderByRedosledAsc(Integer formularId, Integer redosled);
}
