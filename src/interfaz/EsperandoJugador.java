package interfaz;

import utils.GameDataCliente;
import cliente.ClienteConexion;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EsperandoJugador extends JPanel {

    private JLabel mensaje;
    private int puntos = 1;
    boolean recibioIniciar = false;
    boolean recibioDatos = false;
    List<Integer> ids = new ArrayList<>();

    public EsperandoJugador(VentanaPrincipal ventana) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 255));
        setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        // PANEL GIF
        JPanel gifPanel = new JPanel();
        gifPanel.setBackground(new Color(245, 245, 255));
        gifPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        ImageIcon gif = new ImageIcon("assets/gifs/esperando.gif");
        JLabel gifLabel = new JLabel(gif);
        gifPanel.add(gifLabel);
        add(gifPanel);

        add(Box.createRigidArea(new Dimension(0, 20)));

        // PANEL MENSAJE
        JPanel mensajePanel = new JPanel();
        mensajePanel.setBackground(new Color(245, 245, 255));
        mensajePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        mensaje = new JLabel("Esperando al segundo jugador...");
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 24));
        mensaje.setPreferredSize(new Dimension(500, 40));
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        mensajePanel.add(mensaje);
        add(mensajePanel);

        // Animación con puntos
        Timer timer = new Timer(500, e -> {
            StringBuilder texto = new StringBuilder("Esperando al segundo jugador");
            for (int i = 0; i < puntos; i++) {
                texto.append(".");
            }
            mensaje.setText(texto.toString());
            puntos = (puntos % 3) + 1;
        });
        timer.start();

        // Hilo para escuchar mensajes del servidor
        new Thread(() -> {
            try {
                ClienteConexion conexion = GameDataCliente.getConexion();
                if (conexion == null) {
                    System.err.println("Error: La conexión es null en EsperandoJugador.");
                    return;
                }

                DataInputStream in = conexion.getEntrada();

                while (!(recibioIniciar && recibioDatos)) {
                    String msg = in.readUTF();

                    if (msg.startsWith("RIVAL:")) {
                        String rival = msg.substring(6).trim();
                        System.out.println("🎮 Rival recibido: " + rival);
                        GameDataCliente.setNombreRival(rival);

                    } else if (msg.startsWith("DATOS:")) {
                        String datos = msg.substring(6).replaceAll("[\\[\\]\\s]", "");
                        String[] partes = datos.split(",");
                        for (String s : partes) {
                            try {
                                ids.add(Integer.parseInt(s));
                            } catch (NumberFormatException e) {
                                System.out.println("Número inválido recibido: " + s);
                            }
                        }
                        System.out.println("🎯 IDs recibidos del servidor:");
                        System.out.println(ids);
                        GameDataCliente.setListaPersonajes(ids);
                        recibioDatos = true;

                    } else if ("INICIAR".equals(msg)) {
                        System.out.println("✅ Señal INICIAR recibida.");
                        recibioIniciar = true;
                    }
                }

                // Ya podemos cambiar de pantalla
                SwingUtilities.invokeLater(() -> ventana.mostrarTablero());
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this,
                        "Se perdió la conexión con el servidor.",
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();

        add(Box.createRigidArea(new Dimension(0, 30)));
    }
}
