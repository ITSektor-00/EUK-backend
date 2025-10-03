package com.sirus.backend.dto;

import com.sirus.backend.entity.EukFormular;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EukFormularDto {
    
    private Integer formularId;
    
    @NotBlank(message = "Naziv formulare je obavezan")
    @Size(max = 255, message = "Naziv formulare ne može biti duži od 255 karaktera")
    private String naziv;
    
    @Size(max = 1000, message = "Opis formulare ne može biti duži od 1000 karaktera")
    private String opis;
    
    @NotNull(message = "Kategorija je obavezna")
    private Integer kategorijaId;
    
    private String kategorijaNaziv;
    private String kategorijaSkracenica;
    
    private LocalDateTime datumKreiranja;
    private LocalDateTime datumAzuriranja;
    private Boolean aktivna;
    private Integer verzija;
    
    private Integer createdById;
    private String createdByUsername;
    private Integer updatedById;
    private String updatedByUsername;
    
    private List<EukFormularPoljeDto> polja = new ArrayList<>();
    private Integer brojPolja;
    
    // Konstruktori
    public EukFormularDto() {}
    
    public EukFormularDto(EukFormular formular) {
        this.formularId = formular.getFormularId();
        this.naziv = formular.getNaziv();
        this.opis = formular.getOpis();
        this.kategorijaId = formular.getKategorija() != null ? formular.getKategorija().getKategorijaId() : null;
        this.kategorijaNaziv = formular.getKategorija() != null ? formular.getKategorija().getNaziv() : null;
        this.kategorijaSkracenica = formular.getKategorija() != null ? formular.getKategorija().getSkracenica() : null;
        this.datumKreiranja = formular.getDatumKreiranja();
        this.datumAzuriranja = formular.getDatumAzuriranja();
        this.aktivna = formular.getAktivna();
        this.verzija = formular.getVerzija();
        this.createdById = formular.getCreatedBy() != null ? formular.getCreatedBy().getId().intValue() : null;
        this.createdByUsername = formular.getCreatedBy() != null ? formular.getCreatedBy().getUsername() : null;
        this.updatedById = formular.getUpdatedBy() != null ? formular.getUpdatedBy().getId().intValue() : null;
        this.updatedByUsername = formular.getUpdatedBy() != null ? formular.getUpdatedBy().getUsername() : null;
        this.brojPolja = formular.getPolja() != null ? formular.getPolja().size() : 0;
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
    
    public Integer getKategorijaId() {
        return kategorijaId;
    }
    
    public void setKategorijaId(Integer kategorijaId) {
        this.kategorijaId = kategorijaId;
    }
    
    public String getKategorijaNaziv() {
        return kategorijaNaziv;
    }
    
    public void setKategorijaNaziv(String kategorijaNaziv) {
        this.kategorijaNaziv = kategorijaNaziv;
    }
    
    public String getKategorijaSkracenica() {
        return kategorijaSkracenica;
    }
    
    public void setKategorijaSkracenica(String kategorijaSkracenica) {
        this.kategorijaSkracenica = kategorijaSkracenica;
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
    
    public Integer getCreatedById() {
        return createdById;
    }
    
    public void setCreatedById(Integer createdById) {
        this.createdById = createdById;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
    
    public Integer getUpdatedById() {
        return updatedById;
    }
    
    public void setUpdatedById(Integer updatedById) {
        this.updatedById = updatedById;
    }
    
    public String getUpdatedByUsername() {
        return updatedByUsername;
    }
    
    public void setUpdatedByUsername(String updatedByUsername) {
        this.updatedByUsername = updatedByUsername;
    }
    
    public List<EukFormularPoljeDto> getPolja() {
        return polja;
    }
    
    public void setPolja(List<EukFormularPoljeDto> polja) {
        this.polja = polja;
    }
    
    public Integer getBrojPolja() {
        return brojPolja;
    }
    
    public void setBrojPolja(Integer brojPolja) {
        this.brojPolja = brojPolja;
    }
    
    @Override
    public String toString() {
        return "EukFormularDto{" +
                "formularId=" + formularId +
                ", naziv='" + naziv + '\'' +
                ", opis='" + opis + '\'' +
                ", kategorijaId=" + kategorijaId +
                ", kategorijaNaziv='" + kategorijaNaziv + '\'' +
                ", aktivna=" + aktivna +
                ", verzija=" + verzija +
                ", brojPolja=" + brojPolja +
                '}';
    }
}
