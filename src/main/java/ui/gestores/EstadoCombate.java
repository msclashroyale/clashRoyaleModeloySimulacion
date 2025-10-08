package ui.gestores;

import ui.TipoAnimacion;

/**
 * Clase auxiliar para manejar el estado de combate
 */
public class EstadoCombate {
    private final boolean atacando;
    private final boolean recibiendoDanio;

    public EstadoCombate(boolean atacando, boolean recibiendoDanio) {
        this.atacando = atacando;
        this.recibiendoDanio = recibiendoDanio;
    }

    public boolean necesitaAnimacion() {
        return atacando || recibiendoDanio;
    }

    public TipoAnimacion obtenerTipoAnimacion() {
        if (atacando && recibiendoDanio) {
            return TipoAnimacion.AMBOS;
        } else if (atacando) {
            return TipoAnimacion.ATACANDO;
        } else {
            return TipoAnimacion.RECIBIENDO_DANIO;
        }
    }
}