package ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import ui.constantes.ConstantesUI;

/**
 * Componente que maneja la cabecera principal del juego
 * Muestra el título, tiempo, tick actual y ganador
 */
public class ComponenteCabecera {

    private HBox contenedorCabecera;
    private Label etiquetaTiempo;
    private Label etiquetaTick;
    private Label etiquetaGanador;

    /**
     * Constructor - inicializa el componente de cabecera
     */
    public ComponenteCabecera() {
        inicializarComponente();
    }

    /**
     * Inicializa todos los elementos de la cabecera
     */
    private void inicializarComponente() {
        contenedorCabecera = new HBox(20);
        contenedorCabecera.setAlignment(Pos.CENTER);
        contenedorCabecera.setPadding(new Insets(10));
        contenedorCabecera.setStyle(ConstantesUI.Estilos.PANEL_CABECERA);

        crearElementosCabecera();
    }

    /**
     * Crea todos los elementos visuales de la cabecera
     */
    private void crearElementosCabecera() {
        // Título principal
        Label titulo = new Label(ConstantesUI.Etiquetas.TITULO_APP);
        titulo.setFont(ConstantesUI.Fuentes.TITULO_GRANDE);
        titulo.setTextFill(Color.DARKBLUE);

        // Tiempo de juego
        etiquetaTiempo = new Label("0:00");
        etiquetaTiempo.setFont(ConstantesUI.Fuentes.TITULO_MEDIANO);
        etiquetaTiempo.setStyle(ConstantesUI.Estilos.MOSTRAR_TIEMPO);

        // Contador de ticks
        etiquetaTick = new Label("Tick: 0");
        etiquetaTick.setFont(ConstantesUI.Fuentes.TEXTO_GRANDE);

        // Etiqueta para mostrar el ganador
        etiquetaGanador = new Label("");
        etiquetaGanador.setFont(ConstantesUI.Fuentes.TITULO_PEQUENO);
        etiquetaGanador.setTextFill(Color.RED);

        // Agregar todos los elementos al contenedor
        contenedorCabecera.getChildren().addAll(
                titulo,
                etiquetaTiempo,
                etiquetaTick,
                etiquetaGanador
        );
    }

    /**
     * Actualiza la información mostrada en la cabecera
     * @param tiempoFormateado Tiempo del juego en formato MM:SS
     * @param tickActual Número de tick actual
     */
    public void actualizar(String tiempoFormateado, int tickActual) {
        etiquetaTiempo.setText(tiempoFormateado);
        etiquetaTick.setText("Tick: " + tickActual);
    }

    /**
     * Muestra el ganador del juego
     * @param textoGanador Texto que indica quién ganó
     */
    public void mostrarGanador(String textoGanador) {
        etiquetaGanador.setText(textoGanador);

        // Cambiar color según el ganador
        if (textoGanador.contains("JUGADOR 1")) {
            etiquetaGanador.setTextFill(ConstantesUI.Colores.JUGADOR_1_PRIMARIO);
        } else if (textoGanador.contains("JUGADOR 2")) {
            etiquetaGanador.setTextFill(ConstantesUI.Colores.JUGADOR_2_PRIMARIO);
        } else {
            etiquetaGanador.setTextFill(ConstantesUI.Colores.NEUTRAL);
        }
    }

    /**
     * Limpia el texto del ganador
     */
    public void limpiarGanador() {
        etiquetaGanador.setText("");
    }

    /**
     * Obtiene el componente JavaFX para agregarlo a la interfaz
     * @return HBox contenedor de la cabecera
     */
    public HBox obtenerComponente() {
        return contenedorCabecera;
    }

    /**
     * Establece la visibilidad del componente
     * @param visible true para mostrar, false para ocultar
     */
    public void establecerVisible(boolean visible) {
        contenedorCabecera.setVisible(visible);
    }

    /**
     * Obtiene el texto actual del tiempo
     * @return String con el tiempo actual mostrado
     */
    public String obtenerTiempoActual() {
        return etiquetaTiempo.getText();
    }

    /**
     * Obtiene el tick actual mostrado
     * @return String con el tick actual
     */
    public String obtenerTickActual() {
        return etiquetaTick.getText();
    }
}