# Energy Columns Migration Summary

## Overview
This migration drops both `potrosnja_kwh` and `zagrevana_povrsina_m2` columns and creates a new combined column `potrosnja_i_povrsina_combined` in the `ugrozeno_lice_t1` table. The new column stores combined data in the format: "Потрошња у kWh/{kWh_value}/загревана површина у m2/{m2_value}"

## Files Modified

### 1. Database Migration Script
**File:** `merge_energy_columns_into_existing.sql`
- Drops existing `potrosnja_i_povrsina_combined` column if it exists
- Creates new `potrosnja_i_povrsina_combined` column (VARCHAR(200))
- Merges data from both `potrosnja_kwh` and `zagrevana_povrsina_m2` into the new column
- Drops both old columns (`potrosnja_kwh` and `zagrevana_povrsina_m2`)
- **Recreates the table with proper column positioning** - `potrosnja_i_povrsina_combined` is positioned between `osnov_sticanja_statusa` and `iznos_umanjenja_sa_pdv`
- Recreates all indexes for optimal performance
- Provides a backward compatibility view

### 2. Entity Class
**File:** `src/main/java/com/sirus/backend/entity/EukUgrozenoLiceT1.java`
- Added new field `potrosnjaIPovrsinaCombined` with `@Column(name = "potrosnja_i_povrsina_combined")`
- Removed old fields `potrosnjaKwh` and `zagrevanaPovrsinaM2` from entity mapping
- Added helper methods for backward compatibility:
  - `getPotrosnjaKwh()` - extracts kWh value from combined field
  - `setPotrosnjaKwh()` - updates combined field with new kWh value
  - `getZagrevanaPovrsinaM2()` - extracts m² value from combined field
  - `setZagrevanaPovrsinaM2()` - updates combined field with new m² value
  - `updateCombinedField()` - private method to build the combined string

### 3. DTO Class
**File:** `src/main/java/com/sirus/backend/dto/EukUgrozenoLiceT1Dto.java`
- Added new field `potrosnjaKwhCombined` with validation
- Kept old fields `potrosnjaKwh` and `zagrevanaPovrsinaM2` for backward compatibility
- Added getter/setter for the new combined field

### 4. Service Class
**File:** `src/main/java/com/sirus/backend/service/EukUgrozenoLiceT1Service.java`
- Updated `convertToDto()` method to map the new combined field
- Updated `convertToEntity()` and `updateEntityFromDto()` methods to handle both:
  - Direct combined field input (if provided)
  - Individual field input (builds combined field automatically)

### 5. Repository Class
**File:** `src/main/java/com/sirus/backend/repository/EukUgrozenoLiceT1Repository.java`
- Updated aggregation queries to work with the new combined field structure:
  - `avgPotrosnjaKwh()` - uses PostgreSQL string functions to extract kWh values from `potrosnjaKwhCombined`
  - `avgZagrevanaPovrsinaM2()` - uses PostgreSQL string functions to extract m² values from `potrosnjaKwhCombined`

## Data Format
The new combined column format is:
```
"Потрошња у kWh/{kWh_value}/загревана површина у m2/{m2_value}"
```

Examples:
- `"Потрошња у kWh/2500.50/загревана површина у m2/75.5"`
- `"Потрошња у kWh/1200/загревана површина у m2/50"`
- `"Потрошња у kWh//загревана површина у m2/30"` (no kWh value)
- `"Потрошња у kWh/3000/загревана површина у m2/"` (no m² value)

## Backward Compatibility
The implementation maintains backward compatibility by:
1. Keeping old field names in DTOs
2. Providing helper methods in the entity to extract individual values
3. Automatic conversion between individual fields and combined field
4. Optional backward compatibility view in the database

## Migration Steps
1. **Run the database migration script** (`merge_energy_columns_into_existing.sql`)
2. **Deploy the updated Java code**
3. **Test the application** to ensure data is properly converted
4. **Verify that the `zagrevana_povrsina_m2` column has been dropped** (this happens automatically in the migration)

## Testing
After migration, verify:
- Data is properly converted to the new format
- Individual field extraction works correctly
- API endpoints return expected data
- Statistics and aggregation queries work properly

## Rollback Plan
If rollback is needed:
1. Restore old column structure from backup (restore `zagrevana_povrsina_m2` column and convert `potrosnja_kwh` back to NUMERIC)
2. Revert Java code changes
3. Update data from combined field back to individual columns

## Notes
- The migration script includes safety measures (backup creation)
- All changes maintain API compatibility
- The new structure allows for more flexible data representation
- PostgreSQL string functions are used for data extraction in queries
- The existing `potrosnja_kwh` column is reused instead of creating a new column
