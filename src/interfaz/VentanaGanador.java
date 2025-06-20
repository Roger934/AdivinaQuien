package interfaz;

import javax.swing.*;
import java.awt.*;

public class VentanaGanador extends JPanel {

    public VentanaGanador(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(200, 255, 200)); // Verde tenue

        JLabel mensaje = new JLabel("🎉 ¡Felicidades, has ganado!", SwingConstants.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 28));
        mensaje.setForeground(new Color(20, 100, 20));

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFocusable(false);
        btnContinuar.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnContinuar.setBackground(new Color(180, 240, 180));
        btnContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(Box.createVerticalGlue());
        centro.add(mensaje);
        centro.add(Box.createRigidArea(new Dimension(0, 40)));
        centro.add(btnContinuar);
        centro.add(Box.createVerticalGlue());

        add(centro, BorderLayout.CENTER);
    }
}
