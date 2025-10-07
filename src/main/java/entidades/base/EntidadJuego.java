package entidades.base;

import tablero.Posicion;

/**
 * Clase base abstracta para todas las entidades del juego
 * Refactorizada desde Entidad.java con nombres más claros
 */
public abstract class EntidadJuego {

    protected Posicion posicion;
    protected int vidaActual;
    protected int vidaMaxima;
    protected int nivel;
    protected int jugadorId; // 1 o 2
    protected boolean estaViva;

    public EntidadJuego(Posicion posicion, int vidaMaxima, int nivel, int jugadorId) {
        this.posicion = posicion;
        this.vidaMaxima = vidaMaxima;
        this.vidaActual = vidaMaxima;
        this.nivel = nivel;
        this.jugadorId = jugadorId;
        this.estaViva = true;
    }

    /**
     * La entidad recibe daño
     */
    public void recibirDanio(int danio) {
        if (!estaViva) return;

        this.vidaActual = Math.max(0, this.vidaActual - danio);
        if (this.vidaActual <= 0) {
            this.estaViva = false;
            alMorir();
        }
    }

    /**
     * Cura la entidad
     */
    public void curar(int cantidad) {
        if (!estaViva) return;
        this.vidaActual = Math.min(this.vidaMaxima, this.vidaActual + cantidad);
    }

    /**
     * Método llamado cuando la entidad muere
     * Para ser sobrescrito por subclases si necesitan lógica especial
     */
    protected void alMorir() {
        // Implementación por defecto vacía
    }

    /**
     * Verifica si la entidad está en rango de otra posición
     */
    public boolean estaEnRango(Posicion otraPosicion, double rango) {
        return posicion.calcularDistancia(otraPosicion) <= rango;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Posicion getPosicion() { return posicion; } // ✅ CORREGIDO: devuelve Posicion
    public void setPosicion(Posicion posicion) { this.posicion = posicion; }

    public int getVidaActual() { return vidaActual; }
    public int getVidaMaxima() { return vidaMaxima; }
    public int getNivel() { return nivel; }
    public int getJugadorId() { return jugadorId; }
    public boolean estaViva() { return estaViva; }

    public double getPorcentajeVida() {
        return vidaMaxima > 0 ? (double) vidaActual / vidaMaxima : 0.0;
    }

    // ==========================================
    // MÉTODOS ABSTRACTOS
    // ==========================================

    /**
     * Símbolo para mostrar en la consola
     */
    public abstract char getSimboloConsola();

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [Pos: " + posicion +
                ", Vida: " + vidaActual + "/" + vidaMaxima +
                ", Jugador: " + jugadorId +
                ", Vivo: " + estaViva + "]";
    }
}