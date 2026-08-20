package logica;

import utils.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaControlador {

    public static List<String> obtenerPreguntas() {
        List<String> preguntas = new ArrayList<>();
        String sql = "SELECT texto FROM preguntas";

        try (Connection conn = DriverManager.getConnection(Config.getDbUrl(), Config.getDbUsuario(), Config.getDbPassword());
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                preguntas.add(rs.getString("texto"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return preguntas;
    }
}
