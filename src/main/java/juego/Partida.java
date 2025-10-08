// ==========================================
// CLASE PRINCIPAL DE LA PARTIDA
// ==========================================
package juego;

import jugador.Jugador;
import tablero.Tablero;
import combate.SistemaCombate;
import movimiento.SistemaMovimiento;

/**
 * Clase principal que maneja el estado y flujo de una partida
 * Reemplaza al ControladorJuegoConCartas con responsabilidades más claras
 */
public class Partida {

    private final ConfiguracionPartida configuracion;
    private final EstadoPartida estado;

    // Componentes principales
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Tablero tablero;
    private final SistemaCombate sistemaCombate;
    private final SistemaMovimiento sistemaMovimiento;

    // Temporizador y control de juego
    private int tickActual;
    private boolean partidaTerminada;
    private int ganador; // 0=empate, 1=jugador1, 2=jugador2

    public Partida(ConfiguracionPartida configuracion) {
        this.configuracion = configuracion;
        this.estado = new EstadoPartida();

        // Crear jugadores
        this.jugador1 = new Jugador(1, "Jugador 1", configuracion.getNivelJugador1());
        this.jugador2 = new Jugador(2, "Jugador 2", configuracion.getNivelJugador2());

        // Crear tablero y sistemas
        this.tablero = new Tablero();
        this.sistemaCombate = new SistemaCombate(tablero);
        this.sistemaMovimiento = new SistemaMovimiento(tablero);

        this.tickActual = 0;
        this.partidaTerminada = false;
        this.ganador = 0;
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

        // Mostrar información inicial
        mostrarInformacionInicial();

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

        // 8. Mostrar estado (cada cierto tiempo)
        if (tickActual % 10 == 0) {
            mostrarEstadoActual();
        }
    }

    /**
     * Permite a un jugador desplegar una carta manualmente
     */
    public boolean desplegarCarta(int idJugador, String nombreCarta, int x, int y) {
        if (partidaTerminada) {
            return false;
        }

        Jugador jugador = (idJugador == 1) ? jugador1 : jugador2;
        return jugador.intentarDesplegarCarta(nombreCarta, x, y, tablero);
    }

    // ==========================================
    // MÉTODOS PRIVADOS
    // ==========================================

    private void procesarDecisionesJugadores() {
        // La IA de cada jugador decide si juega una carta
        jugador1.jugarCartaIA(tablero, tickActual);
        jugador2.jugarCartaIA(tablero, tickActual);
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

        System.out.println("\n" + "=".repeat(50));
        if (ganadorId == 0) {
            System.out.println("           EMPATE");
        } else {
            System.out.println("       JUGADOR " + ganadorId + " GANA");
        }
        System.out.println("Motivo: " + motivo);
        System.out.println("Tiempo: " + obtenerTiempoFormateado());
        System.out.println("=".repeat(50));
    }

    private void mostrarInformacionInicial() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                 INICIANDO PARTIDA");
        System.out.println("=".repeat(60));
        System.out.println("Configuración:");
        System.out.println("- Duración: " + configuracion.getTiempoPartida() + "s + " +
                configuracion.getTiempoOvertime() + "s overtime");
        System.out.println("- Niveles: J1=" + configuracion.getNivelJugador1() +
                ", J2=" + configuracion.getNivelJugador2());
        System.out.println("=".repeat(60));
    }

    private void mostrarEstadoActual() {
        System.out.println("\n--- TICK " + tickActual + " (" + obtenerTiempoFormateado() + ") ---");
        System.out.println("Tropas: J1=" + tablero.contarTropasVivas(1) +
                " | J2=" + tablero.contarTropasVivas(2));
        System.out.println("Elixir: J1=" + jugador1.getSistemaElixir().getElixirActual() +
                "/" + jugador1.getSistemaElixir().getElixirMaximo() +
                " | J2=" + jugador2.getSistemaElixir().getElixirActual() +
                "/" + jugador2.getSistemaElixir().getElixirMaximo());
        System.out.println("Torres: J1=" + tablero.contarTorresVivas(1) +
                "/3 | J2=" + tablero.contarTorresVivas(2) + "/3");
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
}