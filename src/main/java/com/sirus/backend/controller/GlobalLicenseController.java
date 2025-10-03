package com.sirus.backend.controller;

import com.sirus.backend.entity.GlobalLicense;
import com.sirus.backend.service.GlobalLicenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/global-license")
public class GlobalLicenseController {
    
    @Autowired
    private GlobalLicenseService globalLicenseService;
    
    /**
     * Proverava status globalne licence
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getGlobalLicenseStatus() {
        try {
            GlobalLicenseService.GlobalLicenseInfo licenseInfo = globalLicenseService.getGlobalLicenseInfo();
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasValidLicense", licenseInfo.isValid());
            response.put("endDate", licenseInfo.getEndDate());
            response.put("daysUntilExpiry", licenseInfo.getDaysUntilExpiry());
            response.put("isExpiringSoon", licenseInfo.isExpiringSoon());
            response.put("message", licenseInfo.getMessage());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking global license status: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Proverava da li postoji važeća globalna licenca
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkGlobalLicense() {
        try {
            boolean hasValidLicense = globalLicenseService.hasValidGlobalLicense();
            
            Map<String, Object> response = new HashMap<>();
            response.put("hasValidLicense", hasValidLicense);
            response.put("message", hasValidLicense ? "Global license is valid" : "Global license is invalid or expired");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Error checking global license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Vraća aktivnu globalnu licencu
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveGlobalLicense() {
        try {
            Optional<GlobalLicense> licenseOpt = globalLicenseService.getActiveGlobalLicense();
            
            if (licenseOpt.isPresent()) {
                GlobalLicense license = licenseOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("license", license);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("license", null);
                response.put("message", "No active global license found");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error fetching active global license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Kreira novu globalnu licencu (Admin endpoint)
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createGlobalLicense(
            @RequestParam String licenseKey,
            @RequestParam(defaultValue = "12") int months) {
        try {
            GlobalLicense license = globalLicenseService.createGlobalLicense(licenseKey, months);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("licenseId", license.getId());
            response.put("licenseKey", license.getLicenseKey());
            response.put("startDate", license.getStartDate());
            response.put("endDate", license.getEndDate());
            response.put("message", "Global license created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error creating global license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Proširuje globalnu licencu (Admin endpoint)
     */
    @PostMapping("/extend")
    public ResponseEntity<Map<String, Object>> extendGlobalLicense(
            @RequestParam(defaultValue = "12") int months) {
        try {
            GlobalLicense license = globalLicenseService.extendGlobalLicense(months);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("licenseId", license.getId());
            response.put("newEndDate", license.getEndDate());
            response.put("message", "Global license extended successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error extending global license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Admin endpoint - deaktivira istekli globalnu licencu
     */
    @PostMapping("/admin/deactivate-expired")
    public ResponseEntity<Map<String, Object>> deactivateExpiredGlobalLicense() {
        try {
            int deactivatedCount = globalLicenseService.deactivateExpiredGlobalLicense();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deactivatedCount", deactivatedCount);
            response.put("message", "Deactivated " + deactivatedCount + " expired global license(s)");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error deactivating expired global license: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
