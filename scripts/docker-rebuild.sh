#!/bin/bash

echo "Rebuilding EUK Backend services..."
echo

# Stop services
echo "Stopping services..."
docker-compose down

# Remove old images
echo "Removing old images..."
docker-compose down --rmi all

# Rebuild and start
echo "Rebuilding and starting services..."
docker-compose up --build -d

echo
echo "Services rebuilt and started!"
echo
echo "Backend: http://localhost:8080"
echo "PostgreSQL: localhost:5432"
echo
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
echo
