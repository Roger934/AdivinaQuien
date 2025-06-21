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

        // 🔹 Título
        JLabel titulo = new JLabel("📊 Registros de partidas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));

        // 🔹 Panel de opciones
        JTextField campoNombre = new JTextField(15);
        JButton btnBuscar = new JButton("🔍 Buscar por nombre");
        JButton btnOrdenar = new JButton("⏱ Ordenar por duración");

        btnBuscar.setBackground(new Color(173, 216, 230));
        btnOrdenar.setBackground(new Color(144, 238, 144));

        btnBuscar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                enviarConsulta("BUSCAR:" + nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Ingresa un nombre para buscar.");
            }
        });

        btnOrdenar.addActionListener(e -> enviarConsulta("ORDENAR"));

        JPanel opcionesPanel = new JPanel();
        opcionesPanel.setBackground(Color.WHITE);
        opcionesPanel.add(new JLabel("Jugador:"));
        opcionesPanel.add(campoNombre);
        opcionesPanel.add(btnBuscar);
        opcionesPanel.add(btnOrdenar);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.add(titulo, BorderLayout.NORTH);
        header.add(opcionesPanel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // 🔹 Tabla
        String[] columnas = {"Jugador 1", "Jugador 2", "Ganador", "Personaje", "Fecha", "Duración"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // 🔹 Botón volver
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

        // 🔹 Consulta inicial
        new Thread(() -> enviarConsulta("CONSULTAR")).start();
    }

    private void enviarConsulta(String tipo) {
        new Thread(() -> {
            try {
                conexion = new ClienteConexion("192.168.1.100", 5000);
                conexion.enviar(tipo);

                String respuesta = conexion.recibir();

                SwingUtilities.invokeLater(() -> {
                    if (respuesta.equals("NO_DATOS")) {
                        JOptionPane.showMessageDialog(this, "⚠️ No hay registros para mostrar.");
                    } else if (respuesta.equals("ERROR_BD")) {
                        JOptionPane.showMessageDialog(this, "❌ Error al consultar la base de datos.");
                    } else {
                        mostrarDatosEnTabla(respuesta);
                    }
                });

                conexion.cerrar();
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "❌ Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE)
                );
                e.printStackTrace();
            }
        }).start();
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
        enviarConsulta("CONSULTAR");
    }
}
