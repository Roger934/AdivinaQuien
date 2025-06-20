package interfaz;

import javax.swing.*;
import java.awt.*;

public class VentanaPerdedor extends JPanel {

    public VentanaPerdedor(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 200, 200)); // Rojo tenue

        JLabel mensaje = new JLabel("😢 Lo siento, has perdido.", SwingConstants.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 28));
        mensaje.setForeground(new Color(120, 20, 20));

        JButton btnContinuar = new JButton("Continuar");
        btnContinuar.setFocusable(false);
        btnContinuar.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnContinuar.setBackground(new Color(240, 180, 180));
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
