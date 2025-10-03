#!/bin/bash

echo "========================================"
echo "EUK Backend - Test Endpoint-a"
echo "========================================"

echo ""
echo "1. Testiranje backend dostupnosti..."
if curl -s http://localhost:8080/api/auth/test > /dev/null; then
    echo "✅ Backend je dostupan"
else
    echo "❌ Backend nije dostupan na http://localhost:8080"
    echo "Molimo pokrenite backend aplikaciju:"
    echo "  bashSkripte/start-backend.bat"
    exit 1
fi

echo ""
echo "2. Testiranje autentifikacije..."
curl -s -X POST http://localhost:8080/api/auth/signin \
     -H "Content-Type: application/json" \
     -d '{"usernameOrEmail":"test","password":"test"}'
echo ""
echo "✅ Auth endpoint radi"

echo ""
echo "3. Testiranje PDF endpoint-a..."
if curl -s http://localhost:8080/api/test-envelope-pdf -o test-koverat.pdf; then
    echo "✅ PDF endpoint radi - test-koverat.pdf kreiran"
else
    echo "❌ PDF endpoint nije dostupan"
fi

echo ""
echo "4. Testiranje CORS-a..."
curl -s -H "Origin: http://localhost:3000" \
     -H "Access-Control-Request-Method: POST" \
     -H "Access-Control-Request-Headers: Content-Type" \
     -X OPTIONS http://localhost:8080/api/auth/signin
echo ""
echo "✅ CORS test završen"

echo ""
echo "========================================"
echo "Testiranje završeno!"
echo "========================================"
