package cartas;

public class Carta {
    private String nombre;
    private int costoElixir;
    private TipoCarta tipo;
    private String imagenPath;

    public Carta(String nombre, int costoElixir, TipoCarta tipo, String imagenPath) {
        this.nombre = nombre;
        this.costoElixir = costoElixir;
        this.tipo = tipo;
        this.imagenPath = imagenPath;
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getCostoElixir() { return costoElixir; }
    public TipoCarta getTipo() { return tipo; }
    public String getImagenPath() { return imagenPath; }

    @Override
    public String toString() {
        return nombre + " (" + costoElixir + " elixir)";
    }
}
