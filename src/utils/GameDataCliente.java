package utils;

import cliente.ClienteConexion;

public class GameDataCliente {

    private static String nombreJugador;
    private static ClienteConexion conexion;

    public static String getNombreJugador() {
        return nombreJugador;
    }

    public static void setNombreJugador(String nombre) {
        GameDataCliente.nombreJugador = nombre;
    }

    public static ClienteConexion getConexion() {
        return conexion;
    }

    public static void setConexion(ClienteConexion conn) {
        GameDataCliente.conexion = conn;
    }

    public static void limpiar() {
        nombreJugador = null;
        conexion = null;
    }
}
