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
 * Estrategia de IA que prioriza el ataque y el uso de las tropas más fuertes.
 */
public class EstrategiaAgresiva implements EstrategiaIA {

    private final Random generadorAleatorio = new Random();

    @Override
    public boolean debeIntentarJugarCarta(Jugador jugador, int tickActual) {
        // Una estrategia agresiva intenta jugar más a menudo, especialmente con elixir alto.
        if (jugador.getSistemaElixir().getElixirActual() >= 8) {
            return true; // Siempre intenta jugar si tiene mucho elixir.
        }
        // Probabilidad del 70% en otros casos para mantener la presión.
        return generadorAleatorio.nextDouble() < 0.7;
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

        // Criterio de agresividad: priorizar la carta que invoca la tropa con más daño.
        return Collections.max(cartasJugables, Comparator.comparing(carta -> {
            // Obtenemos el prototipo de la tropa para ver sus estadísticas sin crear un objeto nuevo.
            Tropa tropaPrototipo = FactoriaTropas.getPrototipo(carta.getNombre());
            if (tropaPrototipo != null) {
                return tropaPrototipo.getDanioAtaque();
            }
            return 0; // Si no es una tropa, tiene 0 de daño para este cálculo.
        }));
    }

    @Override
    public Posicion seleccionarPosicionDespliegue(Jugador jugador, Tablero tablero) {
        List<Posicion> zonasValidas = jugador.getZonaDespliegue().obtenerPosicionesValidas();
        if (zonasValidas.isEmpty()) {
            return null;
        }

        // 1. Encontrar la coordenada 'y' de la fila más adelantada.
        final int yAdelantada;
        if (jugador.getId() == 1) { // Jugador 1 (abajo) busca la Y máxima.
            yAdelantada = zonasValidas.stream().mapToInt(Posicion::getY).max().orElse(-1);
        } else { // Jugador 2 (arriba) busca la Y mínima.
            yAdelantada = zonasValidas.stream().mapToInt(Posicion::getY).min().orElse(-1);
        }

        if (yAdelantada == -1) {
            return null; // No debería ocurrir si la lista no está vacía.
        }

        // 2. Obtener todas las posiciones en esa fila en una lista MODIFICABLE.
        List<Posicion> lineaAdelantada = zonasValidas.stream()
                .filter(p -> p.getY() == yAdelantada)
                .collect(Collectors.toList());

        // 3. Barajar las posiciones para elegir una al azar.
        Collections.shuffle(lineaAdelantada, generadorAleatorio);

        // 4. Encontrar la primera posición barajada que esté libre en el tablero.
        return lineaAdelantada.stream()
                .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                .findFirst()
                .orElse(null); // Si toda la fila está ocupada, no se despliega.
    }
}
