// Creditos.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class Creditos extends JPanel {
    public Creditos(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 235, 255)); // Fondo pastel lila

        // TÍTULO
        JLabel titulo = new JLabel("💜 Créditos", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setForeground(new Color(90, 50, 180));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // PANEL DE INTEGRANTES
        JPanel panelIntegrantes = new JPanel();
        panelIntegrantes.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 20));
        panelIntegrantes.setOpaque(false);

        panelIntegrantes.add(crearIntegrante("Diego Alejandro Ramos Vazques", "assets/fotos/diego.png"));
        panelIntegrantes.add(crearIntegrante("Emmanuel Lopez de Jesus García", "assets/fotos/emmanuel.png"));
        panelIntegrantes.add(crearIntegrante("Rogelio Uriel Gutierrez Jimenez", "assets/fotos/rogelio.png"));

        add(panelIntegrantes, BorderLayout.CENTER);

        // PANEL INFERIOR
        JPanel info = new JPanel();
        info.setBackground(new Color(240, 235, 255));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        ImageIcon logoIcono = new ImageIcon("assets/fotos/uaa_logo.png");
        Image logoEscalado = logoIcono.getImage().getScaledInstance(280, 180, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(logoEscalado));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel datos = new JLabel("Universidad Autónoma de Aguascalientes · ISC 4A · Junio 2025");
        datos.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        datos.setForeground(new Color(90, 90, 90));
        datos.setAlignmentX(Component.CENTER_ALIGNMENT);
        datos.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));

        JButton volver = new JButton("Volver al Menú") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(140, 90, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 45, 45);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        volver.setFont(new Font("Segoe UI", Font.BOLD, 20));
        volver.setForeground(Color.WHITE);
        volver.setFocusPainted(false);
        volver.setContentAreaFilled(false);
        volver.setOpaque(false);
        volver.setPreferredSize(new Dimension(220, 50));
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.addActionListener(e -> ventana.mostrar("menu"));

        info.add(logo);
        info.add(datos);
        info.add(volver);

        add(info, BorderLayout.SOUTH);
    }

    private JPanel crearIntegrante(String nombre, String rutaImagen) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        ImageIcon icono = new ImageIcon(rutaImagen);
        Image imagen = icono.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel etiquetaImagen = new JLabel(new ImageIcon(imagen));
        etiquetaImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaImagen.setBorder(BorderFactory.createLineBorder(new Color(180, 150, 255), 3, true));

        JLabel etiquetaNombre = new JLabel("<html><div style='text-align: center;'>" + nombre + "</div></html>");
        etiquetaNombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        etiquetaNombre.setForeground(new Color(60, 60, 60));
        etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaNombre.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(etiquetaImagen);
        panel.add(etiquetaNombre);

        return panel;
    }
}
