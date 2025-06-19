package utils;

import cliente.ClienteConexion;

import java.util.List;

public class GameDataCliente {

    private static String nombreJugador;
    private static ClienteConexion conexion;
    private static List<Integer> listaPersonajes;
    private static String nombreRival;
    private static modelo.Personaje personajeSecreto; // Variable personaje
    private static String personajeRival;

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

    // Lista de personajes Enteros del tablero
    public static void setListaPersonajes(List<Integer> lista) {
        listaPersonajes = lista;
    }

    public static List<Integer> getListaPersonajes() {
        return listaPersonajes;
    }

    // Nombre del rival
    public static String getNombreRival() {
        return nombreRival;
    }

    public static void setNombreRival(String nombre) {
        nombreRival = nombre;
    }

    // Personaje propio
    public static modelo.Personaje getPersonajeSecreto() {
        return personajeSecreto;
    }

    public static void setPersonajeSecreto(modelo.Personaje personaje) {
        personajeSecreto = personaje;
    }

    // Personaje rival
    public static void setPersonajeRival(String nombre) {
        personajeRival = nombre;
    }

    public static String getPersonajeRival() {
        return personajeRival;
    }


    public static void limpiar() {
        nombreJugador = null;
        conexion = null;
    }
}
