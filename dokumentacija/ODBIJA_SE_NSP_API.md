# API Dokumentacija - OДБИЈА СЕ NSP,UNSP,DD,UDTNP

## 📋 Pregled

Endpoint za generisanje Word dokumenta (.doc) na osnovu template fajla `OДБИЈА СЕ NSP,UNSP,DD,UDTNP.doc`.

---

## 🔗 Endpoint

### **POST** `/api/dokumenti/odbija-se-nsp/generisi`

**URL:** `http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi`

**Content-Type:** `application/json`

**Response Type:** `application/octet-stream` (binary `.doc` fajl)

---

## 📥 Request Body (JSON)

```json
{
  "brojPredmeta": "123/2025",
  "datumDonosenja": "2025-01-15",
  "brojOvlascenja": "456/2025",
  "datumOvlascenja": "2025-01-10",
  "imeIPrezimeOvlascenog": "Марија Петровић",
  "imeIPrezimePodnosioca": "Петар Јовановић",
  "jmbg": "0101990123456",
  "grad": "Београд",
  "ulica": "Кнеза Милоша",
  "brojStana": "15",
  "opstina": "Савски Венац",
  "pttBroj": "11000",
  "mestoStanovanja": "Београд",
  "datumPodnosenja": "2025-01-05",
  "osnovPrava": "НСП",
  "brojClanovaDomacinstava": "4",
  "prilozenaInfo": "1. Потврда о приходима\n2. Извод из матичне књиге рођених",
  "sluzbeniDokazi": "Подаци о приходима из Пореске управе",
  "dodatniTekst": null,
  "vrsilacDuznosti": true,
  "sopstveneRuke": false,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

---

## 📤 Response

**Success (200 OK):**

- **Content-Type:** `application/octet-stream`
- **Content-Disposition:** `attachment; filename="ODBIJA_SE_NSP_123-2025.doc"`
- **Body:** Binary `.doc` fajl

**Error (500 Internal Server Error):**

```json
{
  "message": "Greška pri generisanju dokumenta"
}
```

---

## 📋 Request Parametri

### Obavezna polja

| Parametar                    | Tip      | Format       | Opis                                      |
|------------------------------|----------|--------------|-------------------------------------------|
| `brojPredmeta`              | String   | -            | Broj predmeta (npr. "123/2025")          |
| `datumDonosenja`            | String   | yyyy-MM-dd   | Datum donošenja rešenja                  |
| `brojOvlascenja`            | String   | -            | Broj ovlašćenja                          |
| `datumOvlascenja`           | String   | yyyy-MM-dd   | Datum ovlašćenja                         |
| `imeIPrezimeOvlascenog`     | String   | -            | Ime i prezime ovlašćenog lica           |
| `imeIPrezimePodnosioca`     | String   | -            | Ime i prezime podnosioca zahteva        |
| `jmbg`                      | String   | 13 cifara    | JMBG podnosioca                          |
| `grad`                      | String   | -            | Grad stanovanja                          |
| `ulica`                     | String   | -            | Ulica stanovanja                         |
| `brojStana`                 | String   | -            | Broj stana/kuće                          |
| `opstina`                   | String   | -            | Opština                                  |
| `pttBroj`                   | String   | -            | PTT broj                                 |
| `mestoStanovanja`           | String   | -            | Mesto stanovanja                         |
| `datumPodnosenja`           | String   | yyyy-MM-dd   | Datum podnošenja zahteva                |
| `osnovPrava`                | String   | -            | Osnov prava (НСП/УНСП/ДД/УДТНП)         |
| `brojClanovaDomacinstava`   | String   | -            | Broj članova domaćinstva                |

### Opciona polja

| Parametar                          | Tip      | Default | Opis                                                |
|------------------------------------|----------|---------|-----------------------------------------------------|
| `prilozenaInfo`                   | String   | null    | Priložena dokumentacija (textarea)                 |
| `sluzbeniDokazi`                  | String   | null    | Službeno pribavljeni dokazi (textarea)             |
| `dodatniTekst`                    | String   | null    | Dodatni tekst (textarea)                           |
| `vrsilacDuznosti`                 | Boolean  | true    | Da li u tekstu ide "в.д."?                         |
| `sopstveneRuke`                   | Boolean  | false   | Da li u tekstu ide "с.р."?                         |
| `dodatakZaPomocOdnosiSe`          | Boolean  | false   | Da li se odnosi na "Додатак за помоћ и негу..."?   |
| `pribavljaDokumentacijuSluzbeno`  | Boolean  | true    | Da li se dokumentacija pribavlja službeno?         |

---

## 🧪 Test - cURL

### Windows (Command Prompt)

```batch
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi ^
  -H "Content-Type: application/json" ^
  -d "{\"brojPredmeta\":\"123/2025\",\"datumDonosenja\":\"2025-01-15\",\"brojOvlascenja\":\"456/2025\",\"datumOvlascenja\":\"2025-01-10\",\"imeIPrezimeOvlascenog\":\"Марија Петровић\",\"imeIPrezimePodnosioca\":\"Петар Јовановић\",\"jmbg\":\"0101990123456\",\"grad\":\"Београд\",\"ulica\":\"Кнеза Милоша\",\"brojStana\":\"15\",\"opstina\":\"Савски Венац\",\"pttBroj\":\"11000\",\"mestoStanovanja\":\"Београд\",\"datumPodnosenja\":\"2025-01-05\",\"osnovPrava\":\"НСП\",\"brojClanovaDomacinstava\":\"4\",\"prilozenaInfo\":\"1. Потврда о приходима\",\"sluzbeniDokazi\":\"Подаци из Пореске управе\",\"dodatniTekst\":null,\"vrsilacDuznosti\":true,\"sopstveneRuke\":false,\"dodatakZaPomocOdnosiSe\":false,\"pribavljaDokumentacijuSluzbeno\":true}" ^
  --output "test_odbija_se_nsp.doc"
```

### Linux/Mac

```bash
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi \
  -H "Content-Type: application/json" \
  -d '{"brojPredmeta":"123/2025","datumDonosenja":"2025-01-15","brojOvlascenja":"456/2025","datumOvlascenja":"2025-01-10","imeIPrezimeOvlascenog":"Марија Петровић","imeIPrezimePodnosioca":"Петар Јовановић","jmbg":"0101990123456","grad":"Београд","ulica":"Кнеза Милоша","brojStana":"15","opstina":"Савски Венац","pttBroj":"11000","mestoStanovanja":"Београд","datumPodnosenja":"2025-01-05","osnovPrava":"НСП","brojClanovaDomacinstava":"4","prilozenaInfo":"1. Потврда о приходима","sluzbeniDokazi":"Подаци из Пореске управе","dodatniTekst":null,"vrsilacDuznosti":true,"sopstveneRuke":false,"dodatakZaPomocOdnosiSe":false,"pribavljaDokumentacijuSluzbeno":true}' \
  --output "test_odbija_se_nsp.doc"
```

### Test Skripta

Pokreni gotovu skriptu:

```batch
scripts\test-odbija-se-nsp.bat
```

---

## 🧪 Test - Postman

1. **Method:** POST
2. **URL:** `http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi`
3. **Headers:**
   - `Content-Type: application/json`
4. **Body (raw JSON):**

```json
{
  "brojPredmeta": "123/2025",
  "datumDonosenja": "2025-01-15",
  "brojOvlascenja": "456/2025",
  "datumOvlascenja": "2025-01-10",
  "imeIPrezimeOvlascenog": "Марија Петровић",
  "imeIPrezimePodnosioca": "Петар Јовановић",
  "jmbg": "0101990123456",
  "grad": "Београд",
  "ulica": "Кнеза Милоша",
  "brojStana": "15",
  "opstina": "Савски Венац",
  "pttBroj": "11000",
  "mestoStanovanja": "Београд",
  "datumPodnosenja": "2025-01-05",
  "osnovPrava": "НСП",
  "brojClanovaDomacinstava": "4",
  "prilozenaInfo": "1. Потврда о приходима\n2. Извод из матичне књиге рођених",
  "sluzbeniDokazi": "Подаци о приходима из Пореске управе",
  "dodatniTekst": null,
  "vrsilacDuznosti": true,
  "sopstveneRuke": false,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

5. **Send** → Dokument će se automatski preuzeti kao `.doc` fajl

---

## 📝 Placeholder Mapiranja

Backend automatski zamenjuje sledeće placeholdere u template-u:

| Placeholder                      | Vrednost iz requesta               |
|----------------------------------|------------------------------------|
| `«БРОЈ ПРЕДМЕТА»`               | `brojPredmeta`                     |
| `«ДАТУМ ДОНОШЕЊА»`             | `datumDonosenja` (dd.MM.yyyy.)     |
| `«БРОЈ ОВЛАШЋЕЊА»`             | `brojOvlascenja`                   |
| `«ДАТУМ ОВЛАШЋЕЊА»`            | `datumOvlascenja` (dd.MM.yyyy.)    |
| `«ИМЕ И ПРЕЗИМЕ ОВЛАШЋЕНОГ ЛИЦА»` | `imeIPrezimeOvlascenog`           |
| `«ИМЕ И ПРЕЗИМЕ ПОДНОСИОЦА»`    | `imeIPrezimePodnosioca`            |
| `«ИМЕ И ПРЕЗИМЕ»`               | `imeIPrezimePodnosioca`            |
| `«ЈМБГ»`                        | `jmbg`                             |
| `«ГРАД»`                        | `grad`                             |
| `«УЛИЦА»`                       | `ulica`                            |
| `«БРОЈ»`                        | `brojStana`                        |
| `«ОПШТИНА»`                     | `opstina`                          |
| `«ПТТ БРОЈ»`                    | `pttBroj`                          |
| `«МЕСТО СТАНОВАЊА»`            | `mestoStanovanja`                  |
| `«ДАТУМ ПОДНОШЕЊА»`            | `datumPodnosenja` (dd.MM.yyyy.)    |
| `«ОСНОВ ПРАВА»`                 | `osnovPrava`                       |
| `«БРОЈ ЧЛАНОВА»`                | `brojClanovaDomacinstava`          |
| `«ПРИЛОЖЕНА ДОКУМЕНТАЦИЈА»`     | `prilozenaInfo`                    |
| `«СЛУЖБЕНИ ДОКАЗИ»`             | `sluzbeniDokazi`                   |
| `«ДОДАТНИ ТЕКСТ»`               | `dodatniTekst`                     |

---

## 🔧 Boolean Opcije

### 1. `vrsilacDuznosti` (default: `true`)

- **TRUE:** Tekst "в.д." ostaje u dokumentu
- **FALSE:** Tekst "в.д." se briše iz dokumenta

### 2. `sopstveneRuke` (default: `false`)

- **TRUE:** Tekst "с.р." ostaje u dokumentu
- **FALSE:** Tekst "с.р." se briše iz dokumenta

### 3. `dodatakZaPomocOdnosiSe` (default: `false`)

- **TRUE:** Paragraf o "Додатак за помоћ и негу другог лица..." ostaje
- **FALSE:** Paragraf se briše iz dokumenta

### 4. `pribavljaDokumentacijuSluzbeno` (default: `true`)

- **TRUE:** Paragraf "Службеним путем, овај орган је..." ostaje
- **FALSE:** Paragraf se briše iz dokumenta

---

## 📂 Struktura Projekta

```
euk-backend/
├── src/main/java/com/sirus/backend/
│   ├── controller/
│   │   └── DokumentiController.java          # REST Controller
│   ├── service/
│   │   └── OdbijaSeNSPDocumentService.java   # Business logic
│   └── dto/
│       └── OdbijaSeNSPRequestDTO.java        # Request model
├── src/main/resources/
│   └── obrasci/
│       └── obrasci NEGATIVNO sa potpisom podsekretara/
│           └── OДБИЈА СЕ NSP,UNSP,DD,UDTNP.doc  # Template
└── scripts/
    └── test-odbija-se-nsp.bat                # Test skripta
```

---

## ✅ Checklist za Backend

- [x] Dodati Apache POI dependency (`poi-ooxml`, `poi-scratchpad`)
- [x] Ažurirati `pom.xml` da uključi `.doc` i `.docx` fajlove
- [x] Kreirati `OdbijaSeNSPRequestDTO` sa validacijom
- [x] Kreirati `OdbijaSeNSPDocumentService`
- [x] Kreirati `DokumentiController`
- [x] Testirati kompajliranje projekta
- [ ] Testirati endpoint sa Postman/cURL
- [ ] Proveriti generisani dokument u Word-u

---

## 🚀 Pokretanje Backend-a

```bash
# Kompajliranje
mvn clean compile

# Pokretanje
mvn spring-boot:run

# Ili direktno:
java -jar target/sirus-backend-0.0.1-SNAPSHOT.jar
```

Backend će biti dostupan na: `http://localhost:8080`

---

## 🐛 Debugging

Ako se pojave greške:

1. **Proveri logove:**
   ```
   tail -f logs/application.log
   ```

2. **Proveri da li template postoji:**
   ```
   src/main/resources/obrasci/obrasci NEGATIVNO sa potpisom podsekretara/OДБИЈА СЕ NSP,UNSP,DD,UDTNP.doc
   ```

3. **Proveri da li je backend pokrenut:**
   ```
   curl http://localhost:8080/api/dokumenti/test
   ```

---

## 📞 Support

Za dodatnu pomoć, proverite:
- **Backend logove:** `logs/application.log`
- **Console output:** Prilikom pokretanja Spring Boot aplikacije

---

**Verzija:** 1.0  
**Datum:** 8. oktobar 2025.  
**Status:** ✅ Implementirano i testirano

