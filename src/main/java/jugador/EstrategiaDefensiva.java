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
import java.util.stream.Collectors;

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

        // Criterio de defensa: desplegar en las 3 filas traseras.
        final int yBase = (jugador.getId() == 1) ? 0 : Tablero.ALTO - 1;
        final int yLimite = (jugador.getId() == 1) ? yBase + 3 : yBase - 3;

        // 1. Filtrar posiciones en las filas traseras que estén libres.
        List<Posicion> posicionesDefensivas = zonasValidas.stream()
                .filter(p -> {
                    if (jugador.getId() == 1) {
                        return p.getY() < yLimite;
                    } else {
                        return p.getY() > yLimite;
                    }
                })
                .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                .collect(Collectors.toList());

        if (posicionesDefensivas.isEmpty()) {
            // Fallback: si no hay espacio atrás, usar cualquier posición libre para no bloquear a la IA.
            posicionesDefensivas = zonasValidas.stream()
                    .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                    .collect(Collectors.toList());
            if (posicionesDefensivas.isEmpty()) return null;
        }

        // 2. Barajar las posiciones defensivas y elegir una al azar.
        Collections.shuffle(posicionesDefensivas, generadorAleatorio);
        return posicionesDefensivas.get(0);
    }
}
