package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteConexion {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;

    public ClienteConexion(String ipServidor, int puerto) throws IOException {
        this.socket = new Socket(ipServidor, puerto);
        this.entrada = new DataInputStream(socket.getInputStream());
        this.salida = new DataOutputStream(socket.getOutputStream());
    }

    public void enviar(String mensaje) throws IOException {
        salida.writeUTF(mensaje);
    }

    public String recibir() throws IOException {
        return entrada.readUTF();
    }

    public DataInputStream getEntrada() {
        return entrada;
    }

    public int recibirInt() throws IOException {
        return entrada.readInt();
    }

    public void cerrar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            System.out.println("🔌 Conexión cerrada correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean estaConectado() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
