# Frontend Migration Checklist - Energy Columns Update

## ✅ Pre-Migration Checklist

### 1. Backend Migration Status
- [ ] Database migration script executed successfully
- [ ] Backend code deployed with new energy column structure
- [ ] API endpoints tested and working
- [ ] Backward compatibility confirmed

### 2. Frontend Preparation
- [ ] Backup current frontend code
- [ ] Review existing energy field usage
- [ ] Identify components that use `potrosnjaKwh` and `zagrevanaPovrsinaM2`
- [ ] Plan migration strategy (immediate vs gradual)

## 🔄 Migration Options

### Option A: No Immediate Changes (Recommended)
- [ ] Continue using existing `potrosnjaKwh` and `zagrevanaPovrsinaM2` fields
- [ ] Backend automatically handles conversion
- [ ] Zero breaking changes
- [ ] Can migrate to new structure later

### Option B: Gradual Migration
- [ ] Update TypeScript interfaces (add `potrosnjaIPovrsinaCombined`)
- [ ] Add parsing utility functions
- [ ] Update forms to support both old and new fields
- [ ] Gradually migrate components to use new structure

### Option C: Full Migration
- [ ] Replace all energy field usage with new combined field
- [ ] Update all forms and components
- [ ] Remove old field dependencies
- [ ] Implement new parsing logic

## 📋 Implementation Steps

### Step 1: Update TypeScript Interfaces
```bash
# Copy the new interfaces to your project
cp frontend-types.ts src/types/
```

### Step 2: Add Utility Functions
```typescript
// Add to your utilities
import { EnergyDataParser } from './types/frontend-types';

// Use in components
const { potrosnjaKwh, zagrevanaPovrsinaM2 } = EnergyDataParser.parse(data.potrosnjaIPovrsinaCombined);
```

### Step 3: Update Forms (if needed)
```typescript
// Option 1: Keep existing form structure
const formData = {
  potrosnjaKwh: 2500.50,
  zagrevanaPovrsinaM2: 75.5,
  // ... other fields
};

// Option 2: Use new combined field
const formData = {
  potrosnjaIPovrsinaCombined: "Потрошња у kWh/2500.50/загревана површина у m2/75.5",
  // ... other fields
};
```

### Step 4: Update Display Components
```typescript
// Option 1: Parse and display separately
const { potrosnjaKwh, zagrevanaPovrsinaM2 } = EnergyDataParser.parse(data.potrosnjaIPovrsinaCombined);

// Option 2: Display combined field
<div>{data.potrosnjaIPovrsinaCombined}</div>
```

## 🧪 Testing Checklist

### API Testing
- [ ] Test GET endpoints return new field structure
- [ ] Test POST/PUT with old field structure (backward compatibility)
- [ ] Test POST/PUT with new field structure
- [ ] Verify data persistence and retrieval

### Component Testing
- [ ] Test form submission with individual fields
- [ ] Test form submission with combined field
- [ ] Test data display in both modes
- [ ] Test parsing utility functions

### Integration Testing
- [ ] Test complete CRUD operations
- [ ] Test search and filtering
- [ ] Test data export/import
- [ ] Test with existing data

## 🚀 Deployment Steps

### Development Environment
1. [ ] Update TypeScript interfaces
2. [ ] Add utility functions
3. [ ] Test with development backend
4. [ ] Verify all functionality works

### Production Environment
1. [ ] Deploy backend changes first
2. [ ] Deploy frontend changes
3. [ ] Monitor for any issues
4. [ ] Rollback plan ready if needed

## 📊 Monitoring

### Key Metrics to Watch
- [ ] API response times
- [ ] Error rates
- [ ] Data consistency
- [ ] User experience

### Rollback Plan
- [ ] Keep old field structure in code
- [ ] Database rollback script ready
- [ ] Frontend rollback deployment ready
- [ ] Communication plan for users

## 📝 Documentation Updates

### Code Documentation
- [ ] Update API documentation
- [ ] Update component documentation
- [ ] Update utility function documentation
- [ ] Update migration guide

### User Documentation
- [ ] Update user manual (if needed)
- [ ] Update admin guide
- [ ] Update troubleshooting guide

## ✅ Post-Migration Verification

### Data Integrity
- [ ] All existing data preserved
- [ ] New data saves correctly
- [ ] Search and filtering work
- [ ] Export/import functions work

### Performance
- [ ] No performance degradation
- [ ] API response times acceptable
- [ ] Frontend rendering smooth
- [ ] Database queries optimized

### User Experience
- [ ] No breaking changes for users
- [ ] Forms work as expected
- [ ] Data displays correctly
- [ ] Error handling appropriate

## 🆘 Troubleshooting

### Common Issues
1. **API returns old field structure**
   - Check backend deployment
   - Verify database migration

2. **Frontend parsing errors**
   - Check utility function implementation
   - Verify data format

3. **Form submission issues**
   - Check field mapping
   - Verify API endpoint compatibility

### Support Contacts
- Backend Team: [contact info]
- Frontend Team: [contact info]
- Database Team: [contact info]
