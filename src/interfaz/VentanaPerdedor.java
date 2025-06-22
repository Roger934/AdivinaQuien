package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class VentanaPerdedor extends JPanel {

    public VentanaPerdedor(VentanaPrincipal ventana, String nombrePerdedor) {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));

        // Fondo redimensionado
        ImageIcon fondoOriginal = new ImageIcon("assets/fondos/perdedor.jpg");
        Image fondoEscalado = fondoOriginal.getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
        JLabel fondo = new JLabel(new ImageIcon(fondoEscalado));
        fondo.setBounds(0, 0, 1280, 720);
        fondo.setLayout(null);
        add(fondo);

        // Caja de contenido
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setOpaque(false);
        caja.setBounds(750, 200, 480, 300); // Ajustable
        fondo.add(caja);

        JLabel perdido = new JLabel("😥 Has perdido, " + nombrePerdedor, SwingConstants.CENTER);
        perdido.setFont(new Font("SansSerif", Font.BOLD, 30));
        perdido.setForeground(new Color(255, 255, 255));
        perdido.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel animo = new JLabel("¡Póngase pilas para la próxima!", SwingConstants.CENTER);
        animo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        animo.setForeground(new Color(255, 255, 255));
        animo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnVolverAJugar = crearBoton("Volver a jugar", new Color(204, 0, 0));
        btnVolverAJugar.addActionListener((ActionEvent e) -> {
            ventana.mostrar("registroJugador");
        });

        caja.add(perdido);
        caja.add(Box.createVerticalStrut(15));
        caja.add(animo);
        caja.add(Box.createVerticalStrut(30));
        caja.add(btnVolverAJugar);

        // Botón salir al menú (esquina inferior derecha)
        JButton btnSalir = crearBoton("Salir", new Color(102, 102, 102));
        btnSalir.setBounds(1080, 620, 160, 50);
        btnSalir.addActionListener((ActionEvent e) -> {
            ventana.mostrar("menu");
        });
        fondo.add(btnSalir);
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
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 45, 45);
                g2.dispose();
            }
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
