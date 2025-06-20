package logica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class RegistroPartida {
    private String jugador1;
    private String jugador2;
    private String ganador;
    private String personajeGanador;
    private LocalDate fecha;
    private LocalTime duracion;

    public RegistroPartida() {
    }

    public RegistroPartida(String jugador1, String jugador2, String ganador, String personajeGanador, LocalDate fecha, LocalTime duracion) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.ganador = ganador;
        this.personajeGanador = personajeGanador;
        this.fecha = fecha;
        this.duracion = duracion;
    }

    // Getters y Setters
    public String getJugador1() { return jugador1; }
    public void setJugador1(String jugador1) { this.jugador1 = jugador1; }

    public String getJugador2() { return jugador2; }
    public void setJugador2(String jugador2) { this.jugador2 = jugador2; }

    public String getGanador() { return ganador; }
    public void setGanador(String ganador) { this.ganador = ganador; }

    public String getPersonajeGanador() { return personajeGanador; }
    public void setPersonajeGanador(String personajeGanador) { this.personajeGanador = personajeGanador; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getDuracion() { return duracion; }
    public void setDuracion(LocalTime duracion) { this.duracion = duracion; }

    // Método para guardar en MySQL con XAMPP
    public void guardarEnBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/adivina_quien";
        String usuario = "root";
        String contrasena = "";

        String sql = "INSERT INTO partidas (jugador1, jugador2, ganador, personaje_ganador, fecha, duracion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usuario, contrasena);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jugador1);
            stmt.setString(2, jugador2);
            stmt.setString(3, ganador);
            stmt.setString(4, personajeGanador);
            stmt.setDate(5, java.sql.Date.valueOf(fecha));
            stmt.setTime(6, java.sql.Time.valueOf(duracion));

            stmt.executeUpdate();
            System.out.println("✅ Partida guardada correctamente en la base de datos.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("❌ Error al guardar la partida en la base de datos.");
        }
    }
}
