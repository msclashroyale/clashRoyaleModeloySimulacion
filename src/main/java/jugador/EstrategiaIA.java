package jugador;

import cartas.Carta;
import tablero.Posicion;
import tablero.Tablero;

/**
 * Interfaz para definir el comportamiento de una Inteligencia Artificial.
 * Permite implementar diferentes estrategias que pueden ser asignadas a un jugador.
 */
public interface EstrategiaIA {

    /**
     * Determina si la IA debe intentar jugar una carta en el tick actual.
     * @param jugador El jugador que controla la IA.
     * @param tickActual El tick actual de la partida.
     * @return true si debe intentar jugar, false en caso contrario.
     */
    boolean debeIntentarJugarCarta(Jugador jugador, int tickActual);

    /**
     * Selecciona la carta que la IA considera mejor para jugar en el momento actual.
     * @param jugador El jugador que controla la IA.
     * @return La carta seleccionada, o null si no hay ninguna carta jugable.
     */
    Carta seleccionarCartaParaJugar(Jugador jugador);

    /**
     * Selecciona la posición en el tablero donde se desplegará la carta.
     * @param jugador El jugador que controla la IA.
     * @param tablero El estado actual del tablero.
     * @return La posición de despliegue, o null si no se encuentra una posición válida.
     */
    Posicion seleccionarPosicionDespliegue(Jugador jugador, Tablero tablero);

}
