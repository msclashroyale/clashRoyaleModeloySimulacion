package juego.events;

import entidades.base.EntidadJuego;
import entidades.tropas.Tropa;

public class AtaqueRealizadoEvent implements GameEvent {
    private final EntidadJuego atacante;
    private final EntidadJuego objetivo;
    private final int danio;

    public AtaqueRealizadoEvent(EntidadJuego atacante, EntidadJuego objetivo, int danio) {
        this.atacante = atacante;
        this.objetivo = objetivo;
        this.danio = danio;
    }

    public EntidadJuego getAtacante() {
        return atacante;
    }

    public EntidadJuego getObjetivo() {
        return objetivo;
    }

    public int getDanio() {
        return danio;
    }

    public String getNombreAtacante() {
        if (atacante instanceof Tropa) {
            return ((Tropa) atacante).getNombre();
        }
        return atacante.getClass().getSimpleName();
    }

    public String getNombreObjetivo() {
        if (objetivo instanceof Tropa) {
            return ((Tropa) objetivo).getNombre();
        }
        return objetivo.getClass().getSimpleName();
    }
}
