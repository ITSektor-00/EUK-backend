# Backend Fix Instructions - Energy Columns Update

## ✅ Problem Solved

### **Issue:**
- HQL (Hibernate Query Language) doesn't support PostgreSQL-specific functions like `SPLIT_PART` and regex operator `~`
- Application failed to start with HQL grammar errors

### **Solution:**
- Changed queries from HQL to native SQL using `@Query(nativeQuery = true)`
- Added fallback queries for backward compatibility
- Added error handling in service layer

## 🔧 Changes Made

### 1. **Repository Updates** (`EukUgrozenoLiceT1Repository.java`)
```java
// Before (HQL - doesn't work)
@Query("SELECT AVG(CAST(SPLIT_PART(...) FROM EukUgrozenoLiceT1 u WHERE ...")

// After (Native SQL - works)
@Query(value = "SELECT AVG(CAST(SPLIT_PART(...) FROM euk.ugrozeno_lice_t1 u WHERE ...", nativeQuery = true)
```

### 2. **Service Updates** (`EukUgrozenoLiceT1Service.java`)
```java
// Added fallback mechanism
try {
    stats.put("avgPotrosnjaKwh", repository.avgPotrosnjaKwh());
    stats.put("avgZagrevanaPovrsinaM2", repository.avgZagrevanaPovrsinaM2());
} catch (Exception e) {
    // Fallback to old queries
    stats.put("avgPotrosnjaKwh", repository.avgPotrosnjaKwhOld());
    stats.put("avgZagrevanaPovrsinaM2", repository.avgZagrevanaPovrsinaM2Old());
}
```

## 🚀 How to Deploy

### **Step 1: Deploy Code Changes**
```bash
# Build the application
mvn clean package

# Deploy to your server
# (Your deployment process)
```

### **Step 2: Run Database Migration**
```bash
# Execute the migration script
psql -h your-host -U your-user -d your-database -f merge_energy_columns_into_existing.sql
```

### **Step 3: Verify Deployment**
```bash
# Check application logs
tail -f logs/application.log

# Test API endpoints
curl http://localhost:8080/api/ugrozeno-lice-t1/stats
```

## 📋 Testing Checklist

### **Before Migration:**
- [ ] Application starts successfully
- [ ] Old statistics queries work
- [ ] API endpoints respond correctly
- [ ] No HQL grammar errors

### **After Migration:**
- [ ] New combined column exists
- [ ] Data is properly migrated
- [ ] New statistics queries work
- [ ] Old and new data formats both work

## 🔍 Troubleshooting

### **If Application Still Fails to Start:**
1. Check database connection
2. Verify table structure
3. Check for missing columns
4. Review error logs

### **If Statistics Don't Work:**
1. Check if migration was successful
2. Verify column names in database
3. Test queries manually in database
4. Check service logs

### **If Data Migration Fails:**
1. Check database permissions
2. Verify table exists
3. Check for data conflicts
4. Review migration script logs

## 📊 Expected Results

### **Before Migration:**
- Application starts with fallback queries
- Statistics use old column structure
- No breaking changes

### **After Migration:**
- Application uses new combined column
- Statistics work with new structure
- Data is properly formatted

## 🆘 Support

### **Common Issues:**
1. **HQL Grammar Errors** → Use native SQL queries
2. **Column Not Found** → Run database migration
3. **Statistics Fail** → Check fallback queries
4. **Data Format Issues** → Verify migration script

### **Debug Commands:**
```bash
# Check table structure
psql -c "\d euk.ugrozeno_lice_t1"

# Test statistics query
psql -c "SELECT AVG(CAST(SPLIT_PART(SPLIT_PART(potrosnja_i_povrsina_combined, 'Потрошња у kWh/', 2), '/', 1) AS DECIMAL)) FROM euk.ugrozeno_lice_t1 WHERE potrosnja_i_povrsina_combined IS NOT NULL;"

# Check application logs
grep "ERROR" logs/application.log
```

## ✅ Success Criteria

- [ ] Application starts without errors
- [ ] All API endpoints work
- [ ] Statistics queries return correct values
- [ ] Database migration completed successfully
- [ ] No data loss during migration
- [ ] Frontend can connect and use API

## 📝 Next Steps

1. **Deploy code changes**
2. **Run database migration**
3. **Test all functionality**
4. **Monitor for issues**
5. **Update frontend team**
6. **Document any issues**

---

**The application should now start successfully!** 🎯
