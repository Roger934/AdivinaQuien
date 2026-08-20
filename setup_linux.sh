#!/usr/bin/env bash
# ========================================================
# INSTALADOR Y CONFIGURADOR AUTOMATICO PARA LINUX MINT
# ========================================================
set -e

echo "=== 1. Actualizando paquetes e instalando OpenJDK 17 y MariaDB ==="
sudo apt update
sudo apt install -y openjdk-17-jdk mariadb-server

echo "=== 2. Iniciando servicio de MariaDB ==="
sudo systemctl start mariadb
sudo systemctl enable mariadb

echo "=== 3. Creando Base de Datos e importando personajes ==="
sudo mysql -u root < database_setup.sql

echo "=== 4. Compilando el proyecto ==="
mkdir -p bin
javac -encoding UTF-8 -cp ".:XAMMP_JAR/mysql-connector-j-9.2.0/mysql-connector-j-9.2.0.jar" -d bin $(find src -name "*.java")

echo "=== 5. Permisos de ejecucion ==="
chmod +x ejecutar_servidor.sh ejecutar_cliente.sh setup_linux.sh

echo ""
echo "========================================================"
echo " [OK] Instalación y configuración completada con éxito."
echo " Para iniciar el Servidor: ./ejecutar_servidor.sh"
echo " Para iniciar el Juego:    ./ejecutar_cliente.sh"
echo "========================================================"