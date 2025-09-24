# Migration Summary - EUK Ugrožena Lica T1

## ✅ ZAVRŠENO

Sve je uspešno migrirano sa stare `ugrozeno_lice` tabele na novu `ugrozeno_lice_t1` tabelu.

## 📁 Kreirani fajlovi

### Backend (Java):
1. **Entity**: `src/main/java/com/sirus/backend/entity/EukUgrozenoLiceT1.java`
2. **DTO**: `src/main/java/com/sirus/backend/dto/EukUgrozenoLiceT1Dto.java`
3. **Repository**: `src/main/java/com/sirus/backend/repository/EukUgrozenoLiceT1Repository.java`
4. **Service**: `src/main/java/com/sirus/backend/service/EukUgrozenoLiceT1Service.java`
5. **Controller**: `src/main/java/com/sirus/backend/controller/EukUgrozenoLiceT1Controller.java`

### SQL:
6. **Database Script**: `ugrozeno_lice_t1_table_recreation.sql`

### Documentation:
7. **Java Instructions**: `java_entity_update_instructions_t1.md`
8. **Frontend Instructions**: `FRONTEND_MIGRATION_INSTRUCTIONS_T1.md`

## 🗑️ Obrisani fajlovi

### Stari backend fajlovi (OBRISANI):
- `src/main/java/com/sirus/backend/entity/EukUgrozenoLice.java` ❌
- `src/main/java/com/sirus/backend/dto/EukUgrozenoLiceDto.java` ❌
- `src/main/java/com/sirus/backend/service/EukUgrozenoLiceService.java` ❌
- `src/main/java/com/sirus/backend/repository/EukUgrozenoLiceRepository.java` ❌
- `src/main/java/com/sirus/backend/controller/EukUgrozenoLiceController.java` ❌

## 🆕 Nova struktura tabele

### Tabela: `euk.ugrozeno_lice_t1`

| Kolona | Tip | Opis |
|--------|-----|------|
| `ugrozeno_lice_id` | SERIAL PRIMARY KEY | Automatski ID |
| `redni_broj` | VARCHAR(20) NOT NULL | Redni broj u evidenciji |
| `ime` | VARCHAR(100) NOT NULL | Ime |
| `prezime` | VARCHAR(100) NOT NULL | Prezime |
| `jmbg` | CHAR(13) UNIQUE NOT NULL | JMBG |
| `ptt_broj` | VARCHAR(10) | PTT broj |
| `grad_opstina` | VARCHAR(100) | Grad/Opština |
| `mesto` | VARCHAR(100) | Mesto |
| `ulica_i_broj` | VARCHAR(200) | Ulica i broj |
| `broj_clanova_domacinstva` | INTEGER | Broj članova domaćinstva |
| `osnov_sticanja_statusa` | VARCHAR(50) | Osnov sticanja statusa (MP, NSP, DD, UDTNP) |
| `ed_broj_broj_mernog_uredjaja` | VARCHAR(100) | ED broj/broj mernog uređaja za gas/šifra korisnika |
| `potrosnja_kwh` | NUMERIC(10,2) | Potrošnja u kWh |
| `zagrevana_povrsina_m2` | NUMERIC(10,2) | Zagrevana površina u m² |
| `iznos_umanjenja_sa_pdv` | NUMERIC(12,2) | Iznos umanjenja sa PDV |
| `broj_racuna` | VARCHAR(50) | Broj računa |
| `datum_izdavanja_racuna` | DATE | Datum izdavanja računa |
| `created_at` | TIMESTAMP | Datum kreiranja |
| `updated_at` | TIMESTAMP | Datum ažuriranja |

## 🔌 Novi API endpoint-i

### Osnovne operacije:
- `GET /api/euk/ugrozena-lica-t1` - Lista sa paginacijom
- `GET /api/euk/ugrozena-lica-t1/{id}` - Dohvatanje po ID-u
- `POST /api/euk/ugrozena-lica-t1` - Kreiranje
- `PUT /api/euk/ugrozena-lica-t1/{id}` - Ažuriranje
- `DELETE /api/euk/ugrozena-lica-t1/{id}` - Brisanje

### Pretrage:
- `GET /api/euk/ugrozena-lica-t1/search/jmbg/{jmbg}` - Pretraga po JMBG-u
- `GET /api/euk/ugrozena-lica-t1/search/redni-broj/{redniBroj}` - Pretraga po rednom broju
- `GET /api/euk/ugrozena-lica-t1/search/name` - Pretraga po imenu i prezimenu
- `POST /api/euk/ugrozena-lica-t1/search/filters` - Kompleksna pretraga sa filterima

### Statistike:
- `GET /api/euk/ugrozena-lica-t1/statistics` - Statistike
- `GET /api/euk/ugrozena-lica-t1/count` - Broj zapisa
- `GET /api/euk/ugrozena-lica-t1/test` - Test endpoint

## 🚀 Sledeći koraci

### 1. Pokretanje backend-a:
```bash
# Pokrenuti SQL skript
psql -d your_database -f ugrozeno_lice_t1_table_recreation.sql

# Pokrenuti Spring Boot aplikaciju
mvn spring-boot:run
```

### 2. Testiranje endpoint-a:
```bash
# Test endpoint
curl http://localhost:8080/api/euk/ugrozena-lica-t1/test

# Lista svih zapisa
curl http://localhost:8080/api/euk/ugrozena-lica-t1?page=0&size=10
```

### 3. Frontend migracija:
- Pratiti instrukcije u `FRONTEND_MIGRATION_INSTRUCTIONS_T1.md`
- Zameniti sve stare endpoint-e sa novim
- Ažurirati strukture podataka
- Dodati nova polja u forme

## ⚠️ VAŽNE NAPOMENE

1. **STARI ENDPOINT-I VIŠE NE POSTOJE** - `/api/euk/ugrozena-lica/*`
2. **KORISTITI NOVE ENDPOINT-E** - `/api/euk/ugrozena-lica-t1/*`
3. **NOVA STRUKTURA PODATAKA** - ažurirati frontend
4. **NEMA RELACIJE SA PREDMET TABELOM** - uklonjena je veza
5. **NOVA POLJA** - dodati sva nova polja u forme

## 📊 Ključne razlike

| Aspekt | Stara verzija | Nova verzija |
|--------|---------------|--------------|
| Tabela | `ugrozeno_lice` | `ugrozeno_lice_t1` |
| Schema | `EUK` | `euk` |
| Endpoint | `/api/euk/ugrozena-lica` | `/api/euk/ugrozena-lica-t1` |
| Relacija | Ima vezu sa `predmet` | Nema vezu sa `predmet` |
| Polja | Osnovne informacije | Energetski i finansijski podaci |
| Pretraga | Osnovna | Napredna sa filterima |

## ✅ Checklist

- [x] Kreiran SQL skript za novu tabelu
- [x] Kreiran novi Entity
- [x] Kreiran novi DTO
- [x] Kreiran novi Repository
- [x] Kreiran novi Service
- [x] Kreiran novi Controller
- [x] Obrisani stari fajlovi
- [x] Kreirane instrukcije za frontend
- [x] Dokumentovane sve promene

## 🎯 Status: GOTOVO

Backend je potpuno migriran i spreman za korišćenje. Frontend tim može početi sa migracijom prema instrukcijama.
