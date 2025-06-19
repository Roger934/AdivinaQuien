package interfaz;

import logica.TableroControlador;
import modelo.Personaje;
import utils.GameDataCliente;
import logica.PreguntaControlador;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Tablero extends JPanel {

    private VentanaPrincipal ventana;
    private boolean enableTablero = false;
    private boolean personajeYaElegido = false;
    private boolean tableroInteractivo = false;

    private JLabel imagenSel;
    private JLabel nombreSel;
    private Personaje personajeSeleccionado; // 🔹 Nuevo atributo para guardar el personaje
    private List<Personaje> personajes; // 🔹 Ahora es accesible en toda la clase

    private JButton btnDesdeTablero;
    private JButton btnDesdeLista;
    private JButton btnAleatorio;

    private JTextArea areaChat;

    private JButton btnSi;
    private JButton btnNo;
    private String preguntaPendiente = null;


    public Tablero(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        List<Integer> lista = GameDataCliente.getListaPersonajes();
        this.personajes = TableroControlador.obtenerPersonajesDesdeBD(lista);

        List<Personaje> personajes = TableroControlador.obtenerPersonajesDesdeBD(lista);

        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelIzquierdo = crearPanelIzquierdo();
        add(panelIzquierdo, BorderLayout.WEST);

        JPanel tableroPanel = crearTableroVisual(personajes);
        add(tableroPanel, BorderLayout.CENTER);

        // Panel para las preguntas
        JPanel panelDerecho = crearPanelDerecho();
        add(panelDerecho, BorderLayout.EAST);

        // Hilo para los mensajes
        new Thread(() -> {
            try {
                var conexion = GameDataCliente.getConexion();
                var entrada = conexion.getEntrada();

                while (true) {
                    String mensaje = entrada.readUTF();

                    if (mensaje.startsWith("OPONENTE_PERSONAJE:")) {
                        String nombrePersonaje = mensaje.substring("OPONENTE_PERSONAJE:".length()).trim();
                        GameDataCliente.setPersonajeRival(nombrePersonaje);
                        System.out.println("🕵️ Personaje del oponente recibido: " + nombrePersonaje);
                    }

                    else if (mensaje.startsWith("PREGUNTA:")) {
                        String pregunta = mensaje.substring("PREGUNTA:".length()).trim();

                        // Guardar la pregunta actual pendiente
                        preguntaPendiente = pregunta;

                        // Mostrar en chat y habilitar botones de respuesta
                        SwingUtilities.invokeLater(() -> {
                            areaChat.append("Rival: " + pregunta + "\n");
                            btnSi.setEnabled(true);
                            btnNo.setEnabled(true);
                        });
                    }

                    else if (mensaje.startsWith("RESPUESTA:")) {
                        String respuesta = mensaje.substring("RESPUESTA:".length()).trim();
                        SwingUtilities.invokeLater(() -> {
                            areaChat.append("Rival respondió: " + respuesta + "\n");
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.setBackground(new Color(245, 245, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel miNombre = new JLabel("Jugador: " + GameDataCliente.getNombreJugador());
        JLabel rivalNombre = new JLabel("Rival: " + GameDataCliente.getNombreRival());
        JLabel timer = new JLabel("⏱ Tiempo: 00:00");
        JButton musica = new JButton("🎵 Música");

        miNombre.setFont(new Font("SansSerif", Font.BOLD, 16));
        rivalNombre.setFont(new Font("SansSerif", Font.BOLD, 16));
        timer.setFont(new Font("Monospaced", Font.PLAIN, 14));

        panel.add(miNombre);
        panel.add(rivalNombre);
        panel.add(timer);
        panel.add(musica);

        return panel;
    }

    private JPanel crearPanelIzquierdo() {
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(new Color(255, 250, 240));
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Seleccionar"));

        JPanel panelSeleccion = new JPanel();
        panelSeleccion.setLayout(new BoxLayout(panelSeleccion, BoxLayout.Y_AXIS));
        panelSeleccion.setOpaque(false);
        panelSeleccion.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon iconoIncognito = new ImageIcon("assets/iconos/incognito.png");
        Image imgEscalada = iconoIncognito.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imagenSel = new JLabel(new ImageIcon(imgEscalada));
        imagenSel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagenSel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        nombreSel = new JLabel("Sin selección", SwingConstants.CENTER);
        nombreSel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nombreSel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSeleccion.add(imagenSel);
        panelSeleccion.add(nombreSel);
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(panelSeleccion);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        btnDesdeTablero = new JButton("🔘 Desde tablero");
        btnDesdeLista = new JButton("📋 Desde lista");
        btnAleatorio = new JButton("🎲 Aleatorio");


        ActionListener activar = e -> activarSeleccion();

        btnDesdeTablero.addActionListener(activar);
        btnDesdeLista.addActionListener(e -> seleccionarDesdeLista());
        btnAleatorio.addActionListener(e -> seleccionarAleatoriamente());

        for (JButton b : new JButton[]{btnDesdeTablero, btnDesdeLista, btnAleatorio}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(180, 35));
            b.setFocusable(false);
            b.setFont(new Font("SansSerif", Font.PLAIN, 13));
            panelIzquierdo.add(Box.createVerticalStrut(10));
            panelIzquierdo.add(b);
        }

        return panelIzquierdo;
    }

    private JPanel crearTableroVisual(List<Personaje> personajes) {
        JPanel tableroPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        tableroPanel.setBackground(Color.LIGHT_GRAY);
        tableroPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Map<JLabel, ImageIcon> mapaOriginales = new HashMap<>();

        for (Personaje p : personajes) {
            JPanel celda = new JPanel(new BorderLayout());
            celda.setBackground(Color.WHITE);
            celda.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            ImageIcon iconoOriginal = new ImageIcon(p.getRutaImagen());
            Image imgEsc = iconoOriginal.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imgEsc);
            JLabel imagen = new JLabel(iconoEscalado, SwingConstants.CENTER);

            JLabel nombre = new JLabel(p.getNombre(), SwingConstants.CENTER);
            nombre.setFont(new Font("SansSerif", Font.PLAIN, 14));

            mapaOriginales.put(imagen, iconoEscalado);

            celda.add(imagen, BorderLayout.CENTER);
            celda.add(nombre, BorderLayout.SOUTH);

            imagen.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (enableTablero && !personajeYaElegido) {
                        seleccionarPersonaje(p);
                        return;
                    }

                    if (tableroInteractivo && personajeYaElegido) {
                        if (imagen.getIcon() != null) {
                            imagen.setIcon(null);
                            imagen.setOpaque(true);
                            imagen.setBackground(Color.GRAY);
                        } else {
                            ImageIcon icono = new ImageIcon(p.getRutaImagen());
                            Image img = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            imagen.setIcon(new ImageIcon(img));
                            imagen.setOpaque(false);
                        }
                        imagen.repaint();
                    }
                }
            });

            tableroPanel.add(celda);
        }

        return tableroPanel;
    }

    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 500));
        panel.setBackground(new Color(240, 255, 250));
        panel.setBorder(BorderFactory.createTitledBorder("Preguntar"));

        // --- 1. Parte superior: Registro del chat ---
        areaChat = new JTextArea(10, 20);
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        JScrollPane scrollChat = new JScrollPane(areaChat);
        panel.add(scrollChat, BorderLayout.NORTH);

        // --- 2. Parte media: ComboBox de preguntas y campo libre ---
        JPanel panelPreguntas = new JPanel();
        panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
        panelPreguntas.setOpaque(false);

        JComboBox<String> comboPreguntas = new JComboBox<>();
        comboPreguntas.addItem("--- Seleccione una pregunta ---");
        for (String pregunta : PreguntaControlador.obtenerPreguntas()) {
            comboPreguntas.addItem(pregunta);
        }
        comboPreguntas.setMaximumSize(new Dimension(250, 30));

        JTextField campoPreguntaLibre = new JTextField();
        campoPreguntaLibre.setMaximumSize(new Dimension(250, 30));

        JButton btnPreguntar = new JButton("💬 Hacer pregunta");
        btnPreguntar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPreguntar.addActionListener(e -> {
            if (!personajeYaElegido) {
                JOptionPane.showMessageDialog(this, "Primero selecciona tu personaje.");
                return;
            }

            if (GameDataCliente.getPersonajeRival() == null) {
                JOptionPane.showMessageDialog(this, "El rival aún no ha seleccionado su personaje.");
                return;
            }

            String seleccionCombo = (String) comboPreguntas.getSelectedItem();
            String preguntaLibre = campoPreguntaLibre.getText().trim();
            String preguntaFinal = !preguntaLibre.isEmpty() ? preguntaLibre :
                    ("--- Seleccione una pregunta ---".equals(seleccionCombo) ? "" : seleccionCombo);

            if (preguntaFinal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona o escribe una pregunta válida.");
                return;
            }

            try {
                GameDataCliente.getConexion().enviar("PREGUNTA:" + preguntaFinal);
                areaChat.append("Tú: " + preguntaFinal + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        panelPreguntas.add(new JLabel("Elegir pregunta:"));
        panelPreguntas.add(comboPreguntas);
        panelPreguntas.add(Box.createVerticalStrut(10));
        panelPreguntas.add(new JLabel("O escribe una:"));
        panelPreguntas.add(campoPreguntaLibre);
        panelPreguntas.add(Box.createVerticalStrut(15));
        panelPreguntas.add(btnPreguntar);

        panel.add(panelPreguntas, BorderLayout.CENTER);

        // --- 3. Parte inferior: Botones de respuesta Sí/No ---
        JPanel panelRespuestas = new JPanel();
        panelRespuestas.setOpaque(false);
        btnSi = new JButton("✅ Sí");
        btnNo = new JButton("❌ No");

        btnSi.setEnabled(false);
        btnNo.setEnabled(false);

        btnSi.addActionListener(e -> responder("SÍ", areaChat, btnSi, btnNo));
        btnNo.addActionListener(e -> responder("NO", areaChat, btnSi, btnNo));

        panelRespuestas.add(btnSi);
        panelRespuestas.add(btnNo);

        panel.add(panelRespuestas, BorderLayout.SOUTH);

        return panel;
    }

    private void responder(String respuesta, JTextArea areaChat, JButton btnSi, JButton btnNo) {
        try {
            GameDataCliente.getConexion().enviar("RESPUESTA:" + respuesta);
            areaChat.append("Tú respondiste: " + respuesta + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSi.setEnabled(false);
        btnNo.setEnabled(false);
        preguntaPendiente = null;
    }




    private void activarSeleccion() {
        enableTablero = true;
        JOptionPane.showMessageDialog(this, "✅ Tablero activado para selección.");
    }

    private void seleccionarPersonaje(Personaje personaje) {
        GameDataCliente.setPersonajeSecreto(personaje);
        personajeSeleccionado = personaje;

        ImageIcon icono = new ImageIcon(personaje.getRutaImagen());
        Image img = icono.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        imagenSel.setIcon(new ImageIcon(img));
        nombreSel.setText(personaje.getNombre());

        personajeYaElegido = true;
        enableTablero = false;
        tableroInteractivo = true;

        System.out.println("🎯 Personaje propio seleccionado: " + personaje.getNombre());
        System.out.println("👉 Personaje rival actual en GameData: " + GameDataCliente.getPersonajeRival());

        if (GameDataCliente.getPersonajeRival() != null) {
            System.out.println("✅ Ambos jugadores ya seleccionaron personaje.");
        }

        JOptionPane.showMessageDialog(ventana, "🎯 Personaje seleccionado: " + personaje.getNombre());

        enviarPersonajeAlServidor(); // Ahora se envía al servidor

        btnDesdeTablero.setEnabled(false);
        btnDesdeLista.setEnabled(false);
        btnAleatorio.setEnabled(false);
    }


    private void enviarPersonajeAlServidor() {
        if (personajeSeleccionado != null) {
            try {
                String mensaje = "PERSONAJE:" + personajeSeleccionado.getNombre();
                GameDataCliente.getConexion().enviar(mensaje);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Random
    private void seleccionarAleatoriamente() {
        if (personajeYaElegido) {
            JOptionPane.showMessageDialog(this, "Ya seleccionaste un personaje.");
            return;
        }

        Random rand = new Random();
        int posicion = rand.nextInt(personajes.size()); // 0 a 23

        Personaje personaje = personajes.get(posicion);
        seleccionarPersonaje(personaje); // Reutiliza el método existente
    }

    // Selector de lista
    private void seleccionarDesdeLista() {
        if (personajeYaElegido) {
            JOptionPane.showMessageDialog(this, "Ya seleccionaste un personaje.");
            return;
        }

        JComboBox<Personaje> comboBox = new JComboBox<>();
        for (Personaje p : personajes) {
            comboBox.addItem(p); // mostrará el resultado de toString()
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                comboBox,
                "Selecciona tu personaje",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == JOptionPane.OK_OPTION) {
            Personaje seleccionado = (Personaje) comboBox.getSelectedItem();
            if (seleccionado != null) {
                seleccionarPersonaje(seleccionado); // Reutilizamos método
            }
        }
    }


}
