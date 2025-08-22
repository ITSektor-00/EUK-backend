package com.sirus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class EukUgrozenoLiceDto {
    
    private Integer ugrozenoLiceId;
    
    @NotBlank(message = "Ime je obavezno")
    @Size(max = 100, message = "Ime ne može biti duže od 100 karaktera")
    private String ime;
    
    @NotBlank(message = "Prezime je obavezno")
    @Size(max = 100, message = "Prezime ne može biti duže od 100 karaktera")
    private String prezime;
    
    @NotBlank(message = "JMBG je obavezan")
    @Pattern(regexp = "^\\d{13}$", message = "JMBG mora sadržati tačno 13 cifara")
    private String jmbg;
    
    @NotNull(message = "Datum rođenja je obavezan")
    private LocalDate datumRodjenja;
    
    @NotBlank(message = "Država rođenja je obavezna")
    @Size(max = 100, message = "Država rođenja ne može biti duža od 100 karaktera")
    private String drzavaRodjenja;
    
    @NotBlank(message = "Mesto rođenja je obavezno")
    @Size(max = 100, message = "Mesto rođenja ne može biti duže od 100 karaktera")
    private String mestoRodjenja;
    
    @NotBlank(message = "Opština rođenja je obavezna")
    @Size(max = 100, message = "Opština rođenja ne može biti duža od 100 karaktera")
    private String opstinaRodjenja;
    
    @NotNull(message = "Predmet je obavezan")
    private Integer predmetId;
    
    private String predmetNaziv;
    private String predmetStatus;
    
    // Getters and Setters
    public Integer getUgrozenoLiceId() {
        return ugrozenoLiceId;
    }
    
    public void setUgrozenoLiceId(Integer ugrozenoLiceId) {
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
    
    public String getJmbg() {
        return jmbg;
    }
    
    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }
    
    public LocalDate getDatumRodjenja() {
        return datumRodjenja;
    }
    
    public void setDatumRodjenja(LocalDate datumRodjenja) {
        this.datumRodjenja = datumRodjenja;
    }
    
    public String getDrzavaRodjenja() {
        return drzavaRodjenja;
    }
    
    public void setDrzavaRodjenja(String drzavaRodjenja) {
        this.drzavaRodjenja = drzavaRodjenja;
    }
    
    public String getMestoRodjenja() {
        return mestoRodjenja;
    }
    
    public void setMestoRodjenja(String mestoRodjenja) {
        this.mestoRodjenja = mestoRodjenja;
    }
    
    public String getOpstinaRodjenja() {
        return opstinaRodjenja;
    }
    
    public void setOpstinaRodjenja(String opstinaRodjenja) {
        this.opstinaRodjenja = opstinaRodjenja;
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
    
    public String getPredmetStatus() {
        return predmetStatus;
    }
    
    public void setPredmetStatus(String predmetStatus) {
        this.predmetStatus = predmetStatus;
    }
}
