package tablero;

import entidades.edificios.Torre;
import entidades.edificios.TorreRey;
import entidades.edificios.TorrePrincesa;
import entidades.tropas.Tropa;
import entidades.base.EntidadJuego;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase que representa el tablero de juego
 */
public class Tablero {

    // Constantes del tablero (basadas en la imagen del juego)
    public static final int ANCHO = 18;
    public static final int ALTO = 32;

    // Posiciones del río y puentes
    public static final int RIO_Y1 = 15;
    public static final int RIO_Y2 = 16;
    public static final int PUENTE_X1 = 3;
    public static final int PUENTE_X2 = 14;

    // Posiciones fijas de torres (esquina inferior de cada cuadrado)
    private static final Posicion TORRE_REY_J1 = new Posicion(7, 1);
    private static final Posicion TORRE_REY_J2 = new Posicion(7, 27);
    private static final Posicion TORRE_PRINCESA_IZQ_J1 = new Posicion(2, 5);
    private static final Posicion TORRE_PRINCESA_DER_J1 = new Posicion(13, 5);
    private static final Posicion TORRE_PRINCESA_IZQ_J2 = new Posicion(2, 24);
    private static final Posicion TORRE_PRINCESA_DER_J2 = new Posicion(13, 24);

    // Estado del tablero
    private final TipoTerreno[][] terreno;
    private final List<Torre> torres;
    private final List<Tropa> tropas;
    private final ZonaDespliegue zonaDespliegueJ1;
    private final ZonaDespliegue zonaDespliegueJ2;

    public Tablero() {
        this.terreno = new TipoTerreno[ANCHO][ALTO];
        this.torres = new ArrayList<>();
        this.tropas = new ArrayList<>();
        this.zonaDespliegueJ1 = new ZonaDespliegue(1);
        this.zonaDespliegueJ2 = new ZonaDespliegue(2);

        inicializarTerreno();
    }

    /**
     * Inicializa el tablero con torres de ambos jugadores
     */
    public void inicializarConTorres(int nivelJugador1, int nivelJugador2) {
        limpiarTorres();
        colocarTorresJugador(1, nivelJugador1);
        colocarTorresJugador(2, nivelJugador2);
        actualizarZonasDespliegue();
        marcarTorresEnTerreno();
    }

    /**
     * Intenta desplegar una tropa en la posición especificada
     */
    public boolean desplegarTropa(Tropa tropa, int x, int y) {
        Posicion posicion = new Posicion(x, y);

        if (!puedeDesplegarTropa(tropa.getJugadorId(), posicion)) {
            return false;
        }

        tropa.setPosicion(posicion);
        tropas.add(tropa);
        return true;
    }

    /**
     * Verifica si se puede desplegar una tropa en la posición
     */
    public boolean puedeDesplegarTropa(int jugadorId, Posicion posicion) {
        // Verificar límites del tablero
        if (!esPosicionValida(posicion)) {
            return false;
        }

        // Verificar que el terreno sea transitable
        if (!esTerranoTransitable(posicion)) {
            return false;
        }

        // Verificar que no haya otra entidad
        if (hayEntidadEnPosicion(posicion)) {
            return false;
        }

        // Verificar zona de despliegue
        ZonaDespliegue zona = (jugadorId == 1) ? zonaDespliegueJ1 : zonaDespliegueJ2;
        return zona.puedeDesplegarEn(posicion);
    }

    /**
     * Limpia todas las entidades muertas del tablero
     */
    public void limpiarEntidadesMuertas() {
        tropas.removeIf(tropa -> !tropa.estaViva());
        // Las torres no se remueven, solo cambian su estado
    }

    /**
     * Busca la entidad más cercana de un tipo específico
     */
    public EntidadJuego buscarEntidadMasCercana(Posicion origen, int jugadorObjetivo, Class<?> tipoEntidad) {
        EntidadJuego entidadMasCercana = null;
        double menorDistancia = Double.MAX_VALUE;

        // Buscar en tropas
        if (Tropa.class.isAssignableFrom(tipoEntidad) || tipoEntidad == EntidadJuego.class) {
            for (Tropa tropa : tropas) {
                if (tropa.getJugadorId() == jugadorObjetivo && tropa.estaViva()) {
                    double distancia = origen.calcularDistancia(tropa.getPosicion());
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        entidadMasCercana = tropa;
                    }
                }
            }
        }

        // Buscar en torres
        if (Torre.class.isAssignableFrom(tipoEntidad) || tipoEntidad == EntidadJuego.class) {
            for (Torre torre : torres) {
                if (torre.getJugadorId() == jugadorObjetivo && torre.estaViva()) {
                    double distancia = origen.calcularDistancia(torre.getPosicion());
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        entidadMasCercana = torre;
                    }
                }
            }
        }

        return entidadMasCercana;
    }

    // ==========================================
    // MÉTODOS DE CONSULTA
    // ==========================================

    public int contarTropasVivas(int jugadorId) {
        return (int) tropas.stream()
                .filter(tropa -> tropa.getJugadorId() == jugadorId && tropa.estaViva())
                .count();
    }

    public int contarTorresVivas(int jugadorId) {
        return (int) torres.stream()
                .filter(torre -> torre.getJugadorId() == jugadorId && torre.estaViva())
                .count();
    }

    public boolean torreReyViva(int jugadorId) {
        return torres.stream()
                .filter(torre -> torre instanceof TorreRey && torre.getJugadorId() == jugadorId)
                .anyMatch(Torre::estaViva);
    }

    public int calcularVidaTotalTorres(int jugadorId) {
        return torres.stream()
                .filter(torre -> torre.getJugadorId() == jugadorId && torre.estaViva())
                .mapToInt(Torre::getVidaActual)
                .sum();
    }

    public Tropa obtenerTropaEnPosicion(Posicion posicion) {
        return tropas.stream()
                .filter(tropa -> tropa.getPosicion().equals(posicion) && tropa.estaViva())
                .findFirst()
                .orElse(null);
    }

    public Torre obtenerTorreEnPosicion(Posicion posicion) {
        return torres.stream()
                .filter(torre -> torre.ocupaPosicion(posicion))
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // MÉTODOS PRIVADOS
    // ==========================================

    private void inicializarTerreno() {
        // Llenar con terreno vacío
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                terreno[x][y] = TipoTerreno.VACIO;
            }
        }

        // Marcar río
        for (int x = 0; x < ANCHO; x++) {
            terreno[x][RIO_Y1] = TipoTerreno.RIO;
            terreno[x][RIO_Y2] = TipoTerreno.RIO;
        }

        // Marcar puentes
        terreno[PUENTE_X1][RIO_Y1] = TipoTerreno.PUENTE;
        terreno[PUENTE_X1][RIO_Y2] = TipoTerreno.PUENTE;
        terreno[PUENTE_X2][RIO_Y1] = TipoTerreno.PUENTE;
        terreno[PUENTE_X2][RIO_Y2] = TipoTerreno.PUENTE;
    }

    private void limpiarTorres() {
        torres.clear();
        // Limpiar marcas de torres en el terreno
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                if (terreno[x][y] == TipoTerreno.TORRE_REY ||
                        terreno[x][y] == TipoTerreno.TORRE_PRINCESA) {
                    terreno[x][y] = TipoTerreno.VACIO;
                }
            }
        }
    }

    private void colocarTorresJugador(int jugadorId, int nivel) {
        if (jugadorId == 1) {
            torres.add(new TorreRey(TORRE_REY_J1, nivel, jugadorId));
            torres.add(new TorrePrincesa(TORRE_PRINCESA_IZQ_J1, nivel, jugadorId));
            torres.add(new TorrePrincesa(TORRE_PRINCESA_DER_J1, nivel, jugadorId));
        } else {
            torres.add(new TorreRey(TORRE_REY_J2, nivel, jugadorId));
            torres.add(new TorrePrincesa(TORRE_PRINCESA_IZQ_J2, nivel, jugadorId));
            torres.add(new TorrePrincesa(TORRE_PRINCESA_DER_J2, nivel, jugadorId));
        }
    }

    private void marcarTorresEnTerreno() {
        for (Torre torre : torres) {
            TipoTerreno tipo = (torre instanceof TorreRey) ?
                    TipoTerreno.TORRE_REY : TipoTerreno.TORRE_PRINCESA;

            for (Posicion pos : torre.getPosicionesOcupadas()) {
                if (esPosicionValida(pos)) {
                    terreno[pos.getX()][pos.getY()] = tipo;
                }
            }
        }
    }

    private void actualizarZonasDespliegue() {
        // Zona inicial del jugador 1 (parte superior)
        zonaDespliegueJ1.definirZonaInicial(0, 0, ANCHO-1, 14);

        // Zona inicial del jugador 2 (parte inferior)
        zonaDespliegueJ2.definirZonaInicial(0, 17, ANCHO-1, ALTO-1);

        // TODO: Expandir zonas cuando se destruyan torres princesa
    }

    private boolean esPosicionValida(Posicion posicion) {
        return posicion.getX() >= 0 && posicion.getX() < ANCHO &&
                posicion.getY() >= 0 && posicion.getY() < ALTO;
    }

    private boolean esTerranoTransitable(Posicion posicion) {
        if (!esPosicionValida(posicion)) {
            return false;
        }
        return terreno[posicion.getX()][posicion.getY()].esTransitable();
    }

    private boolean hayEntidadEnPosicion(Posicion posicion) {
        return obtenerTropaEnPosicion(posicion) != null ||
                obtenerTorreEnPosicion(posicion) != null;
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public List<Tropa> getTropas() {
        return new ArrayList<>(tropas);
    }

    public List<Tropa> getTropasJugador(int jugadorId) {
        return tropas.stream()
                .filter(tropa -> tropa.getJugadorId() == jugadorId)
                .toList();
    }

    public List<Torre> getTorres() {
        return new ArrayList<>(torres);
    }

    public List<Torre> getTorresJugador(int jugadorId) {
        return torres.stream()
                .filter(torre -> torre.getJugadorId() == jugadorId)
                .collect(Collectors.toList());
    }

    public TipoTerreno getTipoTerreno(int x, int y) {
        if (x < 0 || x >= ANCHO || y < 0 || y >= ALTO) {
            return null;
        }
        return terreno[x][y];
    }

    public ZonaDespliegue getZonaDespliegue(int jugadorId) {
        return (jugadorId == 1) ? zonaDespliegueJ1 : zonaDespliegueJ2;
    }
}
