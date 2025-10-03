package com.sirus.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "formular", schema = "euk")
public class EukFormular {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "formular_id")
    private Integer formularId;
    
    @NotBlank(message = "Naziv formulare je obavezan")
    @Size(max = 255, message = "Naziv formulare ne može biti duži od 255 karaktera")
    @Column(name = "naziv", nullable = false, length = 255)
    private String naziv;
    
    @Size(max = 1000, message = "Opis formulare ne može biti duži od 1000 karaktera")
    @Column(name = "opis", length = 1000)
    private String opis;
    
    @NotNull(message = "Kategorija je obavezna")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kategorija_id", nullable = false)
    private EukKategorija kategorija;
    
    @CreationTimestamp
    @Column(name = "datum_kreiranja", nullable = false, updatable = false)
    private LocalDateTime datumKreiranja;
    
    @UpdateTimestamp
    @Column(name = "datum_azuriranja")
    private LocalDateTime datumAzuriranja;
    
    @Column(name = "aktivna", nullable = false)
    private Boolean aktivna = true;
    
    @Column(name = "verzija", nullable = false)
    private Integer verzija = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;
    
    // Veza sa poljima formulare
    @OneToMany(mappedBy = "formular", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("redosled ASC")
    private List<EukFormularPolje> polja = new ArrayList<>();
    
    // Veza sa istorijom izmena
    @OneToMany(mappedBy = "formular", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("datum DESC")
    private List<EukFormularIstorija> istorija = new ArrayList<>();
    
    // Konstruktori
    public EukFormular() {}
    
    public EukFormular(String naziv, String opis, EukKategorija kategorija) {
        this.naziv = naziv;
        this.opis = opis;
        this.kategorija = kategorija;
    }
    
    // Getters and Setters
    public Integer getFormularId() {
        return formularId;
    }
    
    public void setFormularId(Integer formularId) {
        this.formularId = formularId;
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
    
    public EukKategorija getKategorija() {
        return kategorija;
    }
    
    public void setKategorija(EukKategorija kategorija) {
        this.kategorija = kategorija;
    }
    
    public LocalDateTime getDatumKreiranja() {
        return datumKreiranja;
    }
    
    public void setDatumKreiranja(LocalDateTime datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
    
    public LocalDateTime getDatumAzuriranja() {
        return datumAzuriranja;
    }
    
    public void setDatumAzuriranja(LocalDateTime datumAzuriranja) {
        this.datumAzuriranja = datumAzuriranja;
    }
    
    public Boolean getAktivna() {
        return aktivna;
    }
    
    public void setAktivna(Boolean aktivna) {
        this.aktivna = aktivna;
    }
    
    public Integer getVerzija() {
        return verzija;
    }
    
    public void setVerzija(Integer verzija) {
        this.verzija = verzija;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public User getUpdatedBy() {
        return updatedBy;
    }
    
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }
    
    public List<EukFormularPolje> getPolja() {
        return polja;
    }
    
    public void setPolja(List<EukFormularPolje> polja) {
        this.polja = polja;
    }
    
    public List<EukFormularIstorija> getIstorija() {
        return istorija;
    }
    
    public void setIstorija(List<EukFormularIstorija> istorija) {
        this.istorija = istorija;
    }
    
    // Helper metode
    public void addPolje(EukFormularPolje polje) {
        polja.add(polje);
        polje.setFormular(this);
    }
    
    public void removePolje(EukFormularPolje polje) {
        polja.remove(polje);
        polje.setFormular(null);
    }
    
    public void incrementVerzija() {
        this.verzija++;
    }
    
    @Override
    public String toString() {
        return "EukFormular{" +
                "formularId=" + formularId +
                ", naziv='" + naziv + '\'' +
                ", opis='" + opis + '\'' +
                ", kategorija=" + (kategorija != null ? kategorija.getNaziv() : null) +
                ", datumKreiranja=" + datumKreiranja +
                ", aktivna=" + aktivna +
                ", verzija=" + verzija +
                '}';
    }
}
