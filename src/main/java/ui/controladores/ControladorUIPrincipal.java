package ui.controladores;

import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import juego.Partida;
import juego.ConfiguracionPartida;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import juego.events.GameEvent;
import juego.events.GameEventListener;
import juego.events.PartidaTerminadaEvent;
import ui.componentes.*;
import ui.constantes.ConstantesUI;
import ui.gestores.GestorAnimaciones;

/**
 * Controlador principal de la interfaz de usuario
 * Orquesta todos los componentes y gestiona el ciclo de vida de la aplicación
 */
public class ControladorUIPrincipal implements GameEventListener {

    // Partida del juego
    private Partida partida;

    // Componentes principales de la UI
    private ComponentePanelJugador panelJugador1;
    private ComponentePanelJugador panelJugador2;
    private ComponenteArena componenteArena;
    private ComponentePanelControl componentePanelControl;


    // Gestores
    private GestorAnimaciones gestorAnimaciones;

    // Estado del juego
    private Timeline bucleJuego;
    private boolean estaEjecutandose = false;
    private int ticksParaTerminar = -1; // Contador para el retardo de fin de juego
    private PartidaTerminadaEvent infoPartidaTerminada = null;

    // Contenedores principales
    private VBox contenedorPrincipal;
    private Stage escenarioPrincipal;

    /**
     * Constructor
     */
    public ControladorUIPrincipal() {
        inicializarJuego();
    }

    /**
     * Inicializa la partida del juego
     */
    private void inicializarJuego() {
        ConfiguracionPartida configuracion = ConfiguracionPartida.partidaEstandar();
        partida = new Partida(configuracion);
        partida.getEventManager().subscribe(PartidaTerminadaEvent.class, this);
        partida.inicializar();
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event instanceof PartidaTerminadaEvent) {
            // No terminar el juego de inmediato. Iniciar el contador.
            if (ticksParaTerminar == -1) { // Asegurarse de que solo se active una vez
                this.infoPartidaTerminada = (PartidaTerminadaEvent) event;
                // Retardo de 2 segundos (2000 ms / DURACION_TICK_JUEGO_MS)
                this.ticksParaTerminar = (int) (2000 / ConstantesUI.Tiempos.DURACION_TICK_JUEGO_MS);
            }
        }
    }

    /**
     * Inicializa la interfaz de usuario
     */
    public void inicializarUI(Stage escenario) {
        this.escenarioPrincipal = escenario;

        // Crear componentes principales
        crearComponentesUI();

        // Construir layout principal
        construirLayoutPrincipal();

        // Configurar la escena
        configurarEscena();

        // Inicializar gestor de animaciones
        inicializarGestorAnimaciones();

        // Configurar eventos
        configurarManejadoresEventos();

        // Actualización inicial de la vista
        actualizarTodosLosComponentes();
    }

    /**
     * Crea todos los componentes de la UI
     */
    private void crearComponentesUI() {

        panelJugador1 = new ComponentePanelJugador(1);
        panelJugador2 = new ComponentePanelJugador(2);
        componenteArena = new ComponenteArena();
        componentePanelControl = new ComponentePanelControl();

    }

    /**
     * Construye el layout principal de la aplicación
     */
    private void construirLayoutPrincipal() {
        contenedorPrincipal = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PANEL);
        contenedorPrincipal.setPadding(new Insets(10));
        contenedorPrincipal.setStyle(ConstantesUI.Estilos.GRADIENTE_FONDO);

        // Solo el contenido principal
        HBox contenidoPrincipal = crearContenidoPrincipal();
        contenedorPrincipal.getChildren().add(contenidoPrincipal);

    }

    /**
     * Crea el contenido principal con los paneles de jugador y arena
     */
    private HBox crearContenidoPrincipal() {
        HBox contenidoPrincipal = new HBox(ConstantesUI.Dimensiones.ESPACIADO_PANEL);

        // CONFIGURAR POLÍTICAS DE CRECIMIENTO CON NUEVOS TAMAÑOS
        // Paneles laterales - NO crecen, tamaño fijo responsive
        panelJugador1.obtenerComponente().setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        panelJugador2.obtenerComponente().setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        // Arena - SÍ puede crecer
        componenteArena.obtenerComponente().setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(componenteArena.obtenerComponente(), Priority.ALWAYS);

        // Controles - NO crecen
        componentePanelControl.obtenerComponente().setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        contenidoPrincipal.getChildren().addAll(
                panelJugador1.obtenerComponente(),
                componenteArena.obtenerComponente(),
                panelJugador2.obtenerComponente(),
                componentePanelControl.obtenerComponente()
        );

        return contenidoPrincipal;
    }

    /**
     * Configura la escena y la ventana principal
     */
    private void configurarEscena() {
        Scene escena = new Scene(contenedorPrincipal,
                ConstantesUI.Dimensiones.ANCHO_VENTANA,
                ConstantesUI.Dimensiones.ALTO_VENTANA);

        // Configurar teclas de atajo
        escena.setOnKeyPressed(evento -> {
            switch (evento.getCode()) {
                case SPACE -> alternarPlayPause();
                case ENTER -> ejecutarPaso();
                case R -> reiniciarJuego();
            }
        });

        escenarioPrincipal.setTitle(ConstantesUI.Etiquetas.TITULO_APP);
        escenarioPrincipal.setScene(escena);

        escenarioPrincipal.setOnCloseRequest(e -> {
            limpiezaFinal();
            Platform.exit();
        });
    }

    /**
     * Inicializa el gestor de animaciones
     */
    private void inicializarGestorAnimaciones() {
        gestorAnimaciones = new GestorAnimaciones(
                componenteArena.obtenerGrillaArena(),
                partida
        );
    }

    /**
     * Configura los manejadores de eventos para los componentes
     */
    private void configurarManejadoresEventos() {
        // Eventos del panel de control
        componentePanelControl.configurarAccionPlayPause(this::alternarPlayPause);
        componentePanelControl.configurarAccionPaso(this::ejecutarPaso);
        componentePanelControl.configurarAccionReiniciar(this::reiniciarJuego);
    }

    /**
     * Inicia o pausa el juego
     */
    public void alternarPlayPause() {
        if (estaEjecutandose) {
            pausarJuego();
        } else {
            iniciarJuego();
        }
    }

    /**
     * Inicia el juego
     */
    private void iniciarJuego() {
        bucleJuego = new Timeline(new KeyFrame(
                Duration.millis(ConstantesUI.Tiempos.DURACION_TICK_JUEGO_MS),
                e -> ejecutarTickJuego()));

        bucleJuego.setCycleCount(Timeline.INDEFINITE);
        bucleJuego.play();

        estaEjecutandose = true;
        componentePanelControl.actualizarBotonPlayPause(true);
    }

    /**
     * Pausa el juego
     */
    private void pausarJuego() {
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        // Limpiar animaciones al pausar
        gestorAnimaciones.limpiarTodasLasAnimaciones();

        estaEjecutandose = false;
        componentePanelControl.actualizarBotonPlayPause(false);
    }

    /**
     * Ejecuta un tick individual del juego
     */
    public void ejecutarPaso() {
        ejecutarTickJuego();
    }

    /**
     * Ejecuta un tick del juego y actualiza la vista
     */
    private void ejecutarTickJuego() {
        if (!partida.isPartidaTerminada()) {
            partida.ejecutarTick();
        }

        // Actualizar la vista en el hilo de JavaFX
        Platform.runLater(() -> {
            // PRIMERO: Limpiar animaciones obsoletas ANTES de detectar nuevas
            gestorAnimaciones.limpiarAnimacionesObsoletas(partida.getTablero());

            // SEGUNDO: Actualizar componentes visuales (esto incluye mover unidades)
            actualizarTodosLosComponentes();

            // TERCERO: Detectar y activar nuevas animaciones de combate
            gestorAnimaciones.detectarEventosCombate();

            // CUARTO: Actualizar animaciones existentes
            gestorAnimaciones.actualizarAnimaciones();

            // Gestionar el contador de fin de partida
            if (ticksParaTerminar > 0) {
                ticksParaTerminar--;
            } else if (ticksParaTerminar == 0) {
                manejarFinDelJuego(infoPartidaTerminada);
                ticksParaTerminar = -1;
            }
        });
    }



    /**
     * Maneja el final del juego
     */
    private void manejarFinDelJuego(PartidaTerminadaEvent info) {
        // Detener el bucle del juego
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        estaEjecutandose = false;
        componentePanelControl.actualizarBotonPlayPause(false);

        // Mostrar ganador en el panel de control
        int ganador = info.getGanadorId();
        String textoGanador = switch (ganador) {
            case 1 -> ConstantesUI.Etiquetas.JUGADOR_1_GANA;
            case 2 -> ConstantesUI.Etiquetas.JUGADOR_2_GANA;
            default -> ConstantesUI.Etiquetas.EMPATE;
        };

        // Mostrar ganador en el panel de control en lugar de la cabecera
        componentePanelControl.mostrarGanador(textoGanador);
    }

    /**
     * Reinicia el juego
     */
    public void reiniciarJuego() {
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        // Limpiar animaciones
        gestorAnimaciones.limpiarTodasLasAnimaciones();

        estaEjecutandose = false;
        componentePanelControl.actualizarBotonPlayPause(false);
        componentePanelControl.limpiarGanador(); // Limpiar mensaje de ganador del panel de control

        // Reinicializar partida
        inicializarJuego();

        // Reinicializar gestor de animaciones
        inicializarGestorAnimaciones();

        // Actualizar vista
        actualizarTodosLosComponentes();
    }

    /**
     * Actualiza todos los componentes de la interfaz
     */
    private void actualizarTodosLosComponentes() {

        // Actualizar paneles de jugadores
        panelJugador1.actualizar(partida.getJugador1(), partida.getTablero());
        panelJugador2.actualizar(partida.getJugador2(), partida.getTablero());

        // Actualizar arena, pasando el estado del checkbox
        boolean mostrarRangos = componentePanelControl.isMostrarRangosSeleccionado();
        componenteArena.actualizar(partida, gestorAnimaciones, mostrarRangos);

        // Actualizar panel de control con tiempo y ticks
        componentePanelControl.actualizarTiempoYTicks(
                obtenerTiempoFormateado(),
                partida.getTickActual()
        );
    }
    /**
     * Obtiene el tiempo formateado de la partida
     */
    private String obtenerTiempoFormateado() {
        int tickActual = partida.getTickActual();
        int minutos = tickActual / 60;
        int segundos = tickActual % 60;
        return String.format("%d:%02d", minutos, segundos);
    }

    /**
     * Limpieza final al cerrar la aplicación
     */
    private void limpiezaFinal() {
        if (bucleJuego != null) {
            bucleJuego.stop();
        }

        if (gestorAnimaciones != null) {
            gestorAnimaciones.limpiarTodasLasAnimaciones();
        }
    }

    /**
     * Muestra la ventana principal
     */
    public void mostrar() {
        escenarioPrincipal.show();
    }

    // Getters para acceso desde otros componentes si es necesario
    public Partida obtenerPartida() {
        return partida;
    }

    public boolean estaEjecutandose() {
        return estaEjecutandose;
    }
}