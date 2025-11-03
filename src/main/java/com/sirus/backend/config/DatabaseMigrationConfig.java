package com.sirus.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Komponenta za automatsko pokretanje SQL migracija prilikom pokretanja aplikacije.
 * Ova komponenta će pokrenuti SQL skriptove koji su potrebni za rešavanje problema sa bazom podataka.
 */
@Component
public class DatabaseMigrationConfig implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Migracije su onemogućene - nema logovanja
    }

    private void runSqlScript(String scriptPath) throws IOException {
        System.out.println("Pokretanje SQL skripta: " + scriptPath);
        
        ClassPathResource resource = new ClassPathResource(scriptPath);
        
        // Proveri da li fajl postoji
        if (!resource.exists()) {
            System.err.println("✗ SQL skript ne postoji: " + scriptPath);
            return;
        }
        
        System.out.println("✓ SQL skript pronađen: " + scriptPath);
        String sql = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        // Podeli SQL na pojedinačne upite
        String[] queries = sql.split(";");
        
        for (String query : queries) {
            query = query.trim();
            if (!query.isEmpty() && !query.startsWith("--")) {
                try {
                    if (query.toUpperCase().startsWith("SELECT")) {
                        // Za SELECT upite, izvršavamo ih i prikazujemo rezultate
                        List<Map<String, Object>> results = jdbcTemplate.queryForList(query);
                        if (!results.isEmpty()) {
                            System.out.println("Rezultat upita: " + results);
                        }
                    } else {
                        // Za ostale upite (ALTER, CREATE, itd.)
                        jdbcTemplate.execute(query);
                        System.out.println("✓ Izvršen upit: " + query.substring(0, Math.min(50, query.length())) + "...");
                    }
                } catch (Exception e) {
                    System.err.println("✗ Greška pri izvršavanju upita: " + query.substring(0, Math.min(50, query.length())) + "...");
                    System.err.println("Greška: " + e.getMessage());
                }
            }
        }
    }
}
