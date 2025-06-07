// VentanaPrincipal.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private CardLayout layout;
    private JPanel contenedor;

    public VentanaPrincipal() {
        layout = new CardLayout();
        contenedor = new JPanel(layout);

        contenedor.add(new PantallaPresentacion(this), "presentacion");
        contenedor.add(new MenuPrincipal(this), "menu");
        contenedor.add(new RegistroJugadores(this), "registro");
        contenedor.add(new Instrucciones(this), "instrucciones");
        contenedor.add(new Creditos(this), "creditos");

        setTitle("Adivina Quién");
        setSize(1280, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(contenedor);
        setVisible(true);
    }

    public void mostrar(String nombrePantalla) {
        layout.show(contenedor, nombrePantalla);
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}