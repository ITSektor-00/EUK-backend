# 🛣️ Rute Tabela - Pojednostavljena PostgreSQL Schema

## 📋 Pregled

Kreirao sam **pojednostavljenu** `rute` tabelu sa samo dve kolone za upravljanje rutama u EUK sistemu.

## 🏗️ **Struktura tabele:**

### **Samo dve kolone:**
- `naziv` - URL putanja rute (npr. `/admin/korisnici`) - **PRIMARY KEY**
- `zahteva_admin_role` - Da li ruta zahteva admin rolu (true/false)

## 🔌 **Kategorije ruta:**

### **1. ADMIN RUTE** (`/admin/**`) - **zahteva_admin_role = true**
- `/admin/korisnici` - Upravljanje korisnicima
- `/admin/korisnici/dodaj` - Dodavanje korisnika
- `/admin/korisnici/uredi/:id` - Uređivanje korisnika
- `/admin/korisnici/obrisi/:id` - Brisanje korisnika

### **2. EUK KATEGORIJE** (`/euk/kategorije/**`) - **zahteva_admin_role = false**
- `/euk/kategorije` - Pregled kategorija
- `/euk/kategorije/dodaj` - Dodavanje kategorije
- `/euk/kategorije/uredi/:id` - Uređivanje kategorije
- `/euk/kategorije/obrisi/:id` - Brisanje kategorije

### **3. EUK PREDMETI** (`/euk/predmeti/**`) - **zahteva_admin_role = false**
- `/euk/predmeti` - Pregled predmeta
- `/euk/predmeti/dodaj` - Dodavanje predmeta
- `/euk/predmeti/uredi/:id` - Uređivanje predmeta
- `/euk/predmeti/obrisi/:id` - Brisanje predmeta
- `/euk/predmeti/:id` - Detalji predmeta

### **4. EUK UGROŽENA LICA** (`/euk/ugrozena-lica/**`) - **zahteva_admin_role = false**
- `/euk/ugrozena-lica` - Pregled ugroženih lica
- `/euk/ugrozena-lica/dodaj` - Dodavanje ugroženog lica
- `/euk/ugrozena-lica/uredi/:id` - Uređivanje ugroženog lica
- `/euk/ugrozena-lica/obrisi/:id` - Brisanje ugroženog lica
- `/euk/ugrozena-lica/pretraga/:jmbg` - Pretraga po JMBG-u

### **5. EUK ŠTAMPANJE** (`/euk/stampanje/**`) - **zahteva_admin_role = false**
- `/euk/stampanje` - Glavna stranica za štampanje
- `/euk/stampanje/predmet/:id` - Štampanje predmeta
- `/euk/stampanje/kategorija/:id` - Štampanje po kategoriji
- `/euk/stampanje/izvestaj` - Štampanje izveštaja
- `/euk/stampanje/ugrozeno-lice/:id` - Štampanje ugroženog lica

### **6. API RUTE** (`/api/**`) - **zahteva_admin_role = false**
- `/api/users` - API za korisnike
- `/api/euk/kategorije` - API za EUK kategorije
- `/api/euk/predmeti` - API za EUK predmete
- `/api/euk/ugrozena-lica` - API za ugrožena lica

## 🚀 **Kako koristiti:**

### **1. Izvršite SQL fajl:**
```bash
# U PostgreSQL-u
psql -d your_database -f routes_simple.sql
```

### **2. Ili kopirajte i izvršite direktno:**
```sql
-- Kreiraj tabelu
CREATE TABLE IF NOT EXISTS rute (
    naziv VARCHAR(255) NOT NULL PRIMARY KEY,
    zahteva_admin_role BOOLEAN DEFAULT FALSE
);

-- Ubaci rute
INSERT INTO rute (naziv, zahteva_admin_role) VALUES
('/admin/korisnici', true),
('/euk/kategorije', false),
('/euk/predmeti', false),
('/euk/ugrozena-lica', false),
('/euk/stampanje', false);
```

## 🔍 **Korisni upiti:**

### **Sve rute:**
```sql
SELECT * FROM rute ORDER BY naziv;
```

### **Samo admin rute:**
```sql
SELECT * FROM rute WHERE zahteva_admin_role = true;
```

### **Samo obične rute:**
```sql
SELECT * FROM rute WHERE zahteva_admin_role = false;
```

### **Broj admin ruta:**
```sql
SELECT COUNT(*) FROM rute WHERE zahteva_admin_role = true;
```

### **Broj običnih ruta:**
```sql
SELECT COUNT(*) FROM rute WHERE zahteva_admin_role = false;
```

## 🔧 **Dodavanje novih ruta:**

```sql
-- Dodaj novu admin rutu
INSERT INTO rute (naziv, zahteva_admin_role) VALUES ('/admin/nova-funkcija', true);

-- Dodaj novu običnu rutu
INSERT INTO rute (naziv, zahteva_admin_role) VALUES ('/nova/ruta', false);
```

## 📊 **Prednosti pojednostavljene strukture:**

1. **Jednostavnost** - samo dve kolone
2. **Brza pretraga** - `naziv` je PRIMARY KEY
3. **Lako održavanje** - minimalna struktura
4. **Jasna logika** - true = admin, false = običan korisnik
5. **Efikasnost** - manje memorije i brže upite

## 🎯 **Zaključak:**

**Pojednostavljena `rute` tabela je spreman** za korišćenje! Sadrži sve potrebne rute sa:
- ✅ **Admin funkcionalnosti** (`zahteva_admin_role = true`)
- ✅ **EUK funkcionalnosti** (`zahteva_admin_role = false`)
- ✅ **API endpoint-e** (`zahteva_admin_role = false`)
- ✅ **Jednostavnu strukturu** (samo 2 kolone)

Možete je koristiti za kontrolu pristupa - admin rute za administratore, obične rute za sve korisnike! 🎉
