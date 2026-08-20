package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static Properties props = new Properties();

    static {
        cargarConfiguracion();
    }

    public static synchronized void cargarConfiguracion() {
        props.clear();
        // Valores por defecto
        props.setProperty("servidor_ip", "127.0.0.1");
        props.setProperty("servidor_puerto", "5000");
        props.setProperty("db_url", "jdbc:mysql://localhost:3306/adivina_quien");
        props.setProperty("db_usuario", "root");
        props.setProperty("db_password", "");

        // Intentar leer desde config/config.properties o config.properties
        File archivo = new File("config/config.properties");
        if (!archivo.exists()) {
            archivo = new File("config.properties");
        }

        if (archivo.exists()) {
            try (InputStream input = new FileInputStream(archivo)) {
                props.load(input);
                System.out.println("[CONFIG] Configuracion cargada desde: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("[AVISO] Error al leer archivo de configuracion, usando valores por defecto: " + e.getMessage());
            }
        } else {
            System.out.println("[CONFIG] Archivo de configuracion no encontrado, usando valores por defecto.");
        }
    }

    public static String getIpServidor() {
        return props.getProperty("servidor_ip", "127.0.0.1").trim();
    }

    public static int getPuerto() {
        try {
            return Integer.parseInt(props.getProperty("servidor_puerto", "5000").trim());
        } catch (NumberFormatException e) {
            return 5000;
        }
    }

    public static String getDbUrl() {
        return props.getProperty("db_url", "jdbc:mysql://localhost:3306/adivina_quien").trim();
    }

    public static String getDbUsuario() {
        return props.getProperty("db_usuario", "root").trim();
    }

    public static String getDbPassword() {
        return props.getProperty("db_password", "").trim();
    }

    // Constantes compatibles hacia atrás
    public static String IP_SERVIDOR = getIpServidor();
    public static int PUERTO = getPuerto();
}
