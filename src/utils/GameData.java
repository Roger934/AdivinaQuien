package utils;

public class GameData {
    private static String nombreJugador;

    public static void setNombreJugador(String nombre) {
        nombreJugador = nombre;
    }

    public static String getNombreJugador() {
        return nombreJugador;
    }
}
