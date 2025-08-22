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

class EukUgrozenoLiceDto {
    private Integer ugrozenoLiceId;
    private String ime;
    private String prezime;
    private String jmbg;
    private LocalDate datumRodjenja;
    private String drzavaRodjenja;
    private String mestoRodjenja;
    private String opstinaRodjenja;
    private Integer predmetId;
    private String predmetNaziv;
    private String predmetStatus;

    // Getters and Setters
    public Integer getUgrozenoLiceId() { return ugrozenoLiceId; }
    public void setUgrozenoLiceId(Integer ugrozenoLiceId) { this.ugrozenoLiceId = ugrozenoLiceId; }
    
    public String getIme() { return ime; }
    public void setIme(String ime) { this.ime = ime; }
    
    public String getPrezime() { return prezime; }
    public void setPrezime(String prezime) { this.prezime = prezime; }
    
    public String getJmbg() { return jmbg; }
    public void setJmbg(String jmbg) { this.jmbg = jmbg; }
    
    public LocalDate getDatumRodjenja() { return datumRodjenja; }
    public void setDatumRodjenja(LocalDate datumRodjenja) { this.datumRodjenja = datumRodjenja; }
    
    public String getDrzavaRodjenja() { return drzavaRodjenja; }
    public void setDrzavaRodjenja(String drzavaRodjenja) { this.drzavaRodjenja = drzavaRodjenja; }
    
    public String getMestoRodjenja() { return mestoRodjenja; }
    public void setMestoRodjenja(String mestoRodjenja) { this.mestoRodjenja = mestoRodjenja; }
    
    public String getOpstinaRodjenja() { return opstinaRodjenja; }
    public void setOpstinaRodjenja(String opstinaRodjenja) { this.opstinaRodjenja = opstinaRodjenja; }
    
    public Integer getPredmetId() { return predmetId; }
    public void setPredmetId(Integer predmetId) { this.predmetId = predmetId; }
    
    public String getPredmetNaziv() { return predmetNaziv; }
    public void setPredmetNaziv(String predmetNaziv) { this.predmetNaziv = predmetNaziv; }
    
    public String getPredmetStatus() { return predmetStatus; }
    public void setPredmetStatus(String predmetStatus) { this.predmetStatus = predmetStatus; }
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
            
            // Test ugroženo lice
            System.out.println("\n=== TEST UGROŽENO LICE ===");
            EukUgrozenoLiceDto ugrozenoLice = new EukUgrozenoLiceDto();
            ugrozenoLice.setUgrozenoLiceId(456);
            ugrozenoLice.setIme("Marko");
            ugrozenoLice.setPrezime("Marković");
            ugrozenoLice.setJmbg("0101990123456");
            ugrozenoLice.setDatumRodjenja(LocalDate.of(1990, 1, 1));
            ugrozenoLice.setDrzavaRodjenja("Srbija");
            ugrozenoLice.setMestoRodjenja("Beograd");
            ugrozenoLice.setOpstinaRodjenja("Vračar");
            ugrozenoLice.setPredmetId(123);
            ugrozenoLice.setPredmetNaziv("Test Predmet");
            ugrozenoLice.setPredmetStatus("aktivan");

            String ugrozenoLiceJson = objectMapper.writeValueAsString(ugrozenoLice);
            System.out.println("Ugroženo Lice JSON:");
            System.out.println(ugrozenoLiceJson);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
