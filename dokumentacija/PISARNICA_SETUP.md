# Setup Instrukcije za Pisarnica API

## Pregled
Kompletni API za upravljanje predmetima u pisarnici sa optimizacijom za velike baze podataka (1M+ zapisa).

## Kreirane Komponente

### 1. Entity
- `Predmet.java` - Glavna entity klasa sa svim kolonama iz tabele

### 2. DTO Klase
- `PredmetDto.java` - DTO za response
- `PredmetFilterRequest.java` - DTO za filter request

### 3. Repository
- `PredmetRepository.java` - Repository sa optimizovanim upitima

### 4. Service
- `PredmetService.java` - Business logika sa optimizacijama

### 5. Controller
- `PisarnicaController.java` - REST endpointi

### 6. Konfiguracija
- `env.example` - Environment varijable
- `optimize_predmet_table.sql` - SQL indeksi za optimizaciju

## Setup Koraci

### 1. Environment Varijable
Kopiraj sadržaj iz `env.example` u `.env` fajl i prilagodi vrednosti:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/euk_database
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JPA Configuration
JPA_HIBERNATE_DDL_AUTO=update
JPA_SHOW_SQL=false

# Server Configuration
SERVER_PORT=8080

# Security Configuration
JWT_SECRET=your_jwt_secret_key_here_make_it_long_and_secure
JWT_EXPIRATION=86400000

# CORS Configuration
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:3001,http://127.0.0.1:3000
```

### 2. Database Setup
⚠️ **VAŽNO: Ova baza ima samo READ privilegije!**

Ne možeš kreirati indekse ili menjati strukturu baze. Optimizacija se oslanja na postojeće indekse.

```sql
-- SQL skripte su dostupne u postgresQuery/optimize_predmet_table_sqlserver.sql
-- ali se NE MOGU pokrenuti zbog READ ONLY privilegija
-- Kontaktiraj database administratora za optimizaciju indeksa
```

### 3. Application Properties
Dodaj u `application.properties`:

```properties
# Database Configuration - SQL Server
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=${DB_DRIVER}

# JPA Configuration - READ ONLY DATABASE
spring.jpa.hibernate.ddl-auto=${JPA_HIBERNATE_DDL_AUTO}
spring.jpa.show-sql=${JPA_SHOW_SQL}
spring.jpa.properties.hibernate.dialect=${JPA_PROPERTIES_HIBERNATE_DIALECT}
spring.jpa.properties.hibernate.format_sql=${JPA_PROPERTIES_HIBERNATE_FORMAT_SQL}

# Server Configuration
server.port=${SERVER_PORT}
server.servlet.context-path=${SERVER_SERVLET_CONTEXT_PATH}

# Security Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# CORS Configuration
cors.allowed.origins=${CORS_ALLOWED_ORIGINS}

# Performance Configuration - READ ONLY DATABASE
spring.jpa.properties.hibernate.jdbc.batch_size=${SPRING_JPA_PROPERTIES_HIBERNATE_JDBC_BATCH_SIZE}
spring.jpa.properties.hibernate.order_inserts=${SPRING_JPA_PROPERTIES_HIBERNATE_ORDER_INSERTS}
spring.jpa.properties.hibernate.order_updates=${SPRING_JPA_PROPERTIES_HIBERNATE_ORDER_UPDATES}

# Connection Pool Configuration
spring.datasource.hikari.maximum-pool-size=${SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE}
spring.datasource.hikari.minimum-idle=${SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE}
spring.datasource.hikari.idle-timeout=${SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT}
spring.datasource.hikari.max-lifetime=${SPRING_DATASOURCE_HIKARI_MAX_LIFETIME}
spring.datasource.hikari.connection-timeout=${SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT}
```

### 4. Pokretanje Aplikacije
```bash
# Maven
mvn spring-boot:run

# Ili direktno
java -jar target/sirus-backend-0.0.1-SNAPSHOT.jar
```

## Testiranje API-ja

### 1. Health Check
```bash
curl -X GET "http://localhost:8080/api/pisarnica/health"
```

### 2. Osnovna Pretraga
```bash
curl -X GET "http://localhost:8080/api/pisarnica/filter?searchTerm=Marko&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Napredno Filtriranje (POST)
```bash
curl -X POST "http://localhost:8080/api/pisarnica/filter" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "searchTerm": "Marko",
    "godina": 2024,
    "idOrganOj": 1,
    "page": 0,
    "size": 20,
    "sortBy": "datumPredmeta",
    "sortDirection": "desc"
  }'
```

### 4. Filter Opcije
```bash
curl -X GET "http://localhost:8080/api/pisarnica/filter-options" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 5. Statistike
```bash
curl -X GET "http://localhost:8080/api/pisarnica/statistics" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Optimizacije za Velike Baze

### 1. Indeksi
Pokreni `optimize_predmet_table.sql` za kreiranje indeksa.

### 2. Connection Pool
Konfigurisan za 20 maksimalnih konekcija.

### 3. JPA Optimizacije
- Batch size: 20
- Order inserts: true
- Order updates: true

### 4. Paginacija
- Default: 20 zapisa po strani
- Maksimalno: 100 zapisa po strani

## Monitoring

### 1. Actuator Endpoints
```
GET /actuator/health
GET /actuator/info
GET /actuator/metrics
```

### 2. Database Monitoring
```sql
-- Provera korišćenja indeksa
SELECT 
    schemaname,
    tablename,
    indexname,
    idx_scan,
    idx_tup_read,
    idx_tup_fetch
FROM pg_stat_user_indexes 
WHERE tablename = 'predmet'
ORDER BY idx_scan DESC;

-- Provera veličine tabele
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE tablename = 'predmet';
```

## Troubleshooting

### 1. Performance Problemi
- Proveri da li su indeksi kreirani
- Proveri connection pool konfiguraciju
- Proveri JPA batch size

### 2. Memory Issues
- Povećaj heap size: `-Xmx2g`
- Povećaj connection pool size
- Optimizuj JPA queries

### 3. Database Connection
- Proveri database URL
- Proveri credentials
- Proveri network connectivity

## Frontend Integracija

### 1. Base URL
```
http://localhost:8080/api/pisarnica
```

### 2. Autentifikacija
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer ' + token
};
```

### 3. Primjer Korišćenja
```javascript
// Filtriranje predmeta
const response = await fetch('/api/pisarnica/filter', {
  method: 'POST',
  headers: headers,
  body: JSON.stringify({
    searchTerm: 'Marko',
    godina: 2024,
    page: 0,
    size: 20
  })
});

const data = await response.json();
console.log(data);
```

## Kontakt

Za dodatna pitanja ili podršku, kontaktiraj development tim.
