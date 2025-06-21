package interfaz;

import cliente.ClienteConexion;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class VerRegistros extends JPanel {

    private ClienteConexion conexion;
    private JTable tabla;
    private DefaultTableModel modelo;

    public VerRegistros(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel titulo = new JLabel("📊 Registros de partidas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(titulo, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"Jugador 1", "Jugador 2", "Ganador", "Personaje", "Fecha", "Duración"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        // Botón de volver
        JButton volver = new JButton("Volver");
        volver.setFont(new Font("SansSerif", Font.BOLD, 16));
        volver.setBackground(new Color(255, 182, 193));
        volver.setForeground(Color.BLACK);
        volver.addActionListener(e -> {
            if (conexion != null) {
                conexion.cerrar();
            }
            ventana.mostrar("menu");
        });
        add(volver, BorderLayout.SOUTH);

        // Lanzar consulta en hilo separado
        new Thread(this::cargarDatosDelServidor).start();
    }

    private void cargarDatosDelServidor() {
        try {
            conexion = new ClienteConexion("192.168.1.100", 5000);
            conexion.enviar("CONSULTAR");

            String respuesta = conexion.recibir();

            SwingUtilities.invokeLater(() -> {
                if (respuesta.equals("NO_DATOS")) {
                    JOptionPane.showMessageDialog(this, "⚠️ No hay registros de partidas.");
                } else if (respuesta.equals("ERROR_BD")) {
                    JOptionPane.showMessageDialog(this, "❌ Error al consultar la base de datos.");
                } else {
                    mostrarDatosEnTabla(respuesta);
                }
            });

        } catch (IOException e) {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this, "❌ No se pudo conectar al servidor.", "Error", JOptionPane.ERROR_MESSAGE)
            );
            e.printStackTrace();
        }
    }

    private void mostrarDatosEnTabla(String respuesta) {
        modelo.setRowCount(0); // Limpiar tabla

        String[] filas = respuesta.split("\\|");
        for (String fila : filas) {
            String[] datos = fila.split(",");
            if (datos.length == 6) {
                modelo.addRow(datos);
            }
        }
    }
    public void cargarDesdeBoton() {
        new Thread(this::cargarDatosDelServidor).start();
    }

}
