package com.sirus.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EnvelopeBackSidePdfRequest {
    
    @NotNull(message = "Template je obavezan")
    private String template;
    
    @NotEmpty(message = "Lista ugroženih lica ne može biti prazna")
    private List<UgrozenoLiceDto> ugrozenaLica;
    
    @NotNull(message = "Naziv predmeta je obavezan")
    private String nazivPredmeta;
    
    public EnvelopeBackSidePdfRequest() {}
    
    public EnvelopeBackSidePdfRequest(String template, List<UgrozenoLiceDto> ugrozenaLica, String nazivPredmeta) {
        this.template = template;
        this.ugrozenaLica = ugrozenaLica;
        this.nazivPredmeta = nazivPredmeta;
    }
    
    public String getTemplate() {
        return template;
    }
    
    public void setTemplate(String template) {
        this.template = template;
    }
    
    public List<UgrozenoLiceDto> getUgrozenaLica() {
        return ugrozenaLica;
    }
    
    public void setUgrozenaLica(List<UgrozenoLiceDto> ugrozenaLica) {
        this.ugrozenaLica = ugrozenaLica;
    }
    
    public String getNazivPredmeta() {
        return nazivPredmeta;
    }
    
    public void setNazivPredmeta(String nazivPredmeta) {
        this.nazivPredmeta = nazivPredmeta;
    }
}
