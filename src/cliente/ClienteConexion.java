package cliente;

import utils.Config;
import utils.GameDataCliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteConexion {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public boolean conectar(String nombreJugador) {
        try {
            socket = new Socket(Config.getIpServidor(), Config.getPuertoServidor());
            GameDataCliente.setSocket(socket);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(nombreJugador);
            String respuesta = in.readUTF();

            if (respuesta.equals("RECHAZADO")) {
                socket.close();
                return false;
            }

            GameDataCliente.setNombreJugador(nombreJugador);
            String nombreOponente = in.readUTF();
            GameDataCliente.setNombreOponente(nombreOponente);

            GameDataCliente.setTiempoInicioLocal(System.currentTimeMillis());

            return true;

        } catch (IOException e) {
            System.err.println("[Cliente] Error al conectar: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("[Cliente] Error al cerrar conexión: " + e.getMessage());
        }
    }
}
