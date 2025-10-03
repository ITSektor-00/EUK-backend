package com.sirus.backend.dto;

import com.sirus.backend.entity.EukPredmetFormularPodaci;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class EukPredmetFormularPodaciDto {
    
    private Integer podatakId;
    
    @NotNull(message = "Predmet je obavezan")
    private Integer predmetId;
    private String predmetNaziv;
    
    @NotNull(message = "Polje je obavezno")
    private Integer poljeId;
    private String poljeNaziv;
    private String poljeLabel;
    private String poljeTip;
    
    private String vrednost;
    private LocalDateTime datumUnosa;
    
    private Integer unioKorisnikId;
    private String unioKorisnikUsername;
    
    // Konstruktori
    public EukPredmetFormularPodaciDto() {}
    
    public EukPredmetFormularPodaciDto(EukPredmetFormularPodaci podaci) {
        this.podatakId = podaci.getPodatakId();
        this.predmetId = podaci.getPredmet() != null ? podaci.getPredmet().getPredmetId() : null;
        this.predmetNaziv = podaci.getPredmet() != null ? podaci.getPredmet().getNazivPredmeta() : null;
        this.poljeId = podaci.getPolje() != null ? podaci.getPolje().getPoljeId() : null;
        this.poljeNaziv = podaci.getPolje() != null ? podaci.getPolje().getNazivPolja() : null;
        this.poljeLabel = podaci.getPolje() != null ? podaci.getPolje().getLabel() : null;
        this.poljeTip = podaci.getPolje() != null ? podaci.getPolje().getTipPolja().getValue() : null;
        this.vrednost = podaci.getVrednost();
        this.datumUnosa = podaci.getDatumUnosa();
        this.unioKorisnikId = podaci.getUnioKorisnik() != null ? podaci.getUnioKorisnik().getId().intValue() : null;
        this.unioKorisnikUsername = podaci.getUnioKorisnik() != null ? podaci.getUnioKorisnik().getUsername() : null;
    }
    
    // Getters and Setters
    public Integer getPodatakId() {
        return podatakId;
    }
    
    public void setPodatakId(Integer podatakId) {
        this.podatakId = podatakId;
    }
    
    public Integer getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Integer predmetId) {
        this.predmetId = predmetId;
    }
    
    public String getPredmetNaziv() {
        return predmetNaziv;
    }
    
    public void setPredmetNaziv(String predmetNaziv) {
        this.predmetNaziv = predmetNaziv;
    }
    
    public Integer getPoljeId() {
        return poljeId;
    }
    
    public void setPoljeId(Integer poljeId) {
        this.poljeId = poljeId;
    }
    
    public String getPoljeNaziv() {
        return poljeNaziv;
    }
    
    public void setPoljeNaziv(String poljeNaziv) {
        this.poljeNaziv = poljeNaziv;
    }
    
    public String getPoljeLabel() {
        return poljeLabel;
    }
    
    public void setPoljeLabel(String poljeLabel) {
        this.poljeLabel = poljeLabel;
    }
    
    public String getPoljeTip() {
        return poljeTip;
    }
    
    public void setPoljeTip(String poljeTip) {
        this.poljeTip = poljeTip;
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
    
    public Integer getUnioKorisnikId() {
        return unioKorisnikId;
    }
    
    public void setUnioKorisnikId(Integer unioKorisnikId) {
        this.unioKorisnikId = unioKorisnikId;
    }
    
    public String getUnioKorisnikUsername() {
        return unioKorisnikUsername;
    }
    
    public void setUnioKorisnikUsername(String unioKorisnikUsername) {
        this.unioKorisnikUsername = unioKorisnikUsername;
    }
    
    @Override
    public String toString() {
        return "EukPredmetFormularPodaciDto{" +
                "podatakId=" + podatakId +
                ", predmetId=" + predmetId +
                ", predmetNaziv='" + predmetNaziv + '\'' +
                ", poljeId=" + poljeId +
                ", poljeNaziv='" + poljeNaziv + '\'' +
                ", vrednost='" + vrednost + '\'' +
                ", datumUnosa=" + datumUnosa +
                '}';
    }
}
