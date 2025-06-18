package modelo;

import java.util.List;

public class TestPersonajes {
    public static void main(String[] args) {
        List<Personaje> lista = modelo.Personajes.obtenerTodos();
        for (Personaje p : lista) {
            System.out.println(p.getNombre() + " -> " + p.getRutaImagen());
        }
    }
}
