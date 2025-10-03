package com.sirus.backend.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Konfiguracija za Excel import svih tabela u aplikaciji
 */
@Component
public class TableImportConfig {
    
    /**
     * Mapiranje tabela i njihovih kolona za Excel import
     */
    public static final Map<String, TableMapping> TABLE_MAPPINGS = new HashMap<>();
    
    static {
        // Users tabela (pojednostavljeno)
        TABLE_MAPPINGS.put("users", new TableMapping(
            "users",
            "User",
            new String[]{
                "username", "email", "firstName", "lastName"
            },
            new String[]{
                "Username", "Email", "First Name", "Last Name"
            }
        ));
        
        // EUK Kategorija tabela
        TABLE_MAPPINGS.put("euk.kategorija", new TableMapping(
            "euk.kategorija",
            "EukKategorija", 
            new String[]{"naziv"},
            new String[]{"Naziv"}
        ));
        
        // EUK Predmet tabela (pojednostavljeno)
        TABLE_MAPPINGS.put("euk.predmet", new TableMapping(
            "euk.predmet",
            "EukPredmet",
            new String[]{
                "naziv_predmeta"
            },
            new String[]{
                "Naziv predmeta"
            }
        ));
        
        // EUK Ugrozeno Lice T1 tabela
        TABLE_MAPPINGS.put("euk.ugrozeno_lice_t1", new TableMapping(
            "euk.ugrozeno_lice_t1",
            "EukUgrozenoLiceT1",
            new String[]{
                "redni_broj", "ime", "prezime", "jmbg", "ptt_broj", "grad_opstina",
                "mesto", "ulica_i_broj", "broj_clanova_domacinstva", "osnov_sticanja_statusa",
                "ed_broj_broj_mernog_uredjaja", "potrosnja_i_povrsina_combined", 
                "iznos_umanjenja_sa_pdv", "broj_racuna", "datum_izdavanja_racuna", "datum_trajanja_prava"
            },
            new String[]{
                "Redni broj", "Ime", "Prezime", "JMBG", "PTT broj", "Grad/Opština",
                "Mesto", "Ulica i broj", "Broj članova domaćinstva", "Osnov sticanja statusa",
                "ED broj/broj mernog uređaja", "Potrošnja i površina kombinovano",
                "Iznos umanjenja sa PDV", "Broj računa", "Datum izdavanja računa", "Datum trajanja prava"
            }
        ));
        
        // EUK Ugrozeno Lice T2 tabela
        TABLE_MAPPINGS.put("euk.ugrozeno_lice_t2", new TableMapping(
            "euk.ugrozeno_lice_t2",
            "EukUgrozenoLiceT2",
            new String[]{
                "redni_broj", "ime", "prezime", "jmbg", "ptt_broj", "grad_opstina",
                "mesto", "ulica_i_broj", "ed_broj", "pok_vazenja_resenja_o_statusu"
            },
            new String[]{
                "Redni broj", "Ime", "Prezime", "JMBG", "PTT broj", "Grad/Opština",
                "Mesto", "Ulica i broj", "ED broj", "Pokriće važenja rešenja"
            }
        ));
    }
    
    /**
     * Dohvatanje mapiranja za tabelu
     */
    public static TableMapping getTableMapping(String tableName) {
        return TABLE_MAPPINGS.get(tableName);
    }
    
    /**
     * Lista svih dostupnih tabela
     */
    public static String[] getAvailableTables() {
        return TABLE_MAPPINGS.keySet().toArray(new String[0]);
    }
    
    /**
     * Klasa za mapiranje tabele
     */
    public static class TableMapping {
        private final String tableName;
        private final String entityName;
        private final String[] columnNames;
        private final String[] displayNames;
        
        public TableMapping(String tableName, String entityName, String[] columnNames, String[] displayNames) {
            this.tableName = tableName;
            this.entityName = entityName;
            this.columnNames = columnNames;
            this.displayNames = displayNames;
        }
        
        public String getTableName() { return tableName; }
        public String getEntityName() { return entityName; }
        public String[] getColumnNames() { return columnNames; }
        public String[] getDisplayNames() { return displayNames; }
        public int getColumnCount() { return columnNames.length; }
    }
}
