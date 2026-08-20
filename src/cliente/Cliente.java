package cliente;

import utils.Config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        try (Socket socket = new Socket(Config.getIpServidor(), Config.getPuerto());
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
