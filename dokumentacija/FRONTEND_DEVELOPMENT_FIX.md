# Frontend Development Fix - Rešavanje 429 grešaka

## Problem
Frontend šalje previše zahteva zbog React development mode-a koji mount-uje komponente više puta.

## Rešenje

### 1. Kreiranje globalnog cache servisa

```typescript
// services/globalCacheService.ts
class GlobalCacheService {
  private static instance: GlobalCacheService;
  private cache = new Map<string, any>();
  private loadingPromises = new Map<string, Promise<any>>();
  private lastFetch = new Map<string, number>();
  private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minuta

  static getInstance(): GlobalCacheService {
    if (!GlobalCacheService.instance) {
      GlobalCacheService.instance = new GlobalCacheService();
    }
    return GlobalCacheService.instance;
  }

  async getCachedData<T>(key: string, fetchFn: () => Promise<T>): Promise<T> {
    const now = Date.now();
    const lastFetchTime = this.lastFetch.get(key) || 0;
    
    // Proveri da li je cache još uvek važeći
    if (this.cache.has(key) && (now - lastFetchTime) < this.CACHE_DURATION) {
      console.log(`Cache hit for ${key}`);
      return this.cache.get(key);
    }

    // Proveri da li se već učitava
    if (this.loadingPromises.has(key)) {
      console.log(`Loading in progress for ${key}`);
      return this.loadingPromises.get(key);
    }

    // Kreiraj novi promise
    const promise = fetchFn()
      .then(data => {
        this.cache.set(key, data);
        this.lastFetch.set(key, now);
        this.loadingPromises.delete(key);
        console.log(`Data cached for ${key}`);
        return data;
      })
      .catch(error => {
        this.loadingPromises.delete(key);
        console.error(`Error fetching ${key}:`, error);
        throw error;
      });

    this.loadingPromises.set(key, promise);
    return promise;
  }

  clearCache() {
    this.cache.clear();
    this.loadingPromises.clear();
    this.lastFetch.clear();
    console.log('Cache cleared');
  }

  clearCacheFor(key: string) {
    this.cache.delete(key);
    this.loadingPromises.delete(key);
    this.lastFetch.delete(key);
    console.log(`Cache cleared for ${key}`);
  }
}

export default GlobalCacheService.getInstance();
```

### 2. Optimizovani template service

```typescript
// services/optimizedTemplateService.ts
import globalCache from './globalCacheService';

class OptimizedTemplateService {
  private readonly BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

  async getKategorije() {
    return globalCache.getCachedData('kategorije', async () => {
      const response = await fetch(`${this.BASE_URL}/api/kategorije`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    });
  }

  async getObrasciVrste() {
    return globalCache.getCachedData('obrasci-vrste', async () => {
      const response = await fetch(`${this.BASE_URL}/api/obrasci-vrste`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    });
  }

  async getOrganizacionaStruktura() {
    return globalCache.getCachedData('organizaciona-struktura', async () => {
      const response = await fetch(`${this.BASE_URL}/api/organizaciona-struktura`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    });
  }

  async getPredmeti() {
    return globalCache.getCachedData('predmeti', async () => {
      const response = await fetch(`${this.BASE_URL}/api/predmeti`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    });
  }

  // Batch loading - učitaj sve odjednom
  async loadAllData() {
    try {
      const [kategorije, obrasciVrste, organizacionaStruktura, predmeti] = await Promise.allSettled([
        this.getKategorije(),
        this.getObrasciVrste(),
        this.getOrganizacionaStruktura(),
        this.getPredmeti()
      ]);

      return {
        kategorije: kategorije.status === 'fulfilled' ? kategorije.value : [],
        obrasciVrste: obrasciVrste.status === 'fulfilled' ? obrasciVrste.value : [],
        organizacionaStruktura: organizacionaStruktura.status === 'fulfilled' ? organizacionaStruktura.value : [],
        predmeti: predmeti.status === 'fulfilled' ? predmeti.value : []
      };
    } catch (error) {
      console.error('Error loading all data:', error);
      throw error;
    }
  }
}

export default new OptimizedTemplateService();
```

### 3. Optimizovana komponenta

```typescript
// components/OptimizedTemplateForm.tsx
import React, { useState, useEffect, useCallback } from 'react';
import optimizedTemplateService from '../services/optimizedTemplateService';

const OptimizedTemplateForm: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState({
    kategorije: [],
    obrasciVrste: [],
    organizacionaStruktura: [],
    predmeti: []
  });

  const loadData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      console.log('Loading all data with optimized service...');
      const result = await optimizedTemplateService.loadAllData();
      
      console.log('Loaded data:', {
        kategorije: result.kategorije.length,
        obrasciVrste: result.obrasciVrste.length,
        organizacionaStruktura: result.organizacionaStruktura.length,
        predmeti: result.predmeti.length
      });
      
      setData(result);
    } catch (err) {
      setError('Greška pri učitavanju podataka');
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadData();
  }, [loadData]);

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
      
      <div className="data-summary">
        <p>Kategorije: {data.kategorije.length}</p>
        <p>Obrasci vrste: {data.obrasciVrste.length}</p>
        <p>Organizaciona struktura: {data.organizacionaStruktura.length}</p>
        <p>Predmeti: {data.predmeti.length}</p>
      </div>
      
      {/* Form content */}
    </div>
  );
};

export default OptimizedTemplateForm;
```

### 4. CSS stilovi

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

.data-summary p {
  margin: 0.25rem 0;
  font-weight: 500;
}
```

### 5. Dodatne optimizacije

```typescript
// utils/requestThrottle.ts
class RequestThrottle {
  private static instance: RequestThrottle;
  private requestQueue: Array<() => Promise<any>> = [];
  private isProcessing = false;
  private readonly maxConcurrent = 2;
  private readonly delayBetweenRequests = 100; // 100ms između zahteva

  static getInstance(): RequestThrottle {
    if (!RequestThrottle.instance) {
      RequestThrottle.instance = new RequestThrottle();
    }
    return RequestThrottle.instance;
  }

  async execute<T>(requestFn: () => Promise<T>): Promise<T> {
    return new Promise((resolve, reject) => {
      this.requestQueue.push(async () => {
        try {
          const result = await requestFn();
          resolve(result);
        } catch (error) {
          reject(error);
        }
      });

      this.processQueue();
    });
  }

  private async processQueue() {
    if (this.isProcessing || this.requestQueue.length === 0) {
      return;
    }

    this.isProcessing = true;

    while (this.requestQueue.length > 0) {
      const batch = this.requestQueue.splice(0, this.maxConcurrent);
      
      // Izvršava batch zahteva
      await Promise.allSettled(batch.map(request => request()));
      
      // Pauza između batch-ova
      if (this.requestQueue.length > 0) {
        await new Promise(resolve => setTimeout(resolve, this.delayBetweenRequests));
      }
    }

    this.isProcessing = false;
  }
}

export default RequestThrottle.getInstance();
```

## Implementacija koraka

1. **Kreirajte globalni cache servis** - sprečava duplikate
2. **Implementirajte optimizovani template service** - koristi cache
3. **Ažurirajte komponente** - koristite optimizovani service
4. **Dodajte request throttling** - kontroliše broj zahteva
5. **Testirajte** - proverite da li se greške rešavaju

## Napomene

- Cache traje 5 minuta
- Maksimalno 2 istovremena zahteva
- 100ms pauza između batch-ova
- Automatsko čišćenje cache-a
- Error handling za sve zahteve
