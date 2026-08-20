package logica;

import modelo.Personaje;
import utils.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableroControlador {

    /**
     * Recibe una lista de IDs y devuelve una lista de objetos Personaje desde la base de datos
     */
    public static List<Personaje> obtenerPersonajesDesdeBD(List<Integer> ids) {
        List<Personaje> personajes = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            System.out.println(" Lista de IDs vacía.");
            return personajes;
        }

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String query = "SELECT id, nombre, rutaImagen FROM personajes WHERE id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(Config.getDbUrl(), Config.getDbUsuario(), Config.getDbPassword());
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
