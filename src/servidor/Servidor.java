package servidor;

import utils.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        int puerto = Config.getPuertoServidor();

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto + "...");

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Nuevo cliente conectado desde: " + cliente.getInetAddress().getHostAddress());

                // Manejar el cliente en un hilo separado
                ManejadorCliente manejador = new ManejadorCliente(cliente);
                manejador.start();
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}
