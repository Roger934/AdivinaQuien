package servidor;

import utils.Config;
import utils.GameDataServidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    public static void main(String[] args) {
        List<Socket> clientesConectados = new ArrayList<>();
        List<DataOutputStream> salidas = new ArrayList<>();

        try (ServerSocket serverSocket = new ServerSocket(Config.getPuertoServidor())) {
            System.out.println("[Servidor] Esperando jugadores en el puerto " + Config.getPuertoServidor());

            while (clientesConectados.size() < 2) {
                Socket cliente = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
                DataInputStream in = new DataInputStream(cliente.getInputStream());

                String nombre = in.readUTF();
                System.out.println("[Servidor] Jugador intentando conectarse: " + nombre);

                boolean aceptado = GameDataServidor.getInstance().agregarNombre(nombre);
                if (!aceptado) {
                    out.writeUTF("RECHAZADO");
                    cliente.close();
                } else {
                    out.writeUTF("ACEPTADO");
                    clientesConectados.add(cliente);
                    salidas.add(out);
                    System.out.println("[Servidor] Jugador aceptado: " + nombre);
                }
            }

            System.out.println("[Servidor] Se han conectado los dos jugadores. Iniciando tiempo...");
            GameDataServidor.getInstance().iniciarTiempo();

            for (DataOutputStream out : salidas) {
                out.writeUTF("OK");
            }

        } catch (IOException e) {
            System.err.println("[Servidor] Error: " + e.getMessage());
        }
    }
}
