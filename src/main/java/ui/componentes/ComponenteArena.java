package ui.componentes;

import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import juego.Partida;
import tablero.Tablero;
import tablero.Posicion;
import tablero.TipoTerreno;
import ui.constantes.ConstantesUI;
import ui.gestores.GestorAnimaciones;

/**
 * Componente que maneja la visualización del tablero de juego
 * Muestra las tropas, torres, terreno y efectos visuales
 */
public class ComponenteArena {

    private VBox contenedorArena;
    private GridPane grillaArena;
    private Rectangle[][] casillas;
    private Label[][] simbolos;

    /**
     * Constructor - inicializa el componente del tablero
     */
    public ComponenteArena() {
        inicializarComponente();
    }

    /**
     * Inicializa todos los elementos del tablero
     */
    private void inicializarComponente() {
        contenedorArena = new VBox(5);
        contenedorArena.setAlignment(Pos.CENTER);

        crearTituloArena();
        crearGrillaArena();
    }

    /**
     * Crea el título del tablero
     */
    private void crearTituloArena() {
        Label etiquetaArena = new Label(ConstantesUI.Etiquetas.TITULO_ARENA);
        etiquetaArena.setFont(ConstantesUI.Fuentes.SUBTITULO);
        etiquetaArena.setTextFill(Color.WHITE);

        contenedorArena.getChildren().add(etiquetaArena);
    }

    /**
     * Crea la grilla visual del tablero
     */
    private void crearGrillaArena() {
        grillaArena = new GridPane();
        grillaArena.setAlignment(Pos.CENTER);
        grillaArena.setStyle(ConstantesUI.Estilos.GRILLA_ARENA);

        // Añadir un poco de espaciado entre celdas para mejor visualización
        grillaArena.setHgap(1);
        grillaArena.setVgap(1);

        casillas = new Rectangle[Tablero.ALTO][Tablero.ANCHO];
        simbolos = new Label[Tablero.ALTO][Tablero.ANCHO];

        // Crear las casillas
        for (int y = 0; y < Tablero.ALTO; y++) {
            for (int x = 0; x < Tablero.ANCHO; x++) {
                crearCasilla(x, y);
            }
        }

        contenedorArena.getChildren().add(grillaArena);
    }

    /**
     * Crea una casilla individual del tablero
     * @param x coordenada X
     * @param y coordenada Y
     */
    private void crearCasilla(int x, int y) {
        double tamanoCelda = ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA;

        Rectangle rect = new Rectangle(tamanoCelda, tamanoCelda);
        rect.setStroke(Color.GRAY);
        rect.setStrokeWidth(0.3);
        casillas[y][x] = rect;

        // Label para el símbolo
        Label simbolo = new Label();
        simbolo.setFont(ConstantesUI.esPantallaGrande() ?
                ConstantesUI.Fuentes.TEXTO_DIMINUTO :
                Font.font("Arial", 7));
        simbolo.setAlignment(Pos.CENTER);
        simbolo.setPrefSize(tamanoCelda, tamanoCelda);
        simbolos[y][x] = simbolo;

        // StackPane para superponer rectangle y label
        StackPane celda = new StackPane();
        celda.getChildren().addAll(rect, simbolo);

        grillaArena.add(celda, x, y);
    }

    /**
     * Actualiza la visualización del tablero
     * @paramtablero Tablero del juego con el estado actual
     * @param gestorAnimaciones Gestor para verificar animaciones activas
     */
    public void actualizar(Partida partida, GestorAnimaciones gestorAnimaciones) {
        Tablero tablero = partida.getTablero();

        // Primero limpiar todas las celdas que no tienen animaciones activas
        for (int y = 0; y < Tablero.ALTO; y++) {
            for (int x = 0; x < Tablero.ANCHO; x++) {
                Posicion pos = new Posicion(x, y);

                // Solo actualizar si no hay animación activa O si la animación no está corriendo
                if (gestorAnimaciones == null ||
                        !gestorAnimaciones.tieneAnimacionActiva(pos) ||
                        (gestorAnimaciones.tieneAnimacionActiva(pos) &&
                                !gestorAnimaciones.obtenerAnimacion(pos).estaActiva())) {
                    actualizarCasilla(pos, partida);
                }
            }
        }
    }

    /**
     * Actualiza solo el símbolo de la celda, manteniendo los efectos de animación
     */
    private void actualizarSoloSimbolo(Posicion posicion, Partida partida) {
        int x = posicion.getX();
        int y = posicion.getY();

        String simbolo = "";
        boolean hayEntidad = false;
        Tablero tablero = partida.getTablero();

        // Determinar el símbolo basado en lo que hay en la posición
        Tropa tropa = tablero.obtenerTropaEnPosicion(posicion);
        if (tropa != null && tropa.estaViva()) {
            simbolo = obtenerSimboloTropa(tropa);
            hayEntidad = true;
        } else {
            Torre torre = tablero.obtenerTorreEnPosicion(posicion);
            if (torre != null && torre.estaViva()) {
                simbolo = obtenerSimboloTorre(torre);
                hayEntidad = true;
            } else if (torre != null && !torre.estaViva()) {
                simbolo = "💀";
                hayEntidad = true;
            } else {
                // Para terreno, obtener símbolo apropiado
                simbolo = obtenerSimboloTerreno(posicion, partida);
            }
        }

        // Aplicar solo el símbolo, mantener el color actual (controlado por animación)
        simbolos[y][x].setText(simbolo);
        configurarTextoSimbolo(x, y, hayEntidad);
    }

    /**
     * Obtiene el símbolo para el terreno
     */
    private String obtenerSimboloTerreno(Posicion posicion, Partida partida) {
        TipoTerreno terreno = partida.getTablero().getTipoTerreno(posicion.getX(), posicion.getY());
        switch (terreno) {
            case RIO: return "🌊";
            case PUENTE: return "🌉";
            case TORRE_REY: return "🏰";
            case TORRE_PRINCESA: return "🏯";
            default: return "";
        }
    }

    /**
     * Actualiza una casilla individual
     * @param posicion Posición a actualizar
     * @paramtablero Tablero del juego
     */
    private void actualizarCasilla(Posicion posicion, Partida partida) {
        int x = posicion.getX();
        int y = posicion.getY();

        String simbolo = "";
        Color colorFondo = Color.LIGHTGRAY;
        boolean hayEntidad = false;

        Tablero tablero = partida.getTablero();

        // Prioridad: Tropa > Torre > Terreno
        Tropa tropa = tablero.obtenerTropaEnPosicion(posicion);
        if (tropa != null && tropa.estaViva()) {
            // Usar símbolos más descriptivos
            simbolo = obtenerSimboloTropa(tropa);
            colorFondo = obtenerColorJugador(tropa.getJugadorId(), true);
            hayEntidad = true;
        } else {
            // Verificar si hay una torre
            Torre torre = tablero.obtenerTorreEnPosicion(posicion);
            if (torre != null) {
                hayEntidad = true;
                if (torre.estaViva()) {
                    simbolo = obtenerSimboloTorre(torre);
                    colorFondo = obtenerColorJugador(torre.getJugadorId(), false);
                } else {
                    simbolo = "💀"; // Símbolo más claro para destruido
                    colorFondo = ConstantesUI.Colores.VIDA_DESTRUIDA;
                }
            } else {
                // Verificar tipo de terreno
                configurarTerreno(posicion, partida);
                return;
            }
        }

        // Aplicar cambios visuales
        casillas[y][x].setFill(colorFondo);
        simbolos[y][x].setText(simbolo);

        // Ajustar color del texto
        configurarTextoSimbolo(x, y, hayEntidad);
    }

    private String obtenerSimboloTorre(Torre torre) {
        String nombreClase = torre.getClass().getSimpleName();
        if (nombreClase.equals("TorreRey")) {
            return "♔"; // Rey
        } else {
            return "♖"; // Princesa
        }
    }

    private String obtenerSimboloTropa(Tropa tropa) {
        String nombre = tropa.getNombre().toLowerCase();

        if (nombre.contains("gigante")) return "👹";
        if (nombre.contains("caballero")) return "♞";
        if (nombre.contains("arquera")) return "🏹";
        if (nombre.contains("duende")) return "👺";
        if (nombre.contains("esqueleto")) return "💀";
        if (nombre.contains("mago")) return "🧙";
        if (nombre.contains("dragón")) return "🐉";
        if (nombre.contains("bárbaro")) return "⚔️";

        // Por defecto, primera letra en mayúscula
        return String.valueOf(Character.toUpperCase(tropa.getSimboloConsola()));
    }

    /**
     * Configura la visualización del terreno
     * @param posicion Posición del terreno
     * @paramtablero Tablero del juego
     */
    private void configurarTerreno(Posicion posicion, Partida partida) {
        int x = posicion.getX();
        int y = posicion.getY();

        TipoTerreno terreno = partida.getTablero().getTipoTerreno(x, y);
        String simbolo = "";
        Color colorFondo;

        switch (terreno) {
            case RIO -> {
                simbolo = "🌊"; // Símbolo más representativo
                colorFondo = ConstantesUI.Colores.ARENA_RIO;
            }
            case PUENTE -> {
                simbolo = "🌉"; // Símbolo más representativo
                colorFondo = ConstantesUI.Colores.ARENA_PUENTE;
            }
            case TORRE_REY -> {
                simbolo = "🏰";
                colorFondo = obtenerColorZona(posicion, partida);
            }
            case TORRE_PRINCESA -> {
                simbolo = "🏯";
                colorFondo = obtenerColorZona(posicion, partida);
            }
            case VACIO -> {
                simbolo = "";
                colorFondo = obtenerColorZona(posicion, partida);
            }
            default -> {
                simbolo = "";
                colorFondo = Color.LIGHTGRAY;
            }
        }

        casillas[y][x].setFill(colorFondo);
        simbolos[y][x].setText(simbolo);
        configurarTextoSimbolo(x, y, false);
    }

    /**
     * Obtiene el color según la zona del tablero, considerando las zonas de despliegue dinámicas.
     * @param posicion Posición a colorear
     * @param partida Estado de la partida
     * @return Color correspondiente a la zona
     */
    private Color obtenerColorZona(Posicion posicion, Partida partida) {
        boolean desplegableJ1 = partida.getJugador1().getZonaDespliegue().puedeDesplegarEn(posicion);
        boolean desplegableJ2 = partida.getJugador2().getZonaDespliegue().puedeDesplegarEn(posicion);

        if (desplegableJ1 && desplegableJ2) {
            return ConstantesUI.Colores.ARENA_ZONA_NEUTRAL; // Ambos pueden (puentes)
        } else if (desplegableJ1) {
            return ConstantesUI.Colores.ARENA_ZONA_J1;
        } else if (desplegableJ2) {
            return ConstantesUI.Colores.ARENA_ZONA_J2;
        } else {
            // Ni J1 ni J2 pueden desplegar aquí (ej. zona restringida del defensor)
            return Color.DARKGRAY; // Un color para indicar zona no desplegable
        }
    }

    /**
     * Obtiene el color según el jugador
     * @param jugadorId ID del jugador
     * @param esTropa true si es una tropa, false si es torre
     * @return Color correspondiente
     */
    private Color obtenerColorJugador(int jugadorId, boolean esTropa) {
        if (jugadorId == 1) {
            return esTropa ? Color.DODGERBLUE : Color.BLUE;
        } else {
            return esTropa ? Color.CRIMSON : Color.RED;
        }
    }

    /**
     * Configura el texto del símbolo en una casilla
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param hayEntidad true si hay una entidad en la casilla
     */
    private void configurarTextoSimbolo(int x, int y, boolean hayEntidad) {
        if (hayEntidad) {
            simbolos[y][x].setTextFill(Color.WHITE);
            // Aumentar tamaño de fuente para mejor legibilidad
            simbolos[y][x].setFont(
                    javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 12) // De 8 a 12
            );
        } else {
            simbolos[y][x].setTextFill(Color.DARKBLUE);
            // Aumentar también la fuente para terreno
            simbolos[y][x].setFont(
                    javafx.scene.text.Font.font("Arial", 10) // De 8 a 10
            );
        }
    }

    /**
     * Obtiene el componente JavaFX para agregarlo a la interfaz
     * @return VBox contenedor del tablero
     */
    public VBox obtenerComponente() {
        return contenedorArena;
    }

    /**
     * Obtiene la grilla del tablero para uso en animaciones
     * @return GridPane de la grilla del tablero
     */
    public GridPane obtenerGrillaArena() {
        return grillaArena;
    }

    /**
     * Obtiene una casilla específica del tablero
     * @param x Coordenada X
     * @param y Coordenada Y
     * @return StackPane de la casilla, o null si las coordenadas son inválidas
     */
    public StackPane obtenerCasilla(int x, int y) {
        if (x >= 0 && x < Tablero.ANCHO && y >= 0 && y < Tablero.ALTO) {
            int indice = y * Tablero.ANCHO + x;
            try {
                return (StackPane) grillaArena.getChildren().get(indice);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Resalta una posición específica del tablero
     * @param posicion Posición a resaltar
     * @param color Color del resaltado
     */
    public void resaltarPosicion(Posicion posicion, Color color) {
        int x = posicion.getX();
        int y = posicion.getY();

        if (x >= 0 && x < Tablero.ANCHO && y >= 0 && y < Tablero.ALTO) {
            casillas[y][x].setStroke(color);
            casillas[y][x].setStrokeWidth(2.0);
        }
    }

    /**
     * Limpia el resaltado de una posición
     * @param posicion Posición a limpiar
     */
    public void limpiarResaltado(Posicion posicion) {
        int x = posicion.getX();
        int y = posicion.getY();

        if (x >= 0 && x < Tablero.ANCHO && y >= 0 && y < Tablero.ALTO) {
            casillas[y][x].setStroke(Color.GRAY);
            casillas[y][x].setStrokeWidth(0.3);
        }
    }
}