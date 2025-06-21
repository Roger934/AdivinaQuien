// PantallaPresentacion.java
package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PantallaPresentacion extends JPanel {

    private Image fondo;

    public PantallaPresentacion(VentanaPrincipal ventana) {
        setLayout(null); // Para usar coordenadas absolutas
        fondo = new ImageIcon("assets/fondos/portada.png").getImage();

        // Crear botón personalizado estilo "EMPEZAR"
        JButton botonEmpezar = new JButton("EMPEZAR  ➜") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 66, 66)); // Rojo vivo
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 50, 50);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Sin borde
            }
        };

        botonEmpezar.setFont(new Font("SansSerif", Font.BOLD, 16));
        botonEmpezar.setForeground(Color.BLACK);
        botonEmpezar.setFocusPainted(false);
        botonEmpezar.setContentAreaFilled(false);
        botonEmpezar.setOpaque(false);
        botonEmpezar.setBounds(1000, 550, 200, 45); // Posición y tamaño personalizable

        botonEmpezar.addActionListener(e -> ventana.mostrar("menu"));

        add(botonEmpezar);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this); // Fondo escalado
    }
}
