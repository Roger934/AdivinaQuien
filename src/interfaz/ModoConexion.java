package interfaz;

import utils.Config;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ModoConexion extends JPanel {

    private VentanaPrincipal ventana;

    private void esperarOKDesdeServidor() {
        new Thread(() -> {
            try {
                DataInputStream in = new DataInputStream(GameDataCliente.getSocket().getInputStream());
                String mensaje;
                while ((mensaje = in.readUTF()) != null) {
                    if (mensaje.equals("OK")) {
                        GameDataCliente.setTiempoInicioLocal(System.currentTimeMillis());
                        SwingUtilities.invokeLater(() -> ventana.mostrar("tablero"));
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("[Cliente] Error esperando OK: " + e.getMessage());
            }
        }).start();
    }

    public ModoConexion(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(new Color(245, 245, 255));

        int panelWidth = 500;
        int panelHeight = 250;
        int panelX = (1280 - panelWidth) / 2;
        int panelY = (720 - panelHeight) / 2;

        JPanel contenedor = new JPanel();
        contenedor.setLayout(null);
        contenedor.setBackground(Color.WHITE);
        contenedor.setBounds(panelX, panelY, panelWidth, panelHeight);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        add(contenedor);

        JLabel titulo = new JLabel("Selecciona modo de conexión", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBounds(50, 20, 400, 30);
        contenedor.add(titulo);

        JButton crear = new JButton("Crear Partida");
        crear.setFont(new Font("SansSerif", Font.BOLD, 16));
        crear.setBounds(90, 80, 140, 40);
        contenedor.add(crear);

        JButton unirse = new JButton("Unirse a Partida");
        unirse.setFont(new Font("SansSerif", Font.BOLD, 16));
        unirse.setBounds(270, 80, 140, 40);
        contenedor.add(unirse);

        JButton volver = new JButton("Volver");
        volver.setFont(new Font("SansSerif", Font.BOLD, 16));
        volver.setBounds(180, 140, 140, 35);
        contenedor.add(volver);

        volver.addActionListener(e -> ventana.mostrar("registro"));

        crear.addActionListener(e -> {
            if (GameDataCliente.getSocket() == null) {
                try {
                    new Thread(() -> servidor.Servidor.main(null)).start();
                    Thread.sleep(500);

                    Socket socket = new Socket(Config.getIpServidor(), Config.getPuertoServidor());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    out.writeUTF(GameDataCliente.getNombreJugador());
                    String respuesta = in.readUTF();

                    if (respuesta.equals("RECHAZADO")) {
                        JOptionPane.showMessageDialog(this, "Ese nombre ya está en uso. Intenta con otro.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
                        socket.close();
                    } else {
                        GameDataCliente.setSocket(socket);
                        ventana.mostrar("esperandoJugador");
                        esperarOKDesdeServidor();
                    }

                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(this, "Error al crear conexión:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    GameDataCliente.cerrarSocket();
                }
            }
        });

        unirse.addActionListener(e -> {
            if (GameDataCliente.getSocket() == null) {
                try {
                    Socket socket = new Socket(Config.getIpServidor(), Config.getPuertoServidor());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    out.writeUTF(GameDataCliente.getNombreJugador());
                    String respuesta = in.readUTF();

                    if (respuesta.equals("RECHAZADO")) {
                        JOptionPane.showMessageDialog(this, "Ese nombre ya está en uso. Intenta con otro.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
                        socket.close();
                    } else {
                        GameDataCliente.setSocket(socket);
                        ventana.mostrar("esperandoJugador");
                        esperarOKDesdeServidor();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Primero debe crearse la partida (servidor).", "Servidor no encontrado", JOptionPane.WARNING_MESSAGE);
                    GameDataCliente.cerrarSocket();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Ya estás conectado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private void mostrarInicioJuego() {
        ventana.mostrar("tablero");
    }
}
