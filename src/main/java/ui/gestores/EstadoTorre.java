package ui.gestores;

import java.util.Objects;

/**
 * Clase auxiliar para cachear el estado de las torres
 */
public class EstadoTorre {
    private final int vidaActual;
    private final int vidaMaxima;

    public EstadoTorre(int vidaActual, int vidaMaxima) {
        this.vidaActual = vidaActual;
        this.vidaMaxima = vidaMaxima;
    }

    public int getVidaActual() {
        return vidaActual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EstadoTorre that = (EstadoTorre) obj;
        return vidaActual == that.vidaActual && vidaMaxima == that.vidaMaxima;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vidaActual, vidaMaxima);
    }
}