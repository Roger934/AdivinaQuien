package interfaz;

import utils.GameData;
import servidor.Servidor;

import javax.swing.*;
import java.awt.*;

public class ModoConexion extends JPanel {

    public ModoConexion(VentanaPrincipal ventana) {
        setLayout(null);
        setBackground(new Color(245, 245, 255)); // Fondo suave

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

        JLabel titulo = new JLabel("¿Qué deseas hacer?", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBounds(70, 30, 360, 30);
        contenedor.add(titulo);

        JButton crearBtn = new JButton("Crear partida");
        crearBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        crearBtn.setBackground(new Color(173, 216, 230));
        crearBtn.setBounds(80, 90, 160, 40);
        contenedor.add(crearBtn);

        JButton unirseBtn = new JButton("Unirse a partida");
        unirseBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        unirseBtn.setBackground(new Color(144, 238, 144));
        unirseBtn.setBounds(260, 90, 160, 40);
        contenedor.add(unirseBtn);

        crearBtn.addActionListener(e -> {
            servidor.Servidor.iniciar();  // Inicia servidor en segundo plano
            JOptionPane.showMessageDialog(this, "Servidor iniciado. Esperando al segundo jugador...");
            ventana.mostrar("esperandoJugador");
        });

        unirseBtn.addActionListener(e -> {
            // Aquí puedes lanzar la lógica de conexión del cliente
            // Ej: Cliente.main(null); o mejor, ClienteConexion.conectar(...)
            JOptionPane.showMessageDialog(this, "Intentando conectarse al servidor (simulado)...");
            // ventana.mostrar("tablero"); // cambiar por tu vista real
        });

        // Botón para volver
        JButton btnVolver = new JButton("↩") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(186, 143, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnVolver.setFocusPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setOpaque(false);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnVolver.setBounds(1180, 630, 50, 50);
        btnVolver.addActionListener(e -> ventana.mostrar("registroJugador"));
        add(btnVolver);
    }
}
