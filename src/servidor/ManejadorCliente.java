package servidor;

import utils.GameDataServidor;

import java.io.*;
import java.net.Socket;

public class ManejadorCliente extends Thread {
    private final Socket cliente;

    public ManejadorCliente(Socket cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(cliente.getInputStream());
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

            String nombre = in.readUTF();
            System.out.println("[Servidor] Jugador intentando conectarse: " + nombre);

            boolean aceptado = GameDataServidor.getInstance().agregarNombre(nombre);

            if (!aceptado) {
                out.writeUTF("RECHAZADO");
                cliente.close();
                return;
            }

            out.writeUTF("ACEPTADO");
            System.out.println("[Servidor] Jugador aceptado: " + nombre);

            // Espera breve para que ambos jugadores estén listos
            while (GameDataServidor.getInstance().getNumeroJugadores() < 2) {
                Thread.sleep(200);
            }

            // Enviar nombre del oponente
            String nombreOponente = GameDataServidor.getInstance().getOponente(nombre);
            out.writeUTF(nombreOponente);

            // Aquí podrías enviar también personaje asignado, turno inicial, etc.

        } catch (IOException | InterruptedException e) {
            System.err.println("[Servidor] Error manejando cliente: " + e.getMessage());
        }
    }
}
