package interfaz;
/// ////////////////////////////////////////////////////////////

//           QUITAR DESPUES
/// /////////////////////////////////////////
import utils.Config;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PantallaJugar extends JPanel {

    private Socket socketCliente = null;
    private boolean nombreValidado = false;

    public PantallaJugar(VentanaPrincipal ventana) {
        setLayout(null);
        setBackground(new Color(245, 245, 255));

        int panelWidth = 500;
        int panelHeight = 320;
        int panelX = (1280 - panelWidth) / 2;
        int panelY = (720 - panelHeight) / 2;

        JPanel contenedor = new JPanel();
        contenedor.setLayout(null);
        contenedor.setBackground(Color.WHITE);
        contenedor.setBounds(panelX, panelY, panelWidth, panelHeight);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        add(contenedor);

        JLabel titulo = new JLabel("Nombre de Jugador", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBounds(50, 20, 400, 30);
        contenedor.add(titulo);

        JTextField nombreField = new JTextField();
        nombreField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        nombreField.setBounds(100, 60, 300, 35);
        nombreField.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 255), 2, true));
        contenedor.add(nombreField);

        JButton crearPartida = new JButton("Crear Partida");
        crearPartida.setFont(new Font("SansSerif", Font.BOLD, 18));
        crearPartida.setBackground(new Color(144, 238, 144));
        crearPartida.setBounds(90, 120, 160, 40);
        contenedor.add(crearPartida);

        JButton unirsePartida = new JButton("Unirse a Partida");
        unirsePartida.setFont(new Font("SansSerif", Font.BOLD, 18));
        unirsePartida.setBackground(new Color(135, 206, 250));
        unirsePartida.setBounds(250, 120, 160, 40);
        contenedor.add(unirsePartida);

        crearPartida.addActionListener(e -> {
            if (socketCliente == null) {
                try {
                    new Thread(() -> servidor.Servidor.main(null)).start();
                    Thread.sleep(500);

                    socketCliente = new Socket(Config.getIpServidor(), Config.getPuertoServidor());
                    JOptionPane.showMessageDialog(this, "Socket creado exitosamente.", "Servidor activo", JOptionPane.INFORMATION_MESSAGE);
                    crearPartida.setEnabled(false);
                } catch (IOException | InterruptedException ex) {
                    JOptionPane.showMessageDialog(this, "Error al crear el socket:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    socketCliente = null;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Socket ya está activo.", "Información", JOptionPane.INFORMATION_MESSAGE);
                crearPartida.setEnabled(false);
            }
        });

        unirsePartida.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, ingresa tu nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (socketCliente == null) {
                try {
                    socketCliente = new Socket(Config.getIpServidor(), Config.getPuertoServidor());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Se necesita crear la partida primero.", "Servidor no encontrado", JOptionPane.WARNING_MESSAGE);
                    socketCliente = null;
                    return;
                }
            }

            try {
                DataOutputStream out = new DataOutputStream(socketCliente.getOutputStream());
                DataInputStream in = new DataInputStream(socketCliente.getInputStream());

                out.writeUTF(nombre);
                String respuesta = in.readUTF();

                if (respuesta.equals("RECHAZADO")) {
                    JOptionPane.showMessageDialog(this, "Ese nombre ya está en uso. Intenta con otro.", "Nombre duplicado", JOptionPane.ERROR_MESSAGE);
                    socketCliente.close();
                    socketCliente = null;
                } else {
                    GameDataCliente.setNombreJugador(nombre);
                    mostrarPantallaInicioJuego();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error durante la conexión:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                socketCliente = null;
            }
        });

        JButton btnVolver = new JButton("↩") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(186, 143, 255));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btnVolver.setFocusPainted(false);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setOpaque(false);
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFont(new Font("SansSerif", Font.BOLD, 22));
        btnVolver.setBounds(1180, 600, 50, 50);
        btnVolver.addActionListener(e -> ventana.mostrar("menu"));
        add(btnVolver);
    }

    private void mostrarPantallaInicioJuego() {
        JPanel mensajePanel = new JPanel();
        mensajePanel.setLayout(new BorderLayout());
        mensajePanel.setBackground(Color.WHITE);
        JLabel mensaje = new JLabel("Iniciando el juego...", SwingConstants.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 32));
        mensaje.setForeground(new Color(60, 60, 60));
        mensajePanel.add(mensaje, BorderLayout.CENTER);
        removeAll();
        setLayout(new BorderLayout());
        add(mensajePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
