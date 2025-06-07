// servidor/Servidor.java
package servidor;

import utils.Config;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor implements Runnable {

    @Override
    public void run() {
        int puerto = Config.getPuertoServidor();

        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto + "...");

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Nuevo cliente conectado desde: " + cliente.getInetAddress().getHostAddress());

                ManejadorCliente manejador = new ManejadorCliente(cliente);
                manejador.start();
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public static void iniciar() {
        Thread hiloServidor = new Thread(new Servidor());
        hiloServidor.start();
    }
}
