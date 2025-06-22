package interfaz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import utils.GameDataCliente;

public class VentanaGanador extends JPanel {

    public VentanaGanador(VentanaPrincipal ventana, String nombreGanador) {
        setLayout(null);
        setPreferredSize(new Dimension(1280, 720));

        // Fondo redimensionado
        ImageIcon fondoOriginal = new ImageIcon("assets/fondos/ganador.jpg");
        Image fondoEscalado = fondoOriginal.getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
        JLabel fondo = new JLabel(new ImageIcon(fondoEscalado));
        fondo.setBounds(0, 0, 1280, 720);
        fondo.setLayout(null);
        add(fondo);

        // Caja de contenido con coordenadas
        JPanel caja = new JPanel();
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setOpaque(false);
        caja.setBounds(70, 220, 480, 300); // Puedes ajustar X, Y aquí
        fondo.add(caja);

        JLabel felicidades = new JLabel("\uD83C\uDF89 ¡Felicidades! " + nombreGanador + " Has ganado \uD83C\uDFC6", SwingConstants.CENTER);
        felicidades.setFont(new Font("SansSerif", Font.BOLD, 30));
        felicidades.setForeground(new Color(51, 51, 51));
        felicidades.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel eresBueno = new JLabel("Eres muy bueno jugando", SwingConstants.CENTER);
        eresBueno.setFont(new Font("SansSerif", Font.PLAIN, 20));
        eresBueno.setForeground(new Color(51, 51, 51));
        eresBueno.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnVolverAJugar = crearBoton("Volver a jugar", new Color(255, 204, 0));
        btnVolverAJugar.addActionListener((ActionEvent e) -> {
            GameDataCliente.limpiar();
            GameDataCliente.getConexion().cerrar();
            ventana.mostrar("registroJugador");
        });

        caja.add(felicidades);
        caja.add(Box.createVerticalStrut(15));
        caja.add(eresBueno);
        caja.add(Box.createVerticalStrut(30));
        caja.add(btnVolverAJugar);

        // Botón salir al menú (esquina inferior derecha)
        JButton btnSalir = crearBoton("Salir", new Color(102, 51, 153));
        btnSalir.setBounds(1080, 620, 160, 50);
        btnSalir.addActionListener((ActionEvent e) -> {
            GameDataCliente.limpiar();
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
