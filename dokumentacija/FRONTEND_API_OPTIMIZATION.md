# Frontend API Optimizacija za OДБИЈА СЕ Template

## Problem
Frontend šalje previše zahteva odjednom, što dovodi do 429 (Too Many Requests) grešaka.

## Rešenje

### 1. Debouncing za API pozive

```typescript
// utils/debounce.ts
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: NodeJS.Timeout;
  
  return (...args: Parameters<T>) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => func(...args), delay);
  };
}
```

### 2. Optimizovani service za OДБИЈА СЕ template

```typescript
// services/odbijaSeTemplateService.ts
import { debounce } from '../utils/debounce';

class OdbijaSeTemplateService {
  private cache = new Map<string, any>();
  private loadingPromises = new Map<string, Promise<any>>();

  // Debounced API pozivi
  private debouncedGetKategorije = debounce(this.getKategorije.bind(this), 300);
  private debouncedGetObrasciVrste = debounce(this.getObrasciVrste.bind(this), 300);
  private debouncedGetOrganizacionaStruktura = debounce(this.getOrganizacionaStruktura.bind(this), 300);

  async getKategorije(): Promise<any[]> {
    const cacheKey = 'kategorije';
    
    // Proveri cache
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }

    // Proveri da li se već učitava
    if (this.loadingPromises.has(cacheKey)) {
      return this.loadingPromises.get(cacheKey);
    }

    // Kreiraj novi promise
    const promise = this.fetchWithRetry('/api/kategorije')
      .then(data => {
        this.cache.set(cacheKey, data);
        this.loadingPromises.delete(cacheKey);
        return data;
      })
      .catch(error => {
        this.loadingPromises.delete(cacheKey);
        throw error;
      });

    this.loadingPromises.set(cacheKey, promise);
    return promise;
  }

  async getObrasciVrste(): Promise<any[]> {
    const cacheKey = 'obrasci-vrste';
    
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }

    if (this.loadingPromises.has(cacheKey)) {
      return this.loadingPromises.get(cacheKey);
    }

    const promise = this.fetchWithRetry('/api/obrasci-vrste')
      .then(data => {
        this.cache.set(cacheKey, data);
        this.loadingPromises.delete(cacheKey);
        return data;
      })
      .catch(error => {
        this.loadingPromises.delete(cacheKey);
        throw error;
      });

    this.loadingPromises.set(cacheKey, promise);
    return promise;
  }

  async getOrganizacionaStruktura(): Promise<any[]> {
    const cacheKey = 'organizaciona-struktura';
    
    if (this.cache.has(cacheKey)) {
      return this.cache.get(cacheKey);
    }

    if (this.loadingPromises.has(cacheKey)) {
      return this.loadingPromises.get(cacheKey);
    }

    const promise = this.fetchWithRetry('/api/organizaciona-struktura')
      .then(data => {
        this.cache.set(cacheKey, data);
        this.loadingPromises.delete(cacheKey);
        return data;
      })
      .catch(error => {
        this.loadingPromises.delete(cacheKey);
        throw error;
      });

    this.loadingPromises.set(cacheKey, promise);
    return promise;
  }

  private async fetchWithRetry(url: string, maxRetries = 3): Promise<any> {
    for (let i = 0; i < maxRetries; i++) {
      try {
        const response = await fetch(url, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
        });

        if (response.status === 429) {
          // Rate limit - čekaj pre ponovnog pokušaja
          const retryAfter = response.headers.get('Retry-After');
          const waitTime = retryAfter ? parseInt(retryAfter) * 1000 : Math.pow(2, i) * 1000;
          
          if (i < maxRetries - 1) {
            await new Promise(resolve => setTimeout(resolve, waitTime));
            continue;
          }
        }

        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
      } catch (error) {
        if (i === maxRetries - 1) {
          throw error;
        }
        
        // Čekaj pre ponovnog pokušaja
        await new Promise(resolve => setTimeout(resolve, Math.pow(2, i) * 1000));
      }
    }
  }

  // Batch loading - učitaj sve podatke odjednom
  async loadAllInitialData() {
    try {
      const [kategorije, obrasciVrste, organizacionaStruktura] = await Promise.allSettled([
        this.getKategorije(),
        this.getObrasciVrste(),
        this.getOrganizacionaStruktura()
      ]);

      return {
        kategorije: kategorije.status === 'fulfilled' ? kategorije.value : [],
        obrasciVrste: obrasciVrste.status === 'fulfilled' ? obrasciVrste.value : [],
        organizacionaStruktura: organizacionaStruktura.status === 'fulfilled' ? organizacionaStruktura.value : []
      };
    } catch (error) {
      console.error('Error loading initial data:', error);
      throw error;
    }
  }
}

export default new OdbijaSeTemplateService();
```

### 3. Optimizovana komponenta

```typescript
// components/OdbijaSeTemplateForm.tsx
import React, { useState, useEffect, useCallback } from 'react';
import odbijaSeTemplateService from '../services/odbijaSeTemplateService';

const OdbijaSeTemplateForm: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState({
    kategorije: [],
    obrasciVrste: [],
    organizacionaStruktura: []
  });

  const loadInitialData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Koristi batch loading umesto pojedinačnih poziva
      const result = await odbijaSeTemplateService.loadAllInitialData();
      setData(result);
    } catch (err) {
      setError('Greška pri učitavanju podataka');
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadInitialData();
  }, [loadInitialData]);

  if (loading) {
    return <div>Učitavanje...</div>;
  }

  if (error) {
    return (
      <div>
        <p>Greška: {error}</p>
        <button onClick={loadInitialData}>Pokušaj ponovo</button>
      </div>
    );
  }

  return (
    <div>
      {/* Form content */}
    </div>
  );
};

export default OdbijaSeTemplateForm;
```

### 4. Dodatne optimizacije

```typescript
// utils/apiConfig.ts
export const API_CONFIG = {
  BASE_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080',
  TIMEOUT: 10000,
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000,
};

// utils/requestInterceptor.ts
export class RequestInterceptor {
  private static instance: RequestInterceptor;
  private requestQueue: Array<() => Promise<any>> = [];
  private isProcessing = false;
  private readonly maxConcurrent = 3;

  static getInstance(): RequestInterceptor {
    if (!RequestInterceptor.instance) {
      RequestInterceptor.instance = new RequestInterceptor();
    }
    return RequestInterceptor.instance;
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
      await Promise.allSettled(batch.map(request => request()));
      
      // Kratka pauza između batch-ova
      await new Promise(resolve => setTimeout(resolve, 100));
    }

    this.isProcessing = false;
  }
}
```

## Implementacija koraka

1. **Dodajte debouncing** za sve API pozive
2. **Implementirajte caching** da izbegnete duplikate
3. **Koristite batch loading** umesto pojedinačnih poziva
4. **Dodajte retry logiku** sa eksponencijalnim backoff-om
5. **Implementirajte request queue** da kontrolišete broj istovremenih zahteva

## Napomene

- Rate limiting je isključen u dev okruženju
- Endpoint-ovi su dodati u security konfiguraciju
- Frontend optimizacija će sprečiti 429 greške
- Caching će poboljšati performanse
