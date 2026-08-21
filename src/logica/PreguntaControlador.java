package logica;

import utils.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaControlador {

    private static final String[] PREGUNTAS_FALLBACK = {
        "Es de genero femenino?",
        "Es un humano?",
        "Es un animal o criatura?",
        "Pertenece al universo de Mario Bros?",
        "Pertenece al universo de Pokemon?",
        "Pertenece a The Legend of Zelda?",
        "Lleva sombrero, gorra o casco?",
        "Usa una espada o arma blanca?",
        "Tiene bigote?",
        "Lleva guantes?",
        "Tiene pelo o pelaje amarillo o dorado?",
        "Tiene pelaje o ropa de color verde?",
        "Tiene pelaje o ropa de color rojo?",
        "Tiene pelaje o ropa de color azul?",
        "Tiene pelaje o ropa de color rosa?",
        "Tiene alas o puede volar o levitar?",
        "Es un villano o antagonista?",
        "Lleva armadura metalica?",
        "Usa armas de fuego o proyectiles?",
        "Es de tamano pequeno o infantil?"
    };

    public static List<String> obtenerPreguntas() {
        List<String> preguntas = new ArrayList<>();
        String sql = "SELECT texto FROM preguntas";

        try (Connection conn = DriverManager.getConnection(Config.getDbUrl(), Config.getDbUsuario(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                preguntas.add(rs.getString("texto"));
            }

        } catch (Exception e) {
            System.err.println("[AVISO] Preguntas de base de datos no disponibles (" + e.getMessage() + "). Usando preguntas locales.");
        }

        if (preguntas.isEmpty()) {
            for (String p : PREGUNTAS_FALLBACK) {
                preguntas.add(p);
            }
        }

        return preguntas;
    }
}
