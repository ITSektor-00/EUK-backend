package com.sirus.backend.repository;

import com.sirus.backend.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License, Long> {
    
    // Pronađi aktivnu licencu za korisnika
    @Query("SELECT l FROM License l WHERE l.userId = :userId AND l.isActive = true")
    Optional<License> findActiveLicenseByUserId(@Param("userId") Long userId);
    
    // Pronađi sve aktivne licence
    @Query("SELECT l FROM License l WHERE l.isActive = true")
    List<License> findAllActiveLicenses();
    
    // Pronađi licence koje treba da isteknu u narednih 30 dana
    @Query("SELECT l FROM License l WHERE l.isActive = true " +
           "AND l.endDate BETWEEN :now AND :thirtyDaysFromNow " +
           "AND l.notificationSent = false")
    List<License> findLicensesExpiringSoon(@Param("now") LocalDateTime now, 
                                          @Param("thirtyDaysFromNow") LocalDateTime thirtyDaysFromNow);
    
    // Pronađi istekle licence
    @Query("SELECT l FROM License l WHERE l.isActive = true AND l.endDate < :now")
    List<License> findExpiredLicenses(@Param("now") LocalDateTime now);
    
    // Proveri da li korisnik ima aktivnu licencu
    @Query("SELECT COUNT(l) > 0 FROM License l WHERE l.userId = :userId AND l.isActive = true AND l.endDate > :now")
    boolean hasActiveLicense(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    // Deaktiviraj istekle licence
    @Query("UPDATE License l SET l.isActive = false WHERE l.isActive = true AND l.endDate < :now")
    int deactivateExpiredLicenses(@Param("now") LocalDateTime now);
    
    // Označi da je obaveštenje poslato
    @Query("UPDATE License l SET l.notificationSent = true WHERE l.id = :licenseId")
    int markNotificationSent(@Param("licenseId") Long licenseId);
    
    // Pronađi licencu po korisniku (bilo da je aktivna ili ne)
    @Query("SELECT l FROM License l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<License> findByUserId(@Param("userId") Long userId);
    
    // Pronađi najnoviju licencu za korisnika
    @Query("SELECT l FROM License l WHERE l.userId = :userId ORDER BY l.createdAt DESC LIMIT 1")
    Optional<License> findLatestLicenseByUserId(@Param("userId") Long userId);
}
