package jugador;

import cartas.Carta;
import cartas.Mazo;
import tablero.Tablero;
import tablero.Posicion;
import entidades.tropas.Tropa;
import factoria.FactoriaTropas;

/**
 * Clase Jugador refactorizada con responsabilidades más claras
 * Maneja elixir, cartas y estadísticas de un jugador
 */
public class Jugador {

    private final int id;
    private final String nombre;
    private final int nivel;

    // Componentes del jugador
    private final Mazo mazo;
    private final SistemaElixir sistemaElixir;
    private final EstadisticasJugador estadisticas;
    private final EstrategiaIA estrategiaIA;

    /**
     * Constructor con mazo por defecto
     */
    public Jugador(int id, String nombre, int nivel) {
        this(id, nombre, nivel, Mazo.crearMazoPorDefecto());
    }

    /**
     * Constructor con mazo personalizado
     */
    public Jugador(int id, String nombre, int nivel, Mazo mazoPersonalizado) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.mazo = mazoPersonalizado;
        this.sistemaElixir = new SistemaElixir();
        this.estadisticas = new EstadisticasJugador();
        this.estrategiaIA = new EstrategiaIA();
    }

    /**
     * Actualiza el estado del jugador (llamado cada tick)
     */
    public void actualizar(int tickActual) {
        sistemaElixir.actualizar(tickActual);
        mazo.actualizar(tickActual);
    }

    /**
     * Intenta desplegar una carta en el tablero
     */
    public boolean intentarDesplegarCarta(String nombreCarta, int x, int y, Tablero tablero) {
        Carta carta = mazo.buscarCartaEnMano(nombreCarta);
        if (carta == null) {
            return false;
        }

        // Verificar si tiene suficiente elixir
        if (!sistemaElixir.puedeGastar(carta.getCostoElixir())) {
            return false;
        }

        // Verificar si puede desplegar en esa posición
        if (!tablero.puedeDesplegarTropa(id, new Posicion(x, y))) {
            return false;
        }

        // Crear la tropa/entidad
        Tropa tropa = FactoriaTropas.crearTropa(nombreCarta, new Posicion(x, y), nivel, id);
        if (tropa == null) {
            return false;
        }

        // Desplegar en el tablero
        if (tablero.desplegarTropa(tropa, x, y)) {
            // Consumir recursos
            sistemaElixir.gastar(carta.getCostoElixir());
            mazo.jugarCarta(carta);

            // Actualizar estadísticas
            estadisticas.incrementarCartasJugadas();
            estadisticas.incrementarElixirGastado(carta.getCostoElixir());
            estadisticas.incrementarTropasInvocadas();

            return true;
        }

        return false;
    }



    /**
     * Lógica para que la IA juegue una carta.
     * Ahora la decisión de SI jugar se toma aquí.
     */
    public void jugarCartaIA(Tablero tablero, int tickActual) {
        if (!estrategiaIA.debeIntentarJugarCarta(this, tickActual)) {
            return; // La IA decide no jugar en este tick
        }

        // 1. Seleccionar la carta a jugar
        Carta carta = estrategiaIA.seleccionarCartaParaJugar(this);
        if (carta == null || carta.getCostoElixir() > sistemaElixir.getElixirActual()) {
            return; // No hay carta jugable o no hay elixir
        }

        // 2. Seleccionar la posición de despliegue
        Posicion posicion = estrategiaIA.seleccionarPosicionDespliegue(this, tablero);
        if (posicion == null) {
            return; // No se encontró una posición válida
        }

        // 3. Intentar desplegar la carta
        if (intentarDesplegarCarta(carta.getNombre(), posicion.getX(), posicion.getY(), tablero)) {
            System.out.println("IA Jugador " + id + " jugó " + carta.getNombre() + " en " + posicion);
        }
    }

    /**
     * Obtiene la carta más barata que se puede jugar
     * MÉTODO PÚBLICO para que la estrategia IA pueda usarlo
     */
    public Carta obtenerCartaMasBarataDisponible() {
        return mazo.getCartasEnMano().stream()
                .filter(carta -> sistemaElixir.puedeGastar(carta.getCostoElixir()))
                .min((c1, c2) -> Integer.compare(c1.getCostoElixir(), c2.getCostoElixir()))
                .orElse(null);
    }

    /**
     * Verifica si el jugador puede jugar alguna carta
     */
    public boolean puedeJugarAlgunaCarta() {
        return mazo.getCartasEnMano().stream()
                .anyMatch(carta -> sistemaElixir.puedeGastar(carta.getCostoElixir()));
    }

    /**
     * Obtiene información del estado actual del jugador
     */
    public String obtenerEstadoResumen() {
        return String.format("%s (Nv.%d) - Elixir: %d/%d - Cartas jugadas: %d",
                nombre, nivel,
                sistemaElixir.getElixirActual(),
                sistemaElixir.getElixirMaximo(),
                estadisticas.getCartasJugadas());
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getNivel() { return nivel; }
    public Mazo getMazo() { return mazo; }
    public SistemaElixir getSistemaElixir() { return sistemaElixir; }
    public EstadisticasJugador getEstadisticas() { return estadisticas; }

    @Override
    public String toString() {
        return String.format("Jugador[%d: %s, Nv.%d]", id, nombre, nivel);
    }
}