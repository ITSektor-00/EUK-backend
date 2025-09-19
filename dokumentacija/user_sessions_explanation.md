# 🔐 User Sessions Tabela - Objašnjenje

## 📋 Šta je `user_sessions` tabela?

`user_sessions` tabela je **tabela za upravljanje sesijama korisnika** u sistemu. Ona prati sve aktivne sesije korisnika i omogućava kontrolu pristupa.

## 🏗️ **Tipična struktura `user_sessions` tabele:**

```sql
CREATE TABLE user_sessions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    session_token VARCHAR(255) UNIQUE NOT NULL, -- JWT token ili session ID
    ip_address VARCHAR(45), -- IP adresa korisnika
    user_agent TEXT, -- Browser/device informacije
    is_active BOOLEAN DEFAULT TRUE, -- Da li je sesija aktivna
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Kada je sesija kreirana
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- Poslednja aktivnost
    expires_at TIMESTAMP, -- Kada ističe sesija
    logout_at TIMESTAMP, -- Kada je korisnik odjavljen
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## 🔌 **Šta radi `user_sessions` tabela:**

### **1. Prati aktivne sesije:**
- Koji korisnik je prijavljen
- Koliko sesija ima istovremeno
- Kada je poslednji put aktivan

### **2. Sigurnosne funkcije:**
- **Multi-session kontrola** - možeš ograničiti broj istovremenih sesija
- **Session hijacking zaštita** - prati IP adresu i user agent
- **Auto-logout** - automatski odjavljuje istekle sesije
- **Force logout** - administrator može odjaviti korisnika sa svih uređaja

### **3. Audit i monitoring:**
- Ko je kada prijavljen
- Odakle se prijavljuje (IP adresa)
- Koji uređaj/browser koristi
- Koliko dugo je aktivan

## 🚀 **Kako funkcioniše u praksi:**

### **Kada se korisnik prijavi:**
```sql
-- Kreiraj novu sesiju
INSERT INTO user_sessions (user_id, session_token, ip_address, user_agent, expires_at)
VALUES (1, 'jwt_token_here', '192.168.1.100', 'Mozilla/5.0...', NOW() + INTERVAL '24 hours');
```

### **Kada korisnik radi nešto:**
```sql
-- Ažuriraj poslednju aktivnost
UPDATE user_sessions 
SET last_activity = CURRENT_TIMESTAMP 
WHERE session_token = 'jwt_token_here';
```

### **Kada korisnik odjavi:**
```sql
-- Označi sesiju kao neaktivnu
UPDATE user_sessions 
SET is_active = FALSE, logout_at = CURRENT_TIMESTAMP 
WHERE session_token = 'jwt_token_here';
```

## 🔍 **Korisni upiti za `user_sessions`:**

### **Aktivne sesije korisnika:**
```sql
SELECT 
    u.username,
    us.session_token,
    us.ip_address,
    us.created_at,
    us.last_activity,
    us.expires_at
FROM user_sessions us
JOIN users u ON us.user_id = u.id
WHERE us.is_active = true
ORDER BY us.last_activity DESC;
```

### **Broj aktivnih sesija po korisniku:**
```sql
SELECT 
    u.username,
    COUNT(*) as active_sessions
FROM user_sessions us
JOIN users u ON us.user_id = u.id
WHERE us.is_active = true
GROUP BY u.id, u.username;
```

### **Sesije koje su istekle:**
```sql
SELECT 
    u.username,
    us.session_token,
    us.created_at,
    us.expires_at
FROM user_sessions us
JOIN users u ON us.user_id = u.id
WHERE us.expires_at < CURRENT_TIMESTAMP 
    AND us.is_active = true;
```

### **Sesije sa sumnjivim aktivnostima:**
```sql
SELECT 
    u.username,
    us.ip_address,
    us.user_agent,
    us.created_at,
    us.last_activity
FROM user_sessions us
JOIN users u ON us.user_id = u.id
WHERE us.is_active = true
    AND us.last_activity < NOW() - INTERVAL '30 minutes'
ORDER BY us.last_activity;
```

## 🛠️ **Funkcije za upravljanje sesijama:**

### **Kreiraj sesiju:**
```sql
CREATE OR REPLACE FUNCTION create_user_session(
    p_user_id INTEGER,
    p_session_token VARCHAR(255),
    p_ip_address VARCHAR(45),
    p_user_agent TEXT,
    p_duration_hours INTEGER DEFAULT 24
) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO user_sessions (
        user_id, session_token, ip_address, user_agent, expires_at
    ) VALUES (
        p_user_id, p_session_token, p_ip_address, p_user_agent,
        NOW() + (p_duration_hours || ' hours')::INTERVAL
    );
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;
```

### **Proveri da li je sesija validna:**
```sql
CREATE OR REPLACE FUNCTION is_session_valid(p_session_token VARCHAR(255)) 
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM user_sessions 
        WHERE session_token = p_session_token 
            AND is_active = true 
            AND (expires_at IS NULL OR expires_at > CURRENT_TIMESTAMP)
    );
END;
$$ LANGUAGE plpgsql;
```

### **Odjavi korisnika sa svih uređaja:**
```sql
CREATE OR REPLACE FUNCTION logout_user_all_devices(p_user_id INTEGER) 
RETURNS INTEGER AS $$
DECLARE
    affected_rows INTEGER;
BEGIN
    UPDATE user_sessions 
    SET is_active = FALSE, logout_at = CURRENT_TIMESTAMP 
    WHERE user_id = p_user_id AND is_active = true;
    
    GET DIAGNOSTICS affected_rows = ROW_COUNT;
    RETURN affected_rows;
END;
$$ LANGUAGE plpgsql;
```

## 📱 **Kako se koristi u Spring Boot aplikaciji:**

### **1. Kreiraj Entity:**
```java
@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "session_token")
    private String sessionToken;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    // getters, setters...
}
```

### **2. Kreiraj Service:**
```java
@Service
public class SessionService {
    
    @Autowired
    private UserSessionRepository userSessionRepository;
    
    public void createSession(Long userId, String token, String ipAddress, String userAgent) {
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setSessionToken(token);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        session.setIsActive(true);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastActivity(LocalDateTime.now());
        
        userSessionRepository.save(session);
    }
    
    public boolean isSessionValid(String token) {
        UserSession session = userSessionRepository.findBySessionTokenAndIsActiveTrue(token);
        if (session == null) return false;
        
        // Proveri da li je istekla
        if (session.getExpiresAt() != null && 
            session.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        // Ažuriraj poslednju aktivnost
        session.setLastActivity(LocalDateTime.now());
        userSessionRepository.save(session);
        
        return true;
    }
}
```

## 🎯 **Prednosti `user_sessions` tabele:**

1. **Sigurnost** - možeš videti sve aktivne sesije
2. **Kontrola** - možeš ograničiti broj sesija po korisniku
3. **Monitoring** - pratiš aktivnost korisnika
4. **Audit** - imaš log svih prijava/odjava
5. **Force logout** - možeš odjaviti korisnike sa svih uređaja

## 🔒 **Sigurnosne preporuke:**

1. **Redovno čisti istekle sesije**
2. **Ograniči broj istovremenih sesija**
3. **Prati sumnjive aktivnosti** (različite IP adrese, user agenti)
4. **Implementiraj auto-logout** za neaktivne sesije
5. **Loguj sve sesije** za audit

## 🎉 **Zaključak:**

`user_sessions` tabela je **ključna za sigurnost** vašeg sistema! Omogućava vam da:

- ✅ **Pratite** sve aktivne sesije
- ✅ **Kontrolišete** pristup korisnika
- ✅ **Otkrijete** sumnjive aktivnosti
- ✅ **Odjavite** korisnike sa svih uređaja
- ✅ **Imate audit** svih prijava/odjava

Ovo je **neophodno** za bilo koji ozbiljan sistem sa autentifikacijom! 🔐
