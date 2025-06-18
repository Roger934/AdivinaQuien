package interfaz;

import cliente.ClienteConexion;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegistroJugador extends JPanel {

    private static final String IP_SERVIDOR = "127.0.0.1";
    private static final int PUERTO = 5000;

    public RegistroJugador(VentanaPrincipal ventana) {
        setLayout(null);
        setBackground(new Color(245, 245, 255));

        int panelWidth = 500;
        int panelHeight = 230; // altura aumentada para el mensaje
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

        JLabel mensaje = new JLabel("", SwingConstants.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 14));
        mensaje.setBounds(50, 110, 400, 20);
        contenedor.add(mensaje);

        JButton continuar = new JButton("Continuar");
        continuar.setFont(new Font("SansSerif", Font.BOLD, 18));
        continuar.setBackground(new Color(144, 238, 144));
        continuar.setBounds(170, 140, 160, 40);
        contenedor.add(continuar);

        continuar.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            mensaje.setText("");
            mensaje.setForeground(Color.BLACK);

            if (nombre.isEmpty()) {
                mensaje.setText("Por favor, escribe tu nombre.");
                mensaje.setForeground(Color.RED);
                return;
            }

            try {
                ClienteConexion conexion = new ClienteConexion(IP_SERVIDOR, PUERTO);
                conexion.enviar(nombre);
                String respuesta = conexion.recibir();

                switch (respuesta) {
                    case "OK":
                        GameDataCliente.setNombreJugador(nombre);
                        GameDataCliente.setConexion(conexion);
                        mensaje.setText("Nombre válido.");
                        mensaje.setForeground(new Color(0, 128, 0)); // verde

                        // Breve retardo para ver el mensaje antes de avanzar
                        Timer timer = new Timer(500, evt -> ventana.irAEperandoJugador());
                        timer.setRepeats(false);
                        timer.start();
                        break;


                    case "RECHAZADO":
                        mensaje.setText("Ese nombre ya está en uso.");
                        mensaje.setForeground(Color.RED);
                        conexion.cerrar();
                        break;

                    case "LLENO":
                        mensaje.setText("Servidor lleno. Intenta más tarde.");
                        mensaje.setForeground(new Color(255, 140, 0)); // naranja
                        conexion.cerrar();
                        break;

                    default:
                        mensaje.setText("Respuesta desconocida del servidor.");
                        mensaje.setForeground(Color.RED);
                        conexion.cerrar();
                        break;
                }

            } catch (IOException ex) {
                mensaje.setText("No se pudo conectar al servidor.");
                mensaje.setForeground(Color.RED);
            }
        });
    }
}
