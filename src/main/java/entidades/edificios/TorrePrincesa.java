package entidades.edificios;

import entidades.tropas.Tropa;
import tablero.Posicion;

public class TorrePrincesa extends Torre {
    private static final int VIDA_BASE = 1400;
    private static final int DANIO_BASE = 30;
    private static final int ANCHO = 3;
    private static final int ALTO = 3;
    private int ultimoTickAtaque = 0;
    private static final int VELOCIDAD_ATAQUE = 2; // ticks

    // CONSTRUCTOR MODIFICADO: ahora recibe posición inferior izquierda
    public TorrePrincesa(Posicion posicionInferiorIzquierda, int nivel, int jugadorId) {
        super(posicionInferiorIzquierda, VIDA_BASE, DANIO_BASE, nivel, jugadorId, ANCHO, ALTO);
    }

    // Constructor de conveniencia para migrar código existente que usa posición central
    public static TorrePrincesa crearDesdePosicionCentral(Posicion posicionCentral, int nivel, int jugadorId) {
        Posicion inferiorIzquierda = EdificioBase.convertirCentralAInferiorIzquierda(posicionCentral, ANCHO, ALTO);
        return new TorrePrincesa(inferiorIzquierda, nivel, jugadorId);
    }

    @Override
    protected void calcularEstadisticasPorNivel(int vidaBase, int danioBase) {
        // Fórmula: statistical = base * (1 + (nivel-1) * 0.1)
        double multiplicador = 1.0 + (nivel - 1) * 0.1;

        this.vidaMaxima = (int) (vidaBase * multiplicador);
        this.danioAtaque = (int) (danioBase * multiplicador);
    }

    @Override
    public char getSimboloConsola() {
        // P para jugador 1, p para jugador 2
        return jugadorId == 1 ? 'P' : 'p';
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
        return "Torre Princesa (Jugador " + jugadorId + ") [" +
                "Pos Inf-Izq: " + posicionInferiorIzquierda +
                ", Pos Central: " + posicion +
                ", Vida: " + vidaActual + "/" + vidaMaxima +
                ", Daño: " + danioAtaque +
                ", Nivel: " + nivel +
                ", Tamaño: " + ancho + "x" + alto + "]";
    }
}