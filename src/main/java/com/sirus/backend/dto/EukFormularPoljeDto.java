package com.sirus.backend.dto;

import com.sirus.backend.entity.EukFormularPolje;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EukFormularPoljeDto {
    
    private Integer poljeId;
    
    private Integer formularId;
    private String formularNaziv;
    
    @NotBlank(message = "Naziv polja je obavezan")
    @Size(max = 255, message = "Naziv polja ne može biti duži od 255 karaktera")
    private String nazivPolja;
    
    @NotBlank(message = "Label je obavezan")
    @Size(max = 255, message = "Label ne može biti duži od 255 karaktera")
    private String label;
    
    @NotNull(message = "Tip polja je obavezan")
    private String tipPolja;
    
    private Boolean obavezno;
    private Integer redosled;
    
    @Size(max = 255, message = "Placeholder ne može biti duži od 255 karaktera")
    private String placeholder;
    
    @Size(max = 1000, message = "Opis ne može biti duži od 1000 karaktera")
    private String opis;
    
    @Size(max = 1000, message = "Validacija ne može biti duža od 1000 karaktera")
    private String validacija;
    
    @Size(max = 2000, message = "Opcije ne mogu biti duže od 2000 karaktera")
    private String opcije;
    
    @Size(max = 500, message = "Default vrednost ne može biti duža od 500 karaktera")
    private String defaultVrednost;
    
    private Boolean readonly;
    private Boolean visible;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Dodatna polja za frontend
    private Map<String, Object> validacijaMap; // Parsirana validacija
    private List<Map<String, Object>> opcijeList; // Parsirane opcije
    private String vrednost; // Trenutna vrednost (za edit mode)
    
    // Konstruktori
    public EukFormularPoljeDto() {}
    
    public EukFormularPoljeDto(EukFormularPolje polje) {
        this.poljeId = polje.getPoljeId();
        this.formularId = polje.getFormular() != null ? polje.getFormular().getFormularId() : null;
        this.formularNaziv = polje.getFormular() != null ? polje.getFormular().getNaziv() : null;
        this.nazivPolja = polje.getNazivPolja();
        this.label = polje.getLabel();
        this.tipPolja = polje.getTipPolja() != null ? polje.getTipPolja().getValue() : null;
        this.obavezno = polje.getObavezno();
        this.redosled = polje.getRedosled();
        this.placeholder = polje.getPlaceholder();
        this.opis = polje.getOpis();
        this.validacija = polje.getValidacija();
        this.opcije = polje.getOpcije();
        this.defaultVrednost = polje.getDefaultVrednost();
        this.readonly = polje.getReadonly();
        this.visible = polje.getVisible();
        this.createdAt = polje.getCreatedAt();
        this.updatedAt = polje.getUpdatedAt();
    }
    
    // Getters and Setters
    public Integer getPoljeId() {
        return poljeId;
    }
    
    public void setPoljeId(Integer poljeId) {
        this.poljeId = poljeId;
    }
    
    public Integer getFormularId() {
        return formularId;
    }
    
    public void setFormularId(Integer formularId) {
        this.formularId = formularId;
    }
    
    public String getFormularNaziv() {
        return formularNaziv;
    }
    
    public void setFormularNaziv(String formularNaziv) {
        this.formularNaziv = formularNaziv;
    }
    
    public String getNazivPolja() {
        return nazivPolja;
    }
    
    public void setNazivPolja(String nazivPolja) {
        this.nazivPolja = nazivPolja;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getTipPolja() {
        return tipPolja;
    }
    
    public void setTipPolja(String tipPolja) {
        this.tipPolja = tipPolja;
    }
    
    public Boolean getObavezno() {
        return obavezno;
    }
    
    public void setObavezno(Boolean obavezno) {
        this.obavezno = obavezno;
    }
    
    public Integer getRedosled() {
        return redosled;
    }
    
    public void setRedosled(Integer redosled) {
        this.redosled = redosled;
    }
    
    public String getPlaceholder() {
        return placeholder;
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
    
    public String getOpis() {
        return opis;
    }
    
    public void setOpis(String opis) {
        this.opis = opis;
    }
    
    public String getValidacija() {
        return validacija;
    }
    
    public void setValidacija(String validacija) {
        this.validacija = validacija;
    }
    
    public String getOpcije() {
        return opcije;
    }
    
    public void setOpcije(String opcije) {
        this.opcije = opcije;
    }
    
    public String getDefaultVrednost() {
        return defaultVrednost;
    }
    
    public void setDefaultVrednost(String defaultVrednost) {
        this.defaultVrednost = defaultVrednost;
    }
    
    public Boolean getReadonly() {
        return readonly;
    }
    
    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }
    
    public Boolean getVisible() {
        return visible;
    }
    
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Map<String, Object> getValidacijaMap() {
        return validacijaMap;
    }
    
    public void setValidacijaMap(Map<String, Object> validacijaMap) {
        this.validacijaMap = validacijaMap;
    }
    
    public List<Map<String, Object>> getOpcijeList() {
        return opcijeList;
    }
    
    public void setOpcijeList(List<Map<String, Object>> opcijeList) {
        this.opcijeList = opcijeList;
    }
    
    public String getVrednost() {
        return vrednost;
    }
    
    public void setVrednost(String vrednost) {
        this.vrednost = vrednost;
    }
    
    // Helper metode
    public boolean isSelectType() {
        return "select".equals(tipPolja) || "radio".equals(tipPolja) || "checkbox".equals(tipPolja);
    }
    
    public boolean isFileType() {
        return "file".equals(tipPolja);
    }
    
    public boolean isDateType() {
        return "date".equals(tipPolja) || "datetime".equals(tipPolja);
    }
    
    public boolean isNumberType() {
        return "number".equals(tipPolja);
    }
    
    @Override
    public String toString() {
        return "EukFormularPoljeDto{" +
                "poljeId=" + poljeId +
                ", nazivPolja='" + nazivPolja + '\'' +
                ", label='" + label + '\'' +
                ", tipPolja='" + tipPolja + '\'' +
                ", obavezno=" + obavezno +
                ", redosled=" + redosled +
                ", visible=" + visible +
                '}';
    }
}
