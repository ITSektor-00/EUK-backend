package com.sirus.backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kategorija", schema = "euk")
public class EukKategorija {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kategorija_id")
    private Integer kategorijaId;
    
    @Column(name = "naziv", nullable = false, length = 255)
    private String naziv;
    
    @Column(name = "skracenica", nullable = false, length = 10)
    private String skracenica;
    
    @OneToMany(mappedBy = "kategorija", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EukPredmet> predmeti;
    
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
    
    public List<EukPredmet> getPredmeti() {
        return predmeti;
    }
    
    public void setPredmeti(List<EukPredmet> predmeti) {
        this.predmeti = predmeti;
    }
}
