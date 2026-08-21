@echo off
chcp 65001 >nul
cls
echo ========================================================
echo   CONFIGURADOR AUTOMATICO DE BASE DE DATOS (WINDOWS)
echo ========================================================
echo.

set "MYSQL_EXE="
if exist "%ProgramFiles%\MySQL\MySQL Server 8.0\bin\mysql.exe" (
    set "MYSQL_EXE=%ProgramFiles%\MySQL\MySQL Server 8.0\bin\mysql.exe"
) else if exist "%ProgramFiles%\MySQL\MySQL Server 8.1\bin\mysql.exe" (
    set "MYSQL_EXE=%ProgramFiles%\MySQL\MySQL Server 8.1\bin\mysql.exe"
) else if exist "%ProgramFiles%\MySQL\MySQL Server 8.4\bin\mysql.exe" (
    set "MYSQL_EXE=%ProgramFiles%\MySQL\MySQL Server 8.4\bin\mysql.exe"
) else if exist "C:\xampp\mysql\bin\mysql.exe" (
    set "MYSQL_EXE=C:\xampp\mysql\bin\mysql.exe"
) else (
    where mysql >nul 2>nul
    if %errorlevel% equ 0 (
        set "MYSQL_EXE=mysql"
    )
)

if not defined MYSQL_EXE (
    echo [ERROR] No se encontro MySQL ni XAMPP en rutas estandar.
    echo Asegurate de tener instalado MySQL Server o XAMPP.
    pause
    exit /b 1
)

echo [OK] Cliente MySQL detectado: "%MYSQL_EXE%"
echo.
echo Ingresa la contrasena actual de tu usuario root de MySQL
echo (Si usas XAMPP o no tiene contrasena, solo presiona ENTER):
set /p "DB_PASS="

echo.
echo [1/3] Probando conexion e importando base de datos...
if "%DB_PASS%"=="" (
    "%MYSQL_EXE%" -u root < "%~dp0database_setup.sql"
) else (
    "%MYSQL_EXE%" -u root -p%DB_PASS% < "%~dp0database_setup.sql"
)

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] No se pudo conectar a MySQL. Verifica que la contrasena sea correcta o que el servicio este encendido.
    pause
    exit /b 1
)

echo [OK] Base de datos y 40 personajes importados con exito.
echo.
echo [2/3] Otorgando permisos completos...
if "%DB_PASS%"=="" (
    "%MYSQL_EXE%" -u root -e "GRANT ALL PRIVILEGES ON adivina_quien.* TO 'root'@'localhost'; FLUSH PRIVILEGES;"
) else (
    "%MYSQL_EXE%" -u root -p%DB_PASS% -e "GRANT ALL PRIVILEGES ON adivina_quien.* TO 'root'@'localhost'; FLUSH PRIVILEGES;"
)

echo.
echo [3/3] Actualizando config.properties automaticamente...

(
echo # =======================================================
echo # CONFIGURACION DE RED Y BASE DE DATOS - ADIVINA QUIEN
echo # =======================================================
echo.
echo # Direccion IP del Servidor Socket
echo servidor_ip=127.0.0.1
echo.
echo # Puerto de comunicacion Socket
echo servidor_puerto=5000
echo.
echo # Parametros de Base de Datos MySQL / MariaDB
echo db_url=jdbc:mysql://localhost:3306/adivina_quien
echo db_usuario=root
echo db_password=%DB_PASS%
) > "%~dp0config.properties"

if exist "%~dp0config" (
    copy /y "%~dp0config.properties" "%~dp0config\config.properties" >nul
)

echo.
echo ========================================================
echo   [OK] TODO LISTO Y CONFIGURADO CON EXITO!
echo   Ya puedes iniciar el juego con ejecutar_cliente.bat
echo ========================================================
echo.
pause
