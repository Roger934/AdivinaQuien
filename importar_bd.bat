@echo off
chcp 65001 >nul
echo ========================================================
echo   IMPORTADOR DE BASE DE DATOS - ADIVINA QUIEN
echo ========================================================

set "MYSQL_EXE="
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set "MYSQL_EXE=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
) else if exist "C:\xampp\mysql\bin\mysql.exe" (
    set "MYSQL_EXE=C:\xampp\mysql\bin\mysql.exe"
)

if not defined MYSQL_EXE (
    echo [!] No se encontró mysql.exe instalado en rutas estándar.
    echo Puedes importar 'database_setup.sql' manualmente desde MySQL Workbench o phpMyAdmin.
    pause
    exit /b 1
)

echo [OK] Cliente MySQL detectado: %MYSQL_EXE%
echo.
echo Introduce la contraseña de tu usuario 'root' de MySQL (presiona ENTER si no tiene contraseña):
set /p "DB_PASS="

if "%DB_PASS%"=="" (
    "%MYSQL_EXE%" -u root < "%~dp0database_setup.sql"
) else (
    "%MYSQL_EXE%" -u root -p%DB_PASS% < "%~dp0database_setup.sql"
)

if %errorlevel% equ 0 (
    echo.
    echo [OK] ¡Base de datos 'adivina_quien' importada con éxito con los 40 personajes!
) else (
    echo.
    echo [ERROR] No se pudo importar. Verifica tu contraseña o usa MySQL Workbench.
)
pause