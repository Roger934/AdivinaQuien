@echo off
setlocal
echo ========================================================
echo   INICIANDO SERVIDOR - ADIVINA QUIEN
echo ========================================================

set "JAVA_CMD=java"
if exist "%~dp0jdk\bin\java.exe" (
    set "JAVA_CMD=%~dp0jdk\bin\java.exe"
)

if not exist "%~dp0bin\servidor\Servidor.class" (
    echo El proyecto no esta compilado. Compilando primero...
    call "%~dp0compilar.bat"
)

"%JAVA_CMD%" -cp "%~dp0bin;%~dp0XAMMP_JAR\mysql-connector-j-9.2.0\mysql-connector-j-9.2.0.jar" servidor.Servidor
pause