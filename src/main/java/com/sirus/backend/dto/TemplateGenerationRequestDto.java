package com.sirus.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

public class TemplateGenerationRequestDto {
    
    @NotNull(message = "Lice ID je obavezno")
    private Long liceId;
    
    @NotNull(message = "Tip lice je obavezan (t1 ili t2)")
    private String liceTip;
    
    @NotNull(message = "Kategorija ID je obavezna")
    private Long kategorijaId;
    
    @NotNull(message = "Obrasci vrste ID je obavezan")
    private Long obrasciVrsteId;
    
    @NotNull(message = "Organizaciona struktura ID je obavezna")
    private Long organizacionaStrukturaId;
    
    @NotNull(message = "Predmet ID je obavezan")
    private Long predmetId;
    
    @Valid
    private ManualDataDto manualData;
    
    // Constructors
    public TemplateGenerationRequestDto() {}
    
    public TemplateGenerationRequestDto(Long liceId, String liceTip, Long kategorijaId, 
                                      Long obrasciVrsteId, Long organizacionaStrukturaId, Long predmetId, ManualDataDto manualData) {
        this.liceId = liceId;
        this.liceTip = liceTip;
        this.kategorijaId = kategorijaId;
        this.obrasciVrsteId = obrasciVrsteId;
        this.organizacionaStrukturaId = organizacionaStrukturaId;
        this.predmetId = predmetId;
        this.manualData = manualData;
    }
    
    // Getters and Setters
    public Long getLiceId() {
        return liceId;
    }
    
    public void setLiceId(Long liceId) {
        this.liceId = liceId;
    }
    
    public String getLiceTip() {
        return liceTip;
    }
    
    public void setLiceTip(String liceTip) {
        this.liceTip = liceTip;
    }
    
    public Long getKategorijaId() {
        return kategorijaId;
    }
    
    public void setKategorijaId(Long kategorijaId) {
        this.kategorijaId = kategorijaId;
    }
    
    public Long getObrasciVrsteId() {
        return obrasciVrsteId;
    }
    
    public void setObrasciVrsteId(Long obrasciVrsteId) {
        this.obrasciVrsteId = obrasciVrsteId;
    }
    
    public Long getOrganizacionaStrukturaId() {
        return organizacionaStrukturaId;
    }
    
    public void setOrganizacionaStrukturaId(Long organizacionaStrukturaId) {
        this.organizacionaStrukturaId = organizacionaStrukturaId;
    }
    
    public Long getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Long predmetId) {
        this.predmetId = predmetId;
    }
    
    public ManualDataDto getManualData() {
        return manualData;
    }
    
    public void setManualData(ManualDataDto manualData) {
        this.manualData = manualData;
    }
}
