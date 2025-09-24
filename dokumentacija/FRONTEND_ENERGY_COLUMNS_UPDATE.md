# Frontend Update - Energy Columns Migration

## Overview
The backend has been updated to use a new combined energy column structure. The separate `potrosnjaKwh` and `zagrevanaPovrsinaM2` fields have been merged into a single `potrosnjaIPovrsinaCombined` field.

## API Changes

### New Field Structure
- **New field**: `potrosnjaIPovrsinaCombined` (String)
- **Format**: `"Потрошња у kWh/{kWh_value}/загревана површина у m2/{m2_value}"`
- **Old fields**: `potrosnjaKwh` and `zagrevanaPovrsinaM2` are still available for backward compatibility

### Example Data Format
```json
{
  "ugrozenoLiceId": 1,
  "ime": "Marko",
  "prezime": "Marković",
  "potrosnjaIPovrsinaCombined": "Потрошња у kWh/2500.50/загревана површина у m2/75.5",
  "potrosnjaKwh": 2500.50,  // Extracted from combined field
  "zagrevanaPovrsinaM2": 75.5,  // Extracted from combined field
  // ... other fields
}
```

## Frontend Implementation Options

### Option 1: Use New Combined Field (Recommended)
```typescript
interface UgrozenoLiceT1 {
  ugrozenoLiceId: number;
  ime: string;
  prezime: string;
  // ... other fields
  potrosnjaIPovrsinaCombined: string; // New combined field
  // ... other fields
}

// Parse the combined field
function parseEnergyData(combined: string) {
  const parts = combined.split('/');
  return {
    potrosnjaKwh: parts[1] ? parseFloat(parts[1]) : null,
    zagrevanaPovrsinaM2: parts[3] ? parseFloat(parts[3]) : null
  };
}

// Build the combined field
function buildEnergyData(potrosnjaKwh: number | null, zagrevanaPovrsinaM2: number | null) {
  return `Потрошња у kWh/${potrosnjaKwh || ''}/загревана површина у m2/${zagrevanaPovrsinaM2 || ''}`;
}
```

### Option 2: Use Backward Compatibility Fields
```typescript
interface UgrozenoLiceT1 {
  ugrozenoLiceId: number;
  ime: string;
  prezime: string;
  // ... other fields
  potrosnjaKwh: number | null; // Still available
  zagrevanaPovrsinaM2: number | null; // Still available
  // ... other fields
}
```

## Form Handling

### For Create/Update Operations
```typescript
// When creating/updating, you can use either approach:

// Approach 1: Use individual fields (backend will combine them)
const formData = {
  ime: "Marko",
  prezime: "Marković",
  potrosnjaKwh: 2500.50,
  zagrevanaPovrsinaM2: 75.5,
  // ... other fields
};

// Approach 2: Use combined field directly
const formData = {
  ime: "Marko",
  prezime: "Marković",
  potrosnjaIPovrsinaCombined: "Потрошња у kWh/2500.50/загревана површина у m2/75.5",
  // ... other fields
};
```

## Display in UI

### Option 1: Display Combined Field
```typescript
// Show the combined field as-is
<div>{ugrozenoLice.potrosnjaIPovrsinaCombined}</div>
```

### Option 2: Parse and Display Separately
```typescript
// Parse and display individual values
const { potrosnjaKwh, zagrevanaPovrsinaM2 } = parseEnergyData(ugrozenoLice.potrosnjaIPovrsinaCombined);

<div>
  <div>Potrošnja: {potrosnjaKwh} kWh</div>
  <div>Površina: {zagrevanaPovrsinaM2} m²</div>
</div>
```

## Migration Strategy

### Phase 1: Immediate (No Breaking Changes)
- Continue using `potrosnjaKwh` and `zagrevanaPovrsinaM2` fields
- Backend will automatically handle the conversion
- No frontend changes required

### Phase 2: Future Enhancement
- Gradually migrate to use `potrosnjaIPovrsinaCombined` field
- Implement parsing functions for better data handling
- Update forms to work with the new structure

## API Endpoints (No Changes)
All existing API endpoints remain the same:
- `GET /api/ugrozeno-lice-t1` - List all records
- `GET /api/ugrozeno-lice-t1/{id}` - Get single record
- `POST /api/ugrozeno-lice-t1` - Create new record
- `PUT /api/ugrozeno-lice-t1/{id}` - Update record
- `DELETE /api/ugrozeno-lice-t1/{id}` - Delete record

## Testing
- Test with existing data to ensure backward compatibility
- Test form submissions with both old and new field structures
- Verify that data is properly saved and retrieved

## Notes
- The migration is backward compatible
- Existing frontend code will continue to work
- New `potrosnjaIPovrsinaCombined` field is available for future enhancements
- Database migration has been completed on the backend
