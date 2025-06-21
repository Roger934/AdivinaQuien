package interfaz;

import logica.RegistroPartida;
import logica.TableroControlador;
import modelo.Personaje;
import utils.GameDataCliente;
import logica.PreguntaControlador;

import javax.swing.Timer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.awt.event.ActionEvent;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
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

    // Declaramos como atributos para evitar Spam de preguntas y clicks
    private JButton btnSi;
    private JButton btnNo;
    private String preguntaPendiente = null;
    private JButton btnPreguntar;

    // Adivnar
    private boolean modoAdivinar = false;

    // Para las vidas y finalización de la partida
    private int vidas = 3;
    private boolean yaFinalizoPartida = false;

    // Turnos
    private boolean esMiTurno = false;
    private boolean esperandoRespuesta = false;
    private boolean yaAdivinoEsteTurno = false;

    // Timer
    private Timer timerPartida;
    private long tiempoInicioMillis;
    private JLabel lblTimer;

    // Fecha
    private LocalDate fechaPartida;

    // Musica
    private Clip musicaClip;
    private boolean musicaActiva = true;
    private JButton btnMusica;

    // Extras:
    private JPanel vidasPanel;


    // Creación de todo el tablero
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

        // Hilo para los mensajes -----------------------------------------------------------------------------
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
                            btnPreguntar.setEnabled(false); // Bloquea botón mientras hay pregunta pendiente
                            reproducirSonido("assets/sonidos/pregunta.wav");
                        });
                    }

                    else if (mensaje.startsWith("RESPUESTA:")) {
                        String respuesta = mensaje.substring("RESPUESTA:".length()).trim();
                        SwingUtilities.invokeLater(() -> {
                            areaChat.append("Rival respondió: " + respuesta + "\n");
                            reproducirSonido("assets/sonidos/respuesta.wav");
                        });
                    }

                    else if (mensaje.startsWith("TURNO:")) {
                        int turno = Integer.parseInt(mensaje.substring("TURNO:".length()).trim());
                        int miNumero = GameDataCliente.getNumeroJugador();

                        esMiTurno = (turno == miNumero);
                        System.out.println("📢 Turno actualizado. ¿Es mi turno? " + esMiTurno);
                        yaAdivinoEsteTurno = false;

                        SwingUtilities.invokeLater(() -> {
                            // Solo bloquea preguntar si no es tu turno o hay pregunta sin responder
                            btnPreguntar.setEnabled(esMiTurno && preguntaPendiente == null);
                        });
                    }

                    else if (mensaje.equalsIgnoreCase("GANASTE")) {
                        System.out.println("🏆 Has ganado la partida.");
                        if (timerPartida != null) {
                            timerPartida.stop();
                        }
                        long tiempoFinalMillis = System.currentTimeMillis() - tiempoInicioMillis;
                        int segundos = (int) (tiempoFinalMillis / 1000) % 60;
                        int minutos = (int) ((tiempoFinalMillis / 1000) / 60);

                        // Para guardar luego como LocalTime:
                        LocalTime duracionFinal = LocalTime.of(0, minutos, segundos);
                        System.out.println("Duración de la partida: " + duracionFinal);
                        System.out.println("Fecha: " + fechaPartida);

                        // Guardar partida si eres el ganador
                        String jugador1 = GameDataCliente.getNombreJugador();
                        String jugador2 = GameDataCliente.getNombreRival();
                        String personajeGanador = GameDataCliente.getPersonajeSecreto().getNombre(); // ya está seleccionado por el jugador actual

                        // Construir mensaje y enviar al servidor para guardar la partida
                        String datos = String.format("GUARDAR_PARTIDA:%s;%s;%s;%s;%s;%s",
                                jugador1,
                                jugador2,
                                jugador1,
                                personajeGanador,
                                fechaPartida.toString(),
                                duracionFinal.toString());

                        try {
                            GameDataCliente.getConexion().enviar(datos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (musicaClip != null) {
                            musicaClip.stop();
                        }

                        SwingUtilities.invokeLater(() -> {
                            ventana.mostrar("ventanaGanador");
                        });
                    }

                    else if (mensaje.equalsIgnoreCase("PERDISTE")) {
                        System.out.println("😞 Has perdido la partida.");
                        if (musicaClip != null) {
                            musicaClip.stop();
                        }

                        SwingUtilities.invokeLater(() -> {
                            ventana.mostrar("ventanaPerdedor");
                        });
                    }

                    else if (mensaje.equals("DESCONEXION_RIVAL")) {
                        System.out.println("❌ El rival se ha desconectado. Partida cancelada.");

                        if (musicaClip != null) {
                            musicaClip.stop();
                        }

                        JOptionPane.showMessageDialog(this,
                                "El rival se ha desconectado.\nLa partida se ha cancelado.",
                                "Desconexión del rival",
                                JOptionPane.WARNING_MESSAGE
                        );

                        GameDataCliente.getConexion().cerrar();  // Cerramos el socket
                        GameDataCliente.limpiar();     // Limpiamos el registro
                        ventana.mostrar("registroJugador"); // Vuelve a la pantalla inicial
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "❌ Error de conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
        // -------------------------------------------------------------------------------------------------------------

        // Fecha y timer

        fechaPartida = LocalDate.now();
        tiempoInicioMillis = System.currentTimeMillis();

        timerPartida = new Timer(1000, e -> {
            long tiempoActualMillis = System.currentTimeMillis();
            long transcurrido = tiempoActualMillis - tiempoInicioMillis;

            int segundos = (int) (transcurrido / 1000) % 60;
            int minutos = (int) ((transcurrido / 1000) / 60);

            lblTimer.setText(String.format("⏱ Tiempo: %02d:%02d", minutos, segundos));

        });
        timerPartida.start();
        iniciarMusica();

    }

    //__________________________________SUPERIOR__________________________
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // ---------- Panel de arriba: botón música, fecha, tiempo ----------
        JPanel panelFechaHora = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelFechaHora.setOpaque(false);

        // 🔧 Botón música escalado (rutas absolutas)
        btnMusica = new JButton(cargarYEscalar("C:/AdivinaQuien/assets/iconos/pause.png", 32, 32));
        btnMusica.setPreferredSize(new Dimension(32, 32));
        btnMusica.setFocusPainted(false);
        btnMusica.setContentAreaFilled(false);
        btnMusica.setBorderPainted(false);
        btnMusica.setToolTipText("Pausar/Reproducir música");
        btnMusica.addActionListener(e -> toggleMusica());

        JLabel lblFecha = new JLabel("📅 " + java.time.LocalDate.now());
        lblFecha.setFont(new Font("SansSerif", Font.PLAIN, 13));

        lblTimer = new JLabel("⏱ Tiempo: 00:00");
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 13));

        panelFechaHora.add(btnMusica);
        panelFechaHora.add(Box.createHorizontalStrut(20));
        panelFechaHora.add(lblFecha);
        panelFechaHora.add(Box.createHorizontalStrut(10));
        panelFechaHora.add(lblTimer);

        // ---------- Título ----------
        JLabel titulo = new JLabel("🎲 Adivina Quién 🎯", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(new Color(40, 60, 120));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ---------- Inferior: jugador VS rival ----------
        JPanel panelInferior = new JPanel(new GridLayout(1, 3));
        panelInferior.setOpaque(false);

        JPanel panelJugador = new JPanel();
        panelJugador.setOpaque(false);
        panelJugador.setLayout(new BoxLayout(panelJugador, BoxLayout.Y_AXIS));

        JLabel lblJugador = new JLabel("Jugador: " + GameDataCliente.getNombreJugador(), SwingConstants.CENTER);
        lblJugador.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblJugador.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblJugador.setForeground(new Color(20, 40, 90));

        vidasPanel = crearPanelVidas(vidas);
        panelJugador.add(lblJugador);
        panelJugador.add(Box.createVerticalStrut(5));
        panelJugador.add(vidasPanel);

        JPanel panelCentral = new JPanel();
        panelCentral.setOpaque(false);
        JLabel vs = new JLabel("VS", SwingConstants.CENTER);
        vs.setFont(new Font("Impact", Font.PLAIN, 28));
        vs.setForeground(new Color(200, 50, 50));
        panelCentral.add(vs);

        JPanel panelRival = new JPanel();
        panelRival.setOpaque(false);
        JLabel lblRival = new JLabel("Rival: " + GameDataCliente.getNombreRival(), SwingConstants.CENTER);
        lblRival.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblRival.setForeground(new Color(120, 30, 30));
        panelRival.add(lblRival);

        panelInferior.add(panelJugador);
        panelInferior.add(panelCentral);
        panelInferior.add(panelRival);

        // Añadir todos en orden
        panel.add(panelFechaHora);
        panel.add(Box.createVerticalStrut(5));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(panelInferior);

        return panel;
    }

    private JPanel crearPanelVidas(int vidas) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        for (int i = 0; i < vidas; i++) {
            ImageIcon corazon = new ImageIcon("assets/iconos/corazon.png");
            Image img = corazon.getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(img));
            panel.add(lbl);
        }
        return panel;
    }

    private void actualizarVidasVisuales() {
        vidasPanel.removeAll();
        JPanel nuevo = crearPanelVidas(vidas);
        for (Component c : nuevo.getComponents()) {
            vidasPanel.add(c);
        }
        vidasPanel.revalidate();
        vidasPanel.repaint();
    }


    //________________________________________________________________________________

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

        JButton btnAdivinar = new JButton("🎯 Adivinar personaje");
        btnAdivinar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAdivinar.setMaximumSize(new Dimension(180, 35));
        btnAdivinar.setFocusable(false);
        btnAdivinar.setFont(new Font("SansSerif", Font.PLAIN, 13));

        btnAdivinar.addActionListener(e -> {
            if (yaAdivinoEsteTurno) {
                JOptionPane.showMessageDialog(this, "Solo puedes intentar adivinar una vez por turno.");
                return;
            }

            if (!personajeYaElegido) {
                JOptionPane.showMessageDialog(this, "Primero selecciona tu personaje secreto.");
                return;
            }

            if (!esMiTurno) {
                JOptionPane.showMessageDialog(this, "Espera tu turno para adivinar.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Selecciona un personaje del tablero para intentar adivinar.");
            modoAdivinar = true;
            yaAdivinoEsteTurno = true; // marca que ya intentó adivina
        });
        panelIzquierdo.add(Box.createVerticalStrut(20));
        panelIzquierdo.add(btnAdivinar);


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
                    if (modoAdivinar) {
                        modoAdivinar = false; // 🔁 se desactiva después de usarlo

                        // Validar si ya tienes personaje del rival
                        String personajeRival = GameDataCliente.getPersonajeRival();
                        if (personajeRival == null) {
                            JOptionPane.showMessageDialog(Tablero.this,
                                    "El rival aún no ha seleccionado su personaje.",
                                    "Espera un momento",
                                    JOptionPane.WARNING_MESSAGE);
                            return;
                        }

                        int confirm = JOptionPane.showConfirmDialog(
                                Tablero.this,
                                "¿Quieres adivinar que este es el personaje del oponente?",
                                "Confirmar Adivinanza",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            System.out.println("🔍 Intentaste adivinar: " + p.getNombre());
                            // Luego aquí se compara con personajeRival
                        }

                        String adivinanza = p.getNombre().trim();

                        if (adivinanza.equalsIgnoreCase(personajeRival)) {
                            System.out.println("✅ ¡Has adivinado correctamente!");
                            yaFinalizoPartida = true;

                            // Avisar al servidor que ya terminó la partida y como RESULTADO:GANE
                            try {
                                GameDataCliente.getConexion().enviar("RESULTADO:GANE");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            vidas--;
                            System.out.println("❌ Fallaste. Te quedan " + vidas + " vidas.");
                            actualizarVidasVisuales();

                            if (vidas <= 0) {
                                yaFinalizoPartida = true;
                                System.out.println("💀 Te has quedado sin intentos.");
                                // Aquí avisamos al servidor que perdí y como "RESULTADO:PERDI"
                                try {
                                    GameDataCliente.getConexion().enviar("RESULTADO:PERDI");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                        return;
                    }

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

        btnPreguntar = new JButton("💬 Hacer pregunta");
        btnPreguntar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPreguntar.addActionListener(e -> {
            if (esperandoRespuesta) {
                JOptionPane.showMessageDialog(this, "Debes esperar la respuesta del oponente.");
                return;
            }


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

            if (!esMiTurno) {
                JOptionPane.showMessageDialog(this, "Espera tu turno.");
                return;
            }

            if (preguntaFinal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona o escribe una pregunta válida.");
                return;
            }

            try {
                GameDataCliente.getConexion().enviar("PREGUNTA:" + preguntaFinal);
                areaChat.append("Tú: " + preguntaFinal + "\n");
                esperandoRespuesta = true; // 🔒 bloquea nuevas preguntas
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
            esperandoRespuesta = false;
            preguntaPendiente = null;
            btnPreguntar.setEnabled(esMiTurno);
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSi.setEnabled(false);
        btnNo.setEnabled(false);
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

    private void reproducirSonido(String ruta) {
        try {
            File archivo = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void iniciarMusica() {
        try {
            File archivo = new File("assets/musica/Undertale.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
            musicaClip = AudioSystem.getClip();
            musicaClip.open(audioStream);
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicaClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void toggleMusica() {
        if (musicaClip == null) return;

        if (musicaActiva) {
            musicaClip.stop();
            btnMusica.setIcon(cargarYEscalar("C:/AdivinaQuien/assets/iconos/play.png", 32, 32));
        } else {
            musicaClip.start();
            musicaClip.loop(Clip.LOOP_CONTINUOUSLY);
            btnMusica.setIcon(cargarYEscalar("C:/AdivinaQuien/assets/iconos/pause.png", 32, 32));
        }

        musicaActiva = !musicaActiva;
    }


    private ImageIcon cargarYEscalar(String ruta, int ancho, int alto) {
        ImageIcon icono = new ImageIcon(ruta);
        Image img = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }



}
