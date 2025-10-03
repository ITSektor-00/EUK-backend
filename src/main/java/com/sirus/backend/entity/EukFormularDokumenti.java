package com.sirus.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "formular_dokumenti", schema = "euk")
public class EukFormularDokumenti {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dokument_id")
    private Integer dokumentId;
    
    @NotNull(message = "Predmet je obavezan")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predmet_id", nullable = false)
    private EukPredmet predmet;
    
    @NotNull(message = "Polje je obavezno")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polje_id", nullable = false)
    private EukFormularPolje polje;
    
    @NotBlank(message = "Originalno ime je obavezno")
    @Size(max = 255, message = "Originalno ime ne može biti duže od 255 karaktera")
    @Column(name = "originalno_ime", nullable = false, length = 255)
    private String originalnoIme;
    
    @NotBlank(message = "Putanja je obavezna")
    @Size(max = 500, message = "Putanja ne može biti duža od 500 karaktera")
    @Column(name = "putanja", nullable = false, length = 500)
    private String putanja;
    
    @Size(max = 100, message = "Tip fajla ne može biti duži od 100 karaktera")
    @Column(name = "tip_fajla", length = 100)
    private String tipFajla;
    
    @Column(name = "velicina_fajla")
    private Long velicinaFajla;
    
    @CreationTimestamp
    @Column(name = "datum_uploada", nullable = false, updatable = false)
    private LocalDateTime datumUploada;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_korisnik")
    private User uploadKorisnik;
    
    // Konstruktori
    public EukFormularDokumenti() {}
    
    public EukFormularDokumenti(EukPredmet predmet, EukFormularPolje polje, String originalnoIme, String putanja) {
        this.predmet = predmet;
        this.polje = polje;
        this.originalnoIme = originalnoIme;
        this.putanja = putanja;
    }
    
    // Getters and Setters
    public Integer getDokumentId() {
        return dokumentId;
    }
    
    public void setDokumentId(Integer dokumentId) {
        this.dokumentId = dokumentId;
    }
    
    public EukPredmet getPredmet() {
        return predmet;
    }
    
    public void setPredmet(EukPredmet predmet) {
        this.predmet = predmet;
    }
    
    public EukFormularPolje getPolje() {
        return polje;
    }
    
    public void setPolje(EukFormularPolje polje) {
        this.polje = polje;
    }
    
    public String getOriginalnoIme() {
        return originalnoIme;
    }
    
    public void setOriginalnoIme(String originalnoIme) {
        this.originalnoIme = originalnoIme;
    }
    
    public String getPutanja() {
        return putanja;
    }
    
    public void setPutanja(String putanja) {
        this.putanja = putanja;
    }
    
    public String getTipFajla() {
        return tipFajla;
    }
    
    public void setTipFajla(String tipFajla) {
        this.tipFajla = tipFajla;
    }
    
    public Long getVelicinaFajla() {
        return velicinaFajla;
    }
    
    public void setVelicinaFajla(Long velicinaFajla) {
        this.velicinaFajla = velicinaFajla;
    }
    
    public LocalDateTime getDatumUploada() {
        return datumUploada;
    }
    
    public void setDatumUploada(LocalDateTime datumUploada) {
        this.datumUploada = datumUploada;
    }
    
    public User getUploadKorisnik() {
        return uploadKorisnik;
    }
    
    public void setUploadKorisnik(User uploadKorisnik) {
        this.uploadKorisnik = uploadKorisnik;
    }
    
    // Helper metode
    public String getFileExtension() {
        if (originalnoIme != null && originalnoIme.contains(".")) {
            return originalnoIme.substring(originalnoIme.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
    
    public String getFormattedSize() {
        if (velicinaFajla == null) return "N/A";
        
        long bytes = velicinaFajla;
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    @Override
    public String toString() {
        return "EukFormularDokumenti{" +
                "dokumentId=" + dokumentId +
                ", predmet=" + (predmet != null ? predmet.getPredmetId() : null) +
                ", polje=" + (polje != null ? polje.getPoljeId() : null) +
                ", originalnoIme='" + originalnoIme + '\'' +
                ", putanja='" + putanja + '\'' +
                ", tipFajla='" + tipFajla + '\'' +
                ", velicinaFajla=" + velicinaFajla +
                ", datumUploada=" + datumUploada +
                '}';
    }
}
