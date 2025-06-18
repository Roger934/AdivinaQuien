package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {
    private static final String IP_SERVIDOR = "127.0.0.1"; // Cambia por IP real en otra máquina
    private static final int PUERTO = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP_SERVIDOR, PUERTO);
             DataInputStream entrada = new DataInputStream(socket.getInputStream());
             DataOutputStream salida = new DataOutputStream(socket.getOutputStream())) {

            System.out.println("Conectado al servidor.");

            // Recibir bienvenida
            String mensajeServidor = entrada.readUTF();
            System.out.println("Servidor dice: " + mensajeServidor);

            // Enviar mensaje de prueba
            salida.writeUTF("¡Hola servidor, soy el cliente!");

            // Esperar respuesta (eco)
            String respuesta = entrada.readUTF();
            System.out.println("Servidor respondió: " + respuesta);

        } catch (IOException e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
