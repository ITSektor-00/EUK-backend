-- Provera trenutnih kolona i tipova u tabeli ugrozeno_lice_t1

SELECT 
    column_name,
    data_type,
    character_maximum_length,
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_schema = 'euk' 
    AND table_name = 'ugrozeno_lice_t1'
ORDER BY ordinal_position;

