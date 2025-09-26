package com.sirus.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * Šalje email obaveštenje
     */
    public void sendEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            mailSender.send(mailMessage);
            System.out.println("Email sent successfully to: " + to);
            
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Šalje obaveštenje o isteku licence
     */
    public void sendLicenseExpirationNotification(String userEmail, String userName, int daysUntilExpiry) {
        String subject = "Obaveštenje o isteku licence - EUK Sistem";
        String message = buildLicenseExpirationMessage(userName, daysUntilExpiry);
        
        sendEmail(userEmail, subject, message);
    }
    
    /**
     * Kreira poruku za obaveštenje o isteku licence
     */
    private String buildLicenseExpirationMessage(String userName, int daysUntilExpiry) {
        StringBuilder message = new StringBuilder();
        message.append("Poštovani ").append(userName).append(",\n\n");
        message.append("Obaveštavamo Vas da će Vaša licenca za EUK sistem isteći za ");
        message.append(daysUntilExpiry);
        message.append(" dana.\n\n");
        
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
}
