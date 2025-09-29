@echo off
echo Starting EUK Backend...

docker run -d ^
  --name sirus-backend ^
  --restart unless-stopped ^
  -p 8080:8080 ^
  -e SPRING_PROFILES_ACTIVE=prod ^
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/euk_database ^
  -e DATABASE_USERNAME=postgres ^
  -e DATABASE_PASSWORD=password ^
  -e JWT_SECRET=your-super-secret-jwt-key-here-make-it-long-and-random ^
  -e JWT_EXPIRATION=86400000 ^
  -e ADMIN_PASSWORD=admin123 ^
  -e EUK_ALLOWED_DOMAINS=http://localhost:3000,https://euk.vercel.app ^
  -e EUK_RATE_LIMIT_ENABLED=true ^
  -e EUK_RATE_LIMIT_MAX_REQUESTS=150 ^
  sirus-backend:latest

echo Backend started on http://localhost:8080
echo Check logs with: docker logs sirus-backend
echo Stop with: docker stop sirus-backend
pause

