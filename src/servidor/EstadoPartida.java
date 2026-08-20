package servidor;

import java.util.HashSet;
import java.util.Set;

public class EstadoPartida {

    private static final Set<String> nombresJugadores = new HashSet<>();

    public static synchronized boolean registrarNombre(String nombre) {
        if (nombresJugadores.contains(nombre)) {
            return false;
        }
        if (nombresJugadores.size() >= 2) {
            return false;
        }
        nombresJugadores.add(nombre);
        return true;
    }

    public static synchronized boolean partidaCompleta() {
        return nombresJugadores.size() >= 2;
    }

    public static synchronized void reiniciar() {
        nombresJugadores.clear();
    }

    public static synchronized Set<String> getNombres() {
        return new HashSet<>(nombresJugadores);
    }
}
