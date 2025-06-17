package interfaz;

import javax.swing.*;
import java.awt.*;

public class Tablero extends JPanel {

    private VentanaPrincipal ventana;

    public Tablero(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("TABLERO", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setForeground(new Color(60, 60, 60));

        add(titulo, BorderLayout.CENTER);
    }
}
