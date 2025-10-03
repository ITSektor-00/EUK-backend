# Implementacija PDF Generisanja za Koverate

## Pregled

Implementiran je backend endpoint za generisanje PDF koverata sa tačnim dimenzijama i pozicioniranjem elemenata.

## Endpoint

### POST /api/generate-envelope-pdf

Generiše PDF koverat sa podacima ugroženih lica.

#### Request Body

```json
{
  "template": "T1" | "T2",
  "ugrozenaLica": [
    {
      "ugrozenoLiceId": 123,
      "ime": "Marko",
      "prezime": "Petrović",
      "ulicaIBroj": "Knez Mihailova 15",
      "pttBroj": "11000",
      "gradOpstina": "Beograd",
      "mesto": "Stari grad"
    }
  ]
}
```

#### Response

- **Content-Type**: `application/pdf`
- **Content-Disposition**: `attachment; filename="koverat.pdf"`
- **Body**: PDF fajl sa koveratom

### GET /api/test-envelope-pdf

Test endpoint za proveru funkcionalnosti sa unapred definisanim podacima.

## Tehnička Implementacija

### Dependencies

Dodati su sledeći dependency-ji u `pom.xml`:

```xml
<!-- iText for PDF generation -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>8.0.2</version>
    <type>pom</type>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>html2pdf</artifactId>
    <version>5.0.4</version>
</dependency>
```

### Komponente

#### 1. DTO Klase

- **EnvelopePdfRequest**: Glavni request DTO
- **UgrozenoLiceDto**: DTO za podatke ugroženog lica

#### 2. Service

- **EnvelopePdfService**: Generiše PDF koristeći iText biblioteku

#### 3. Controller

- **EnvelopePdfController**: HTTP endpoint-i za PDF generisanje

## Dimenzije i Pozicioniranje

### Koverat Dimenzije
- **Širina**: 246mm
- **Visina**: 175mm

### Pozicije Elemenata

| Element | Pozicija | Veličina Fonta |
|---------|----------|----------------|
| Grb | 15mm od leve, 21mm od gore | - |
| Naslov | 15mm od leve, 21mm od gore | 10pt, bold |
| Adresa | 15mm od leve, 31mm od gore | 10pt, bold |
| Grad | 15mm od leve, 41mm od gore | 10pt, bold |
| Ime i prezime | 90mm od leve, 100mm od gore | 12pt, bold |
| Adresa lica | 90mm od leve, 108mm od gore | 10pt |
| PTT i grad | 90mm od leve, 116mm od gore | 10pt |
| Mesto | 90mm od leve, 124mm od gore | 10pt |

## Font i Kodiranje

Koristi se font koji podržava ćirilicu:
- **Font**: STSong-Light
- **Kodiranje**: UniGB-UCS2-H
- **Strategija**: PREFER_EMBEDDED

## Validacija

### Request Validacija

- `template`: Obavezan, mora biti "T1" ili "T2"
- `ugrozenaLica`: Ne može biti prazna lista
- `ugrozenoLiceId`: Obavezan
- `ime`: Obavezno, ne može biti prazan
- `prezime`: Obavezno, ne može biti prazan
- `ulicaIBroj`: Obavezno, ne može biti prazan
- `pttBroj`: Obavezno, ne može biti prazan
- `gradOpstina`: Obavezno, ne može biti prazan
- `mesto`: Obavezno, ne može biti prazan

## Testiranje

### Test Endpoint

```
GET /api/test-envelope-pdf
```

Generiše test PDF sa sledećim podacima:
- Ime: Marko
- Prezime: Petrović
- Adresa: Knez Mihailova 15
- PTT: 11000
- Grad: Beograd
- Mesto: Stari grad

### Testiranje sa Postman

1. **Test Endpoint**:
   ```
   GET http://localhost:8080/api/test-envelope-pdf
   ```

2. **Glavni Endpoint**:
   ```
   POST http://localhost:8080/api/generate-envelope-pdf
   Content-Type: application/json
   
   {
     "template": "T1",
     "ugrozenaLica": [
       {
         "ugrozenoLiceId": 1,
         "ime": "Marko",
         "prezime": "Petrović",
         "ulicaIBroj": "Knez Mihailova 15",
         "pttBroj": "11000",
         "gradOpstina": "Beograd",
         "mesto": "Stari grad"
       }
     ]
   }
   ```

## Greške i Rukovanje

### Moguće Greške

1. **400 Bad Request**: Neispravni template ili validacija
2. **500 Internal Server Error**: Greška pri generisanju PDF-a

### Error Response Format

```json
{
  "error": "Opis greške"
}
```

## Napomene

- PDF se generiše sa tačnim dimenzijama koverata
- Podržava ćirilicu i latinica
- Svako ugroženo lice se generiše na novoj stranici
- Font se embeduje u PDF za konzistentnost
