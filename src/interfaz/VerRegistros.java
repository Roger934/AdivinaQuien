package interfaz;

import cliente.ClienteConexion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class VerRegistros extends JPanel {

    private ClienteConexion conexion;
    private JTable tabla;
    private DefaultTableModel modelo;

    public VerRegistros(VentanaPrincipal ventana) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 244, 248)); // Fondo general claro

        // Título superior
        JLabel titulo = new JLabel("Registros de Partidas", SwingConstants.CENTER);
        titulo.setFont(new Font("Georgia", Font.BOLD, 30));
        titulo.setForeground(new Color(40, 40, 60));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Campo de texto para ingresar nombre y botones de búsqueda
        JTextField campoNombre = new JTextField(15);
        campoNombre.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        campoNombre.setPreferredSize(new Dimension(200, 35));
        campoNombre.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JButton btnBuscar = crearBoton("Buscar por nombre", new Color(0, 120, 215), new Color(0, 90, 170));
        JButton btnOrdenar = crearBoton("Ordenar por duración", new Color(214, 137, 16), new Color(160, 64, 0));

        // Acción del botón Buscar
        btnBuscar.addActionListener(e -> {
            String nombre = campoNombre.getText().trim();
            if (!nombre.isEmpty()) {
                enviarConsulta("BUSCAR:" + nombre);
            } else {
                JOptionPane.showMessageDialog(this, "Ingresa un nombre para buscar.");
            }
        });

        // Acción del botón Ordenar
        btnOrdenar.addActionListener(e -> enviarConsulta("ORDENAR"));

        // Panel que contiene el campo de texto y los botones
        JPanel opcionesPanel = new JPanel();
        opcionesPanel.setBackground(new Color(240, 244, 248));
        opcionesPanel.add(new JLabel("Jugador:"));
        opcionesPanel.add(campoNombre);
        opcionesPanel.add(btnBuscar);
        opcionesPanel.add(btnOrdenar);

        // Panel superior (título + opciones)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(240, 244, 248));
        header.add(titulo, BorderLayout.NORTH);
        header.add(opcionesPanel, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Tabla para mostrar los registros
        String[] columnas = {"Jugador 1", "Jugador 2", "Ganador", "Personaje", "Fecha", "Duración"};
        modelo = new DefaultTableModel(columnas, 0);
        tabla = new JTable(modelo) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(249, 249, 249));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tabla.setOpaque(false);
        tabla.setRowHeight(28);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setShowGrid(false);

        // Encabezado de la tabla con colores alternos
        tabla.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel headerLabel = new JLabel(value.toString(), SwingConstants.CENTER);
                headerLabel.setOpaque(true);
                headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                headerLabel.setForeground(Color.WHITE);
                headerLabel.setBackground(column % 2 == 0 ? new Color(74, 111, 165) : new Color(114, 137, 169));
                return headerLabel;
            }
        });

        // Estilo de las celdas del contenido
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        cellRenderer.setBackground(new Color(249, 249, 249));
        for (int i = 0; i < columnas.length; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Scroll personalizado con esquinas redondeadas y barra gris oscuro
        JScrollPane scrollPane = new JScrollPane(tabla) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL);
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(120, 120, 120);
                this.trackColor = new Color(220, 220, 220);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return crearBotonVacio();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return crearBotonVacio();
            }

            private JButton crearBotonVacio() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
        scrollPane.setVerticalScrollBar(scrollBar);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        scrollPane.setBackground(new Color(240, 244, 248));
        add(scrollPane, BorderLayout.CENTER);

        // Botón para regresar al menú principal
        JButton volver = crearBoton("Volver", new Color(176, 58, 46), new Color(148, 49, 38));
        volver.setPreferredSize(new Dimension(120, 45));
        volver.addActionListener(e -> {
            if (conexion != null) conexion.cerrar();
            ventana.mostrar("menu");
        });

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(240, 244, 248));
        southPanel.add(volver);
        add(southPanel, BorderLayout.SOUTH);

        // Realiza consulta inicial automáticamente
        new Thread(() -> enviarConsulta("CONSULTAR")).start();
    }

    // Envia una solicitud al servidor (consulta por nombre, ordenado, o todos)
    private void enviarConsulta(String tipo) {
        new Thread(() -> {
            try {
                conexion = new ClienteConexion("127.0.0.1", 5000);
                conexion.enviar(tipo);
                String respuesta = conexion.recibir();

                SwingUtilities.invokeLater(() -> {
                    if (respuesta.equals("NO_DATOS")) {
                        JOptionPane.showMessageDialog(this, "No hay registros para mostrar.");
                    } else if (respuesta.equals("ERROR_BD")) {
                        JOptionPane.showMessageDialog(this, "Error al consultar la base de datos.");
                    } else {
                        mostrarDatosEnTabla(respuesta);
                    }
                });

                conexion.cerrar();
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE)
                );
                e.printStackTrace();
            }
        }).start();
    }

    // Carga los datos recibidos en la tabla
    private void mostrarDatosEnTabla(String respuesta) {
        modelo.setRowCount(0); // Limpia la tabla actual
        String[] filas = respuesta.split("\\|");
        for (String fila : filas) {
            String[] datos = fila.split(",");
            if (datos.length == 6) {
                modelo.addRow(datos);
            }
        }
    }

    // Crea botones con degradado y esquinas redondeadas
    private JButton crearBoton(String texto, Color color1, Color color2) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {}
        };
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    public void cargarDesdeBoton() {
        enviarConsulta("CONSULTAR");
    }
}
