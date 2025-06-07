package utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Animaciones {

    // 💗 Efecto de palpitación (zoom in/out)
    public static void palpitar(JComponent comp) {
        Timer timer = new Timer(30, null);
        final float[] escala = {1.0f};
        final boolean[] creciendo = {true};

        timer.addActionListener((ActionEvent e) -> {
            escala[0] += creciendo[0] ? 0.01f : -0.01f;

            if (escala[0] >= 1.1f) creciendo[0] = false;
            if (escala[0] <= 1.0f) creciendo[0] = true;

            comp.setFont(comp.getFont().deriveFont(20f * escala[0]));
            comp.repaint();
        });

        timer.start();
    }

    // 🔄 Efecto de sacudida horizontal (para errores)
    public static void sacudir(JComponent comp) {
        Point original = comp.getLocation();
        Timer timer = new Timer(20, null);
        final int[] desplazamiento = {0};
        final boolean[] derecha = {true};

        timer.addActionListener((ActionEvent e) -> {
            if (derecha[0]) desplazamiento[0] += 2;
            else desplazamiento[0] -= 2;

            if (desplazamiento[0] > 6) derecha[0] = false;
            if (desplazamiento[0] < -6) derecha[0] = true;

            comp.setLocation(original.x + desplazamiento[0], original.y);

            // parar después de un ciclo
            if (Math.abs(desplazamiento[0]) == 6 && !derecha[0]) {
                timer.stop();
                comp.setLocation(original);
            }
        });

        timer.start();
    }

    // 🪂 Efecto de rebote hacia abajo
    public static void rebote(JComponent comp) {
        Point original = comp.getLocation();
        Timer timer = new Timer(20, null);
        final int[] altura = {0};
        final boolean[] bajando = {true};

        timer.addActionListener((ActionEvent e) -> {
            if (bajando[0]) altura[0] += 2;
            else altura[0] -= 2;

            if (altura[0] > 20) bajando[0] = false;
            if (altura[0] < 0) {
                timer.stop();
                comp.setLocation(original);
            } else {
                comp.setLocation(original.x, original.y + altura[0]);
            }
        });

        timer.start();
    }
}
