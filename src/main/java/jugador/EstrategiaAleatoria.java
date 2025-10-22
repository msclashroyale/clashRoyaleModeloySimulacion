package jugador;

import cartas.Carta;
import tablero.Posicion;
import tablero.Tablero;

import java.util.List;
import java.util.Random;

/**
 * Estrategia de IA que juega de forma completamente aleatoria.
 * Útil como línea base para comparar la efectividad de otras estrategias.
 */
public class EstrategiaAleatoria implements EstrategiaIA {

    private final Random generadorAleatorio = new Random();

    @Override
    public boolean debeIntentarJugarCarta(Jugador jugador, int tickActual) {
        // 50% de probabilidad de intentar jugar si tiene al menos 2 de elixir.
        if (jugador.getSistemaElixir().getElixirActual() >= 2) {
            return generadorAleatorio.nextDouble() < 0.5;
        }
        return false;
    }

    @Override
    public Carta seleccionarCartaParaJugar(Jugador jugador) {
        List<Carta> mano = jugador.getMazo().getCartasEnMano();

        // Filtrar solo las cartas que el jugador puede pagar.
        List<Carta> cartasJugables = mano.stream()
                .filter(c -> jugador.getSistemaElixir().puedeGastar(c.getCostoElixir()))
                .toList();

        if (cartasJugables.isEmpty()) {
            return null;
        }

        // Seleccionar una carta jugable al azar.
        return cartasJugables.get(generadorAleatorio.nextInt(cartasJugables.size()));
    }

    @Override
    public Posicion seleccionarPosicionDespliegue(Jugador jugador, Tablero tablero) {
        List<Posicion> zonasValidas = jugador.getZonaDespliegue().obtenerPosicionesValidas();
        if (zonasValidas.isEmpty()) {
            return null;
        }

        // Encontrar todas las posiciones que están realmente libres.
        List<Posicion> posicionesLibres = zonasValidas.stream()
                .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                .toList();

        if (posicionesLibres.isEmpty()) {
            return null;
        }

        // Seleccionar una posición libre al azar.
        return posicionesLibres.get(generadorAleatorio.nextInt(posicionesLibres.size()));
    }
}
