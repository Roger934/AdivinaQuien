package utils;

import java.net.Socket;

public class GameDataCliente {
    private static String nombreJugador;
    private static String nombreOponente;

    private static String personajePropio;
    private static String personajeOponente;

    private static boolean esMiTurno;
    private static long tiempoInicioLocal;

    private static Socket socket;

    public static String getNombreJugador() {
        return nombreJugador;
    }

    public static void setNombreJugador(String nombreJugador) {
        GameDataCliente.nombreJugador = nombreJugador;
    }

    public static String getNombreOponente() {
        return nombreOponente;
    }

    public static void setNombreOponente(String nombreOponente) {
        GameDataCliente.nombreOponente = nombreOponente;
    }

    public static String getPersonajePropio() {
        return personajePropio;
    }

    public static void setPersonajePropio(String personajePropio) {
        GameDataCliente.personajePropio = personajePropio;
    }

    public static String getPersonajeOponente() {
        return personajeOponente;
    }

    public static void setPersonajeOponente(String personajeOponente) {
        GameDataCliente.personajeOponente = personajeOponente;
    }

    public static boolean isEsMiTurno() {
        return esMiTurno;
    }

    public static void setEsMiTurno(boolean esMiTurno) {
        GameDataCliente.esMiTurno = esMiTurno;
    }

    public static long getTiempoInicioLocal() {
        return tiempoInicioLocal;
    }

    public static void setTiempoInicioLocal(long tiempoInicioLocal) {
        GameDataCliente.tiempoInicioLocal = tiempoInicioLocal;
    }

    public static int getDuracionTranscurrida() {
        return (int) ((System.currentTimeMillis() - tiempoInicioLocal) / 1000);
    }

    public static void reiniciar() {
        nombreJugador = null;
        nombreOponente = null;
        personajePropio = null;
        personajeOponente = null;
        esMiTurno = false;
        tiempoInicioLocal = 0;
        socket = null;
    }

    public static Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket s) {
        socket = s;
    }

    public static void cerrarSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception e) {
            System.err.println("[Cliente] Error al cerrar socket: " + e.getMessage());
        } finally {
            socket = null;
        }
    }
}
