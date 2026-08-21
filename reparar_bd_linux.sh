#!/usr/bin/env bash
# ========================================================
# REPARAR BASE DE DATOS Y PERMISOS EN LINUX MINT
# ========================================================
set -e

echo "=== 1. Iniciando servicio de MariaDB / MySQL ==="
sudo systemctl start mariadb 2>/dev/null || sudo service mariadb start 2>/dev/null || sudo service mysql start 2>/dev/null || true

echo "=== 2. Configurando usuario root con contrasena 'root' y permisos TCP ==="
sudo mysql -e "
ALTER USER 'root'@'localhost' IDENTIFIED BY 'root';
CREATE USER IF NOT EXISTS 'root'@'127.0.0.1' IDENTIFIED BY 'root';
ALTER USER 'root'@'127.0.0.1' IDENTIFIED BY 'root';
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'root';
ALTER USER 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'localhost' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'127.0.0.1' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
" 2>/dev/null || true

echo "=== 3. Creando e importando Base de Datos 'adivina_quien' ==="
if sudo mysql -u root -proot -e "SHOW DATABASES;" >/dev/null 2>&1; then
    sudo mysql -u root -proot < database_setup.sql
else
    sudo mysql -u root < database_setup.sql
fi

echo ""
echo "========================================================"
echo " [OK] Base de datos y contrasena 'root' configuradas."
echo " Ya puedes ejecutar ./ejecutar_cliente.sh"
echo "========================================================"