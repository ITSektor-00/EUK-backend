#!/bin/bash

# EUK Backend Deployment Script with PostgreSQL
# This script deploys the complete EUK Backend stack with PostgreSQL database

set -e

echo "🚀 Starting EUK Backend Deployment with PostgreSQL..."

# Configuration
CONTAINER_NAME_BACKEND="sirus-backend"
CONTAINER_NAME_POSTGRES="euk-postgres"
IMAGE_NAME="sirus-backend:latest"
POSTGRES_IMAGE="postgres:15"
PORT_BACKEND="8080"
PORT_POSTGRES="5432"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Check if Docker is installed and running
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed. Please install Docker first."
    exit 1
fi

if ! docker info &> /dev/null; then
    print_error "Docker is not running. Please start Docker first."
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    if [ -f ../.env ]; then
        print_status "Using .env file from parent directory..."
        source ../.env
    else
        print_error ".env file not found. Please create it first."
        echo "You can use the setup-database.sh script to create it automatically."
        exit 1
    fi
else
    # Load environment variables
    source .env
fi

print_header "1. Stopping existing containers..."
docker stop $CONTAINER_NAME_BACKEND $CONTAINER_NAME_POSTGRES 2>/dev/null || true
docker rm $CONTAINER_NAME_BACKEND $CONTAINER_NAME_POSTGRES 2>/dev/null || true

print_header "2. Building Docker image..."
docker build -t $IMAGE_NAME .
if [ $? -eq 0 ]; then
    print_status "Docker image built successfully!"
else
    print_error "Failed to build Docker image"
    exit 1
fi

print_header "3. Starting PostgreSQL container..."
docker run -d \
    --name $CONTAINER_NAME_POSTGRES \
    -e POSTGRES_DB=euk_database \
    -e POSTGRES_USER=euk_user \
    -e POSTGRES_PASSWORD="$DATABASE_PASSWORD" \
    -p $PORT_POSTGRES:5432 \
    -v postgres_data:/var/lib/postgresql/data \
    -v ./src/main/resources/prod/schema.sql:/docker-entrypoint-initdb.d/01-schema.sql \
    -v ./src/main/resources/prod/routes_schema.sql:/docker-entrypoint-initdb.d/02-routes.sql \
    --restart unless-stopped \
    $POSTGRES_IMAGE

if [ $? -eq 0 ]; then
    print_status "PostgreSQL container started successfully!"
else
    print_error "Failed to start PostgreSQL container"
    exit 1
fi

print_header "4. Waiting for PostgreSQL to be ready..."
sleep 10

# Wait for PostgreSQL to be ready
for i in {1..30}; do
    if docker exec $CONTAINER_NAME_POSTGRES pg_isready -U euk_user -d euk_database > /dev/null 2>&1; then
        print_status "PostgreSQL is ready!"
        break
    fi
    if [ $i -eq 30 ]; then
        print_error "PostgreSQL failed to start within 30 seconds"
        exit 1
    fi
    echo -n "."
    sleep 1
done

print_header "5. Starting Backend container..."
docker run -d \
    --name $CONTAINER_NAME_BACKEND \
    -p $PORT_BACKEND:8080 \
    --env-file .env \
    -e DATABASE_URL=jdbc:postgresql://$CONTAINER_NAME_POSTGRES:5432/euk_database \
    -e DATABASE_USERNAME=euk_user \
    -e DATABASE_PASSWORD="$DATABASE_PASSWORD" \
    --restart unless-stopped \
    $IMAGE_NAME

if [ $? -eq 0 ]; then
    print_status "Backend container started successfully!"
else
    print_error "Failed to start Backend container"
    exit 1
fi

print_header "6. Waiting for Backend to be ready..."
sleep 15

# Wait for Backend to be ready
for i in {1..60}; do
    if curl -f http://localhost:$PORT_BACKEND/api/test/health > /dev/null 2>&1; then
        print_status "Backend is ready!"
        break
    fi
    if [ $i -eq 60 ]; then
        print_warning "Backend health check failed, but container is running"
        break
    fi
    echo -n "."
    sleep 1
done

print_header "7. Deployment Summary"
echo "=========================================="
echo "✅ Deployment completed successfully!"
echo "=========================================="
echo
echo "Container Details:"
echo "  - Backend: $CONTAINER_NAME_BACKEND (Port: $PORT_BACKEND)"
echo "  - PostgreSQL: $CONTAINER_NAME_POSTGRES (Port: $PORT_POSTGRES)"
echo
echo "Application URLs:"
echo "  - Backend API: http://localhost:$PORT_BACKEND"
echo "  - Health Check: http://localhost:$PORT_BACKEND/api/test/health"
echo
echo "Database Details:"
echo "  - Host: localhost"
echo "  - Port: $PORT_POSTGRES"
echo "  - Database: euk_database"
echo "  - User: euk_user"
echo
echo "Useful Commands:"
echo "  - View logs: docker logs $CONTAINER_NAME_BACKEND"
echo "  - View PostgreSQL logs: docker logs $CONTAINER_NAME_POSTGRES"
echo "  - Stop containers: docker stop $CONTAINER_NAME_BACKEND $CONTAINER_NAME_POSTGRES"
echo "  - Remove containers: docker rm $CONTAINER_NAME_BACKEND $CONTAINER_NAME_POSTGRES"
echo "  - Connect to database: docker exec -it $CONTAINER_NAME_POSTGRES psql -U euk_user -d euk_database"
echo

# Test the deployment
print_header "8. Testing deployment..."
if curl -f http://localhost:$PORT_BACKEND/api/test/health > /dev/null 2>&1; then
    print_status "✅ Health check passed!"
    echo "Response:"
    curl -s http://localhost:$PORT_BACKEND/api/test/health | python3 -m json.tool 2>/dev/null || curl -s http://localhost:$PORT_BACKEND/api/test/health
else
    print_warning "⚠️ Health check failed, but deployment completed"
    echo "Check logs with: docker logs $CONTAINER_NAME_BACKEND"
fi

echo
print_status "🎉 EUK Backend deployment completed!"
