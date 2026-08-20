package servidor;

import servidor.Servidor;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;


public class ManejadorCliente extends Thread {

    private Socket socket;
    private DataInputStream entrada;
    private DataOutputStream salida;
    private int numeroJugador;
    private String nombreJugador;
    private String personajeSecreto;

    public ManejadorCliente(Socket socket, int numeroJugador) {
        this.socket = socket;
        this.numeroJugador = numeroJugador;

        try {
            entrada = new DataInputStream(socket.getInputStream());
            salida = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String mensaje = entrada.readUTF();
                System.out.println("Jugador " + numeroJugador + ": " + mensaje);

                // Manejo de tipos de mensaje
                if (mensaje.startsWith("PERSONAJE:")) {
                    personajeSecreto = mensaje.substring("PERSONAJE:".length());

                    // Reenviar al rival
                    for (ManejadorCliente mc : Servidor.getClientes()) {
                        if (mc != this) {
                            mc.enviarMensaje("OPONENTE_PERSONAJE:" + personajeSecreto);
                        }
                    }

                } 
                
                else if (mensaje.startsWith("PREGUNTA:")) {
                // Reenviar solo la pregunta al otro jugador
                    for (ManejadorCliente mc : Servidor.getClientes()) {
                        if (mc != this) {
                            mc.enviarMensaje(mensaje);
                        }
                    }
                // NO cambiar turno aquí
                }

                else if (mensaje.startsWith("RESPUESTA:")) {
                // Reenviar solo la respuesta al otro jugador
                for (ManejadorCliente mc : Servidor.getClientes()) {
                    if (mc != this) {
                        mc.enviarMensaje(mensaje);
                        }
                    }

                    //  Ahora sí cambiar turno
                    Servidor.cambiarTurno();
                    Servidor.enviarTurnoAClientes();
                }
 
                else if (mensaje.startsWith("RESULTADO:")) {
                    String resultado = mensaje.substring("RESULTADO:".length()).trim();

                    for (ManejadorCliente mc : Servidor.getClientes()) {
                        if (mc == this) {
                            if (resultado.equalsIgnoreCase("GANE")) {
                                mc.enviarMensaje("GANASTE");
                            } else {
                                mc.enviarMensaje("PERDISTE");
                            }
                        } else {
                            if (resultado.equalsIgnoreCase("GANE")) {
                                mc.enviarMensaje("PERDISTE");
                            } else {
                                mc.enviarMensaje("GANASTE");
                            }
                        }
                    }

                } else if (mensaje.startsWith("GUARDAR_PARTIDA:")) {
                    String datos = mensaje.substring("GUARDAR_PARTIDA:".length());
                    String[] partes = datos.split(";");

                    if (partes.length == 6) {
                        String jugador1 = partes[0];
                        String jugador2 = partes[1];
                        String ganador = partes[2];
                        String personajeGanador = partes[3];
                        LocalDate fecha = LocalDate.parse(partes[4]);
                        LocalTime duracion = LocalTime.parse(partes[5]);

                        RegistroPartida partida = new RegistroPartida(
                                jugador1,
                                jugador2,
                                ganador,
                                personajeGanador,
                                fecha,
                                duracion
                        );

                        partida.guardarEnBaseDeDatos(); // Guarda en MySQL desde el servidor
                        System.out.println("Datos recibidos: " + jugador1 + jugador2 + ganador + personajeGanador + fecha + duracion);
                        enviarMensaje("PARTIDA_GUARDADA");
                        partida.limpiar(); // Borramos los datos para evitar duplicados
                        Servidor.reiniciarPartida();
                    } else {
                        System.err.println(" Datos incompletos para guardar partida: " + datos);
                    }
                } else {
                    salida.writeUTF("Recibido: " + mensaje);
                }
            }

        } catch (IOException e) {
            System.out.println("Jugador " + numeroJugador + " desconectado.");
        } finally {
            try {
                if (socket != null)
                    socket.close();
                System.out.println("Jugador " + numeroJugador + " desconectado");
                
                // Notificar al rival que este jugador se ha desconeectado
                for (ManejadorCliente mc : Servidor.getClientes())
                {
                    if (mc != null)
                    {
                        mc.enviarMensaje("DESCONEXION_RIVAL");
                    }
                }
                //  Reiniciar lista de jugadores y estado
                Servidor.getClientes().clear();         // Limpia los hilos actuales
                EstadoPartida.reiniciar();              // Limpia nombres registrados
                System.out.println("Estado reiniciado. Esperando nuevos jugadores...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNombreJugador(String nombre) {
        this.nombreJugador = nombre;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }
    
    public int getNumeroJugador() {
        return numeroJugador;
    }


    public void enviarMensaje(String msg) {
        try {
            salida.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
