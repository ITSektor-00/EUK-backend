import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;

// Simulacija DTO klasa
class EukPredmetDto {
    private Integer predmetId;
    private String nazivPredmeta;
    private String status;
    private String prioritet;
    private String odgovornaOsoba;
    private Integer kategorijaId;
    private String kategorijaNaziv;
    private Integer brojUgrozenihLica;
    private LocalDate datumKreiranja;
    private LocalDate rokZaZavrsetak;

    // Getters and Setters
    public Integer getPredmetId() { return predmetId; }
    public void setPredmetId(Integer predmetId) { this.predmetId = predmetId; }
    
    public String getNazivPredmeta() { return nazivPredmeta; }
    public void setNazivPredmeta(String nazivPredmeta) { this.nazivPredmeta = nazivPredmeta; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getPrioritet() { return prioritet; }
    public void setPrioritet(String prioritet) { this.prioritet = prioritet; }
    
    public String getOdgovornaOsoba() { return odgovornaOsoba; }
    public void setOdgovornaOsoba(String odgovornaOsoba) { this.odgovornaOsoba = odgovornaOsoba; }
    
    public Integer getKategorijaId() { return kategorijaId; }
    public void setKategorijaId(Integer kategorijaId) { this.kategorijaId = kategorijaId; }
    
    public String getKategorijaNaziv() { return kategorijaNaziv; }
    public void setKategorijaNaziv(String kategorijaNaziv) { this.kategorijaNaziv = kategorijaNaziv; }
    
    public Integer getBrojUgrozenihLica() { return brojUgrozenihLica; }
    public void setBrojUgrozenihLica(Integer brojUgrozenihLica) { this.brojUgrozenihLica = brojUgrozenihLica; }
    
    public LocalDate getDatumKreiranja() { return datumKreiranja; }
    public void setDatumKreiranja(LocalDate datumKreiranja) { this.datumKreiranja = datumKreiranja; }
    
    public LocalDate getRokZaZavrsetak() { return rokZaZavrsetak; }
    public void setRokZaZavrsetak(LocalDate rokZaZavrsetak) { this.rokZaZavrsetak = rokZaZavrsetak; }
}

class EukUgrozenoLiceT1Dto {
    private Integer ugrozenoLiceId;
    private String redniBroj;
    private String ime;
    private String prezime;
    private String jmbg;
    private String pttBroj;
    private String gradOpstina;
    private String mesto;
    private String ulicaIBroj;
    private Integer brojClanovaDomacinstva;
    private String osnovSticanjaStatusa;
    private String edBrojBrojMernogUredjaja;
    private java.math.BigDecimal potrosnjaKwh;
    private java.math.BigDecimal zagrevanaPovrsinaM2;
    private java.math.BigDecimal iznosUmanjenjaSaPdv;
    private String brojRacuna;
    private LocalDate datumIzdavanjaRacuna;
    private LocalDate datumTrajanjaPrava;

    // Getters and Setters
    public Integer getUgrozenoLiceId() { return ugrozenoLiceId; }
    public void setUgrozenoLiceId(Integer ugrozenoLiceId) { this.ugrozenoLiceId = ugrozenoLiceId; }
    
    public String getRedniBroj() { return redniBroj; }
    public void setRedniBroj(String redniBroj) { this.redniBroj = redniBroj; }
    
    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }
    
    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }
    
    public String getJmbg() { return jmbg; }
    public void setJmbg(String jmbg) { this.jmbg = jmbg; }
    
    public String getPttBroj() { return pttBroj; }
    public void setPttBroj(String pttBroj) { this.pttBroj = pttBroj; }
    
    public String getGradOpstina() { return gradOpstina; }
    public void setGradOpstina(String gradOpstina) { this.gradOpstina = gradOpstina; }
    
    public String getMesto() { return mesto; }
    public void setMesto(String mesto) { this.mesto = mesto; }
    
    public String getUlicaIBroj() { return ulicaIBroj; }
    public void setUlicaIBroj(String ulicaIBroj) { this.ulicaIBroj = ulicaIBroj; }
    
    public Integer getBrojClanovaDomacinstva() { return brojClanovaDomacinstva; }
    public void setBrojClanovaDomacinstva(Integer brojClanovaDomacinstva) { this.brojClanovaDomacinstva = brojClanovaDomacinstva; }
    
    public String getOsnovSticanjaStatusa() { return osnovSticanjaStatusa; }
    public void setOsnovSticanjaStatusa(String osnovSticanjaStatusa) { this.osnovSticanjaStatusa = osnovSticanjaStatusa; }
    
    public String getEdBrojBrojMernogUredjaja() { return edBrojBrojMernogUredjaja; }
    public void setEdBrojBrojMernogUredjaja(String edBrojBrojMernogUredjaja) { this.edBrojBrojMernogUredjaja = edBrojBrojMernogUredjaja; }
    
    public java.math.BigDecimal getPotrosnjaKwh() { return potrosnjaKwh; }
    public void setPotrosnjaKwh(java.math.BigDecimal potrosnjaKwh) { this.potrosnjaKwh = potrosnjaKwh; }
    
    public java.math.BigDecimal getZagrevanaPovrsinaM2() { return zagrevanaPovrsinaM2; }
    public void setZagrevanaPovrsinaM2(java.math.BigDecimal zagrevanaPovrsinaM2) { this.zagrevanaPovrsinaM2 = zagrevanaPovrsinaM2; }
    
    public java.math.BigDecimal getIznosUmanjenjaSaPdv() { return iznosUmanjenjaSaPdv; }
    public void setIznosUmanjenjaSaPdv(java.math.BigDecimal iznosUmanjenjaSaPdv) { this.iznosUmanjenjaSaPdv = iznosUmanjenjaSaPdv; }
    
    public String getBrojRacuna() { return brojRacuna; }
    public void setBrojRacuna(String brojRacuna) { this.brojRacuna = brojRacuna; }
    
    public LocalDate getDatumIzdavanjaRacuna() { return datumIzdavanjaRacuna; }
    public void setDatumIzdavanjaRacuna(LocalDate datumIzdavanjaRacuna) { this.datumIzdavanjaRacuna = datumIzdavanjaRacuna; }
    
    public LocalDate getDatumTrajanjaPrava() { return datumTrajanjaPrava; }
    public void setDatumTrajanjaPrava(LocalDate datumTrajanjaPrava) { this.datumTrajanjaPrava = datumTrajanjaPrava; }
}

public class TestSerialization {
    public static void main(String[] args) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Test predmet
            System.out.println("=== TEST PREDMET ===");
            EukPredmetDto predmet = new EukPredmetDto();
            predmet.setPredmetId(123);
            predmet.setNazivPredmeta("Test Predmet");
            predmet.setStatus("aktivan");
            predmet.setPrioritet("srednji");
            predmet.setOdgovornaOsoba("Pera Perić");
            predmet.setKategorijaId(5);
            predmet.setKategorijaNaziv("Test Kategorija");
            predmet.setBrojUgrozenihLica(3);
            predmet.setDatumKreiranja(LocalDate.of(2024, 1, 15));
            predmet.setRokZaZavrsetak(LocalDate.of(2024, 12, 31));

            String predmetJson = objectMapper.writeValueAsString(predmet);
            System.out.println("Predmet JSON:");
            System.out.println(predmetJson);
            
            // Test ugroženo lice T1
            System.out.println("\n=== TEST UGROŽENO LICE T1 ===");
            EukUgrozenoLiceT1Dto ugrozenoLice = new EukUgrozenoLiceT1Dto();
            ugrozenoLice.setUgrozenoLiceId(456);
            ugrozenoLice.setRedniBroj("001");
            ugrozenoLice.setIme("Marko");
            ugrozenoLice.setPrezime("Marković");
            ugrozenoLice.setJmbg("0101990123456");
            ugrozenoLice.setPttBroj("11000");
            ugrozenoLice.setGradOpstina("Beograd");
            ugrozenoLice.setMesto("Vračar");
            ugrozenoLice.setUlicaIBroj("Knez Mihailova 1");
            ugrozenoLice.setBrojClanovaDomacinstva(3);
            ugrozenoLice.setOsnovSticanjaStatusa("MP");
            ugrozenoLice.setEdBrojBrojMernogUredjaja("ED123456");
            ugrozenoLice.setPotrosnjaKwh(new java.math.BigDecimal("2500.50"));
            ugrozenoLice.setZagrevanaPovrsinaM2(new java.math.BigDecimal("75.5"));
            ugrozenoLice.setIznosUmanjenjaSaPdv(new java.math.BigDecimal("1250.25"));
            ugrozenoLice.setBrojRacuna("RAC-2024-001");
            ugrozenoLice.setDatumIzdavanjaRacuna(LocalDate.of(2024, 1, 15));
            ugrozenoLice.setDatumTrajanjaPrava(LocalDate.of(2025, 1, 15));

            String ugrozenoLiceJson = objectMapper.writeValueAsString(ugrozenoLice);
            System.out.println("Ugroženo Lice T1 JSON:");
            System.out.println(ugrozenoLiceJson);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
