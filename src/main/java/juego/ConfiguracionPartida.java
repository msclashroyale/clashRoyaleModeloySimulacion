// ==========================================
// CONFIGURACIÓN DE LA PARTIDA
// ==========================================
package juego;

/**
 * Clase que encapsula toda la configuración necesaria para una partida
 * Simplifica la creación y hace el código más mantenible
 */
public class ConfiguracionPartida {

    // Configuración de tiempo (en segundos)
    public static final int TIEMPO_PARTIDA_DEFAULT = 180; // 3 minutos
    public static final int TIEMPO_OVERTIME_DEFAULT = 120; // 2 minutos

    // Configuración de jugadores
    public static final int NIVEL_DEFAULT = 5;

    private final int tiempoPartida;
    private final int tiempoOvertime;
    private final int nivelJugador1;
    private final int nivelJugador2;
    private final boolean modoIA;

    /**
     * Constructor con valores por defecto
     */
    public ConfiguracionPartida() {
        this(TIEMPO_PARTIDA_DEFAULT, TIEMPO_OVERTIME_DEFAULT,
                NIVEL_DEFAULT, NIVEL_DEFAULT, true);
    }

    /**
     * Constructor personalizado
     */
    public ConfiguracionPartida(int tiempoPartida, int tiempoOvertime,
                                int nivelJugador1, int nivelJugador2, boolean modoIA) {
        this.tiempoPartida = tiempoPartida;
        this.tiempoOvertime = tiempoOvertime;
        this.nivelJugador1 = nivelJugador1;
        this.nivelJugador2 = nivelJugador2;
        this.modoIA = modoIA;
    }

    /**
     * Factory method para partida rápida
     */
    public static ConfiguracionPartida partidaRapida() {
        return new ConfiguracionPartida(60, 30, 5, 5, true);
    }

    /**
     * Factory method para partida estándar
     */
    public static ConfiguracionPartida partidaEstandar() {
        return new ConfiguracionPartida();
    }

    // Getters
    public int getTiempoPartida() { return tiempoPartida; }
    public int getTiempoOvertime() { return tiempoOvertime; }
    public int getTiempoTotal() { return tiempoPartida + tiempoOvertime; }
    public int getNivelJugador1() { return nivelJugador1; }
    public int getNivelJugador2() { return nivelJugador2; }
    public boolean esModoIA() { return modoIA; }

    @Override
    public String toString() {
        return String.format("Configuración[Tiempo: %ds+%ds, Niveles: %d/%d, IA: %s]",
                tiempoPartida, tiempoOvertime, nivelJugador1, nivelJugador2, modoIA);
    }
}