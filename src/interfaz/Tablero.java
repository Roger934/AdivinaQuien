package interfaz;

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
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1));
        infoPanel.setBackground(new Color(245, 245, 255));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel miNombre = new JLabel("Tu nombre: " + GameDataCliente.getNombreJugador(), SwingConstants.LEFT);
        miNombre.setFont(new Font("SansSerif", Font.BOLD, 18));

        JLabel rivalNombre = new JLabel("Rival: " + GameDataCliente.getNombreRival(), SwingConstants.LEFT);
        rivalNombre.setFont(new Font("SansSerif", Font.BOLD, 18));

        List<Integer> lista = GameDataCliente.getListaPersonajes();
        JLabel idsLabel = new JLabel("IDs: " + (lista != null ? lista.toString() : "No recibidos"), SwingConstants.LEFT);
        idsLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

        infoPanel.add(miNombre);
        infoPanel.add(rivalNombre);
        infoPanel.add(idsLabel);

        add(infoPanel, BorderLayout.NORTH);

        // 🔸 Centro (temporal mientras no dibujamos tablero visual)
        JLabel titulo = new JLabel("TABLERO (próximamente con imágenes)", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titulo, BorderLayout.CENTER);
    }
}
