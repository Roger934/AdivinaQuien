# 🎮 Documentación del Proyecto: Adivina Quién (Cliente + Servidor Unificado)

Proyecto desarrollado para la materia **Programación III** (ISC 4A) en la **Universidad Autónoma de Aguascalientes (UAA)**.

---

## 📌 1. Resumen Ejecutivo

**Adivina Quién** es una solución completa (Cliente gráfico + Servidor centralizado) desarrollada en **Java SE (Swing)** y **MySQL/MariaDB**, que permite jugar partidas multijugador en red local (LAN) o mediante zona Wi-Fi móvil (Hotspot) sin necesidad de abrir ningún entorno de desarrollo (IDE como IntelliJ, NetBeans o VS Code).

El sistema incluye:
- **Juego Cliente:** Interfaz gráfica completa en Swing con animaciones, efectos de sonido, música y tablero interactivo de 24 personajes.
- **Servidor Multihilo:** Servidor en segundo plano que coordina emparejamiento, turnos, validación de personajes y almacenamiento de estadísticas.
- **Configuración de IP Externa:** Archivo `config.properties` editable en el Bloc de Notas para configurar la IP del servidor en cualquier red.
- **Scripts de Un Clic:** Scripts `.bat` (Windows) y `.sh` (Linux Mint) para compilar e iniciar la aplicación inmediatamente.

---

## 📁 2. Estructura Unificada del Proyecto

```
AdivinaQuien/
├── config.properties        # ⚙️ Archivo principal para cambiar IP del Servidor y BD
├── config/
│   └── config.properties    # Copia de seguridad del archivo de configuración
├── database_setup.sql       # 🗄️ Script SQL para crear la base de datos y 40 personajes
│
├── compilar.bat             # 🔨 Script Windows para compilar todo el proyecto
├── ejecutar_servidor.bat    # 🚀 Script Windows para iniciar el Servidor
├── ejecutar_cliente.bat     # 🎮 Script Windows para iniciar el Juego (Cliente)
│
├── setup_linux.sh           # 🐧 Script Linux Mint: Instala Java, MariaDB, BD y compila
├── ejecutar_servidor.sh     # 🚀 Script Linux Mint para iniciar el Servidor
├── ejecutar_cliente.sh      # 🎮 Script Linux Mint para iniciar el Juego (Cliente)
│
├── assets/                  # Recursos multimedia del juego
│   ├── fondos/              # Imágenes de fondo (menú, portada, resultados)
│   ├── fotos/               # Fotos de desarrolladores y logo institucional UAA
│   ├── gifs/                # Animación GIF para la sala de espera
│   ├── iconos/              # 40 avatares de personajes de Smash Bros + UI
│   ├── musica/              # Música de fondo (Undertale.wav)
│   └── sonidos/             # Efectos de audio (pregunta, respuesta, vida)
│
├── src/                     # Código fuente Java unificado
│   ├── cliente/             # Socket cliente y conexión TCP
│   │   ├── Cliente.java
│   │   └── ClienteConexion.java
│   ├── interfaz/            # Pantallas de interfaz gráfica (Swing)
│   │   ├── Creditos.java
│   │   ├── EsperandoJugador.java
│   │   ├── Instrucciones.java
│   │   ├── MenuPrincipal.java
│   │   ├── PantallaPresentacion.java
│   │   ├── RegistroJugador.java
│   │   ├── Tablero.java
│   │   ├── VentanaGanador.java
│   │   ├── VentanaJugar.java
│   │   ├── VentanaPerdedor.java
│   │   ├── VentanaPrincipal.java
│   │   └── VerRegistros.java
│   ├── logica/              # Controladores de negocio y consultas cliente
│   │   ├── PreguntaControlador.java
│   │   ├── RegistroPartida.java
│   │   └── TableroControlador.java
│   ├── modelo/              # Clases de entidad de datos
│   │   ├── Personaje.java
│   │   └── TestBase.java
│   ├── servidor/            # Backend del Servidor TCP Multihilo
│   │   ├── Servidor.java
│   │   ├── ManejadorCliente.java
│   │   ├── EstadoPartida.java
│   │   ├── HiloConsulta.java
│   │   └── RegistroPartida.java
│   └── utils/               # Gestor dinámico de configuración y utilidades
│       ├── Animaciones.java
│       ├── Config.java
│       └── GameDataCliente.java
│
└── XAMMP_JAR/               # Driver MySQL JDBC (mysql-connector-j-9.2.0.jar)
```

---

## ⚙️ 3. Configuración de IP para Red Local / Módem

El archivo [`config.properties`](file:///c:/Users/roger/AdivinaQuien/config.properties) permite cambiar la IP sin tocar código ni recompilar:

```properties
# =======================================================
# CONFIGURACION DE RED Y BASE DE DATOS - ADIVINA QUIEN
# =======================================================

# Direccion IP del Servidor Socket
# - Para jugar en la misma computadora: 127.0.0.1
# - Para jugar en red local / modem / hotspot: Colocar la IP de la laptop Servidor (ej. 192.168.1.15)
servidor_ip=127.0.0.1

# Puerto de comunicacion Socket (por defecto 5000)
servidor_puerto=5000

# Parametros de Base de Datos MySQL / MariaDB
db_url=jdbc:mysql://localhost:3306/adivina_quien
db_usuario=root
db_password=
```

> **💡 Consejo para Redes y Presentaciones:**  
> Si estás en una red escolar o pública grande (con miles de usuarios), conecta ambas computadoras a un **módem propio** o activa la **Zona Wi-Fi Móvil (Hotspot)** de tu celular. Averigua la IP del servidor con `ipconfig` (Windows) o `hostname -I` (Linux), y escríbela en el archivo `config.properties` de la máquina cliente.

---

## 🚀 4. Guía Rápida de Ejecución

### 🪟 En Windows:

1. **Servidor (Máquina 1):**
   - Asegúrate de tener XAMPP / MySQL encendido e importa `database_setup.sql`.
   - Haz doble clic en [`ejecutar_servidor.bat`](file:///c:/Users/roger/AdivinaQuien/ejecutar_servidor.bat).
2. **Jugadores (Máquina 1 y Máquina 2):**
   - En la máquina 2, abre [`config.properties`](file:///c:/Users/roger/AdivinaQuien/config.properties) y pon la IP de la Máquina 1.
   - Haz doble clic en [`ejecutar_cliente.bat`](file:///c:/Users/roger/AdivinaQuien/ejecutar_cliente.bat).

---

### 🐧 En Linux Mint:

1. **Instalación Inicial Automatizada:**
   Abre una terminal en la carpeta del proyecto y ejecuta:
   ```bash
   chmod +x setup_linux.sh
   ./setup_linux.sh
   ```
   *(Este script instala Java 17, MariaDB, crea la base de datos con los 40 personajes y compila todo).*

2. **Iniciar Servidor:**
   ```bash
   ./ejecutar_servidor.sh
   ```

3. **Iniciar Juego (Cliente):**
   ```bash
   ./ejecutar_cliente.sh
   ```

---

## 🗄️ 5. Base de Datos (`database_setup.sql`)

El script [`database_setup.sql`](file:///c:/Users/roger/AdivinaQuien/database_setup.sql) contiene la creación automática de:
1. `personajes`: Catálogo de 40 personajes de Smash Bros (`id`, `nombre`, `rutaImagen`).
2. `preguntas`: 20 preguntas predefinidas para la interfaz del juego (`id`, `texto`).
3. `partidas`: Historial de encuentros (`jugador1`, `jugador2`, `ganador`, `personaje_ganador`, `fecha`, `duracion`).

---

## 👥 Autores

- **Diego Alejandro Ramos Vázquez**
- **Emmanuel López de Jesús García**
- **Rogelio Uriel Gutiérrez Jiménez**

**Materia:** Programación III (ISC 4A)  
**Institución:** Universidad Autónoma de Aguascalientes (UAA)  
**Fecha:** Junio 2025
