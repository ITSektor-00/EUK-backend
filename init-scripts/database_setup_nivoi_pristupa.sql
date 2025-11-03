-- Database setup for new access level system
-- Run this script to create the new tables and update existing ones

-- 1. Drop existing tables if they exist (be careful in production!)
DROP TABLE IF EXISTS user_routes CASCADE;
DROP TABLE IF EXISTS rute CASCADE;

-- 2. Create new rute table with access levels
CREATE TABLE rute (
    id SERIAL PRIMARY KEY,
    ruta VARCHAR(255) NOT NULL UNIQUE,
    naziv VARCHAR(255) NOT NULL,
    opis TEXT,
    sekcija VARCHAR(100),
    nivo_min INTEGER NOT NULL DEFAULT 1,
    nivo_max INTEGER NOT NULL DEFAULT 5,
    aktivna BOOLEAN DEFAULT true,
    datum_kreiranja TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create new user_routes table with access levels
CREATE TABLE user_routes (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    route_id INTEGER NOT NULL,
    nivo_dozvole INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (route_id) REFERENCES rute(id) ON DELETE CASCADE,
    UNIQUE(user_id, route_id)
);

-- 4. Add nivo_pristupa column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS nivo_pristupa INTEGER DEFAULT 1;

-- 5. Insert initial routes
INSERT INTO rute (ruta, naziv, opis, sekcija, nivo_min, nivo_max) VALUES
    ('euk/kategorije', 'Kategorije', 'Upravljanje kategorijama predmeta', 'EUK', 1, 5),
    ('euk/predmeti', 'Predmeti', 'Upravljanje predmetima', 'EUK', 1, 5),
    ('euk/ugrozena-lica', 'Ugrožena lica', 'Upravljanje ugroženim licima', 'EUK', 2, 5),
    ('euk/stampanje', 'Štampanje', 'Štampanje dokumenata', 'EUK', 1, 5),
    ('admin/korisnici', 'Korisnici', 'Upravljanje korisnicima', 'ADMIN', 5, 5),
    ('admin/rute', 'Rute', 'Upravljanje rutama', 'ADMIN', 5, 5),
    ('admin/user-routes', 'Korisničke rute', 'Upravljanje korisničkim rutama', 'ADMIN', 5, 5)
ON CONFLICT (ruta) DO NOTHING;

-- 6. Set default access levels for existing users
UPDATE users SET nivo_pristupa = 5 WHERE role = 'ADMIN';
UPDATE users SET nivo_pristupa = 3 WHERE role = 'USER' AND nivo_pristupa = 1;

-- 7. Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_user_routes_user_id ON user_routes(user_id);
CREATE INDEX IF NOT EXISTS idx_user_routes_route_id ON user_routes(route_id);
CREATE INDEX IF NOT EXISTS idx_user_routes_nivo_dozvole ON user_routes(nivo_dozvole);
CREATE INDEX IF NOT EXISTS idx_rute_aktivna ON rute(aktivna);
CREATE INDEX IF NOT EXISTS idx_rute_sekcija ON rute(sekcija);
CREATE INDEX IF NOT EXISTS idx_users_nivo_pristupa ON users(nivo_pristupa);

-- 8. Insert initial user routes based on access levels
-- Admin users get access to all routes with their level
INSERT INTO user_routes (user_id, route_id, nivo_dozvole)
SELECT u.id, r.id, u.nivo_pristupa
FROM users u, rute r
WHERE u.role = 'ADMIN'
ON CONFLICT (user_id, route_id) DO NOTHING;

-- Regular users get access to routes they can access based on their level
INSERT INTO user_routes (user_id, route_id, nivo_dozvole)
SELECT u.id, r.id, u.nivo_pristupa
FROM users u, rute r
WHERE u.role = 'USER' 
  AND u.nivo_pristupa >= r.nivo_min
  AND u.nivo_pristupa <= r.nivo_max
ON CONFLICT (user_id, route_id) DO NOTHING;
