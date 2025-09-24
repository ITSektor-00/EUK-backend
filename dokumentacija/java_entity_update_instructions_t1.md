# Instrukcije za ažuriranje Java entiteta i DTO-a - ugrozeno_lice_t1

## Pregled promena

Nova tabela `ugrozeno_lice_t1` ima potpuno drugačiju strukturu od postojeće `ugrozeno_lice`. Evo šta treba ažurirati:

### 1. Ažuriranje EukUgrozenoLice.java entiteta

Trenutna struktura (stara):
```java
- ugrozenoLiceId (Integer)
- ime (String)
- prezime (String) 
- jmbg (String)
- datumRodjenja (LocalDate)
- drzavaRodjenja (String)
- mestoRodjenja (String)
- opstinaRodjenja (String)
- predmet (EukPredmet) - ManyToOne relacija
```

Nova struktura (ugrozeno_lice_t1):
```java
- ugrozenoLiceId (Integer) - PRIMARY KEY
- redniBroj (String)
- ime (String)
- prezime (String)
- jmbg (String)
- pttBroj (String)
- gradOpstina (String)
- mesto (String)
- ulicaIBroj (String)
- brojClanovaDomacinstva (Integer)
- osnovSticanjaStatusa (String) // MP, NSP, DD, UDTNP
- edBrojBrojMernogUredjaja (String)
- potrosnjaKwh (BigDecimal)
- zagrevanaPovrsinaM2 (BigDecimal)
- iznosUmanjenjaSaPdv (BigDecimal)
- brojRacuna (String)
- datumIzdavanjaRacuna (LocalDate)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)
```

### 2. Promena imena tabele

**VAŽNO**: Tabela se sada zove `ugrozeno_lice_t1` umesto `ugrozeno_lice`

```java
@Table(name = "ugrozeno_lice_t1", schema = "euk")
public class EukUgrozenoLice {
    // ...
}
```

### 3. Uklanjanje relacije sa EukPredmet

Stara tabela je imala foreign key `predmet_id` koji povezuje sa `euk.predmet` tabelom. Nova tabela nema ovu relaciju.

### 4. Ažuriranje EukUgrozenoLiceDto.java

DTO treba ažurirati da odgovara novim kolonama.

### 5. Ažuriranje EukUgrozenoLiceService.java

Service metode treba prilagoditi novoj strukturi.

### 6. Ažuriranje EukUgrozenoLiceRepository.java

Repository query-je treba ažurirati jer nema više JOIN sa predmet tabelom.

### 7. Ažuriranje EukUgrozenoLiceController.java

Controller treba ažurirati da koristi nove DTO polja.

## Koraci za implementaciju

1. **Prvo pokreni SQL skript** `ugrozeno_lice_t1_table_recreation.sql`
2. **Zatim ažuriraj Java klase** prema novoj strukturi
3. **Promeni ime tabele** u `@Table` anotaciji na `ugrozeno_lice_t1`
4. **Testiraj aplikaciju** da proveriš da li sve radi

## Napomene

- Nova tabela nema relaciju sa `predmet` tabelom
- Sve numeričke vrednosti za novac i potrošnju koriste `BigDecimal`
- Datumi koriste `LocalDate` i `LocalDateTime`
- Validacije treba prilagoditi novim poljima
- **Ime tabele je `ugrozeno_lice_t1`** (ne `ugrozeno_lice`)

## Lista fajlova za ažuriranje

1. `src/main/java/com/sirus/backend/entity/EukUgrozenoLice.java`
2. `src/main/java/com/sirus/backend/dto/EukUgrozenoLiceDto.java`
3. `src/main/java/com/sirus/backend/service/EukUgrozenoLiceService.java`
4. `src/main/java/com/sirus/backend/repository/EukUgrozenoLiceRepository.java`
5. `src/main/java/com/sirus/backend/controller/EukUgrozenoLiceController.java`
