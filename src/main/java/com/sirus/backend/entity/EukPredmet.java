package com.sirus.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "predmet", schema = "EUK")
public class EukPredmet {
    
    public enum Status {
        АКТИВАН("активан"),
        ЗАТВОРЕН("затворен"),
        НА_ЧЕКАЊУ("на_чекању"),
        У_ОБРАДИ("у_обради");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static Status fromJson(String input) {
            if (input == null) {
                return null;
            }
            for (Status status : values()) {
                if (status.name().equalsIgnoreCase(input) || status.value.equalsIgnoreCase(input)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Непознат статус: " + input);
        }

        @JsonValue
        public String toJson() {
            return value;
        }
    }
    
    public enum Prioritet {
        НИЗАК("низак"),
        СРЕДЊИ("средњи"),
        ВИСОК("висок"),
        КРИТИЧАН("критичан");
        
        private final String value;
        
        Prioritet(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static Prioritet fromJson(String input) {
            if (input == null) {
                return null;
            }
            for (Prioritet p : values()) {
                if (p.name().equalsIgnoreCase(input) || p.value.equalsIgnoreCase(input)) {
                    return p;
                }
            }
            throw new IllegalArgumentException("Непознат приоритет: " + input);
        }

        @JsonValue
        public String toJson() {
            return value;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "predmet_id")
    private Integer predmetId;
    
    @Column(name = "datum_kreiranja", nullable = false)
    private LocalDate datumKreiranja;
    
    @Column(name = "naziv_predmeta", nullable = false, length = 255)
    private String nazivPredmeta;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private Status status;
    
    @Column(name = "odgovorna_osoba", nullable = false, length = 255)
    private String odgovornaOsoba;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prioritet", nullable = false, length = 20)
    private Prioritet prioritet;
    
    @Column(name = "rok_za_zavrsetak")
    private LocalDate rokZaZavrsetak;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kategorija_id", nullable = false)
    private EukKategorija kategorija;
    
    @OneToMany(mappedBy = "predmet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EukUgrozenoLice> ugrozenaLica;
    
    // Getters and Setters
    public Integer getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Integer predmetId) {
        this.predmetId = predmetId;
    }
    
    public LocalDate getDatumKreiranja() {
        return datumKreiranja;
    }
    
    public void setDatumKreiranja(LocalDate datumKreiranja) {
        this.datumKreiranja = datumKreiranja;
    }
    
    public String getNazivPredmeta() {
        return nazivPredmeta;
    }
    
    public void setNazivPredmeta(String nazivPredmeta) {
        this.nazivPredmeta = nazivPredmeta;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getOdgovornaOsoba() {
        return odgovornaOsoba;
    }
    
    public void setOdgovornaOsoba(String odgovornaOsoba) {
        this.odgovornaOsoba = odgovornaOsoba;
    }
    
    public Prioritet getPrioritet() {
        return prioritet;
    }
    
    public void setPrioritet(Prioritet prioritet) {
        this.prioritet = prioritet;
    }
    
    public LocalDate getRokZaZavrsetak() {
        return rokZaZavrsetak;
    }
    
    public void setRokZaZavrsetak(LocalDate rokZaZavrsetak) {
        this.rokZaZavrsetak = rokZaZavrsetak;
    }
    
    public EukKategorija getKategorija() {
        return kategorija;
    }
    
    public void setKategorija(EukKategorija kategorija) {
        this.kategorija = kategorija;
    }
    
    public List<EukUgrozenoLice> getUgrozenaLica() {
        return ugrozenaLica;
    }
    
    public void setUgrozenaLica(List<EukUgrozenoLice> ugrozenaLica) {
        this.ugrozenaLica = ugrozenaLica;
    }
}
