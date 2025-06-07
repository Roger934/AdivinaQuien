package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String CONFIG_PATH = "config/config.properties";
    private static Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            props.load(fis);
        } catch (IOException e) {
            System.err.println("No se pudo cargar el archivo de configuración.");
            e.printStackTrace();
        }
    }

    public static String getIpServidor() {
        return props.getProperty("servidor_ip");
    }

    public static int getPuertoServidor() {
        return Integer.parseInt(props.getProperty("servidor_puerto"));
    }
}
