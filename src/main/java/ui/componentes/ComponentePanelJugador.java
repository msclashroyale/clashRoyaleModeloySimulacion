package ui.componentes;
import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import tablero.Tablero;
import cartas.Carta;
import jugador.Jugador;
import jugador.SistemaElixir;
import ui.constantes.ConstantesUI;

import java.io.InputStream;
import java.util.List;

/**
 * Componente que maneja toda la información visual de un jugador
 * Incluye estadísticas, cartas, torres, tropas y leyenda
 */
public class ComponentePanelJugador {

    private final int jugadorId;
    private VBox contenedorPrincipal;
    private javafx.animation.Timeline animacionPulsoElixir;

    private StackPane barraContenedorElixir;
    private javafx.animation.Timeline animacionProgreso;
    private double progresoActual = 0.5;
    private javafx.animation.Timeline animacionPulso;


    private ProgressBar barraProgresoElixir;


    private Label etiquetaTitulo;

    // Elementos de información del jugador
    private ProgressBar barraElixir;
    private Label etiquetaElixirTexto;
    private Label etiquetaTropas;
    private Label etiquetaTorres;
    private Label etiquetaCartas;
    private Label etiquetaEstrategia;

    // Paneles de contenido
    private VBox panelCartas;
    private VBox panelTorres;
    private VBox panelTropasVivas;

    /**
     * Constructor del panel de jugador
     * @param jugadorId ID del jugador (1 o 2)
     */
    public ComponentePanelJugador(int jugadorId) {
        this.jugadorId = jugadorId;
        inicializarComponente();
    }

    /**
     * Inicializa todos los elementos del panel de jugador
     */
    private void inicializarComponente() {
        contenedorPrincipal = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PANEL);

        // USAR ANCHO RESPONSIVE
        double anchoPanel = ConstantesUI.Dimensiones.getAnchoPanelJugador();
        contenedorPrincipal.setPrefWidth(anchoPanel);
        contenedorPrincipal.setMinWidth(anchoPanel);
        contenedorPrincipal.setMaxWidth(anchoPanel);

        // Altura también responsive pero con límites
        double altoMaximo = ConstantesUI.Dimensiones.ALTO_VENTANA * 0.9;
        double altoMinimo = ConstantesUI.Dimensiones.ALTO_VENTANA * 0.7;

        contenedorPrincipal.setPrefHeight(altoMaximo);
        contenedorPrincipal.setMinHeight(altoMinimo);
        contenedorPrincipal.setMaxHeight(altoMaximo);

        contenedorPrincipal.setAlignment(Pos.TOP_CENTER);

        // Aplicar estilo según el jugador
        String estiloPanel = jugadorId == 1 ?
                ConstantesUI.Estilos.PANEL_JUGADOR_1 :
                ConstantesUI.Estilos.PANEL_JUGADOR_2;
        contenedorPrincipal.setStyle(estiloPanel);

        crearElementosPanel();
    }

    /**
     * Crea todos los elementos del panel
     */
    private void crearElementosPanel() {
        // Título del jugador
        Label titulo = crearTituloJugador();

        // Información básica del jugador
        VBox infoBasica = crearInfoBasica();

        // Panel de cartas en mano
        VBox seccionCartas = crearSeccionCartas();

        // Panel de torres
        VBox seccionTorres = crearSeccionTorres();

        // Panel de tropas vivas
        VBox seccionTropas = crearSeccionTropasVivas();

        // Leyenda
        VBox leyenda = crearLeyenda();

        // Separadores
        Separator sep1 = crearSeparador();
        Separator sep2 = crearSeparador();
        Separator sep3 = crearSeparador();

        // Ensamblar todo
        contenedorPrincipal.getChildren().addAll(
                titulo,
                infoBasica,
                sep1,
                seccionCartas,
                sep2,
                seccionTorres,
                sep3,
                seccionTropas,
                leyenda
        );
    }

    /**
     * Crea el título del panel del jugador
     */
    private Label crearTituloJugador() {
        String textoTitulo = jugadorId == 1 ?
                ConstantesUI.Etiquetas.TITULO_JUGADOR_1 :
                ConstantesUI.Etiquetas.TITULO_JUGADOR_2;

        etiquetaTitulo = new Label(textoTitulo);
        etiquetaTitulo.setFont(ConstantesUI.Fuentes.TITULO_MEDIANO); // Aumentado de TITULO_PEQUENO
        etiquetaTitulo.setTextFill(Color.WHITE);
        etiquetaTitulo.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0.8, 2, 2);");

        return etiquetaTitulo;
    }

    /**
     * Crea la sección de información básica del jugador
     */
    private VBox crearInfoBasica() {
        VBox infoBox = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PEQUENO); // Aumentado espaciado

        // Crear contenedor para la barra de elixir (se mantiene igual)
        VBox contenedorElixir = crearBarraElixir();

        etiquetaTropas = new Label("Tropas: 0");
        etiquetaTorres = new Label("Torres: 3/3");
        etiquetaCartas = new Label("Cartas jugadas: 0");
        etiquetaEstrategia = new Label("Estrategia: N/A");

        // Aplicar estilo a las etiquetas con fuentes más grandes
        Label[] etiquetas = {etiquetaTropas, etiquetaTorres, etiquetaCartas, etiquetaEstrategia};
        for (Label etiqueta : etiquetas) {
            etiqueta.setTextFill(Color.WHITE);
            etiqueta.setFont(ConstantesUI.Fuentes.TEXTO_GRANDE); // Aumentado de TEXTO_PEQUENO
            etiqueta.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 2, 0.6, 1, 1);");
        }

        infoBox.getChildren().add(contenedorElixir);
        infoBox.getChildren().addAll(etiquetas);
        return infoBox;
    }

    /**
     * Crea la barra de elixir con su etiqueta - VERSIÓN MEJORADA
     */
    private VBox crearBarraElixir() {
        VBox contenedor = new VBox();
        contenedor.setAlignment(Pos.CENTER);
        contenedor.setMaxWidth(Double.MAX_VALUE);

        if (ConstantesUI.esPantallaGrande()) {
            contenedor.setPadding(new Insets(10, 15, 15, 15));
        } else {
            contenedor.setPadding(new Insets(8, 12, 12, 12));
        }

        // Contenedor principal - SIN FONDO VISIBLE
        StackPane barraPrincipal = new StackPane();
        barraPrincipal.setMaxWidth(Double.MAX_VALUE);
        barraPrincipal.setPrefHeight(35);

        // ProgressBar que ocupa TODO el espacio
        barraProgresoElixir = new ProgressBar(0.5);
        barraProgresoElixir.setMaxWidth(Double.MAX_VALUE);
        barraProgresoElixir.setPrefHeight(35); // Misma altura que el contenedor
        barraProgresoElixir.setStyle(
                "-fx-background-radius: 18; " +
                        "-fx-border-radius: 18; " +
                        "-fx-background-color: #2d3047; " + // Fondo de la barra vacía
                        "-fx-accent: #8a2be2; " + // Color del progreso
                        "-fx-padding: 0;" // Eliminar padding interno
        );

        // Texto superpuesto
        etiquetaElixirTexto = new Label("⏣ 5/10");
        etiquetaElixirTexto.setTextFill(Color.WHITE);
        etiquetaElixirTexto.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 14));
        etiquetaElixirTexto.setStyle(
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 8, 0.8, 2, 2);"
        );

        // Efecto de brillo interno (opcional)
        Region efectoBrillo = new Region();
        efectoBrillo.setStyle(
                "-fx-background-color: linear-gradient(to bottom, rgba(255,255,255,0.2), transparent 70%); " +
                        "-fx-background-radius: 18;"
        );
        efectoBrillo.setPrefHeight(35);

        // Organizar elementos - la ProgressBar es el fondo principal
        barraPrincipal.getChildren().addAll(barraProgresoElixir, efectoBrillo, etiquetaElixirTexto);

        // Guardar referencia para animaciones
        this.barraContenedorElixir = barraPrincipal;

        contenedor.getChildren().add(barraPrincipal);
        return contenedor;
    }

    /**
     * Crea la sección de cartas en mano
     */
    private VBox crearSeccionCartas() {
        VBox seccionCartas = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);
        seccionCartas.setAlignment(Pos.CENTER);

        // ALTURA RESPONSIVE
        double alto = ConstantesUI.Dimensiones.getAltoSeccionCartas();
        seccionCartas.setPrefHeight(alto);
        seccionCartas.setMinHeight(alto);
        seccionCartas.setMaxHeight(alto);

        Label tituloCartas = new Label(ConstantesUI.Etiquetas.CARTAS_EN_MANO);
        tituloCartas.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        tituloCartas.setTextFill(Color.WHITE);

        // Panel de cartas con tamaño controlado
        panelCartas = new VBox();
        panelCartas.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);
        panelCartas.setPrefHeight(alto - 40); // Restar espacio del título
        panelCartas.setMinHeight(alto - 40);
        panelCartas.setMaxHeight(alto - 40);
        panelCartas.setAlignment(Pos.CENTER);
        panelCartas.setPadding(new Insets(8)); // Padding reducido

        seccionCartas.getChildren().addAll(tituloCartas, panelCartas);
        return seccionCartas;
    }

    /**
     * Crea la sección de estado de torres
     */
    private VBox crearSeccionTorres() {
        VBox seccionTorres = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PEQUENO);
        seccionTorres.setAlignment(Pos.CENTER);

        // ALTURA RESPONSIVE
        double alto = ConstantesUI.Dimensiones.getAltoSeccionTorres();
        seccionTorres.setPrefHeight(alto);
        seccionTorres.setMinHeight(alto);
        seccionTorres.setMaxHeight(alto);

        Label tituloTorres = new Label(ConstantesUI.Etiquetas.ESTADO_TORRES);
        tituloTorres.setFont(ConstantesUI.Fuentes.TEXTO_GRANDE);
        tituloTorres.setTextFill(Color.WHITE);
        tituloTorres.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 2, 0.6, 1, 1);");

        panelTorres = new VBox(4);
        panelTorres.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);
        panelTorres.setPrefHeight(alto - 40); // Ajustar por el título

        seccionTorres.getChildren().addAll(tituloTorres, panelTorres);
        return seccionTorres;
    }

    /**
     * Crea la sección de tropas vivas
     */
    private VBox crearSeccionTropasVivas() {
        VBox seccionTropas = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PEQUENO);
        seccionTropas.setAlignment(Pos.CENTER);

        // ALTURA RESPONSIVE
        double altoSeccion = ConstantesUI.Dimensiones.getAltoSeccionTropas();
        seccionTropas.setPrefHeight(altoSeccion);
        seccionTropas.setMinHeight(altoSeccion);
        seccionTropas.setMaxHeight(altoSeccion);

        Label tituloTropas = new Label(ConstantesUI.Etiquetas.TROPAS_VIVAS);
        tituloTropas.setFont(ConstantesUI.Fuentes.TEXTO_GRANDE);
        tituloTropas.setTextFill(Color.WHITE);
        tituloTropas.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 2, 0.6, 1, 1);");

        ScrollPane scrollPane = new ScrollPane();

        // Configuración del ScrollPane
        double altoScroll = ConstantesUI.Dimensiones.getAltoScrollTropas();
        scrollPane.setPrefHeight(altoScroll);
        scrollPane.setMinHeight(altoScroll);
        scrollPane.setMaxHeight(altoScroll);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        panelTropasVivas = new VBox(4);
        panelTropasVivas.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);

        scrollPane.setContent(panelTropasVivas);

        seccionTropas.getChildren().addAll(tituloTropas, scrollPane);
        return seccionTropas;
    }

    /**
     * Crea la leyenda de símbolos
     */
    private VBox crearLeyenda() {
        VBox leyendaBox = new VBox(4); // Aumentado espaciado
        leyendaBox.setAlignment(Pos.CENTER);

        Label tituloLeyenda = new Label(ConstantesUI.Etiquetas.TITULO_LEYENDA);
        tituloLeyenda.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        tituloLeyenda.setTextFill(Color.WHITE);
        tituloLeyenda.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 2, 0.6, 1, 1);");

        HBox elementosLeyenda = new HBox(10); // Aumentado espaciado
        elementosLeyenda.setAlignment(Pos.CENTER);

        for (String elemento : ConstantesUI.Etiquetas.ELEMENTOS_LEYENDA) {
            Label label = new Label(elemento);
            label.setTextFill(Color.WHITE);
            label.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
            label.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 1, 0.6, 1, 1);");
            elementosLeyenda.getChildren().add(label);
        }

        leyendaBox.getChildren().addAll(tituloLeyenda, elementosLeyenda);
        return leyendaBox;
    }

    /**
     * Crea un separador visual
     */
    private Separator crearSeparador() {
        Separator separador = new Separator();
        separador.setPrefWidth(180);
        return separador;
    }

    /**
     * Actualiza toda la información del panel con los datos actuales del jugador
     * CORREGIDO: Ahora usa Tablero en lugar de Arena
     */
    public void actualizar(Jugador jugador, Tablero tablero) {
        // Actualizar información básica
        actualizarInfoBasica(jugador, tablero);

        // Actualizar cartas en mano
        actualizarPanelCartas(jugador.getMazo().getCartasEnMano(), jugador.getSistemaElixir());

        // Actualizar estado de torres
        actualizarPanelTorres(tablero.getTorresJugador(jugadorId));

        // Actualizar tropas vivas
        actualizarPanelTropasVivas(tablero.getTropasJugador(jugadorId));
    }

    private void actualizarBarraElixir(int elixirActual, int elixirMaximo) {
        double nuevoProgreso = (double) elixirActual / elixirMaximo;

        // Actualizar texto
        etiquetaElixirTexto.setText("⏣ " + elixirActual + "/" + elixirMaximo);

        // Determinar color basado en el progreso
        String colorBarra;
        String colorTexto;

        if (nuevoProgreso >= 0.8) {
            colorBarra = "#00ff88"; // Verde brillante
            colorTexto = "#ccffeb";
        } else if (nuevoProgreso >= 0.5) {
            colorBarra = "#8a2be2"; // Morado
            colorTexto = "#e6d9ff";
        } else if (nuevoProgreso >= 0.3) {
            colorBarra = "#ffaa00"; // Naranja
            colorTexto = "#fff5e6";
        } else {
            colorBarra = "#ff4444"; // Rojo
            colorTexto = "#ffe6e6";
        }

        // Aplicar color al texto
        etiquetaElixirTexto.setStyle(
                "-fx-text-fill: " + colorTexto + "; " +
                        "-fx-font-weight: extra-bold; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.9), 8, 0.8, 2, 2);"
        );

        // Aplicar estilo a la barra
        String estiloBarra =
                "-fx-background-radius: 18; " +
                        "-fx-border-radius: 18; " +
                        "-fx-background-color: #2d3047; " +
                        "-fx-accent: " + colorBarra + "; " +
                        "-fx-padding: 0;";

        barraProgresoElixir.setStyle(estiloBarra);

        // Animación suave del progreso (manteniendo tu animación)
        animarProgresoSuave(nuevoProgreso);

        // Efectos especiales (manteniendo tus efectos)
        manejarEfectosEspeciales(nuevoProgreso);
    }

    /**
     * Animación suave del progreso - MANTENIENDO TU CÓDIGO
     */
    private void animarProgresoSuave(double nuevoProgreso) {
        javafx.animation.Timeline progresoAnimation = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(800),
                        new javafx.animation.KeyValue(barraProgresoElixir.progressProperty(), nuevoProgreso)
                )
        );
        progresoAnimation.play();
    }

    /**
     * Animación suave para la ProgressBar
     */
    private void animarProgresoSuave(double nuevoProgreso, String colorBarra) {
        // Crear transición para el progreso
        javafx.animation.Timeline progresoAnimation = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(800),
                        new javafx.animation.KeyValue(barraProgresoElixir.progressProperty(), nuevoProgreso)
                )
        );

        // Crear transición para el color
        String estiloFinal =
                "-fx-background-radius: 16; " +
                        "-fx-border-radius: 16; " +
                        "-fx-background-color: #2d3047; " +
                        "-fx-accent: " + colorBarra + "; " +
                        "-fx-padding: 2;";

        // Aplicar estilo inmediatamente para el color
        barraProgresoElixir.setStyle(estiloFinal);

        // Ejecutar animación del progreso
        progresoAnimation.play();

        // Guardar progreso actual
        progresoActual = nuevoProgreso;
    }


    /**
     * Efecto de pulso intenso para la barra llena
     */
    private void aplicarEfectoPulsoIntenso() {
        if (animacionPulso != null) {
            animacionPulso.stop();
        }

        animacionPulso = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleXProperty(), 1.0),
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleYProperty(), 1.0),
                        new javafx.animation.KeyValue(barraContenedorElixir.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e); " +
                                        "-fx-border-color: #2d3047; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0.5, 0, 5);")
                ),
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(500),
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleXProperty(), 1.03),
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleYProperty(), 1.03),
                        new javafx.animation.KeyValue(barraContenedorElixir.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, #2a2a4e, #1a2a4e); " +
                                        "-fx-border-color: #00ff88; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,255,136,0.6), 20, 0.7, 0, 8);")
                ),
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(1000),
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleXProperty(), 1.0),
                        new javafx.animation.KeyValue(barraContenedorElixir.scaleYProperty(), 1.0),
                        new javafx.animation.KeyValue(barraContenedorElixir.styleProperty(),
                                "-fx-background-color: linear-gradient(to bottom, #1a1a2e, #16213e); " +
                                        "-fx-border-color: #2d3047; " +
                                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0.5, 0, 5);")
                )
        );
        animacionPulso.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        animacionPulso.play();
    }

    /**
     * Efecto de destello suave cuando aumenta el elixir
     */
    private void aplicarEfectoDestelloSuave() {
        Circle destello = new Circle(4, Color.WHITE);
        destello.setOpacity(0.0);
        destello.setCenterX(barraProgresoElixir.getWidth() * progresoActual);
        destello.setCenterY(barraContenedorElixir.getHeight() / 2);

        barraContenedorElixir.getChildren().add(destello);

        javafx.animation.Timeline animDestello = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(destello.opacityProperty(), 0.0),
                        new javafx.animation.KeyValue(destello.radiusProperty(), 4)
                ),
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(200),
                        new javafx.animation.KeyValue(destello.opacityProperty(), 0.7),
                        new javafx.animation.KeyValue(destello.radiusProperty(), 8)
                ),
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(400),
                        new javafx.animation.KeyValue(destello.opacityProperty(), 0.0),
                        new javafx.animation.KeyValue(destello.radiusProperty(), 12)
                )
        );

        animDestello.setOnFinished(e -> barraContenedorElixir.getChildren().remove(destello));
        animDestello.play();
    }

    private void removerEfectoPulso() {
        if (animacionPulso != null) {
            animacionPulso.stop();
            animacionPulso = null;
            barraContenedorElixir.setScaleX(1.0);
            barraContenedorElixir.setScaleY(1.0);
        }
    }

    /**
     * Animación suave del progreso de la barra
     */
    private void animarProgresoElixir(double nuevoProgreso, String colorGradiente) {
        // Detener animación anterior si existe
        if (animacionProgreso != null) {
            animacionProgreso.stop();
        }

        // Crear nueva animación
        animacionProgreso = new javafx.animation.Timeline();

        // KeyValue para animar el ancho de la barra
        double anchoFinal = barraContenedorElixir.getWidth() * nuevoProgreso;

        javafx.animation.KeyValue kvAncho = new javafx.animation.KeyValue(
                barraProgresoElixir.prefWidthProperty(), anchoFinal
        );

        javafx.animation.KeyValue kvColor = new javafx.animation.KeyValue(
                barraProgresoElixir.styleProperty(),
                "-fx-background-color: " + colorGradiente + "; " +
                        "-fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, " +
                        (nuevoProgreso >= 0.9 ? "rgba(0, 255, 136, 0.7)" : "rgba(138, 43, 226, 0.5)") +
                        ", 15, 0.3, 0, 2);"
        );

        javafx.animation.KeyFrame kf = new javafx.animation.KeyFrame(
                javafx.util.Duration.millis(600), kvAncho, kvColor
        );

        animacionProgreso.getKeyFrames().add(kf);
        animacionProgreso.play();

        // Actualizar progreso actual
        progresoActual = nuevoProgreso;
    }

    /**
     * Maneja efectos especiales como pulso y partículas
     */
    private void manejarEfectosEspeciales(double progreso) {
        // Efecto de pulso cuando está casi lleno
        if (progreso >= 0.9) {
            aplicarEfectoPulsoIntenso();
        } else {
            removerEfectoPulso();
        }

        // Efecto de destello cuando aumenta el elixir
        if (progreso > progresoActual) {
            aplicarEfectoDestelloSuave();
        }
    }


    /**
     * Efecto de destello cuando aumenta el elixir
     */
    private void aplicarEfectoDestello() {
        Circle destello = new Circle(3, Color.WHITE);
        destello.setOpacity(0.8);
        destello.setCenterX(barraProgresoElixir.getWidth());
        destello.setCenterY(barraProgresoElixir.getHeight() / 2);

        barraContenedorElixir.getChildren().add(destello);

        // Animación del destello
        javafx.animation.Timeline animDestello = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.ZERO,
                        new javafx.animation.KeyValue(destello.opacityProperty(), 0.8),
                        new javafx.animation.KeyValue(destello.radiusProperty(), 3)
                ),
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.millis(300),
                        new javafx.animation.KeyValue(destello.opacityProperty(), 0),
                        new javafx.animation.KeyValue(destello.radiusProperty(), 8)
                )
        );

        animDestello.setOnFinished(e -> barraContenedorElixir.getChildren().remove(destello));
        animDestello.play();
    }


    private void actualizarInfoBasica(Jugador jugador, Tablero tablero) {
        // Actualizar título
        etiquetaTitulo.setText(jugador.getNombre() + " (Nv. " + jugador.getNivel() + ")");

        // ACTUALIZACIÓN DE ELIXIR CON PROGRESSBAR
        int elixirActual = jugador.getSistemaElixir().getElixirActual();
        int elixirMaximo = jugador.getSistemaElixir().getElixirMaximo();

        actualizarBarraElixir(elixirActual, elixirMaximo);

        // Resto de la información
        etiquetaTropas.setText("Tropas: " + tablero.contarTropasVivas(jugadorId));
        etiquetaTorres.setText("Torres: " + contarTorresVivas(tablero.getTorresJugador(jugadorId)) + "/3");
        etiquetaCartas.setText("Cartas jugadas: " + jugador.getEstadisticas().getCartasJugadas());
        etiquetaEstrategia.setText("Estrategia: " + jugador.getEstrategiaIA().getClass().getSimpleName());
    }

    private void manejarAnimacionPulso(double progreso) {
        // Limpiar animación anterior si existe
        if (animacionPulsoElixir != null) {
            animacionPulsoElixir.stop();
            animacionPulsoElixir = null;
            barraElixir.setScaleX(1.0);
            barraElixir.setScaleY(1.0);
        }

        // Crear nueva animación si el elixir está casi lleno
        if (progreso >= 0.9) {
            animacionPulsoElixir = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.millis(0),
                            new javafx.animation.KeyValue(barraElixir.scaleXProperty(), 1.0),
                            new javafx.animation.KeyValue(barraElixir.scaleYProperty(), 1.0)
                    ),
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.millis(500),
                            new javafx.animation.KeyValue(barraElixir.scaleXProperty(), 1.03),
                            new javafx.animation.KeyValue(barraElixir.scaleYProperty(), 1.03)
                    ),
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.millis(1000),
                            new javafx.animation.KeyValue(barraElixir.scaleXProperty(), 1.0),
                            new javafx.animation.KeyValue(barraElixir.scaleYProperty(), 1.0)
                    )
            );
            animacionPulsoElixir.setCycleCount(javafx.animation.Timeline.INDEFINITE);
            animacionPulsoElixir.play();
        }
    }

    /**
     * Actualiza el panel de cartas en mano
     */
    private void actualizarPanelCartas(List<Carta> cartasEnMano, SistemaElixir elixir) {
        panelCartas.getChildren().clear();

        if (cartasEnMano.isEmpty()) {
            Label sinCartas = new Label("No hay cartas en mano");
            sinCartas.setTextFill(Color.LIGHTGRAY);
            sinCartas.setFont(Font.font("Arial", FontWeight.NORMAL, 11)); // Fuente ligeramente más pequeña
            panelCartas.getChildren().add(sinCartas);
            return;
        }

        // Contenedor horizontal con ESPACIADO REDUCIDO (cambio 3)
        HBox cartasContainer = new HBox(5); // Reducido de 15 a 5
        cartasContainer.setAlignment(Pos.CENTER);
        cartasContainer.setPrefHeight(ConstantesUI.Dimensiones.getAltoSeccionCartas() - 40);

        for (Carta carta : cartasEnMano) {
            boolean disponible = elixir.puedeGastar(carta.getCostoElixir());
            StackPane cartaVisual = crearCartaVisual(carta, disponible);
            cartasContainer.getChildren().add(cartaVisual);
        }

        panelCartas.getChildren().add(cartasContainer);
    }

    private StackPane crearCartaVisual(Carta carta, boolean disponible) {
        StackPane cartaPane = new StackPane();

        // USAR TAMAÑOS RESPONSIVE
        double anchoCarta = ConstantesUI.Dimensiones.getAnchoCarta();
        double altoCarta = ConstantesUI.Dimensiones.getAltoCarta();

        cartaPane.setPrefSize(anchoCarta, altoCarta);
        cartaPane.setMinSize(anchoCarta, altoCarta);
        cartaPane.setMaxSize(anchoCarta, altoCarta);

        // Estilo base con fondos MUY transparentes
        String estiloBase =
                "-fx-background-radius: 10; " + // Reducido de 12
                        "-fx-border-radius: 10; " +     // Reducido de 12
                        "-fx-border-width: 2; " +       // Reducido de 3
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0.4, 2, 2); "; // Efecto más sutil

        String estiloCompleto;
        if (disponible) {
            estiloCompleto = estiloBase +
                    "-fx-background-color: rgba(0,0,0,0.1); " +
                    "-fx-border-color: #10B981;";
        } else {
            estiloCompleto = estiloBase +
                    "-fx-background-color: rgba(0,0,0,0.2); " +
                    "-fx-border-color: #6B7280;";
        }

        cartaPane.setStyle(estiloCompleto);

        // Contenedor de imagen
        StackPane imagenContainer = new StackPane();
        imagenContainer.setAlignment(Pos.CENTER);
        imagenContainer.setStyle("-fx-background-radius: 8; -fx-background-color: transparent;");

        // Cargar la imagen real de la carta
        ImageView imagenCarta = crearImagenCarta(carta, disponible);

        // Ajustar la imagen a los nuevos tamaños
        double anchoImagen = anchoCarta - 4;
        double altoImagen = altoCarta - 4;
        imagenCarta.setFitWidth(anchoImagen);
        imagenCarta.setFitHeight(altoImagen);
        imagenCarta.setPreserveRatio(true);

        // Efecto de brillo MUY sutil
        Region efectoBrillo = new Region();
        efectoBrillo.setStyle(
                "-fx-background-color: radial-gradient(center 50% 50%, radius 80%, " +
                        (disponible ? "rgba(255,255,255,0.05)" : "rgba(255,255,255,0.02)") +
                        ", transparent); " +
                        "-fx-background-radius: 8;"
        );
        efectoBrillo.setPrefSize(anchoImagen, altoImagen);

        imagenContainer.getChildren().addAll(imagenCarta, efectoBrillo);

        // Contenedor del costo
        StackPane costoContainer = new StackPane();
        costoContainer.setAlignment(Pos.TOP_RIGHT);
        costoContainer.setStyle(
                "-fx-background-color: rgba(0,0,0,0); " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 1 4 1 4; " + // Padding reducido
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 2, 0.5, 1, 1);"
        );

        Label costoLabel = new Label("⏣" + carta.getCostoElixir());
        costoLabel.setTextFill(Color.GOLD);
        costoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 9)); // Fuente más pequeña
        costoContainer.getChildren().add(costoLabel);

        // Contenedor del nombre
        HBox nombreContainer = new HBox();
        nombreContainer.setAlignment(Pos.BOTTOM_CENTER);
        nombreContainer.setPadding(new Insets(0, 0, 2, 0)); // Padding inferior reducido

        Label nombreLabel = new Label(carta.getNombre());
        nombreLabel.setTextFill(Color.WHITE);
        nombreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 7)); // Fuente más pequeña
        nombreLabel.setWrapText(true);
        nombreLabel.setTextAlignment(TextAlignment.CENTER);
        nombreLabel.setMaxWidth(anchoCarta - 10); // Ancho máximo ajustado

        // Fondo semitransparente para mejor legibilidad
        nombreLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.48); " +
                        "-fx-background-radius: 4; " +
                        "-fx-padding: 1 2 1 2; " + // Padding reducido
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.87), 2, 0.5, 1, 1);"
        );

        nombreContainer.getChildren().add(nombreLabel);

        // Ensamblar todo
        cartaPane.getChildren().addAll(imagenContainer, costoContainer, nombreContainer);

        // Posicionar elementos superpuestos
        StackPane.setAlignment(costoContainer, Pos.TOP_RIGHT);
        StackPane.setMargin(costoContainer, new Insets(2, 2, 0, 0)); // Margen pequeño
        StackPane.setAlignment(nombreContainer, Pos.BOTTOM_CENTER);

        // Efectos hover (manteniendo la funcionalidad pero ajustados a nuevo tamaño)
        cartaPane.setOnMouseEntered(e -> {
            if (disponible) {
                cartaPane.setScaleX(1.08); // Efecto más sutil
                cartaPane.setScaleY(1.08);
                cartaPane.setStyle(estiloBase +
                        "-fx-background-color: rgba(0,0,0,0.05); " +
                        "-fx-border-color: #10B981; " +
                        "-fx-effect: dropshadow(gaussian, rgba(72,187,120,0.6), 12, 0.6, 0, 3);"); // Efecto más sutil

                // Efecto de brillo en hover
                Region efectoHover = new Region();
                efectoHover.setStyle(
                        "-fx-background-color: radial-gradient(center 50% 50%, radius 80%, rgba(255,255,255,0.15), transparent); " +
                                "-fx-background-radius: 10;"
                );
                efectoHover.setPrefSize(anchoCarta, altoCarta);

                // Añadir efecto solo si no existe ya
                if (cartaPane.getChildren().size() == 3) {
                    cartaPane.getChildren().add(efectoHover);
                }

                cartaPane.setTranslateY(-3); // Elevación más sutil
            }
        });

        cartaPane.setOnMouseExited(e -> {
            cartaPane.setScaleX(1.0);
            cartaPane.setScaleY(1.0);
            cartaPane.setTranslateY(0);
            cartaPane.setStyle(estiloCompleto);

            // Remover efecto de brillo si existe
            if (cartaPane.getChildren().size() > 3) {
                cartaPane.getChildren().remove(3);
            }
        });

        // Tooltip informativo (mantenido igual)
        Tooltip tooltip = new Tooltip(
                "🎴 " + carta.getNombre().toUpperCase() + "\n" +
                        "⏣ Costo: " + carta.getCostoElixir() + " elixir\n" +
                        "📊 Tipo: " + carta.getTipo() + "\n" +
                        (disponible ? "✅ DISPONIBLE" : "❌ ELIXIR INSUFICIENTE")
        );
        tooltip.setStyle(
                "-fx-font-size: 11; " + // Fuente ligeramente más pequeña
                        "-fx-text-fill: white; " +
                        "-fx-background-color: rgba(0,0,0,0.9); " +
                        "-fx-border-color: gold; " +
                        "-fx-border-width: 1;"
        );
        Tooltip.install(cartaPane, tooltip);

        return cartaPane;
    }

    /**
     * Actualiza el panel de torres
     */
    private void actualizarPanelTorres(List<Torre> torres) {
        panelTorres.getChildren().clear();

        // Ordenar torres: Rey primero, luego Princesas
        torres.sort((t1, t2) -> {
            if (t1.getClass().equals(t2.getClass())) return 0;
            if (t1.getClass().getSimpleName().equals("TorreRey")) return -1;
            if (t2.getClass().getSimpleName().equals("TorreRey")) return 1;
            return 0;
        });

        for (Torre torre : torres) {
            HBox torreBox = crearElementoTorre(torre);
            panelTorres.getChildren().add(torreBox);
        }
    }

    /**
     * Crea un elemento visual para una torre
     */
    private HBox crearElementoTorre(Torre torre) {
        HBox torreBox = new HBox(8); // Aumentado espaciado
        torreBox.setAlignment(Pos.CENTER_LEFT);
        torreBox.setPadding(new Insets(4)); // Aumentado padding

        String colorFondo;
        if (torre.estaViva()) {
            double porcentajeVida = (double) torre.getVidaActual() / torre.getVidaMaxima();
            if (porcentajeVida > 0.7) {
                colorFondo = "rgba(34, 197, 94, 0.7)"; // Verde
            } else if (porcentajeVida > 0.4) {
                colorFondo = "rgba(251, 191, 36, 0.7)"; // Amarillo
            } else if (porcentajeVida > 0.15) {
                colorFondo = "rgba(249, 115, 22, 0.7)"; // Naranja
            } else {
                colorFondo = "rgba(239, 68, 68, 0.7)"; // Rojo
            }
        } else {
            colorFondo = "rgba(75, 85, 99, 0.8)"; // Gris para destruidas
        }

        torreBox.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 4;");

        String simboloTorre = torre.getClass().getSimpleName().equals("TorreRey") ? "♔" : "♖";
        String nombreTorre = torre.getClass().getSimpleName().equals("TorreRey") ? "Rey" : "Princesa";

        Label simboloLabel = new Label(simboloTorre);
        simboloLabel.setTextFill(Color.WHITE);
        simboloLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        simboloLabel.setPrefWidth(22); // Aumentado ancho
        simboloLabel.setAlignment(Pos.CENTER);

        Label nombreLabel = new Label(nombreTorre);
        nombreLabel.setTextFill(Color.WHITE);
        nombreLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        nombreLabel.setPrefWidth(60); // Aumentado ancho

        // Estado de vida
        Label vidaLabel;
        if (torre.estaViva()) {
            vidaLabel = new Label(torre.getVidaActual() + "/" + torre.getVidaMaxima());
            vidaLabel.setTextFill(Color.WHITE);
            vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        } else {
            vidaLabel = new Label(ConstantesUI.Etiquetas.TORRE_DESTRUIDA);
            vidaLabel.setTextFill(Color.LIGHTCORAL);
            vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        }

        Label nivelLabel = new Label("Nv." + torre.getNivel());
        nivelLabel.setTextFill(Color.LIGHTGRAY);
        nivelLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado

        torreBox.getChildren().addAll(simboloLabel, nombreLabel, vidaLabel, nivelLabel);
        return torreBox;
    }

    /**
     * Actualiza el panel de tropas vivas
     * CORREGIDO: Ahora filtra las tropas vivas directamente
     */
    private void actualizarPanelTropasVivas(List<Tropa> tropasJugador) {
        panelTropasVivas.getChildren().clear();

        // Filtrar solo las tropas vivas
        List<Tropa> tropasVivas = tropasJugador.stream()
                .filter(Tropa::estaViva)
                .toList();

        if (tropasVivas.isEmpty()) {
            Label sinTropas = new Label(ConstantesUI.Etiquetas.SIN_TROPAS_DESPLEGADAS);
            sinTropas.setTextFill(Color.LIGHTGRAY);
            sinTropas.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);
            panelTropasVivas.getChildren().add(sinTropas);
        } else {
            for (Tropa tropa : tropasVivas) {
                HBox tropaBox = crearElementoTropa(tropa);
                panelTropasVivas.getChildren().add(tropaBox);
            }
        }
    }

    /**
     * Crea un elemento visual para una tropa
     */
    private HBox crearElementoTropa(Tropa tropa) {
        HBox tropaBox = new HBox(8); // Aumentado espaciado
        tropaBox.setAlignment(Pos.CENTER_LEFT);
        tropaBox.setPadding(new Insets(4)); // Aumentado padding

        // Color según porcentaje de vida
        double porcentajeVida = (double) tropa.getVidaActual() / tropa.getVidaMaxima();
        String colorFondo;
        if (porcentajeVida > 0.7) {
            colorFondo = "rgba(34, 197, 94, 0.6)"; // Verde
        } else if (porcentajeVida > 0.4) {
            colorFondo = "rgba(251, 191, 36, 0.6)"; // Amarillo
        } else if (porcentajeVida > 0.15) {
            colorFondo = "rgba(249, 115, 22, 0.6)"; // Naranja
        } else {
            colorFondo = "rgba(239, 68, 68, 0.6)"; // Rojo
        }

        tropaBox.setStyle("-fx-background-color: " + colorFondo + "; -fx-background-radius: 3;");

        Label simboloLabel = new Label(String.valueOf(tropa.getSimboloConsola()));
        simboloLabel.setTextFill(Color.WHITE);
        simboloLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        simboloLabel.setPrefWidth(18); // Aumentado ancho
        simboloLabel.setAlignment(Pos.CENTER);

        String tipoTropa = tropa.getNombre();
        Label tipoLabel = new Label(tipoTropa);
        tipoLabel.setTextFill(Color.WHITE);
        tipoLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado
        tipoLabel.setPrefWidth(55); // Aumentado ancho

        Label vidaLabel = new Label(tropa.getVidaActual() + "/" + tropa.getVidaMaxima());
        vidaLabel.setTextFill(Color.WHITE);
        vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado

        Label posLabel = new Label("(" + tropa.getPosicion().getX() + "," + tropa.getPosicion().getY() + ")");
        posLabel.setTextFill(Color.LIGHTGRAY);
        posLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO); // Aumentado

        tropaBox.getChildren().addAll(simboloLabel, tipoLabel, vidaLabel, posLabel);
        return tropaBox;
    }

    /**
     * Cuenta las torres vivas de una lista
     */
    private int contarTorresVivas(List<Torre> torres) {
        return (int) torres.stream().filter(Torre::estaViva).count();
    }

    /**
     * Obtiene el componente JavaFX para agregarlo a la interfaz
     */
    public VBox obtenerComponente() {
        return contenedorPrincipal;
    }

    /**
     * Obtiene el ID del jugador asociado a este panel
     */
    public int obtenerJugadorId() {
        return jugadorId;
    }

    /**
     * Establece la visibilidad del panel
     */
    public void establecerVisible(boolean visible) {
        contenedorPrincipal.setVisible(visible);
    }

    /**
     * Resalta el panel del jugador
     */
    public void establecerResaltado(boolean resaltado) {
        if (resaltado) {
            String estiloResaltado = (jugadorId == 1 ?
                    ConstantesUI.Estilos.PANEL_JUGADOR_1 :
                    ConstantesUI.Estilos.PANEL_JUGADOR_2) +
                    "-fx-border-color: gold; -fx-border-width: 3;";
            contenedorPrincipal.setStyle(estiloResaltado);
        } else {
            String estiloNormal = jugadorId == 1 ?
                    ConstantesUI.Estilos.PANEL_JUGADOR_1 :
                    ConstantesUI.Estilos.PANEL_JUGADOR_2;
            contenedorPrincipal.setStyle(estiloNormal);
        }
    }

    private ImageView crearImagenCarta(Carta carta, boolean disponible) {
        try {
            // Usar la ruta completa que ya incluye "imagenCartas/"
            String rutaImagen = "/" + carta.getImagenPath();
            System.out.println("Intentando cargar imagen para " + carta.getNombre() + ": " + rutaImagen);

            InputStream imagenStream = getClass().getResourceAsStream(rutaImagen);

            if (imagenStream != null) {
                Image imagen = new Image(imagenStream);
                ImageView imageView = new ImageView(imagen);

                // Configurar el ImageView
                imageView.setFitWidth(89);
                imageView.setFitHeight(134);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);

                // Aplicar efectos según disponibilidad
                if (!disponible) {
                    ColorAdjust colorAdjust = new ColorAdjust();
                    colorAdjust.setSaturation(-0.8);
                    colorAdjust.setBrightness(-0.3);
                    imageView.setEffect(colorAdjust);
                }

                System.out.println("✅ Imagen cargada exitosamente: " + carta.getNombre());
                return imageView;
            } else {
                System.err.println("❌ ERROR: No se pudo cargar la imagen: " + rutaImagen);
                System.err.println("   Ruta completa esperada: src/main/resources" + rutaImagen);

                // Intentar cargar un placeholder genérico
                return crearPlaceholderEspecifico(carta, disponible);
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen para carta: " + carta.getNombre());
            e.printStackTrace();
            return crearPlaceholderEspecifico(carta, disponible);
        }
    }

    /**
     * Placeholder específico para cada tipo de carta
     */
    private ImageView crearPlaceholderEspecifico(Carta carta, boolean disponible) {
        WritableImage placeholder = new WritableImage(89, 134);
        PixelWriter pixelWriter = placeholder.getPixelWriter();

        // Color según tipo de carta
        Color colorBase;
        switch (carta.getTipo()) {
            case TROPA_TERRESTRE:
                colorBase = disponible ? Color.LIGHTGREEN : Color.DARKGREEN;
                break;
            case TROPA_AEREA:
                colorBase = disponible ? Color.LIGHTBLUE : Color.DARKBLUE;
                break;
            case HECHIZO:
                colorBase = disponible ? Color.ORANGE : Color.DARKORANGE;
                break;
            default:
                colorBase = disponible ? Color.LIGHTGRAY : Color.GRAY;
        }

        // Dibujar fondo
        for (int y = 0; y < 134; y++) {
            for (int x = 0; x < 89; x++) {
                pixelWriter.setColor(x, y, colorBase);
            }
        }

        // Dibujar texto del tipo
        Canvas canvas = new Canvas(89, 134);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText(carta.getTipo().name(), 5, 15);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage imagenFinal = canvas.snapshot(params, null);

        ImageView imageView = new ImageView(imagenFinal);
        imageView.setFitWidth(89);
        imageView.setFitHeight(134);

        return imageView;
    }

    private ImageView crearPlaceholderCarta(Carta carta, boolean disponible) {
        // Crear un canvas simple como fallback
        WritableImage placeholder = new WritableImage(89, 134);
        PixelWriter pixelWriter = placeholder.getPixelWriter();

        // Color base según disponibilidad
        Color colorBase = disponible ? Color.LIGHTGREEN : Color.GRAY;

        // Dibujar fondo
        for (int y = 0; y < 134; y++) {
            for (int x = 0; x < 89; x++) {
                pixelWriter.setColor(x, y, colorBase);
            }
        }

        ImageView imageView = new ImageView(placeholder);
        imageView.setFitWidth(89);
        imageView.setFitHeight(134);

        return imageView;
    }


}