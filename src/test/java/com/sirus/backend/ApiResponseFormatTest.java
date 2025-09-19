package com.sirus.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirus.backend.dto.EukPredmetDto;
import com.sirus.backend.dto.EukUgrozenoLiceDto;
import com.sirus.backend.entity.EukPredmet;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApiResponseFormatTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPredmetDtoSerialization() throws Exception {
        // Kreiraj test predmet
        EukPredmetDto predmet = new EukPredmetDto();
        predmet.setPredmetId(123);
        predmet.setNazivPredmeta("Test Predmet");
        predmet.setStatus(EukPredmet.Status.АКТИВАН);
        predmet.setPrioritet(EukPredmet.Prioritet.СРЕДЊИ);
        predmet.setOdgovornaOsoba("Pera Perić");
        predmet.setKategorijaId(5);
        predmet.setKategorijaNaziv("Test Kategorija");
        predmet.setBrojUgrozenihLica(3);
        predmet.setDatumKreiranja(LocalDate.of(2024, 1, 15));
        predmet.setRokZaZavrsetak(LocalDate.of(2024, 12, 31));

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(predmet);
        System.out.println("Predmet JSON: " + json);

        // Proveri da li ID-jevi su brojevi
        assertTrue(json.contains("\"predmetId\":123"));
        assertTrue(json.contains("\"kategorijaId\":5"));
        assertTrue(json.contains("\"brojUgrozenihLica\":3"));
        
        // Proveri da li nisu stringovi
        assertFalse(json.contains("\"predmetId\":\"123\""));
        assertFalse(json.contains("\"kategorijaId\":\"5\""));
        assertFalse(json.contains("\"brojUgrozenihLica\":\"3\""));

        // Deserijalizuj nazad
        EukPredmetDto deserialized = objectMapper.readValue(json, EukPredmetDto.class);
        assertEquals(123, deserialized.getPredmetId());
        assertEquals(5, deserialized.getKategorijaId());
        assertEquals(3, deserialized.getBrojUgrozenihLica());
    }

    @Test
    void testPredmetPageSerialization() throws Exception {
        // Kreiraj test predmet
        EukPredmetDto predmet = new EukPredmetDto();
        predmet.setPredmetId(123);
        predmet.setNazivPredmeta("Test Predmet");
        predmet.setStatus(EukPredmet.Status.АКТИВАН);
        predmet.setPrioritet(EukPredmet.Prioritet.СРЕДЊИ);
        predmet.setOdgovornaOsoba("Pera Perić");
        predmet.setKategorijaId(5);
        predmet.setKategorijaNaziv("Test Kategorija");
        predmet.setBrojUgrozenihLica(3);

        // Kreiraj paginiran response
        List<EukPredmetDto> content = Arrays.asList(predmet);
        Page<EukPredmetDto> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(page);
        System.out.println("Predmet Page JSON: " + json);

        // Proveri strukturu
        assertTrue(json.contains("\"content\":"));
        assertTrue(json.contains("\"totalPages\":1"));
        assertTrue(json.contains("\"totalElements\":1"));
        assertTrue(json.contains("\"predmetId\":123"));
        assertTrue(json.contains("\"kategorijaId\":5"));
    }

    @Test
    void testUgrozenoLiceDtoSerialization() throws Exception {
        // Kreiraj test ugroženo lice
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

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(ugrozenoLice);
        System.out.println("Ugroženo Lice JSON: " + json);

        // Proveri da li ID-jevi su brojevi
        assertTrue(json.contains("\"ugrozenoLiceId\":456"));
        assertTrue(json.contains("\"predmetId\":123"));
        
        // Proveri da li nisu stringovi
        assertFalse(json.contains("\"ugrozenoLiceId\":\"456\""));
        assertFalse(json.contains("\"predmetId\":\"123\""));

        // Deserijalizuj nazad
        EukUgrozenoLiceDto deserialized = objectMapper.readValue(json, EukUgrozenoLiceDto.class);
        assertEquals(456, deserialized.getUgrozenoLiceId());
        assertEquals(123, deserialized.getPredmetId());
    }

    @Test
    void testUgrozenoLicePageSerialization() throws Exception {
        // Kreiraj test ugroženo lice
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

        // Kreiraj paginiran response
        List<EukUgrozenoLiceDto> content = Arrays.asList(ugrozenoLice);
        Page<EukUgrozenoLiceDto> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(page);
        System.out.println("Ugroženo Lice Page JSON: " + json);

        // Proveri strukturu
        assertTrue(json.contains("\"content\":"));
        assertTrue(json.contains("\"totalPages\":1"));
        assertTrue(json.contains("\"totalElements\":1"));
        assertTrue(json.contains("\"ugrozenoLiceId\":456"));
        assertTrue(json.contains("\"predmetId\":123"));
    }
}
