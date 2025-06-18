package modelo;

import java.io.Serializable;

public class Personaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombre;
    private String rutaImagen;

    public Personaje(String nombre, String rutaImagen) {
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    @Override
    public String toString() {
        return nombre + " -> " + rutaImagen;
    }
}
