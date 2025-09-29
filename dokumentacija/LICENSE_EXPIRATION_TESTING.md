# 🧪 License Expiration Testing Guide

## 📋 Pregled

Ovaj dokument opisuje kako da testirate licencni sistem sa korisnicima koji imaju licence koje ističu u različitim vremenskim periodima.

## 🚀 Brzo Pokretanje

### 1. Kreiranje Test Podataka

```bash
# Windows Batch
run_license_test_data.bat

# Windows PowerShell  
.\run_license_test_data.ps1

# Linux/Mac
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/test_license_expiration.sql
```

### 2. Test Korisnici

| Korisnik | Email | Lozinka | Status Licence | Dana do isteka |
|----------|-------|---------|----------------|----------------|
| `testuser_license_expires_30_days` | test.license.30days@example.com | testpass123 | Važeća | 30 dana |
| `testuser_license_expires_15_days` | test.license.15days@example.com | testpass123 | Važeća | 15 dana |
| `testuser_license_expires_5_days` | test.license.5days@example.com | testpass123 | Važeća | 5 dana |
| `testuser_license_expired` | test.license.expired@example.com | testpass123 | **Istekla** | -30 dana |

## 🔍 Testiranje API Endpoint-a

### 1. **Provera Statusa Licence**

```bash
# Test korisnik sa licencom koja ističe za 30 dana
curl "http://localhost:8080/api/licenses/status?userId=1"

# Očekivani odgovor:
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 30,
  "isExpiringSoon": true
}
```

### 2. **Provera Važenja Licence**

```bash
# Test korisnik sa licencom koja ističe za 15 dana
curl "http://localhost:8080/api/licenses/check/2"

# Očekivani odgovor:
{
  "hasValidLicense": true,
  "message": "License is valid"
}
```

### 3. **Admin - Licence koje Ističu**

```bash
# Admin endpoint za licence koje ističu
curl "http://localhost:8080/api/licenses/admin/expiring"

# Očekivani odgovor:
{
  "success": true,
  "expiringLicenses": [
    {
      "id": 1,
      "userId": 1,
      "endDate": "2024-12-31T23:59:59",
      "isActive": true
    }
  ],
  "count": 3
}
```

### 4. **Testiranje Login-a**

```bash
# Login sa test korisnikom
curl -X POST "http://localhost:8080/api/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser_license_expires_30_days",
    "password": "testpass123"
  }'

# Očekivani odgovor:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "username": "testuser_license_expires_30_days",
    "email": "test.license.30days@example.com",
    "role": "korisnik"
  }
}
```

## 🎯 Test Scenariji

### **Scenario 1: Korisnik sa Licencom koja Ističe za 30 Dana**

1. **Login** - ✅ Trebalo bi da prođe
2. **Pristup aplikaciji** - ✅ Trebalo bi da radi
3. **Obaveštenje** - ⚠️ Trebalo bi da se prikaže upozorenje

### **Scenario 2: Korisnik sa Licencom koja Ističe za 15 Dana**

1. **Login** - ✅ Trebalo bi da prođe
2. **Pristup aplikaciji** - ✅ Trebalo bi da radi
3. **Obaveštenje** - ⚠️ Trebalo bi da se prikaže hitno upozorenje

### **Scenario 3: Korisnik sa Licencom koja Ističe za 5 Dana**

1. **Login** - ✅ Trebalo bi da prođe
2. **Pristup aplikaciji** - ✅ Trebalo bi da radi
3. **Obaveštenje** - 🚨 Trebalo bi da se prikaže vrlo hitno upozorenje

### **Scenario 4: Korisnik sa Isteklom Licencom**

1. **Login** - ❌ Trebalo bi da ne prođe (401)
2. **Pristup aplikaciji** - ❌ Trebalo bi da bude blokiran
3. **Obaveštenje** - 🚫 Trebalo bi da se prikaže greška

## 🔧 Frontend Testiranje

### **1. License Status Component**

```typescript
// Test komponenta za prikaz statusa licence
const LicenseStatusTest = () => {
  const [licenseStatus, setLicenseStatus] = useState(null);
  
  const testUsers = [
    { id: 1, name: "30 days", username: "testuser_license_expires_30_days" },
    { id: 2, name: "15 days", username: "testuser_license_expires_15_days" },
    { id: 3, name: "5 days", username: "testuser_license_expires_5_days" },
    { id: 4, name: "Expired", username: "testuser_license_expired" }
  ];
  
  const testLicenseStatus = async (userId) => {
    try {
      const response = await fetch(`/api/licenses/status?userId=${userId}`);
      const data = await response.json();
      setLicenseStatus(data);
    } catch (error) {
      console.error('Error testing license status:', error);
    }
  };
  
  return (
    <div>
      <h2>License Status Testing</h2>
      {testUsers.map(user => (
        <button key={user.id} onClick={() => testLicenseStatus(user.id)}>
          Test {user.name} User
        </button>
      ))}
      
      {licenseStatus && (
        <div>
          <h3>License Status:</h3>
          <pre>{JSON.stringify(licenseStatus, null, 2)}</pre>
        </div>
      )}
    </div>
  );
};
```

### **2. License Expiration Popup**

```typescript
// Test popup-a za istek licence
const LicenseExpirationPopup = ({ licenseStatus }) => {
  if (!licenseStatus?.isExpiringSoon) return null;
  
  const daysLeft = licenseStatus.daysUntilExpiry;
  
  return (
    <div className="license-warning-popup">
      <div className="warning-icon">⚠️</div>
      <h3>Licenca Ističe Uskoro!</h3>
      <p>
        Vaša licenca ističe za {daysLeft} dana.
        Kontaktirajte administratora za obnovu.
      </p>
      <button>Kontaktiraj Administratora</button>
    </div>
  );
};
```

## 📊 Database Queries za Testiranje

### **1. Proverite Test Podatke**

```sql
-- Proverite da li su test korisnici kreirani
SELECT 
    u.id,
    u.username,
    u.email,
    u.created_at,
    u.updated_at,
    l.id as license_id,
    l.start_date,
    l.end_date,
    l.is_active,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_until_expiry
FROM users u
LEFT JOIN licenses l ON u.id = l.user_id
WHERE u.username LIKE 'testuser_license%'
ORDER BY l.end_date;
```

### **2. Testiranje Licence Funkcija**

```sql
-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id IN (
    SELECT id FROM users WHERE username LIKE 'testuser_license%'
);

-- Testiranje funkcija za licence koje treba da isteknu
SELECT * FROM get_licenses_expiring_soon();
```

### **3. Testiranje Deaktivacije Isteklih Licenci**

```sql
-- Deaktiviraj istekle licence
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

-- Proverite rezultate
SELECT 
    u.username,
    l.end_date,
    l.is_active,
    CASE 
        WHEN l.end_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN l.end_date BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.username LIKE 'testuser_license%'
ORDER BY l.end_date;
```

## 🧹 Čišćenje Test Podataka

```sql
-- Obriši test korisnike i licence
DELETE FROM licenses WHERE user_id IN (
    SELECT id FROM users WHERE username LIKE 'testuser_license%'
);

DELETE FROM users WHERE username LIKE 'testuser_license%';
```

## 🎯 Očekivani Rezultati

### **30 Dana do Isteka**
- ✅ Login radi
- ✅ Pristup aplikaciji radi
- ⚠️ Prikazuje se upozorenje o isteku

### **15 Dana do Isteka**
- ✅ Login radi
- ✅ Pristup aplikaciji radi
- ⚠️ Prikazuje se hitno upozorenje

### **5 Dana do Isteka**
- ✅ Login radi
- ✅ Pristup aplikaciji radi
- 🚨 Prikazuje se vrlo hitno upozorenje

### **Istekla Licenca**
- ❌ Login ne radi (401)
- ❌ Pristup aplikaciji blokiran
- 🚫 Prikazuje se greška o isteku

## 📝 Napomene

- **Test podaci** se kreiraju samo za testiranje
- **Ne koristiti** u produkciji
- **Obrišite test podatke** nakon testiranja
- **Proverite logove** za detaljne informacije o greškama
