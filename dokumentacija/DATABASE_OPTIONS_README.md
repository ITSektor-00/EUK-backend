# 🗄️ EUK Backend - Opcije Baze Podataka

## 📋 Pregled
EUK Backend podržava dve opcije za bazu podataka:
1. **Supabase (Cloud)** - Trenutno konfigurisano
2. **Lokalna PostgreSQL** - Preporučeno za server firme

---

## 🌐 Opcija 1: Supabase (Trenutno)

### Prednosti:
- ✅ Brza implementacija
- ✅ Automatski backup
- ✅ Skalabilnost
- ✅ Nema potrebe za server setup

### Nedostaci:
- ❌ Podaci su van firme
- ❌ Zavisi od interneta
- ❌ Troškovi po korišćenju

### Konfiguracija:
```bash
# .env fajl
DATABASE_URL=jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres
DATABASE_USERNAME=postgres.wynfrojhkzddzjbrpdcr
DATABASE_PASSWORD=a*Xxk3B7?HF8&3r
```

---

## 🏠 Opcija 2: Lokalna PostgreSQL (Preporučeno)

### Prednosti:
- ✅ Potpuna kontrola nad podacima
- ✅ Bolja sigurnost
- ✅ Niži troškovi
- ✅ Brži pristup
- ✅ Nema zavisanost od interneta

### Nedostaci:
- ❌ Potrebno održavanje
- ❌ Backup mora biti ručno konfigurisan

### Konfiguracija:
```bash
# .env fajl
DATABASE_URL=jdbc:postgresql://localhost:5432/euk_database
DATABASE_USERNAME=euk_user
DATABASE_PASSWORD=your_secure_password_here
```

---

## 🚀 Brza Migracija na Lokalnu PostgreSQL

### Korak 1: Setup PostgreSQL
```bash
# Linux/Mac
chmod +x scripts/setup-database.sh
./scripts/setup-database.sh

# Windows
scripts\setup-database.bat
```

### Korak 2: Deploy sa PostgreSQL
```bash
# Linux/Mac
chmod +x scripts/deploy-with-postgres.sh
./scripts/deploy-with-postgres.sh

# Windows
docker-compose -f docker-compose.prod.yml up -d
```

### Korak 3: Testiranje
```bash
curl http://localhost:8080/api/test/health
```

---

## 📁 Fajlovi za Lokalnu PostgreSQL

### Konfiguracija:
- `docker-compose.prod.yml` - Docker Compose sa PostgreSQL
- `env.example` - Environment varijable za lokalnu bazu
- `POSTGRESQL_SERVER_SETUP.md` - Detaljni vodič za setup

### Skripte:
- `scripts/setup-database.sh` - Linux/Mac setup skripta
- `scripts/setup-database.bat` - Windows setup skripta
- `scripts/deploy-with-postgres.sh` - Deployment skripta

---

## 🔄 Migracija Podataka

### Iz Supabase u Lokalnu PostgreSQL:

#### 1. Backup iz Supabase
```bash
# Konektuj se na Supabase
pg_dump -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres > supabase_backup.sql
```

#### 2. Restore u Lokalnu PostgreSQL
```bash
# Konektuj se na lokalnu bazu
psql -h localhost -U euk_user -d euk_database < supabase_backup.sql
```

#### 3. Ažuriraj .env fajl
```bash
# Promeni DATABASE_URL u .env fajlu
DATABASE_URL=jdbc:postgresql://localhost:5432/euk_database
DATABASE_USERNAME=euk_user
DATABASE_PASSWORD=your_secure_password_here
```

---

## 🛠️ Troubleshooting

### Česte Greške:

#### 1. Connection Refused
```bash
# Proveri da li PostgreSQL radi
sudo systemctl status postgresql

# Proveri port
sudo netstat -tulpn | grep 5432
```

#### 2. Authentication Failed
```bash
# Proveri korisnika
sudo -u postgres psql -c "\du"

# Resetuj lozinku
sudo -u postgres psql -c "ALTER USER euk_user PASSWORD 'new_password';"
```

#### 3. Database Does Not Exist
```bash
# Kreiraj bazu
sudo -u postgres createdb euk_database
```

---

## 📊 Poređenje Performansi

| Kriterijum | Supabase | Lokalna PostgreSQL |
|-----------|----------|-------------------|
| **Sigurnost** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Brzina** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Kontrola** | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Troškovi** | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Održavanje** | ⭐⭐⭐⭐⭐ | ⭐⭐ |
| **Skalabilnost** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |

---

## 🎯 Preporuka

**Za server firme preporučujem Lokalnu PostgreSQL** jer:
- Podaci ostaju u firmi
- Bolja sigurnost
- Niži troškovi
- Brži pristup
- Potpuna kontrola

**Supabase koristiti samo za:**
- Development
- Testiranje
- Prototype projekte
