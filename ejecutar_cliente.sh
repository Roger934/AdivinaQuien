#!/usr/bin/env bash
# ========================================================
# INICIAR CLIENTE (JUEGO) - ADIVINA QUIEN (LINUX)
# ========================================================
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

if [ ! -f "bin/interfaz/VentanaPrincipal.class" ]; then
    echo "Compilando proyecto primero..."
    mkdir -p bin
    javac -encoding UTF-8 -cp ".:XAMMP_JAR/mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar" -d bin $(find src -name "*.java")
fi

echo "Iniciando Juego Adivina Quién en Linux..."
java -cp "bin:XAMMP_JAR/mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar" interfaz.VentanaPrincipal