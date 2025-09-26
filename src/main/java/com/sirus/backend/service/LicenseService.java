package com.sirus.backend.service;

import com.sirus.backend.entity.License;
import com.sirus.backend.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LicenseService {
    
    @Autowired
    private LicenseRepository licenseRepository;
    
    /**
     * Kreira novu licencu za korisnika sa 12-mesečnim važenjem
     */
    public License createLicense(Long userId) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(12);
        
        License license = new License(userId, startDate, endDate);
        return licenseRepository.save(license);
    }
    
    /**
     * Proverava da li korisnik ima aktivnu licencu
     */
    public boolean hasValidLicense(Long userId) {
        return licenseRepository.hasActiveLicense(userId, LocalDateTime.now());
    }
    
    /**
     * Vraća aktivnu licencu za korisnika
     */
    public Optional<License> getActiveLicense(Long userId) {
        return licenseRepository.findActiveLicenseByUserId(userId);
    }
    
    /**
     * Proverava i ažurira status licence
     * Ako je licenca istekla, deaktivira je
     */
    public boolean checkAndUpdateLicenseStatus(Long userId) {
        Optional<License> licenseOpt = getActiveLicense(userId);
        
        if (licenseOpt.isEmpty()) {
            return false;
        }
        
        License license = licenseOpt.get();
        
        // Proveri da li je licenca istekla
        if (license.isExpired()) {
            license.setIsActive(false);
            licenseRepository.save(license);
            return false;
        }
        
        return true;
    }
    
    /**
     * Vraća sve licence koje treba da isteknu u narednih 30 dana
     */
    public List<License> getLicensesExpiringSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        return licenseRepository.findLicensesExpiringSoon(now, thirtyDaysFromNow);
    }
    
    /**
     * Vraća sve istekle licence
     */
    public List<License> getExpiredLicenses() {
        return licenseRepository.findExpiredLicenses(LocalDateTime.now());
    }
    
    /**
     * Deaktivira sve istekle licence
     */
    public int deactivateExpiredLicenses() {
        return licenseRepository.deactivateExpiredLicenses(LocalDateTime.now());
    }
    
    /**
     * Označava da je obaveštenje poslato za licencu
     */
    public void markNotificationSent(Long licenseId) {
        licenseRepository.markNotificationSent(licenseId);
    }
    
    /**
     * Vraća informacije o licenci za korisnika
     */
    public LicenseInfo getLicenseInfo(Long userId) {
        Optional<License> licenseOpt = getActiveLicense(userId);
        
        if (licenseOpt.isEmpty()) {
            return new LicenseInfo(false, null, 0, false);
        }
        
        License license = licenseOpt.get();
        boolean isValid = !license.isExpired();
        boolean isExpiringSoon = license.isExpiringSoon();
        long daysUntilExpiry = license.getDaysUntilExpiry();
        
        return new LicenseInfo(isValid, license.getEndDate(), daysUntilExpiry, isExpiringSoon);
    }
    
    /**
     * Vraća sve licence za korisnika
     */
    public List<License> getUserLicenses(Long userId) {
        return licenseRepository.findByUserId(userId);
    }
    
    /**
     * Vraća najnoviju licencu za korisnika
     */
    public Optional<License> getLatestLicense(Long userId) {
        return licenseRepository.findLatestLicenseByUserId(userId);
    }
    
    /**
     * Proširuje licencu za dodatnih 12 meseci
     */
    public License extendLicense(Long userId) {
        Optional<License> currentLicenseOpt = getActiveLicense(userId);
        
        if (currentLicenseOpt.isPresent()) {
            License currentLicense = currentLicenseOpt.get();
            currentLicense.setEndDate(currentLicense.getEndDate().plusMonths(12));
            return licenseRepository.save(currentLicense);
        } else {
            // Kreiraj novu licencu
            return createLicense(userId);
        }
    }
    
    /**
     * Klasa za vraćanje informacija o licenci
     */
    public static class LicenseInfo {
        private boolean isValid;
        private LocalDateTime endDate;
        private long daysUntilExpiry;
        private boolean isExpiringSoon;
        
        public LicenseInfo(boolean isValid, LocalDateTime endDate, long daysUntilExpiry, boolean isExpiringSoon) {
            this.isValid = isValid;
            this.endDate = endDate;
            this.daysUntilExpiry = daysUntilExpiry;
            this.isExpiringSoon = isExpiringSoon;
        }
        
        // Getters
        public boolean isValid() { return isValid; }
        public LocalDateTime getEndDate() { return endDate; }
        public long getDaysUntilExpiry() { return daysUntilExpiry; }
        public boolean isExpiringSoon() { return isExpiringSoon; }
    }
}
