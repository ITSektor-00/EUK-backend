# Template Generation System - Setup Instructions

## Pregled

Kompletan sistem za generisanje template rešenja sa hijerarhijskim izborom parametara.

## Setup koraci

### 1. Baza podataka

Pokrenuti SQL skripte u redosledu:

```sql
-- 1. Kreiranje tabela i podataka
\i postgresQuery/setup_template_generation_system.sql

-- Ili pojedinačno:
\i postgresQuery/create_obrasci_vrste_table.sql
\i postgresQuery/create_organizaciona_struktura_table.sql
\i postgresQuery/add_file_path_to_predmeti.sql
```

### 2. Java kod

Svi potrebni fajlovi su kreirani:

#### Entity klase
- `EukObrasciVrste.java`
- `EukOrganizacionaStruktura.java`
- Ažuriran `EukPredmet.java` sa template kolonama

#### Repository klase
- `EukObrasciVrsteRepository.java`
- `EukOrganizacionaStrukturaRepository.java`

#### Service klase
- `EukObrasciVrsteService.java`
- `EukOrganizacionaStrukturaService.java`
- `TemplateGenerationService.java`

#### Controller
- `TemplateGenerationController.java`

#### DTO klase
- `TemplateGenerationRequestDto.java`
- `TemplateGenerationResponseDto.java`

### 3. Pokretanje aplikacije

```bash
# Kompajliranje
mvn clean compile

# Pokretanje
mvn spring-boot:run
```

### 4. Testiranje

#### Test generisanja template-a
```bash
curl -X POST http://localhost:8080/api/template/generate \
  -H "Content-Type: application/json" \
  -d '{
    "liceId": 1,
    "liceTip": "t1",
    "kategorijaId": 1,
    "obrasciVrsteId": 1,
    "organizacionaStrukturaId": 1,
    "predmetId": 1
  }'
```

#### Test dobijanja obrasci vrste
```bash
curl http://localhost:8080/api/template/obrasci-vrste
```

#### Test dobijanja organizaciona struktura
```bash
curl http://localhost:8080/api/template/organizaciona-struktura
```

## API Endpoints

### Template Generation
- `POST /api/template/generate` - Generisanje template-a

### Obrasci Vrste
- `GET /api/template/obrasci-vrste` - Svi obrasci vrste
- `GET /api/template/obrasci-vrste/{id}` - Obrasci vrste po ID
- `POST /api/template/obrasci-vrste` - Kreiranje novog
- `PUT /api/template/obrasci-vrste/{id}` - Ažuriranje
- `DELETE /api/template/obrasci-vrste/{id}` - Brisanje

### Organizaciona Struktura
- `GET /api/template/organizaciona-struktura` - Sve organizacione strukture
- `GET /api/template/organizaciona-struktura/{id}` - Organizaciona struktura po ID
- `POST /api/template/organizaciona-struktura` - Kreiranje nove
- `PUT /api/template/organizaciona-struktura/{id}` - Ažuriranje
- `DELETE /api/template/organizaciona-struktura/{id}` - Brisanje

## Struktura fajlova

```
templates/
├── template_1_1234567890.txt
├── template_2_1234567891.txt
└── ...
```

## Napomene

1. **Fizički fajlovi** se čuvaju u `templates/` direktorijumu
2. **Cloud storage** integracija će biti dodana kasnije
3. **Validacija** se vrši na svim nivoima
4. **Error handling** je implementiran
5. **Transaction management** je konfigurisan

## Frontend integracija

Pogledati `TEMPLATE_GENERATION_SYSTEM.md` za detaljne instrukcije za frontend tim.

## Troubleshooting

### Česte greške

1. **Template fajl se ne kreira**
   - Proveriti da li `templates/` direktorijum postoji
   - Proveriti dozvole za pisanje

2. **Database connection greške**
   - Proveriti database connection string
   - Proveriti da li su tabele kreirane

3. **Entity not found greške**
   - Proveriti da li svi entiteti postoje u bazi
   - Proveriti ID-jeve u request-u

### Logovi

```bash
# Proveriti logove aplikacije
tail -f logs/application.log
```

## Buduća poboljšanja

1. Cloud storage integracija
2. PDF export
3. Email notifications
4. Template versioning
5. Advanced template editor
