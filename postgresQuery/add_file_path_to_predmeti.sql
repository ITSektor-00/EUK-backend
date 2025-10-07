-- Dodavanje kolone za čuvanje putanje fizičkog fajla u predmeti tabeli
ALTER TABLE euk.predmet
ADD COLUMN IF NOT EXISTS template_file_path VARCHAR(500),
ADD COLUMN IF NOT EXISTS template_generated_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS template_status VARCHAR(50) DEFAULT 'pending';
