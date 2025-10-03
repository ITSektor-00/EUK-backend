package com.sirus.backend.dto;

import com.sirus.backend.entity.EukFormularDokumenti;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class EukFormularDokumentiDto {
    
    private Integer dokumentId;
    
    @NotNull(message = "Predmet je obavezan")
    private Integer predmetId;
    private String predmetNaziv;
    
    @NotNull(message = "Polje je obavezno")
    private Integer poljeId;
    private String poljeNaziv;
    private String poljeLabel;
    
    @NotBlank(message = "Originalno ime je obavezno")
    @Size(max = 255, message = "Originalno ime ne može biti duže od 255 karaktera")
    private String originalnoIme;
    
    @NotBlank(message = "Putanja je obavezna")
    @Size(max = 500, message = "Putanja ne može biti duža od 500 karaktera")
    private String putanja;
    
    @Size(max = 100, message = "Tip fajla ne može biti duži od 100 karaktera")
    private String tipFajla;
    
    private Long velicinaFajla;
    private String formattedSize;
    private String fileExtension;
    
    private LocalDateTime datumUploada;
    
    private Integer uploadKorisnikId;
    private String uploadKorisnikUsername;
    
    // Konstruktori
    public EukFormularDokumentiDto() {}
    
    public EukFormularDokumentiDto(EukFormularDokumenti dokument) {
        this.dokumentId = dokument.getDokumentId();
        this.predmetId = dokument.getPredmet() != null ? dokument.getPredmet().getPredmetId() : null;
        this.predmetNaziv = dokument.getPredmet() != null ? dokument.getPredmet().getNazivPredmeta() : null;
        this.poljeId = dokument.getPolje() != null ? dokument.getPolje().getPoljeId() : null;
        this.poljeNaziv = dokument.getPolje() != null ? dokument.getPolje().getNazivPolja() : null;
        this.poljeLabel = dokument.getPolje() != null ? dokument.getPolje().getLabel() : null;
        this.originalnoIme = dokument.getOriginalnoIme();
        this.putanja = dokument.getPutanja();
        this.tipFajla = dokument.getTipFajla();
        this.velicinaFajla = dokument.getVelicinaFajla();
        this.formattedSize = dokument.getFormattedSize();
        this.fileExtension = dokument.getFileExtension();
        this.datumUploada = dokument.getDatumUploada();
        this.uploadKorisnikId = dokument.getUploadKorisnik() != null ? dokument.getUploadKorisnik().getId().intValue() : null;
        this.uploadKorisnikUsername = dokument.getUploadKorisnik() != null ? dokument.getUploadKorisnik().getUsername() : null;
    }
    
    // Getters and Setters
    public Integer getDokumentId() {
        return dokumentId;
    }
    
    public void setDokumentId(Integer dokumentId) {
        this.dokumentId = dokumentId;
    }
    
    public Integer getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Integer predmetId) {
        this.predmetId = predmetId;
    }
    
    public String getPredmetNaziv() {
        return predmetNaziv;
    }
    
    public void setPredmetNaziv(String predmetNaziv) {
        this.predmetNaziv = predmetNaziv;
    }
    
    public Integer getPoljeId() {
        return poljeId;
    }
    
    public void setPoljeId(Integer poljeId) {
        this.poljeId = poljeId;
    }
    
    public String getPoljeNaziv() {
        return poljeNaziv;
    }
    
    public void setPoljeNaziv(String poljeNaziv) {
        this.poljeNaziv = poljeNaziv;
    }
    
    public String getPoljeLabel() {
        return poljeLabel;
    }
    
    public void setPoljeLabel(String poljeLabel) {
        this.poljeLabel = poljeLabel;
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
    
    public String getFormattedSize() {
        return formattedSize;
    }
    
    public void setFormattedSize(String formattedSize) {
        this.formattedSize = formattedSize;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
    
    public LocalDateTime getDatumUploada() {
        return datumUploada;
    }
    
    public void setDatumUploada(LocalDateTime datumUploada) {
        this.datumUploada = datumUploada;
    }
    
    public Integer getUploadKorisnikId() {
        return uploadKorisnikId;
    }
    
    public void setUploadKorisnikId(Integer uploadKorisnikId) {
        this.uploadKorisnikId = uploadKorisnikId;
    }
    
    public String getUploadKorisnikUsername() {
        return uploadKorisnikUsername;
    }
    
    public void setUploadKorisnikUsername(String uploadKorisnikUsername) {
        this.uploadKorisnikUsername = uploadKorisnikUsername;
    }
    
    @Override
    public String toString() {
        return "EukFormularDokumentiDto{" +
                "dokumentId=" + dokumentId +
                ", predmetId=" + predmetId +
                ", predmetNaziv='" + predmetNaziv + '\'' +
                ", poljeId=" + poljeId +
                ", poljeNaziv='" + poljeNaziv + '\'' +
                ", originalnoIme='" + originalnoIme + '\'' +
                ", putanja='" + putanja + '\'' +
                ", tipFajla='" + tipFajla + '\'' +
                ", velicinaFajla=" + velicinaFajla +
                ", datumUploada=" + datumUploada +
                '}';
    }
}
