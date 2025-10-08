package entidades.edificios;

import entidades.tropas.Tropa;
import tablero.Posicion;


public class TorreRey extends Torre {
    private static final int VIDA_BASE = 2400;
    private static final int DANIO_BASE = 50;
    private static final int ANCHO = 4;
    private static final int ALTO = 4;
    private int ultimoTickAtaque = 0;
    private static final int VELOCIDAD_ATAQUE = 3; // ticks

    // CONSTRUCTOR MODIFICADO: ahora recibe posición inferior izquierda
    public TorreRey(Posicion posicionInferiorIzquierda, int nivel, int jugadorId) {
        super(posicionInferiorIzquierda, VIDA_BASE, DANIO_BASE, nivel, jugadorId, ANCHO, ALTO);
    }

    // Constructor de conveniencia para migrar código existente que use posición central
    public static TorreRey crearDesdePosicionCentral(Posicion posicionCentral, int nivel, int jugadorId) {
        Posicion inferiorIzquierda = EdificioBase.convertirCentralAInferiorIzquierda(posicionCentral, ANCHO, ALTO);
        return new TorreRey(inferiorIzquierda, nivel, jugadorId);
    }

    @Override
    protected void calcularEstadisticasPorNivel(int vidaBase, int danioBase) {
        // Fórmula original mantenida
        double multiplicador = 1.0 + (nivel - 1) * 0.1;

        this.vidaMaxima = (int) (vidaBase * multiplicador);
        this.danioAtaque = (int) (danioBase * multiplicador);
    }

    @Override
    public char getSimboloConsola() {
        return jugadorId == 1 ? 'R' : 'r';
    }

    public static int getVidaBasePorNivel(int nivel) {
        double multiplicador = 1.0 + (nivel - 1) * 0.1;
        return (int) (VIDA_BASE * multiplicador);
    }

    public static int getDanioBasePorNivel(int nivel) {
        double multiplicador = 1.0 + (nivel - 1) * 0.1;
        return (int) (DANIO_BASE * multiplicador);
    }

    @Override
    public boolean puedeAtacar(int tickActual) {
        return estaViva && (tickActual - ultimoTickAtaque >= VELOCIDAD_ATAQUE);
    }

    @Override
    public int atacar(Tropa objetivo, int tickActual) {
        if (puedeAtacar(tickActual)) {
            ultimoTickAtaque = tickActual;
            return danioAtaque;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Torre Rey (Jugador " + jugadorId + ") [" +
                "Pos Inf-Izq: " + posicionInferiorIzquierda +
                ", Pos Central: " + posicion +
                ", Vida: " + vidaActual + "/" + vidaMaxima +
                ", Daño: " + danioAtaque +
                ", Nivel: " + nivel +
                ", Tamaño: " + ancho + "x" + alto + "]";
    }
}