// MenuPrincipal.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JPanel {
    public MenuPrincipal(VentanaPrincipal ventana) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(245, 245, 255));

        JLabel titulo = new JLabel("Menú Principal");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(60, 0, 40, 0));

        JButton registro = crearBoton("Registro de Jugadores", new Color(255, 204, 102));
        registro.addActionListener(e -> ventana.mostrar("registro"));

        JButton instrucciones = crearBoton("Instrucciones", new Color(186, 238, 150));
        instrucciones.addActionListener(e -> ventana.mostrar("instrucciones"));

        JButton creditos = crearBoton("Créditos", new Color(186, 143, 255));
        creditos.addActionListener(e -> ventana.mostrar("creditos"));

        add(titulo);
        add(registro);
        add(Box.createVerticalStrut(20));
        add(instrucciones);
        add(Box.createVerticalStrut(20));
        add(creditos);
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
        boton.setPreferredSize(new Dimension(300, 50));
        boton.setMaximumSize(new Dimension(300, 50));
        return boton;
    }
}