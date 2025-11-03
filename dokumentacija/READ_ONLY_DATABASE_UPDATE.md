# Ažuriranje za READ ONLY SQL Server Bazu

## ⚠️ Važne Promene

### 1. Database Konfiguracija
- **Baza:** Microsoft SQL Server (READ ONLY privilegije)
- **Driver:** `com.microsoft.sqlserver.jdbc.SQLServerDriver`
- **DDL:** `none` (ne može se menjati struktura)
- **Indeksi:** Ne mogu se kreirati (samo READ privilegije)

### 2. Ažurirani Fajlovi

#### Entity (Predmet.java)
- ✅ Dodati `BigDecimal` za `Vrednost_Takse` (money tip)
- ✅ Dodati `Boolean` za `Popis_Akata` (bit tip)
- ✅ Dodati length ograničenja za string polja
- ✅ Dodati precision/scale za decimal polja

#### DTO Klase
- ✅ `PredmetDto.java` - ažuriran sa `BigDecimal` i `Boolean`
- ✅ `PredmetFilterRequest.java` - ažuriran sa `BigDecimal` za range filtere

#### Konfiguracija
- ✅ `env.example` - SQL Server konfiguracija
- ✅ `pom.xml` - SQL Server driver dependency
- ✅ `application.properties` - READ ONLY mode

### 3. SQL Server Specifičnosti

#### Tipovi Podataka
```java
// Entity mapiranje
@Column(name = "Vrednost_Takse", precision = 19, scale = 4)
private BigDecimal vrednostTakse;

@Column(name = "Popis_Akata")
private Boolean popisAkata;

@Column(name = "Tip_Podnosioca", length = 2)
private String tipPodnosioca;

@Column(name = "Naziv_Podnosioca", length = 200)
private String nazivPodnosioca;
```

#### Connection String
```
jdbc:sqlserver://localhost:1433;databaseName=euk_database;encrypt=true;trustServerCertificate=true
```

### 4. READ ONLY Ograničenja

#### Što NE MOŽEŠ:
- ❌ Kreirati indekse
- ❌ Menjati strukturu tabele
- ❌ Dodavati kolone
- ❌ Kreirati view-ove
- ❌ Kreirati stored procedure

#### Što MOŽEŠ:
- ✅ Čitati podatke
- ✅ Filtrirati podatke
- ✅ Sortirati podatke
- ✅ Paginirati rezultate
- ✅ Optimizovati upite

### 5. Optimizacija za READ ONLY

#### Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
```

#### JPA Optimizacije
```properties
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=false
spring.jpa.properties.hibernate.order_updates=false
```

### 6. Preporučene Optimizacije

#### Za Database Administratora:
```sql
-- Preporučeni indeksi za performanse
CREATE INDEX idx_predmet_id_organ_oj ON predmet(ID_Organ_OJ);
CREATE INDEX idx_predmet_godina ON predmet(Godina);
CREATE INDEX idx_predmet_datum_predmeta ON predmet(Datum_Predmeta);
CREATE INDEX idx_predmet_naziv_podnosioca ON predmet(Naziv_Podnosioca);
CREATE INDEX idx_predmet_maticni_broj ON predmet(Maticni_Broj);
CREATE INDEX idx_predmet_pib ON predmet(PIB);
```

#### Za Aplikaciju:
- ✅ Paginacija (max 100 zapisa po strani)
- ✅ Optimizovani upiti
- ✅ Connection pooling
- ✅ Caching (može se dodati)

### 7. Testiranje

#### Health Check
```bash
curl -X GET "http://localhost:8080/api/pisarnica/health"
```

#### Osnovna Pretraga
```bash
curl -X GET "http://localhost:8080/api/pisarnica/filter?searchTerm=Marko&page=0&size=20"
```

#### Napredno Filtriranje
```bash
curl -X POST "http://localhost:8080/api/pisarnica/filter" \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "Marko",
    "godina": 2024,
    "page": 0,
    "size": 20
  }'
```

### 8. Troubleshooting

#### Performance Problemi
- Proveri da li postoje indeksi u bazi
- Kontaktiraj database administratora
- Optimizuj upite u aplikaciji

#### Connection Problemi
- Proveri SQL Server URL
- Proveri credentials
- Proveri network connectivity

#### Memory Issues
- Povećaj heap size: `-Xmx2g`
- Povećaj connection pool size
- Optimizuj JPA queries

### 9. Kontakt

Za dodatna pitanja ili podršku, kontaktiraj development tim.

## ✅ Sve je spremno za READ ONLY SQL Server bazu!
