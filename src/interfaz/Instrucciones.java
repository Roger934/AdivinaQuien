package interfaz;

import javax.swing.*;
import java.awt.*;

public class Instrucciones extends JPanel {
    public Instrucciones(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(235, 255, 240)); // fondo pastel verde menta

        // Título
        JLabel titulo = new JLabel("Instrucciones del Juego", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titulo.setForeground(new Color(34, 120, 75)); // verde hoja oscuro
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Panel decorativo del contenido
        JPanel contenido = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(170, 220, 170)); // borde verde suave
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            }
        };
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Área de texto con scroll personalizado
        JTextArea texto = new JTextArea();
        texto.setText("""
OBJETIVO
Adivinar correctamente el personaje secreto que tiene el oponente antes de que él adivine el tuyo.

PARTICIPANTES
Dos jugadores en red local. Cada uno con su propia computadora.

CÓMO JUGAR
1. Ambos jugadores ingresan su nombre. El sistema les asigna un personaje secreto.
2. Verás una cuadrícula con 24 personajes únicos.
3. En tu turno puedes:
   • Hacer una pregunta de sí o no (Ej: ¿Tiene barba?).
   • Intentar adivinar el personaje del oponente.
4. Puedes hacer clic en los personajes que descartes para ocultarlos.

GANADOR
Si aciertas el personaje del oponente, ganas. Si fallas, tienes tres intentos. Si fallas en el
tercer intento pierdes.

FIN DEL JUEGO
Verás una pantalla de resultado. La partida se guarda automáticamente en la base de datos.
""");
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setMargin(new Insets(15, 20, 15, 20));

        // ScrollPane personalizado
        JScrollPane scroll = new JScrollPane(texto);
        scroll.setPreferredSize(new Dimension(850, 360));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(120, 200, 120);
                this.trackColor = new Color(225, 255, 225);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }
        });

        contenido.add(scroll);
        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.add(contenido);
        add(centro, BorderLayout.CENTER);

        // Botón estilizado en verde
        JButton volver = new JButton("Volver al Menú") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(75, 170, 100)); // verde intenso
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
        volver.addActionListener(e -> ventana.mostrar("menu"));

        JPanel pie = new JPanel();
        pie.setBackground(new Color(235, 255, 240));
        pie.add(volver);
        add(pie, BorderLayout.SOUTH);
    }
}
