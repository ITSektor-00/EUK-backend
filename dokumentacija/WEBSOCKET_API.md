# 🔌 WebSocket API - Dokumentacija

## 📋 Pregled

Kreirao sam **WebSocket API** koji rešava greške povezivanja sa `ws://localhost:8080/ws`. API omogućava real-time komunikaciju između frontend-a i backend-a za EUK aplikaciju.

## 🏗️ **Kreirane klase:**

### 1. **WebSocketConfig** - Konfiguracija WebSocket-a
- Omogućava WebSocket komunikaciju na `/ws` endpoint-u
- Konfiguriše STOMP message broker
- Omogućava SockJS fallback za browsere koji ne podržavaju WebSocket

### 2. **WebSocketController** - Kontroler za WebSocket poruke
- Handle-uje poruke poslate na `/app/message`
- Handle-uje EUK update-ove na `/app/euk/update`
- Omogućava slanje notifikacija specifičnim korisnicima

### 3. **WebSocketService** - Servis za WebSocket operacije
- Broadcast EUK update-ova
- Slanje korisničkih notifikacija
- Sistem notifikacija za EUK entitete

## 🔌 **WebSocket Endpoint-i:**

### **Osnovni WebSocket endpoint:**
```
ws://localhost:8080/ws
```

### **STOMP Topics:**
```
/topic/messages          - General messages
/topic/euk-updates      - EUK entity updates
/topic/system           - System messages
/topic/euk-notifications - EUK-specific notifications
```

### **User-specific destinations:**
```
/user/{username}/notifications - Notifications for specific user
```

## 📡 **Message Flow:**

### **1. Client to Server (via /app):**
```
/app/message     → Broadcasts to /topic/messages
/app/euk/update → Broadcasts to /topic/euk-updates
```

### **2. Server to Client (via /topic):**
```
/topic/messages          ← General broadcasts
/topic/euk-updates      ← EUK updates
/topic/system           ← System messages
/topic/euk-notifications ← EUK notifications
```

## 🚀 **Kako koristiti:**

### **Frontend WebSocket Connection:**
```javascript
// Connect to WebSocket
const socket = new WebSocket('ws://localhost:8080/ws');

// Or using SockJS (fallback)
const socket = new SockJS('http://localhost:8080/ws');

// Handle connection
socket.onopen = () => {
    console.log('WebSocket connected!');
};

// Handle messages
socket.onmessage = (event) => {
    const data = JSON.parse(event.data);
    console.log('Received:', data);
};

// Handle errors
socket.onerror = (error) => {
    console.error('WebSocket error:', error);
};
```

### **STOMP Client (Recommended):**
```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const client = new Client({
    webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
    debug: (str) => console.log(str),
});

client.onConnect = (frame) => {
    console.log('Connected to WebSocket');
    
    // Subscribe to topics
    client.subscribe('/topic/euk-updates', (message) => {
        const update = JSON.parse(message.body);
        console.log('EUK Update:', update);
    });
    
    client.subscribe('/topic/system', (message) => {
        const systemMsg = JSON.parse(message.body);
        console.log('System:', systemMsg);
    });
};

client.activate();
```

## 📊 **Message Formats:**

### **General Message:**
```json
{
  "content": "Hello from client",
  "timestamp": "2024-01-15T10:30:00",
  "type": "MESSAGE"
}
```

### **EUK Update:**
```json
{
  "entityType": "predmet",
  "action": "CREATE",
  "entityData": {
    "id": 1,
    "naziv": "Test Predmet"
  },
  "timestamp": "2024-01-15T10:30:00",
  "type": "EUK_UPDATE"
}
```

### **System Message:**
```json
{
  "message": "System maintenance scheduled",
  "level": "INFO",
  "timestamp": "2024-01-15T10:30:00",
  "type": "SYSTEM"
}
```

## 🔒 **Sigurnost:**

- WebSocket endpoint je dostupan bez autentifikacije
- CORS konfigurisan za EUK domene
- SockJS fallback omogućen za kompatibilnost

## 🧪 **Testiranje:**

### **1. WebSocket Health Check:**
```bash
GET /ws/health
```

### **2. Test WebSocket Connection:**
```bash
# Using wscat (install with: npm install -g wscat)
wscat -c ws://localhost:8080/ws

# Send test message
{"content": "Test message"}
```

## 📝 **Napomene:**

1. **WebSocket endpoint** je sada dostupan na `/ws`
2. **STOMP protocol** omogućava strukturiranu komunikaciju
3. **SockJS fallback** omogućava kompatibilnost sa starijim browserima
4. **Real-time updates** za EUK entitete
5. **User-specific notifications** omogućene

## 🔄 **Buduća poboljšanja:**

- JWT autentifikacija za WebSocket
- WebSocket session management
- Message persistence
- WebSocket metrics i monitoring
- Rate limiting za WebSocket poruke

## ✅ **Rešeni problemi:**

- ❌ **WebSocket connection failed** → ✅ WebSocket endpoint implementiran
- ❌ **404 for /ws** → ✅ WebSocket endpoint dostupan
- ❌ **Real-time communication** → ✅ STOMP message broker omogućen
- ❌ **Frontend WebSocket errors** → ✅ Backend WebSocket support dodat
