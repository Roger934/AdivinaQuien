package interfaz;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import utils.GameDataCliente;

public class EsperandoJugador extends JPanel {

    private JLabel mensaje;
    private int puntos = 1;

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

        // Timer para los puntos
        Timer timer = new Timer(500, e -> {
            StringBuilder texto = new StringBuilder("Esperando al segundo jugador");
            for (int i = 0; i < puntos; i++) {
                texto.append(".");
            }
            mensaje.setText(texto.toString());
            puntos = (puntos % 3) + 1;
        });
        timer.start();

        add(Box.createRigidArea(new Dimension(0, 30)));

        // HILO DE ESCUCHA DEL SERVIDOR
        new Thread(() -> {
            try {
                if (GameDataCliente.getSocket() == null) return;

                DataInputStream in = new DataInputStream(GameDataCliente.getSocket().getInputStream());
                String mensajeServidor;
                while ((mensajeServidor = in.readUTF()) != null) {
                    if (mensajeServidor.equals("OK")) {
                        GameDataCliente.setTiempoInicioLocal(System.currentTimeMillis());
                        SwingUtilities.invokeLater(() -> ventana.mostrar("tablero"));
                        break;
                    }
                }
            } catch (IOException e) {
                System.err.println("[EsperandoJugador] Error de conexión: " + e.getMessage());
            }
        }).start();
    }
}
