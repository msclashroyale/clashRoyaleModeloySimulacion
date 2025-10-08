package ui.componentes;

import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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
        // Rectangle para el color de fondo
        Rectangle rect = new Rectangle(
                ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA,
                ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA
        );
        rect.setStroke(Color.GRAY);
        rect.setStrokeWidth(0.3);
        casillas[y][x] = rect;

        // Label para el símbolo
        Label simbolo = new Label();
        simbolo.setFont(ConstantesUI.Fuentes.TEXTO_DIMINUTO);
        simbolo.setAlignment(Pos.CENTER);
        simbolo.setPrefSize(
                ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA,
                ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA
        );
        simbolos[y][x] = simbolo;

        // StackPane para superponer rectangle y label
        StackPane celda = new StackPane();
        celda.getChildren().addAll(rect, simbolo);

        grillaArena.add(celda, x, y);
    }

    /**
     * Actualiza la visualización del tablero
     * @param tablero Tablero del juego con el estado actual
     * @param gestorAnimaciones Gestor para verificar animaciones activas
     */
    public void actualizar(Partida partida, GestorAnimaciones gestorAnimaciones) {
        Tablero tablero = partida.getTablero();
        for (int y = 0; y < Tablero.ALTO; y++) {
            for (int x = 0; x < Tablero.ANCHO; x++) {
                Posicion pos = new Posicion(x, y);

                // Si hay animación activa en esta posición, no modificar los colores
                if (gestorAnimaciones != null && gestorAnimaciones.tieneAnimacionActiva(pos)) {
                    continue;
                }

                actualizarCasilla(pos, partida);
            }
        }
    }

    /**
     * Actualiza una casilla individual
     * @param posicion Posición a actualizar
     * @param tablero Tablero del juego
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
            simbolo = String.valueOf(tropa.getSimboloConsola());
            colorFondo = obtenerColorJugador(tropa.getJugadorId(), true);
            hayEntidad = true;
        } else {
            // Verificar si hay una torre
            Torre torre = tablero.obtenerTorreEnPosicion(posicion);
            if (torre != null) {
                hayEntidad = true;
                if (torre.estaViva()) {
                    simbolo = String.valueOf(torre.getSimboloConsola());
                    colorFondo = obtenerColorJugador(torre.getJugadorId(), false);
                } else {
                    simbolo = "X";
                    colorFondo = ConstantesUI.Colores.VIDA_DESTRUIDA;
                }
            } else {
                // Verificar tipo de terreno
                configurarTerreno(posicion, partida);
                return; // El método configurarTerreno ya maneja la actualización visual
            }
        }

        // Aplicar cambios visuales
        casillas[y][x].setFill(colorFondo);
        simbolos[y][x].setText(simbolo);

        // Ajustar color del texto
        configurarTextoSimbolo(x, y, hayEntidad);
    }

    /**
     * Configura la visualización del terreno
     * @param posicion Posición del terreno
     * @param tablero Tablero del juego
     */
    private void configurarTerreno(Posicion posicion, Partida partida) {
        int x = posicion.getX();
        int y = posicion.getY();

        TipoTerreno terreno = partida.getTablero().getTipoTerreno(x, y);
        String simbolo = "";
        Color colorFondo;

        switch (terreno) {
            case RIO -> {
                simbolo = "~";
                colorFondo = ConstantesUI.Colores.ARENA_RIO;
            }
            case PUENTE -> {
                simbolo = "=";
                colorFondo = ConstantesUI.Colores.ARENA_PUENTE;
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
            simbolos[y][x].setFont(
                    javafx.scene.text.Font.font("Arial", FontWeight.BOLD, 8)
            );
        } else {
            simbolos[y][x].setTextFill(Color.DARKBLUE);
            simbolos[y][x].setFont(ConstantesUI.Fuentes.TEXTO_DIMINUTO);
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