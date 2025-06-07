package cliente;

import utils.Config;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String ip = Config.getIpServidor();
        int puerto = Config.getPuertoServidor();

        try (Socket socket = new Socket(ip, puerto)) {
            System.out.println("Conectado al servidor en " + ip + ":" + puerto);

            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter escritor = new PrintWriter(socket.getOutputStream(), true);

            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                String mensaje;
                try {
                    while ((mensaje = lector.readLine()) != null) {
                        System.out.println("[Servidor] " + mensaje);
                    }
                } catch (IOException e) {
                    System.out.println("Servidor desconectado.");
                }
            }).start();

            // Enviar mensajes desde consola
            Scanner scanner = new Scanner(System.in);
            String entrada;
            while ((entrada = scanner.nextLine()) != null) {
                escritor.println(entrada);
            }

        } catch (IOException e) {
            System.err.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}
