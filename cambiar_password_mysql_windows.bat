@echo off
chcp 65001 >nul
cls
echo ========================================================
echo   RESETEAR / CAMBIAR CONTRASEÑA DE MYSQL (WINDOWS)
echo ========================================================
echo.

:: 1. Comprobar si tiene permisos de Administrador
net session >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Solicitando permisos de Administrador para gestionar el servicio MySQL...
    powershell -Command "Start-Process cmd.exe -ArgumentList '/c \"\"%~f0\"\"' -Verb RunAs"
    exit /b
)

echo Escribe la NUEVA contrasena que deseas asignarle al usuario root de MySQL:
echo (Ejemplos: root, 1234, o presiona ENTER para dejarla vacia)
set /p "NUEVA_PASS="

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\resetear_pass.ps1" -NuevaPassword "%NUEVA_PASS%"

echo.
pause
