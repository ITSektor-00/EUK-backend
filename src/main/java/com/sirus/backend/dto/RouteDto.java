package com.sirus.backend.dto;

import java.time.LocalDateTime;

public class RouteDto {
    
    private Long id;
    private String ruta;
    private String naziv;
    private String opis;
    private String sekcija;
    private Boolean aktivna;
    private LocalDateTime datumKreiranja;
    
    // Constructors
    public RouteDto() {}
    
    public RouteDto(Long id, String ruta, String naziv, String opis, String sekcija, 
                   Boolean aktivna, LocalDateTime datumKreiranja) {
        this.id = id;
        this.ruta = ruta;
        this.naziv = naziv;
        this.opis = opis;
        this.sekcija = sekcija;
        this.aktivna = aktivna;
        this.datumKreiranja = datumKreiranja;
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