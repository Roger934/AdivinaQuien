#!/usr/bin/env bash
# ========================================================
# INICIAR SERVIDOR - ADIVINA QUIEN (LINUX)
# ========================================================
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
cd "$DIR"

if [ ! -f "bin/servidor/Servidor.class" ]; then
    echo "Compilando proyecto primero..."
    mkdir -p bin
    javac -encoding UTF-8 -cp ".:XAMMP_JAR/mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar" -d bin $(find src -name "*.java")
fi

echo "Iniciando Servidor Adivina Quién en Linux..."
java -cp "bin:XAMMP_JAR/mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar" servidor.Servidor