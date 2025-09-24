# PAGINATION LIMIT UPDATE

## 📋 **Pregled promena**

Ažurirana su ograničenja za paginaciju u endpoint-ima za ugrožena lica T1.

## 🔧 **Ažurirani endpoint-i**

### **1. EukUgrozenoLiceT1Controller**
- **Endpoint**: `GET /api/euk/ugrozena-lica-t1`
- **Endpoint**: `GET /api/euk/ugrozena-lica-t1/search/name`
- **Endpoint**: `POST /api/euk/ugrozena-lica-t1/search/filters`

### **2. EukUgrozenoLiceController (Legacy)**
- **Endpoint**: `GET /api/euk/ugrozena-lica`

## 📊 **Promene u ograničenjima**

### **Pre promene:**
```java
if (size <= 0 || size > 1000) {
    return ResponseEntity.badRequest().body(Map.of(
        "error", "INVALID_SIZE", 
        "message", "Size must be between 1 and 1000",
        "path", "/api/euk/ugrozena-lica-t1"
    ));
}
```

### **Posle promene:**
```java
if (size <= 0 || size > 50000) {
    return ResponseEntity.badRequest().body(Map.of(
        "error", "INVALID_SIZE", 
        "message", "Size must be between 1 and 50000",
        "path", "/api/euk/ugrozena-lica-t1"
    ));
}
```

## ✅ **Rezultat**

- **Staro ograničenje**: 1-1000 zapisa po stranici
- **Novo ograničenje**: 1-50000 zapisa po stranici
- **Povećanje**: 50x veći limit

## 🚀 **Korišćenje**

### **Primer zahtev:**
```bash
# Dohvatanje 30000 zapisa odjednom
GET /api/euk/ugrozena-lica-t1?page=0&size=30000

# Dohvatanje 50000 zapisa odjednom (maksimum)
GET /api/euk/ugrozena-lica-t1?page=0&size=50000
```

### **Primer odgovora:**
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 30000
  },
  "totalElements": 1000,
  "totalPages": 1,
  "first": true,
  "last": true,
  "numberOfElements": 1000
}
```

## ⚠️ **Napomene**

1. **Performanse**: Veći broj zapisa može uticati na performanse
2. **Memorija**: Frontend treba da bude spreman za veće response-ove
3. **Mreža**: Veći payload može uticati na brzinu prenosa
4. **Backward compatibility**: Stari endpoint-i i dalje rade sa manjim brojevima

## 🔍 **Testiranje**

### **Test 1: Maksimalni limit**
```bash
curl "http://localhost:8080/api/euk/ugrozena-lica-t1?page=0&size=50000"
```

### **Test 2: Prekoračenje limita**
```bash
curl "http://localhost:8080/api/euk/ugrozena-lica-t1?page=0&size=50001"
# Očekivani odgovor: 400 Bad Request
```

### **Test 3: Legacy endpoint**
```bash
curl "http://localhost:8080/api/euk/ugrozena-lica?page=0&size=30000"
```

## 📝 **Datum promene**

**Datum**: $(date)
**Verzija**: 1.0
**Status**: ✅ Implementirano
