@echo off
echo ========================================
echo EUK Backend - Insert 3000 Test Records
echo ========================================
echo.

echo Clearing existing test data...
psql -h localhost -U postgres -d euk_db -c "DELETE FROM euk.ugrozeno_lice_t1 WHERE redni_broj LIKE 'RB%';"

echo.
echo Inserting 3000 test records...
echo This may take a few minutes...
echo.

psql -h localhost -U postgres -d euk_db -f insert_3000_simple.sql

echo.
echo ========================================
echo Test data insertion completed!
echo ========================================
echo.
echo You can now test the batch import functionality.
echo.
pause

