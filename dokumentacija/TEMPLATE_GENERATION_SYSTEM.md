# Template Generation System - Frontend Implementation Guide

## Pregled sistema

Sistem za generisanje template rešenja omogućava hijerarhijski izbor parametara za kreiranje personalizovanih rešenja. Tok je sledeći:

1. **Izbor lice** (iz t1 ili t2 tabele)
2. **Izbor kategorije**
3. **Izbor obrasci_vrste**
4. **Izbor organizaciona_struktura**
5. **Generisanje template-a**

## Baza podataka

### Nove tabele

#### euk.obrasci_vrste
```sql
CREATE TABLE euk.obrasci_vrste (
    id SERIAL PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL UNIQUE,
    opis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Podaci:**
- negativno
- neograniceno
- ograniceno
- borci
- penzioneri
- obustave

#### euk.organizaciona_struktura
```sql
CREATE TABLE euk.organizaciona_struktura (
    id SERIAL PRIMARY KEY,
    naziv VARCHAR(100) NOT NULL UNIQUE,
    opis TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Podaci:**
- sekretar
- podsekretar

#### Ažuriranje euk.predmeti
```sql
ALTER TABLE euk.predmeti 
ADD COLUMN template_file_path VARCHAR(500),
ADD COLUMN template_generated_at TIMESTAMP,
ADD COLUMN template_status VARCHAR(50) DEFAULT 'pending';
```

## API Endpoints

### Base URL
```
/api/template
```

### 1. Generisanje Template-a

**POST** `/api/template/generate`

**Request Body:**
```json
{
    "liceId": 1,
    "liceTip": "t1", // ili "t2"
    "kategorijaId": 1,
    "obrasciVrsteId": 1,
    "organizacionaStrukturaId": 1,
    "predmetId": 1
}
```

**Response:**
```json
{
    "predmetId": 1,
    "templateFilePath": "templates/template_1_1234567890.txt",
    "templateStatus": "generated",
    "templateGeneratedAt": "2024-01-15T10:30:00",
    "message": "Template je uspešno generisan",
    "success": true
}
```

### 2. Obrasci Vrste

#### Dobijanje svih obrasci vrste
**GET** `/api/template/obrasci-vrste`

**Response:**
```json
[
    {
        "id": 1,
        "naziv": "negativno",
        "opis": "Negativni obrasci",
        "createdAt": "2024-01-15T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
    },
    // ... ostali obrasci
]
```

#### Dobijanje obrasci vrste po ID
**GET** `/api/template/obrasci-vrste/{id}`

#### Kreiranje nove obrasci vrste
**POST** `/api/template/obrasci-vrste`

**Request Body:**
```json
{
    "naziv": "novi_obrasci",
    "opis": "Opis novih obraza"
}
```

#### Ažuriranje obrasci vrste
**PUT** `/api/template/obrasci-vrste/{id}`

#### Brisanje obrasci vrste
**DELETE** `/api/template/obrasci-vrste/{id}`

### 3. Organizaciona Struktura

#### Dobijanje svih organizaciona struktura
**GET** `/api/template/organizaciona-struktura`

**Response:**
```json
[
    {
        "id": 1,
        "naziv": "sekretar",
        "opis": "Sekretar",
        "createdAt": "2024-01-15T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
    },
    {
        "id": 2,
        "naziv": "podsekretar",
        "opis": "Podsekretar",
        "createdAt": "2024-01-15T10:00:00",
        "updatedAt": "2024-01-15T10:00:00"
    }
]
```

#### Ostali endpoints (GET, POST, PUT, DELETE)
**GET** `/api/template/organizaciona-struktura/{id}`
**POST** `/api/template/organizaciona-struktura`
**PUT** `/api/template/organizaciona-struktura/{id}`
**DELETE** `/api/template/organizaciona-struktura/{id}`

## Frontend Implementation

### 1. Komponente koje treba kreirati

#### TemplateGenerationForm
- Forma sa hijerarhijskim izborom
- Validacija na svakom koraku
- Progress indicator

#### ObrasciVrsteList
- Lista svih obrasci vrste
- Mogućnost dodavanja/uređivanja/brisanja

#### OrganizacionaStrukturaList
- Lista svih organizaciona struktura
- Mogućnost dodavanja/uređivanja/brisanja

### 2. Stepeni implementacije

#### Korak 1: Izbor lice
```typescript
interface LiceSelection {
    liceId: number;
    liceTip: 't1' | 't2';
    liceNaziv: string;
}
```

#### Korak 2: Izbor kategorije
```typescript
interface KategorijaSelection {
    kategorijaId: number;
    kategorijaNaziv: string;
}
```

#### Korak 3: Izbor obrasci vrste
```typescript
interface ObrasciVrsteSelection {
    obrasciVrsteId: number;
    obrasciVrsteNaziv: string;
}
```

#### Korak 4: Izbor organizaciona struktura
```typescript
interface OrganizacionaStrukturaSelection {
    organizacionaStrukturaId: number;
    organizacionaStrukturaNaziv: string;
}
```

### 3. API Service funkcije

```typescript
// Template generation service
export class TemplateGenerationService {
    
    // Generisanje template-a
    async generateTemplate(request: TemplateGenerationRequest): Promise<TemplateGenerationResponse> {
        const response = await fetch('/api/template/generate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(request)
        });
        return response.json();
    }
    
    // Obrasci vrste
    async getObrasciVrste(): Promise<ObrasciVrste[]> {
        const response = await fetch('/api/template/obrasci-vrste');
        return response.json();
    }
    
    async createObrasciVrste(obrasciVrste: ObrasciVrste): Promise<ObrasciVrste> {
        const response = await fetch('/api/template/obrasci-vrste', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(obrasciVrste)
        });
        return response.json();
    }
    
    // Organizaciona struktura
    async getOrganizacionaStruktura(): Promise<OrganizacionaStruktura[]> {
        const response = await fetch('/api/template/organizaciona-struktura');
        return response.json();
    }
    
    async createOrganizacionaStruktura(organizacionaStruktura: OrganizacionaStruktura): Promise<OrganizacionaStruktura> {
        const response = await fetch('/api/template/organizaciona-struktura', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(organizacionaStruktura)
        });
        return response.json();
    }
}
```

### 4. TypeScript interfejsi

```typescript
interface TemplateGenerationRequest {
    liceId: number;
    liceTip: 't1' | 't2';
    kategorijaId: number;
    obrasciVrsteId: number;
    organizacionaStrukturaId: number;
    predmetId: number;
}

interface TemplateGenerationResponse {
    predmetId: number;
    templateFilePath: string;
    templateStatus: string;
    templateGeneratedAt: string;
    message: string;
    success: boolean;
}

interface ObrasciVrste {
    id: number;
    naziv: string;
    opis: string;
    createdAt: string;
    updatedAt: string;
}

interface OrganizacionaStruktura {
    id: number;
    naziv: string;
    opis: string;
    createdAt: string;
    updatedAt: string;
}
```

### 5. UI/UX preporuke

#### Stepper komponenta
```typescript
const steps = [
    { title: 'Izbor lice', description: 'Izaberite lice iz t1 ili t2 tabele' },
    { title: 'Izbor kategorije', description: 'Izaberite kategoriju' },
    { title: 'Obrasci vrste', description: 'Izaberite tip obraza' },
    { title: 'Organizaciona struktura', description: 'Izaberite organizacionu strukturu' },
    { title: 'Generisanje', description: 'Generiši template' }
];
```

#### Validacija
- Proveriti da li su svi koraci završeni pre generisanja
- Prikazati greške ako neki entitet ne postoji
- Loading state tokom generisanja

#### Rezultat
- Prikazati status generisanja
- Link za download generisanog fajla
- Mogućnost ponovnog generisanja

## SQL skripte za setup

### 1. Kreiranje tabela
```sql
-- Pokrenuti u redosledu:
-- 1. postgresQuery/create_obrasci_vrste_table.sql
-- 2. postgresQuery/create_organizaciona_struktura_table.sql
-- 3. postgresQuery/add_file_path_to_predmeti.sql
```

### 2. Test podaci
```sql
-- Obrasci vrste se automatski insertuju
-- Organizaciona struktura se automatski insertuje
```

## Napomene za implementaciju

1. **Validacija**: Proveriti da li svi entiteti postoje pre generisanja
2. **Error handling**: Prikazati korisne greške korisniku
3. **Loading states**: Prikazati loading tokom API poziva
4. **File management**: Fizički fajlovi se čuvaju u `templates/` direktorijumu
5. **Cloud storage**: Kasnije će se integrisati sa external cloud storage-om
6. **Security**: Dodati autentifikaciju i autorizaciju prema potrebi

## Testiranje

### Manualno testiranje
1. Kreirati test predmet
2. Izabrati lice, kategoriju, obrasci vrste, organizaciona struktura
3. Generisati template
4. Proveriti da li je fajl kreiran
5. Proveriti da li je predmet ažuriran sa putanjom fajla

### API testiranje
```bash
# Test generisanja template-a
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

## Buduća poboljšanja

1. **Cloud storage integracija**
2. **Template caching**
3. **Batch generisanje**
4. **Template versioning**
5. **Advanced template editor**
6. **PDF export**
7. **Email notifications**
