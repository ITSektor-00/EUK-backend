# React Development Mode Optimizacija

## Problem
React development mode mount-uje komponente više puta, što dovodi do previše API poziva.

## Rešenje

### 1. Disable React Strict Mode za development

```typescript
// next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: false, // Disable za development
  swcMinify: true,
  experimental: {
    appDir: true,
  },
}

module.exports = nextConfig
```

### 2. Dodajte useEffect cleanup

```typescript
// components/TemplateForm.tsx
import React, { useState, useEffect, useRef } from 'react';

const TemplateForm: React.FC = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const mountedRef = useRef(true);

  useEffect(() => {
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const loadData = async () => {
    if (!mountedRef.current) return;
    
    setLoading(true);
    try {
      const result = await fetchData();
      if (mountedRef.current) {
        setData(result);
      }
    } catch (error) {
      if (mountedRef.current) {
        console.error('Error:', error);
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

  // Component render...
};
```

### 3. Dodajte request deduplication

```typescript
// utils/requestDeduplication.ts
class RequestDeduplication {
  private static instance: RequestDeduplication;
  private pendingRequests = new Map<string, Promise<any>>();

  static getInstance(): RequestDeduplication {
    if (!RequestDeduplication.instance) {
      RequestDeduplication.instance = new RequestDeduplication();
    }
    return RequestDeduplication.instance;
  }

  async deduplicate<T>(key: string, requestFn: () => Promise<T>): Promise<T> {
    // Ako već postoji pending request, vrati ga
    if (this.pendingRequests.has(key)) {
      console.log(`Deduplicating request for ${key}`);
      return this.pendingRequests.get(key);
    }

    // Kreiraj novi request
    const promise = requestFn()
      .finally(() => {
        // Obriši iz pending requests kada se završi
        this.pendingRequests.delete(key);
      });

    this.pendingRequests.set(key, promise);
    return promise;
  }

  clear() {
    this.pendingRequests.clear();
  }
}

export default RequestDeduplication.getInstance();
```

### 4. Optimizovani service sa deduplication

```typescript
// services/optimizedService.ts
import requestDeduplication from '../utils/requestDeduplication';

class OptimizedService {
  private readonly BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

  async getKategorije() {
    return requestDeduplication.deduplicate('kategorije', async () => {
      const response = await fetch(`${this.BASE_URL}/api/kategorije`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
    });
  }

  async getObrasciVrste() {
    return requestDeduplication.deduplicate('obrasci-vrste', async () => {
      const response = await fetch(`${this.BASE_URL}/api/obrasci-vrste`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
    });
  }

  async getOrganizacionaStruktura() {
    return requestDeduplication.deduplicate('organizaciona-struktura', async () => {
      const response = await fetch(`${this.BASE_URL}/api/organizaciona-struktura`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
    });
  }

  async getPredmeti() {
    return requestDeduplication.deduplicate('predmeti', async () => {
      const response = await fetch(`${this.BASE_URL}/api/predmeti`);
      if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
      return await response.json();
    });
  }
}

export default new OptimizedService();
```

### 5. Dodajte development mode detection

```typescript
// utils/developmentMode.ts
export const isDevelopment = process.env.NODE_ENV === 'development';

export const logDevelopment = (message: string, ...args: any[]) => {
  if (isDevelopment) {
    console.log(`[DEV] ${message}`, ...args);
  }
};

export const warnDevelopment = (message: string, ...args: any[]) => {
  if (isDevelopment) {
    console.warn(`[DEV WARNING] ${message}`, ...args);
  }
};
```

### 6. Optimizovana komponenta sa development mode

```typescript
// components/OptimizedTemplateForm.tsx
import React, { useState, useEffect, useRef } from 'react';
import optimizedService from '../services/optimizedService';
import { isDevelopment, logDevelopment } from '../utils/developmentMode';

const OptimizedTemplateForm: React.FC = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(false);
  const mountedRef = useRef(true);
  const loadAttemptsRef = useRef(0);

  useEffect(() => {
    return () => {
      mountedRef.current = false;
    };
  }, []);

  const loadData = async () => {
    if (!mountedRef.current) return;
    
    loadAttemptsRef.current++;
    logDevelopment(`Loading data attempt ${loadAttemptsRef.current}`);
    
    setLoading(true);
    try {
      const [kategorije, obrasciVrste, organizacionaStruktura, predmeti] = await Promise.allSettled([
        optimizedService.getKategorije(),
        optimizedService.getObrasciVrste(),
        optimizedService.getOrganizacionaStruktura(),
        optimizedService.getPredmeti()
      ]);

      if (mountedRef.current) {
        const result = {
          kategorije: kategorije.status === 'fulfilled' ? kategorije.value : [],
          obrasciVrste: obrasciVrste.status === 'fulfilled' ? obrasciVrste.value : [],
          organizacionaStruktura: organizacionaStruktura.status === 'fulfilled' ? organizacionaStruktura.value : [],
          predmeti: predmeti.status === 'fulfilled' ? predmeti.value : []
        };
        
        setData(result);
        logDevelopment('Data loaded successfully', result);
      }
    } catch (error) {
      if (mountedRef.current) {
        console.error('Error loading data:', error);
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
    return <div>Loading...</div>;
  }

  return (
    <div>
      <h2>Template Form</h2>
      {data && (
        <div>
          <p>Kategorije: {data.kategorije.length}</p>
          <p>Obrasci vrste: {data.obrasciVrste.length}</p>
          <p>Organizaciona struktura: {data.organizacionaStruktura.length}</p>
          <p>Predmeti: {data.predmeti.length}</p>
        </div>
      )}
    </div>
  );
};

export default OptimizedTemplateForm;
```

## Implementacija koraka

1. **Disable React Strict Mode** u development
2. **Dodajte useEffect cleanup** da sprečite memory leaks
3. **Implementirajte request deduplication** da sprečite duplikate
4. **Dodajte development mode detection** za bolje debugging
5. **Testirajte** da li se greške rešavaju

## Napomene

- React Strict Mode je koristan za production, ali problematičan za development
- Request deduplication sprečava duplikate
- useEffect cleanup sprečava memory leaks
- Development mode detection omogućava bolje debugging
