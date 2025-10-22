package juego.events;

public class PartidaTerminadaEvent implements GameEvent {
    private final int ganadorId; // 0=empate, 1=jugador1, 2=jugador2
    private final String motivo;

    public PartidaTerminadaEvent(int ganadorId, String motivo) {
        this.ganadorId = ganadorId;
        this.motivo = motivo;
    }

    public int getGanadorId() {
        return ganadorId;
    }

    public String getMotivo() {
        return motivo;
    }
}
