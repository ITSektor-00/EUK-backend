package com.sirus.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "formular_istorija", schema = "euk")
public class EukFormularIstorija {
    
    public enum Akcija {
        CREATED("created"),
        UPDATED("updated"),
        FIELD_ADDED("field_added"),
        FIELD_REMOVED("field_removed"),
        FIELD_UPDATED("field_updated"),
        ACTIVATED("activated"),
        DEACTIVATED("deactivated");
        
        private final String value;
        
        Akcija(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Akcija fromValue(String value) {
            for (Akcija akcija : values()) {
                if (akcija.value.equals(value)) {
                    return akcija;
                }
            }
            throw new IllegalArgumentException("Nepoznata akcija: " + value);
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "istorija_id")
    private Integer istorijaId;
    
    @NotNull(message = "Formular je obavezan")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formular_id", nullable = false)
    private EukFormular formular;
    
    @NotNull(message = "Akcija je obavezna")
    @Enumerated(EnumType.STRING)
    @Column(name = "akcija", nullable = false, length = 50)
    private Akcija akcija;
    
    @Size(max = 1000, message = "Opis ne može biti duži od 1000 karaktera")
    @Column(name = "opis", length = 1000)
    private String opis;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "korisnik_id")
    private User korisnik;
    
    @CreationTimestamp
    @Column(name = "datum", nullable = false, updatable = false)
    private LocalDateTime datum;
    
    @Column(name = "stara_vrednost", columnDefinition = "TEXT")
    private String staraVrednost;
    
    @Column(name = "nova_vrednost", columnDefinition = "TEXT")
    private String novaVrednost;
    
    // Konstruktori
    public EukFormularIstorija() {}
    
    public EukFormularIstorija(EukFormular formular, Akcija akcija, String opis, User korisnik) {
        this.formular = formular;
        this.akcija = akcija;
        this.opis = opis;
        this.korisnik = korisnik;
    }
    
    public EukFormularIstorija(EukFormular formular, Akcija akcija, String opis, User korisnik, String staraVrednost, String novaVrednost) {
        this.formular = formular;
        this.akcija = akcija;
        this.opis = opis;
        this.korisnik = korisnik;
        this.staraVrednost = staraVrednost;
        this.novaVrednost = novaVrednost;
    }
    
    // Getters and Setters
    public Integer getIstorijaId() {
        return istorijaId;
    }
    
    public void setIstorijaId(Integer istorijaId) {
        this.istorijaId = istorijaId;
    }
    
    public EukFormular getFormular() {
        return formular;
    }
    
    public void setFormular(EukFormular formular) {
        this.formular = formular;
    }
    
    public Akcija getAkcija() {
        return akcija;
    }
    
    public void setAkcija(Akcija akcija) {
        this.akcija = akcija;
    }
    
    public String getOpis() {
        return opis;
    }
    
    public void setOpis(String opis) {
        this.opis = opis;
    }
    
    public User getKorisnik() {
        return korisnik;
    }
    
    public void setKorisnik(User korisnik) {
        this.korisnik = korisnik;
    }
    
    public LocalDateTime getDatum() {
        return datum;
    }
    
    public void setDatum(LocalDateTime datum) {
        this.datum = datum;
    }
    
    public String getStaraVrednost() {
        return staraVrednost;
    }
    
    public void setStaraVrednost(String staraVrednost) {
        this.staraVrednost = staraVrednost;
    }
    
    public String getNovaVrednost() {
        return novaVrednost;
    }
    
    public void setNovaVrednost(String novaVrednost) {
        this.novaVrednost = novaVrednost;
    }
    
    @Override
    public String toString() {
        return "EukFormularIstorija{" +
                "istorijaId=" + istorijaId +
                ", formular=" + (formular != null ? formular.getFormularId() : null) +
                ", akcija=" + akcija +
                ", opis='" + opis + '\'' +
                ", korisnik=" + (korisnik != null ? korisnik.getUsername() : null) +
                ", datum=" + datum +
                '}';
    }
}
