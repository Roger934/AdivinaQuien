package servidor;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ManejadorCliente extends Thread {
    private Socket cliente;

    public ManejadorCliente(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);

            // Hilo para leer mensajes del cliente
            new Thread(() -> {
                String mensaje;
                try {
                    while ((mensaje = lector.readLine()) != null) {
                        System.out.println("[Cliente] " + mensaje);
                    }
                } catch (IOException e) {
                    System.out.println("Cliente desconectado.");
                }
            }).start();

            // Leer mensajes desde la consola del servidor y enviarlos al cliente
            Scanner scanner = new Scanner(System.in);
            String entrada;
            while ((entrada = scanner.nextLine()) != null) {
                escritor.println(entrada);
            }

        } catch (IOException e) {
            System.err.println("Error al manejar cliente: " + e.getMessage());
        }
    }
}
