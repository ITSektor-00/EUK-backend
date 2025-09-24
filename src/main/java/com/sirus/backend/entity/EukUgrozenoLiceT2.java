package com.sirus.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ugrozeno_lice_t2", schema = "euk")
public class EukUgrozenoLiceT2 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ugrozeno_lice_id")
    private Integer ugrozenoLiceId;
    
    // Osnovne informacije o licu
    @NotBlank(message = "Redni broj je obavezan")
    @Size(max = 20, message = "Redni broj ne može biti duži od 20 karaktera")
    @Column(name = "redni_broj", nullable = false, length = 20)
    private String redniBroj;
    
    @NotBlank(message = "Ime je obavezno")
    @Size(max = 100, message = "Ime ne može biti duže od 100 karaktera")
    @Column(name = "ime", nullable = false, length = 100)
    private String ime;
    
    @NotBlank(message = "Prezime je obavezno")
    @Size(max = 100, message = "Prezime ne može biti duže od 100 karaktera")
    @Column(name = "prezime", nullable = false, length = 100)
    private String prezime;
    
    @NotBlank(message = "JMBG je obavezan")
    @Pattern(regexp = "^\\d{13}$", message = "JMBG mora sadržati tačno 13 cifara")
    @Column(name = "jmbg", nullable = false, unique = true, columnDefinition = "CHAR(13)")
    private String jmbg;
    
    // Adresne informacije
    @Size(max = 10, message = "PTT broj ne može biti duži od 10 karaktera")
    @Column(name = "ptt_broj", length = 10)
    private String pttBroj;
    
    @Size(max = 100, message = "Grad/Opština ne može biti duži od 100 karaktera")
    @Column(name = "grad_opstina", length = 100)
    private String gradOpstina;
    
    @Size(max = 100, message = "Mesto ne može biti duže od 100 karaktera")
    @Column(name = "mesto", length = 100)
    private String mesto;
    
    @Size(max = 200, message = "Ulica i broj ne može biti duži od 200 karaktera")
    @Column(name = "ulica_i_broj", length = 200)
    private String ulicaIBroj;
    
    // Energetski podaci
    @Size(max = 100, message = "ED broj ne može biti duži od 100 karaktera")
    @Column(name = "ed_broj", length = 100)
    private String edBroj;
    
    // Informacije o važenju rešenja
    @Size(max = 200, message = "Period važenja rešenja o statusu ne može biti duži od 200 karaktera")
    @Column(name = "pok_vazenja_resenja_o_statusu", length = 200)
    private String pokVazenjaResenjaOStatusu;
    
    // Audit kolone
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Konstruktori
    public EukUgrozenoLiceT2() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public EukUgrozenoLiceT2(String redniBroj, String ime, String prezime, String jmbg) {
        this();
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
        return "EukUgrozenoLiceT2{" +
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
    
    // equals i hashCode metode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        EukUgrozenoLiceT2 that = (EukUgrozenoLiceT2) o;
        
        return ugrozenoLiceId != null ? ugrozenoLiceId.equals(that.ugrozenoLiceId) : that.ugrozenoLiceId == null;
    }
    
    @Override
    public int hashCode() {
        return ugrozenoLiceId != null ? ugrozenoLiceId.hashCode() : 0;
    }
}
