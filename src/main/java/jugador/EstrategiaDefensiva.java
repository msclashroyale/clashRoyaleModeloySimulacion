package jugador;

import cartas.Carta;
import factoria.FactoriaTropas;
import tablero.Posicion;
import tablero.Tablero;
import entidades.tropas.Tropa;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Estrategia de IA que prioriza la defensa y el uso de las tropas más resistentes.
 */
public class EstrategiaDefensiva implements EstrategiaIA {

    private final Random generadorAleatorio = new Random();

    @Override
    public boolean debeIntentarJugarCarta(Jugador jugador, int tickActual) {
        // Una estrategia defensiva es más conservadora, juega con menos frecuencia.
        if (jugador.getSistemaElixir().getElixirActual() < 5) {
            return false; // No jugar si tiene poco elixir.
        }
        // Probabilidad del 50% en otros casos para guardar elixir.
        return generadorAleatorio.nextDouble() < 0.5;
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

        // Criterio de defensa: priorizar la carta que invoca la tropa con más vida (más resistente).
        return Collections.max(cartasJugables, Comparator.comparing(carta -> {
            Tropa tropaPrototipo = FactoriaTropas.getPrototipo(carta.getNombre());
            if (tropaPrototipo != null) {
                return tropaPrototipo.getVidaMaxima();
            }
            return 0;
        }));
    }

    @Override
    public Posicion seleccionarPosicionDespliegue(Jugador jugador, Tablero tablero) {
        List<Posicion> zonasValidas = jugador.getZonaDespliegue().obtenerPosicionesValidas();
        if (zonasValidas.isEmpty()) {
            return null;
        }

        // Criterio de defensa: desplegar cerca de la torre del rey para protegerla.
        // La posición de la torre del rey depende del jugador.
        Posicion torreDelRey = (jugador.getId() == 1) ? new Posicion(8, 2) : new Posicion(8, 28);

        // De todas las posiciones válidas y libres, encontrar la más cercana a la torre del rey.
        return zonasValidas.stream()
                .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                .min(Comparator.comparingDouble(p -> p.calcularDistancia(torreDelRey)))
                .orElse(null);
    }
}
