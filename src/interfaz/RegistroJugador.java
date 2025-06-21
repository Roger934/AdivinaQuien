package interfaz;

import cliente.ClienteConexion;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class RegistroJugador extends JPanel {

    private static final String IP_SERVIDOR = "192.168.1.100";
    private static final int PUERTO = 5000;

    public RegistroJugador(VentanaPrincipal ventana) {
        setLayout(null);
        setBackground(new Color(30, 30, 60)); // fondo más oscuro y profesional

        int panelWidth = 500;
        int panelHeight = 230;
        int panelX = (1280 - panelWidth) / 2;
        int panelY = (720 - panelHeight) / 2;

        JPanel contenedor = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(255, 255, 255, 90));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);
                g2.dispose();
            }
        };

        contenedor.setLayout(null);
        contenedor.setBackground(Color.WHITE);
        contenedor.setBounds(panelX, panelY, panelWidth, panelHeight);
        contenedor.setOpaque(false); // para ver el fondo general
        add(contenedor);

        JLabel titulo = new JLabel("Ingresa tu nombre", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        titulo.setBounds(panelX, panelY - 60, panelWidth, 40);
        add(titulo);

        JTextField nombreField = new JTextField();
        nombreField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        nombreField.setBounds(100, 70, 300, 38);
        nombreField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        contenedor.add(nombreField);

        JLabel mensaje = new JLabel("", SwingConstants.CENTER);
        mensaje.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mensaje.setBounds(50, 115, 400, 20);
        contenedor.add(mensaje);

        JButton continuar = new JButton("Continuar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(70, 130, 180)); // azul fuerte
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        JButton volverMenu = new JButton("Menú") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); // fondo blanco
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                g2.setColor(Color.BLACK); // borde negro
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 40, 40);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };

        volverMenu.setFont(new Font("Segoe UI", Font.BOLD, 16));
        volverMenu.setForeground(Color.BLACK);
        volverMenu.setFocusPainted(false);
        volverMenu.setContentAreaFilled(false);
        volverMenu.setOpaque(false);
        volverMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        volverMenu.setBounds(1100, 600, 120, 40); // esquina inferior izquierda
        volverMenu.addActionListener(e -> ventana.mostrar("menu"));
        add(volverMenu);



        continuar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        continuar.setForeground(Color.WHITE);
        continuar.setFocusPainted(false);
        continuar.setContentAreaFilled(false);
        continuar.setOpaque(false);
        continuar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continuar.setBounds(170, 150, 160, 45);
        contenedor.add(continuar);

        // --- Lógica del botón (NO MODIFICADA) ---
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
                conexion.enviar("LISTO");
                conexion.enviar(nombre);
                String respuesta = conexion.recibir();

                int numeroJugador = conexion.recibirInt();
                GameDataCliente.setNumeroJugador(numeroJugador);

                switch (respuesta) {
                    case "OK":
                        GameDataCliente.setNombreJugador(nombre);
                        GameDataCliente.setConexion(conexion);
                        mensaje.setText("Nombre válido.");
                        mensaje.setForeground(new Color(0, 128, 0));

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
                        mensaje.setForeground(new Color(255, 140, 0));
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
