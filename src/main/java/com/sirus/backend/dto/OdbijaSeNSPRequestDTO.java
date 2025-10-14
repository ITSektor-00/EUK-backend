package com.sirus.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO za generisanje Word dokumenta "OДБИЈА СЕ NSP,UNSP,DD,UDTNP"
 */
public class OdbijaSeNSPRequestDTO {
    
    // ========== ID predmeta (obavezan za čuvanje u bazu) ==========
    
    private Integer predmetId;
    
    // ========== Osnovno zaglavlje ==========
    
    @NotBlank(message = "Broj predmeta je obavezan")
    private String brojPredmeta;
    
    @NotBlank(message = "Datum donošenja je obavezan")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Datum mora biti u formatu yyyy-MM-dd")
    private String datumDonosenja;
    
    @NotBlank(message = "Broj ovlašćenja je obavezan")
    private String brojOvlascenja;
    
    @NotBlank(message = "Datum ovlašćenja je obavezan")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Datum mora biti u formatu yyyy-MM-dd")
    private String datumOvlascenja;
    
    @NotBlank(message = "Ime i prezime ovlašćenog lica je obavezno")
    private String imeIPrezimeOvlascenog;
    
    // ========== Podaci o podnosiocu ==========
    
    @NotBlank(message = "Ime i prezime podnosioca je obavezno")
    private String imeIPrezimePodnosioca;
    
    @NotBlank(message = "JMBG je obavezan")
    @Pattern(regexp = "\\d{13}", message = "JMBG mora imati 13 cifara")
    private String jmbg;
    
    @NotBlank(message = "Grad je obavezan")
    private String grad;
    
    @NotBlank(message = "Ulica je obavezna")
    private String ulica;
    
    @NotBlank(message = "Broj stana je obavezan")
    private String brojStana;
    
    @NotBlank(message = "Opština je obavezna")
    private String opstina;
    
    @NotBlank(message = "PTT broj je obavezan")
    private String pttBroj;
    
    @NotBlank(message = "Mesto stanovanja je obavezno")
    private String mestoStanovanja;
    
    // ========== Podaci za obrazloženje ==========
    
    @NotBlank(message = "Datum podnošenja je obavezan")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Datum mora biti u formatu yyyy-MM-dd")
    private String datumPodnosenja;
    
    @NotBlank(message = "Osnov prava je obavezan")
    private String osnovPrava;
    
    /**
     * Kategorija osnova prava za dugi placeholder.
     * Frontend šalje specifičnu kategoriju umesto placeholder-a sa svim opcijama.
     * Primer: "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ"
     */
    private String kategorijaOsnovaPrava;
    
    @NotBlank(message = "Broj članova domaćinstva je obavezan")
    private String brojClanovaDomacinstava;
    
    // ========== Opcioni podaci (textarea) ==========
    
    private String prilozenaInfo;
    private String sluzbeniDokazi;
    private String dodatniTekst;
    
    // ========== Boolean pitanja ==========
    
    /**
     * Da li u tekstu ide "в.д." (vršilac dužnosti)?
     * Ako je FALSE, obrisati "в.д." iz celog dokumenta.
     */
    private Boolean vrsilacDuznosti = true;
    
    /**
     * Da li u tekstu ide "с.р." (sopstvene ruke)?
     * Ako je FALSE, obrisati "с.р." iz dokumenta.
     */
    private Boolean sopstveneRuke = false;
    
    /**
     * Da li se deo o "Додатак за помоћ и негу другог лица..." odnosi na konkretan predmet?
     * Ako je FALSE, NE PISATI taj paragraf u dokumentu.
     */
    private Boolean dodatakZaPomocOdnosiSe = false;
    
    /**
     * Da li se dokumentacija NE pribavlja po službenoj dužnosti?
     * Ako je FALSE, NE PISATI paragraf sa tekstom "Службеним путем, овај орган је...".
     */
    private Boolean pribavljaDokumentacijuSluzbeno = true;
    
    // ========== Constructors ==========
    
    public OdbijaSeNSPRequestDTO() {}
    
    public OdbijaSeNSPRequestDTO(String brojPredmeta, String datumDonosenja, String brojOvlascenja,
                                 String datumOvlascenja, String imeIPrezimeOvlascenog,
                                 String imeIPrezimePodnosioca, String jmbg, String grad, String ulica,
                                 String brojStana, String opstina, String pttBroj, String mestoStanovanja,
                                 String datumPodnosenja, String osnovPrava, String kategorijaOsnovaPrava, String brojClanovaDomacinstava,
                                 String prilozenaInfo, String sluzbeniDokazi, String dodatniTekst,
                                 Boolean vrsilacDuznosti, Boolean sopstveneRuke,
                                 Boolean dodatakZaPomocOdnosiSe, Boolean pribavljaDokumentacijuSluzbeno) {
        this.brojPredmeta = brojPredmeta;
        this.datumDonosenja = datumDonosenja;
        this.brojOvlascenja = brojOvlascenja;
        this.datumOvlascenja = datumOvlascenja;
        this.imeIPrezimeOvlascenog = imeIPrezimeOvlascenog;
        this.imeIPrezimePodnosioca = imeIPrezimePodnosioca;
        this.jmbg = jmbg;
        this.grad = grad;
        this.ulica = ulica;
        this.brojStana = brojStana;
        this.opstina = opstina;
        this.pttBroj = pttBroj;
        this.mestoStanovanja = mestoStanovanja;
        this.datumPodnosenja = datumPodnosenja;
        this.osnovPrava = osnovPrava;
        this.kategorijaOsnovaPrava = kategorijaOsnovaPrava;
        this.brojClanovaDomacinstava = brojClanovaDomacinstava;
        this.prilozenaInfo = prilozenaInfo;
        this.sluzbeniDokazi = sluzbeniDokazi;
        this.dodatniTekst = dodatniTekst;
        this.vrsilacDuznosti = vrsilacDuznosti;
        this.sopstveneRuke = sopstveneRuke;
        this.dodatakZaPomocOdnosiSe = dodatakZaPomocOdnosiSe;
        this.pribavljaDokumentacijuSluzbeno = pribavljaDokumentacijuSluzbeno;
    }
    
    // ========== Getters and Setters ==========
    
    public Integer getPredmetId() {
        return predmetId;
    }
    
    public void setPredmetId(Integer predmetId) {
        this.predmetId = predmetId;
    }
    
    public String getBrojPredmeta() {
        return brojPredmeta;
    }
    
    public void setBrojPredmeta(String brojPredmeta) {
        this.brojPredmeta = brojPredmeta;
    }
    
    public String getDatumDonosenja() {
        return datumDonosenja;
    }
    
    public void setDatumDonosenja(String datumDonosenja) {
        this.datumDonosenja = datumDonosenja;
    }
    
    public String getBrojOvlascenja() {
        return brojOvlascenja;
    }
    
    public void setBrojOvlascenja(String brojOvlascenja) {
        this.brojOvlascenja = brojOvlascenja;
    }
    
    public String getDatumOvlascenja() {
        return datumOvlascenja;
    }
    
    public void setDatumOvlascenja(String datumOvlascenja) {
        this.datumOvlascenja = datumOvlascenja;
    }
    
    public String getImeIPrezimeOvlascenog() {
        return imeIPrezimeOvlascenog;
    }
    
    public void setImeIPrezimeOvlascenog(String imeIPrezimeOvlascenog) {
        this.imeIPrezimeOvlascenog = imeIPrezimeOvlascenog;
    }
    
    public String getImeIPrezimePodnosioca() {
        return imeIPrezimePodnosioca;
    }
    
    public void setImeIPrezimePodnosioca(String imeIPrezimePodnosioca) {
        this.imeIPrezimePodnosioca = imeIPrezimePodnosioca;
    }
    
    public String getJmbg() {
        return jmbg;
    }
    
    public void setJmbg(String jmbg) {
        this.jmbg = jmbg;
    }
    
    public String getGrad() {
        return grad;
    }
    
    public void setGrad(String grad) {
        this.grad = grad;
    }
    
    public String getUlica() {
        return ulica;
    }
    
    public void setUlica(String ulica) {
        this.ulica = ulica;
    }
    
    public String getBrojStana() {
        return brojStana;
    }
    
    public void setBrojStana(String brojStana) {
        this.brojStana = brojStana;
    }
    
    public String getOpstina() {
        return opstina;
    }
    
    public void setOpstina(String opstina) {
        this.opstina = opstina;
    }
    
    public String getPttBroj() {
        return pttBroj;
    }
    
    public void setPttBroj(String pttBroj) {
        this.pttBroj = pttBroj;
    }
    
    public String getMestoStanovanja() {
        return mestoStanovanja;
    }
    
    public void setMestoStanovanja(String mestoStanovanja) {
        this.mestoStanovanja = mestoStanovanja;
    }
    
    public String getDatumPodnosenja() {
        return datumPodnosenja;
    }
    
    public void setDatumPodnosenja(String datumPodnosenja) {
        this.datumPodnosenja = datumPodnosenja;
    }
    
    public String getOsnovPrava() {
        return osnovPrava;
    }
    
    public void setOsnovPrava(String osnovPrava) {
        this.osnovPrava = osnovPrava;
    }
    
    public String getKategorijaOsnovaPrava() {
        return kategorijaOsnovaPrava;
    }
    
    public void setKategorijaOsnovaPrava(String kategorijaOsnovaPrava) {
        this.kategorijaOsnovaPrava = kategorijaOsnovaPrava;
    }
    
    public String getBrojClanovaDomacinstava() {
        return brojClanovaDomacinstava;
    }
    
    public void setBrojClanovaDomacinstava(String brojClanovaDomacinstava) {
        this.brojClanovaDomacinstava = brojClanovaDomacinstava;
    }
    
    public String getPrilozenaInfo() {
        return prilozenaInfo;
    }
    
    public void setPrilozenaInfo(String prilozenaInfo) {
        this.prilozenaInfo = prilozenaInfo;
    }
    
    public String getSluzbeniDokazi() {
        return sluzbeniDokazi;
    }
    
    public void setSluzbeniDokazi(String sluzbeniDokazi) {
        this.sluzbeniDokazi = sluzbeniDokazi;
    }
    
    public String getDodatniTekst() {
        return dodatniTekst;
    }
    
    public void setDodatniTekst(String dodatniTekst) {
        this.dodatniTekst = dodatniTekst;
    }
    
    public Boolean getVrsilacDuznosti() {
        return vrsilacDuznosti;
    }
    
    public void setVrsilacDuznosti(Boolean vrsilacDuznosti) {
        this.vrsilacDuznosti = vrsilacDuznosti;
    }
    
    public Boolean getSopstveneRuke() {
        return sopstveneRuke;
    }
    
    public void setSopstveneRuke(Boolean sopstveneRuke) {
        this.sopstveneRuke = sopstveneRuke;
    }
    
    public Boolean getDodatakZaPomocOdnosiSe() {
        return dodatakZaPomocOdnosiSe;
    }
    
    public void setDodatakZaPomocOdnosiSe(Boolean dodatakZaPomocOdnosiSe) {
        this.dodatakZaPomocOdnosiSe = dodatakZaPomocOdnosiSe;
    }
    
    public Boolean getPribavljaDokumentacijuSluzbeno() {
        return pribavljaDokumentacijuSluzbeno;
    }
    
    public void setPribavljaDokumentacijuSluzbeno(Boolean pribavljaDokumentacijuSluzbeno) {
        this.pribavljaDokumentacijuSluzbeno = pribavljaDokumentacijuSluzbeno;
    }
}

