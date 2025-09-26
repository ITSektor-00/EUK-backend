package com.sirus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class EukKategorijaDto {
    
    private Integer kategorijaId;
    
    @NotBlank(message = "Naziv kategorije je obavezan")
    @Size(max = 255, message = "Naziv kategorije ne može biti duži od 255 karaktera")
    private String naziv;
    
    @NotBlank(message = "Skraćenica kategorije je obavezna")
    @Size(max = 10, message = "Skraćenica kategorije ne može biti duža od 10 karaktera")
    private String skracenica;
    
    // Getters and Setters
    public Integer getKategorijaId() {
        return kategorijaId;
    }
    
    public void setKategorijaId(Integer kategorijaId) {
        this.kategorijaId = kategorijaId;
    }
    
    public String getNaziv() {
        return naziv;
    }
    
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    
    public String getSkracenica() {
        return skracenica;
    }
    
    public void setSkracenica(String skracenica) {
        this.skracenica = skracenica;
    }
}
