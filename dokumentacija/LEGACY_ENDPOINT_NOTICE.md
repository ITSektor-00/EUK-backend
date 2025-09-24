# Legacy Endpoint Notice - EukUgrozenoLice

## ⚠️ VAŽNO: Legacy Endpoint je kreiran za backward compatibility

Kreiran je **legacy controller** `EukUgrozenoLiceController.java` koji eksponira stari endpoint `/api/euk/ugrozena-lica` za backward compatibility sa postojećim frontend kodom.

### **Legacy Endpoint-i:**

- `GET /api/euk/ugrozena-lica` - Lista svih ugroženih lica sa paginacijom
- `GET /api/euk/ugrozena-lica/{id}` - Dohvatanje ugroženog lica po ID-u
- `POST /api/euk/ugrozena-lica` - Kreiranje novog ugroženog lica
- `PUT /api/euk/ugrozena-lica/{id}` - Ažuriranje ugroženog lica
- `DELETE /api/euk/ugrozena-lica/{id}` - Brisanje ugroženog lica
- `GET /api/euk/ugrozena-lica/search/{jmbg}` - Pretraga po JMBG-u
- `GET /api/euk/ugrozena-lica/test` - Test endpoint

### **Kako radi:**

Legacy controller **internally redirects** sve zahteve na novi `EukUgrozenoLiceT1Service` i koristi novu tabelu `ugrozeno_lice_t1`.

### **Status:**

- ✅ **RADI** - Legacy endpoint je funkcionalan
- ⚠️ **DEPRECATED** - Oznaka `@Deprecated` je dodana
- 📝 **LOGGING** - Svi pozivi se loguju sa "(LEGACY)" oznakom

### **Preporuka za Frontend tim:**

1. **Kratkoročno**: Legacy endpoint će raditi dok frontend ne migrira
2. **Dugoročno**: Migrirati na nove endpoint-e `/api/euk/ugrozena-lica-t1`
3. **Testiranje**: Koristiti `/api/euk/ugrozena-lica/test` za proveru statusa

### **Uklanjanje Legacy endpoint-a:**

Kada frontend tim završi migraciju, legacy controller može biti obrisan:
```bash
# Brisanje legacy fajla
rm src/main/java/com/sirus/backend/controller/EukUgrozenoLiceController.java
```

---

**Rezultat**: Frontend sada može da koristi stari endpoint `/api/euk/ugrozena-lica` dok ne završi migraciju na `/api/euk/ugrozena-lica-t1`.
