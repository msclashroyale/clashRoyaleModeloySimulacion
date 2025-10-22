package juego.events;

import entidades.tropas.Tropa;
import jugador.Jugador;
import tablero.Posicion;

public class TropaDesplegadaEvent implements GameEvent {
    private final Jugador jugador;
    private final Tropa tropa;
    private final Posicion posicion;

    public TropaDesplegadaEvent(Jugador jugador, Tropa tropa, Posicion posicion) {
        this.jugador = jugador;
        this.tropa = tropa;
        this.posicion = posicion;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Tropa getTropa() {
        return tropa;
    }

    public Posicion getPosicion() {
        return posicion;
    }
}
