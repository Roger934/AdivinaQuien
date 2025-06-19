package logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PreguntaControlador {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/adivina_quien";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    public static List<String> obtenerPreguntas() {
        List<String> preguntas = new ArrayList<>();
        String sql = "SELECT texto FROM preguntas";

        try (Connection conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
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
