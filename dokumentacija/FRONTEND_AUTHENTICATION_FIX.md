# Frontend Authentication Fix - Rešavanje "No authentication token found" greške

## Problem
Frontend pokušava da šalje authentication token za endpoint-ove koji ne zahtevaju autentifikaciju u development modu.

## Rešenje

### 1. Kreiranje development-friendly servisa

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

    const response = await fetch(url, {
      method: 'GET',
      headers,
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
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
      const [kategorije, obrasciVrste, organizacionaStruktura, predmeti, t1Lice, t2Lice] = await Promise.allSettled([
        this.getKategorije(),
        this.getObrasciVrste(),
        this.getOrganizacionaStruktura(),
        this.getPredmeti(),
        this.getT1Lice(),
        this.getT2Lice()
      ]);

      return {
        kategorije: kategorije.status === 'fulfilled' ? kategorije.value : [],
        obrasciVrste: obrasciVrste.status === 'fulfilled' ? obrasciVrste.value : [],
        organizacionaStruktura: organizacionaStruktura.status === 'fulfilled' ? organizacionaStruktura.value : [],
        predmeti: predmeti.status === 'fulfilled' ? predmeti.value : [],
        t1Lice: t1Lice.status === 'fulfilled' ? t1Lice.value : [],
        t2Lice: t2Lice.status === 'fulfilled' ? t2Lice.value : []
      };
    } catch (error) {
      console.error('Error loading all data:', error);
      throw error;
    }
  }
}

export default new DevelopmentTemplateService();
```

### 2. Ažuriranje komponente

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
      console.log('Loading data with development service...');
      const result = await developmentTemplateService.loadAllData();
      
      if (mountedRef.current) {
        console.log('Data loaded successfully:', {
          kategorije: result.kategorije.length,
          obrasciVrste: result.obrasciVrste.length,
          organizacionaStruktura: result.organizacionaStruktura.length,
          predmeti: result.predmeti.length,
          t1Lice: result.t1Lice.length,
          t2Lice: result.t2Lice.length
        });
        
        setData(result);
      }
    } catch (err) {
      if (mountedRef.current) {
        setError('Greška pri učitavanju podataka');
        console.error('Error loading data:', err);
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

### 3. Environment-based service selection

```typescript
// services/templateServiceFactory.ts
import developmentTemplateService from './developmentTemplateService';
import optimizedTemplateService from './optimizedTemplateService';

const isDevelopment = process.env.NODE_ENV === 'development';

export default isDevelopment ? developmentTemplateService : optimizedTemplateService;
```

### 4. Ažuriranje postojeće komponente

```typescript
// components/OdbijaSeTemplateForm.tsx
import React, { useState, useEffect, useRef } from 'react';
import templateService from '../services/templateServiceFactory';

const OdbijaSeTemplateForm: React.FC = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const mountedRef = useRef(true);

  useEffect(() => {
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const loadInitialData = async () => {
    if (!mountedRef.current) return;
    
    setLoading(true);
    setError(null);
    
    try {
      console.log('Loading initial data...');
      const result = await templateService.loadAllData();
      
      if (mountedRef.current) {
        console.log('Initial data loaded:', result);
        setData(result);
      }
    } catch (err) {
      if (mountedRef.current) {
        setError('Greška pri učitavanju početnih podataka');
        console.error('Error loading initial data:', err);
      }
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  };

  useEffect(() => {
    loadInitialData();
  }, []);

  // Rest of component...
};
```

### 5. Dodatne optimizacije

```typescript
// utils/requestLogger.ts
export const logRequest = (endpoint: string, isDevelopment: boolean) => {
  if (isDevelopment) {
    console.log(`[DEV] Making request to ${endpoint} without authentication`);
  }
};

export const logResponse = (endpoint: string, success: boolean, data?: any) => {
  if (process.env.NODE_ENV === 'development') {
    console.log(`[DEV] Response from ${endpoint}:`, success ? 'SUCCESS' : 'ERROR', data);
  }
};
```

### 6. CSS stilovi

```css
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container {
  padding: 2rem;
  text-align: center;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  color: #721c24;
}

.retry-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}

.retry-button:hover {
  background-color: #0056b3;
}

.data-summary {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.data-summary h3 {
  margin-top: 0;
  color: #495057;
}

.data-summary p {
  margin: 0.25rem 0;
  font-weight: 500;
}
```

## Implementacija koraka

1. **Kreirajte development-friendly servis** - ne šalje token u development modu
2. **Implementirajte service factory** - automatski bira pravi servis
3. **Ažurirajte komponente** - koristite factory servis
4. **Dodajte logging** - bolje debugging
5. **Testirajte** - proverite da li se greške rešavaju

## Napomene

- Development servis ne šalje authentication token
- Production servis koristi optimizovane servise sa token-om
- Service factory automatski bira pravi servis
- Dodatno logging za bolje debugging
