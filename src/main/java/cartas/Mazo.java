package cartas;

import java.util.*;

public class Mazo {
    private List<Carta> cartas;
    private List<Carta> mano;
    private Random random;
    private static final int TAMAÑO_MAZO = 8;
    private static final int TAMAÑO_MANO = 4;

    public Mazo() {
        this.random = new Random();
        this.cartas = generarMazoAleatorio();
        this.mano = new ArrayList<>();
        inicializarMano();
    }

    public Mazo(List<Carta> cartasPersonalizadas) {
        this.random = new Random();
        this.cartas = new ArrayList<>(cartasPersonalizadas.subList(0, Math.min(TAMAÑO_MAZO, cartasPersonalizadas.size())));
        this.mano = new ArrayList<>();
        inicializarMano();
    }

    /**
     * Método estático para crear mazo por defecto (requerido por Jugador)
     */
    public static Mazo crearMazoPorDefecto() {
        return new Mazo();
    }

    private List<Carta> generarMazoAleatorio() {
        List<Carta> todasLasCartas = new ArrayList<>(GestorCartas.getInstance().getCartasTropas());
        Collections.shuffle(todasLasCartas);
        return new ArrayList<>(todasLasCartas.subList(0, Math.min(TAMAÑO_MAZO, todasLasCartas.size())));
    }

    private void inicializarMano() {
        List<Carta> cartasBarajadas = new ArrayList<>(cartas);
        Collections.shuffle(cartasBarajadas);

        for (int i = 0; i < Math.min(TAMAÑO_MANO, cartasBarajadas.size()); i++) {
            mano.add(cartasBarajadas.get(i));
        }
    }

    /**
     * Busca una carta específica en la mano (requerido por Jugador)
     */
    public Carta buscarCartaEnMano(String nombreCarta) {
        return mano.stream()
                .filter(carta -> carta.getNombre().equalsIgnoreCase(nombreCarta))
                .findFirst()
                .orElse(null);
    }

    /**
     * Juega una carta específica (requerido por Jugador)
     */
    public void jugarCarta(Carta cartaJugada) {
        if (mano.remove(cartaJugada)) {
            // Agregar nueva carta a la mano
            agregarNuevaCartaAMano();
        }
    }

    /**
     * Juega una carta aleatoria (método original)
     */
    public Carta jugarCartaAleatoria() {
        if (mano.isEmpty()) {
            reiniciarMano();
        }

        if (mano.isEmpty()) {
            return null;
        }

        Carta cartaJugada = mano.remove(random.nextInt(mano.size()));
        agregarNuevaCartaAMano();
        return cartaJugada;
    }

    /**
     * Obtiene una carta aleatoria sin jugarla
     */
    public Carta obtenerCartaAleatoria() {
        if (mano.isEmpty()) {
            reiniciarMano();
        }
        if (mano.isEmpty()) {
            return null;
        }
        return mano.get(random.nextInt(mano.size()));
    }

    /**
     * Actualiza el mazo (requerido por Jugador)
     */
    public void actualizar(int tickActual) {
        // Por ahora no hace nada, pero está disponible para futuras funcionalidades
        // como rotar cartas automáticamente o efectos especiales
    }

    private void agregarNuevaCartaAMano() {
        if (mano.size() < TAMAÑO_MANO) {
            List<Carta> cartasDisponibles = new ArrayList<>(cartas);
            // No remover las que están en mano para permitir duplicados
            if (!cartasDisponibles.isEmpty()) {
                mano.add(cartasDisponibles.get(random.nextInt(cartasDisponibles.size())));
            }
        }
    }

    private void reiniciarMano() {
        mano.clear();
        inicializarMano();
    }

    // Getters
    public List<Carta> getCartas() { return new ArrayList<>(cartas); }

    /**
     * Método renombrado para compatibilidad con Jugador
     */
    public List<Carta> getCartasEnMano() { return new ArrayList<>(mano); }

    /**
     * Método original mantenido para compatibilidad
     */
    public List<Carta> getMano() { return new ArrayList<>(mano); }

    public double getCostoPromedioElixir() {
        return cartas.stream().mapToInt(Carta::getCostoElixir).average().orElse(0.0);
    }
}