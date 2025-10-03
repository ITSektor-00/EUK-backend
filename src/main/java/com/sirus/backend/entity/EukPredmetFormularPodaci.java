package com.sirus.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "predmet_formular_podaci", schema = "euk")
public class EukPredmetFormularPodaci {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "podatak_id")
    private Integer podatakId;
    
    @NotNull(message = "Predmet je obavezan")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predmet_id", nullable = false)
    private EukPredmet predmet;
    
    @NotNull(message = "Polje je obavezno")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polje_id", nullable = false)
    private EukFormularPolje polje;
    
    @Column(name = "vrednost", columnDefinition = "TEXT")
    private String vrednost;
    
    @CreationTimestamp
    @Column(name = "datum_unosa", nullable = false, updatable = false)
    private LocalDateTime datumUnosa;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unio_korisnik")
    private User unioKorisnik;
    
    // Konstruktori
    public EukPredmetFormularPodaci() {}
    
    public EukPredmetFormularPodaci(EukPredmet predmet, EukFormularPolje polje, String vrednost) {
        this.predmet = predmet;
        this.polje = polje;
        this.vrednost = vrednost;
    }
    
    // Getters and Setters
    public Integer getPodatakId() {
        return podatakId;
    }
    
    public void setPodatakId(Integer podatakId) {
        this.podatakId = podatakId;
    }
    
    public EukPredmet getPredmet() {
        return predmet;
    }
    
    public void setPredmet(EukPredmet predmet) {
        this.predmet = predmet;
    }
    
    public EukFormularPolje getPolje() {
        return polje;
    }
    
    public void setPolje(EukFormularPolje polje) {
        this.polje = polje;
    }
    
    public String getVrednost() {
        return vrednost;
    }
    
    public void setVrednost(String vrednost) {
        this.vrednost = vrednost;
    }
    
    public LocalDateTime getDatumUnosa() {
        return datumUnosa;
    }
    
    public void setDatumUnosa(LocalDateTime datumUnosa) {
        this.datumUnosa = datumUnosa;
    }
    
    public User getUnioKorisnik() {
        return unioKorisnik;
    }
    
    public void setUnioKorisnik(User unioKorisnik) {
        this.unioKorisnik = unioKorisnik;
    }
    
    @Override
    public String toString() {
        return "EukPredmetFormularPodaci{" +
                "podatakId=" + podatakId +
                ", predmet=" + (predmet != null ? predmet.getPredmetId() : null) +
                ", polje=" + (polje != null ? polje.getPoljeId() : null) +
                ", vrednost='" + vrednost + '\'' +
                ", datumUnosa=" + datumUnosa +
                '}';
    }
}
