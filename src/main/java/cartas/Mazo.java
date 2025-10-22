package cartas;

import java.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa el conjunto de 8 cartas de un jugador y maneja la rotación de la mano.
 */
public class Mazo {
    private final List<Carta> mazoCompleto; // Las 8 cartas del mazo.
    private final List<Carta> mano;           // Las 4 cartas que se pueden jugar.
    private final List<Carta> ciclo;          // Las 4 cartas en espera.

    private static final int TAMAÑO_MAZO = 8;
    private static final int TAMAÑO_MANO = 4;

    /**
     * Constructor por defecto que crea un mazo aleatorio de 8 cartas únicas.
     */
    public Mazo() {
        List<Carta> todasLasCartas = new ArrayList<>(GestorCartas.getInstance().getCartasTropas());
        Collections.shuffle(todasLasCartas);
        this.mazoCompleto = new ArrayList<>(todasLasCartas.subList(0, Math.min(TAMAÑO_MAZO, todasLasCartas.size())));

        this.mano = new ArrayList<>(TAMAÑO_MANO);
        this.ciclo = new ArrayList<>();
        inicializarManoYCiclo();
    }

    /**
     * Constructor que usa una lista de cartas personalizada para formar el mazo.
     * @param cartasPersonalizadas La lista de cartas para el mazo.
     */
    public Mazo(List<Carta> cartasPersonalizadas) {
        if (cartasPersonalizadas.size() < TAMAÑO_MAZO) {
            throw new IllegalArgumentException("Se necesitan al menos " + TAMAÑO_MAZO + " cartas para un mazo.");
        }
        this.mazoCompleto = new ArrayList<>(cartasPersonalizadas.subList(0, TAMAÑO_MAZO));
        this.mano = new ArrayList<>(TAMAÑO_MANO);
        this.ciclo = new ArrayList<>();
        inicializarManoYCiclo();
    }

    /**
     * Método estático para crear un mazo por defecto, requerido por Jugador.
     */
    public static Mazo crearMazoPorDefecto() {
        return new Mazo();
    }

    /**
     * Baraja el mazo completo y reparte las cartas iniciales en la mano y en el ciclo.
     */
    private void inicializarManoYCiclo() {
        List<Carta> cartasBarajadas = new ArrayList<>(this.mazoCompleto);
        Collections.shuffle(cartasBarajadas);

        mano.clear();
        ciclo.clear();

        // Reparte las primeras 4 a la mano
        for (int i = 0; i < TAMAÑO_MANO; i++) {
            mano.add(cartasBarajadas.remove(0));
        }

        // Las restantes van al ciclo
        ciclo.addAll(cartasBarajadas);
    }

    /**
     * Busca una carta específica en la mano actual del jugador.
     */
    public Carta buscarCartaEnMano(String nombreCarta) {
        return mano.stream()
                .filter(carta -> carta.getNombre().equalsIgnoreCase(nombreCarta))
                .findFirst()
                .orElse(null);
    }

    /**
     * Procesa el juego de una carta: la saca de la mano y hace rotar el ciclo.
     */
    public void jugarCarta(Carta cartaJugada) {
        boolean cartaEstabaEnMano = mano.remove(cartaJugada);

        if (cartaEstabaEnMano) {
            // 1. La carta del principio del ciclo pasa a la mano.
            if (!ciclo.isEmpty()) {
                Carta siguienteCarta = ciclo.remove(0);
                mano.add(siguienteCarta);
            }
            // 2. La carta jugada se va al final del ciclo.
            ciclo.add(cartaJugada);
        }
    }

    /**
     * Actualiza el estado del mazo. No se usa por ahora pero se mantiene para el futuro.
     */
    public void actualizar(int tickActual) {
        // Para futuras funcionalidades como rotar cartas automáticamente.
    }

    // --- GETTERS ---

    /**
     * Devuelve una copia de la lista de 8 cartas del mazo.
     */
    public List<Carta> getMazoCompleto() {
        return new ArrayList<>(mazoCompleto);
    }

    /**
     * Devuelve una copia de la lista de 4 cartas en la mano.
     */
    public List<Carta> getCartasEnMano() {
        return new ArrayList<>(mano);
    }

    /**
     * Calcula el coste promedio de elixir de las 8 cartas del mazo.
     */
    public double getCostoPromedioElixir() {
        return mazoCompleto.stream().mapToInt(Carta::getCostoElixir).average().orElse(0.0);
    }
}