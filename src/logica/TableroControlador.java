package logica;

import modelo.Personaje;
import utils.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableroControlador {

    private static final java.util.Map<Integer, Personaje> PERSONAJES_FALLBACK = new java.util.HashMap<>();
    static {
        String[][] data = {
            {"1", "Bowser", "assets/iconos/Bowser.png"},
            {"2", "Bowser Jr.", "assets/iconos/BowserJr.png"},
            {"3", "Captain Falcon", "assets/iconos/CaptainFalcon.png"},
            {"4", "Charizard", "assets/iconos/Charizard.png"},
            {"5", "Daisy", "assets/iconos/Daisy.png"},
            {"6", "Diddy Kong", "assets/iconos/DiddyKong.png"},
            {"7", "Donkey Kong", "assets/iconos/DonkeyKong.png"},
            {"8", "Dr. Mario", "assets/iconos/DrMario.png"},
            {"9", "Fox", "assets/iconos/Fox.png"},
            {"10", "Ganondorf", "assets/iconos/Ganondorf.png"},
            {"11", "Ice Climbers", "assets/iconos/IceClimbers.png"},
            {"12", "Isabelle", "assets/iconos/Isabelle.png"},
            {"13", "Jigglypuff", "assets/iconos/Jigglypuff.png"},
            {"14", "Ken", "assets/iconos/Ken.png"},
            {"15", "King Dedede", "assets/iconos/KingDedede.png"},
            {"16", "King K. Rool", "assets/iconos/KingKRool.png"},
            {"17", "Kirby", "assets/iconos/Kirby.png"},
            {"18", "Link", "assets/iconos/Link.png"},
            {"19", "Luigi", "assets/iconos/Luigi.png"},
            {"20", "Mario", "assets/iconos/Mario.png"},
            {"21", "Mega Man", "assets/iconos/MegaMan.png"},
            {"22", "Meta Knight", "assets/iconos/MetaKnight.png"},
            {"23", "Ness", "assets/iconos/Ness.png"},
            {"24", "Pac-Man", "assets/iconos/Pac-Man.png"},
            {"25", "Peach", "assets/iconos/Peach.png"},
            {"26", "Pikachu", "assets/iconos/Pikachu.png"},
            {"27", "Piranha Plant", "assets/iconos/PiranhaPlant.png"},
            {"28", "Pit", "assets/iconos/PitIcon.png"},
            {"29", "Ryu", "assets/iconos/Ryu.png"},
            {"30", "Samus", "assets/iconos/Samus.png"},
            {"31", "Snake", "assets/iconos/Snake.png"},
            {"32", "Sonic", "assets/iconos/Sonic.png"},
            {"33", "Sora", "assets/iconos/Sora.png"},
            {"34", "Steve", "assets/iconos/Steve.png"},
            {"35", "Terry", "assets/iconos/Terry.png"},
            {"36", "Toon Link", "assets/iconos/ToonLink.png"},
            {"37", "Wario", "assets/iconos/Wario.png"},
            {"38", "Yoshi", "assets/iconos/Yoshi.png"},
            {"39", "Young Link", "assets/iconos/YoungLink.png"},
            {"40", "Zelda", "assets/iconos/Zelda.png"}
        };
        for (String[] d : data) {
            int id = Integer.parseInt(d[0]);
            PERSONAJES_FALLBACK.put(id, new Personaje(id, d[1], d[2]));
        }
    }

    /**
     * Recibe una lista de IDs y devuelve una lista de objetos Personaje desde la base de datos (con respaldo local)
     */
    public static List<Personaje> obtenerPersonajesDesdeBD(List<Integer> ids) {
        List<Personaje> personajes = new ArrayList<>();

        if (ids == null || ids.isEmpty()) {
            System.out.println("[INFO] Lista de IDs vacia.");
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

        } catch (Exception e) {
            System.err.println("[AVISO] Base de datos inaccesible (" + e.getMessage() + "). Cargando personajes desde memoria local.");
        }

        // Si la base de datos no devolvió registros o falló la conexión, usar respaldo
        if (personajes.isEmpty()) {
            for (Integer id : ids) {
                if (PERSONAJES_FALLBACK.containsKey(id)) {
                    personajes.add(PERSONAJES_FALLBACK.get(id));
                }
            }
        }

        return personajes;
    }
}
