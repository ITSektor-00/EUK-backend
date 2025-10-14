package com.sirus.backend.entity;

import jakarta.persistence.*;
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
    @Column(name = "redni_broj", length = 20, columnDefinition = "VARCHAR(20)")
    private String redniBroj;
    
    @Column(name = "ime", length = 100, columnDefinition = "VARCHAR(100)")
    private String ime;
    
    @Column(name = "prezime", length = 100, columnDefinition = "VARCHAR(100)")
    private String prezime;
    
    @Column(name = "jmbg", length = 50, columnDefinition = "VARCHAR(50)")
    private String jmbg;
    
    // Adresne informacije
    @Column(name = "ptt_broj", length = 10, columnDefinition = "VARCHAR(10)")
    private String pttBroj;
    
    @Column(name = "grad_opstina", length = 100, columnDefinition = "VARCHAR(100)")
    private String gradOpstina;
    
    @Column(name = "mesto", length = 100, columnDefinition = "VARCHAR(100)")
    private String mesto;
    
    @Column(name = "ulica_i_broj", length = 200, columnDefinition = "VARCHAR(200)")
    private String ulicaIBroj;
    
    // Informacije o domaćinstvu
    @Column(name = "broj_clanova_domacinstva")
    private Integer brojClanovaDomacinstva;
    
    // Energetski status - povezano sa kategorija.skracenica
    @Column(name = "osnov_sticanja_statusa", length = 10, columnDefinition = "VARCHAR(10)")
    private String osnovSticanjaStatusa; // Povezano sa kategorija.skracenica
    
    @Column(name = "ed_broj_broj_mernog_uredjaja", length = 100, columnDefinition = "VARCHAR(100)")
    private String edBrojBrojMernogUredjaja;
    
    // Energetski podaci - kombinovana kolona
    @Column(name = "potrosnja_i_povrsina_combined", length = 200, columnDefinition = "VARCHAR(200)")
    private String potrosnjaIPovrsinaCombined;
    
    // Finansijski podaci
    @Column(name = "iznos_umanjenja_sa_pdv", precision = 12, scale = 2)
    private BigDecimal iznosUmanjenjaSaPdv;
    
    @Column(name = "broj_racuna", length = 50, columnDefinition = "VARCHAR(50)")
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
