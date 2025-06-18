package base_datos;

import modelo.Personaje;

import java.sql.*;
import java.util.*;

public class TestBase {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/adivina_quien";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // tu contraseña si configuraste una

    public static void main(String[] args) {
        List<Personaje> todos = obtenerTodosLosPersonajes();
        System.out.println("✅ Todos los personajes:");
        for (Personaje p : todos) {
            System.out.println(p);
        }

        System.out.println("\n✅ Personajes seleccionados aleatoriamente:");
        List<Integer> idsAleatorios = generarIDsAleatorios(40, 24);
        List<Personaje> seleccionados = obtenerPorIDs(idsAleatorios);

        for (Personaje p : seleccionados) {
            System.out.println(p);
        }
    }

    // 1. Mostrar todos los personajes
    public static List<Personaje> obtenerTodosLosPersonajes() {
        List<Personaje> lista = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre, rutaImagen FROM personajes")) {

            while (rs.next()) {
                lista.add(new Personaje(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rutaImagen")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 2. Obtener personajes por ID
    public static List<Personaje> obtenerPorIDs(List<Integer> ids) {
        List<Personaje> lista = new ArrayList<>();

        if (ids == null || ids.isEmpty()) return lista;

        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String query = "SELECT id, nombre, rutaImagen FROM personajes WHERE id IN (" + placeholders + ")";

        try (Connection conn = DriverManager.getConnection(DB_URL, USUARIO, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            for (int i = 0; i < ids.size(); i++) {
                stmt.setInt(i + 1, ids.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Personaje(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("rutaImagen")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // 3. Generar lista aleatoria
    public static List<Integer> generarIDsAleatorios(int total, int cuantos) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i <= total; i++) ids.add(i);
        Collections.shuffle(ids);
        return new ArrayList<>(ids.subList(0, cuantos));
    }
}
