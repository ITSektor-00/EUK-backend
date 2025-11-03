# pgAdmin Setup - Povezivanje na PostgreSQL

## 1. Pristup pgAdmin-u

pgAdmin je dostupan na:
```
http://localhost:5050
```

## 2. Login podaci

**Email:** `admin@admin.com` (ili vrednost iz `PGADMIN_EMAIL` environment varijable)  
**Password:** `admin` (ili vrednost iz `PGADMIN_PASSWORD` environment varijable)

**Napomena:** Ako niste promenili vrednosti u `.env` fajlu, koristite podrazumevane vrednosti gore.

## 3. Dodavanje PostgreSQL Server-a u pgAdmin

### Korak 1: Otvorite pgAdmin
1. Idite na `http://localhost:5050` u browser-u
2. Ulogujte se sa email-om i password-om

### Korak 2: Dodajte novi server
1. Desni klik na **"Servers"** u levoj strani
2. Izaberite **"Register" → "Server..."**

### Korak 3: General tab
- **Name:** `EUK PostgreSQL` (ili bilo koje ime koje želite)

### Korak 4: Connection tab
- **Host name/address:** `postgres` (Docker service name) ili `localhost` ako pristupate spoljašnjom portu
- **Port:** `5432`
- **Maintenance database:** `euk_database` (ili vrednost iz `POSTGRES_DB`)
- **Username:** `euk_user` (ili vrednost iz `POSTGRES_USER`)
- **Password:** `euk_password_2024` (ili vrednost iz `POSTGRES_PASSWORD`)

**⚠️ VAŽNO:** 
- Ako ste u Docker kontejneru (pgAdmin kontejner), koristite **`postgres`** kao host name (Docker service name)
- Ako pristupate sa host mašine, koristite **`localhost`**

### Korak 5: Save
- Kliknite **"Save"** da sačuvate konekciju

## 4. Provera konekcije

Nakon što dodate server:
1. Proširite server u levoj strani
2. Proširite **"Databases"**
3. Trebalo bi da vidite **`euk_database`** bazu
4. Proširite **`euk_database`**
5. Trebalo bi da vidite **"Schemas"** → **"euk"** sa svim tabelama

## 5. Podrazumevani konekcioni podaci

Ako niste kreirali `.env` fajl, podrazumevane vrednosti su:

```env
# PostgreSQL
POSTGRES_DB=euk_database
POSTGRES_USER=euk_user
POSTGRES_PASSWORD=euk_password_2024
POSTGRES_PORT=5432

# pgAdmin
PGADMIN_EMAIL=admin@admin.com
PGADMIN_PASSWORD=admin
PGADMIN_PORT=5050
```

## 6. Ako imate probleme sa konekcijom

### Problem: "Could not connect to server" ili 401 UNAUTHORIZED
**Rešenje:**
1. **Proverite da li su SVI kontejneri pokrenuti:**
   ```bash
   docker-compose ps
   ```
   Ako neki kontejner nije pokrenut:
   ```bash
   docker-compose up -d
   ```
   
2. **Sačekajte da PostgreSQL kontejner bude "healthy":**
   - Može potrajati nekoliko sekundi dok se baza podataka inicijalizuje
   - Status bi trebalo da bude "Up X seconds (healthy)" a ne samo "Up X seconds"
   
3. Proverite da koristite ispravan host name:
   - **Iz pgAdmin kontejnera:** koristite `postgres` (Docker service name)
   - **Sa host mašine:** koristite `localhost`
   
4. Proverite da li je port 5432 dostupan

### Problem: "Password authentication failed"
**Rešenje:**
1. Proverite da li koristite ispravne kredencijale iz `.env` fajla
2. Ako nema `.env` fajla, koristite podrazumevane vrednosti:
   - Username: `euk_user`
   - Password: `euk_password_2024`
   - Database: `euk_database`

### Problem: "Database does not exist"
**Rešenje:**
1. Proverite ime baze u `.env` fajlu (`POSTGRES_DB`)
2. Proverite da li je baza kreirana (trebalo bi automatski preko init skripti)

## 7. Testiranje konekcije iz terminala

Ako želite da testirate konekciju pre nego što se konektujete preko pgAdmin-a:

```bash
# Iz Docker kontejnera
docker exec -it euk-postgres psql -U euk_user -d euk_database

# Sa host mašine
psql -h localhost -p 5432 -U euk_user -d euk_database
```

## 8. Pregled strukture baze

Nakon uspešne konekcije u pgAdmin-u:

1. **euk_database** → **Schemas** → **euk**
2. Trebalo bi da vidite sledeće tabele:
   - `ugrozeno_lice_t1`
   - `ugrozeno_lice_t2`
   - `predmet`
   - `kategorija`
   - `users`
   - `global_license`
   - itd.

## 9. Pregled podataka

Možete pregledati podatke direktno iz pgAdmin-a:
1. Desni klik na tabelu → **"View/Edit Data"** → **"All Rows"**

## 10. SQL Query Editor

Za izvršavanje SQL upita:
1. Desni klik na bazu → **"Query Tool"**
2. Unesite SQL upit
3. Kliknite **F5** ili **Execute** dugme

---

**Napomena:** Sve vrednosti zavise od vaših `.env` podešavanja. Ako imate različite vrednosti, koristite te vrednosti umesto podrazumevanih.

