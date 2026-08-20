# 🎮 Adivina Quién – Proyecto Final (Programación III)

Juego multijugador en red local tipo **Adivina Quién** desarrollado en **Java SE (Swing)** con sincronización por **Sockets TCP** y persistencia en **MySQL / MariaDB**, ambientado con personajes de **Super Smash Bros / Nintendo**.

---

## 🚀 Inicio Rápido (Sin necesidad de IDE)

### 1. Configuración de Red
Edita el archivo [`config.properties`](config.properties) con el Bloc de Notas:
- Para jugar en la misma máquina: `servidor_ip=127.0.0.1`
- Para jugar en red local / módem: Coloca la IP del servidor (ej. `servidor_ip=192.168.1.15`).

### 2. Ejecutar en Windows
1. **Iniciar Servidor:** Doble clic en [`ejecutar_servidor.bat`](ejecutar_servidor.bat).
2. **Iniciar Juego (Cliente):** Doble clic en [`ejecutar_cliente.bat`](ejecutar_cliente.bat).

### 3. Ejecutar en Linux Mint
1. **Instalación y configuración automática:**
   ```bash
   chmod +x setup_linux.sh
   ./setup_linux.sh
   ```
2. **Iniciar Servidor:** `./ejecutar_servidor.sh`
3. **Iniciar Juego:** `./ejecutar_cliente.sh`

---

## 🗄️ Base de Datos
El script [`database_setup.sql`](database_setup.sql) contiene la creación de las tablas y la inserción de los 40 personajes de Smash Bros y preguntas temáticas.

---

## 📖 Documentación Completa
Para consultar la arquitectura del software, especificación del protocolo de sockets y diagramas de flujo, consulta:
👉 **[DOCUMENTACION.md](DOCUMENTACION.md)**

---

## 👥 Autores

- Diego Alejandro Ramos Vázquez
- Emmanuel López de Jesús García
- Rogelio Uriel Gutiérrez Jiménez

---

© ISC 4A – Universidad Autónoma de Aguascalientes – 2025
