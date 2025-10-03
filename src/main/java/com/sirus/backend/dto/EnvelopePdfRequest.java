package com.sirus.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class EnvelopePdfRequest {
    
    @NotNull(message = "Template je obavezan")
    private String template;
    
    @NotEmpty(message = "Lista ugroženih lica ne može biti prazna")
    private List<UgrozenoLiceDto> ugrozenaLica;
    
    public EnvelopePdfRequest() {}
    
    public EnvelopePdfRequest(String template, List<UgrozenoLiceDto> ugrozenaLica) {
        this.template = template;
        this.ugrozenaLica = ugrozenaLica;
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
}
