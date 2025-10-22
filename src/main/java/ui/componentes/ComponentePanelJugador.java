package ui.componentes;
import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import tablero.Tablero;
import cartas.Carta;
import jugador.Jugador;
import jugador.SistemaElixir;
import ui.constantes.ConstantesUI;
import java.util.List;

/**
 * Componente que maneja toda la información visual de un jugador
 * Incluye estadísticas, cartas, torres, tropas y leyenda
 */
public class ComponentePanelJugador {

    private final int jugadorId;
    private VBox contenedorPrincipal;

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
        contenedorPrincipal = new VBox(ConstantesUI.Dimensiones.ESPACIADO_PEQUENO);
        contenedorPrincipal.setPrefWidth(ConstantesUI.Dimensiones.ANCHO_PANEL_JUGADOR);
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
        etiquetaTitulo.setFont(ConstantesUI.Fuentes.TITULO_PEQUENO);
        etiquetaTitulo.setTextFill(Color.WHITE);

        return etiquetaTitulo;
    }

    /**
     * Crea la sección de información básica del jugador
     */
    private VBox crearInfoBasica() {
        VBox infoBox = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);

        // Crear contenedor para la barra de elixir
        VBox contenedorElixir = crearBarraElixir();

        etiquetaTropas = new Label("Tropas: 0");
        etiquetaTorres = new Label("Torres: 3/3");
        etiquetaCartas = new Label("Cartas jugadas: 0");
        etiquetaEstrategia = new Label("Estrategia: N/A");

        // Aplicar estilo a las etiquetas
        Label[] etiquetas = {etiquetaTropas, etiquetaTorres, etiquetaCartas, etiquetaEstrategia};
        for (Label etiqueta : etiquetas) {
            etiqueta.setTextFill(Color.WHITE);
            etiqueta.setFont(ConstantesUI.Fuentes.TEXTO_PEQUENO);
        }

        infoBox.getChildren().add(contenedorElixir);
        infoBox.getChildren().addAll(etiquetas);
        return infoBox;
    }

    /**
     * Crea la barra de elixir con su etiqueta - VERSIÓN MEJORADA
     */
    private VBox crearBarraElixir() {
        VBox contenedor = new VBox(3);
        contenedor.setAlignment(Pos.CENTER);

        // Etiqueta "Elixir"
        etiquetaElixirTexto = new Label("Elixir: 5/10");
        etiquetaElixirTexto.setTextFill(Color.WHITE);
        etiquetaElixirTexto.setFont(ConstantesUI.Fuentes.TEXTO_PEQUENO);

        // Barra de progreso
        barraElixir = new ProgressBar(0.5);
        barraElixir.setPrefWidth(170);
        barraElixir.setPrefHeight(18);

        // Estilo inicial de la barra
        barraElixir.setStyle(
                "-fx-accent: #a855f7;" + // Color morado inicial
                        "-fx-control-inner-background: #374151;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-insets: 0;"
        );

        contenedor.getChildren().addAll(etiquetaElixirTexto, barraElixir);
        return contenedor;
    }

    /**
     * Crea la sección de cartas en mano
     */
    private VBox crearSeccionCartas() {
        VBox seccionCartas = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);
        seccionCartas.setAlignment(Pos.CENTER);

        Label tituloCartas = new Label(ConstantesUI.Etiquetas.CARTAS_EN_MANO);
        tituloCartas.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        tituloCartas.setTextFill(Color.WHITE);

        panelCartas = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);
        panelCartas.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);
        panelCartas.setPrefHeight(100);

        seccionCartas.getChildren().addAll(tituloCartas, panelCartas);
        return seccionCartas;
    }

    /**
     * Crea la sección de estado de torres
     */
    private VBox crearSeccionTorres() {
        VBox seccionTorres = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);
        seccionTorres.setAlignment(Pos.CENTER);

        Label tituloTorres = new Label(ConstantesUI.Etiquetas.ESTADO_TORRES);
        tituloTorres.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        tituloTorres.setTextFill(Color.WHITE);

        panelTorres = new VBox(2);
        panelTorres.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);
        panelTorres.setPrefHeight(80);

        seccionTorres.getChildren().addAll(tituloTorres, panelTorres);
        return seccionTorres;
    }

    /**
     * Crea la sección de tropas vivas
     */
    private VBox crearSeccionTropasVivas() {
        VBox seccionTropas = new VBox(ConstantesUI.Dimensiones.ESPACIADO_DIMINUTO);
        seccionTropas.setAlignment(Pos.CENTER);
        seccionTropas.setPrefHeight(120);

        Label tituloTropas = new Label(ConstantesUI.Etiquetas.TROPAS_VIVAS);
        tituloTropas.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        tituloTropas.setTextFill(Color.WHITE);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefHeight(100);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        panelTropasVivas = new VBox(2);
        panelTropasVivas.setStyle(ConstantesUI.Estilos.CONTENEDOR_LISTA);

        scrollPane.setContent(panelTropasVivas);

        seccionTropas.getChildren().addAll(tituloTropas, scrollPane);
        return seccionTropas;
    }

    /**
     * Crea la leyenda de símbolos
     */
    private VBox crearLeyenda() {
        VBox leyendaBox = new VBox(2);
        leyendaBox.setAlignment(Pos.CENTER);

        Label tituloLeyenda = new Label(ConstantesUI.Etiquetas.TITULO_LEYENDA);
        tituloLeyenda.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);
        tituloLeyenda.setTextFill(Color.WHITE);

        HBox elementosLeyenda = new HBox(8);
        elementosLeyenda.setAlignment(Pos.CENTER);

        for (String elemento : ConstantesUI.Etiquetas.ELEMENTOS_LEYENDA) {
            Label label = new Label(elemento);
            label.setTextFill(Color.WHITE);
            label.setFont(ConstantesUI.Fuentes.TEXTO_MICRO);
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

    /**
     * Actualiza la información básica del jugador con animación de barra de elixir
     * CORREGIDO: Ahora usa Tablero en lugar de Arena
     */
    private void actualizarInfoBasica(Jugador jugador, Tablero tablero) {
        // Actualizar título con nombre y nivel del jugador
        etiquetaTitulo.setText(jugador.getNombre() + " (Nv. " + jugador.getNivel() + ")");

        // Actualizar barra de elixir
        int elixirActual = jugador.getSistemaElixir().getElixirActual();
        int elixirMaximo = jugador.getSistemaElixir().getElixirMaximo();

        etiquetaElixirTexto.setText("Elixir: " + elixirActual + "/" + elixirMaximo);

        // Calcular progreso (0.0 a 1.0)
        double progreso = (double) elixirActual / elixirMaximo;

        // Actualizar el progreso de la barra (JavaFX animará automáticamente el cambio)
        barraElixir.setProgress(progreso);

        // Cambiar color según el nivel de elixir
        String colorBarra;
        String colorTexto = "white";

        if (progreso >= 0.8) {
            colorBarra = "#10b981"; // Verde brillante
            colorTexto = "#d1fae5"; // Verde claro para el texto
        } else if (progreso >= 0.5) {
            colorBarra = "#a855f7"; // Morado
            colorTexto = "#e9d5ff"; // Morado claro
        } else if (progreso >= 0.3) {
            colorBarra = "#f59e0b"; // Naranja
            colorTexto = "#fed7aa"; // Naranja claro
        } else {
            colorBarra = "#ef4444"; // Rojo
            colorTexto = "#fecaca"; // Rojo claro
        }

        // Actualizar color del texto según el nivel
        etiquetaElixirTexto.setStyle("-fx-text-fill: " + colorTexto + ";");

        // Aplicar estilo completo a la barra
        barraElixir.setStyle(
                "-fx-accent: " + colorBarra + ";" +
                        "-fx-control-inner-background: #374151;" +
                        "-fx-background-radius: 4;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-insets: 0;" +
                        "-fx-border-insets: 0;"
        );

        // Forzar actualización del estilo de la barra interna
        if (barraElixir.lookup(".bar") != null) {
            barraElixir.lookup(".bar").setStyle(
                    "-fx-background-color: " + colorBarra + ";" +
                            "-fx-background-radius: 3;" +
                            "-fx-background-insets: 1;"
            );
        }

        // Actualizar otras estadísticas
        etiquetaTropas.setText("Tropas: " + tablero.contarTropasVivas(jugadorId));
        etiquetaTorres.setText("Torres: " + contarTorresVivas(tablero.getTorresJugador(jugadorId)) + "/3");
        etiquetaCartas.setText("Cartas jugadas: " + jugador.getEstadisticas().getCartasJugadas());
        etiquetaEstrategia.setText("Estrategia: " + jugador.getEstrategiaIA().getClass().getSimpleName());
    }

    /**
     * Actualiza el panel de cartas en mano
     */
    private void actualizarPanelCartas(List<Carta> cartasEnMano, SistemaElixir elixir) {
        panelCartas.getChildren().clear();

        for (Carta carta : cartasEnMano) {
            HBox cartaBox = new HBox(5);
            cartaBox.setAlignment(Pos.CENTER_LEFT);
            cartaBox.setPadding(new Insets(2));

            // Color basado en si puede pagarse
            String colorFondo = elixir.puedeGastar(carta.getCostoElixir()) ?
                    ConstantesUI.Estilos.CARTA_DISPONIBLE : ConstantesUI.Estilos.CARTA_NO_DISPONIBLE;
            cartaBox.setStyle(colorFondo);

            Label nombreLabel = new Label(carta.getNombre());
            nombreLabel.setTextFill(Color.WHITE);
            nombreLabel.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);

            Label costoLabel = new Label("(" + carta.getCostoElixir() + ")");
            costoLabel.setTextFill(Color.YELLOW);
            costoLabel.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);

            cartaBox.getChildren().addAll(nombreLabel, costoLabel);
            panelCartas.getChildren().add(cartaBox);
        }
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
        HBox torreBox = new HBox(5);
        torreBox.setAlignment(Pos.CENTER_LEFT);
        torreBox.setPadding(new Insets(2));

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

        // Símbolo y nombre
        String simboloTorre = torre.getClass().getSimpleName().equals("TorreRey") ? "♔" : "♖";
        String nombreTorre = torre.getClass().getSimpleName().equals("TorreRey") ? "Rey" : "Princesa";

        Label simboloLabel = new Label(simboloTorre);
        simboloLabel.setTextFill(Color.WHITE);
        simboloLabel.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        simboloLabel.setPrefWidth(18);
        simboloLabel.setAlignment(Pos.CENTER);

        Label nombreLabel = new Label(nombreTorre);
        nombreLabel.setTextFill(Color.WHITE);
        nombreLabel.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);
        nombreLabel.setPrefWidth(50);

        // Estado de vida
        Label vidaLabel;
        if (torre.estaViva()) {
            vidaLabel = new Label(torre.getVidaActual() + "/" + torre.getVidaMaxima());
            vidaLabel.setTextFill(Color.WHITE);
            vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_LEYENDA);
        } else {
            vidaLabel = new Label(ConstantesUI.Etiquetas.TORRE_DESTRUIDA);
            vidaLabel.setTextFill(Color.LIGHTCORAL);
            vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_DIMINUTO);
        }

        Label nivelLabel = new Label("Nv." + torre.getNivel());
        nivelLabel.setTextFill(Color.LIGHTGRAY);
        nivelLabel.setFont(ConstantesUI.Fuentes.TEXTO_MICRO);

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
        HBox tropaBox = new HBox(5);
        tropaBox.setAlignment(Pos.CENTER_LEFT);
        tropaBox.setPadding(new Insets(3));

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
        simboloLabel.setFont(ConstantesUI.Fuentes.TEXTO_PEQUENO);
        simboloLabel.setPrefWidth(15);
        simboloLabel.setAlignment(Pos.CENTER);

        String tipoTropa = tropa.getNombre();
        Label tipoLabel = new Label(tipoTropa);
        tipoLabel.setTextFill(Color.WHITE);
        tipoLabel.setFont(ConstantesUI.Fuentes.TEXTO_DIMINUTO);
        tipoLabel.setPrefWidth(45);

        Label vidaLabel = new Label(tropa.getVidaActual() + "/" + tropa.getVidaMaxima());
        vidaLabel.setTextFill(Color.WHITE);
        vidaLabel.setFont(ConstantesUI.Fuentes.TEXTO_DIMINUTO);

        Label posLabel = new Label("(" + tropa.getPosicion().getX() + "," + tropa.getPosicion().getY() + ")");
        posLabel.setTextFill(Color.LIGHTGRAY);
        posLabel.setFont(ConstantesUI.Fuentes.TEXTO_MICRO);

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
}