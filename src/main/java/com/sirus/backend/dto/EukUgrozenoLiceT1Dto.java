package com.sirus.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EukUgrozenoLiceT1Dto {
    
    private Integer ugrozenoLiceId;
    
    // Osnovne informacije o licu
    private String redniBroj;
    private String ime;
    private String prezime;
    private String jmbg;
    
    // Adresne informacije
    private String pttBroj;
    
    private String gradOpstina;
    private String mesto;
    private String ulicaIBroj;
    
    // Informacije o domaćinstvu
    private Integer brojClanovaDomacinstva;
    
    // Energetski status
    private String osnovSticanjaStatusa; // MP, NSP, DD, UDTNP
    private String edBrojBrojMernogUredjaja;
    
    // Energetski podaci - kombinovana kolona
    private String potrosnjaIPovrsinaCombined;
    
    // Energetski podaci - za backward compatibility
    private BigDecimal potrosnjaKwh;
    private BigDecimal zagrevanaPovrsinaM2;
    
    // Finansijski podaci
    private BigDecimal iznosUmanjenjaSaPdv;
    private String brojRacuna;
    
    private LocalDate datumIzdavanjaRacuna;
    
    private LocalDate datumTrajanjaPrava;
    
    // Audit kolone
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Konstruktori
    public EukUgrozenoLiceT1Dto() {}
    
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
    
    public BigDecimal getPotrosnjaKwh() {
        return potrosnjaKwh;
    }
    
    public void setPotrosnjaKwh(BigDecimal potrosnjaKwh) {
        this.potrosnjaKwh = potrosnjaKwh;
    }
    
    public BigDecimal getZagrevanaPovrsinaM2() {
        return zagrevanaPovrsinaM2;
    }
    
    public void setZagrevanaPovrsinaM2(BigDecimal zagrevanaPovrsinaM2) {
        this.zagrevanaPovrsinaM2 = zagrevanaPovrsinaM2;
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
        return "EukUgrozenoLiceT1Dto{" +
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
                ", potrosnjaKwh=" + potrosnjaKwh +
                ", zagrevanaPovrsinaM2=" + zagrevanaPovrsinaM2 +
                ", iznosUmanjenjaSaPdv=" + iznosUmanjenjaSaPdv +
                ", brojRacuna='" + brojRacuna + '\'' +
                ", datumIzdavanjaRacuna=" + datumIzdavanjaRacuna +
                ", datumTrajanjaPrava=" + datumTrajanjaPrava +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
