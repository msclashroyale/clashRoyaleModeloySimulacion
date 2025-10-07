// ==========================================
// TORRE REFACTORIZADA - CON MÉTODOS FALTANTES
// ==========================================
package entidades.edificios;

import entidades.tropas.Tropa;
import tablero.Posicion;

/**
 * Clase Torre refactorizada que hereda de EdificioBase
 * MODIFICADA: ahora recibe posición inferior izquierda y tiene todos los métodos necesarios
 */
public abstract class Torre extends EdificioBase {

    public Torre(Posicion posicionInferiorIzquierda, int vidaBase, int danioBase,
                 int nivel, int jugadorId, int ancho, int alto) {
        super(posicionInferiorIzquierda, vidaBase, danioBase, nivel, jugadorId, ancho, alto);
    }

    public abstract boolean puedeAtacar(int tickActual);

    public abstract int atacar(Tropa objetivo, int tickActual);

    /**
     * Obtiene información detallada de la torre
     * ACTUALIZADA para mostrar ambas posiciones
     */
    public String obtenerInformacionDetallada() {
        String estado = estaViva ? "ACTIVA" : "DESTRUIDA";
        return String.format("%s (Jugador %d) - %s\n" +
                        "  Posición Inferior Izq: %s\n" +
                        "  Posición Central: %s\n" +
                        "  Vida: %d/%d (%.1f%%)\n" +
                        "  Daño: %d | Rango: %d\n" +
                        "  Nivel: %d | Tamaño: %dx%d",
                getClass().getSimpleName(), jugadorId, estado,
                posicionInferiorIzquierda,
                posicion,
                vidaActual, vidaMaxima, getPorcentajeVida() * 100,
                danioAtaque, rangoAtaque,
                nivel, ancho, alto);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " (Jugador " + jugadorId + ") [" +
                "Pos Inf-Izq: " + posicionInferiorIzquierda +
                ", Pos Central: " + posicion +
                ", Vida: " + vidaActual + "/" + vidaMaxima +
                ", Daño: " + danioAtaque +
                ", Nivel: " + nivel +
                ", Tamaño: " + ancho + "x" + alto + "]";
    }
}