// Instrucciones.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class Instrucciones extends JPanel {
    public Instrucciones(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 245, 250)); // fondo pastel rosado

        JLabel titulo = new JLabel("Instrucciones del Juego", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        titulo.setForeground(new Color(140, 98, 255));
        titulo.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setBackground(new Color(255, 255, 255));
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 50, 20, 50),
                BorderFactory.createLineBorder(new Color(200, 200, 255), 2)
        ));
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JTextPane texto = new JTextPane();
        texto.setContentType("text/html");
        texto.setText("""
<html><body style='font-family: SansSerif; font-size: 14px; text-align: center;'>
<h2 style='color:#8860D0;'>🎯 Objetivo</h2>
<p style='color:#333'>Adivinar correctamente el personaje secreto que tiene el oponente antes de que él adivine el tuyo.</p>

<h2 style='color:#F18F01;'>👥 Participantes</h2>
<p style='color:#333'>Este juego se juega entre dos jugadores conectados en red local.<br>Cada jugador estará en su propia computadora.</p>

<h2 style='color:#6A994E; text-align:center;'>📘 Cómo jugar</h2>
<ol style='color:#333; text-align: center; max-width: 800px; margin: auto;'>
<li><b>Inicio del juego:</b> Ambos jugadores ingresan su nombre. El sistema asigna un personaje secreto.</li>
<li><b>Tablero:</b> Verás una cuadrícula con 24 personajes únicos.</li>
<li><b>Turnos:</b> En tu turno puedes:
<ul style='list-style: none; padding-left: 0;'>
<li><span style='display: inline;'>Hacer una pregunta de sí o no (Ej: ¿Tiene barba?).</span></li>
<li>Intentar adivinar el personaje del oponente.</li>
</ul></li>
<li><b>Descarte:</b> Puedes hacer clic en los personajes que descartes para ocultarlos visualmente.</li>
<li><b>Ganador:</b> Si aciertas el personaje secreto, ganas. Si fallas, pierdes.</li>
</ol>

<h2 style='color:#F28D35;'>🔁 Final del juego</h2>
<p style='color:#333'>Aparece una pantalla de felicitación o de ánimo.<br>Se guarda la partida en la base de datos.</p>
</body></html>
""");
        texto.setEditable(false);
        texto.setOpaque(false);

        JScrollPane scroll = new JScrollPane(texto);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        contenido.add(scroll);
        add(contenido, BorderLayout.CENTER);

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

        JPanel pie = new JPanel();
        pie.setBackground(new Color(255, 245, 250));
        pie.add(volver);

        add(pie, BorderLayout.SOUTH);
    }
}
