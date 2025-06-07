package interfaz;

import javax.swing.*;
import java.awt.*;
import utils.GameData;

public class RegistroJugador extends JPanel {

    public RegistroJugador(VentanaPrincipal ventana) {
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

        JLabel titulo = new JLabel("Ingresa tu nombre", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBounds(50, 30, 400, 30);
        contenedor.add(titulo);

        JTextField nombreField = new JTextField();
        nombreField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        nombreField.setBounds(100, 80, 300, 35);
        nombreField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        contenedor.add(nombreField);

        JButton siguiente = new JButton("Siguiente");
        siguiente.setFont(new Font("SansSerif", Font.BOLD, 18));
        siguiente.setBackground(new Color(144, 238, 144));
        siguiente.setBounds(170, 140, 160, 40);
        contenedor.add(siguiente);

        siguiente.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa tu nombre.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                GameData.setNombreJugador(nombre);  // Guardamos nombre en clase global
                ventana.mostrar("modoConexion");    // Mostramos pantalla siguiente
            }
        });

        // Botón redondo para regresar
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
        btnVolver.setBounds(1180, 600, 50, 50);
        btnVolver.addActionListener(e -> ventana.mostrar("menu"));
        add(btnVolver);
    }
}
