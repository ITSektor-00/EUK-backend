#!/bin/bash

# EUK Database Setup Script
# This script sets up PostgreSQL database for EUK Backend

set -e

echo "🐘 Starting EUK Database Setup..."

# Configuration
DB_NAME="euk_database"
DB_USER="euk_user"
DB_PASSWORD=""
POSTGRES_USER="postgres"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
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

# Check if running as root
if [[ $EUID -eq 0 ]]; then
   print_error "This script should not be run as root"
   exit 1
fi

# Check if PostgreSQL is installed
if ! command -v psql &> /dev/null; then
    print_error "PostgreSQL is not installed. Please install it first:"
    echo "Ubuntu/Debian: sudo apt install postgresql postgresql-contrib"
    echo "CentOS/RHEL: sudo yum install postgresql-server postgresql-contrib"
    exit 1
fi

# Check if PostgreSQL service is running
if ! systemctl is-active --quiet postgresql; then
    print_warning "PostgreSQL service is not running. Starting it..."
    sudo systemctl start postgresql
    sudo systemctl enable postgresql
fi

# Get database password
if [ -z "$DB_PASSWORD" ]; then
    echo -n "Enter password for database user '$DB_USER': "
    read -s DB_PASSWORD
    echo
fi

# Validate password
if [ -z "$DB_PASSWORD" ]; then
    print_error "Password cannot be empty"
    exit 1
fi

print_status "Setting up database: $DB_NAME"
print_status "Database user: $DB_USER"

# Create database and user
print_status "Creating database and user..."

sudo -u $POSTGRES_USER psql << EOF
-- Create database
CREATE DATABASE $DB_NAME;

-- Create user
CREATE USER $DB_USER WITH PASSWORD '$DB_PASSWORD';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE $DB_NAME TO $DB_USER;

-- Connect to database and grant schema privileges
\c $DB_NAME
GRANT ALL ON SCHEMA public TO $DB_USER;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO $DB_USER;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO $DB_USER;

-- Set default privileges for future objects
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO $DB_USER;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO $DB_USER;

\q
EOF

if [ $? -eq 0 ]; then
    print_status "Database and user created successfully!"
else
    print_error "Failed to create database and user"
    exit 1
fi

# Test connection
print_status "Testing database connection..."
if psql -h localhost -U $DB_USER -d $DB_NAME -c "SELECT version();" > /dev/null 2>&1; then
    print_status "Database connection test successful!"
else
    print_error "Database connection test failed"
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    print_status "Creating .env file..."
    cat > .env << EOF
# Database Configuration - LOCAL POSTGRESQL
DATABASE_URL=jdbc:postgresql://localhost:5432/$DB_NAME
DATABASE_USERNAME=$DB_USER
DATABASE_PASSWORD=$DB_PASSWORD

# Application Configuration
SPRING_PROFILES_ACTIVE=prod

# JWT Configuration
JWT_SECRET=$(openssl rand -base64 32)
JWT_EXPIRATION=86400000

# Admin Configuration
ADMIN_PASSWORD=admin123!

# Server Configuration
PORT=8080

# EUK Domain Configuration
EUK_ALLOWED_DOMAINS=https://your-frontend-domain.com,https://your-other-domain.com
EUK_RATE_LIMIT_ENABLED=true
EUK_RATE_LIMIT_MAX_REQUESTS=150

# Security Headers
SECURITY_HEADERS_ENABLED=true
EOF
    print_status ".env file created successfully!"
else
    print_warning ".env file already exists. Please update it manually with the new database credentials."
fi

# Configure pg_hba.conf for local connections
print_status "Configuring PostgreSQL authentication..."

PG_VERSION=$(sudo -u $POSTGRES_USER psql -t -c "SELECT version();" | grep -oP '\d+\.\d+' | head -1)
PG_CONFIG_DIR="/etc/postgresql/$PG_VERSION/main"

if [ -d "$PG_CONFIG_DIR" ]; then
    # Backup original pg_hba.conf
    sudo cp "$PG_CONFIG_DIR/pg_hba.conf" "$PG_CONFIG_DIR/pg_hba.conf.backup"
    
    # Add local connection rules
    sudo tee -a "$PG_CONFIG_DIR/pg_hba.conf" > /dev/null << EOF

# EUK Backend local connections
local   $DB_NAME    $DB_USER    md5
host    $DB_NAME    $DB_USER    127.0.0.1/32    md5
host    $DB_NAME    $DB_USER    ::1/128         md5
EOF
    
    # Restart PostgreSQL to apply changes
    sudo systemctl restart postgresql
    print_status "PostgreSQL authentication configured and service restarted"
else
    print_warning "Could not find PostgreSQL config directory. Please configure pg_hba.conf manually."
fi

# Final test
print_status "Performing final connection test..."
if psql -h localhost -U $DB_USER -d $DB_NAME -c "SELECT 'Connection successful!' as status;" > /dev/null 2>&1; then
    print_status "✅ Database setup completed successfully!"
    echo
    echo "Database Details:"
    echo "  - Database: $DB_NAME"
    echo "  - User: $DB_USER"
    echo "  - Host: localhost"
    echo "  - Port: 5432"
    echo
    echo "Next steps:"
    echo "  1. Update .env file with your domain names in EUK_ALLOWED_DOMAINS"
    echo "  2. Change ADMIN_PASSWORD to a secure password"
    echo "  3. Run: docker-compose -f docker-compose.prod.yml up -d"
    echo
else
    print_error "Final connection test failed"
    exit 1
fi
