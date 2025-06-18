package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Personajes {

    private static final String DB_URL = "jdbc:sqlite:assets/db/adivina_quien.db";

    public static List<Personaje> obtenerTodos() {
        List<Personaje> lista = new ArrayList<>();

        String sql = "SELECT nombre, rutaImagen FROM personajes";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String ruta = rs.getString("rutaImagen");
                lista.add(new Personaje(nombre, ruta));
            }

        } catch (SQLException e) {
            System.err.println("[Personajes] Error al cargar personajes: " + e.getMessage());
        }

        return lista;
    }

    public static List<Personaje> obtenerPorIds(int[] ids) {
        List<Personaje> lista = new ArrayList<>();
        if (ids == null || ids.length == 0) return lista;

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            placeholders.append("?");
            if (i < ids.length - 1) placeholders.append(",");
        }

        String sql = "SELECT id, nombre, rutaImagen FROM personajes WHERE id IN (" + placeholders + ")";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.length; i++) {
                ps.setInt(i + 1, ids[i]);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Personaje(rs.getString("nombre"), rs.getString("rutaImagen")));
            }

        } catch (SQLException e) {
            System.err.println("[Personajes] Error al cargar por IDs: " + e.getMessage());
        }

        return lista;
    }

}
