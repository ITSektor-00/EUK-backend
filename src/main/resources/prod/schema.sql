-- Kreiraj EUK schema
CREATE SCHEMA IF NOT EXISTS "EUK";

-- Kreiraj users tabelu (PostgreSQL sintaksa)
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Kreiraj index za brže pretrage
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Kreiraj sessions tabelu za JWT token management
CREATE TABLE IF NOT EXISTS user_sessions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Kreiraj index za token pretrage
CREATE INDEX IF NOT EXISTS idx_sessions_token ON user_sessions(token);
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON user_sessions(user_id);

-- EUK Kategorija tabela
CREATE TABLE IF NOT EXISTS "EUK".kategorija (
    kategorija_id SERIAL PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL
);

-- EUK Predmet tabela
CREATE TABLE IF NOT EXISTS "EUK".predmet (
    predmet_id SERIAL PRIMARY KEY,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT,
    prioritet VARCHAR(50),
    status VARCHAR(50),
    kategorija_id INTEGER REFERENCES "EUK".kategorija(kategorija_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- EUK Ugrozeno Lice tabela
CREATE TABLE IF NOT EXISTS "EUK".ugrozeno_lice (
    lice_id SERIAL PRIMARY KEY,
    ime VARCHAR(100) NOT NULL,
    prezime VARCHAR(100) NOT NULL,
    datum_rodjenja DATE,
    mesto_rodjenja VARCHAR(100),
    adresa TEXT,
    telefon VARCHAR(20),
    email VARCHAR(100),
    predmet_id INTEGER REFERENCES "EUK".predmet(predmet_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Kreiraj indexe za EUK tabele
CREATE INDEX IF NOT EXISTS idx_euk_kategorija_naziv ON "EUK".kategorija(naziv);
CREATE INDEX IF NOT EXISTS idx_euk_predmet_kategorija ON "EUK".predmet(kategorija_id);
CREATE INDEX IF NOT EXISTS idx_euk_predmet_status ON "EUK".predmet(status);
CREATE INDEX IF NOT EXISTS idx_euk_ugrozeno_lice_predmet ON "EUK".ugrozeno_lice(predmet_id);

-- Dodaj test podatke
INSERT INTO "EUK".kategorija (naziv) VALUES 
    ('Krivična dela'),
    ('Građanski sporovi'),
    ('Upravni sporovi'),
    ('Privredni sporovi')
ON CONFLICT DO NOTHING;

INSERT INTO "EUK".predmet (naziv, opis, prioritet, status, kategorija_id) VALUES 
    ('Predmet 001', 'Test predmet za krivična dela', 'VISOK', 'AKTIVAN', 1),
    ('Predmet 002', 'Test predmet za građanske sporove', 'SREDNJI', 'AKTIVAN', 2)
ON CONFLICT DO NOTHING; 