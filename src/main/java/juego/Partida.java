// ==========================================
// CLASE PRINCIPAL DE LA PARTIDA
// ==========================================
package juego;

import jugador.*;
import tablero.Tablero;
import combate.SistemaCombate;
import movimiento.SistemaMovimiento;
import entidades.edificios.Torre;
import entidades.edificios.TorrePrincesa;
import juego.events.EntidadDestruidaEvent;
import juego.events.GameEvent;
import juego.events.GameEventListener;
import juego.events.EventManager;
import entidades.base.EntidadJuego;
import entidades.tropas.Tropa;
import juego.events.TropaDesplegadaEvent;
import juego.events.PartidaTerminadaEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Clase principal que maneja el estado y flujo de una partida
 * Reemplaza al ControladorJuegoConCartas con responsabilidades más claras
 */
public class Partida implements GameEventListener {

    private final ConfiguracionPartida configuracion;
    private final EstadoPartida estado;

    // Componentes principales
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Tablero tablero;
    private final SistemaCombate sistemaCombate;
    private final SistemaMovimiento sistemaMovimiento;
    private final EventManager eventManager;

    // Temporizador y control de juego
    private int tickActual;
    private boolean partidaTerminada;
    private int ganador; // 0=empate, 1=jugador1, 2=jugador2

    public Partida(ConfiguracionPartida configuracion) {
        this.configuracion = configuracion;
        this.estado = new EstadoPartida();
        this.eventManager = new EventManager();

        // --- Selección Aleatoria de Estrategias ---
        List<EstrategiaIA> estrategias = Arrays.asList(
                new EstrategiaMenorCosto(),
                new EstrategiaAgresiva(),
                new EstrategiaDefensiva(),
                new EstrategiaAleatoria()
        );
        Random random = new Random();
        EstrategiaIA estrategiaJ1 = estrategias.get(random.nextInt(estrategias.size()));
        EstrategiaIA estrategiaJ2 = estrategias.get(random.nextInt(estrategias.size()));

        // ----------------------------------------

        // Crear jugadores con la estrategia asignada
        this.jugador1 = new Jugador(1, "Jugador 1", configuracion.getNivelJugador1(), estrategiaJ1);
        this.jugador2 = new Jugador(2, "Jugador 2", configuracion.getNivelJugador2(), estrategiaJ2);

        // Crear tablero y sistemas
        this.tablero = new Tablero();
        this.sistemaCombate = new SistemaCombate(tablero, eventManager);
        this.sistemaMovimiento = new SistemaMovimiento(tablero);

        this.tickActual = 0;
        this.partidaTerminada = false;
        this.ganador = 0;

        // Suscribirse a eventos
        eventManager.subscribe(EntidadDestruidaEvent.class, this);
        eventManager.subscribe(PartidaTerminadaEvent.class, this);
    }

    /**
     * Inicializa la partida colocando las torres y preparando el estado inicial
     */
    public void inicializar() {
        // Colocar torres en el tablero
        tablero.inicializarConTorres(
                configuracion.getNivelJugador1(),
                configuracion.getNivelJugador2()
        );

        // Definir zonas de despliegue iniciales
        jugador1.getZonaDespliegue().definirZonaInicial(0, 0, Tablero.ANCHO - 1, 14);
        jugador2.getZonaDespliegue().definirZonaInicial(0, 17, Tablero.ANCHO - 1, Tablero.ALTO - 1);

        // Marcar como inicializada
        estado.marcarComoInicializada();
    }

    /**
     * Ejecuta un tick de la partida (llamado cada segundo)
     */
    public void ejecutarTick() {
        if (partidaTerminada || !estado.estaInicializada()) {
            return;
        }

        tickActual++;

        // 1. Actualizar jugadores (elixir, etc.)
        jugador1.actualizar(tickActual);
        jugador2.actualizar(tickActual);

        // 2. Procesar decisiones de IA/jugadores
        procesarDecisionesJugadores();

        // 3. Actualizar movimiento de tropas
        sistemaMovimiento.actualizarTropas(tickActual);

        // 4. Ejecutar combate
        sistemaCombate.ejecutarCombate(tickActual);

        // 5. Limpiar entidades muertas
        tablero.limpiarEntidadesMuertas();

        // 6. Verificar condiciones de victoria
        verificarCondicionesVictoria();

        // 7. Verificar tiempo límite
        verificarTiempo();
    }

    /**
     * Permite a un jugador desplegar una carta manualmente
     */
    public boolean desplegarCarta(int idJugador, String nombreCarta, int x, int y) {
        if (partidaTerminada) {
            return false;
        }

        Jugador jugador = (idJugador == 1) ? jugador1 : jugador2;
        Tropa tropaDesplegada = jugador.intentarDesplegarCarta(nombreCarta, x, y, tablero);

        if (tropaDesplegada != null) {
            eventManager.notify(new TropaDesplegadaEvent(jugador, tropaDesplegada, tropaDesplegada.getPosicion()));
            return true;
        }
        return false;
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof EntidadDestruidaEvent) {
            procesarTorreDestruida((EntidadDestruidaEvent) event);
        }
    }

    private void procesarTorreDestruida(EntidadDestruidaEvent event) {
        if (event.getEntidad() instanceof TorrePrincesa) {
            Torre torre = (Torre) event.getEntidad();
            int idDefensor = torre.getJugadorId();
            int idAtacante = (idDefensor == 1) ? 2 : 1;
            Jugador atacante = (idAtacante == 1) ? jugador1 : jugador2;
            Jugador defensor = (idDefensor == 1) ? jugador1 : jugador2;

            boolean esLadoIzquierdo = torre.getPosicion().getX() < 9;

            // Crear la nueva zona de 9x5 y la zona del puente
            tablero.ZonaDespliegue.RectanguloZona nuevaZona;
            tablero.ZonaDespliegue.RectanguloZona zonaPuente;

            if (idAtacante == 1) { // J1 ataca a J2 (despliega en la parte de abajo)
                nuevaZona = esLadoIzquierdo ? new tablero.ZonaDespliegue.RectanguloZona(0, 17, 8, 21) : new tablero.ZonaDespliegue.RectanguloZona(9, 17, 17, 21);
                zonaPuente = esLadoIzquierdo ? new tablero.ZonaDespliegue.RectanguloZona(Tablero.PUENTE_X1, 15, Tablero.PUENTE_X1, 16) : new tablero.ZonaDespliegue.RectanguloZona(Tablero.PUENTE_X2, 15, Tablero.PUENTE_X2, 16);
            } else { // J2 ataca a J1 (despliega en la parte de arriba)
                nuevaZona = esLadoIzquierdo ? new tablero.ZonaDespliegue.RectanguloZona(0, 10, 8, 14) : new tablero.ZonaDespliegue.RectanguloZona(9, 10, 17, 14);
                zonaPuente = esLadoIzquierdo ? new tablero.ZonaDespliegue.RectanguloZona(Tablero.PUENTE_X1, 15, Tablero.PUENTE_X1, 16) : new tablero.ZonaDespliegue.RectanguloZona(Tablero.PUENTE_X2, 15, Tablero.PUENTE_X2, 16);
            }

            // Actualizar zonas para ambos jugadores
            atacante.getZonaDespliegue().agregarZona(nuevaZona);
            atacante.getZonaDespliegue().agregarZona(zonaPuente);
            defensor.getZonaDespliegue().restringirZona(nuevaZona);
        }
    }

    private void procesarDecisionesJugadores() {
        // La IA de cada jugador decide si juega una carta
        Tropa tropaJ1 = jugador1.jugarCartaIA(tablero, tickActual);
        if (tropaJ1 != null) {
            eventManager.notify(new TropaDesplegadaEvent(jugador1, tropaJ1, tropaJ1.getPosicion()));
        }

        Tropa tropaJ2 = jugador2.jugarCartaIA(tablero, tickActual);
        if (tropaJ2 != null) {
            eventManager.notify(new TropaDesplegadaEvent(jugador2, tropaJ2, tropaJ2.getPosicion()));
        }
    }

    private void verificarCondicionesVictoria() {
        // Verificar torres rey
        if (!tablero.torreReyViva(1)) {
            terminarPartida(2, "Torre Rey del Jugador 1 destruida");
            return;
        }

        if (!tablero.torreReyViva(2)) {
            terminarPartida(1, "Torre Rey del Jugador 2 destruida");
            return;
        }
    }

    private void verificarTiempo() {
        if (tickActual >= configuracion.getTiempoPartida()) {
            int torresJ1 = tablero.contarTorresVivas(1);
            int torresJ2 = tablero.contarTorresVivas(2);

            if (torresJ1 > torresJ2) {
                terminarPartida(1, "Más torres restantes");
            } else if (torresJ2 > torresJ1) {
                terminarPartida(2, "Más torres restantes");
            } else if (tickActual >= configuracion.getTiempoTotal()) {
                // Verificar por vida de torres
                verificarVictoriaPorVida();
            }
        }
    }

    private void verificarVictoriaPorVida() {
        int vidaJ1 = tablero.calcularVidaTotalTorres(1);
        int vidaJ2 = tablero.calcularVidaTotalTorres(2);

        if (vidaJ1 > vidaJ2) {
            terminarPartida(1, "Mayor vida total en torres");
        } else if (vidaJ2 > vidaJ1) {
            terminarPartida(2, "Mayor vida total en torres");
        } else {
            terminarPartida(0, "Empate por vida total");
        }
    }

    private void terminarPartida(int ganadorId, String motivo) {
        this.partidaTerminada = true;
        this.ganador = ganadorId;
        eventManager.notify(new PartidaTerminadaEvent(ganadorId, motivo));
    }

    private String obtenerTiempoFormateado() {
        int minutos = tickActual / 60;
        int segundos = tickActual % 60;

        String estado = "";
        if (tickActual > configuracion.getTiempoPartida()) {
            estado = " [OVERTIME]";
        }

        return String.format("%d:%02d%s", minutos, segundos, estado);
    }

    // ==========================================
    // GETTERS PÚBLICOS
    // ==========================================

    public Tablero getTablero() { return tablero; }
    public Jugador getJugador1() { return jugador1; }
    public Jugador getJugador2() { return jugador2; }
    public int getTickActual() { return tickActual; }
    public boolean isPartidaTerminada() { return partidaTerminada; }
    public int getGanador() { return ganador; }
    public EstadoPartida getEstado() { return estado; }
    public EventManager getEventManager() { return eventManager; }
}