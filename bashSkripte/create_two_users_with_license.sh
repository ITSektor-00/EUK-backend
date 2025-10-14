#!/bin/bash

# Postavi encoding za UTF-8
export LC_ALL=C.UTF-8
export LANG=C.UTF-8

echo "=========================================="
echo "Kreiranje admin korisnika i globalne licence"
echo "=========================================="
echo ""

# Učitaj parametre iz environment varijabli
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-EUK}
DB_USER=${DB_USERNAME:-postgres}
DB_PASSWORD=${DB_PASSWORD:-luka}

echo ""
echo "Konekcioni parametri:"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "Database: $DB_NAME"
echo "User: $DB_USER"
echo ""

# Proveri da li je PostgreSQL dostupan (čekaj do 30 sekundi)
echo "Čekam da PostgreSQL bude dostupan..."
MAX_RETRIES=30
RETRY_COUNT=0

while ! pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" > /dev/null 2>&1; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ $RETRY_COUNT -ge $MAX_RETRIES ]; then
        echo "GREŠKA: PostgreSQL nije dostupan na $DB_HOST:$DB_PORT nakon $MAX_RETRIES pokušaja"
        exit 1
    fi
    echo "Pokušaj $RETRY_COUNT/$MAX_RETRIES - čekam PostgreSQL..."
    sleep 1
done

echo "PostgreSQL server je dostupan ✓"
echo ""
echo "Kreiram admin korisnika i licencu..."
echo ""

# Pokreni SQL upit
export PGPASSWORD="$DB_PASSWORD"

# Pronađi SQL fajl - može biti na više lokacija zavisno od okruženja
SQL_FILE=""
if [ -f "/app/sql/create_two_users_with_global_license.sql" ]; then
    SQL_FILE="/app/sql/create_two_users_with_global_license.sql"
elif [ -f "../postgresQuery/create_two_users_with_global_license.sql" ]; then
    SQL_FILE="../postgresQuery/create_two_users_with_global_license.sql"
elif [ -f "./postgresQuery/create_two_users_with_global_license.sql" ]; then
    SQL_FILE="./postgresQuery/create_two_users_with_global_license.sql"
else
    echo "GREŠKA: Ne mogu da pronađem SQL fajl create_two_users_with_global_license.sql"
    exit 1
fi

psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$SQL_FILE"

if [ $? -ne 0 ]; then
    echo ""
    echo "GREŠKA pri kreiranju admin korisnika i licence!"
    exit 1
fi

echo ""
echo "=========================================="
echo "✓ Uspešno kreiran admin korisnik i licenca!"
echo "=========================================="
echo ""
echo "Admin korisnik:"
echo "  - Username: admin"
echo "  - Email: admin@euk.rs"
echo "  - Password: admin123!"
echo "  - Role: ADMIN"
echo ""
echo "Globalna licenca: EUK-GLOBAL-LICENSE-2025-2026"
echo "  - Važi od danas"
echo "  - Ističe za godinu dana"
echo ""

