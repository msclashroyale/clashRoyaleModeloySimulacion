package analisis;

/**
 * Estadísticas detalladas de un jugador en una partida específica
 */
public class EstadisticasPartidaJugador {
    private final int jugadorId;
    
    // Cartas y elixir
    private int cartasJugadas;
    private int elixirGastado;
    private int elixirDesperdiciado; // Cuando alcanza el máximo
    
    // Tropas
    private int tropasInvocadas;
    private int tropasMuertas;
    
    // Combate
    private int ataqueRealizados;
    private int dañoCausado;
    private int dañoRecibido;
    
    // Torres
    private int torresDestruidas;
    private int dañoATorres;
    
    // Timing
    private int primerCartaSegundo;
    private int ultimaCartaSegundo;
    
    public EstadisticasPartidaJugador(int jugadorId) {
        this.jugadorId = jugadorId;
        this.primerCartaSegundo = -1;
    }
    
    // Métodos para incrementar estadísticas
    public void registrarCartaJugada(int segundo, int costoElixir) {
        this.cartasJugadas++;
        this.elixirGastado += costoElixir;
        this.ultimaCartaSegundo = segundo;
        
        if (primerCartaSegundo == -1) {
            primerCartaSegundo = segundo;
        }
    }
    
    public void registrarTropaInvocada() {
        this.tropasInvocadas++;
    }
    
    public void registrarTropaMuerta() {
        this.tropasMuertas++;
    }
    
    public void registrarAtaque(int danio) {
        this.ataqueRealizados++;
        this.dañoCausado += danio;
    }
    
    public void registrarDañoRecibido(int danio) {
        this.dañoRecibido += danio;
    }
    
    public void registrarTorreDestruida() {
        this.torresDestruidas++;
    }
    
    public void registrarDañoATorre(int danio) {
        this.dañoATorres += danio;
    }
    
    public void registrarElixirDesperdiciado(int cantidad) {
        this.elixirDesperdiciado += cantidad;
    }
    
    // Getters
    public int getJugadorId() { return jugadorId; }
    public int getCartasJugadas() { return cartasJugadas; }
    public int getElixirGastado() { return elixirGastado; }
    public int getElixirDesperdiciado() { return elixirDesperdiciado; }
    public int getTropasInvocadas() { return tropasInvocadas; }
    public int getTropasMuertas() { return tropasMuertas; }
    public int getAtaqueRealizados() { return ataqueRealizados; }
    public int getDañoCausado() { return dañoCausado; }
    public int getDañoRecibido() { return dañoRecibido; }
    public int getTorresDestruidas() { return torresDestruidas; }
    public int getDañoATorres() { return dañoATorres; }
    public int getPrimerCartaSegundo() { return primerCartaSegundo; }
    public int getUltimaCartaSegundo() { return ultimaCartaSegundo; }
    
    public double getPromedioElixirPorCarta() {
        return cartasJugadas > 0 ? (double) elixirGastado / cartasJugadas : 0;
    }
    
    public double getRatioDañoCausadoRecibido() {
        return dañoRecibido > 0 ? (double) dañoCausado / dañoRecibido : 0;
    }
}
