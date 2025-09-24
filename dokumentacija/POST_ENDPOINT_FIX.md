# POST ENDPOINT FIX

## 🐛 **Problem**

**Endpoint**: `POST /api/euk/ugrozena-lica-t1`
**Symptom**: "Empty response body" - backend ne vraća odgovor
**Uzrok**: Kontroler je hvatao `EukException` i vraćao `ResponseEntity.badRequest().build()` bez body-ja

## 🔧 **Rešenje**

Ažurirani su svi endpoint-i u `EukUgrozenoLiceT1Controller` da vraćaju detaljne greške sa porukama.

### **Pre promene:**
```java
} catch (EukException e) {
    logger.warn("Error creating ugroženo lice T1: {}", e.getMessage());
    return ResponseEntity.badRequest().build(); // ❌ Prazan body
} catch (Exception e) {
    logger.error("Error creating ugroženo lice T1: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // ❌ Prazan body
}
```

### **Posle promene:**
```java
} catch (EukException e) {
    logger.warn("Error creating ugroženo lice T1: {}", e.getMessage());
    return ResponseEntity.badRequest().body(Map.of(
        "error", "VALIDATION_ERROR",
        "message", e.getMessage(),
        "path", "/api/euk/ugrozena-lica-t1"
    )); // ✅ Detaljna greška
} catch (Exception e) {
    logger.error("Error creating ugroženo lice T1: {}", e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
        "error", "INTERNAL_ERROR",
        "message", "Greška pri kreiranju ugroženog lica: " + e.getMessage(),
        "path", "/api/euk/ugrozena-lica-t1"
    )); // ✅ Detaljna greška
}
```

## ✅ **Ažurirani endpoint-i**

1. **POST /api/euk/ugrozena-lica-t1** - Kreiranje
2. **PUT /api/euk/ugrozena-lica-t1/{id}** - Ažuriranje  
3. **DELETE /api/euk/ugrozena-lica-t1/{id}** - Brisanje

## 📊 **Primer odgovora**

### **Uspešno kreiranje (201 Created):**
```json
{
  "ugrozenoLiceId": 1,
  "redniBroj": "TEST-0001",
  "ime": "Marko",
  "prezime": "Marković",
  "jmbg": "1234567890123",
  "potrosnjaIPovrsinaCombined": "Потрошња у kWh/2500/загревана површина у m2/75",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### **Greška validacije (400 Bad Request):**
```json
{
  "error": "VALIDATION_ERROR",
  "message": "JMBG mora sadržati tačno 13 cifara",
  "path": "/api/euk/ugrozena-lica-t1"
}
```

### **Greška servera (500 Internal Server Error):**
```json
{
  "error": "INTERNAL_ERROR",
  "message": "Greška pri kreiranju ugroženog lica: Database connection failed",
  "path": "/api/euk/ugrozena-lica-t1"
}
```

## 🧪 **Testiranje**

### **Test 1: Validni podaci**
```bash
curl -X POST http://localhost:8080/api/euk/ugrozena-lica-t1 \
  -H "Content-Type: application/json" \
  -d '{
    "redniBroj": "TEST-001",
    "ime": "Marko",
    "prezime": "Marković",
    "jmbg": "1234567890123",
    "potrosnjaKwh": 2500.50,
    "zagrevanaPovrsinaM2": 75.5
  }'
```

### **Test 2: Nevalidni JMBG**
```bash
curl -X POST http://localhost:8080/api/euk/ugrozena-lica-t1 \
  -H "Content-Type: application/json" \
  -d '{
    "redniBroj": "TEST-002",
    "ime": "Ana",
    "prezime": "Anić",
    "jmbg": "123",
    "potrosnjaKwh": 2000.00,
    "zagrevanaPovrsinaM2": 60.0
  }'
```

## 📝 **Datum promene**

**Datum**: $(date)
**Verzija**: 1.0
**Status**: ✅ Implementirano
