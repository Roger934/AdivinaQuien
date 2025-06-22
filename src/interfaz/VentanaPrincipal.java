// VentanaPrincipal.java
package interfaz;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    private CardLayout layout;
    private JPanel contenedor;
    private VerRegistros verRegistros;


    public VentanaPrincipal() {
        layout = new CardLayout();
        contenedor = new JPanel(layout);

        contenedor.add(new PantallaPresentacion(this), "presentacion");
        contenedor.add(new MenuPrincipal(this), "menu");
        contenedor.add(new RegistroJugador(this), "registro");
        contenedor.add(new Instrucciones(this), "instrucciones");
        contenedor.add(new Creditos(this), "creditos");
        contenedor.add(new RegistroJugador(this), "registroJugador");
        contenedor.add(new VentanaJugar(this), "ventanaJugar");
        // Tabla para los registros
        verRegistros = new VerRegistros(this);
        contenedor.add(verRegistros, "verRegistros");

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

    public void irAEperandoJugador() {
        contenedor.add(new EsperandoJugador(this), "esperandoJugador");
        mostrar("esperandoJugador");
    }

    public void mostrarTablero() {
        contenedor.add(new Tablero(this), "tablero");
        mostrar("tablero");
    }

    public VerRegistros getVerRegistros() {
        return verRegistros;
    }

    public JPanel getContenedor() {
        return contenedor;
    }

    public static void main(String[] args) {
        new VentanaPrincipal();
    }
}