package com.sirus.backend.dto;

import com.sirus.backend.entity.EukPredmet;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class EukPredmetDto {
    
    private Integer predmetId;
    private LocalDate datumKreiranja;
    
    @NotBlank(message = "Naziv predmeta je obavezan")
    @Size(max = 255, message = "Naziv predmeta ne može biti duži od 255 karaktera")
    private String nazivPredmeta;
    
    @NotNull(message = "Status predmeta je obavezan")
    private EukPredmet.Status status;
    
    @NotBlank(message = "Odgovorna osoba je obavezna")
    @Size(max = 255, message = "Odgovorna osoba ne može biti duža od 255 karaktera")
    private String odgovornaOsoba;
    
    @NotNull(message = "Prioritet predmeta je obavezan")
    private EukPredmet.Prioritet prioritet;
    
    private LocalDate rokZaZavrsetak;
    
    @NotNull(message = "Kategorija je obavezna")
    private Integer kategorijaId;
    
    private String kategorijaNaziv;
    private String kategorijaSkracenica;
    private Integer brojUgrozenihLica;
    
    // Getters and Setters
    public Integer getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Integer predmetId) {
        this.predmetId = predmetId;
    }
    
    public LocalDate getDatumKreiranja() {
        return datumKreiranja;
    }
    
    public void setDatumKreiranja(LocalDate datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
    
    public String getNazivPredmeta() {
        return nazivPredmeta;
    }
    
    public void setNazivPredmeta(String nazivPredmeta) {
        this.nazivPredmeta = nazivPredmeta;
    }
    
    public EukPredmet.Status getStatus() {
        return status;
    }
    
    public void setStatus(EukPredmet.Status status) {
        this.status = status;
    }
    
    public String getOdgovornaOsoba() {
        return odgovornaOsoba;
    }
    
    public void setOdgovornaOsoba(String odgovornaOsoba) {
        this.odgovornaOsoba = odgovornaOsoba;
    }
    
    public EukPredmet.Prioritet getPrioritet() {
        return prioritet;
    }
    
    public void setPrioritet(EukPredmet.Prioritet prioritet) {
        this.prioritet = prioritet;
    }
    
    public LocalDate getRokZaZavrsetak() {
        return rokZaZavrsetak;
    }
    
    public void setRokZaZavrsetak(LocalDate rokZaZavrsetak) {
        this.rokZaZavrsetak = rokZaZavrsetak;
    }
    
    public Integer getKategorijaId() {
        return kategorijaId;
    }
    
    public void setKategorijaId(Integer kategorijaId) {
        this.kategorijaId = kategorijaId;
    }
    
    public String getKategorijaNaziv() {
        return kategorijaNaziv;
    }
    
    public void setKategorijaNaziv(String kategorijaNaziv) {
        this.kategorijaNaziv = kategorijaNaziv;
    }
    
    public String getKategorijaSkracenica() {
        return kategorijaSkracenica;
    }
    
    public void setKategorijaSkracenica(String kategorijaSkracenica) {
        this.kategorijaSkracenica = kategorijaSkracenica;
    }
    
    public Integer getBrojUgrozenihLica() {
        return brojUgrozenihLica;
    }
    
    public void setBrojUgrozenihLica(Integer brojUgrozenihLica) {
        this.brojUgrozenihLica = brojUgrozenihLica;
    }
}
