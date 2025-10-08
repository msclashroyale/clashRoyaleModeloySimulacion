package jugador;

public class EstadisticasJugador {
    private int cartasJugadas;
    private int elixirGastado;
    private int tropasInvocadas;
    private int dañoCausado;
    private int dañoRecibido;

    public EstadisticasJugador() {
        this.cartasJugadas = 0;
        this.elixirGastado = 0;
        this.tropasInvocadas = 0;
        this.dañoCausado = 0;
        this.dañoRecibido = 0;
    }

    // Métodos para incrementar estadísticas
    public void incrementarCartasJugadas() {
        this.cartasJugadas++;
    }

    public void incrementarElixirGastado(int cantidad) {
        this.elixirGastado += cantidad;
    }

    public void incrementarTropasInvocadas() {
        this.tropasInvocadas++;
    }

    public void incrementarDañoCausado(int danio) {
        this.dañoCausado += danio;
    }

    public void incrementarDañoRecibido(int danio) {
        this.dañoRecibido += danio;
    }

    // Getters
    public int getCartasJugadas() { return cartasJugadas; }
    public int getElixirGastado() { return elixirGastado; }
    public int getTropasInvocadas() { return tropasInvocadas; }
    public int getDañoCausado() { return dañoCausado; }
    public int getDañoRecibido() { return dañoRecibido; }

    @Override
    public String toString() {
        return String.format("Cartas: %d | Elixir: %d | Tropas: %d | Daño C/R: %d/%d",
                cartasJugadas, elixirGastado, tropasInvocadas, dañoCausado, dañoRecibido);
    }
}