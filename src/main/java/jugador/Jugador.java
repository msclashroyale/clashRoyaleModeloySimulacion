package jugador;

import cartas.Carta;
import cartas.Mazo;
import tablero.Tablero;
import tablero.Posicion;
import entidades.tropas.Tropa;
import factoria.FactoriaTropas;
import tablero.ZonaDespliegue;

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
    private final ZonaDespliegue zonaDespliegue;


    /**
     * Constructor con mazo por defecto
     */
    public Jugador(int id, String nombre, int nivel) {
        this(id, nombre, nivel, Mazo.crearMazoPorDefecto(), new EstrategiaMenorCosto());
    }

    /**
     * Constructor con mazo personalizado y estrategia por defecto
     */
    public Jugador(int id, String nombre, int nivel, Mazo mazoPersonalizado) {
        this(id, nombre, nivel, mazoPersonalizado, new EstrategiaMenorCosto());
    }

    /**
     * Constructor con mazo por defecto y estrategia personalizada
     */
    public Jugador(int id, String nombre, int nivel, EstrategiaIA estrategia) {
        this(id, nombre, nivel, Mazo.crearMazoPorDefecto(), estrategia);
    }

    /**
     * Constructor principal que permite inyectar todas las dependencias.
     */
    public Jugador(int id, String nombre, int nivel, Mazo mazo, EstrategiaIA estrategia) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.mazo = mazo;
        this.sistemaElixir = new SistemaElixir();
        this.estadisticas = new EstadisticasJugador();
        this.estrategiaIA = estrategia;
        this.zonaDespliegue = new ZonaDespliegue(id);
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
    public Tropa intentarDesplegarCarta(String nombreCarta, int x, int y, Tablero tablero) {
        Carta carta = mazo.buscarCartaEnMano(nombreCarta);
        if (carta == null) {
            return null;
        }

        // Verificar si tiene suficiente elixir
        if (!sistemaElixir.puedeGastar(carta.getCostoElixir())) {
            return null;
        }

        // Verificar si puede desplegar en esa posición
        if (!tablero.puedeDesplegarTropa(this, new Posicion(x, y))) {
            return null;
        }

        // Crear la tropa/entidad
        Tropa tropa = FactoriaTropas.crearTropa(nombreCarta, new Posicion(x, y), nivel, id);
        if (tropa == null) {
            return null;
        }

        // Desplegar en el tablero
        if (tablero.desplegarTropa(this, tropa, x, y)) {
            // Consumir recursos
            sistemaElixir.gastar(carta.getCostoElixir());
            mazo.jugarCarta(carta);

            // Actualizar estadísticas
            estadisticas.incrementarCartasJugadas();
            estadisticas.incrementarElixirGastado(carta.getCostoElixir());
            estadisticas.incrementarTropasInvocadas();

            return tropa;
        }

        return null;
    }



    /**
     * Lógica para que la IA juegue una carta.
     * Ahora la decisión de SI jugar se toma aquí.
     */
    public Tropa jugarCartaIA(Tablero tablero, int tickActual) {
        if (!estrategiaIA.debeIntentarJugarCarta(this, tickActual)) {
            return null; // La IA decide no jugar en este tick
        }

        // 1. Seleccionar la carta a jugar
        Carta carta = estrategiaIA.seleccionarCartaParaJugar(this);
        if (carta == null || carta.getCostoElixir() > sistemaElixir.getElixirActual()) {
            return null; // No hay carta jugable o no hay elixir
        }

        // 2. Seleccionar la posición de despliegue
        Posicion posicion = estrategiaIA.seleccionarPosicionDespliegue(this, tablero);
        if (posicion == null) {
            return null; // No se encontró una posición válida
        }

        // 3. Intentar desplegar la carta y devolver la tropa desplegada
        return intentarDesplegarCarta(carta.getNombre(), posicion.getX(), posicion.getY(), tablero);
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
    public ZonaDespliegue getZonaDespliegue() { return zonaDespliegue; }
    public EstrategiaIA getEstrategiaIA() { return estrategiaIA; }

    @Override
    public String toString() {
        return String.format("Jugador[%d: %s, Nv.%d]", id, nombre, nivel);
    }
}