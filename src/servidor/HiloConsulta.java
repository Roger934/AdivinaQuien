package servidor;

import utils.Config;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HiloConsulta extends Thread {
    private Socket socket;
    private String tipoConsulta;

    public HiloConsulta(Socket socket) {
        this.socket = socket;
    }

    public HiloConsulta(Socket socket, String tipoConsulta) {
        this.socket = socket;
        this.tipoConsulta = tipoConsulta;
    }

    public void run() {
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            try (
                    Connection conn = DriverManager.getConnection(Config.getDbUrl(), Config.getDbUsuario(), Config.getDbPassword());
                    PreparedStatement stmt = prepararConsulta(conn);
                    ResultSet rs = stmt.executeQuery()
            ) {
                List<String> registros = new ArrayList<>();

                while (rs.next()) {
                    String fila = rs.getString("jugador1") + "," +
                            rs.getString("jugador2") + "," +
                            rs.getString("ganador") + "," +
                            rs.getString("personaje_ganador") + "," +
                            rs.getDate("fecha") + "," +
                            rs.getTime("duracion");
                    registros.add(fila);
                }

                String respuesta = String.join("|", registros);
                out.writeUTF(respuesta.isEmpty() ? "NO_DATOS" : respuesta);
                System.out.println(" Consulta enviada al cliente (" + registros.size() + " registros)");

            } catch (SQLException e) {
                e.printStackTrace();
                out.writeUTF("ERROR_BD");
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private PreparedStatement prepararConsulta(Connection conn) throws SQLException {
        String sql;

        if (tipoConsulta.startsWith("BUSCAR:")) {
            String nombre = tipoConsulta.substring("BUSCAR:".length()).trim();
            sql = "SELECT jugador1, jugador2, ganador, personaje_ganador, fecha, duracion FROM partidas WHERE jugador1 = ? OR jugador2 = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nombre);
            stmt.setString(2, nombre);
            return stmt;
        } else if (tipoConsulta.equals("ORDENAR")) {
            sql = "SELECT jugador1, jugador2, ganador, personaje_ganador, fecha, duracion FROM partidas ORDER BY duracion ASC";
        } else {
            sql = "SELECT jugador1, jugador2, ganador, personaje_ganador, fecha, duracion FROM partidas";
        }

        return conn.prepareStatement(sql);
    }
}
