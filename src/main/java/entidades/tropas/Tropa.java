package entidades.tropas;

import entidades.base.EntidadJuego;
import tablero.Posicion;

import java.util.List;

public class Tropa extends EntidadJuego {

    public enum TipoAtaque {
        INDIVIDUAL,
        AREA
    }

    public enum TipoObjetivo {
        ESTRUCTURAS,
        TROPAS_Y_ESTRUCTURAS
    }

    // Atributos de la Tropa
    private String nombre;
    private int costoElixir;
    private String imagenPath;

    // Atributos de Combate
    private int danioAtaque;
    private int rangoAtaque;
    private int cooldownAtaque;
    private int ticksUltimoAtaque;
    private TipoAtaque tipoAtaque;
    private TipoObjetivo tipoObjetivo;
    private int radioArea;

    // Atributos de Movimiento
    private int ticksParaMover;
    private EntidadJuego objetivo;

    public Tropa(String nombre, int vidaMaxima, int danioAtaque, int ticksParaMover, int rangoAtaque, int costoElixir,
                 TipoAtaque tipoAtaque, TipoObjetivo tipoObjetivo, int radioArea, String imagenPath,
                 Posicion posicion, int nivel, int jugadorId) {
        super(posicion, vidaMaxima, nivel, jugadorId);
        this.nombre = nombre;
        this.costoElixir = costoElixir;
        this.imagenPath = imagenPath;
        this.danioAtaque = danioAtaque;
        this.rangoAtaque = rangoAtaque;
        this.cooldownAtaque = 1; // Default, can be changed
        this.ticksUltimoAtaque = 0;
        this.tipoAtaque = tipoAtaque;
        this.tipoObjetivo = tipoObjetivo;
        this.radioArea = radioArea;
        this.ticksParaMover = ticksParaMover;
    }

    // Clone constructor
    public Tropa(Tropa otra, Posicion posicion, int nivel, int jugadorId) {
        super(posicion, otra.vidaMaxima, nivel, jugadorId);
        this.nombre = otra.nombre;
        this.costoElixir = otra.costoElixir;
        this.imagenPath = otra.imagenPath;
        this.danioAtaque = otra.danioAtaque;
        this.rangoAtaque = otra.rangoAtaque;
        this.cooldownAtaque = otra.cooldownAtaque;
        this.ticksUltimoAtaque = 0;
        this.tipoAtaque = otra.tipoAtaque;
        this.tipoObjetivo = otra.tipoObjetivo;
        this.radioArea = otra.radioArea;
        this.ticksParaMover = otra.ticksParaMover;
    }


    public int atacar(EntidadJuego objetivo, int tickActual) {
        if (!puedeAtacar(objetivo, tickActual)) {
            return 0;
        }
        this.ticksUltimoAtaque = tickActual;
        objetivo.recibirDanio(danioAtaque);
        return danioAtaque;
    }

    public boolean puedeAtacar(EntidadJuego objetivo, int tickActual) {
        if (!estaViva() || !objetivo.estaViva()) return false;
        if (tickActual - ticksUltimoAtaque < cooldownAtaque) return false;
        double distancia = getPosicion().calcularDistancia(objetivo.getPosicion());
        return distancia <= rangoAtaque;
    }

    public boolean estaEnRangoDeAtaque(Posicion posicionObjetivo) {
        if (posicionObjetivo == null) return false;
        return getPosicion().calcularDistancia(posicionObjetivo) <= rangoAtaque;
    }

    public EntidadJuego buscarEnemigoEnRango(tablero.Tablero tablero) {
        List<EntidadJuego> posiblesObjetivos = new java.util.ArrayList<>();
        if (tipoObjetivo == TipoObjetivo.TROPAS_Y_ESTRUCTURAS) {
            posiblesObjetivos.addAll(tablero.getTropas());
            posiblesObjetivos.addAll(tablero.getTorres());
        } else { // SOLO ESTRUCTURAS
            posiblesObjetivos.addAll(tablero.getTorres());
        }

        EntidadJuego enemigoMasCercano = null;
        double distanciaMinima = Double.MAX_VALUE;

        for (EntidadJuego enemigo : posiblesObjetivos) {
            if (enemigo.getJugadorId() != this.getJugadorId() && enemigo.estaViva()) {
                double distancia = this.getPosicion().calcularDistancia(enemigo.getPosicion());
                if (distancia <= this.rangoAtaque && distancia < distanciaMinima) {
                    distanciaMinima = distancia;
                    enemigoMasCercano = enemigo;
                }
            }
        }
        return enemigoMasCercano;
    }

    @Override
    public char getSimboloConsola() {
        return nombre.charAt(0);
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getCostoElixir() { return costoElixir; }
    public String getImagenPath() { return imagenPath; }
    public int getDanioAtaque() { return danioAtaque; }
    public int getRangoAtaque() { return rangoAtaque; }
    public int getCooldownAtaque() { return cooldownAtaque; }
    public TipoAtaque getTipoAtaque() { return tipoAtaque; }
    public TipoObjetivo getTipoObjetivo() { return tipoObjetivo; }
    public int getRadioArea() { return radioArea; }
    public int getTicksParaMover() { return ticksParaMover; }
    public EntidadJuego getObjetivo() { return objetivo; }
    public void setObjetivo(EntidadJuego objetivo) { this.objetivo = objetivo; }
}