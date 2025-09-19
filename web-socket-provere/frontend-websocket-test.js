// Frontend WebSocket Test
// Ovo je JavaScript kod koji možeš da pokreneš u browser console

console.log('🔌 Starting WebSocket test...');

// Test 1: Basic WebSocket connection
function testBasicWebSocket() {
    console.log('📡 Testing basic WebSocket connection...');
    
    try {
        const socket = new WebSocket('ws://localhost:8080/ws');
        
        socket.onopen = function(event) {
            console.log('✅ WebSocket connected successfully!');
            console.log('Event:', event);
            
            // Send test message
            const testMsg = { type: 'TEST', message: 'Hello from frontend!' };
            socket.send(JSON.stringify(testMsg));
            console.log('📤 Sent test message:', testMsg);
        };
        
        socket.onmessage = function(event) {
            console.log('📨 Message received:', event.data);
        };
        
        socket.onclose = function(event) {
            console.log('🔌 WebSocket closed:', event);
        };
        
        socket.onerror = function(error) {
            console.error('🚨 WebSocket error:', error);
        };
        
        return socket;
        
    } catch (error) {
        console.error('❌ WebSocket connection failed:', error);
        return null;
    }
}

// Test 2: SockJS connection
function testSockJS() {
    console.log('📡 Testing SockJS connection...');
    
    // Check if SockJS is available
    if (typeof SockJS === 'undefined') {
        console.error('❌ SockJS not loaded. Please include SockJS library first.');
        return null;
    }
    
    try {
        const sockjs = new SockJS('http://localhost:8080/ws');
        
        sockjs.onopen = function(event) {
            console.log('✅ SockJS connected successfully!');
            console.log('Event:', event);
        };
        
        sockjs.onmessage = function(event) {
            console.log('📨 SockJS message received:', event.data);
        };
        
        sockjs.onclose = function(event) {
            console.log('🔌 SockJS closed:', event);
        };
        
        sockjs.onerror = function(error) {
            console.error('🚨 SockJS error:', error);
        };
        
        return sockjs;
        
    } catch (error) {
        console.error('❌ SockJS connection failed:', error);
        return null;
    }
}

// Test 3: HTTP endpoint test
async function testHttpEndpoint() {
    console.log('📡 Testing HTTP endpoint...');
    
    try {
        const response = await fetch('http://localhost:8080/ws/health');
        const data = await response.json();
        
        console.log('✅ HTTP endpoint working:', data);
        console.log('Status:', response.status);
        console.log('Headers:', response.headers);
        
        return data;
        
    } catch (error) {
        console.error('❌ HTTP endpoint failed:', error);
        return null;
    }
}

// Run all tests
async function runAllTests() {
    console.log('🚀 Running all WebSocket tests...');
    
    // Test 1: HTTP endpoint
    await testHttpEndpoint();
    
    // Test 2: Basic WebSocket
    const basicSocket = testBasicWebSocket();
    
    // Test 3: SockJS (if available)
    const sockjsSocket = testSockJS();
    
    console.log('🎯 All tests completed. Check console for results.');
    
    return {
        basicSocket,
        sockjsSocket
    };
}

// Export functions for manual testing
window.WebSocketTest = {
    testBasicWebSocket,
    testSockJS,
    testHttpEndpoint,
    runAllTests
};

console.log('💡 WebSocket test functions loaded. Use WebSocketTest.runAllTests() to run tests.');
console.log('💡 Or call individual functions: WebSocketTest.testBasicWebSocket()');
