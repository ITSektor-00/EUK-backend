package com.sirus.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class EukUgrozenoLiceT2Dto {
    
    private Integer ugrozenoLiceId;
    
    @NotBlank(message = "Redni broj je obavezan")
    @Size(max = 20, message = "Redni broj ne može biti duži od 20 karaktera")
    private String redniBroj;
    
    @NotBlank(message = "Ime je obavezno")
    @Size(max = 100, message = "Ime ne može biti duže od 100 karaktera")
    private String ime;
    
    @NotBlank(message = "Prezime je obavezno")
    @Size(max = 100, message = "Prezime ne može biti duže od 100 karaktera")
    private String prezime;
    
    @NotBlank(message = "JMBG je obavezan")
    @Pattern(regexp = "^\\d{13}$", message = "JMBG mora sadržati tačno 13 cifara")
    private String jmbg;
    
    @Size(max = 10, message = "PTT broj ne može biti duži od 10 karaktera")
    private String pttBroj;
    
    @Size(max = 100, message = "Grad/Opština ne može biti duži od 100 karaktera")
    private String gradOpstina;
    
    @Size(max = 100, message = "Mesto ne može biti duže od 100 karaktera")
    private String mesto;
    
    @Size(max = 200, message = "Ulica i broj ne može biti duži od 200 karaktera")
    private String ulicaIBroj;
    
    @Size(max = 100, message = "ED broj ne može biti duži od 100 karaktera")
    private String edBroj;
    
    @Size(max = 200, message = "Period važenja rešenja o statusu ne može biti duži od 200 karaktera")
    private String pokVazenjaResenjaOStatusu;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Konstruktori
    public EukUgrozenoLiceT2Dto() {}
    
    public EukUgrozenoLiceT2Dto(String redniBroj, String ime, String prezime, String jmbg) {
        this.redniBroj = redniBroj;
        this.ime = ime;
        this.prezime = prezime;
        this.jmbg = jmbg;
    }
    
    // Getters i Setters
    public Integer getUgrozenoLiceId() {
        return ugrozenoLiceId;
    }
    
    public void setUgrozenoLiceId(Integer ugrozenoLiceId) {
        this.ugrozenoLiceId = ugrozenoLiceId;
    }
    
    public String getRedniBroj() {
        return redniBroj;
    }
    
    public void setRedniBroj(String redniBroj) {
        this.redniBroj = redniBroj;
    }
    
    public String getIme() {
        return ime;
    }
    
    public void setIme(String ime) {
        this.ime = ime;
    }
    
    public String getPrezime() {
        return prezime;
    }
    
    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }
    
    public String getJmbg() {
        return jmbg;
    }
    
    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }
    
    public String getPttBroj() {
        return pttBroj;
    }
    
    public void setPttBroj(String pttBroj) {
        this.pttBroj = pttBroj;
    }
    
    public String getGradOpstina() {
        return gradOpstina;
    }
    
    public void setGradOpstina(String gradOpstina) {
        this.gradOpstina = gradOpstina;
    }
    
    public String getMesto() {
        return mesto;
    }
    
    public void setMesto(String mesto) {
        this.mesto = mesto;
    }
    
    public String getUlicaIBroj() {
        return ulicaIBroj;
    }
    
    public void setUlicaIBroj(String ulicaIBroj) {
        this.ulicaIBroj = ulicaIBroj;
    }
    
    public String getEdBroj() {
        return edBroj;
    }
    
    public void setEdBroj(String edBroj) {
        this.edBroj = edBroj;
    }
    
    public String getPokVazenjaResenjaOStatusu() {
        return pokVazenjaResenjaOStatusu;
    }
    
    public void setPokVazenjaResenjaOStatusu(String pokVazenjaResenjaOStatusu) {
        this.pokVazenjaResenjaOStatusu = pokVazenjaResenjaOStatusu;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // toString metoda
    @Override
    public String toString() {
        return "EukUgrozenoLiceT2Dto{" +
                "ugrozenoLiceId=" + ugrozenoLiceId +
                ", redniBroj='" + redniBroj + '\'' +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", jmbg='" + jmbg + '\'' +
                ", pttBroj='" + pttBroj + '\'' +
                ", gradOpstina='" + gradOpstina + '\'' +
                ", mesto='" + mesto + '\'' +
                ", ulicaIBroj='" + ulicaIBroj + '\'' +
                ", edBroj='" + edBroj + '\'' +
                ", pokVazenjaResenjaOStatusu='" + pokVazenjaResenjaOStatusu + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
