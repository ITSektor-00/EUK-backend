# Frontend License System Implementation Guide

## Pregled

Ovaj dokument opisuje kako implementirati licencni sistem na frontend strani. Sistem proverava važenje licence, prikazuje obaveštenja o isteku i blokira pristup kada licenca istekne.

## API Endpoint-i

### 1. Provera statusa licence
```javascript
GET /api/licenses/status?userId={userId}
```

**Response:**
```json
{
  "hasValidLicense": true,
  "endDate": "2024-12-31T23:59:59",
  "daysUntilExpiry": 45,
  "isExpiringSoon": false
}
```

### 2. Provera da li korisnik ima važeću licencu
```javascript
GET /api/licenses/check/{userId}
```

**Response:**
```json
{
  "hasValidLicense": true,
  "message": "License is valid"
}
```

### 3. Dobijanje svih licenci korisnika
```javascript
GET /api/licenses/user/{userId}
```

**Response:**
```json
{
  "success": true,
  "licenses": [...],
  "count": 1
}
```

## Frontend Implementacija

### 1. License Service (Angular/React/Vue)

#### Angular Service
```typescript
// license.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';

export interface LicenseInfo {
  hasValidLicense: boolean;
  endDate: string;
  daysUntilExpiry: number;
  isExpiringSoon: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class LicenseService {
  private apiUrl = '/api/licenses';
  private licenseStatusSubject = new BehaviorSubject<LicenseInfo | null>(null);
  public licenseStatus$ = this.licenseStatusSubject.asObservable();

  constructor(private http: HttpClient) {}

  checkLicenseStatus(userId: number): Observable<LicenseInfo> {
    return this.http.get<LicenseInfo>(`${this.apiUrl}/status?userId=${userId}`);
  }

  checkLicense(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/check/${userId}`);
  }

  getUserLicenses(userId: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/${userId}`);
  }

  updateLicenseStatus(licenseInfo: LicenseInfo) {
    this.licenseStatusSubject.next(licenseInfo);
  }
}
```

#### React Hook
```javascript
// useLicense.js
import { useState, useEffect } from 'react';
import axios from 'axios';

export const useLicense = (userId) => {
  const [licenseInfo, setLicenseInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const checkLicense = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`/api/licenses/status?userId=${userId}`);
        setLicenseInfo(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Error checking license');
      } finally {
        setLoading(false);
      }
    };

    if (userId) {
      checkLicense();
    }
  }, [userId]);

  return { licenseInfo, loading, error };
};
```

### 2. License Guard (Angular)

```typescript
// license.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { LicenseService } from './license.service';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LicenseGuard implements CanActivate {
  
  constructor(
    private licenseService: LicenseService,
    private router: Router
  ) {}

  canActivate(): Observable<boolean> {
    const userId = this.getCurrentUserId(); // Implementiraj kako dobijaš user ID
    
    return this.licenseService.checkLicense(userId).pipe(
      map(response => {
        if (response.hasValidLicense) {
          return true;
        } else {
          this.router.navigate(['/license-expired']);
          return false;
        }
      }),
      catchError(() => {
        this.router.navigate(['/license-expired']);
        return of(false);
      })
    );
  }

  private getCurrentUserId(): number {
    // Implementiraj logiku za dobijanje trenutnog user ID-a
    return 1; // Placeholder
  }
}
```

### 3. License Expired Component

```typescript
// license-expired.component.ts
import { Component } from '@angular/core';

@Component({
  selector: 'app-license-expired',
  template: `
    <div class="license-expired-container">
      <div class="license-expired-card">
        <div class="icon">
          <i class="fas fa-exclamation-triangle"></i>
        </div>
        <h1>Licenca je istekla</h1>
        <p>Vaša licenca je istekla. Molimo kontaktirajte administratora za produženje licence.</p>
        <div class="actions">
          <button class="btn btn-primary" (click)="contactAdmin()">
            Kontaktiraj administratora
          </button>
          <button class="btn btn-secondary" (click)="logout()">
            Odjavi se
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .license-expired-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    }
    
    .license-expired-card {
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
export class LicenseExpiredComponent {
  
  contactAdmin() {
    // Implementiraj logiku za kontaktiranje administratora
    window.location.href = 'mailto:admin@example.com?subject=License Renewal Request';
  }
  
  logout() {
    // Implementiraj logiku za logout
    localStorage.removeItem('token');
    window.location.href = '/login';
  }
}
```

### 4. License Warning Component

```typescript
// license-warning.component.ts
import { Component, Input, OnInit } from '@angular/core';
import { LicenseService, LicenseInfo } from './license.service';

@Component({
  selector: 'app-license-warning',
  template: `
    <div class="license-warning" *ngIf="showWarning">
      <div class="warning-content">
        <i class="fas fa-exclamation-triangle"></i>
        <span>{{ warningMessage }}</span>
        <button class="btn-close" (click)="dismissWarning()">×</button>
      </div>
    </div>
  `,
  styles: [`
    .license-warning {
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
export class LicenseWarningComponent implements OnInit {
  @Input() userId: number;
  
  showWarning = false;
  warningMessage = '';
  
  constructor(private licenseService: LicenseService) {}
  
  ngOnInit() {
    if (this.userId) {
      this.checkLicenseStatus();
    }
  }
  
  checkLicenseStatus() {
    this.licenseService.checkLicenseStatus(this.userId).subscribe(
      (licenseInfo: LicenseInfo) => {
        if (!licenseInfo.hasValidLicense) {
          this.showWarning = true;
          this.warningMessage = 'Vaša licenca je istekla! Kontaktirajte administratora.';
        } else if (licenseInfo.isExpiringSoon) {
          this.showWarning = true;
          this.warningMessage = `Vaša licenca će isteći za ${licenseInfo.daysUntilExpiry} dana.`;
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
import { LicenseService, LicenseInfo } from './license.service';

@Component({
  selector: 'app-root',
  template: `
    <app-license-warning [userId]="currentUserId"></app-license-warning>
    <router-outlet></router-outlet>
  `
})
export class AppComponent implements OnInit {
  currentUserId: number;
  
  constructor(private licenseService: LicenseService) {}
  
  ngOnInit() {
    this.currentUserId = this.getCurrentUserId();
    
    // Proveri licencu na početku aplikacije
    if (this.currentUserId) {
      this.licenseService.checkLicenseStatus(this.currentUserId).subscribe(
        (licenseInfo: LicenseInfo) => {
          this.licenseService.updateLicenseStatus(licenseInfo);
        }
      );
    }
  }
  
  private getCurrentUserId(): number {
    // Implementiraj logiku za dobijanje trenutnog user ID-a
    return 1; // Placeholder
  }
}
```

### 6. Route Configuration

```typescript
// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LicenseGuard } from './license.guard';
import { LicenseExpiredComponent } from './license-expired.component';

const routes: Routes = [
  {
    path: 'license-expired',
    component: LicenseExpiredComponent
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
    canActivate: [LicenseGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    canActivate: [LicenseGuard]
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

### 1. License Context

```javascript
// LicenseContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const LicenseContext = createContext();

export const useLicense = () => {
  const context = useContext(LicenseContext);
  if (!context) {
    throw new Error('useLicense must be used within a LicenseProvider');
  }
  return context;
};

export const LicenseProvider = ({ children, userId }) => {
  const [licenseInfo, setLicenseInfo] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (userId) {
      checkLicense();
    }
  }, [userId]);

  const checkLicense = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`/api/licenses/status?userId=${userId}`);
      setLicenseInfo(response.data);
    } catch (error) {
      console.error('Error checking license:', error);
    } finally {
      setLoading(false);
    }
  };

  const value = {
    licenseInfo,
    loading,
    checkLicense
  };

  return (
    <LicenseContext.Provider value={value}>
      {children}
    </LicenseContext.Provider>
  );
};
```

### 2. License Warning Component (React)

```javascript
// LicenseWarning.jsx
import React from 'react';
import { useLicense } from './LicenseContext';

const LicenseWarning = () => {
  const { licenseInfo } = useLicense();
  const [dismissed, setDismissed] = useState(false);

  if (!licenseInfo || dismissed) return null;

  const getWarningMessage = () => {
    if (!licenseInfo.hasValidLicense) {
      return 'Vaša licenca je istekla! Kontaktirajte administratora.';
    }
    if (licenseInfo.isExpiringSoon) {
      return `Vaša licenca će isteći za ${licenseInfo.daysUntilExpiry} dana.`;
    }
    return null;
  };

  const message = getWarningMessage();
  if (!message) return null;

  return (
    <div className="license-warning">
      <div className="warning-content">
        <i className="fas fa-exclamation-triangle"></i>
        <span>{message}</span>
        <button 
          className="btn-close" 
          onClick={() => setDismissed(true)}
        >
          ×
        </button>
      </div>
    </div>
  );
};

export default LicenseWarning;
```

### 3. License Guard (React)

```javascript
// LicenseGuard.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

const LicenseGuard = ({ children, userId }) => {
  const navigate = useNavigate();
  const [isValid, setIsValid] = useState(null);

  useEffect(() => {
    const checkLicense = async () => {
      try {
        const response = await axios.get(`/api/licenses/check/${userId}`);
        setIsValid(response.data.hasValidLicense);
        
        if (!response.data.hasValidLicense) {
          navigate('/license-expired');
        }
      } catch (error) {
        console.error('Error checking license:', error);
        navigate('/license-expired');
      }
    };

    if (userId) {
      checkLicense();
    }
  }, [userId, navigate]);

  if (isValid === null) {
    return <div>Checking license...</div>;
  }

  if (!isValid) {
    return null; // Redirect će se desiti u useEffect
  }

  return children;
};

export default LicenseGuard;
```

## CSS Stilovi

```css
/* license-warning.css */
.license-warning {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  background: #ff6b6b;
  color: white;
  padding: 1rem;
  text-align: center;
  animation: slideDown 0.3s ease-out;
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
  transition: opacity 0.3s ease;
}

.btn-close:hover {
  opacity: 0.7;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
  }
  to {
    transform: translateY(0);
  }
}

/* License expired page */
.license-expired-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.license-expired-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
  text-align: center;
  max-width: 500px;
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
```

## Testiranje

### 1. Unit Testovi

```javascript
// license.service.spec.ts
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LicenseService } from './license.service';

describe('LicenseService', () => {
  let service: LicenseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LicenseService]
    });
    service = TestBed.inject(LicenseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should check license status', () => {
    const userId = 1;
    service.checkLicenseStatus(userId).subscribe(result => {
      expect(result).toBeDefined();
      expect(result.hasValidLicense).toBeDefined();
    });
  });
});
```

### 2. E2E Testovi

```javascript
// license.e2e-spec.ts
import { browser, by, element } from 'protractor';

describe('License System E2E', () => {
  it('should show license warning when expiring soon', () => {
    browser.get('/dashboard');
    expect(element(by.css('.license-warning')).isDisplayed()).toBeTruthy();
  });

  it('should redirect to license expired page when license is invalid', () => {
    browser.get('/dashboard');
    expect(browser.getCurrentUrl()).toContain('/license-expired');
  });
});
```

## Deployment Checklist

- [ ] Kreirati licenses tabelu u bazi podataka
- [ ] Konfigurisati cron job-ove za obaveštenja
- [ ] Testirati sve API endpoint-e
- [ ] Implementirati frontend komponente
- [ ] Konfigurisati routing sa guard-ovima
- [ ] Testirati licencnu proveru
- [ ] Konfigurisati email servis za obaveštenja
- [ ] Testirati obaveštenja o isteku licence

## Troubleshooting

### Česti problemi:

1. **CORS greške**: Dodaj CORS konfiguraciju u backend
2. **401 Unauthorized**: Proveri da li je user ID ispravno prosleđen
3. **License check ne radi**: Proveri da li je interceptor pravilno konfigurisan
4. **Obaveštenja se ne šalju**: Proveri cron job konfiguraciju i email servis

### Debug koraci:

1. Proveri network tab u browser-u za API pozive
2. Proveri console log-ove za greške
3. Testiraj API endpoint-e direktno sa Postman-om
4. Proveri da li su cron job-ovi aktivni
