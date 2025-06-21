// MenuPrincipal.java
package interfaz;

import cliente.ClienteConexion;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JPanel {

    private Image fondo;

    public MenuPrincipal(VentanaPrincipal ventana) {
        setLayout(null);
        fondo = new ImageIcon("assets/fondos/menu.png").getImage();

        // Colores
        Color colorRegistro = new Color(255, 160, 0);
        Color colorInstrucciones = new Color(129, 199, 90);
        Color colorCreditos = new Color(140, 55, 255);
        Color colorRegistros = new Color(30, 90, 255);
        Color colorVolver = new Color(255, 0, 27);

        JButton registro = crearBoton("Registro de Jugadores", colorRegistro);
        registro.setBounds(300, 285, 280, 45);
        registro.addActionListener(e -> ventana.mostrar("registro"));
        add(registro);

        JButton instrucciones = crearBoton("Instrucciones", colorInstrucciones);
        instrucciones.setBounds(250, 435, 280, 45);
        instrucciones.addActionListener(e -> ventana.mostrar("instrucciones"));
        add(instrucciones);

        JButton creditos = crearBoton("Créditos", colorCreditos);
        creditos.setBounds(730, 435, 280, 45);
        creditos.addActionListener(e -> ventana.mostrar("creditos"));
        add(creditos);

        JButton verRegistros = crearBoton("Ver registros", colorRegistros);
        verRegistros.setBounds(680, 285, 280, 45);
        verRegistros.addActionListener(e -> {
            ventana.getVerRegistros().cargarDesdeBoton();
            ventana.mostrar("verRegistros");
        });
        add(verRegistros);

        JButton volver = crearBoton("Inicio", colorVolver);
        volver.setBounds(1100, 620, 150, 40);
        volver.setFont(new Font("SansSerif", Font.BOLD, 18));
        volver.addActionListener(e -> ventana.mostrar("presentacion"));
        add(volver);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Relleno del botón
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 45, 45);

                // Borde blanco
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 45, 45);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Se desactiva el borde predeterminado
            }
        };

        boton.setFont(new Font("SansSerif", Font.BOLD, 20));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
    }
}
