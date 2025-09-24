package com.sirus.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ugrozeno_lice_t1", schema = "euk")
public class EukUgrozenoLiceT1 {
    
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
    
    // Informacije o domaćinstvu
    @Min(value = 1, message = "Broj članova domaćinstva mora biti veći od 0")
    @Max(value = 20, message = "Broj članova domaćinstva ne može biti veći od 20")
    @Column(name = "broj_clanova_domacinstva")
    private Integer brojClanovaDomacinstva;
    
    // Energetski status
    @Size(max = 50, message = "Osnov sticanja statusa ne može biti duži od 50 karaktera")
    @Column(name = "osnov_sticanja_statusa", length = 50)
    private String osnovSticanjaStatusa; // MP, NSP, DD, UDTNP
    
    @Size(max = 100, message = "ED broj/broj mernog uređaja ne može biti duži od 100 karaktera")
    @Column(name = "ed_broj_broj_mernog_uredjaja", length = 100)
    private String edBrojBrojMernogUredjaja;
    
    // Energetski podaci - kombinovana kolona
    @Size(max = 200, message = "Kombinovana kolona potrošnje i površine ne može biti duža od 200 karaktera")
    @Column(name = "potrosnja_i_povrsina_combined", length = 200)
    private String potrosnjaIPovrsinaCombined;
    
    // Finansijski podaci
    @DecimalMin(value = "0.0", message = "Iznos umanjenja ne može biti negativan")
    @Digits(integer = 10, fraction = 2, message = "Iznos umanjenja mora imati najviše 10 cifara pre decimalne tačke i 2 posle")
    @Column(name = "iznos_umanjenja_sa_pdv", precision = 12, scale = 2)
    private BigDecimal iznosUmanjenjaSaPdv;
    
    @Size(max = 50, message = "Broj računa ne može biti duži od 50 karaktera")
    @Column(name = "broj_racuna", length = 50)
    private String brojRacuna;
    
    @Column(name = "datum_izdavanja_racuna")
    private LocalDate datumIzdavanjaRacuna;

    @Column(name = "datum_trajanja_prava")
    private LocalDate datumTrajanjaPrava;
    
    // Audit kolone
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Konstruktori
    public EukUgrozenoLiceT1() {}
    
    // Getters and Setters
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
    
    public Integer getBrojClanovaDomacinstva() {
        return brojClanovaDomacinstva;
    }
    
    public void setBrojClanovaDomacinstva(Integer brojClanovaDomacinstva) {
        this.brojClanovaDomacinstva = brojClanovaDomacinstva;
    }
    
    public String getOsnovSticanjaStatusa() {
        return osnovSticanjaStatusa;
    }
    
    public void setOsnovSticanjaStatusa(String osnovSticanjaStatusa) {
        this.osnovSticanjaStatusa = osnovSticanjaStatusa;
    }
    
    public String getEdBrojBrojMernogUredjaja() {
        return edBrojBrojMernogUredjaja;
    }
    
    public void setEdBrojBrojMernogUredjaja(String edBrojBrojMernogUredjaja) {
        this.edBrojBrojMernogUredjaja = edBrojBrojMernogUredjaja;
    }
    
    public String getPotrosnjaIPovrsinaCombined() {
        return potrosnjaIPovrsinaCombined;
    }
    
    public void setPotrosnjaIPovrsinaCombined(String potrosnjaIPovrsinaCombined) {
        this.potrosnjaIPovrsinaCombined = potrosnjaIPovrsinaCombined;
    }
    
    // Helper methods for backward compatibility and data extraction
    public BigDecimal getPotrosnjaKwh() {
        if (potrosnjaIPovrsinaCombined == null) return null;
        try {
            String[] parts = potrosnjaIPovrsinaCombined.split("/");
            if (parts.length >= 2 && !parts[1].isEmpty()) {
                return new BigDecimal(parts[1]);
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return null;
    }
    
    public void setPotrosnjaKwh(BigDecimal potrosnjaKwh) {
        updateCombinedField(potrosnjaKwh, getZagrevanaPovrsinaM2());
    }
    
    public BigDecimal getZagrevanaPovrsinaM2() {
        if (potrosnjaIPovrsinaCombined == null) return null;
        try {
            String[] parts = potrosnjaIPovrsinaCombined.split("/");
            if (parts.length >= 4 && !parts[3].isEmpty()) {
                return new BigDecimal(parts[3]);
            }
        } catch (Exception e) {
            // Log error if needed
        }
        return null;
    }
    
    public void setZagrevanaPovrsinaM2(BigDecimal zagrevanaPovrsinaM2) {
        updateCombinedField(getPotrosnjaKwh(), zagrevanaPovrsinaM2);
    }
    
    private void updateCombinedField(BigDecimal potrosnjaKwh, BigDecimal zagrevanaPovrsinaM2) {
        StringBuilder combined = new StringBuilder("Потрошња у kWh/");
        combined.append(potrosnjaKwh != null ? potrosnjaKwh.toString() : "");
        combined.append("/загревана површина у m2/");
        combined.append(zagrevanaPovrsinaM2 != null ? zagrevanaPovrsinaM2.toString() : "");
        this.potrosnjaIPovrsinaCombined = combined.toString();
    }
    
    public BigDecimal getIznosUmanjenjaSaPdv() {
        return iznosUmanjenjaSaPdv;
    }
    
    public void setIznosUmanjenjaSaPdv(BigDecimal iznosUmanjenjaSaPdv) {
        this.iznosUmanjenjaSaPdv = iznosUmanjenjaSaPdv;
    }
    
    public String getBrojRacuna() {
        return brojRacuna;
    }
    
    public void setBrojRacuna(String brojRacuna) {
        this.brojRacuna = brojRacuna;
    }
    
    public LocalDate getDatumIzdavanjaRacuna() {
        return datumIzdavanjaRacuna;
    }
    
    public void setDatumIzdavanjaRacuna(LocalDate datumIzdavanjaRacuna) {
        this.datumIzdavanjaRacuna = datumIzdavanjaRacuna;
    }

    public LocalDate getDatumTrajanjaPrava() {
        return datumTrajanjaPrava;
    }
    
    public void setDatumTrajanjaPrava(LocalDate datumTrajanjaPrava) {
        this.datumTrajanjaPrava = datumTrajanjaPrava;
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
    
    @Override
    public String toString() {
        return "EukUgrozenoLiceT1{" +
                "ugrozenoLiceId=" + ugrozenoLiceId +
                ", redniBroj='" + redniBroj + '\'' +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", jmbg='" + jmbg + '\'' +
                ", pttBroj='" + pttBroj + '\'' +
                ", gradOpstina='" + gradOpstina + '\'' +
                ", mesto='" + mesto + '\'' +
                ", ulicaIBroj='" + ulicaIBroj + '\'' +
                ", brojClanovaDomacinstva=" + brojClanovaDomacinstva +
                ", osnovSticanjaStatusa='" + osnovSticanjaStatusa + '\'' +
                ", edBrojBrojMernogUredjaja='" + edBrojBrojMernogUredjaja + '\'' +
                ", potrosnjaIPovrsinaCombined='" + potrosnjaIPovrsinaCombined + '\'' +
                ", iznosUmanjenjaSaPdv=" + iznosUmanjenjaSaPdv +
                ", brojRacuna='" + brojRacuna + '\'' +
                ", datumIzdavanjaRacuna=" + datumIzdavanjaRacuna +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
