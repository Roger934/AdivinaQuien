// RegistroJugadores.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class RegistroJugadores extends JPanel {
    public RegistroJugadores(VentanaPrincipal ventana) {
        setBackground(new Color(255, 250, 240));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Registra a los jugadores");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(50, 0, 30, 0));

        JTextField campoJugador1 = crearCampo("Jugador 1");
        JTextField campoJugador2 = crearCampo("Jugador 2");

        JButton continuar = crearBoton("Continuar", new Color(255, 204, 102));
        continuar.addActionListener(e -> {
            String j1 = campoJugador1.getText().trim();
            String j2 = campoJugador2.getText().trim();
            if (j1.isEmpty() || j2.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ambos jugadores deben ingresar su nombre");
                return;
            }
            JOptionPane.showMessageDialog(this, "Jugadores registrados correctamente.");
        });

        JButton volver = crearBoton("Volver al Menú", new Color(186, 143, 255));
        volver.addActionListener(e -> ventana.mostrar("menu"));

        add(titulo);
        add(campoJugador1);
        add(Box.createVerticalStrut(20));
        add(campoJugador2);
        add(Box.createVerticalStrut(30));
        add(continuar);
        add(Box.createVerticalStrut(20));
        add(volver);
    }

    private JTextField crearCampo(String titulo) {
        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(400, 40));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 20));
        campo.setBorder(BorderFactory.createTitledBorder(titulo));
        return campo;
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
        boton.setFont(new Font("SansSerif", Font.BOLD, 20));
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