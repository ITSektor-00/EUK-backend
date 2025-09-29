# 🚫 User 1 Expired License Testing Guide

## 📋 Pregled

Ovaj dokument opisuje kako da testirate scenarij sa isteklom licencom za korisnika sa ID 1.

## 🚀 Brzo Pokretanje

### 1. Postavljanje Istekle Licence

```bash
# Windows Batch
expire_user_1_license.bat

# Windows PowerShell  
.\expire_user_1_license.ps1

# Linux/Mac
psql -h aws-0-eu-central-1.pooler.supabase.com -p 6543 -U postgres.wynfrojhkzddzjbrpdcr -d postgres -f postgresQuery/expire_user_1_license.sql
```

### 2. Test Korisnik

| Polje | Vrednost |
|-------|----------|
| **User ID** | `1` |
| **License Status** | **EXPIRED** (istekla pre 30 dana) |
| **Days Since Expiry** | -30 dana |

## 🔍 Testiranje API Endpoint-a

### 1. **Provera Statusa Licence**

```bash
# Test korisnik sa ID 1 (istekla licenca)
curl "http://localhost:8080/api/licenses/status?userId=1"

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
# Test korisnik sa ID 1 (istekla licenca)
curl "http://localhost:8080/api/licenses/check/1"

# Očekivani odgovor:
{
  "hasValidLicense": false,
  "message": "License is invalid or expired"
}
```

### 3. **Testiranje Login-a sa Isteklom Licencom**

```bash
# Login sa korisnikom ID 1 (TREBALO BI DA NE RADI)
curl -X POST "http://localhost:8080/api/auth/signin" \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "<username_or_email_for_user_1>",
    "password": "<password_for_user_1>"
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

1. **Expired licenses** - ✅ Trebalo bi da se prikaže korisnik ID 1
2. **Deactivation** - ✅ Trebalo bi da se može deaktivirati
3. **Notification** - ✅ Trebalo bi da se može poslati obaveštenje

## 🔧 Frontend Testiranje

### **1. Login Form sa Isteklom Licencom**

```typescript
// Test komponenta za login sa isteklom licencom
const User1ExpiredLoginTest = () => {
  const [loginResult, setLoginResult] = useState(null);
  
  const testUser1ExpiredLogin = async () => {
    try {
      const response = await fetch('/api/auth/signin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          usernameOrEmail: '<username_or_email_for_user_1>',
          password: '<password_for_user_1>'
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
      <h2>User 1 Expired License Login Test</h2>
      <button onClick={testUser1ExpiredLogin}>
        Test Login with User 1 (Expired License)
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
const User1LicenseStatusTest = () => {
  const [licenseStatus, setLicenseStatus] = useState(null);
  
  const testUser1LicenseStatus = async () => {
    try {
      const response = await fetch('/api/licenses/status?userId=1');
      const data = await response.json();
      setLicenseStatus(data);
    } catch (error) {
      console.error('Error testing license status:', error);
    }
  };
  
  return (
    <div>
      <h2>User 1 License Status Test</h2>
      <button onClick={testUser1LicenseStatus}>
        Test User 1 License Status
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
const User1ExpiredLicenseErrorPopup = () => {
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
-- Proverite da li je korisnik sa ID 1 kreiran
SELECT 
    u.id,
    u.username,
    u.email,
    u.created_at,
    u.updated_at
FROM users u
WHERE u.id = 1;

-- Proverite licencu korisnika sa ID 1
SELECT 
    l.id as license_id,
    l.user_id,
    l.start_date,
    l.end_date,
    l.is_active,
    l.notification_sent,
    l.created_at,
    l.updated_at,
    EXTRACT(DAY FROM (l.end_date - CURRENT_TIMESTAMP))::INT as days_since_expiry
FROM licenses l
WHERE l.user_id = 1;
```

### **2. Testiranje Licence Funkcija**

```sql
-- Testiranje funkcija za proveru važenja licence
SELECT * FROM check_license_validity() 
WHERE user_id = 1;

-- Očekivani rezultat: is_expired = true, days_until_expiry = -30
```

### **3. Testiranje Deaktivacije Isteklih Licenci**

```sql
-- Deaktiviraj istekle licence
UPDATE licenses 
SET is_active = false, updated_at = CURRENT_TIMESTAMP 
WHERE user_id = 1 AND end_date < CURRENT_TIMESTAMP;

-- Proverite da li je licenca deaktivirana
SELECT 
    u.id,
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
WHERE u.id = 1;
```

## 🧹 Vraćanje Licence u Normalno Stanje

```sql
-- Vrati licencu korisnika sa ID 1 u normalno stanje (ističe za 12 meseci)
UPDATE licenses 
SET 
    end_date = CURRENT_TIMESTAMP + INTERVAL '12 months',
    is_active = true,
    updated_at = CURRENT_TIMESTAMP
WHERE user_id = 1;
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
- ✅ **Prikazuje se korisnik ID 1 u expired licenses**
- ✅ **Može se deaktivirati**
- ✅ **Može se poslati obaveštenje**

## 📝 Napomene

- **Test podaci** se kreiraju samo za testiranje
- **Ne koristiti** u produkciji
- **Vratite licencu** u normalno stanje nakon testiranja
- **Proverite logove** za detaljne informacije o greškama
- **Očekivano ponašanje** je da login ne radi sa isteklom licencom
