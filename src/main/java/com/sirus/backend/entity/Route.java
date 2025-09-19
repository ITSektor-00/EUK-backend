package com.sirus.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rute")
public class Route {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ruta", nullable = false, unique = true)
    private String ruta;
    
    @Column(name = "naziv", nullable = false)
    private String naziv;
    
    @Column(name = "opis", columnDefinition = "TEXT")
    private String opis;
    
    @Column(name = "sekcija", length = 100)
    private String sekcija;
    
    @Column(name = "aktivna")
    private Boolean aktivna = true;
    
    @Column(name = "datum_kreiranja")
    private LocalDateTime datumKreiranja;
    
    // Constructors
    public Route() {
        this.datumKreiranja = LocalDateTime.now();
    }
    
    public Route(String ruta, String naziv, String opis, String sekcija) {
        this();
        this.ruta = ruta;
        this.naziv = naziv;
        this.opis = opis;
        this.sekcija = sekcija;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRuta() {
        return ruta;
    }
    
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    
    public String getNaziv() {
        return naziv;
    }
    
    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }
    
    public String getOpis() {
        return opis;
    }
    
    public void setOpis(String opis) {
        this.opis = opis;
    }
    
    public String getSekcija() {
        return sekcija;
    }
    
    public void setSekcija(String sekcija) {
        this.sekcija = sekcija;
    }
    
    
    public Boolean getAktivna() {
        return aktivna;
    }
    
    public void setAktivna(Boolean aktivna) {
        this.aktivna = aktivna;
    }
    
    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }
    
    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
}