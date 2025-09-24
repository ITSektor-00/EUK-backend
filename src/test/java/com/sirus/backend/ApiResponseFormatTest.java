package com.sirus.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sirus.backend.dto.EukPredmetDto;
import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
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
    void testUgrozenoLiceT1DtoSerialization() throws Exception {
        // Kreiraj test ugroženo lice T1
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

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(ugrozenoLice);
        System.out.println("Ugroženo Lice T1 JSON: " + json);

        // Proveri da li ID-jevi su brojevi
        assertTrue(json.contains("\"ugrozenoLiceId\":456"));
        assertTrue(json.contains("\"brojClanovaDomacinstva\":3"));
        
        // Proveri da li nisu stringovi
        assertFalse(json.contains("\"ugrozenoLiceId\":\"456\""));
        assertFalse(json.contains("\"brojClanovaDomacinstva\":\"3\""));

        // Deserijalizuj nazad
        EukUgrozenoLiceT1Dto deserialized = objectMapper.readValue(json, EukUgrozenoLiceT1Dto.class);
        assertEquals(456, deserialized.getUgrozenoLiceId());
        assertEquals(3, deserialized.getBrojClanovaDomacinstva());
    }

    @Test
    void testUgrozenoLiceT1PageSerialization() throws Exception {
        // Kreiraj test ugroženo lice T1
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

        // Kreiraj paginiran response
        List<EukUgrozenoLiceT1Dto> content = Arrays.asList(ugrozenoLice);
        Page<EukUgrozenoLiceT1Dto> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);

        // Serijalizuj u JSON
        String json = objectMapper.writeValueAsString(page);
        System.out.println("Ugroženo Lice T1 Page JSON: " + json);

        // Proveri strukturu
        assertTrue(json.contains("\"content\":"));
        assertTrue(json.contains("\"totalPages\":1"));
        assertTrue(json.contains("\"totalElements\":1"));
        assertTrue(json.contains("\"ugrozenoLiceId\":456"));
        assertTrue(json.contains("\"brojClanovaDomacinstva\":3"));
    }
}
