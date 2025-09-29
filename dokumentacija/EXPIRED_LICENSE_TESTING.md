# 🚫 Expired License Testing Guide

## 📋 Pregled

Ovaj dokument opisuje kako da testirate scenarij sa isteklom licencom - kada korisnik pokušava da se uloguje sa licencom koja je istekla.

## 🚀 Brzo Pokretanje

### 1. Kreiranje Test Podataka

```bash
# Windows Batch
run_expired_license_test.bat

# Windows PowerShell  
.\run_expired_license_test.ps1

# Linux/Mac
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/test_expired_license.sql
```

### 2. Test Korisnik

| Polje | Vrednost |
|-------|----------|
| **Username** | `testuser_expired_license` |
| **Email** | `test.expired@example.com` |
| **Password** | `testpass123` |
| **License Status** | **EXPIRED** (istekla pre 30 dana) |
| **Days Since Expiry** | -30 dana |

## 🔍 Testiranje API Endpoint-a

### 1. **Provera Statusa Licence**

```bash
# Test korisnik sa isteklom licencom
curl "http://localhost:8080/api/licenses/status?userId=<user_id>"

# Očekivani odgovor:
{
  "hasValidLicense": false,
  "endDate": "2024-11-01T00:00:00",
  "daysUntilExpiry": -30,
  "isExpiringSoon": false
}
```

### 2. **Provera Važenja Licence**

```bash
# Test korisnik sa isteklom licencom
curl "http://localhost:8080/api/licenses/check/<user_id>"

# Očekivani odgovor:
{
  "hasValidLicense": false,
  "message": "License is invalid or expired"
}
```

### 3. **Testiranje Login-a sa Isteklom Licencom**

```bash
# Login sa test korisnikom (TREBALO BI DA NE RADI)
curl -X POST "http://localhost:8080/api/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser_expired_license",
    "password": "testpass123"
  }'

# Očekivani odgovor: 401 Unauthorized
{
  "errorCode": "AUTH_ERROR",
  "message": "Neispravno korisničko ime ili lozinka",
  "path": "/api/auth/signin"
}
```

## 🎯 Test Scenariji

### **Scenario 1: Login sa Isteklom Licencom**

1. **Pokušaj login-a** - ❌ Trebalo bi da ne prođe (401)
2. **Pristup aplikaciji** - ❌ Trebalo bi da bude blokiran
3. **Obaveštenje** - 🚫 Trebalo bi da se prikaže greška o isteku

### **Scenario 2: Provera Statusa Licence**

1. **License status** - ❌ Trebalo bi da pokazuje `hasValidLicense: false`
2. **Days until expiry** - ❌ Trebalo bi da pokazuje negativan broj
3. **Expiring soon** - ❌ Trebalo bi da pokazuje `false`

### **Scenario 3: Admin Panel**

1. **Expired licenses** - ✅ Trebalo bi da se prikaže u admin panelu
2. **Deactivation** - ✅ Trebalo bi da se može deaktivirati
3. **Notification** - ✅ Trebalo bi da se može poslati obaveštenje

## 🔧 Frontend Testiranje

### **1. Login Form sa Isteklom Licencom**

```typescript
// Test komponenta za login sa isteklom licencom
const ExpiredLicenseLoginTest = () => {
  const [loginResult, setLoginResult] = useState(null);
  
  const testExpiredLogin = async () => {
    try {
      const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          usernameOrEmail: 'testuser_expired_license',
          password: 'testpass123'
        })
      });
      
      if (response.ok) {
        setLoginResult({ success: true, message: 'Login successful (UNEXPECTED!)' });
      } else {
        const errorData = await response.json();
        setLoginResult({ 
          success: false, 
          status: response.status,
          message: errorData.message,
          expected: 'Should fail with 401'
        });
      }
    } catch (error) {
      setLoginResult({ 
        success: false, 
        error: error.message,
        expected: 'Should fail with 401'
      });
    }
  };
  
  return (
    <div>
      <h2>Expired License Login Test</h2>
      <button onClick={testExpiredLogin}>
        Test Login with Expired License
      </button>
      
      {loginResult && (
        <div style={{ 
          padding: '1rem', 
          border: '1px solid #ccc',
          backgroundColor: loginResult.success ? '#ffebee' : '#e8f5e8'
        }}>
          <h3>Result:</h3>
          <pre>{JSON.stringify(loginResult, null, 2)}</pre>
        </div>
      )}
    </div>
  );
};
```

### **2. License Status Component**

```typescript
// Test komponenta za prikaz statusa istekle licence
const ExpiredLicenseStatusTest = () => {
  const [licenseStatus, setLicenseStatus] = useState(null);
  
  const testExpiredLicenseStatus = async () => {
    try {
      // Prvo pronađite user_id za test korisnika
      const response = await fetch('/api/licenses/status?userId=<user_id>');
      const data = await response.json();
      setLicenseStatus(data);
    } catch (error) {
      console.error('Error testing license status:', error);
    }
  };
  
  return (
    <div>
      <h2>Expired License Status Test</h2>
      <button onClick={testExpiredLicenseStatus}>
        Test Expired License Status
      </button>
      
      {licenseStatus && (
        <div>
          <h3>License Status:</h3>
          <div style={{ 
            padding: '1rem', 
            border: '1px solid #f44336',
            backgroundColor: '#ffebee'
          }}>
            <p><strong>Has Valid License:</strong> {licenseStatus.hasValidLicense ? '✅' : '❌'}</p>
            <p><strong>End Date:</strong> {licenseStatus.endDate}</p>
            <p><strong>Days Until Expiry:</strong> {licenseStatus.daysUntilExpiry}</p>
            <p><strong>Is Expiring Soon:</strong> {licenseStatus.isExpiringSoon ? '⚠️' : '❌'}</p>
          </div>
        </div>
      )}
    </div>
  );
};
```

### **3. Error Popup za Isteklu Licencu**

```typescript
// Test popup-a za isteklu licencu
const ExpiredLicenseErrorPopup = () => {
  return (
    <div className="expired-license-popup">
      <div className="error-icon">🚫</div>
      <h3>Licenca je Istekla!</h3>
      <p>
        Vaša licenca je istekla pre 30 dana.
        Kontaktirajte administratora za obnovu licence.
      </p>
      <div className="error-actions">
        <button className="btn-primary">Kontaktiraj Administratora</button>
        <button className="btn-secondary">Zatvori</button>
      </div>
    </div>
  );
};
```

## 📊 Database Queries za Testiranje

### **1. Proverite Test Podatke**

```sql
-- Proverite da li je test korisnik kreiran
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
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM users u
LEFT JOIN licenses l ON u.id = l.user_id
WHERE u.username = 'testuser_expired_license';
```

### **2. Testiranje Licence Funkcija**

```sql
-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id = (SELECT id FROM users WHERE username = 'testuser_expired_license');

-- Očekivani rezultat: is_expired = true, days_until_expiry = -30
```

### **3. Testiranje Deaktivacije Isteklih Licenci**

```sql
-- Deaktiviraj istekle licence
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE end_date < CURRENT_TIMESTAMP AND is_active = true;

-- Proverite da li je licenca deaktivirana
SELECT 
    u.username,
    l.end_date,
    l.is_active,
    CASE 
        WHEN l.end_date < CURRENT_TIMESTAMP THEN 'EXPIRED'
        WHEN l.end_date BETWEEN CURRENT_TIMESTAMP AND CURRENT_TIMESTAMP + INTERVAL '30 days' THEN 'EXPIRING_SOON'
        ELSE 'VALID'
    END as license_status,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM users u
JOIN licenses l ON u.id = l.user_id
WHERE u.username = 'testuser_expired_license';
```

## 🧹 Čišćenje Test Podataka

```sql
-- Obriši test korisnika i licencu
DELETE FROM licenses WHERE user_id = (
    SELECT id FROM users WHERE username = 'testuser_expired_license'
);

DELETE FROM users WHERE username = 'testuser_expired_license';
```

## 🎯 Očekivani Rezultati

### **Login Test**
- ❌ **Login ne radi** (401 Unauthorized)
- ❌ **Pristup aplikaciji blokiran**
- 🚫 **Prikazuje se greška o isteku**

### **License Status Test**
- ❌ **hasValidLicense: false**
- ❌ **daysUntilExpiry: -30**
- ❌ **isExpiringSoon: false**

### **Admin Panel Test**
- ✅ **Prikazuje se u expired licenses**
- ✅ **Može se deaktivirati**
- ✅ **Može se poslati obaveštenje**

## 📝 Napomene

- **Test podaci** se kreiraju samo za testiranje
- **Ne koristiti** u produkciji
- **Obrišite test podatke** nakon testiranja
- **Proverite logove** za detaljne informacije o greškama
- **Očekivano ponašanje** je da login ne radi sa isteklom licencom
