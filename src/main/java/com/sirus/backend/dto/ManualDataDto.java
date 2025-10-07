package com.sirus.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ManualDataDto {
    
    @NotBlank(message = "Zaglavlje je obavezno")
    private String zaglavlje;
    
    @NotBlank(message = "Obrazloženje je obavezno")
    private String obrazlozenje;
    
    private String dodatniPodaci;
    
    // Nova polja za rešenje OДБИЈА СЕ NSP,UNSP,DD,UDTNP
    private String predmet; // (предмета)_____________________
    private String datumDonosenjaResenja; // (доношења решења)_____________године
    private String brojResenja; // (број)___________________________
    private String datumOvlastenja; // (датум овлашћења)________________
    private String imePrezimeLica; // (име и презиме)___________ - iz baze
    private String ulicaIBroj; // Ул .__________бр._______ - iz baze
    private String imePrezimePravnogLica; // (име и презиме)__________________ - iz baze
    private String jmbgPravnogLica; // ЈМБГ ___________ - iz baze
    private String adresaPravnogLica; // ________________(град, улица и број, општина, ПТТ број) - iz baze
    private String datumPodnosenja; // _______________(датум подношења) - ručno
    private String imePrezimePodnosioca; // (име и презиме)__________________ - iz baze
    private String jmbgPodnosioca; // ЈМБГ ___________ - iz baze
    private String adresaPodnosioca; // из __________,Ул. ____________ бр. ______ - iz baze
    private String dodatniTekst; // Оставити простор да се, уколико има потребе, убаци још неки део текста
    private Boolean pribavljaDokumentaciju; // da li se pribavlja dokumentacija po službenoj dužnosti
    private Boolean vdPotpis; // da li je в.д. ili ne
    private Boolean srPotpis; // da li ima potpis с.р.
    
    // Constructors
    public ManualDataDto() {}
    
    public ManualDataDto(String zaglavlje, String obrazlozenje, String dodatniPodaci) {
        this.zaglavlje = zaglavlje;
        this.obrazlozenje = obrazlozenje;
        this.dodatniPodaci = dodatniPodaci;
    }
    
    // Getters and Setters
    public String getZaglavlje() {
        return zaglavlje;
    }
    
    public void setZaglavlje(String zaglavlje) {
        this.zaglavlje = zaglavlje;
    }
    
    public String getObrazlozenje() {
        return obrazlozenje;
    }
    
    public void setObrazlozenje(String obrazlozenje) {
        this.obrazlozenje = obrazlozenje;
    }
    
    public String getDodatniPodaci() {
        return dodatniPodaci;
    }
    
    public void setDodatniPodaci(String dodatniPodaci) {
        this.dodatniPodaci = dodatniPodaci;
    }
    
    // Nova polja getters and setters
    public String getPredmet() {
        return predmet;
    }
    
    public void setPredmet(String predmet) {
        this.predmet = predmet;
    }
    
    public String getDatumDonosenjaResenja() {
        return datumDonosenjaResenja;
    }
    
    public void setDatumDonosenjaResenja(String datumDonosenjaResenja) {
        this.datumDonosenjaResenja = datumDonosenjaResenja;
    }
    
    public String getBrojResenja() {
        return brojResenja;
    }
    
    public void setBrojResenja(String brojResenja) {
        this.brojResenja = brojResenja;
    }
    
    public String getDatumOvlastenja() {
        return datumOvlastenja;
    }
    
    public void setDatumOvlastenja(String datumOvlastenja) {
        this.datumOvlastenja = datumOvlastenja;
    }
    
    public String getImePrezimeLica() {
        return imePrezimeLica;
    }
    
    public void setImePrezimeLica(String imePrezimeLica) {
        this.imePrezimeLica = imePrezimeLica;
    }
    
    public String getUlicaIBroj() {
        return ulicaIBroj;
    }
    
    public void setUlicaIBroj(String ulicaIBroj) {
        this.ulicaIBroj = ulicaIBroj;
    }
    
    public String getImePrezimePravnogLica() {
        return imePrezimePravnogLica;
    }
    
    public void setImePrezimePravnogLica(String imePrezimePravnogLica) {
        this.imePrezimePravnogLica = imePrezimePravnogLica;
    }
    
    public String getJmbgPravnogLica() {
        return jmbgPravnogLica;
    }
    
    public void setJmbgPravnogLica(String jmbgPravnogLica) {
        this.jmbgPravnogLica = jmbgPravnogLica;
    }
    
    public String getAdresaPravnogLica() {
        return adresaPravnogLica;
    }
    
    public void setAdresaPravnogLica(String adresaPravnogLica) {
        this.adresaPravnogLica = adresaPravnogLica;
    }
    
    public String getDatumPodnosenja() {
        return datumPodnosenja;
    }
    
    public void setDatumPodnosenja(String datumPodnosenja) {
        this.datumPodnosenja = datumPodnosenja;
    }
    
    public String getImePrezimePodnosioca() {
        return imePrezimePodnosioca;
    }
    
    public void setImePrezimePodnosioca(String imePrezimePodnosioca) {
        this.imePrezimePodnosioca = imePrezimePodnosioca;
    }
    
    public String getJmbgPodnosioca() {
        return jmbgPodnosioca;
    }
    
    public void setJmbgPodnosioca(String jmbgPodnosioca) {
        this.jmbgPodnosioca = jmbgPodnosioca;
    }
    
    public String getAdresaPodnosioca() {
        return adresaPodnosioca;
    }
    
    public void setAdresaPodnosioca(String adresaPodnosioca) {
        this.adresaPodnosioca = adresaPodnosioca;
    }
    
    public String getDodatniTekst() {
        return dodatniTekst;
    }
    
    public void setDodatniTekst(String dodatniTekst) {
        this.dodatniTekst = dodatniTekst;
    }
    
    public Boolean getPribavljaDokumentaciju() {
        return pribavljaDokumentaciju;
    }
    
    public void setPribavljaDokumentaciju(Boolean pribavljaDokumentaciju) {
        this.pribavljaDokumentaciju = pribavljaDokumentaciju;
    }
    
    public Boolean getVdPotpis() {
        return vdPotpis;
    }
    
    public void setVdPotpis(Boolean vdPotpis) {
        this.vdPotpis = vdPotpis;
    }
    
    public Boolean getSrPotpis() {
        return srPotpis;
    }
    
    public void setSrPotpis(Boolean srPotpis) {
        this.srPotpis = srPotpis;
    }
}
