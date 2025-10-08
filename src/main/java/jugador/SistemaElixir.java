package jugador;

public class SistemaElixir {
    private int elixirActual;
    private int elixirMaximo;
    private int ticksUltimaGeneracion;
    private static final int ELIXIR_INICIAL = 5;
    private static final int ELIXIR_MAXIMO_DEFAULT = 10;
    private static final int TICKS_POR_ELIXIR = 3; // Genera 1 elixir cada 3 ticks (3 segundos)

    public SistemaElixir() {
        this.elixirMaximo = ELIXIR_MAXIMO_DEFAULT;
        this.elixirActual = ELIXIR_INICIAL;
        this.ticksUltimaGeneracion = 0;
    }

    public void actualizar(int tickActual) {
        if (tickActual - ticksUltimaGeneracion >= TICKS_POR_ELIXIR) {
            if (elixirActual < elixirMaximo) {
                elixirActual++;
                ticksUltimaGeneracion = tickActual;
            }
        }
    }

    public boolean puedeGastar(int costo) {
        return elixirActual >= costo;
    }

    public boolean gastar(int costo) {
        if (puedeGastar(costo)) {
            elixirActual -= costo;
            return true;
        }
        return false;
    }

    public void regenerar(int cantidad) {
        elixirActual = Math.min(elixirMaximo, elixirActual + cantidad);
    }

    // Getters
    public int getElixirActual() { return elixirActual; }
    public int getElixirMaximo() { return elixirMaximo; }
    public double getPorcentajeElixir() { return (double) elixirActual / elixirMaximo; }
}