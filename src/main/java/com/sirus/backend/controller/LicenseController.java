package com.sirus.backend.controller;

import com.sirus.backend.entity.License;
import com.sirus.backend.service.LicenseService;
import com.sirus.backend.service.LicenseNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
@CrossOrigin(origins = "*")
public class LicenseController {
    
    @Autowired
    private LicenseService licenseService;
    
    @Autowired
    private LicenseNotificationService notificationService;
    
    /**
     * Proverava status licence za trenutnog korisnika
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getLicenseStatus(@RequestParam Long userId) {
        try {
            LicenseService.LicenseInfo licenseInfo = licenseService.getLicenseInfo(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasValidLicense", licenseInfo.isValid());
            response.put("endDate", licenseInfo.getEndDate());
            response.put("daysUntilExpiry", licenseInfo.getDaysUntilExpiry());
            response.put("isExpiringSoon", licenseInfo.isExpiringSoon());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking license status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Kreira novu licencu za korisnika
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createLicense(@RequestParam Long userId) {
        try {
            License license = licenseService.createLicense(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("licenseId", license.getId());
            response.put("startDate", license.getStartDate());
            response.put("endDate", license.getEndDate());
            response.put("message", "License created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error creating license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Proširuje licencu za dodatnih 12 meseci
     */
    @PostMapping("/extend")
    public ResponseEntity<Map<String, Object>> extendLicense(@RequestParam Long userId) {
        try {
            License license = licenseService.extendLicense(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("licenseId", license.getId());
            response.put("newEndDate", license.getEndDate());
            response.put("message", "License extended successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error extending license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Vraća sve licence za korisnika
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserLicenses(@PathVariable Long userId) {
        try {
            List<License> licenses = licenseService.getUserLicenses(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("licenses", licenses);
            response.put("count", licenses.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error fetching user licenses: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Vraća najnoviju licencu za korisnika
     */
    @GetMapping("/latest/{userId}")
    public ResponseEntity<Map<String, Object>> getLatestLicense(@PathVariable Long userId) {
        try {
            return licenseService.getLatestLicense(userId)
                .map(license -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("license", license);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("success", true);
                    put("license", null);
                    put("message", "No license found for user");
                }}));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error fetching latest license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Proverava da li korisnik ima važeću licencu
     */
    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkLicense(@PathVariable Long userId) {
        try {
            boolean hasValidLicense = licenseService.hasValidLicense(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasValidLicense", hasValidLicense);
            response.put("message", hasValidLicense ? "License is valid" : "License is invalid or expired");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Admin endpoint - vraća sve licence koje treba da isteknu u narednih 30 dana
     */
    @GetMapping("/admin/expiring")
    public ResponseEntity<Map<String, Object>> getExpiringLicenses() {
        try {
            List<License> expiringLicenses = licenseService.getLicensesExpiringSoon();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("expiringLicenses", expiringLicenses);
            response.put("count", expiringLicenses.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error fetching expiring licenses: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Admin endpoint - deaktivira istekle licence
     */
    @PostMapping("/admin/deactivate-expired")
    public ResponseEntity<Map<String, Object>> deactivateExpiredLicenses() {
        try {
            int deactivatedCount = licenseService.deactivateExpiredLicenses();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deactivatedCount", deactivatedCount);
            response.put("message", "Deactivated " + deactivatedCount + " expired licenses");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error deactivating expired licenses: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Admin endpoint - šalje obaveštenja za licence koje treba da isteknu
     */
    @PostMapping("/admin/send-notifications")
    public ResponseEntity<Map<String, Object>> sendExpirationNotifications() {
        try {
            notificationService.checkAllExpiringLicenses();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Expiration notifications sent");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error sending notifications: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
