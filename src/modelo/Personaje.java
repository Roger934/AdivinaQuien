package modelo;

import java.io.Serializable;

public class Personaje implements Serializable {

    private int id;
    private String nombre;
    private String rutaImagen;

    // Constructor vacío (necesario para algunas librerías o bases de datos)
    public Personaje() {
    }

    // Constructor completo
    public Personaje(int id, String nombre, String rutaImagen) {
        this.id = id;
        this.nombre = nombre;
        this.rutaImagen = rutaImagen;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    @Override
    public String toString() {
        return "Personaje{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", rutaImagen='" + rutaImagen + '\'' +
                '}';
    }
}
