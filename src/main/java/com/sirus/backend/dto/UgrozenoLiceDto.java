package com.sirus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UgrozenoLiceDto {
    
    @NotNull(message = "ID ugroženog lica je obavezan")
    private Long ugrozenoLiceId;
    
    @NotBlank(message = "Ime je obavezno")
    private String ime;
    
    @NotBlank(message = "Prezime je obavezno")
    private String prezime;
    
    @NotBlank(message = "Ulica i broj su obavezni")
    private String ulicaIBroj;
    
    @NotBlank(message = "PTT broj je obavezan")
    private String pttBroj;
    
    @NotBlank(message = "Grad/Opština je obavezan")
    private String gradOpstina;
    
    @NotBlank(message = "Mesto je obavezno")
    private String mesto;
    
    public UgrozenoLiceDto() {}
    
    public UgrozenoLiceDto(Long ugrozenoLiceId, String ime, String prezime, 
                          String ulicaIBroj, String pttBroj, String gradOpstina, String mesto) {
        this.ugrozenoLiceId = ugrozenoLiceId;
        this.ime = ime;
        this.prezime = prezime;
        this.ulicaIBroj = ulicaIBroj;
        this.pttBroj = pttBroj;
        this.gradOpstina = gradOpstina;
        this.mesto = mesto;
    }
    
    public Long getUgrozenoLiceId() {
        return ugrozenoLiceId;
    }
    
    public void setUgrozenoLiceId(Long ugrozenoLiceId) {
        this.ugrozenoLiceId = ugrozenoLiceId;
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
    
    public String getUlicaIBroj() {
        return ulicaIBroj;
    }
    
    public void setUlicaIBroj(String ulicaIBroj) {
        this.ulicaIBroj = ulicaIBroj;
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
}
