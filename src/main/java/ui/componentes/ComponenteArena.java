package ui.componentes;

import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Componente que maneja la visualización del tablero de juego
 * Muestra las tropas, torres, terreno y efectos visuales
 */
public class ComponenteArena {

    private VBox contenedorArena;
    private GridPane grillaArena;
    private Canvas canvasArena; // Lienzo para dibujar rangos y efectos
    private Rectangle[][] casillas;
    private ImageView[][] imagenes;
    private Label[][] simbolos;
    private Map<String, Image> cacheImagenes;

    /**
     * Constructor - inicializa el componente del tablero
     */
    public ComponenteArena() {
        cacheImagenes = new HashMap<>();
        inicializarComponente();
        cargarImagenes();
    }

    /**
     * Carga todas las imágenes necesarias en el caché
     */
    private void cargarImagenes() {
        try {
            // Cargar imágenes de tropas usando los nombres EXACTOS de los archivos
            cargarImagen("gigante", "/imagenCartas/Card_Giant.png");
            cargarImagen("caballero", "/imagenCartas/Card_Knight.png");
            cargarImagen("arquera", "/imagenCartas/Card_Archer.png");
            cargarImagen("duende", "/imagenCartas/Card_Goblin.png");
            cargarImagen("esqueleto", "/imagenCartas/Card_skeletons.png");
            cargarImagen("mago", "/imagenCartas/Card_Wizard.png");
            cargarImagen("dragon", "/imagenCartas/Baby Dragon.png");
            cargarImagen("barbaro", "/imagenCartas/Card_Barbarians.png");
            cargarImagen("pekka", "/imagenCartas/Card_PEKKA.png");
            cargarImagen("valquiria", "/imagenCartas/Card_Valkyrie.png");
            cargarImagen("principe", "/imagenCartas/Card_Prince.png");
            cargarImagen("bruja", "/imagenCartas/Card_Witch.png");
            cargarImagen("golem", "/imagenCartas/Card_Golem.png");
            cargarImagen("minero", "/imagenCartas/Card_Miner.png");
            cargarImagen("montapuercos", "/imagenCartas/Card_Hog Rider.png");
            cargarImagen("mosquetero", "/imagenCartas/Card_Musketeer.png");
            cargarImagen("mini", "/imagenCartas/Card_Mini PEKKA.png");
            cargarImagen("globo", "/imagenCartas/Balloon.png");
            cargarImagen("esbirro", "/imagenCartas/Card_Minion.png");
            cargarImagen("princesa", "/imagenCartas/Card_Princess.png");
            cargarImagen("mago_electrico", "/imagenCartas/Card_Electro Wizard.png");
            cargarImagen("mago_hielo", "/imagenCartas/Card_Ice Wizard.png");
            cargarImagen("megacaballero", "/imagenCartas/Card_Mega Knight.png");
            cargarImagen("gigante_real", "/imagenCartas/Card_Royale Giant.png");
            cargarImagen("montacareneros", "/imagenCartas/Card_Ram Rider.png");
            cargarImagen("lanzadardos", "/imagenCartas/Card_Dart Goblin.png");
            cargarImagen("bandido", "/imagenCartas/Card_Bandit.png");
            cargarImagen("leñador", "/imagenCartas/Card_Lumberjack.png");
            cargarImagen("pescador", "/imagenCartas/Card_Fisherman.png");
            cargarImagen("verdugo", "/imagenCartas/Card_Executioner.png");
            cargarImagen("bombardero", "/imagenCartas/Card_Bomber.png");
            cargarImagen("cazador", "/imagenCartas/Card_Hunter.png");

        } catch (Exception e) {
            System.err.println("Error al cargar imágenes: " + e.getMessage());
        }
    }

    /**
     * Carga una imagen en el caché
     */
    private void cargarImagen(String clave, String ruta) {
        try {
            Image imagen = new Image(getClass().getResourceAsStream(ruta));
            if (!imagen.isError()) {
                cacheImagenes.put(clave, imagen);
            } else {
                System.err.println("Error al cargar imagen: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar imagen: " + ruta + " - " + e.getMessage());
        }
    }

    /**
     * Obtiene una imagen del caché
     */
    private Image obtenerImagen(String clave) {
        return cacheImagenes.get(clave);
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
        imagenes = new ImageView[Tablero.ALTO][Tablero.ANCHO];
        simbolos = new Label[Tablero.ALTO][Tablero.ANCHO];

        // Crear las casillas
        for (int y = 0; y < Tablero.ALTO; y++) {
            for (int x = 0; x < Tablero.ANCHO; x++) {
                crearCasilla(x, y);
            }
        }

        // Crear el Canvas del mismo tamaño que la grilla
        double canvasWidth = Tablero.ANCHO * (ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA + 1);
        double canvasHeight = Tablero.ALTO * (ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA + 1);
        canvasArena = new Canvas(canvasWidth, canvasHeight);
        canvasArena.setMouseTransparent(true); // El canvas no intercepta eventos de ratón

        // Usar un StackPane para superponer la grilla y el canvas
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(grillaArena, canvasArena);

        contenedorArena.getChildren().add(stackPane);
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

        // ImageView para las imágenes de tropas
        ImageView imageView = new ImageView();
        imageView.setFitWidth(tamanoCelda - 4);
        imageView.setFitHeight(tamanoCelda - 4);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imagenes[y][x] = imageView;

        // Label para el símbolo (para terrenos y torres)
        Label simbolo = new Label();
        simbolo.setFont(ConstantesUI.esPantallaGrande() ?
                ConstantesUI.Fuentes.TEXTO_DIMINUTO :
                Font.font("Arial", 7));
        simbolo.setAlignment(Pos.CENTER);
        simbolo.setPrefSize(tamanoCelda, tamanoCelda);
        simbolos[y][x] = simbolo;

        // StackPane para superponer rectangle, imagen y label
        StackPane celda = new StackPane();
        celda.getChildren().addAll(rect, imageView, simbolo);

        grillaArena.add(celda, x, y);
    }

    /**
     * Actualiza la visualización del tablero
     * @param partida Partida del juego con el estado actual
     * @param gestorAnimaciones Gestor para verificar animaciones activas
     * @param mostrarRangos Si se deben mostrar los rangos de detección
     */
    public void actualizar(Partida partida, GestorAnimaciones gestorAnimaciones, boolean mostrarRangos) {
        Tablero tablero = partida.getTablero();

        // 1. Actualizar la grilla base
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

        // 2. Dibujar o limpiar los rangos según el estado del checkbox
        if (mostrarRangos) {
            dibujarRangos(partida);
        } else {
            canvasArena.getGraphicsContext2D().clearRect(0, 0, canvasArena.getWidth(), canvasArena.getHeight());
        }
    }

    /**
     * Dibuja los rangos de detección de las tropas en el canvas
     */
    private void dibujarRangos(Partida partida) {
        GraphicsContext gc = canvasArena.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasArena.getWidth(), canvasArena.getHeight());

        double tamanoCelda = ConstantesUI.Dimensiones.TAMANO_CELDA_ARENA + 1; // Incluir el gap

        // Configurar el estilo de línea para los rangos
        gc.setLineDashes(8, 4); // 8 píxeles de línea, 4 de espacio
        gc.setLineWidth(1.0);

        for (Tropa tropa : partida.getTablero().getTropas()) {
            if (!tropa.estaViva()) continue;

            double centroX = (tropa.getPosicion().getX() + 0.5) * tamanoCelda;
            double centroY = (tropa.getPosicion().getY() + 0.5) * tamanoCelda;
            double radioPixels = tropa.getRangoDeteccion() * tamanoCelda;

            // Configurar el color del borde del círculo
            Color colorBorde = (tropa.getJugadorId() == 1) ? Color.rgb(0, 100, 255, 0.5) : Color.rgb(255, 50, 50, 0.5);
            gc.setStroke(colorBorde);

            // Dibujar solo el borde del círculo
            gc.strokeOval(centroX - radioPixels, centroY - radioPixels, radioPixels * 2, radioPixels * 2);
        }

        // Limpiar la configuración de guiones para no afectar otros posibles dibujos futuros
        gc.setLineDashes(null);
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
     * @param partida Partida del juego
     */
    private void actualizarCasilla(Posicion posicion, Partida partida) {
        int x = posicion.getX();
        int y = posicion.getY();

        String simbolo = "";
        Image imagen = null;
        Color colorFondo = Color.LIGHTGRAY;
        boolean hayEntidad = false;
        boolean esTropa = false;

        Tablero tablero = partida.getTablero();

        // Prioridad: Tropa > Torre > Terreno
        Tropa tropa = tablero.obtenerTropaEnPosicion(posicion);
        if (tropa != null && tropa.estaViva()) {
            // Usar imagen para tropas
            imagen = obtenerImagenTropa(tropa);
            colorFondo = obtenerColorJugador(tropa.getJugadorId(), true);
            hayEntidad = true;
            esTropa = true;
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

        if (esTropa) {
            // Mostrar imagen, ocultar símbolo
            imagenes[y][x].setImage(imagen);
            simbolos[y][x].setText("");
        } else {
            // Mostrar símbolo, ocultar imagen
            imagenes[y][x].setImage(null);
            simbolos[y][x].setText(simbolo);
            configurarTextoSimbolo(x, y, hayEntidad);
        }
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
     * Obtiene la imagen correspondiente a una tropa
     */
    private Image obtenerImagenTropa(Tropa tropa) {
        String nombre = tropa.getNombre().toLowerCase();

        // Normalizar nombre quitando acentos y puntos para comparación
        String nombreNormalizado = nombre
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace(".", "");  // Quitar puntos

        // Mapeo de nombres a claves de imagen
        if (nombreNormalizado.contains("mini") && nombreNormalizado.contains("pekka")) return obtenerImagen("mini");
        if (nombreNormalizado.contains("pekka")) return obtenerImagen("pekka");
        if (nombreNormalizado.contains("gigante") && nombreNormalizado.contains("real")) return obtenerImagen("gigante_real");
        if (nombreNormalizado.contains("gigante")) return obtenerImagen("gigante");
        if (nombreNormalizado.contains("caballero") && nombreNormalizado.contains("mega")) return obtenerImagen("megacaballero");
        if (nombreNormalizado.contains("caballero")) return obtenerImagen("caballero");
        if (nombreNormalizado.contains("arquera")) return obtenerImagen("arquera");
        if (nombreNormalizado.contains("duende")) return obtenerImagen("duende");
        if (nombreNormalizado.contains("esqueleto")) return obtenerImagen("esqueleto");
        if (nombreNormalizado.contains("mago") && nombreNormalizado.contains("electrico")) return obtenerImagen("mago_electrico");
        if (nombreNormalizado.contains("mago") && nombreNormalizado.contains("hielo")) return obtenerImagen("mago_hielo");
        if (nombreNormalizado.contains("mago") || nombreNormalizado.contains("maga")) return obtenerImagen("mago");
        if (nombreNormalizado.contains("dragon")) return obtenerImagen("dragon");
        if (nombreNormalizado.contains("barbaro") || nombreNormalizado.contains("barbara")) return obtenerImagen("barbaro");
        if (nombreNormalizado.contains("valquiria")) return obtenerImagen("valquiria");
        if (nombreNormalizado.contains("principe") && !nombreNormalizado.contains("princesa")) return obtenerImagen("principe");
        if (nombreNormalizado.contains("princesa")) return obtenerImagen("princesa");
        if (nombreNormalizado.contains("bruja") || nombreNormalizado.contains("brujo")) return obtenerImagen("bruja");
        if (nombreNormalizado.contains("golem")) return obtenerImagen("golem");
        if (nombreNormalizado.contains("minero") || nombreNormalizado.contains("minera")) return obtenerImagen("minero");
        if (nombreNormalizado.contains("montapuercos") || nombreNormalizado.contains("puerco") || nombreNormalizado.contains("hog")) return obtenerImagen("montapuercos");
        if (nombreNormalizado.contains("carnero") || nombreNormalizado.contains("ram")) return obtenerImagen("montacareneros");
        if (nombreNormalizado.contains("mosquetero") || nombreNormalizado.contains("mosquetera")) return obtenerImagen("mosquetero");
        if (nombreNormalizado.contains("globo")) return obtenerImagen("globo");
        if (nombreNormalizado.contains("esbirro") || nombreNormalizado.contains("minion")) return obtenerImagen("esbirro");
        if (nombreNormalizado.contains("bandido") || nombreNormalizado.contains("bandida")) return obtenerImagen("bandido");
        if (nombreNormalizado.contains("leñador") || nombreNormalizado.contains("lenador")) return obtenerImagen("leñador");
        if (nombreNormalizado.contains("pescador")) return obtenerImagen("pescador");
        if (nombreNormalizado.contains("verdugo")) return obtenerImagen("verdugo");
        if (nombreNormalizado.contains("bombardero")) return obtenerImagen("bombardero");
        if (nombreNormalizado.contains("cazador")) return obtenerImagen("cazador");
        if (nombreNormalizado.contains("lanzadardos") || nombreNormalizado.contains("dardo")) return obtenerImagen("lanzadardos");

        // Si no se encuentra la imagen, mostrar advertencia
        System.out.println("ADVERTENCIA: No se encontró imagen para: '" + nombre + "'");
        return null;
    }

    /**
     * Configura la visualización del terreno
     * @param posicion Posición del terreno
     * @param partida Partida del juego
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
        imagenes[y][x].setImage(null); // Sin imagen para terreno
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