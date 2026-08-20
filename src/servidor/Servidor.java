package servidor;

import utils.Config;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Servidor {

    private static ArrayList<ManejadorCliente> clientes = new ArrayList<>();
    private static int turnoActual = 1;

    public static void main(String[] args) {
        int puerto = Config.getPuerto();
        System.out.println("Servidor iniciado en puerto " + puerto + ". Esperando jugadores...");

        try (ServerSocket servidorSocket = new ServerSocket(puerto)) {
            while (true) {
                Socket socketCliente = servidorSocket.accept();
                System.out.println("Cliente conectado: " + socketCliente.getInetAddress());

                DataInputStream in = new DataInputStream(socketCliente.getInputStream());
                DataOutputStream out = new DataOutputStream(socketCliente.getOutputStream());

                String primerMensaje = in.readUTF();
                System.out.println("Mensaje inicial recibido: " + primerMensaje);

                if (primerMensaje.startsWith("CONSULTAR") || primerMensaje.startsWith("BUSCAR:") || primerMensaje.equals("ORDENAR")) {
                    new HiloConsulta(socketCliente, primerMensaje).start();
                    System.out.println(" Petición de consulta: " + primerMensaje);
                    continue;
                }

                if (!primerMensaje.equalsIgnoreCase("LISTO")) {
                    System.out.println(" Mensaje inicial inválido: " + primerMensaje);
                    socketCliente.close();
                    continue;
                }

                // Si es LISTO, recibimos el nombre del jugador
                String nombre = in.readUTF();
                System.out.println("Intentando registrar: " + nombre);

                if (EstadoPartida.partidaCompleta()) {
                    out.writeUTF("LLENO");
                    socketCliente.close();
                    System.out.println(" Partida ya llena. Conexión cerrada.");
                    continue;
                }

                boolean registrado = EstadoPartida.registrarNombre(nombre);
                if (registrado) {
                    out.writeUTF("OK");
                    out.writeInt(clientes.size() + 1);

                    ManejadorCliente manejador = new ManejadorCliente(socketCliente, clientes.size() + 1);
                    manejador.setNombreJugador(nombre);
                    clientes.add(manejador);
                    manejador.start();

                    System.out.println("Jugador " + clientes.size() + " registrado como: " + nombre);
                } else {
                    out.writeUTF("RECHAZADO");
                    socketCliente.close();
                    System.out.println(" Nombre duplicado. Conexión cerrada.");
                    continue;
                }

                if (clientes.size() == 2) {
                    System.out.println(" Dos jugadores conectados. Iniciando partida...");

                    // Generar 24 IDs aleatorios del 1 al 40
                    List<Integer> ids = new ArrayList<>();
                    for (int i = 1; i <= 40; i++) ids.add(i);
                    Collections.shuffle(ids);
                    List<Integer> seleccionados = ids.subList(0, 24);
                    String datos = seleccionados.toString();

                    // Obtener nombres
                    String nombre1 = clientes.get(0).getNombreJugador();
                    String nombre2 = clientes.get(1).getNombreJugador();

                    // Enviar rival
                    clientes.get(0).enviarMensaje("RIVAL:" + nombre2);
                    clientes.get(1).enviarMensaje("RIVAL:" + nombre1);

                    // Enviar IDs y señal de inicio
                    for (ManejadorCliente c : clientes) {
                        c.enviarMensaje("DATOS:" + datos);
                        c.enviarMensaje("INICIAR");
                    }

                    // Enviar turnos
                    clientes.get(0).enviarMensaje("TURNO:1");
                    clientes.get(1).enviarMensaje("TURNO:2");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cambiarTurno() {
        turnoActual = (turnoActual == 1) ? 2 : 1;
    }

    public static void enviarTurnoAClientes() {
        for (ManejadorCliente mc : clientes) {
            if (mc.getNumeroJugador() == turnoActual) {
                mc.enviarMensaje("TURNO:" + turnoActual); // su turno
            } else {
                mc.enviarMensaje("TURNO:" + (turnoActual == 1 ? 2 : 1)); // no su turno
            }
        }
    }


    public static int getTurnoActual() {
        return turnoActual;
    }

    public static List<ManejadorCliente> getClientes() {
        return clientes;
    }

    public static void reiniciarPartida() {
        clientes.clear();
        turnoActual = 1;
        EstadoPartida.reiniciar();
        System.out.println(" Partida reiniciada.");
    }

}
