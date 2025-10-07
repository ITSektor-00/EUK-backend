package com.sirus.backend.service;

import com.sirus.backend.dto.TemplateGenerationRequestDto;
import com.sirus.backend.dto.TemplateGenerationResponseDto;
import com.sirus.backend.dto.EukKategorijaDto;
import com.sirus.backend.entity.*;
import com.sirus.backend.repository.EukPredmetRepository;
import com.sirus.backend.repository.EukUgrozenoLiceT1Repository;
import com.sirus.backend.repository.EukUgrozenoLiceT2Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.sirus.backend.dto.EukUgrozenoLiceT1Dto;
import com.sirus.backend.dto.EukUgrozenoLiceT2Dto;
import com.sirus.backend.dto.ManualDataDto;

@Service
@Transactional
public class TemplateGenerationService {
    
    @Autowired
    private EukPredmetRepository predmetiRepository;
    
    @Autowired
    private EukUgrozenoLiceT1Repository ugrovenoLiceT1Repository;
    
    @Autowired
    private EukUgrozenoLiceT2Repository ugrovenoLiceT2Repository;
    
    @Autowired
    private EukUgrozenoLiceT1Service ugrovenoLiceT1Service;
    
    @Autowired
    private EukUgrozenoLiceT2Service ugrovenoLiceT2Service;
    
    @Autowired
    private EukKategorijaService kategorijaService;
    
    @Autowired
    private EukObrasciVrsteService obrasciVrsteService;
    
    @Autowired
    private EukOrganizacionaStrukturaService organizacionaStrukturaService;
    
    @Autowired
    private WordTemplateService wordTemplateService;
    
    public TemplateGenerationResponseDto generateTemplate(TemplateGenerationRequestDto request) {
        try {
            // Validacija da li svi entiteti postoje
            if (!validateEntities(request)) {
                return new TemplateGenerationResponseDto(
                    request.getPredmetId(), 
                    null, 
                    "error", 
                    null, 
                    "Jedan ili više entiteta ne postoje", 
                    false
                );
            }
            
            // Dobijanje podataka
            Optional<EukPredmet> predmetOpt = predmetiRepository.findById(request.getPredmetId().intValue());
            if (!predmetOpt.isPresent()) {
                return new TemplateGenerationResponseDto(
                    request.getPredmetId(), 
                    null, 
                    "error", 
                    null, 
                    "Predmet ne postoji", 
                    false
                );
            }
            
            EukPredmet predmet = predmetOpt.get();
            
            // Dobijanje podataka o licu
            Object liceData = getLiceData(request);
            
            // Priprema ručnih podataka
            Map<String, String> manualDataMap = prepareManualData(request.getManualData());
            
            // Generisanje Word dokumenta
            String filePath = wordTemplateService.generateWordDocument(
                request, 
                (request.getLiceTip().equals("t1") ? (EukUgrozenoLiceT1Dto) liceData : null),
                (request.getLiceTip().equals("t2") ? (EukUgrozenoLiceT2Dto) liceData : null),
                manualDataMap
            );
            
            // Ažuriranje predmeta sa putanjom fajla
            predmet.setTemplateFilePath(filePath);
            predmet.setTemplateGeneratedAt(LocalDateTime.now());
            predmet.setTemplateStatus("generated");
            predmetiRepository.save(predmet);
            
            return new TemplateGenerationResponseDto(
                request.getPredmetId(),
                filePath,
                "generated",
                LocalDateTime.now(),
                "Template je uspešno generisan",
                true
            );
            
        } catch (Exception e) {
            return new TemplateGenerationResponseDto(
                request.getPredmetId(),
                null,
                "error",
                null,
                "Greška pri generisanju template-a: " + e.getMessage(),
                false
            );
        }
    }
    
    private boolean validateEntities(TemplateGenerationRequestDto request) {
        System.out.println("=== VALIDATING ENTITIES ===");
        System.out.println("Request: " + request);
        
        // Proverava da li lice postoji (t1 ili t2)
        boolean liceExists = false;
        if ("t1".equals(request.getLiceTip())) {
            liceExists = ugrovenoLiceT1Repository.existsById(request.getLiceId().intValue());
            System.out.println("T1 lice exists: " + liceExists + " (ID: " + request.getLiceId() + ")");
        } else if ("t2".equals(request.getLiceTip())) {
            liceExists = ugrovenoLiceT2Repository.existsById(request.getLiceId().intValue());
            System.out.println("T2 lice exists: " + liceExists + " (ID: " + request.getLiceId() + ")");
        }
        
        // Proverava da li kategorija postoji
        boolean kategorijaExists = false;
        try {
            kategorijaService.findById(request.getKategorijaId().intValue());
            kategorijaExists = true;
            System.out.println("Kategorija exists: " + kategorijaExists + " (ID: " + request.getKategorijaId() + ")");
        } catch (Exception e) {
            kategorijaExists = false;
            System.out.println("Kategorija exists: " + kategorijaExists + " (ID: " + request.getKategorijaId() + ") - Error: " + e.getMessage());
        }
        
        // Proverava da li obrasci vrste postoji
        boolean obrasciVrsteExists = obrasciVrsteService.getObrasciVrsteById(request.getObrasciVrsteId()).isPresent();
        System.out.println("Obrasci vrste exists: " + obrasciVrsteExists + " (ID: " + request.getObrasciVrsteId() + ")");
        
        // Proverava da li organizaciona struktura postoji
        boolean organizacionaStrukturaExists = organizacionaStrukturaService.getOrganizacionaStrukturaById(request.getOrganizacionaStrukturaId()).isPresent();
        System.out.println("Organizaciona struktura exists: " + organizacionaStrukturaExists + " (ID: " + request.getOrganizacionaStrukturaId() + ")");
        
        boolean result = liceExists && kategorijaExists && obrasciVrsteExists && organizacionaStrukturaExists;
        System.out.println("Final validation result: " + result);
        System.out.println("=== END VALIDATION ===");
        
        return result;
    }
    
    private String generateTemplateContent(TemplateGenerationRequestDto request) {
        StringBuilder content = new StringBuilder();
        
        // Dobijanje podataka
        EukKategorijaDto kategorijaDto = kategorijaService.findById(request.getKategorijaId().intValue());
        EukObrasciVrste obrasciVrste = obrasciVrsteService.getObrasciVrsteById(request.getObrasciVrsteId()).get();
        EukOrganizacionaStruktura organizacionaStruktura = organizacionaStrukturaService.getOrganizacionaStrukturaById(request.getOrganizacionaStrukturaId()).get();
        
        // Generisanje template sadržaja
        content.append("=== TEMPLATE REŠENJA ===\n\n");
        content.append("Datum generisanja: ").append(LocalDateTime.now()).append("\n");
        content.append("Predmet ID: ").append(request.getPredmetId()).append("\n");
        content.append("Lice ID: ").append(request.getLiceId()).append(" (tip: ").append(request.getLiceTip()).append(")\n");
        content.append("Kategorija: ").append(kategorijaDto.getNaziv()).append("\n");
        content.append("Obrasci vrste: ").append(obrasciVrste.getNaziv()).append("\n");
        content.append("Organizaciona struktura: ").append(organizacionaStruktura.getNaziv()).append("\n\n");
        
        content.append("=== SADRŽAJ REŠENJA ===\n");
        content.append("Na osnovu izabranih parametara, generisano je rešenje koje kombinuje:\n");
        content.append("- Kategoriju: ").append(kategorijaDto.getNaziv()).append("\n");
        content.append("- Tip obraza: ").append(obrasciVrste.getNaziv()).append("\n");
        content.append("- Organizacionu strukturu: ").append(organizacionaStruktura.getNaziv()).append("\n\n");
        
        content.append("=== DETALJI ===\n");
        content.append("Kategorija skraćenica: ").append(kategorijaDto.getSkracenica()).append("\n");
        if (obrasciVrste.getOpis() != null) {
            content.append("Obrasci vrste opis: ").append(obrasciVrste.getOpis()).append("\n");
        }
        if (organizacionaStruktura.getOpis() != null) {
            content.append("Organizaciona struktura opis: ").append(organizacionaStruktura.getOpis()).append("\n");
        }
        
        return content.toString();
    }
    
    private String createPhysicalFile(EukPredmet predmet, String content) throws IOException {
        // Kreiranje direktorijuma za template fajlove
        String templatesDir = "templates";
        File dir = new File(templatesDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Generisanje imena fajla
        String fileName = "template_" + predmet.getPredmetId() + "_" + System.currentTimeMillis() + ".txt";
        String filePath = templatesDir + File.separator + fileName;
        
        // Kreiranje fajla
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        
        return filePath;
    }
    
    private Object getLiceData(TemplateGenerationRequestDto request) {
        if ("t1".equals(request.getLiceTip())) {
            return ugrovenoLiceT1Service.findById(request.getLiceId().intValue());
        } else if ("t2".equals(request.getLiceTip())) {
            return ugrovenoLiceT2Service.getUgrozenoLiceById(request.getLiceId().intValue());
        }
        return null;
    }
    
    private Map<String, String> prepareManualData(ManualDataDto manualData) {
        Map<String, String> manualDataMap = new HashMap<>();
        if (manualData != null) {
            // Osnovni podaci
            manualDataMap.put("ZAGLAVLJE", manualData.getZaglavlje());
            manualDataMap.put("OBRAZLOZENJE", manualData.getObrazlozenje());
            if (manualData.getDodatniPodaci() != null) {
                manualDataMap.put("DODATNI_PODACI", manualData.getDodatniPodaci());
            }
            
            // Nova polja za rešenje OДБИЈА СЕ NSP,UNSP,DD,UDTNP
            if (manualData.getPredmet() != null) {
                manualDataMap.put("PREDMET", manualData.getPredmet());
            }
            if (manualData.getDatumDonosenjaResenja() != null) {
                manualDataMap.put("DATUM_DONOSENJA_RESENJA", manualData.getDatumDonosenjaResenja());
            }
            if (manualData.getBrojResenja() != null) {
                manualDataMap.put("BROJ_RESENJA", manualData.getBrojResenja());
            }
            if (manualData.getDatumOvlastenja() != null) {
                manualDataMap.put("DATUM_OVLASTENJA", manualData.getDatumOvlastenja());
            }
            if (manualData.getDatumPodnosenja() != null) {
                manualDataMap.put("DATUM_PODNOSENJA", manualData.getDatumPodnosenja());
            }
            if (manualData.getDodatniTekst() != null) {
                manualDataMap.put("DODATNI_TEKST", manualData.getDodatniTekst());
            }
            
            // Logički podaci
            if (manualData.getPribavljaDokumentaciju() != null) {
                manualDataMap.put("PRIBAVLJA_DOKUMENTACIJU", manualData.getPribavljaDokumentaciju().toString());
            }
            if (manualData.getVdPotpis() != null) {
                manualDataMap.put("VD_POTPIS", manualData.getVdPotpis().toString());
            }
            if (manualData.getSrPotpis() != null) {
                manualDataMap.put("SR_POTPIS", manualData.getSrPotpis().toString());
            }
        }
        return manualDataMap;
    }
}