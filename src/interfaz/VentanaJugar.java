package interfaz;

import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;

public class VentanaJugar extends JPanel {

    private VentanaPrincipal ventana;

    public VentanaJugar(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(null);
        setBackground(new Color(245, 245, 255));

        int panelWidth = 400;
        int panelHeight = 180;
        int panelX = (1280 - panelWidth) / 2;
        int panelY = (720 - panelHeight) / 2;

        JPanel contenedor = new JPanel();
        contenedor.setLayout(null);
        contenedor.setBackground(Color.WHITE);
        contenedor.setBounds(panelX, panelY, panelWidth, panelHeight);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        add(contenedor);

        JLabel titulo = new JLabel("Listo para jugar", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBounds(50, 20, 300, 30);
        contenedor.add(titulo);

        JButton jugar = new JButton("JUGAR");
        jugar.setFont(new Font("SansSerif", Font.BOLD, 20));
        jugar.setBackground(new Color(0, 153, 255));
        jugar.setForeground(Color.WHITE);
        jugar.setBounds(125, 80, 150, 50);
        jugar.setFocusPainted(false);
        jugar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contenedor.add(jugar);

        jugar.addActionListener(e -> {
            if (GameDataCliente.getConexion() != null) {
                System.out.println("➡️ Ir a esperandoJugador con conexión: " + GameDataCliente.getConexion());
                ventana.mostrar("esperandoJugador");
            } else {
                mostrarError("Primero debes registrarte correctamente.");
            }
        });
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
