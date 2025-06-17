package utils;

import java.util.HashSet;
import java.util.Set;

public class GameDataServidor {
    private static GameDataServidor instance = null;

    private final Set<String> nombresJugadores = new HashSet<>();
    private String jugador1 = null;
    private String jugador2 = null;

    private String personajeJugador1;
    private String personajeJugador2;

    private long tiempoInicio;
    private long tiempoFin;

    private GameDataServidor() {}

    public static synchronized GameDataServidor getInstance() {
        if (instance == null) {
            instance = new GameDataServidor();
        }
        return instance;
    }

    public synchronized boolean agregarNombre(String nombre) {
        if (nombresJugadores.contains(nombre)) {
            return false;
        } else {
            nombresJugadores.add(nombre);
            if (jugador1 == null) {
                jugador1 = nombre;
            } else {
                jugador2 = nombre;
            }
            return true;
        }
    }

    public synchronized int getNumeroJugadores() {
        return nombresJugadores.size();
    }


    public synchronized void setPersonaje(String jugador, String personaje) {
        if (jugador.equals(jugador1)) {
            personajeJugador1 = personaje;
        } else if (jugador.equals(jugador2)) {
            personajeJugador2 = personaje;
        }
    }

    public synchronized String getOponente(String jugador) {
        if (jugador.equals(jugador1)) return jugador2;
        if (jugador.equals(jugador2)) return jugador1;
        return null;
    }

    public synchronized String getPersonajeOponente(String jugador) {
        if (jugador.equals(jugador1)) return personajeJugador2;
        if (jugador.equals(jugador2)) return personajeJugador1;
        return null;
    }

    public synchronized boolean adivinoCorrectamente(String jugador, String intento) {
        String personajeOponente = getPersonajeOponente(jugador);
        return personajeOponente != null && personajeOponente.equalsIgnoreCase(intento);
    }

    public synchronized void iniciarTiempo() {
        tiempoInicio = System.currentTimeMillis();
    }

    public synchronized void finalizarTiempo() {
        tiempoFin = System.currentTimeMillis();
    }

    public synchronized int getDuracionEnSegundos() {
        if (tiempoInicio == 0 || tiempoFin == 0) return 0;
        return (int) ((tiempoFin - tiempoInicio) / 1000);
    }

    public synchronized void reiniciar() {
        nombresJugadores.clear();
        jugador1 = null;
        jugador2 = null;
        personajeJugador1 = null;
        personajeJugador2 = null;
        tiempoInicio = 0;
        tiempoFin = 0;
    }
}
