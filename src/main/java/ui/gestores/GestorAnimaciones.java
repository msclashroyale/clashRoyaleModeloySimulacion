package ui.gestores;

import juego.Partida;
import entidades.base.EntidadJuego;
import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import tablero.Tablero;
import tablero.Posicion;
import ui.AnimacionCombate;
import ui.TipoAnimacion;
import ui.efectos.SistemaParticulas;
import ui.constantes.ConstantesUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Gestor de animaciones refactorizado con mejor organización
 * Maneja todas las animaciones de combate del juego
 */
public class GestorAnimaciones {

    private final Map<Posicion, AnimacionCombate> animacionesActivas;
    private final GridPane grillaArena;
    private final Partida partida;
    private final SistemaParticulas sistemaParticulas; // DECLARACIÓN AÑADIDA
    private final Pane contenedorAnimaciones; // DECLARACIÓN AÑADIDA

    // Cache para optimizar el rendimiento
    private final Map<Tropa, Posicion> cachePosicionesAnterioresTropas;
    private final Set<Posicion> posicionesLimpiandose;
    private final Map<Torre, EstadoTorre> cacheEstadosTorres;

    /**
     * Constructor del gestor de animaciones
     * @param grillaArena Grilla visual del arena
     * @param partida Partida principal del juego
     */
    public GestorAnimaciones(GridPane grillaArena, Partida partida) {
        this.animacionesActivas = new HashMap<>();
        this.grillaArena = grillaArena;
        this.partida = partida;
        this.cachePosicionesAnterioresTropas = new HashMap<>();
        this.posicionesLimpiandose = new HashSet<>();
        this.cacheEstadosTorres = new HashMap<>();

        // INICIALIZACIÓN DEL SISTEMA DE PARTÍCULAS AÑADIDA
        this.contenedorAnimaciones = new Pane();
        this.contenedorAnimaciones.setMouseTransparent(true);
        this.sistemaParticulas = new SistemaParticulas(contenedorAnimaciones);

        // Añadir el contenedor de animaciones sobre la grilla
        if (grillaArena.getParent() instanceof Pane) {
            ((Pane) grillaArena.getParent()).getChildren().add(contenedorAnimaciones);
        }
    }

    /**
     * Inicia una animación de combate en la posición especificada
     * @param posicion Posición donde iniciar la animación
     * @param tipo Tipo de animación a reproducir
     */
    public void iniciarAnimacion(Posicion posicion, TipoAnimacion tipo) {
        if (!esPosicionValida(posicion) || posicionesLimpiandose.contains(posicion)) {
            return;
        }

        // Si ya hay una animación en esta posición, detenerla
        detenerAnimacionEnPosicion(posicion);

        // Obtener la celda visual
        StackPane celda = obtenerCeldaVisual(posicion);
        if (celda == null) {
            return;
        }

        // Crear y registrar nueva animación MEJORADA con sistema de partículas
        AnimacionCombate nuevaAnimacion = new AnimacionCombate(celda, tipo, sistemaParticulas);
        animacionesActivas.put(crearCopiaPosicion(posicion), nuevaAnimacion);
    }

    /**
     * Inicia animación en múltiples posiciones (útil para torres grandes)
     * @param posiciones Lista de posiciones
     * @param tipo Tipo de animación
     */
    public void iniciarAnimacionMultiple(List<Posicion> posiciones, TipoAnimacion tipo) {
        if (posiciones == null || posiciones.isEmpty()) {
            return;
        }

        for (Posicion pos : posiciones) {
            if (pos != null) {
                iniciarAnimacion(pos, tipo);
            }
        }
    }

    /**
     * Detecta eventos de combate y activa las animaciones correspondientes
     */
    public void detectarEventosCombate() {
        Tablero tablero = partida.getTablero();

        // Limpiar animaciones obsoletas
        limpiarAnimacionesObsoletas(tablero);

        // Procesar tropas
        procesarAnimacionesTropas(tablero);

        // Procesar torres
        procesarAnimacionesTorres(tablero);
    }

    /**
     * Procesa las animaciones para todas las tropas activas
     * @param tablero Tablero del juego
     */
    private void procesarAnimacionesTropas(Tablero tablero) {
        List<Tropa> tropasActivas = tablero.getTropas();

        for (Tropa tropa : tropasActivas) {
            if (!tropa.estaViva()) {
                continue;
            }

            Posicion posicionActual = tropa.getPosicion();

            // Limpiar posición anterior si la tropa se movió
            gestionarMovimientoTropa(tropa, posicionActual);

            // Verificar estado de combate
            EstadoCombate estadoCombate = evaluarEstadoCombateTropa(tropa, tablero);

            // Activar animación si está en combate
            if (estadoCombate.necesitaAnimacion()) {
                activarAnimacionSegunEstado(posicionActual, estadoCombate);
            }
        }
    }

    /**
     * Procesa las animaciones para todas las torres
     * @param tablero Tablero del juego
     */
    private void procesarAnimacionesTorres(Tablero tablero) {
        for (Torre torre : tablero.getTorres()) {
            if (!torre.estaViva()) {
                continue;
            }

            EstadoCombate estadoCombate = evaluarEstadoCombateTorre(torre, tablero);

            if (estadoCombate.necesitaAnimacion()) {
                List<Posicion> posicionesTorre = torre.getPosicionesOcupadas();
                iniciarAnimacionMultiple(posicionesTorre, estadoCombate.obtenerTipoAnimacion());
            }
        }
    }

    /**
     * Gestiona el movimiento de una tropa limpiando su posición anterior
     * @param tropa Tropa que se movió
     * @param posicionActual Nueva posición de la tropa
     */
    private void gestionarMovimientoTropa(Tropa tropa, Posicion posicionActual) {
        Posicion posicionAnterior = cachePosicionesAnterioresTropas.get(tropa);

        if (posicionAnterior != null && !posicionAnterior.equals(posicionActual)) {
            // Limpieza inmediata y forzada de la posición anterior
            limpiarAnimacionEnPosicionForzado(posicionAnterior);

            // También limpiar cualquier animación en la nueva posición que pueda ser obsoleta
            if (animacionesActivas.containsKey(posicionActual)) {
                AnimacionCombate animacion = animacionesActivas.get(posicionActual);
                // Si la animación en la nueva posición no está activa, limpiarla
                if (!animacion.estaActiva()) {
                    limpiarAnimacionEnPosicionForzado(posicionActual);
                }
            }
        }

        // Actualizar cache
        cachePosicionesAnterioresTropas.put(tropa, crearCopiaPosicion(posicionActual));
    }

    private void limpiarAnimacionEnPosicionForzado(Posicion posicion) {
        AnimacionCombate animacion = animacionesActivas.get(posicion);
        if (animacion != null) {
            animacion.detener();
            animacionesActivas.remove(posicion);
        }
        posicionesLimpiandose.add(posicion);
    }


    /**
     * Evalúa el estado de combate de una tropa
     * @param tropa Tropa a evaluar
     * @param tablero Tablero del juego
     * @return Estado de combate de la tropa
     */
    private EstadoCombate evaluarEstadoCombateTropa(Tropa tropa, Tablero tablero) {
        boolean estaAtacando = verificarTropaAtacando(tropa, tablero);
        boolean recibioDanio = verificarDanioReciente(tropa);

        return new EstadoCombate(estaAtacando, recibioDanio);
    }

    /**
     * Evalúa el estado de combate de una torre
     * @param torre Torre a evaluar
     * @param tablero Tablero del juego
     * @return Estado de combate de la torre
     */
    private EstadoCombate evaluarEstadoCombateTorre(Torre torre, Tablero tablero) {
        boolean estaAtacando = verificarTorreAtacando(torre, tablero);
        boolean recibioDanio = verificarCambioEstadoTorre(torre);

        return new EstadoCombate(estaAtacando, recibioDanio);
    }

    public AnimacionCombate obtenerAnimacion(Posicion posicion) {
        return animacionesActivas.get(posicion);
    }

    /**
     * Verifica si una tropa está atacando actualmente
     * @param tropa Tropa a verificar
     * @param tablero Tablero del juego
     * @return true si está atacando
     */
    private boolean verificarTropaAtacando(Tropa tropa, Tablero tablero) {
        EntidadJuego enemigo = tropa.buscarEnemigoEnRango(tablero);
        return enemigo != null && tropa.puedeAtacar(enemigo, partida.getTickActual());
    }

    /**
     * Verifica si una tropa recibió daño recientemente
     * @param tropa Tropa a verificar
     * @return true si recibió daño
     */
    private boolean verificarDanioReciente(Tropa tropa) {
        return tropa.getVidaActual() < tropa.getVidaMaxima();
    }

    /**
     * Verifica si una torre está atacando
     * @param torre Torre a verificar
     * @param tablero Tablero del juego
     * @return true si está atacando
     */
    private boolean verificarTorreAtacando(Torre torre, Tablero tablero) {
        return tablero.getTropas().stream()
                .anyMatch(t -> t.estaViva()
                        && t.getJugadorId() != torre.getJugadorId()
                        && torre.getPosicion().calcularDistancia(t.getPosicion()) <=
                        ConstantesUI.ConfiguracionJuego.RANGO_ATAQUE_TORRE);
    }

    /**
     * Verifica si el estado de una torre cambió (recibió daño)
     * @param torre Torre a verificar
     * @return true si cambió su estado
     */
    private boolean verificarCambioEstadoTorre(Torre torre) {
        EstadoTorre estadoAnterior = cacheEstadosTorres.get(torre);
        EstadoTorre estadoActual = new EstadoTorre(torre.getVidaActual(), torre.getVidaMaxima());

        cacheEstadosTorres.put(torre, estadoActual);

        return estadoAnterior != null &&
                estadoAnterior.getVidaActual() > estadoActual.getVidaActual();
    }

    /**
     * Activa la animación apropiada según el estado de combate
     * @param posicion Posición donde activar la animación
     * @param estadoCombate Estado de combate evaluado
     */
    private void activarAnimacionSegunEstado(Posicion posicion, EstadoCombate estadoCombate) {
        iniciarAnimacion(posicion, estadoCombate.obtenerTipoAnimacion());
    }

    /**
     * Actualiza todas las animaciones activas
     */
    public void actualizarAnimaciones() {
        List<Posicion> animacionesCompletadas = new ArrayList<>();

        for (Map.Entry<Posicion, AnimacionCombate> entrada : animacionesActivas.entrySet()) {
            if (entrada.getValue().actualizar()) {
                animacionesCompletadas.add(entrada.getKey());
            }
        }

        // Eliminar animaciones completadas
        for (Posicion pos : animacionesCompletadas) {
            finalizarAnimacion(pos);
        }
    }

    /**
     * Limpia animaciones en posiciones donde ya no hay entidades válidas
     * @param tablero Tablero actual del juego
     */
    public void limpiarAnimacionesObsoletas(Tablero tablero) {
        List<Posicion> posicionesALimpiar = new ArrayList<>();

        for (Posicion pos : animacionesActivas.keySet()) {
            boolean debeLimpiar = !hayEntidadValidaEnPosicion(pos, tablero) ||
                    posicionesLimpiandose.contains(pos);

            if (debeLimpiar) {
                posicionesALimpiar.add(pos);
            }
        }

        for (Posicion pos : posicionesALimpiar) {
            limpiarAnimacionEnPosicion(pos);
        }
    }

    public void limpiarAnimacionesEnPosicionForzado(Posicion posicion) {
        if (animacionesActivas.containsKey(posicion)) {
            AnimacionCombate animacion = animacionesActivas.get(posicion);
            animacion.detener();
            animacionesActivas.remove(posicion);

            // Forzar actualización visual inmediata
            posicionesLimpiandose.add(posicion);
        }
    }

    /**
     * Verifica si hay una entidad válida en la posición
     * @param posicion Posición a verificar
     * @param tablero Tablero del juego
     * @return true si hay una entidad válida
     */
    private boolean hayEntidadValidaEnPosicion(Posicion posicion, Tablero tablero) {
        Tropa tropa = tablero.obtenerTropaEnPosicion(posicion);
        Torre torre = tablero.obtenerTorreEnPosicion(posicion);

        return (tropa != null && tropa.estaViva()) || torre != null;
    }

    /**
     * Detiene una animación en una posición específica
     * @param posicion Posición donde detener la animación
     */
    private void detenerAnimacionEnPosicion(Posicion posicion) {
        AnimacionCombate animacion = animacionesActivas.get(posicion);
        if (animacion != null) {
            animacion.detener();
        }
    }

    /**
     * Limpia una animación en una posición específica
     * @param posicion Posición a limpiar
     */
    private void limpiarAnimacionEnPosicion(Posicion posicion) {
        detenerAnimacionEnPosicion(posicion);
        animacionesActivas.remove(posicion);
        posicionesLimpiandose.add(posicion);
    }

    /**
     * Finaliza una animación completada
     * @param posicion Posición de la animación finalizada
     */
    private void finalizarAnimacion(Posicion posicion) {
        animacionesActivas.remove(posicion);
        posicionesLimpiandose.remove(posicion);
    }

    /**
     * Obtiene la celda visual correspondiente a una posición
     * @param posicion Posición en el tablero
     * @return StackPane de la celda o null si no existe
     */
    private StackPane obtenerCeldaVisual(Posicion posicion) {
        try {
            int indice = posicion.getY() * Tablero.ANCHO + posicion.getX();
            return (StackPane) grillaArena.getChildren().get(indice);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica si una posición es válida
     * @param posicion Posición a verificar
     * @return true si es válida
     */
    private boolean esPosicionValida(Posicion posicion) {
        return posicion != null &&
                posicion.getX() >= 0 && posicion.getX() < Tablero.ANCHO &&
                posicion.getY() >= 0 && posicion.getY() < Tablero.ALTO;
    }

    /**
     * Crea una copia de la posición para evitar referencias compartidas
     * @param posicion Posición original
     * @return Nueva instancia de Posicion
     */
    private Posicion crearCopiaPosicion(Posicion posicion) {
        return new Posicion(posicion.getX(), posicion.getY());
    }

    // Métodos públicos para la interfaz

    /**
     * Verifica si hay una animación activa en la posición
     * @param posicion Posición a verificar
     * @return true si hay animación activa
     */
    public boolean tieneAnimacionActiva(Posicion posicion) {
        return animacionesActivas.containsKey(posicion);
    }

    /**
     * Detiene y limpia todas las animaciones activas
     */
    public void limpiarTodasLasAnimaciones() {
        for (AnimacionCombate animacion : animacionesActivas.values()) {
            animacion.detener();
        }
        animacionesActivas.clear();
        cachePosicionesAnterioresTropas.clear();
        posicionesLimpiandose.clear();
        cacheEstadosTorres.clear();
        sistemaParticulas.limpiar(); // Limpiar partículas también
    }

    /**
     * Obtiene el número de animaciones activas
     * @return Número de animaciones activas
     */
    public int obtenerNumAnimacionesActivas() {
        return animacionesActivas.size();
    }

    /**
     * Obtiene información de debug sobre las animaciones
     * @return String con información de las animaciones activas
     */
    public String obtenerEstadoAnimaciones() {
        StringBuilder sb = new StringBuilder();
        sb.append("Animaciones activas: ").append(animacionesActivas.size()).append("\n");
        sb.append("Partículas activas: ").append(sistemaParticulas.getNumeroParticulasActivas()).append("\n");

        for (Map.Entry<Posicion, AnimacionCombate> entrada : animacionesActivas.entrySet()) {
            Posicion pos = entrada.getKey();
            AnimacionCombate anim = entrada.getValue();
            sb.append("  - Pos(").append(pos.getX()).append(",").append(pos.getY())
                    .append(") Tipo: ").append(anim.getTipo())
                    .append(" Ticks: ").append(anim.getTicksRestantes()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Obtiene el sistema de partículas para uso externo
     * @return Sistema de partículas
     */
    public SistemaParticulas getSistemaParticulas() {
        return sistemaParticulas;
    }

    /**
     * Obtiene el contenedor de animaciones para posicionamiento
     * @return Contenedor de animaciones
     */
    public Pane getContenedorAnimaciones() {
        return contenedorAnimaciones;
    }
}