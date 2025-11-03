# PowerShell script to create the euk schema
Write-Host "Creating euk schema..." -ForegroundColor Green

# Get database connection details
$DB_HOST = Read-Host "Database host (default: localhost)"
if ([string]::IsNullOrEmpty($DB_HOST)) { $DB_HOST = "localhost" }

$DB_PORT = Read-Host "Database port (default: 5432)"
if ([string]::IsNullOrEmpty($DB_PORT)) { $DB_PORT = "5432" }

$DB_NAME = Read-Host "Database name"
if ([string]::IsNullOrEmpty($DB_NAME)) {
    Write-Host "Database name is required!" -ForegroundColor Red
    exit 1
}

$DB_USER = Read-Host "Database user (default: postgres)"
if ([string]::IsNullOrEmpty($DB_USER)) { $DB_USER = "postgres" }

Write-Host "Creating euk schema in database: $DB_NAME" -ForegroundColor Yellow

# Create the schema
$createSchemaSQL = @"
CREATE SCHEMA IF NOT EXISTS "euk";
GRANT ALL ON SCHEMA "euk" TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA "euk" TO PUBLIC;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "euk" TO PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA "euk" GRANT ALL ON TABLES TO PUBLIC;
ALTER DEFAULT PRIVILEGES IN SCHEMA "euk" GRANT ALL ON SEQUENCES TO PUBLIC;
"@

try {
    # Execute the SQL
    $createSchemaSQL | psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ euk schema created successfully!" -ForegroundColor Green
        Write-Host "You can now start your Spring Boot application." -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to create euk schema. Please check your connection parameters." -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
