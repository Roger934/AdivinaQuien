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

    private Image fondo;
    private JLabel gifLabel;
    private JLabel puntosLabel;
    private int puntos = 1;

    boolean recibioIniciar = false;
    boolean recibioDatos = false;
    List<Integer> ids = new ArrayList<>();

    public EsperandoJugador(VentanaPrincipal ventana) {
        setLayout(null);
        fondo = new ImageIcon("assets/fondos/esperando.png").getImage();

        // GIF separado
        ImageIcon gifIcon = new ImageIcon("assets/gifs/esperando.gif");
        gifLabel = new JLabel(gifIcon);
        gifLabel.setBounds(780, 230, gifIcon.getIconWidth(), gifIcon.getIconHeight());
        add(gifLabel);

        // Texto de puntos (con fuente más estilizada)
        puntosLabel = new JLabel("Esperando");
        puntosLabel.setFont(new Font("Impact", Font.BOLD, 35));
        puntosLabel.setForeground(Color.WHITE);
        puntosLabel.setBounds(100, 570, 400, 50); // Centrado manual
        add(puntosLabel);

        // Animación de puntos
        Timer timer = new Timer(500, e -> {
            StringBuilder texto = new StringBuilder("Esperando");
            for (int i = 0; i < puntos; i++) texto.append(".");
            puntosLabel.setText(texto.toString());
            puntos = (puntos % 3) + 1;
        });
        timer.start();

        // Botón Cancelar centrado en X (en una ventana de 1280px)
        int botonAncho = 180;
        int posX = 1000;//(1280 - botonAncho) / 2;

        JButton cancelar = new JButton("Cancelar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(220, 50, 50)); // rojo fuerte
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        cancelar.setFont(new Font("SansSerif", Font.BOLD, 18));
        cancelar.setForeground(Color.WHITE);
        cancelar.setFocusPainted(false);
        cancelar.setContentAreaFilled(false);
        cancelar.setOpaque(false);
        cancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelar.setBounds(posX, 600, botonAncho, 45);
        cancelar.addActionListener(e -> ventana.mostrar("menu"));
        add(cancelar);

        // Hilo para escuchar mensajes del servidor
        new Thread(() -> {
            try {
                ClienteConexion conexion = GameDataCliente.getConexion();
                if (conexion == null) return;
                DataInputStream in = conexion.getEntrada();

                while (!(recibioIniciar && recibioDatos)) {
                    String msg = in.readUTF();

                    if (msg.startsWith("RIVAL:")) {
                        GameDataCliente.setNombreRival(msg.substring(6).trim());
                    } else if (msg.startsWith("DATOS:")) {
                        String datos = msg.substring(6).replaceAll("[\\[\\]\\s]", "");
                        for (String s : datos.split(",")) {
                            try {
                                ids.add(Integer.parseInt(s));
                            } catch (NumberFormatException ignored) {}
                        }
                        GameDataCliente.setListaPersonajes(ids);
                        recibioDatos = true;
                    } else if ("INICIAR".equals(msg)) {
                        recibioIniciar = true;
                    }
                }

                SwingUtilities.invokeLater(() -> ventana.mostrarTablero());

            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        this, "Se perdió la conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE
                ));
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }
}
