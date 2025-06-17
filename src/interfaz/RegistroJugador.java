package interfaz;

import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;

public class RegistroJugador extends JPanel {

    public RegistroJugador(VentanaPrincipal ventana) {
        setLayout(null);
        setBackground(new Color(245, 245, 255));

        int panelWidth = 500;
        int panelHeight = 200;
        int panelX = (1280 - panelWidth) / 2;
        int panelY = (720 - panelHeight) / 2;

        JPanel contenedor = new JPanel();
        contenedor.setLayout(null);
        contenedor.setBackground(Color.WHITE);
        contenedor.setBounds(panelX, panelY, panelWidth, panelHeight);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        add(contenedor);

        JLabel titulo = new JLabel("Ingresa tu nombre", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setBounds(50, 20, 400, 30);
        contenedor.add(titulo);

        JTextField nombreField = new JTextField();
        nombreField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        nombreField.setBounds(100, 70, 300, 35);
        contenedor.add(nombreField);

        JButton continuar = new JButton("Continuar");
        continuar.setFont(new Font("SansSerif", Font.BOLD, 18));
        continuar.setBackground(new Color(144, 238, 144));
        continuar.setBounds(170, 120, 160, 40);
        contenedor.add(continuar);

        continuar.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, escribe tu nombre.", "Campo vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            GameDataCliente.setNombreJugador(nombre);
            ventana.mostrar("modoConexion");
        });
    }
}
