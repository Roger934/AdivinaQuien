package cliente;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClienteTest1 {
    public static void main(String[] args) {
        ejecutarCliente("JugadorPrueba1");
    }

    public static void ejecutarCliente(String nombre) {
        String servidor = "127.0.0.1";
        int puerto = 5000;

        try (Socket socket = new Socket(servidor, puerto);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            out.writeUTF(nombre);
            String respuesta = in.readUTF();

            if (!"OK".equals(respuesta)) {
                System.out.println("⛔ Rechazado por el servidor: " + respuesta);
                return;
            }

            while (true) {
                String msg = in.readUTF();
                if (msg.startsWith("DATOS:")) {
                    String contenido = msg.substring(6).replaceAll("[\\[\\]\\s]", "");
                    String[] partes = contenido.split(",");
                    System.out.println("🎯 " + nombre + " recibió:");
                    for (String num : partes) {
                        System.out.print(num + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("📩 " + nombre + ": " + msg);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
