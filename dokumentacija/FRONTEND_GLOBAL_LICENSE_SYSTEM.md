# Frontend Global License System Implementation

## Pregled

Globalni licencni sistem kontroliše pristup celom softveru na osnovu jedne globalne licence. Kada licenca istekne, ceo sistem prestaje da radi.

## API Endpoint-i

### 1. Provera statusa globalne licence
```javascript
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

### 2. Provera da li postoji važeća globalna licenca
```javascript
GET /api/global-license/check
```

**Response:**
```json
{
  "hasValidLicense": true,
  "message": "Global license is valid"
}
```

### 3. Dobijanje aktivne globalne licence
```javascript
GET /api/global-license/active
```

**Response:**
```json
{
  "success": true,
  "license": {
    "id": 1,
    "licenseKey": "GLOBAL-LICENSE-2024",
    "startDate": "2024-01-01T00:00:00",
    "endDate": "2024-12-31T23:59:59",
    "isActive": true,
    "notificationSent": false
  }
}
```

## Frontend Implementacija

### 1. Global License Service (Angular)

```typescript
// global-license.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

export interface GlobalLicenseInfo {
  hasValidLicense: boolean;
  endDate: string;
  daysUntilExpiry: number;
  isExpiringSoon: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class GlobalLicenseService {
  private apiUrl = '/api/global-license';
  private globalLicenseStatusSubject = new BehaviorSubject<GlobalLicenseInfo | null>(null);
  public globalLicenseStatus$ = this.globalLicenseStatusSubject.asObservable();

  constructor(private http: HttpClient) {}

  checkGlobalLicenseStatus(): Observable<GlobalLicenseInfo> {
    return this.http.get<GlobalLicenseInfo>(`${this.apiUrl}/status`);
  }

  checkGlobalLicense(): Observable<any> {
    return this.http.get(`${this.apiUrl}/check`);
  }

  getActiveGlobalLicense(): Observable<any> {
    return this.http.get(`${this.apiUrl}/active`);
  }

  updateGlobalLicenseStatus(licenseInfo: GlobalLicenseInfo) {
    this.globalLicenseStatusSubject.next(licenseInfo);
  }
}
```

### 2. Global License Guard (Angular)

```typescript
// global-license.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { GlobalLicenseService } from './global-license.service';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class GlobalLicenseGuard implements CanActivate {
  
  constructor(
    private globalLicenseService: GlobalLicenseService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    return this.globalLicenseService.checkGlobalLicense().pipe(
      map(response => {
        if (response.hasValidLicense) {
          return true;
        } else {
          this.router.navigate(['/global-license-expired']);
          return false;
        }
      }),
      catchError(() => {
        this.router.navigate(['/global-license-expired']);
        return of(false);
      })
    );
  }
}
```

### 3. Global License Expired Component

```typescript
// global-license-expired.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-global-license-expired',
  template: `
    <div class="global-license-expired-container">
      <div class="global-license-expired-card">
        <div class="icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h1>Softverska licenca je istekla</h1>
        <p>Licenca za EUK sistem je istekla. Molimo kontaktirajte administratora za produženje licence.</p>
        <div class="actions">
          <button class="btn btn-primary" (click)="contactAdmin()">
            Kontaktiraj administratora
          </button>
          <button class="btn btn-secondary" (click)="refreshPage()">
            Osveži stranicu
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .global-license-expired-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
    }
    
    .global-license-expired-card {
      background: white;
      padding: 2rem;
      border-radius: 10px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.3);
      text-align: center;
      max-width: 500px;
    }
    
    .icon {
      font-size: 4rem;
      color: #ff6b6b;
      margin-bottom: 1rem;
    }
    
    h1 {
      color: #333;
      margin-bottom: 1rem;
    }
    
    p {
      color: #666;
      margin-bottom: 2rem;
      line-height: 1.6;
    }
    
    .actions {
      display: flex;
      gap: 1rem;
      justify-content: center;
    }
    
    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-weight: 500;
      transition: all 0.3s ease;
    }
    
    .btn-primary {
      background: #007bff;
      color: white;
    }
    
    .btn-primary:hover {
      background: #0056b3;
    }
    
    .btn-secondary {
      background: #6c757d;
      color: white;
    }
    
    .btn-secondary:hover {
      background: #545b62;
    }
  `]
})
export class GlobalLicenseExpiredComponent {
  
  contactAdmin() {
    window.location.href = 'mailto:admin@euk.rs?subject=Global License Renewal Request';
  }
  
  refreshPage() {
    window.location.reload();
  }
}
```

### 4. Global License Warning Component

```typescript
// global-license-warning.component.ts
import { Component, OnInit } from '@angular/core';
import { GlobalLicenseService, GlobalLicenseInfo } from './global-license.service';

@Component({
  selector: 'app-global-license-warning',
  template: `
    <div class="global-license-warning" *ngIf="showWarning">
      <div class="warning-content">
        <i class="fas fa-exclamation-triangle"></i>
        <span>{{ warningMessage }}</span>
        <button class="btn-close" (click)="dismissWarning()">×</button>
      </div>
    </div>
  `,
  styles: [`
    .global-license-warning {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      z-index: 9999;
      background: #ff6b6b;
      color: white;
      padding: 1rem;
      text-align: center;
    }
    
    .warning-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      max-width: 1200px;
      margin: 0 auto;
    }
    
    .btn-close {
      background: none;
      border: none;
      color: white;
      font-size: 1.5rem;
      cursor: pointer;
      margin-left: 1rem;
    }
  `]
})
export class GlobalLicenseWarningComponent implements OnInit {
  
  showWarning = false;
  warningMessage = '';
  
  constructor(private globalLicenseService: GlobalLicenseService) {}
  
  ngOnInit() {
    this.checkGlobalLicenseStatus();
  }
  
  checkGlobalLicenseStatus() {
    this.globalLicenseService.checkGlobalLicenseStatus().subscribe(
      (licenseInfo: GlobalLicenseInfo) => {
        if (!licenseInfo.hasValidLicense) {
          this.showWarning = true;
          this.warningMessage = 'Softverska licenca je istekla! Kontaktirajte administratora.';
        } else if (licenseInfo.isExpiringSoon) {
          this.showWarning = true;
          this.warningMessage = `Softverska licenca će isteći za ${licenseInfo.daysUntilExpiry} dana.`;
        }
      }
    );
  }
  
  dismissWarning() {
    this.showWarning = false;
  }
}
```

### 5. App Component Integration

```typescript
// app.component.ts
import { Component, OnInit } from '@angular/core';
import { GlobalLicenseService, GlobalLicenseInfo } from './global-license.service';

@Component({
  selector: 'app-root',
  template: `
    <app-global-license-warning></app-global-license-warning>
    <router-outlet></router-outlet>
  `
})
export class AppComponent implements OnInit {
  
  constructor(private globalLicenseService: GlobalLicenseService) {}
  
  ngOnInit() {
    // Proveri globalnu licencu na početku aplikacije
    this.globalLicenseService.checkGlobalLicenseStatus().subscribe(
      (licenseInfo: GlobalLicenseInfo) => {
        this.globalLicenseService.updateGlobalLicenseStatus(licenseInfo);
      }
    );
  }
}
```

### 6. Route Configuration

```typescript
// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GlobalLicenseGuard } from './global-license.guard';
import { GlobalLicenseExpiredComponent } from './global-license-expired.component';

const routes: Routes = [
  {
    path: 'global-license-expired',
    component: GlobalLicenseExpiredComponent
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [GlobalLicenseGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canActivate: [GlobalLicenseGuard]
  }
  // ... ostale rute
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
```

## React Implementation

### 1. Global License Hook

```javascript
// useGlobalLicense.js
import { useState, useEffect } from 'react';
import axios from 'axios';

export const useGlobalLicense = () => {
  const [licenseInfo, setLicenseInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const checkGlobalLicense = async () => {
      try {
        setLoading(true);
        const response = await axios.get('/api/global-license/status');
        setLicenseInfo(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Error checking global license');
      } finally {
        setLoading(false);
      }
    };

    checkGlobalLicense();
  }, []);

  return { licenseInfo, loading, error };
};
```

### 2. Global License Guard (React)

```javascript
// GlobalLicenseGuard.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const GlobalLicenseGuard = ({ children }) => {
  const navigate = useNavigate();
  const [isValid, setIsValid] = useState(null);

  useEffect(() => {
    const checkGlobalLicense = async () => {
      try {
        const response = await axios.get('/api/global-license/check');
        setIsValid(response.data.hasValidLicense);
        
        if (!response.data.hasValidLicense) {
          navigate('/global-license-expired');
        }
      } catch (error) {
        console.error('Error checking global license:', error);
        navigate('/global-license-expired');
      }
    };

    checkGlobalLicense();
  }, [navigate]);

  if (isValid === null) {
    return <div>Checking global license...</div>;
  }

  if (!isValid) {
    return null; // Redirect će se desiti u useEffect
  }

  return children;
};

export default GlobalLicenseGuard;
```

## Testiranje

### 1. Unit Testovi

```javascript
// global-license.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { GlobalLicenseService } from './global-license.service';

describe('GlobalLicenseService', () => {
  let service: GlobalLicenseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GlobalLicenseService]
    });
    service = TestBed.inject(GlobalLicenseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should check global license status', () => {
    service.checkGlobalLicenseStatus().subscribe(result => {
      expect(result).toBeDefined();
      expect(result.hasValidLicense).toBeDefined();
    });
  });
});
```

## Deployment Checklist

- [ ] Kreirati global_license tabelu u bazi podataka
- [ ] Konfigurisati cron job-ove za obaveštenja
- [ ] Testirati sve API endpoint-e
- [ ] Implementirati frontend komponente
- [ ] Konfigurisati routing sa guard-ovima
- [ ] Testirati globalnu licencnu proveru
- [ ] Konfigurisati email servis za obaveštenja
- [ ] Testirati obaveštenja o isteku globalne licence

## Razlika od Individualnog Sistema

| Individualni Sistem | Globalni Sistem |
|-------------------|----------------|
| Licenca po korisniku | Jedna licenca za ceo softver |
| `licenses` tabela | `global_license` tabela |
| `/api/licenses/*` | `/api/global-license/*` |
| Provera po korisniku | Provera za ceo sistem |
| Obaveštenja korisnicima | Obaveštenja administratorima |
