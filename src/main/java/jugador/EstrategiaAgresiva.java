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

        // Criterio de agresividad: encontrar el punto más "adelantado" en la zona de despliegue.
        Posicion puntoMasAdelantado = Collections.max(zonasValidas, Comparator.comparingInt(p -> {
            return (jugador.getId() == 1) ? p.getY() : -p.getY();
        }));

        // De todas las posiciones válidas, encontrar una que esté libre y sea la más cercana al punto adelantado.
        return zonasValidas.stream()
                .filter(p -> tablero.puedeDesplegarTropa(jugador, p))
                .min(Comparator.comparingDouble(p -> p.calcularDistancia(puntoMasAdelantado)))
                .orElse(null); // Si no hay ninguna posición libre, no se despliega nada.
    }
}
