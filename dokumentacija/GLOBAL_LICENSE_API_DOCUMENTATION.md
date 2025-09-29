# Global License API Documentation

## Base URL
```
http://localhost:8080/api/global-license
```

## API Endpoints

### 1. **GET /api/global-license/status**
**Opis:** Proverava status globalne licence

**Request:**
```http
GET /api/global-license/status
```

**Response:**
```json
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 45,
  "isExpiringSoon": false,
  "message": "License is valid"
}
```

**Testiranje:**
```bash
curl -X GET "http://localhost:8080/api/global-license/status"
```

---

### 2. **GET /api/global-license/check**
**Opis:** Proverava da li postoji važeća globalna licenca

**Request:**
```http
GET /api/global-license/check
```

**Response:**
```json
{
  "hasValidLicense": true,
  "message": "Global license is valid"
}
```

**Testiranje:**
```bash
curl -X GET "http://localhost:8080/api/global-license/check"
```

---

### 3. **GET /api/global-license/active**
**Opis:** Vraća aktivnu globalnu licencu

**Request:**
```http
GET /api/global-license/active
```

**Response:**
```json
{
  "success": true,
  "license": {
    "id": 1,
    "licenseKey": "GLOBAL-LICENSE-2024-30DAYS",
    "startDate": "2024-09-29T12:00:00",
    "endDate": "2024-10-29T12:00:00",
    "isActive": true,
    "notificationSent": false,
    "createdAt": "2024-09-29T12:00:00",
    "updatedAt": "2024-09-29T12:00:00"
  }
}
```

**Testiranje:**
```bash
curl -X GET "http://localhost:8080/api/global-license/active"
```

---

### 4. **POST /api/global-license/create**
**Opis:** Kreira novu globalnu licencu (Admin endpoint)

**Request:**
```http
POST /api/global-license/create?licenseKey=NEW-LICENSE-KEY&months=12
```

**Response:**
```json
{
  "success": true,
  "licenseId": 1,
  "licenseKey": "NEW-LICENSE-KEY",
  "startDate": "2024-09-29T12:00:00",
  "endDate": "2025-09-29T12:00:00",
  "message": "Global license created successfully"
}
```

**Testiranje:**
```bash
curl -X POST "http://localhost:8080/api/global-license/create?licenseKey=NEW-LICENSE-KEY&months=12"
```

---

### 5. **POST /api/global-license/extend**
**Opis:** Proširuje globalnu licencu (Admin endpoint)

**Request:**
```http
POST /api/global-license/extend?months=6
```

**Response:**
```json
{
  "success": true,
  "licenseId": 1,
  "newEndDate": "2025-03-29T12:00:00",
  "message": "Global license extended successfully"
}
```

**Testiranje:**
```bash
curl -X POST "http://localhost:8080/api/global-license/extend?months=6"
```

---

### 6. **POST /api/global-license/admin/deactivate-expired**
**Opis:** Admin endpoint - deaktivira istekli globalnu licencu

**Request:**
```http
POST /api/global-license/admin/deactivate-expired
```

**Response:**
```json
{
  "success": true,
  "deactivatedCount": 1,
  "message": "Deactivated 1 expired global license(s)"
}
```

**Testiranje:**
```bash
curl -X POST "http://localhost:8080/api/global-license/admin/deactivate-expired"
```

---

## Frontend Integration

### 1. **JavaScript Fetch Examples:**

**Proveri status:**
```javascript
fetch('/api/global-license/status')
  .then(response => response.json())
  .then(data => {
    console.log('License Status:', data);
    if (data.hasValidLicense) {
      console.log('License is valid until:', data.endDate);
      console.log('Days until expiry:', data.daysUntilExpiry);
    }
  })
  .catch(error => console.error('Error:', error));
```

**Proveri da li je validna:**
```javascript
fetch('/api/global-license/check')
  .then(response => response.json())
  .then(data => {
    console.log('License Check:', data);
    if (data.hasValidLicense) {
      console.log('License is valid');
    } else {
      console.log('License is invalid or expired');
    }
  })
  .catch(error => console.error('Error:', error));
```

**Dobij aktivnu licencu:**
```javascript
fetch('/api/global-license/active')
  .then(response => response.json())
  .then(data => {
    console.log('Active License:', data);
    if (data.success && data.license) {
      console.log('License Key:', data.license.licenseKey);
      console.log('End Date:', data.license.endDate);
    }
  })
  .catch(error => console.error('Error:', error));
```

### 2. **Angular/React Service Examples:**

**Angular Service:**
```typescript
@Injectable()
export class GlobalLicenseService {
  private baseUrl = '/api/global-license';

  getStatus() {
    return this.http.get(`${this.baseUrl}/status`);
  }

  checkLicense() {
    return this.http.get(`${this.baseUrl}/check`);
  }

  getActiveLicense() {
    return this.http.get(`${this.baseUrl}/active`);
  }
}
```

**React Hook:**
```typescript
const useGlobalLicense = () => {
  const [licenseStatus, setLicenseStatus] = useState(null);

  useEffect(() => {
    fetch('/api/global-license/status')
      .then(response => response.json())
      .then(data => setLicenseStatus(data))
      .catch(error => console.error('Error:', error));
  }, []);

  return licenseStatus;
};
```

## Error Handling

### Common Error Responses:

**400 Bad Request:**
```json
{
  "error": "Error checking global license status: [error message]"
}
```

**500 Internal Server Error:**
```json
{
  "success": false,
  "error": "Error creating global license: [error message]"
}
```

## Testing

### 1. **Test sve endpoint-e:**
```bash
# Test status
curl -X GET "http://localhost:8080/api/global-license/status"

# Test check
curl -X GET "http://localhost:8080/api/global-license/check"

# Test active
curl -X GET "http://localhost:8080/api/global-license/active"

# Test create (Admin)
curl -X POST "http://localhost:8080/api/global-license/create?licenseKey=TEST-LICENSE&months=12"

# Test extend (Admin)
curl -X POST "http://localhost:8080/api/global-license/extend?months=6"

# Test deactivate (Admin)
curl -X POST "http://localhost:8080/api/global-license/admin/deactivate-expired"
```

### 2. **Test u browser console:**
```javascript
// Test sve endpoint-e
Promise.all([
  fetch('/api/global-license/status').then(r => r.json()),
  fetch('/api/global-license/check').then(r => r.json()),
  fetch('/api/global-license/active').then(r => r.json())
]).then(([status, check, active]) => {
  console.log('Status:', status);
  console.log('Check:', check);
  console.log('Active:', active);
});
```

## Security Notes

- **Admin endpoints** (`/create`, `/extend`, `/admin/deactivate-expired`) treba da imaju autentifikaciju
- **CORS** je konfigurisan za sve origin-e (`@CrossOrigin(origins = "*")`)
- **Interceptor** exclude global license endpoint-e od licence provere
- **Rate limiting** exclude global license endpoint-e

## Troubleshooting

### 1. **Ako dobiješ 403 grešku:**
- Proveri da li je interceptor pravilno konfigurisan
- Proveri da li su endpoint-i excluded

### 2. **Ako dobiješ 404 grešku:**
- Proveri da li je backend pokrenut
- Proveri da li je URL ispravan

### 3. **Ako dobiješ 500 grešku:**
- Proveri da li je `GlobalLicenseService` pravilno konfigurisan
- Proveri da li je tabela kreirana u bazi
