# ODBIJA SE NSP - Word Document Generation

## 📋 Pregled sistema

Sistem za generisanje Word dokumenata "OДБИЈА СЕ NSP,UNSP,DD,UDTNP" na osnovu template-a i podataka sa frontend-a.

## 🏗️ Arhitektura

### Backend komponente:
- **Controller**: `DokumentiController.java` - REST endpoint
- **Service**: `OdbijaSeNSPDocumentService.java` - logika generisanja
- **DTO**: `OdbijaSeNSPRequestDTO.java` - struktura zahteva
- **Security**: `DevelopmentSecurityConfig.java` i `ProductionSecurityConfig.java`

### Template:
- **Lokacija**: `src/main/resources/obrasci/obrasci NEGATIVNO sa potpisom podsekretara/OДБИЈА СЕ NSP,UNSP,DD,UDTNP.docx`
- **Format**: `.docx` (Apache POI XWPFDocument)

## 🔧 API Endpoint

```
POST /api/dokumenti/odbija-se-nsp/generisi
Content-Type: application/json
```

### Request struktura:

```json
{
  "brojPredmeta": "SA_VD_SR/2025",
  "datumDonosenja": "2025-01-15",
  "brojOvlascenja": "123/2025",
  "datumOvlascenja": "2025-01-10",
  "imeIPrezimeOvlascenog": "Марко Марковић",
  "imeIPrezimePodnosioca": "Петар Петровић",
  "jmbg": "1234567890123",
  "grad": "Београд",
  "ulica": "27. марта",
  "brojStana": "43-45",
  "opstina": "Стари град",
  "pttBroj": "11000",
  "mestoStanovanja": "Београд",
  "datumPodnosenja": "2025-01-05",
  "osnovPrava": "NSP",
  "brojClanovaDomacinstava": "4",
  "prilozenaInfo": "Документација о приходу",
  "sluzbeniDokazi": "Службеним путем прибављени докази",
  "dodatniTekst": "Додатни текст",
  "vrsilacDuznosti": true,
  "sopstveneRuke": true,
  "dodatakZaPomocOdnosiSe": false,
  "pribavljaDokumentacijuSluzbeno": true
}
```

### Response:
- **Content-Type**: `application/vnd.openxmlformats-officedocument.wordprocessingml.document`
- **Content-Disposition**: `attachment; filename="SA_VD_SR_2025.docx"`
- **Body**: `byte[]` - Word dokument

## 📝 Placeholder sistem

### Osnovni placeholderi:

| Placeholder | Vrednost | Primer |
|-------------|----------|---------|
| `«БРОЈ_ПРЕДМЕТА»` | `brojPredmeta` | "SA_VD_SR/2025" |
| `«ДАТУМ_ДОНОШЕЊА_РЕШЕЊА»` | `datumDonosenja` | "15.01.2025." |
| `«БРОЈ_ОВЛАШЋЕЊА»` | `brojOvlascenja` | "123/2025" |
| `«ДАТУМ_ОВЛАШЋЕЊА»` | `datumOvlascenja` | "10.01.2025." |
| `«ИМЕ_И_ПРЕЗИМЕ_ОВЛАШЋЕНОГ_ЛИЦА»` | `imeIPrezimeOvlascenog` | "Марко Марковић" |
| `«ИМЕ_И_ПРЕЗИМЕ_ПОДНОСИОЦА»` | `imeIPrezimePodnosioca` | "Петар Петровић" |
| `«ЈМБГ»` | `jmbg` | "1234567890123" |
| `«МЕСТО_СТАНОВАЊА»` | `mestoStanovanja` | "Београд" |
| `«УЛИЦА»` | `ulica` | "27. марта" |
| `«БРОЈ_УЛИЦЕ»` | `brojStana` | "43-45" |
| `«ГРАД_ОПШТИНА»` | `grad + ", " + opstina` | "Београд, Стари град" |
| `«ПТТ_БРОЈ»` | `pttBroj` | "11000" |
| `«ДАТУМ_ПОДНОШЕЊА»` | `datumPodnosenja` | "05.01.2025." |
| `«БРОЈ_ЧЛАНОВА_ДОМАЋИНСТВА»` | `brojClanovaDomacinstava` | "4" |
| `«ПРИЛОЖЕНА_ИНФОРМАЦИЈА»` | `prilozenaInfo` | "Документација о приходу" |
| `«СЛУЖБЕНИ_ДОКАЗИ»` | `sluzbeniDokazi` | "Службеним путем прибављени докази" |
| `«ДОДАТНИ_ТЕКСТ»` | `dodatniTekst` | "Додатни текст" |

### Kombinovani placeholderi:

| Placeholder | Kombinacija |
|-------------|-------------|
| `«УЛИЦА_И_БРОЈ»` | `ulica + " " + brojStana` |
| `«МЕСТО»` | `mestoStanovanja` |

### Uslovni placeholderi:

| Placeholder | Uslov | Vrednost |
|-------------|-------|----------|
| `«в.д.»` | `vrsilacDuznosti = true` | "в.д." |
| `«с.р.»` | `sopstveneRuke = true` | "с.р." |
| `«ОСТВАРЕНОГ_ПРАВА_НА»` | - | Genitiv forma `osnovPrava` |
| `«ОСНОВ_ПРАВА_ПУНИ_ТЕКСТ»` | - | Puni tekst sa negacijom |

### Uslovni tekstovi:

#### "Службеним путем..." tekst:
- **Placeholder**: `«СЛУЖБЕНИМ_ПУТЕМ_ТЕКСТ»`
- **Uslov**: `pribavljaDokumentacijuSluzbeno = true`
- **Vrednost**: "Службеним путем, овај орган је у поступку по Захтеву, на основу претходно дате сагласности подносиоца Захтева и члана 103. Закона о општем управном поступку поступку („Службени гласник РС", бр. бр. 18/2016 и 2/2023-одлука УС РС) прибавио доказ о:"
- **Ako false**: Uklanja se ceo paragraf

#### "Додатак за помоћ..." tekst:
- **Placeholder**: `«ДОДАТАК_ЗА_ПОМОЋ_ТЕКСТ»`
- **Uslov**: `dodatakZaPomocOdnosiSe = true`
- **Vrednost**: "Додатак за помоћ и негу другог лица, а које право је остварено по прописима из области социјалне заштите/Новчана накнада за помоћ и негу другог лица а које право је остварено преко РФ ПИО/ није основ за стицање статуса енергетски угроженог купца."
- **Ako false**: Uklanja se ceo paragraf

## 🎯 Osnov prava mapping

### Input vrednosti:
- `"NSP"` → новчану социјалну помоћ
- `"UNSP"` → увећану новчану социјалну помоћ  
- `"DD"` → дечији додатак
- `"UDTNP"` → увећани додатак за помоћ и негу другог лица

### Genitiv forma (`«ОСТВАРЕНОГ_ПРАВА_НА»`):
- `"NSP"` → "новчану социјалну помоћ"
- `"UNSP"` → "увећану новчану социјалну помоћ"
- `"DD"` → "дечији додатак"
- `"UDTNP"` → "увећани додатак за помоћ и негу другог лица"

### Puni tekst (`«ОСНОВ_ПРАВА_ПУНИ_ТЕКСТ»`):
- `"NSP"` → "на новчану социјалну помоћ, односно није корисник права на новчану социјалну помоћ"
- `"UNSP"` → "на увећану новчану социјалну помоћ, односно није корисник права на увећану новчану социјалну помоћ"
- `"DD"` → "на дечији додатак, односно није корисник права на дечији додатак"
- `"UDTNP"` → "на увећани додатак за помоћ и негу другог лица, односно није корисник права на увећани додатак за помоћ и негу другог лица"

## 🔧 Tehnički detalji

### Apache POI konfiguracija:
- **Document type**: `XWPFDocument` (za .docx fajlove)
- **Font**: "Times New Roman" za sve izmene
- **Encoding**: UTF-8 za ćirilicu

### File handling:
- **Template loading**: `getClass().getResourceAsStream()`
- **Output directory**: `generated_templates/`
- **Filename format**: `{brojPredmeta}_{timestamp}.docx`
- **Character sanitization**: Uklanjanje nedozvoljenih karaktera iz filename-a

### Security:
```java
.requestMatchers("/api/dokumenti/**").permitAll()
```

## 🧪 Testiranje

### Test fajlovi:
- `test-sa-vd-sr.json` - sa `vrsilacDuznosti=true`, `sopstveneRuke=true`
- `test-bez-vd-sr.json` - sa `vrsilacDuznosti=false`, `sopstveneRuke=false`
- `test-oba-dokumenta.ps1` - PowerShell script za oba testa

### cURL primer:
```bash
curl -X POST http://localhost:8080/api/dokumenti/odbija-se-nsp/generisi \
  -H "Content-Type: application/json" \
  -d @test-sa-vd-sr.json \
  --output dokument.docx
```

## 📁 File struktura

```
src/main/java/com/sirus/backend/
├── controller/
│   └── DokumentiController.java
├── service/
│   └── OdbijaSeNSPDocumentService.java
├── dto/
│   └── OdbijaSeNSPRequestDTO.java
└── config/
    ├── DevelopmentSecurityConfig.java
    └── ProductionSecurityConfig.java

src/main/resources/
└── obrasci/obrasci NEGATIVNO sa potpisom podsekretara/
    └── OДБИЈА СЕ NSP,UNSP,DD,UDTNP.docx

generated_templates/
├── SA_VD_SR_2025_1759753709547.docx
└── SA_VD_SR_2025_1759753880503.docx
```

## ⚠️ Važne napomene

1. **Template mora biti .docx format** - ne .doc
2. **Placeholderi moraju tačno odgovarati** - sa donjom crtom `_`
3. **Ćirilica encoding** - UTF-8 kroz ceo sistem
4. **Font consistency** - "Times New Roman" za sve izmene
5. **Line breaks** - čuvaju se originalni format template-a
6. **Conditional text** - uklanja se ceo paragraf ako flag=false
7. **File permissions** - `generated_templates/` direktorijum mora biti writable

## 🐛 Česti problemi

### "Failed to fetch" / 403 Forbidden:
- **Uzrok**: Security config ne dozvoljava `/api/dokumenti/**`
- **Rešenje**: Dodati `.requestMatchers("/api/dokumenti/**").permitAll()`

### "Unsupported Sprm operation":
- **Uzrok**: Korišćenje .doc umesto .docx
- **Rešenje**: Konvertovati template u .docx format

### "nije dobro jer sada ne popunjava nista od podataka":
- **Uzrok**: Placeholderi u kodu ne odgovaraju placeholderima u template-u
- **Rešenje**: Proveriti tačno kako izgledaju placeholderi u template-u

### "stavio si mi sve sada u jedan red":
- **Uzrok**: `replacePlaceholdersInParagraph` zamenjuje ceo paragraf
- **Rešenje**: Run-by-run replacement za čuvanje formatiranja

### Cyrillic karakteri se ne prikazuju:
- **Uzrok**: Pogrešan encoding ili font
- **Rešenje**: UTF-8 encoding + "Times New Roman" font

## 🔄 Workflow

1. **Frontend** šalje JSON sa podacima
2. **Controller** prima zahtev i poziva service
3. **Service** učitava template iz resources
4. **Service** zamenjuje placeholderi sa vrednostima
5. **Service** uklanja uslovne sekcije ako flag=false
6. **Service** čuva dokument u `generated_templates/`
7. **Controller** vraća byte[] kao download
8. **Frontend** prima i download-uje .docx fajl

## 📋 Checklist za deployment

- [ ] Template konvertovan u .docx
- [ ] Placeholderi u template-u odgovaraju kodu
- [ ] Security config dozvoljava `/api/dokumenti/**`
- [ ] `generated_templates/` direktorijum postoji i je writable
- [ ] Maven build uspešan (`mvn clean compile`)
- [ ] Test endpoint radi sa cURL
- [ ] Cyrillic karakteri se prikazuju ispravno
- [ ] Uslovni tekstovi se uklanjaju/dodaju ispravno
- [ ] Font je "Times New Roman" kroz ceo dokument
