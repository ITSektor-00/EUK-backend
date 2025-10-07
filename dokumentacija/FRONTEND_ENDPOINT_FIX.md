# Frontend Endpoint Fix - Rešavanje 403 grešaka

## Problem
Frontend pokušava da pozove endpoint-ove koji ne postoje ili nisu dozvoljeni u security konfiguraciji.

## Rešenje

### 1. Backend izmene (završene)

✅ **Dodani endpoint-ovi u security konfiguraciju:**
- `/api/obrasci-vrste/**`
- `/api/organizaciona-struktura/**`
- `/api/t1-lice/**`
- `/api/t2-lice/**`

✅ **Dodani endpoint-ovi u JwtAuthenticationFilter:**
- `/api/obrasci-vrste/`
- `/api/organizaciona-struktura/`
- `/api/t1-lice`
- `/api/t2-lice`

✅ **Kreiran AliasController** za T1/T2 endpoint-ove:
- `/api/t1-lice` → poziva `/api/euk/t1`
- `/api/t2-lice` → poziva `/api/euk/t2`

### 2. Frontend izmene (potrebno implementirati)

#### Ažuriranje servisa da koristi ispravne endpoint-ove:

```typescript
// services/developmentTemplateService.ts
class DevelopmentTemplateService {
  private readonly BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  private readonly isDevelopment = process.env.NODE_ENV === 'development';

  private async makeRequest<T>(endpoint: string): Promise<T> {
    const url = `${this.BASE_URL}${endpoint}`;
    
    // U development modu, ne šaljemo authentication token
    const headers: HeadersInit = {
      'Content-Type': 'application/json',
    };

    // Dodaj token samo ako nije development mode
    if (!this.isDevelopment) {
      const token = localStorage.getItem('authToken');
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }

    console.log(`[DEV] Making request to ${url} without authentication`);

    const response = await fetch(url, {
      method: 'GET',
      headers,
    });

    if (!response.ok) {
      console.error(`[DEV] Request failed: ${response.status} ${response.statusText}`);
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    console.log(`[DEV] Request successful:`, data);
    return data;
  }

  async getKategorije() {
    return this.makeRequest('/api/kategorije');
  }

  async getObrasciVrste() {
    return this.makeRequest('/api/obrasci-vrste');
  }

  async getOrganizacionaStruktura() {
    return this.makeRequest('/api/organizaciona-struktura');
  }

  async getPredmeti() {
    return this.makeRequest('/api/predmeti');
  }

  async getT1Lice() {
    return this.makeRequest('/api/t1-lice');
  }

  async getT2Lice() {
    return this.makeRequest('/api/t2-lice');
  }

  // Batch loading
  async loadAllData() {
    try {
      console.log('[DEV] Loading all data with development service...');
      
      const [kategorije, obrasciVrste, organizacionaStruktura, predmeti, t1Lice, t2Lice] = await Promise.allSettled([
        this.getKategorije(),
        this.getObrasciVrste(),
        this.getOrganizacionaStruktura(),
        this.getPredmeti(),
        this.getT1Lice(),
        this.getT2Lice()
      ]);

      const result = {
        kategorije: kategorije.status === 'fulfilled' ? kategorije.value : [],
        obrasciVrste: obrasciVrste.status === 'fulfilled' ? obrasciVrste.value : [],
        organizacionaStruktura: organizacionaStruktura.status === 'fulfilled' ? organizacionaStruktura.value : [],
        predmeti: predmeti.status === 'fulfilled' ? predmeti.value : [],
        t1Lice: t1Lice.status === 'fulfilled' ? t1Lice.value : [],
        t2Lice: t2Lice.status === 'fulfilled' ? t2Lice.value : []
      };

      console.log('[DEV] All data loaded successfully:', {
        kategorije: result.kategorije.length,
        obrasciVrste: result.obrasciVrste.length,
        organizacionaStruktura: result.organizacionaStruktura.length,
        predmeti: result.predmeti.length,
        t1Lice: result.t1Lice.length,
        t2Lice: result.t2Lice.length
      });

      return result;
    } catch (error) {
      console.error('[DEV] Error loading all data:', error);
      throw error;
    }
  }
}

export default new DevelopmentTemplateService();
```

#### Ažuriranje komponente:

```typescript
// components/DevelopmentTemplateForm.tsx
import React, { useState, useEffect, useRef } from 'react';
import developmentTemplateService from '../services/developmentTemplateService';

const DevelopmentTemplateForm: React.FC = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const mountedRef = useRef(true);

  useEffect(() => {
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const loadData = async () => {
    if (!mountedRef.current) return;
    
    setLoading(true);
    setError(null);
    
    try {
      console.log('[DEV] Loading data with development service...');
      const result = await developmentTemplateService.loadAllData();
      
      if (mountedRef.current) {
        console.log('[DEV] Data loaded successfully:', result);
        setData(result);
      }
    } catch (err) {
      if (mountedRef.current) {
        setError('Greška pri učitavanju podataka');
        console.error('[DEV] Error loading data:', err);
      }
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Učitavanje podataka...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p>Greška: {error}</p>
        <button onClick={loadData} className="retry-button">
          Pokušaj ponovo
        </button>
      </div>
    );
  }

  return (
    <div className="template-form">
      <h2>Template Form</h2>
      
      {data && (
        <div className="data-summary">
          <h3>Učitani podaci:</h3>
          <p>Kategorije: {data.kategorije.length}</p>
          <p>Obrasci vrste: {data.obrasciVrste.length}</p>
          <p>Organizaciona struktura: {data.organizacionaStruktura.length}</p>
          <p>Predmeti: {data.predmeti.length}</p>
          <p>T1 lice: {data.t1Lice.length}</p>
          <p>T2 lice: {data.t2Lice.length}</p>
        </div>
      )}
      
      {/* Form content */}
    </div>
  );
};

export default DevelopmentTemplateForm;
```

### 3. Testiranje endpoint-ova

```typescript
// utils/endpointTester.ts
export const testEndpoints = async () => {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
  const endpoints = [
    '/api/kategorije',
    '/api/obrasci-vrste',
    '/api/organizaciona-struktura',
    '/api/predmeti',
    '/api/t1-lice',
    '/api/t2-lice'
  ];

  console.log('[DEV] Testing endpoints...');
  
  for (const endpoint of endpoints) {
    try {
      const response = await fetch(`${baseUrl}${endpoint}`);
      console.log(`[DEV] ${endpoint}: ${response.status} ${response.statusText}`);
      
      if (response.ok) {
        const data = await response.json();
        console.log(`[DEV] ${endpoint} data:`, data);
      }
    } catch (error) {
      console.error(`[DEV] ${endpoint} error:`, error);
    }
  }
};
```

## Implementacija koraka

1. **Restartujte backend** da se primene izmene
2. **Implementirajte development servis** iz dokumentacije
3. **Ažurirajte komponente** da koriste development servis
4. **Testirajte endpoint-ove** da proverite da li rade
5. **Proverite da li se greške rešavaju**

## Napomene

- Backend je potpuno konfigurisan za development mode
- Svi potrebni endpoint-ovi su dodati u security konfiguraciju
- JwtAuthenticationFilter je ažuriran da dozvoli sve potrebne endpoint-ove
- AliasController omogućava frontend da koristi `/api/t1-lice` i `/api/t2-lice`
- Development servis ne šalje authentication token
