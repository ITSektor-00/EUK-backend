package com.sirus.backend.service;

import com.sirus.backend.entity.GlobalLicense;
import com.sirus.backend.repository.GlobalLicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Transactional
public class GlobalLicenseNotificationService {
    
    @Autowired
    private GlobalLicenseRepository globalLicenseRepository;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Cron job koji se izvršava svakog dana u 9:00 ujutru
     * Proverava globalnu licencu koja treba da istekne u narednih 30 dana
     */
    @Scheduled(cron = "0 0 9 * * ?") // Svaki dan u 9:00
    public void checkAndSendGlobalLicenseExpirationNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        Optional<GlobalLicense> expiringLicenseOpt = globalLicenseRepository.findGlobalLicenseExpiringSoon(now, thirtyDaysFromNow);
        
        if (expiringLicenseOpt.isPresent()) {
            GlobalLicense license = expiringLicenseOpt.get();
            try {
                sendGlobalLicenseExpirationNotification(license);
                globalLicenseRepository.markNotificationSent(license.getId());
            } catch (Exception e) {
                System.err.println("Error sending global license notification: " + e.getMessage());
            }
        }
    }
    
    /**
     * Cron job koji se izvršava svakog dana u 10:00 ujutru
     * Deaktivira istekli globalnu licencu
     */
    @Scheduled(cron = "0 0 10 * * ?") // Svaki dan u 10:00
    public void deactivateExpiredGlobalLicense() {
        int deactivatedCount = globalLicenseRepository.deactivateExpiredGlobalLicense(LocalDateTime.now());
        if (deactivatedCount > 0) {
            System.out.println("Deactivated " + deactivatedCount + " expired global license(s)");
        }
    }
    
    /**
     * Šalje obaveštenje o isteku globalne licence
     */
    public void sendGlobalLicenseExpirationNotification(GlobalLicense license) {
        try {
            String subject = "Obaveštenje o isteku softverske licence - EUK Sistem";
            String message = buildGlobalLicenseExpirationMessage(license);
            
            // Šalje se svim administratorima (možeš dodati logiku za dobijanje admin email-a)
            String adminEmail = "admin@euk.rs"; // Zameni sa stvarnim admin email-om
            
            emailService.sendEmail(adminEmail, subject, message);
            
            // Log za development
            System.out.println("=== GLOBAL LICENSE EXPIRATION NOTIFICATION ===");
            System.out.println("License Key: " + license.getLicenseKey());
            System.out.println("Expires on: " + license.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            System.out.println("Days until expiry: " + license.getDaysUntilExpiry());
            System.out.println("Message: " + message);
            System.out.println("=============================================");
            
        } catch (Exception e) {
            System.err.println("Error sending global license expiration notification: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Kreira poruku za obaveštenje o isteku globalne licence
     */
    private String buildGlobalLicenseExpirationMessage(GlobalLicense license) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        long daysUntilExpiry = license.getDaysUntilExpiry();
        
        StringBuilder message = new StringBuilder();
        message.append("Poštovani administratori,\n\n");
        message.append("Obaveštavamo Vas da će softverska licenca za EUK sistem isteći za ");
        message.append(daysUntilExpiry);
        message.append(" dana.\n\n");
        message.append("Detalji licence:\n");
        message.append("- Ključ licence: ");
        message.append(license.getLicenseKey());
        message.append("\n");
        message.append("- Datum isteka: ");
        message.append(license.getEndDate().format(formatter));
        message.append("\n");
        message.append("- Preostalo dana: ");
        message.append(daysUntilExpiry);
        message.append("\n\n");
        
        if (daysUntilExpiry <= 7) {
            message.append("⚠️ UPOZORENJE: Softverska licenca će isteći za manje od 7 dana!\n");
            message.append("Molimo Vas da kontaktirate dobavljača za produženje licence.\n\n");
        } else if (daysUntilExpiry <= 14) {
            message.append("⚠️ Softverska licenca će isteći za manje od 2 nedelje!\n");
            message.append("Preporučujemo da kontaktirate dobavljača za produženje licence.\n\n");
        }
        
        message.append("Nakon isteka licence, ceo sistem će prestati da radi.\n\n");
        message.append("Za produženje licence, molimo kontaktirajte dobavljača sistema.\n\n");
        message.append("S poštovanjem,\n");
        message.append("EUK Sistem");
        
        return message.toString();
    }
    
    /**
     * Proverava i šalje obaveštenja za globalnu licencu koja treba da istekne
     */
    public void checkGlobalLicenseExpiringSoon() {
        Optional<GlobalLicense> expiringLicenseOpt = globalLicenseRepository.findGlobalLicenseExpiringSoon(
            LocalDateTime.now(), 
            LocalDateTime.now().plusDays(30)
        );
        
        if (expiringLicenseOpt.isPresent()) {
            GlobalLicense license = expiringLicenseOpt.get();
            if (!license.getNotificationSent()) {
                sendGlobalLicenseExpirationNotification(license);
                globalLicenseRepository.markNotificationSent(license.getId());
            }
        }
    }
}
