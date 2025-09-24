# 🚀 Instrukcije za kreiranje ugrozeno_lice_t2 tabele

## 📋 Pregled
Kreirana je nova tabela `ugrozeno_lice_t2` u EUK šemi sa specifikovanim kolonama i kompletnim backend API-jem.

## 🗄️ Struktura tabele
```sql
CREATE TABLE euk.ugrozeno_lice_t2 (
    ugrozeno_lice_id SERIAL PRIMARY KEY,
    redni_broj VARCHAR(20) NOT NULL,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    jmbg CHAR(13) UNIQUE NOT NULL,
    ptt_broj VARCHAR(10),
    grad_opstina VARCHAR(100),
    mesto VARCHAR(100),
    ulica_i_broj VARCHAR(200),
    ed_broj VARCHAR(100),
    pok_vazenja_resenja_o_statusu VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🛠️ Koraci za implementaciju

### 1. Kreiranje tabele u bazi
```bash
# Opcija 1: Batch fajl (Windows)
create_ugrozeno_lice_t2_table.bat

# Opcija 2: PowerShell (Windows)
.\create_ugrozeno_lice_t2_table.ps1

# Opcija 3: Manualno
psql -h localhost -U postgres -d euk_db -f postgresQuery/ugrozeno_lice_t2_table_creation.sql
```

### 2. Dodavanje test podataka (opciono)
```bash
psql -h localhost -U postgres -d euk_db -f postgresQuery/insert_test_data_ugrozeno_lice_t2.sql
```

### 3. Restart aplikacije
```bash
# Zaustavi aplikaciju (Ctrl+C)
# Zatim pokreni ponovo
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔗 API Endpointi

Base URL: `http://localhost:8080/api/ugrozeno-lice-t2`

### CRUD operacije
- `POST /api/ugrozeno-lice-t2` - Kreiranje novog ugroženog lica
- `GET /api/ugrozeno-lice-t2` - Lista svih sa paginacijom
- `GET /api/ugrozeno-lice-t2/{id}` - Pronalaženje po ID
- `PUT /api/ugrozeno-lice-t2/{id}` - Ažuriranje
- `DELETE /api/ugrozeno-lice-t2/{id}` - Brisanje

### Napredne pretrage
- `GET /api/ugrozeno-lice-t2/search` - Pretraga po kriterijumu
- `GET /api/ugrozeno-lice-t2/search/ime-prezime` - Pretraga po imenu i prezimenu
- `GET /api/ugrozeno-lice-t2/search/adresa` - Pretraga po adresi
- `GET /api/ugrozeno-lice-t2/search/energetski` - Pretraga po energetskim podacima

### Bulk operacije
- `POST /api/ugrozeno-lice-t2/bulk` - Kreiranje više zapisa odjednom
- `POST /api/ugrozeno-lice-t2/search/jmbg-list` - Pretraga po više JMBG-ova

## 📊 Test podaci

```json
{
  "redniBroj": "001",
  "ime": "Marko",
  "prezime": "Marković",
  "jmbg": "1234567890123",
  "pttBroj": "11000",
  "gradOpstina": "Beograd",
  "mesto": "Beograd",
  "ulicaIBroj": "Knez Mihailova 1",
  "edBroj": "ED123456",
  "pokVazenjaResenjaOStatusu": "01.01.2024 - 31.12.2024"
}
```

## ✅ Validacije

- `redniBroj` - obavezno, max 20 karaktera
- `ime` - obavezno, max 100 karaktera
- `prezime` - obavezno, max 100 karaktera
- `jmbg` - obavezno, tačno 13 cifara, jedinstveno
- `pttBroj` - opciono, max 10 karaktera
- `gradOpstina` - opciono, max 100 karaktera
- `mesto` - opciono, max 100 karaktera
- `ulicaIBroj` - opciono, max 200 karaktera
- `edBroj` - opciono, max 100 karaktera
- `pokVazenjaResenjaOStatusu` - opciono, max 200 karaktera

## 🐛 Rešavanje problema

### Problem: 400 Bad Request na DELETE
**Uzrok:** Tabela nije kreirana u bazi
**Rešenje:** Pokreni SQL skriptu za kreiranje tabele

### Problem: 404 Not Found
**Uzrok:** Aplikacija nije restartovana
**Rešenje:** Restartuj Spring Boot aplikaciju

### Problem: 500 Internal Server Error
**Uzrok:** Greška u kodu ili bazi
**Rešenje:** Proveri logove aplikacije

## 📁 Kreirani fajlovi

### Backend (Java)
- `src/main/java/com/sirus/backend/entity/EukUgrozenoLiceT2.java`
- `src/main/java/com/sirus/backend/dto/EukUgrozenoLiceT2Dto.java`
- `src/main/java/com/sirus/backend/repository/EukUgrozenoLiceT2Repository.java`
- `src/main/java/com/sirus/backend/service/EukUgrozenoLiceT2Service.java`
- `src/main/java/com/sirus/backend/controller/EukUgrozenoLiceT2Controller.java`

### Database
- `postgresQuery/ugrozeno_lice_t2_table_creation.sql`
- `postgresQuery/insert_test_data_ugrozeno_lice_t2.sql`

### Setup skripte
- `create_ugrozeno_lice_t2_table.bat`
- `create_ugrozeno_lice_t2_table.ps1`

## 🎯 Sledeći koraci

1. **Pokreni SQL skriptu** da kreiraš tabelu
2. **Restartuj aplikaciju** da se učitaju novi Java fajlovi
3. **Testiraj API endpointove** kroz Postman
4. **Implementiraj frontend** komponente
5. **Dodaj validacije** na frontend strani

## 📞 Podrška

Ako imaš problema:
1. Proveri da li je tabela kreirana u bazi
2. Proveri da li je aplikacija restartovana
3. Proveri logove aplikacije za greške
4. Testiraj endpointove kroz Postman

**Sve je spremno za korišćenje!** 🚀
