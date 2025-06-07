// PantallaPresentacion.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class PantallaPresentacion extends JPanel {
    public PantallaPresentacion(VentanaPrincipal ventana) {
        setBackground(new Color(230, 240, 255));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("ADIVINA QUIÉN");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(100, 0, 50, 0));

        JButton botonComenzar = crearBoton("Menú Principal", new Color(186, 143, 255));
        botonComenzar.addActionListener(e -> ventana.mostrar("menu"));

        add(titulo);
        add(botonComenzar);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {}
        };
        boton.setFont(new Font("SansSerif", Font.BOLD, 22));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setPreferredSize(new Dimension(200, 50));
        boton.setMaximumSize(new Dimension(200, 50));
        return boton;
    }
}
