package com.sirus.backend.repository;

import com.sirus.backend.entity.GlobalLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface GlobalLicenseRepository extends JpaRepository<GlobalLicense, Long> {
    
    // Pronađi aktivnu globalnu licencu
    @Query("SELECT gl FROM GlobalLicense gl WHERE gl.isActive = true")
    Optional<GlobalLicense> findActiveGlobalLicense();
    
    // Proveri da li postoji aktivna globalna licenca
    @Query("SELECT COUNT(gl) > 0 FROM GlobalLicense gl WHERE gl.isActive = true AND gl.endDate > :now")
    boolean hasActiveGlobalLicense(@Param("now") LocalDateTime now);
    
    // Pronađi globalnu licencu koja treba da istekne u narednih 30 dana
    @Query("SELECT gl FROM GlobalLicense gl WHERE gl.isActive = true " +
           "AND gl.endDate BETWEEN :now AND :thirtyDaysFromNow " +
           "AND gl.notificationSent = false")
    Optional<GlobalLicense> findGlobalLicenseExpiringSoon(@Param("now") LocalDateTime now, 
                                                          @Param("thirtyDaysFromNow") LocalDateTime thirtyDaysFromNow);
    
    // Pronađi istekli globalnu licencu
    @Query("SELECT gl FROM GlobalLicense gl WHERE gl.isActive = true AND gl.endDate < :now")
    Optional<GlobalLicense> findExpiredGlobalLicense(@Param("now") LocalDateTime now);
    
    // Deaktiviraj istekli globalnu licencu
    @Query("UPDATE GlobalLicense gl SET gl.isActive = false WHERE gl.isActive = true AND gl.endDate < :now")
    int deactivateExpiredGlobalLicense(@Param("now") LocalDateTime now);
    
    // Označi da je obaveštenje poslato
    @Query("UPDATE GlobalLicense gl SET gl.notificationSent = true WHERE gl.id = :licenseId")
    int markNotificationSent(@Param("licenseId") Long licenseId);
    
    // Pronađi globalnu licencu po ključu
    @Query("SELECT gl FROM GlobalLicense gl WHERE gl.licenseKey = :licenseKey")
    Optional<GlobalLicense> findByLicenseKey(@Param("licenseKey") String licenseKey);
}
