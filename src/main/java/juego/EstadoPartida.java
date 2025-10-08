// ==========================================
// ESTADO DE LA PARTIDA
// ==========================================
package juego;

/**
 * Clase que mantiene el estado actual de la partida
 * Patrón State simplificado para controlar las transiciones de estado
 */
public class EstadoPartida {

    public enum FasePartida {
        NO_INICIADA,
        INICIALIZANDO,
        EN_JUEGO,
        OVERTIME,
        TERMINADA
    }

    private FasePartida faseActual;
    private boolean partidaPausada;
    private long tiempoInicio;

    public EstadoPartida() {
        this.faseActual = FasePartida.NO_INICIADA;
        this.partidaPausada = false;
        this.tiempoInicio = 0;
    }

    public void marcarComoInicializada() {
        if (faseActual == FasePartida.NO_INICIADA) {
            faseActual = FasePartida.INICIALIZANDO;
        }
    }

    public void iniciarJuego() {
        if (faseActual == FasePartida.INICIALIZANDO) {
            faseActual = FasePartida.EN_JUEGO;
            tiempoInicio = System.currentTimeMillis();
        }
    }

    public void entrarOvertime() {
        if (faseActual == FasePartida.EN_JUEGO) {
            faseActual = FasePartida.OVERTIME;
        }
    }

    public void terminarPartida() {
        faseActual = FasePartida.TERMINADA;
    }

    public void pausar() {
        partidaPausada = true;
    }

    public void reanudar() {
        partidaPausada = false;
    }

    // Métodos de consulta
    public boolean estaInicializada() {
        return faseActual != FasePartida.NO_INICIADA;
    }

    public boolean estaEnJuego() {
        return faseActual == FasePartida.EN_JUEGO || faseActual == FasePartida.OVERTIME;
    }

    public boolean estaTerminada() {
        return faseActual == FasePartida.TERMINADA;
    }

    public boolean estaPausada() {
        return partidaPausada;
    }

    public boolean estaEnOvertime() {
        return faseActual == FasePartida.OVERTIME;
    }

    public FasePartida getFaseActual() {
        return faseActual;
    }

    public long getTiempoTranscurrido() {
        if (tiempoInicio == 0) return 0;
        return (System.currentTimeMillis() - tiempoInicio) / 1000;
    }

    @Override
    public String toString() {
        return String.format("Estado[Fase: %s, Pausada: %s]", faseActual, partidaPausada);
    }
}