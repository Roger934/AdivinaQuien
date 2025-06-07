package interfaz;

import javax.swing.*;
import java.awt.*;

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
        mensaje.setHorizontalAlignment(SwingConstants.CENTER); // o CENTER si prefieres
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

        // PANEL BOTÓN
        JPanel botonPanel = new JPanel();
        botonPanel.setBackground(new Color(245, 245, 255));
        botonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton btnCancelar = new JButton("Cancelar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 100, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override protected void paintBorder(Graphics g) {}
        };
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setOpaque(false);
        btnCancelar.setPreferredSize(new Dimension(200, 40));
        btnCancelar.addActionListener(e -> ventana.mostrar("modoConexion"));
        botonPanel.add(btnCancelar);
        add(botonPanel);
    }
}
