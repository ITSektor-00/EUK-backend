#!/usr/bin/env python3
"""
Generate 3000 test records for ugrozeno_lice_t1 table
This script creates SQL INSERT statements for testing batch import
"""

import random
from datetime import datetime, timedelta

def generate_test_data(count=3000):
    """Generate test data for ugrozeno_lice_t1 table"""
    
    # Sample data pools
    first_names = [
        'Marko', 'Ana', 'Petar', 'Milica', 'Stefan', 'Jovana', 'Nikola', 'Sara', 'Luka', 'Maja',
        'Aleksandar', 'Jelena', 'Miloš', 'Tamara', 'Nemanja', 'Katarina', 'Dušan', 'Milena', 'Vladimir', 'Jasmina',
        'Bojan', 'Snežana', 'Dejan', 'Marija', 'Milan', 'Ana', 'Zoran', 'Milica', 'Goran', 'Jelena',
        'Dragan', 'Svetlana', 'Radovan', 'Biljana', 'Slobodan', 'Vesna', 'Zoran', 'Ljiljana', 'Miloš', 'Radmila'
    ]
    
    last_names = [
        'Marković', 'Anić', 'Petrović', 'Milić', 'Stefanović', 'Jovanović', 'Nikolić', 'Sarić', 'Lukić', 'Majić',
        'Aleksandrić', 'Jelenić', 'Milošević', 'Tamarović', 'Nemanjić', 'Katarinić', 'Dušanić', 'Milenić', 'Vladimirović', 'Jasminić',
        'Bojanić', 'Snežanić', 'Dejanić', 'Marijić', 'Milanić', 'Anić', 'Zoranić', 'Milicić', 'Goranić', 'Jelenić',
        'Draganić', 'Svetlanić', 'Radovanić', 'Biljanić', 'Slobodanić', 'Vesnić', 'Zoranić', 'Ljiljanić', 'Milošić', 'Radmilić'
    ]
    
    cities = [
        'Beograd', 'Novi Sad', 'Niš', 'Kragujevac', 'Subotica', 'Zrenjanin', 'Pančevo', 'Čačak', 'Kraljevo', 'Smederevo',
        'Leskovac', 'Valjevo', 'Kruševac', 'Vranje', 'Šabac', 'Užice', 'Sombor', 'Požarevac', 'Pirot', 'Zaječar'
    ]
    
    streets = [
        'Knez Mihailova', 'Terazije', 'Kralja Milana', 'Nemanjina', 'Bulevar Kralja Aleksandra',
        'Vračar', 'Zvezdara', 'Palilula', 'Stari Grad', 'Savski Venac',
        'Novi Beograd', 'Zemun', 'Voždovac', 'Rakovica', 'Mladenovac'
    ]
    
    statuses = ['MP', 'NSP', 'DD', 'UDTNP']
    
    # Generate SQL INSERT statements
    sql_statements = []
    
    for i in range(1, count + 1):
        # Generate unique data
        first_name = random.choice(first_names)
        last_name = random.choice(last_names)
        jmbg = f"{random.randint(1000000000000, 9999999999999)}"
        redni_broj = f"RB{i:04d}"
        ptt_broj = f"{random.randint(10000, 99999)}"
        city = random.choice(cities)
        street = random.choice(streets)
        street_number = random.randint(1, 200)
        household_members = random.randint(1, 6)
        status = random.choice(statuses)
        ed_number = f"ED{i:04d}"
        consumption = round(random.uniform(1000, 5000), 2)
        area = round(random.uniform(40, 150), 1)
        reduction_amount = round(random.uniform(1000, 10000), 2)
        account_number = f"RAC{i:04d}"
        
        # Generate dates
        issue_date = datetime.now() - timedelta(days=random.randint(1, 365))
        expiry_date = issue_date + timedelta(days=365)
        
        # Create combined energy field
        energy_combined = f"Potrošnja u kWh/{consumption}/zagrevana površina u m2/{area}"
        
        # Create SQL VALUES clause
        values = f"""('{redni_broj}', '{first_name}', '{last_name}', '{jmbg}', '{ptt_broj}', '{city}', '{city}', '{street} {street_number}', {household_members}, '{status}', '{ed_number}', '{energy_combined}', {reduction_amount}, '{account_number}', '{issue_date.strftime('%Y-%m-%d')}', '{expiry_date.strftime('%Y-%m-%d')}', NOW(), NOW())"""
        
        sql_statements.append(values)
    
    return sql_statements

def create_sql_file(filename="insert_3000_test_lice.sql"):
    """Create SQL file with INSERT statements"""
    
    print(f"Generating {3000} test records...")
    sql_statements = generate_test_data(3000)
    
    with open(filename, 'w', encoding='utf-8') as f:
        f.write("-- Insert 3000 test records for ugrozeno_lice_t1 table\n")
        f.write("-- Generated for batch import testing\n\n")
        
        f.write("INSERT INTO euk.ugrozeno_lice_t1 (\n")
        f.write("    redni_broj, ime, prezime, jmbg, ptt_broj, grad_opstina, mesto,\n")
        f.write("    ulica_i_broj, broj_clanova_domacinstva, osnov_sticanja_statusa,\n")
        f.write("    ed_broj_broj_mernog_uredjaja, potrosnja_i_povrsina_combined,\n")
        f.write("    iznos_umanjenja_sa_pdv, broj_racuna, datum_izdavanja_racuna,\n")
        f.write("    datum_trajanja_prava, created_at, updated_at\n")
        f.write(") VALUES\n")
        
        # Write values in batches of 100
        batch_size = 100
        for i in range(0, len(sql_statements), batch_size):
            batch = sql_statements[i:i + batch_size]
            for j, statement in enumerate(batch):
                if i + j == len(sql_statements) - 1:
                    # Last statement
                    f.write(f"    {statement};\n\n")
                else:
                    f.write(f"    {statement},\n")
            
            if i + batch_size < len(sql_statements):
                f.write("\n-- Next batch\n")
                f.write("INSERT INTO euk.ugrozeno_lice_t1 (\n")
                f.write("    redni_broj, ime, prezime, jmbg, ptt_broj, grad_opstina, mesto,\n")
                f.write("    ulica_i_broj, broj_clanova_domacinstva, osnov_sticanja_statusa,\n")
                f.write("    ed_broj_broj_mernog_uredjaja, potrosnja_i_povrsina_combined,\n")
                f.write("    iznos_umanjenja_sa_pdv, broj_racuna, datum_izdavanja_racuna,\n")
                f.write("    datum_trajanja_prava, created_at, updated_at\n")
                f.write(") VALUES\n")
    
    print(f"SQL file created: {filename}")
    print(f"Total records: {len(sql_statements)}")

if __name__ == "__main__":
    create_sql_file()
