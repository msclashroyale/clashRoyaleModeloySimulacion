// ==========================================
// ESTRATEGIA IA REFACTORIZADA
// ==========================================
package jugador;

import cartas.Carta;
import tablero.Tablero;
import tablero.Posicion;
import java.util.Random;
import java.util.List;

/**
 * Clase refactorizada que maneja las estrategias de IA para los jugadores
 * Mantiene toda la lógica inteligente original pero con mejor organización
 */
public class EstrategiaIA {

    private final Random generadorAleatorio;

    // Constantes de estrategia
    private static final double PROBABILIDAD_BASE = 0.8;           // 80% base por tick
    private static final double PROBABILIDAD_ELIXIR_ALTO = 0.95;    // 95% si tiene mucho elixir
    private static final double PROBABILIDAD_ELIXIR_MEDIO = 0.85;   // 85% si tiene elixir medio
    private static final double MULTIPLICADOR_FINAL_PARTIDA = 1.3; // Más agresivo al final

    private static final int ELIXIR_ALTO = 7;
    private static final int ELIXIR_MEDIO = 4;
    private static final int TICK_FINAL_PARTIDA = 120; // Últimos 2 minutos
    private static final int MAX_INTENTOS_POSICION = 10;

    public EstrategiaIA() {
        this.generadorAleatorio = new Random();
    }

    /**
     * Determina si la IA debe intentar jugar una carta en este tick
     * Mantiene la lógica original con probabilidades dinámicas
     */
    public boolean debeIntentarJugarCarta(Jugador jugador, int tickActual) {
        double probabilidad = calcularProbabilidadJugar(jugador, tickActual);
        return generadorAleatorio.nextDouble() < probabilidad;
    }

    /**
     * Calcula la probabilidad de jugar una carta según el estado del jugador
     */
    private double calcularProbabilidadJugar(Jugador jugador, int tickActual) {
        double probabilidad = PROBABILIDAD_BASE;
        int elixirActual = jugador.getSistemaElixir().getElixirActual();

        // Ajustar según cantidad de elixir disponible
        if (elixirActual >= ELIXIR_ALTO) {
            probabilidad = PROBABILIDAD_ELIXIR_ALTO;
        } else if (elixirActual >= ELIXIR_MEDIO) {
            probabilidad = PROBABILIDAD_ELIXIR_MEDIO;
        }

        // Ser más agresivo en la fase final de la partida
        if (tickActual > TICK_FINAL_PARTIDA) {
            probabilidad *= MULTIPLICADOR_FINAL_PARTIDA;
        }

        // Limitar probabilidad máxima a 100%
        return Math.min(probabilidad, 1.0);
    }

    /**
     * Selecciona la mejor carta disponible para jugar
     * Mantiene la estrategia original de priorizar cartas pagables
     */
    public Carta seleccionarCartaParaJugar(Jugador jugador) {
        // Usar la lógica existente del jugador para obtener la mejor carta
        return jugador.obtenerCartaMasBarataDisponible();
    }

    /**
     * Selecciona una posición estratégica para desplegar una tropa
     * Mantiene la lógica original de posiciones aleatorias en zona propia
     */
    public Posicion seleccionarPosicionDespliegue(Jugador jugador, Tablero tablero) {
        List<Posicion> zonasValidas = tablero.getZonaDespliegue(jugador.getId())
                .obtenerPosicionesValidas();

        return buscarPosicionValidaAleatoria(zonasValidas, tablero);
    }

    /**
     * Busca una posición válida aleatoria dentro de las zonas permitidas
     */
    private Posicion buscarPosicionValidaAleatoria(List<Posicion> zonasPermitidas, Tablero tablero) {
        if (zonasPermitidas.isEmpty()) {
            return null;
        }

        // Intentar varias posiciones aleatorias
        for (int intento = 0; intento < MAX_INTENTOS_POSICION; intento++) {
            int indiceAleatorio = generadorAleatorio.nextInt(zonasPermitidas.size());
            Posicion candidata = zonasPermitidas.get(indiceAleatorio);

            // Verificar si es una posición libre y válida
            if (esPosicionLibreParaDespliegue(candidata, tablero)) {
                return candidata;
            }
        }

        // Si no encuentra posición después de varios intentos, usar fallback
        return obtenerPosicionFallback(zonasPermitidas);
    }

    /**
     * Verifica si una posición está libre para desplegar una tropa
     */
    private boolean esPosicionLibreParaDespliegue(Posicion posicion, Tablero tablero) {
        // Verificar que no hay tropas en la posición
        if (tablero.obtenerTropaEnPosicion(posicion) != null) {
            return false;
        }

        // Verificar que no hay torres en la posición
        if (tablero.obtenerTorreEnPosicion(posicion) != null) {
            return false;
        }

        // Verificar que el terreno es transitable
        return tablero.getTipoTerreno(posicion.getX(), posicion.getY()).esTransitable();
    }

    /**
     * Obtiene una posición de fallback si no encuentra posiciones libres
     */
    private Posicion obtenerPosicionFallback(List<Posicion> zonasPermitidas) {
        if (zonasPermitidas.isEmpty()) {
            return null;
        }

        // Usar la primera posición disponible como fallback
        return zonasPermitidas.get(0);
    }

    /**
     * Evalúa la situación táctica actual y ajusta la estrategia
     * NUEVA funcionalidad para futuras mejoras de IA
     */
    public EvaluacionTactica evaluarSituacionActual(Jugador jugador, Tablero tablero) {
        int tropasAliadas = tablero.contarTropasVivas(jugador.getId());
        int tropasEnemigas = tablero.contarTropasVivas(jugador.getId() == 1 ? 2 : 1);
        int torresAliadas = tablero.contarTorresVivas(jugador.getId());
        int torresEnemigas = tablero.contarTorresVivas(jugador.getId() == 1 ? 2 : 1);

        return new EvaluacionTactica(tropasAliadas, tropasEnemigas, torresAliadas, torresEnemigas);
    }

    /**
     * Clase interna para almacenar evaluación táctica
     */
    public static class EvaluacionTactica {
        public final int tropasAliadas;
        public final int tropasEnemigas;
        public final int torresAliadas;
        public final int torresEnemigas;

        public EvaluacionTactica(int tropasAliadas, int tropasEnemigas,
                                 int torresAliadas, int torresEnemigas) {
            this.tropasAliadas = tropasAliadas;
            this.tropasEnemigas = tropasEnemigas;
            this.torresAliadas = torresAliadas;
            this.torresEnemigas = torresEnemigas;
        }

        public boolean estaEnVentaja() {
            return tropasAliadas > tropasEnemigas || torresAliadas > torresEnemigas;
        }

        public boolean estaEnDesventaja() {
            return tropasAliadas < tropasEnemigas || torresAliadas < torresEnemigas;
        }

        @Override
        public String toString() {
            return String.format("Evaluación[Tropas: %d vs %d, Torres: %d vs %d]",
                    tropasAliadas, tropasEnemigas, torresAliadas, torresEnemigas);
        }
    }

    /**
     * Método para configurar estrategias personalizadas
     * NUEVA funcionalidad extensible
     */
    public void configurarEstrategia(ConfiguracionEstrategia config) {
        // TODO: Implementar cuando se necesiten estrategias más sofisticadas
        // Por ahora mantiene la estrategia original
    }

    /**
     * Clase para configuraciones futuras de estrategia
     */
    public static class ConfiguracionEstrategia {
        public final double agresividad;
        public final boolean priorizarDefensa;
        public final boolean priorizarAtaque;

        public ConfiguracionEstrategia(double agresividad, boolean priorizarDefensa, boolean priorizarAtaque) {
            this.agresividad = agresividad;
            this.priorizarDefensa = priorizarDefensa;
            this.priorizarAtaque = priorizarAtaque;
        }

        public static ConfiguracionEstrategia equilibrada() {
            return new ConfiguracionEstrategia(0.5, false, false);
        }

        public static ConfiguracionEstrategia agresiva() {
            return new ConfiguracionEstrategia(0.8, false, true);
        }

        public static ConfiguracionEstrategia defensiva() {
            return new ConfiguracionEstrategia(0.3, true, false);
        }
    }
}