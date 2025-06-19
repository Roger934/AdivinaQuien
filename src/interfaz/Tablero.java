package interfaz;

import logica.TableroControlador;
import modelo.Personaje;
import utils.GameDataCliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
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

    public Tablero(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        List<Integer> lista = GameDataCliente.getListaPersonajes();
        List<Personaje> personajes = TableroControlador.obtenerPersonajesDesdeBD(lista);

        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelIzquierdo = crearPanelIzquierdo();
        add(panelIzquierdo, BorderLayout.WEST);

        JPanel tableroPanel = crearTableroVisual(personajes);
        add(tableroPanel, BorderLayout.CENTER);
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

        JButton btnDesdeTablero = new JButton("🔘 Desde tablero");
        JButton btnDesdeLista = new JButton("📋 Desde lista");
        JButton btnAleatorio = new JButton("🎲 Aleatorio");

        ActionListener activar = e -> activarSeleccion();

        btnDesdeTablero.addActionListener(activar);
        btnDesdeLista.addActionListener(activar);
        btnAleatorio.addActionListener(activar);

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

                    if (tableroInteractivo && personajeYaElegido && !p.equals(GameDataCliente.getPersonajeSecreto())) {
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

        JOptionPane.showMessageDialog(ventana, "🎯 Personaje seleccionado: " + personaje.getNombre());

        // 🔥 Aquí puedes luego llamar a enviarPersonajeAlServidor()
    }

    private void enviarPersonajeAlServidor() {
        if (personajeSeleccionado != null) {
            // Lógica de envío usando GameDataCliente.getConexion().enviar(...)
        }
    }
}
