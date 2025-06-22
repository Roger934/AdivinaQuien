package interfaz;

import logica.RegistroPartida;
import logica.TableroControlador;
import modelo.Personaje;
import utils.GameDataCliente;
import logica.PreguntaControlador;

import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    // Mensajitos de error
    private JLabel lblMensajeArriba;
    private JLabel lblMensajeAbajo;


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
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("assets/fondos/header.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(1280, 180));
        panel.setOpaque(false);

        // Fuente
        Font fuenteGeneral = new Font("Segoe UI Emoji", Font.BOLD, 20);
        Color colorTexto = Color.WHITE;

        // --- Panel de arriba: botón música, fecha y tiempo ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelTop.setOpaque(false);

        btnMusica = new JButton(cargarYEscalar("assets/iconos/pause.png", 35, 35));
        btnMusica.setPreferredSize(new Dimension(35, 35));
        btnMusica.setFocusPainted(false);
        btnMusica.setContentAreaFilled(false);
        btnMusica.setBorderPainted(false);
        btnMusica.setToolTipText("Pausar/Reproducir música");
        btnMusica.addActionListener(e -> toggleMusica());

        JLabel lblFecha = new JLabel("📅 " + java.time.LocalDate.now());
        lblFecha.setFont(fuenteGeneral);
        lblFecha.setForeground(colorTexto);

        lblTimer = new JLabel("⏱ Tiempo: 00:00");
        lblTimer.setFont(fuenteGeneral);
        lblTimer.setForeground(colorTexto);

        panelTop.add(btnMusica);
        panelTop.add(Box.createHorizontalStrut(10));
        panelTop.add(lblFecha);
        panelTop.add(Box.createHorizontalStrut(10));
        panelTop.add(lblTimer);

        // --- Panel inferior: nombres de jugadores ---
        JPanel panelNombres = new JPanel(new GridLayout(1, 2));
        panelNombres.setOpaque(false);
        panelNombres.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));

        // Jugador
        JPanel panelJugador = new JPanel();
        panelJugador.setOpaque(false);
        panelJugador.setLayout(new BoxLayout(panelJugador, BoxLayout.Y_AXIS));

        JLabel lblJugador = new JLabel("JUGADOR: " + GameDataCliente.getNombreJugador(), SwingConstants.CENTER);
        lblJugador.setFont(fuenteGeneral);
        lblJugador.setForeground(colorTexto);
        lblJugador.setAlignmentX(Component.CENTER_ALIGNMENT);

        vidasPanel = crearPanelVidas(vidas);
        panelJugador.add(lblJugador);
        panelJugador.add(Box.createVerticalStrut(5));
        panelJugador.add(vidasPanel);

        // Rival
        JPanel panelRival = new JPanel();
        panelRival.setOpaque(false);
        JLabel lblRival = new JLabel("RIVAL: " + GameDataCliente.getNombreRival(), SwingConstants.CENTER);
        lblRival.setFont(fuenteGeneral);
        lblRival.setForeground(colorTexto);

        panelRival.add(lblRival);

        // Añadir a contenedor
        panelNombres.add(panelJugador);
        panelNombres.add(panelRival);

        // Añadir todo al header
        panel.add(Box.createVerticalStrut(5));
        panel.add(panelTop);
        panel.add(Box.createVerticalStrut(15));
        panel.add(panelNombres);

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
        panelIzquierdo.setBackground(Color.WHITE);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        nombreSel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nombreSel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelSeleccion.add(imagenSel);
        panelSeleccion.add(nombreSel);
        panelIzquierdo.add(Box.createVerticalStrut(10));
        panelIzquierdo.add(panelSeleccion);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        // Botón principal
        JButton btnAdivinar = crearBotonRedondo("Adivinar personaje", new Color(106, 27, 154), Color.WHITE);
        btnAdivinar.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnAdivinar.addActionListener(e -> {
            if (yaAdivinoEsteTurno) {
                mostrarMensajeTemporal(lblMensajeArriba, "Solo puedes adivinar una vez por turno.", 3000);
                return;
            }

            if (!personajeYaElegido) {
                mostrarMensajeTemporal(lblMensajeArriba, "Primero selecciona tu personaje secreto.", 3000);
                return;
            }

            if (!esMiTurno) {
                mostrarMensajeTemporal(lblMensajeArriba, "Espera tu turno-", 3000);
                return;
            }

            mostrarMensajeTemporal(lblMensajeArriba, "Selecciona un personaje del tablero.", 3000);
            modoAdivinar = true;
            yaAdivinoEsteTurno = true;
        });

        panelIzquierdo.add(btnAdivinar);
        panelIzquierdo.add(Box.createVerticalStrut(10));

        // Mensaje entre adivinar y botones de abajo
        lblMensajeArriba = new JLabel(" ");
        lblMensajeArriba.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblMensajeArriba.setForeground(new Color(100, 0, 180));
        lblMensajeArriba.setPreferredSize(new Dimension(220, 24));
        lblMensajeArriba.setMaximumSize(new Dimension(220, 24));
        lblMensajeArriba.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelIzquierdo.add(lblMensajeArriba);
        panelIzquierdo.add(Box.createVerticalStrut(15));

        // Botones inferiores
        btnDesdeTablero = crearBotonRedondo("Desde tablero", new Color(26, 35, 126), Color.WHITE);
        btnDesdeLista = crearBotonRedondo("Desde lista", new Color(198, 40, 40), Color.WHITE);
        btnAleatorio = crearBotonRedondo("Aleatorio", new Color(239, 108, 0), Color.WHITE);

        ActionListener activar = e -> {
            activarSeleccion();
            mostrarMensajeTemporal(lblMensajeAbajo, "Tablero activado para selección.", 2500);
        };

        btnDesdeTablero.addActionListener(activar);
        btnDesdeLista.addActionListener(e -> seleccionarDesdeLista());
        btnAleatorio.addActionListener(e -> seleccionarAleatoriamente());

        for (JButton b : new JButton[]{btnDesdeTablero, btnDesdeLista, btnAleatorio}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelIzquierdo.add(Box.createVerticalStrut(10));
            panelIzquierdo.add(b);
        }

        // Mensaje debajo de los 3 botones
        panelIzquierdo.add(Box.createVerticalStrut(15));
        lblMensajeAbajo = new JLabel(" ");
        lblMensajeAbajo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblMensajeAbajo.setForeground(new Color(50, 50, 50));
        lblMensajeAbajo.setPreferredSize(new Dimension(220, 24));
        lblMensajeAbajo.setMaximumSize(new Dimension(220, 24));
        lblMensajeAbajo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelIzquierdo.add(lblMensajeAbajo);

        return panelIzquierdo;
    }


    private JPanel crearTableroVisual(List<Personaje> personajes) {
        JPanel tableroPanel = new JPanel(new GridLayout(4, 6, 10, 10));
        tableroPanel.setBackground(new Color(230, 240, 250));
        tableroPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Map<JLabel, ImageIcon> mapaOriginales = new HashMap<>();

        for (Personaje p : personajes) {
            JPanel celda = new JPanel(new BorderLayout());
            celda.setBackground(Color.WHITE);
            celda.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(180, 180, 180), 2, true),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            ImageIcon iconoOriginal = new ImageIcon(p.getRutaImagen());
            Image imgEsc = iconoOriginal.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            ImageIcon iconoEscalado = new ImageIcon(imgEsc);
            JLabel imagen = new JLabel(iconoEscalado, SwingConstants.CENTER);
            imagen.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel nombre = new JLabel(p.getNombre(), SwingConstants.CENTER);
            nombre.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nombre.setForeground(new Color(50, 50, 50));

            mapaOriginales.put(imagen, iconoEscalado);

            celda.add(imagen, BorderLayout.CENTER);
            celda.add(nombre, BorderLayout.SOUTH);

            // Hover visual
            celda.addMouseListener(new MouseAdapter() {
                Color original = celda.getBackground();

                @Override
                public void mouseEntered(MouseEvent e) {
                    celda.setBackground(new Color(220, 235, 250));
                    celda.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    celda.setBackground(original);
                }
            });

            imagen.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (modoAdivinar) {
                        modoAdivinar = false;

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
                        }

                        String adivinanza = p.getNombre().trim();
                        if (adivinanza.equalsIgnoreCase(personajeRival)) {
                            System.out.println("✅ ¡Has adivinado correctamente!");
                            yaFinalizoPartida = true;
                            try {
                                GameDataCliente.getConexion().enviar("RESULTADO:GANE");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            vidas--;
                            System.out.println("❌ Fallaste. Te quedan " + vidas + " vidas.");
                            reproducirSonido("assets/sonidos/vida.wav");
                            actualizarVidasVisuales();
                            if (vidas <= 0) {
                                yaFinalizoPartida = true;
                                System.out.println("💀 Te has quedado sin intentos.");
                                try {
                                    GameDataCliente.getConexion().enviar("RESULTADO:PERDI");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    GameDataCliente.getConexion().enviar("RESPUESTA:ADIVINANZA_FALLIDA");
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
                            Image img = icono.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
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
        panel.setPreferredSize(new Dimension(300, 550));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Preguntar"));

        // --- 1. Chat superior ---
        areaChat = new JTextArea();
        areaChat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaChat.setEditable(false);
        areaChat.setLineWrap(true);
        areaChat.setWrapStyleWord(true);
        JScrollPane scrollChat = new JScrollPane(areaChat);
        scrollChat.setPreferredSize(new Dimension(280, 130));
        panel.add(scrollChat, BorderLayout.NORTH);

        // --- 2. Preguntas ---
        JPanel panelPreguntas = new JPanel();
        panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
        panelPreguntas.setOpaque(false);
        panelPreguntas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl1 = new JLabel("Elegir pregunta:");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelPreguntas.add(lbl1);

        JComboBox<String> comboPreguntas = new JComboBox<>();
        comboPreguntas.addItem("--- Seleccione una pregunta ---");
        for (String pregunta : PreguntaControlador.obtenerPreguntas()) {
            comboPreguntas.addItem(pregunta);
        }
        comboPreguntas.setMaximumSize(new Dimension(250, 30));
        panelPreguntas.add(comboPreguntas);
        panelPreguntas.add(Box.createVerticalStrut(10));

        JLabel lbl2 = new JLabel("O escribe una:");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelPreguntas.add(lbl2);

        JTextField campoPreguntaLibre = new JTextField();
        campoPreguntaLibre.setMaximumSize(new Dimension(250, 30));
        panelPreguntas.add(campoPreguntaLibre);
        panelPreguntas.add(Box.createVerticalStrut(15));

        btnPreguntar = new JButton("Hacer pregunta");
        btnPreguntar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPreguntar.setBackground(new Color(51, 102, 255));
        btnPreguntar.setForeground(Color.WHITE);
        btnPreguntar.setFocusPainted(false);
        btnPreguntar.setBorder(BorderFactory.createLineBorder(new Color(30, 60, 200)));
        btnPreguntar.setMaximumSize(new Dimension(250, 35));
        btnPreguntar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelPreguntas.add(btnPreguntar);
        panelPreguntas.add(Box.createVerticalStrut(10));

        // --- Label para mensajes debajo del botón ---
        JLabel lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMensaje.setForeground(Color.RED);
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setMaximumSize(new Dimension(250, 30));
        panelPreguntas.add(lblMensaje);

        panel.add(panelPreguntas, BorderLayout.CENTER);

        // --- 3. Botones de respuesta ---
        JPanel panelRespuestas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelRespuestas.setOpaque(false);

        btnSi = new JButton("Sí");
        btnNo = new JButton("No");

        for (JButton b : new JButton[]{btnSi, btnNo}) {
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setPreferredSize(new Dimension(100, 35));
            b.setFocusPainted(false);
            b.setEnabled(false);
        }

        btnSi.setBackground(new Color(2, 151, 5));
        btnSi.setForeground(Color.WHITE);

        btnNo.setBackground(new Color(220, 20, 60));
        btnNo.setForeground(Color.WHITE);

        panelRespuestas.add(btnSi);
        panelRespuestas.add(btnNo);

        panel.add(panelRespuestas, BorderLayout.SOUTH);

        // --- Lógica del botón preguntar ---
        btnPreguntar.addActionListener(e -> {
            if (esperandoRespuesta) {
                mostrarMensajeTemp(lblMensaje, "Debes esperar la respuesta del oponente.");
                return;
            }

            if (!personajeYaElegido) {
                mostrarMensajeTemp(lblMensaje, "Primero selecciona tu personaje.");
                return;
            }

            if (GameDataCliente.getPersonajeRival() == null) {
                mostrarMensajeTemp(lblMensaje, "El rival aún no ha seleccionado su personaje.");
                return;
            }

            String seleccionCombo = (String) comboPreguntas.getSelectedItem();
            String preguntaLibre = campoPreguntaLibre.getText().trim();
            String preguntaFinal = !preguntaLibre.isEmpty() ? preguntaLibre :
                    ("--- Seleccione una pregunta ---".equals(seleccionCombo) ? "" : seleccionCombo);

            if (!esMiTurno) {
                mostrarMensajeTemp(lblMensaje, "Espera tu turno.");
                return;
            }

            if (preguntaFinal.isEmpty()) {
                mostrarMensajeTemp(lblMensaje, "Selecciona o escribe una pregunta válida.");
                return;
            }

            try {
                GameDataCliente.getConexion().enviar("PREGUNTA:" + preguntaFinal);
                areaChat.append("Tú: " + preguntaFinal + "\n");
                esperandoRespuesta = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnSi.addActionListener(e -> responder("SÍ", areaChat, btnSi, btnNo));
        btnNo.addActionListener(e -> responder("NO", areaChat, btnSi, btnNo));

        return panel;
    }

    // Método para mostrar mensajes temporales
    private void mostrarMensajeTemp(JLabel lbl, String mensaje) {
        lbl.setText(mensaje);
        Timer t = new Timer(3000, evt -> lbl.setText(" "));
        t.setRepeats(false);
        t.start();
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
        new Thread(() -> {
            try {
                File archivo = new File(ruta);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivo);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();

                // Espera a que termine de reproducirse
                Thread.sleep(clip.getMicrosecondLength() / 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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

    // BOTONES_______________________________
    private JButton crearBotonRedondo(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto);
        boton.setMaximumSize(new Dimension(200, 40));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setForeground(textoColor);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setContentAreaFilled(true);
        boton.setBorder(BorderFactory.createLineBorder(fondo.darker(), 2, true));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    //----------Mensajes
    private void mostrarMensajeTemporal(JLabel label, String texto, int milisegundos) {
        label.setText(texto);
        Timer timer = new Timer(milisegundos, e -> label.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }


}
