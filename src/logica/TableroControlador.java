package logica;

import modelo.Personaje;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableroControlador {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/adivina_quien";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "";

    /**
     * Recibe una lista de IDs y devuelve una lista de objetos Personaje desde la base de datos
     */
    public static List<Personaje> obtenerPersonajesDesdeBD(List<Integer> ids) {
        List<Personaje> personajes = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            System.out.println("⚠️ Lista de IDs vacía.");
            return personajes;
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String query = "SELECT id, nombre, rutaImagen FROM personajes WHERE id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Personaje p = new Personaje(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rutaImagen")
                );
                personajes.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personajes;
    }
}
