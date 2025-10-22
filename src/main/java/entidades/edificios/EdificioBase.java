package entidades.edificios;

import entidades.base.EntidadJuego;
import tablero.Posicion;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase base para edificios que ahora recibe posición inferior izquierda
 */
public abstract class EdificioBase extends EntidadJuego {
    protected Posicion posicionInferiorIzquierda;  // Cambio: ahora almacena inferior izquierda
    protected int danioAtaque;
    protected int rangoAtaque;
    protected int ancho;
    protected int alto;

    public EdificioBase(Posicion posicionInferiorIzquierda, int vidaBase, int danioBase,
                        int nivel, int jugadorId, int ancho, int alto) {
        super(calcularPosicionCentral(posicionInferiorIzquierda, ancho, alto), 0, nivel, jugadorId);
        this.posicionInferiorIzquierda = posicionInferiorIzquierda;
        this.ancho = ancho;
        this.alto = alto;

        // Calcular estadísticas basadas en el nivel
        calcularEstadisticasPorNivel(vidaBase, danioBase);
        this.vidaActual = this.vidaMaxima;
        this.rangoAtaque = 5; // Valor por defecto
    }

    /**
     * Convierte posición inferior izquierda a posición central
     */
    private static Posicion calcularPosicionCentral(Posicion inferiorIzquierda, int ancho, int alto) {
        int centroX = inferiorIzquierda.getX() + (ancho - 1) / 2;
        int centroY = inferiorIzquierda.getY() + (alto - 1) / 2;
        return new Posicion(centroX, centroY);
    }

    /**
     * Convierte posición central a posición inferior izquierda
     */
    public static Posicion convertirCentralAInferiorIzquierda(Posicion central, int ancho, int alto) {
        int inferiorIzquierdaX = central.getX() - (ancho - 1) / 2;
        int inferiorIzquierdaY = central.getY() - (alto - 1) / 2;
        return new Posicion(inferiorIzquierdaX, inferiorIzquierdaY);
    }

    /**
     * Verifica si el edificio ocupa una posición específica
     */
    public boolean ocupaPosicion(Posicion posicion) {
        int minX = posicionInferiorIzquierda.getX();
        int maxX = minX + ancho - 1;
        int minY = posicionInferiorIzquierda.getY();
        int maxY = minY + alto - 1;

        return posicion.getX() >= minX && posicion.getX() <= maxX &&
                posicion.getY() >= minY && posicion.getY() <= maxY;
    }

    /**
     * Obtiene todas las posiciones que ocupa el edificio
     */
    public List<Posicion> getPosicionesOcupadas() {
        List<Posicion> posiciones = new ArrayList<>();

        for (int x = posicionInferiorIzquierda.getX(); x < posicionInferiorIzquierda.getX() + ancho; x++) {
            for (int y = posicionInferiorIzquierda.getY(); y < posicionInferiorIzquierda.getY() + alto; y++) {
                posiciones.add(new Posicion(x, y));
            }
        }

        return posiciones;
    }

    /**
     * Obtiene la posición principal del edificio (por compatibilidad)
     * Por defecto retorna la posición central
     */
    @Override
    public Posicion getPosicion() {
        return super.getPosicion();
    }

    // Métodos abstractos
    protected abstract void calcularEstadisticasPorNivel(int vidaBase, int danioBase);

    // Getters y setters
    public Posicion getPosicionInferiorIzquierda() {
        return posicionInferiorIzquierda;
    }

    public Posicion getPosicionCentral() {
        return super.getPosicion();
    }

    public void setPosicionInferiorIzquierda(Posicion nuevaPosicion) {
        this.posicionInferiorIzquierda = nuevaPosicion;
        this.posicion = calcularPosicionCentral(nuevaPosicion, ancho, alto);
    }

    public int getDanioAtaque() {
        return danioAtaque;
    }

    public int getRangoAtaque() {
        return rangoAtaque;
    }

    @Override
    public int getAncho() {
        return ancho;
    }

    @Override
    public int getAlto() {
        return alto;
    }
}