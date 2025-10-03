package com.sirus.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "formular_polja", schema = "euk")
public class EukFormularPolje {
    
    public enum TipPolja {
        TEXT("text"),
        TEXTAREA("textarea"),
        NUMBER("number"),
        DATE("date"),
        DATETIME("datetime"),
        SELECT("select"),
        CHECKBOX("checkbox"),
        RADIO("radio"),
        FILE("file");
        
        private final String value;
        
        TipPolja(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static TipPolja fromValue(String value) {
            for (TipPolja tip : values()) {
                if (tip.value.equals(value)) {
                    return tip;
                }
            }
            throw new IllegalArgumentException("Nepoznat tip polja: " + value);
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "polje_id")
    private Integer poljeId;
    
    @NotNull(message = "Formular je obavezan")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formular_id", nullable = false)
    private EukFormular formular;
    
    @NotBlank(message = "Naziv polja je obavezan")
    @Size(max = 255, message = "Naziv polja ne može biti duži od 255 karaktera")
    @Column(name = "naziv_polja", nullable = false, length = 255)
    private String nazivPolja;
    
    @NotBlank(message = "Label je obavezan")
    @Size(max = 255, message = "Label ne može biti duži od 255 karaktera")
    @Column(name = "label", nullable = false, length = 255)
    private String label;
    
    @NotNull(message = "Tip polja je obavezan")
    @Enumerated(EnumType.STRING)
    @Column(name = "tip_polja", nullable = false, length = 50)
    private TipPolja tipPolja;
    
    @Column(name = "obavezno", nullable = false)
    private Boolean obavezno = false;
    
    @NotNull(message = "Redosled je obavezan")
    @Column(name = "redosled", nullable = false)
    private Integer redosled = 0;
    
    @Size(max = 255, message = "Placeholder ne može biti duži od 255 karaktera")
    @Column(name = "placeholder", length = 255)
    private String placeholder;
    
    @Size(max = 1000, message = "Opis ne može biti duži od 1000 karaktera")
    @Column(name = "opis", length = 1000)
    private String opis;
    
    @Size(max = 1000, message = "Validacija ne može biti duža od 1000 karaktera")
    @Column(name = "validacija", length = 1000)
    private String validacija; // JSON string za validaciju
    
    @Size(max = 2000, message = "Opcije ne mogu biti duže od 2000 karaktera")
    @Column(name = "opcije", length = 2000)
    private String opcije; // JSON string za select/checkbox/radio opcije
    
    @Size(max = 500, message = "Default vrednost ne može biti duža od 500 karaktera")
    @Column(name = "default_vrednost", length = 500)
    private String defaultVrednost;
    
    @Column(name = "readonly", nullable = false)
    private Boolean readonly = false;
    
    @Column(name = "visible", nullable = false)
    private Boolean visible = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Veza sa podacima formulare
    @OneToMany(mappedBy = "polje", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private java.util.List<EukPredmetFormularPodaci> podaci = new java.util.ArrayList<>();
    
    // Veza sa dokumentima
    @OneToMany(mappedBy = "polje", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private java.util.List<EukFormularDokumenti> dokumenti = new java.util.ArrayList<>();
    
    // Konstruktori
    public EukFormularPolje() {}
    
    public EukFormularPolje(String nazivPolja, String label, TipPolja tipPolja, Integer redosled) {
        this.nazivPolja = nazivPolja;
        this.label = label;
        this.tipPolja = tipPolja;
        this.redosled = redosled;
    }
    
    // Getters and Setters
    public Integer getPoljeId() {
        return poljeId;
    }
    
    public void setPoljeId(Integer poljeId) {
        this.poljeId = poljeId;
    }
    
    public EukFormular getFormular() {
        return formular;
    }
    
    public void setFormular(EukFormular formular) {
        this.formular = formular;
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
    
    public TipPolja getTipPolja() {
        return tipPolja;
    }
    
    public void setTipPolja(TipPolja tipPolja) {
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
    
    public java.util.List<EukPredmetFormularPodaci> getPodaci() {
        return podaci;
    }
    
    public void setPodaci(java.util.List<EukPredmetFormularPodaci> podaci) {
        this.podaci = podaci;
    }
    
    public java.util.List<EukFormularDokumenti> getDokumenti() {
        return dokumenti;
    }
    
    public void setDokumenti(java.util.List<EukFormularDokumenti> dokumenti) {
        this.dokumenti = dokumenti;
    }
    
    // Helper metode
    public boolean isSelectType() {
        return tipPolja == TipPolja.SELECT || tipPolja == TipPolja.RADIO || tipPolja == TipPolja.CHECKBOX;
    }
    
    public boolean isFileType() {
        return tipPolja == TipPolja.FILE;
    }
    
    public boolean isDateType() {
        return tipPolja == TipPolja.DATE || tipPolja == TipPolja.DATETIME;
    }
    
    public boolean isNumberType() {
        return tipPolja == TipPolja.NUMBER;
    }
    
    @Override
    public String toString() {
        return "EukFormularPolje{" +
                "poljeId=" + poljeId +
                ", nazivPolja='" + nazivPolja + '\'' +
                ", label='" + label + '\'' +
                ", tipPolja=" + tipPolja +
                ", obavezno=" + obavezno +
                ", redosled=" + redosled +
                ", visible=" + visible +
                '}';
    }
}
