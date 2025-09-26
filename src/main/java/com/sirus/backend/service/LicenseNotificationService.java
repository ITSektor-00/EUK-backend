package com.sirus.backend.service;

import com.sirus.backend.entity.License;
import com.sirus.backend.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class LicenseNotificationService {
    
    @Autowired
    private LicenseRepository licenseRepository;
    
    @Autowired
    private EmailService emailService; // Pretpostavka da postoji EmailService
    
    /**
     * Cron job koji se izvršava svakog dana u 9:00 ujutru
     * Proverava licence koje treba da isteknu u narednih 30 dana
     */
    @Scheduled(cron = "0 0 9 * * ?") // Svaki dan u 9:00
    public void checkAndSendExpirationNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        List<License> expiringLicenses = licenseRepository.findLicensesExpiringSoon(now, thirtyDaysFromNow);
        
        for (License license : expiringLicenses) {
            try {
                sendExpirationNotification(license);
                licenseRepository.markNotificationSent(license.getId());
            } catch (Exception e) {
                // Log error but continue with other licenses
                System.err.println("Error sending notification for license " + license.getId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Cron job koji se izvršava svakog dana u 10:00 ujutru
     * Deaktivira istekle licence
     */
    @Scheduled(cron = "0 0 10 * * ?") // Svaki dan u 10:00
    public void deactivateExpiredLicenses() {
        int deactivatedCount = licenseRepository.deactivateExpiredLicenses(LocalDateTime.now());
        if (deactivatedCount > 0) {
            System.out.println("Deactivated " + deactivatedCount + " expired licenses");
        }
    }
    
    /**
     * Šalje obaveštenje korisniku o isteku licence
     */
    public void sendExpirationNotification(License license) {
        try {
            String subject = "Obaveštenje o isteku licence - EUK Sistem";
            String message = buildExpirationMessage(license);
            
            // Pretpostavka da imamo EmailService
            // emailService.sendEmail(userEmail, subject, message);
            
            // Za sada ćemo samo logovati
            System.out.println("=== LICENSE EXPIRATION NOTIFICATION ===");
            System.out.println("User ID: " + license.getUserId());
            System.out.println("License ID: " + license.getId());
            System.out.println("Expires on: " + license.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            System.out.println("Days until expiry: " + license.getDaysUntilExpiry());
            System.out.println("Message: " + message);
            System.out.println("=====================================");
            
        } catch (Exception e) {
            System.err.println("Error sending expiration notification: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Šalje obaveštenje o isteku licence odmah
     */
    public void sendImmediateExpirationNotification(Long licenseId) {
        License license = licenseRepository.findById(licenseId).orElse(null);
        if (license != null && !license.getNotificationSent()) {
            sendExpirationNotification(license);
            licenseRepository.markNotificationSent(licenseId);
        }
    }
    
    /**
     * Kreira poruku za obaveštenje o isteku licence
     */
    private String buildExpirationMessage(License license) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        long daysUntilExpiry = license.getDaysUntilExpiry();
        
        StringBuilder message = new StringBuilder();
        message.append("Poštovani korisniče,\n\n");
        message.append("Obaveštavamo Vas da će Vaša licenca za EUK sistem isteći za ");
        message.append(daysUntilExpiry);
        message.append(" dana.\n\n");
        message.append("Detalji licence:\n");
        message.append("- Datum isteka: ");
        message.append(license.getEndDate().format(formatter));
        message.append("\n");
        message.append("- Preostalo dana: ");
        message.append(daysUntilExpiry);
        message.append("\n\n");
        
        if (daysUntilExpiry <= 7) {
            message.append("⚠️ UPOZORENJE: Vaša licenca će isteći za manje od 7 dana!\n");
            message.append("Molimo Vas da kontaktirate administratora za produženje licence.\n\n");
        } else if (daysUntilExpiry <= 14) {
            message.append("⚠️ Vaša licenca će isteći za manje od 2 nedelje!\n");
            message.append("Preporučujemo da kontaktirate administratora za produženje licence.\n\n");
        }
        
        message.append("Nakon isteka licence, pristup sistemu će biti blokiran.\n\n");
        message.append("Za produženje licence, molimo kontaktirajte administratora sistema.\n\n");
        message.append("S poštovanjem,\n");
        message.append("EUK Sistem");
        
        return message.toString();
    }
    
    /**
     * Proverava i šalje obaveštenja za sve licence koje treba da isteknu
     */
    public void checkAllExpiringLicenses() {
        List<License> expiringLicenses = licenseRepository.findLicensesExpiringSoon(
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30)
        );
        
        for (License license : expiringLicenses) {
            if (!license.getNotificationSent()) {
                sendExpirationNotification(license);
                licenseRepository.markNotificationSent(license.getId());
            }
        }
    }
}
