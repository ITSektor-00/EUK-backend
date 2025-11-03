# 🏛️ EUK Pisarnica API - Zahtev za Kreiranje

## 📋 **Opis Projekta**
Treba da se kreira **poseban Spring Boot projekat** za Pisarnica API koji će se povezati na **Microsoft SQL Server** bazu podataka.

## 🎯 **Zašto Poseban Projekat?**
- **Glavna aplikacija** koristi PostgreSQL
- **Pisarnica API** koristi Microsoft SQL Server
- **Izolacija** - nema konflikta između baza
- **Nezavisnost** - može se deploy-ovati odvojeno

## 🛠️ **Spring Initializr Konfiguracija**

### **1. Project Settings:**
- **Project:** Maven
- **Language:** Java
- **Spring Boot:** 3.5.6
- **Packaging:** Jar
- **Java:** 17

### **2. Project Metadata:**
```
Group: com.sirus
Artifact: euk-pisarnica-api
Name: euk-pisarnica-api
Description: EUK Pisarnica API - SQL Server Database
Package name: com.sirus.pisarnica
```

### **3. Dependencies:**
- ✅ **Spring Web** - za REST API
- ✅ **Spring Data JPA** - za database operacije
- ✅ **SQL Server Driver** - za Microsoft SQL Server
- ✅ **Spring Boot DevTools** - za development
- ✅ **Lombok** - za smanjenje boilerplate koda

## 🗄️ **Database Konfiguracija**

### **SQL Server Podaci:**
- **Server:** 192.168.1.16:1433
- **Database:** PisarnicaBG
- **Username:** euk
- **Password:** Beogr@d#2025!
- **Schema:** dbo
- **Table:** Predmet

### **Connection String:**
```
jdbc:sqlserver://192.168.1.16:1433;databaseName=PisarnicaBG;encrypt=true;trustServerCertificate=true
```

## 📁 **Struktura Projekta**
```
euk-pisarnica-api/
├── src/main/java/com/sirus/pisarnica/
│   ├── PisarnicaApiApplication.java
│   ├── entity/Predmet.java
│   ├── repository/PredmetRepository.java
│   ├── service/PredmetService.java
│   ├── controller/PisarnicaController.java
│   └── dto/
│       ├── PredmetDto.java
│       ├── PredmetFilterRequest.java
│       └── PaginatedResponse.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🔧 **Potrebne Klase**

### **1. Predmet Entity (27 kolona):**
- ID_Predmet, ID_Organ_OJ, ID_Odsek, ID_Klasifikacija
- ID_Vrsta_Predmeta, ID_Opis_Klasifikacije, ID_Opstina
- Tip_Podnosioca, Naziv_Podnosioca, Maticni_Broj, PIB
- Adresa, Ekstemi_ID_Podnosioca, Napomena, Dodatna_Napomena
- Strani_Broj, Katastarski_Podaci, Vrednost_Takse
- Datum_Predmeta, Datum_Unosa, Godina, Azurirano
- ID_Ovlasceno_Lice, ID, ID_Baza, Popis_Akata, Jedinstveni_Kod

### **2. API Endpoints:**
```
GET /api/pisarnica/filter - Filtriranje sa paginacijom
GET /api/pisarnica/search - Brza pretraga
GET /api/pisarnica/statistics - Statistike
GET /api/pisarnica/health - Health check
```

### **3. Filter Parametri:**
- searchTerm, page, size, sortBy, sortDirection
- idOrganOj, idOdsek, idKlasifikacija, idVrstaPredmeta
- idOpisKlasifikacije, idOpstina, tipPodnosioca
- nazivPodnosioca, maticniBroj, pib
- datumPredmetaOd, datumPredmetaDo, godina
- vrednostTakseOd, vrednostTakseDo
- napomena, straniBroj, jedinstveniKod

## 🚀 **Deployment**

### **Port:**
- **Glavna aplikacija:** 8080 (PostgreSQL)
- **Pisarnica API:** 8081 (SQL Server)

### **Environment Variables:**
```env
# SQL Server Configuration
PISARNICA_DB_URL=jdbc:sqlserver://192.168.1.16:1433;databaseName=PisarnicaBG;encrypt=true;trustServerCertificate=true
PISARNICA_DB_USERNAME=euk
PISARNICA_DB_PASSWORD="Beogr@d#2025!"
PISARNICA_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

## 📊 **Performance Requirements**
- **Database:** Preko 1 milion zapisa
- **Optimizacija:** Paginacija, indeksi, caching
- **Response Time:** < 2 sekunde za filter
- **Concurrent Users:** 50+ korisnika

## 🔒 **Security**
- **CORS:** Dozvoli sve origin-e
- **Authentication:** Javni endpoint-i (bez JWT)
- **Rate Limiting:** 1000 zahteva/minut

## 📝 **Dodatne Napomene**
- **Read-Only Database** - nema DDL operacija
- **SQL Server Dialect** - org.hibernate.dialect.SQLServerDialect
- **JPA DDL Auto:** none
- **Connection Pool:** HikariCP sa optimizacijom

## 🎯 **Prioritet**
**VISOK** - Potreban za produkciju

## 📞 **Kontakt**
Za dodatna pitanja vezana za implementaciju.

---
**Datum:** 15.10.2025  
**Status:** U razvoju  
**Tim:** Backend Development
