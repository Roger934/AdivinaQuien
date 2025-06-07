// Creditos.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class Creditos extends JPanel {
    public Creditos(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255));

        JLabel titulo = new JLabel("Créditos", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel de integrantes
        JPanel panelIntegrantes = new JPanel();
        panelIntegrantes.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20));
        panelIntegrantes.setBackground(new Color(245, 245, 255));

        panelIntegrantes.add(crearIntegrante("Diego Alejandro Ramos Vazques", "assets/fotos/diego.png"));
        panelIntegrantes.add(crearIntegrante("Emmanuel Lopez de Jesus García", "assets/fotos/emmanuel.png"));
        panelIntegrantes.add(crearIntegrante("Rogelio Uriel Gutierrez Jimenez", "assets/fotos/rogelio.png"));

        add(panelIntegrantes, BorderLayout.CENTER);

        // Panel inferior con logo, datos institucionales y botón
        JPanel info = new JPanel();
        info.setBackground(new Color(245, 245, 255));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        // Logo universidad (más grande y mejor centrado verticalmente)
        ImageIcon logoIcono = new ImageIcon("assets/fotos/uaa_logo.png");
        Image logoEscalado = logoIcono.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(logoEscalado));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Datos institucionales
        JLabel datos = new JLabel("Universidad Autónoma de Aguascalientes - ISC 4A - Junio 2025", SwingConstants.CENTER);
        datos.setFont(new Font("Serif", Font.ITALIC, 18));
        datos.setAlignmentX(Component.CENTER_ALIGNMENT);
        datos.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JButton volver = new JButton("Volver al Menú") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(186, 143, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override
            protected void paintBorder(Graphics g) {}
        };
        volver.setFont(new Font("SansSerif", Font.BOLD, 20));
        volver.setForeground(Color.WHITE);
        volver.setFocusPainted(false);
        volver.setContentAreaFilled(false);
        volver.setOpaque(false);
        volver.setAlignmentX(Component.CENTER_ALIGNMENT);
        volver.setPreferredSize(new Dimension(200, 50));
        volver.setMaximumSize(new Dimension(200, 50));
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

        JLabel etiquetaNombre = new JLabel("<html><div style='text-align: center;'>" + nombre + "</div></html>");
        etiquetaNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        etiquetaNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        etiquetaNombre.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(etiquetaImagen);
        panel.add(etiquetaNombre);

        return panel;
    }
}