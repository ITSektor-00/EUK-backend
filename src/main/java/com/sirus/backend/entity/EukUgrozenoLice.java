package com.sirus.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ugrozeno_lice", schema = "EUK")
public class EukUgrozenoLice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ugrozeno_lice_id")
    private Integer ugrozenoLiceId;
    
    @Column(name = "ime", nullable = false, length = 100)
    private String ime;
    
    @Column(name = "prezime", nullable = false, length = 100)
    private String prezime;
    
    @Column(name = "jmbg", nullable = false, unique = true, columnDefinition = "CHAR(13)")
    private String jmbg;
    
    @Column(name = "datum_rodjenja", nullable = false)
    private LocalDate datumRodjenja;
    
    @Column(name = "drzava_rodjenja", nullable = false, length = 100)
    private String drzavaRodjenja;
    
    @Column(name = "mesto_rodjenja", nullable = false, length = 100)
    private String mestoRodjenja;
    
    @Column(name = "opstina_rodjenja", nullable = false, length = 100)
    private String opstinaRodjenja;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predmet_id", nullable = false)
    private EukPredmet predmet;
    

    
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
    
    public EukPredmet getPredmet() {
        return predmet;
    }
    
    public void setPredmet(EukPredmet predmet) {
        this.predmet = predmet;
    }
}
