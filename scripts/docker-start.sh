#!/bin/bash

echo "Starting EUK Backend with Docker..."
echo

# Check if .env file exists
if [ ! -f .env ]; then
    echo ".env file not found! Copying from .env.example..."
    cp .env.example .env
    echo "Please edit .env file with your configuration before running again."
    exit 1
fi

# Start services
echo "Starting services..."
docker-compose up -d

echo
echo "Services started!"
echo
echo "Backend: http://localhost:8080"
echo "PostgreSQL: localhost:5432"
echo
echo "To view logs: docker-compose logs -f"
echo "To stop: docker-compose down"
echo
