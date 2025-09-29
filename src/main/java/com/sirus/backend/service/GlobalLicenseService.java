package com.sirus.backend.service;

import com.sirus.backend.entity.GlobalLicense;
import com.sirus.backend.repository.GlobalLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class GlobalLicenseService {
    
    @Autowired
    private GlobalLicenseRepository globalLicenseRepository;
    
    /**
     * Proverava da li postoji aktivna globalna licenca
     */
    public boolean hasValidGlobalLicense() {
        return globalLicenseRepository.hasActiveGlobalLicense(LocalDateTime.now());
    }
    
    /**
     * Vraća aktivnu globalnu licencu
     */
    public Optional<GlobalLicense> getActiveGlobalLicense() {
        return globalLicenseRepository.findActiveGlobalLicense();
    }
    
    /**
     * Proverava i ažurira status globalne licence
     */
    public boolean checkAndUpdateGlobalLicenseStatus() {
        Optional<GlobalLicense> licenseOpt = getActiveGlobalLicense();
        
        if (licenseOpt.isEmpty()) {
            return false;
        }
        
        GlobalLicense license = licenseOpt.get();
        
        // Proveri da li je licenca istekla
        if (license.isExpired()) {
            license.setIsActive(false);
            globalLicenseRepository.save(license);
            return false;
        }
        
        return true;
    }
    
    /**
     * Vraća informacije o globalnoj licenci
     */
    public GlobalLicenseInfo getGlobalLicenseInfo() {
        Optional<GlobalLicense> licenseOpt = getActiveGlobalLicense();
        
        if (licenseOpt.isEmpty()) {
            return new GlobalLicenseInfo(false, null, 0, false, "No active license found");
        }
        
        GlobalLicense license = licenseOpt.get();
        boolean isValid = !license.isExpired();
        boolean isExpiringSoon = license.isExpiringSoon();
        long daysUntilExpiry = license.getDaysUntilExpiry();
        
        return new GlobalLicenseInfo(isValid, license.getEndDate(), daysUntilExpiry, isExpiringSoon, "License is valid");
    }
    
    /**
     * Kreira novu globalnu licencu
     */
    public GlobalLicense createGlobalLicense(String licenseKey, int months) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusMonths(months);
        
        GlobalLicense license = new GlobalLicense(licenseKey, startDate, endDate);
        return globalLicenseRepository.save(license);
    }
    
    /**
     * Proširuje globalnu licencu
     */
    public GlobalLicense extendGlobalLicense(int months) {
        Optional<GlobalLicense> currentLicenseOpt = getActiveGlobalLicense();
        
        if (currentLicenseOpt.isPresent()) {
            GlobalLicense currentLicense = currentLicenseOpt.get();
            currentLicense.setEndDate(currentLicense.getEndDate().plusMonths(months));
            return globalLicenseRepository.save(currentLicense);
        } else {
            // Kreiraj novu licencu
            return createGlobalLicense("GLOBAL-LICENSE-" + System.currentTimeMillis(), months);
        }
    }
    
    /**
     * Vraća globalnu licencu koja treba da istekne u narednih 30 dana
     */
    public Optional<GlobalLicense> getGlobalLicenseExpiringSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        return globalLicenseRepository.findGlobalLicenseExpiringSoon(now, thirtyDaysFromNow);
    }
    
    /**
     * Deaktivira istekli globalnu licencu
     */
    public int deactivateExpiredGlobalLicense() {
        return globalLicenseRepository.deactivateExpiredGlobalLicense(LocalDateTime.now());
    }
    
    /**
     * Označava da je obaveštenje poslato
     */
    public void markNotificationSent(Long licenseId) {
        globalLicenseRepository.markNotificationSent(licenseId);
    }
    
    /**
     * Klasa za vraćanje informacija o globalnoj licenci
     */
    public static class GlobalLicenseInfo {
        private boolean isValid;
        private LocalDateTime endDate;
        private long daysUntilExpiry;
        private boolean isExpiringSoon;
        private String message;
        
        public GlobalLicenseInfo(boolean isValid, LocalDateTime endDate, long daysUntilExpiry, boolean isExpiringSoon, String message) {
            this.isValid = isValid;
            this.endDate = endDate;
            this.daysUntilExpiry = daysUntilExpiry;
            this.isExpiringSoon = isExpiringSoon;
            this.message = message;
        }
        
        // Getters
        public boolean isValid() { return isValid; }
        public LocalDateTime getEndDate() { return endDate; }
        public long getDaysUntilExpiry() { return daysUntilExpiry; }
        public boolean isExpiringSoon() { return isExpiringSoon; }
        public String getMessage() { return message; }
    }
}
