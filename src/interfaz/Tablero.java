package interfaz;

import logica.TableroControlador;
import modelo.Personaje;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Tablero extends JPanel {

    private VentanaPrincipal ventana;

    public Tablero(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 🔹 Panel superior con nombres e IDs
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(new Color(245, 245, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel miNombre = new JLabel("Tu nombre: " + GameDataCliente.getNombreJugador(), SwingConstants.LEFT);
        JLabel rivalNombre = new JLabel("Rival: " + GameDataCliente.getNombreRival(), SwingConstants.LEFT);

        List<Integer> lista = GameDataCliente.getListaPersonajes();
        JLabel idsLabel = new JLabel("IDs: " + (lista != null ? lista.toString() : "No recibidos"), SwingConstants.LEFT);

        miNombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        rivalNombre.setFont(new Font("SansSerif", Font.BOLD, 18));
        idsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        infoPanel.add(miNombre);
        infoPanel.add(rivalNombre);
        infoPanel.add(idsLabel);
        add(infoPanel, BorderLayout.NORTH);

        // 🔸 Centro: tablero visual con imagen y nombre
        JPanel tableroPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        tableroPanel.setBackground(Color.LIGHT_GRAY);
        tableroPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        List<Personaje> personajes = TableroControlador.obtenerPersonajesDesdeBD(lista);

        for (Personaje p : personajes) {
            JPanel celda = new JPanel();
            celda.setLayout(new BorderLayout());
            celda.setBackground(Color.WHITE);
            celda.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            // Cargar imagen
            ImageIcon iconoOriginal = new ImageIcon(p.getRutaImagen());
            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel imagen = new JLabel(new ImageIcon(imgEscalada));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);

            // Nombre
            JLabel nombre = new JLabel(p.getNombre(), SwingConstants.CENTER);
            nombre.setFont(new Font("SansSerif", Font.PLAIN, 14));

            celda.add(imagen, BorderLayout.CENTER);
            celda.add(nombre, BorderLayout.SOUTH);

            tableroPanel.add(celda);
        }

        add(tableroPanel, BorderLayout.CENTER);
    }
}
