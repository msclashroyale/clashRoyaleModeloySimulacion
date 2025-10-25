package analisis;

/**
 * Representa un evento que ocurrió durante la partida
 */
public class EventoPartida {
    private final TipoEvento tipo;
    private final int segundo;
    private final int jugadorId;
    private final String detalles;
    
    public EventoPartida(TipoEvento tipo, int segundo, int jugadorId, String detalles) {
        this.tipo = tipo;
        this.segundo = segundo;
        this.jugadorId = jugadorId;
        this.detalles = detalles;
    }
    
    public enum TipoEvento {
        CARTA_JUGADA,
        TROPA_DESPLEGADA,
        TROPA_MUERTA,
        ATAQUE_REALIZADO,
        TORRE_DESTRUIDA,
        PARTIDA_TERMINADA
    }
    
    // Getters
    public TipoEvento getTipo() { return tipo; }
    public int getSegundo() { return segundo; }
    public int getJugadorId() { return jugadorId; }
    public String getDetalles() { return detalles; }
    
    @Override
    public String toString() {
        return String.format("[%ds] J%d - %s: %s", segundo, jugadorId, tipo, detalles);
    }
}
