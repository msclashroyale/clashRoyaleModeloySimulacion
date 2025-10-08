package ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import ui.constantes.ConstantesUI;

/**
 * Componente que maneja la barra de estado en la parte inferior
 * Muestra el estado actual del juego y estadísticas generales
 */
public class ComponenteBarraEstado {

    private HBox contenedorBarraEstado;
    private Label etiquetaEstadoJuego;
    private Label etiquetaEstadisticas;

    /**
     * Constructor - inicializa el componente de barra de estado
     */
    public ComponenteBarraEstado() {
        inicializarComponente();
    }

    /**
     * Inicializa todos los elementos de la barra de estado
     */
    private void inicializarComponente() {
        contenedorBarraEstado = new HBox(20);
        contenedorBarraEstado.setAlignment(Pos.CENTER);
        contenedorBarraEstado.setPadding(new Insets(10));
        contenedorBarraEstado.setStyle(ConstantesUI.Estilos.PANEL_CABECERA);

        crearElementosBarraEstado();
    }

    /**
     * Crea todos los elementos de la barra de estado
     */
    private void crearElementosBarraEstado() {
        // Estado del juego
        etiquetaEstadoJuego = new Label(ConstantesUI.Etiquetas.JUEGO_PAUSADO);
        etiquetaEstadoJuego.setFont(ConstantesUI.Fuentes.SUBTITULO);
        etiquetaEstadoJuego.setTextFill(ConstantesUI.Colores.NEUTRAL);

        // Estadísticas adicionales (puede expandirse en el futuro)
        etiquetaEstadisticas = new Label("");
        etiquetaEstadisticas.setFont(ConstantesUI.Fuentes.TEXTO_MEDIANO);
        etiquetaEstadisticas.setTextFill(Color.DARKBLUE);

        contenedorBarraEstado.getChildren().addAll(
                etiquetaEstadoJuego,
                etiquetaEstadisticas
        );
    }

    /**
     * Establece el estado actual del juego
     * @param estadoJuego Texto que describe el estado (PAUSADO, EJECUTANDO, TERMINADO)
     */
    public void establecerEstadoJuego(String estadoJuego) {
        etiquetaEstadoJuego.setText(estadoJuego);

        // Cambiar color según el estado
        switch (estadoJuego) {
            case "PAUSADO" -> etiquetaEstadoJuego.setTextFill(ConstantesUI.Colores.NEUTRAL);
            case "EJECUTANDO" -> etiquetaEstadoJuego.setTextFill(ConstantesUI.Colores.EXITO);
            case "TERMINADO" -> etiquetaEstadoJuego.setTextFill(ConstantesUI.Colores.PELIGRO);
            default -> etiquetaEstadoJuego.setTextFill(ConstantesUI.Colores.INFORMACION);
        }
    }

    /**
     * Establece texto de estadísticas adicionales
     * @param estadisticas Texto con estadísticas del juego
     */
    public void establecerEstadisticas(String estadisticas) {
        etiquetaEstadisticas.setText(estadisticas);
    }

    /**
     * Actualiza la barra de estado con información completa
     * @param estadoJuego Estado actual del juego
     * @param estadisticas Estadísticas adicionales (opcional)
     */
    public void actualizar(String estadoJuego, String estadisticas) {
        establecerEstadoJuego(estadoJuego);
        if (estadisticas != null && !estadisticas.isEmpty()) {
            establecerEstadisticas(estadisticas);
        }
    }

    /**
     * Limpia todas las estadísticas mostradas
     */
    public void limpiarEstadisticas() {
        etiquetaEstadisticas.setText("");
    }

    /**
     * Muestra un mensaje temporal en la barra de estado
     * @param mensaje Mensaje a mostrar temporalmente
     * @param duracionSegundos Duración en segundos (0 para permanente)
     */
    public void mostrarMensajeTemporal(String mensaje, int duracionSegundos) {
        String estadisticasOriginales = etiquetaEstadisticas.getText();
        establecerEstadisticas(mensaje);

        if (duracionSegundos > 0) {
            // Usar Timeline para restaurar el mensaje original después del tiempo especificado
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(
                            javafx.util.Duration.seconds(duracionSegundos),
                            e -> establecerEstadisticas(estadisticasOriginales)
                    )
            );
            timeline.play();
        }
    }

    /**
     * Obtiene el componente JavaFX para agregarlo a la interfaz
     * @return HBox contenedor de la barra de estado
     */
    public HBox obtenerComponente() {
        return contenedorBarraEstado;
    }

    /**
     * Obtiene el estado actual del juego mostrado
     * @return String con el estado actual
     */
    public String obtenerEstadoActual() {
        return etiquetaEstadoJuego.getText();
    }

    /**
     * Establece la visibilidad del componente
     * @param visible true para mostrar, false para ocultar
     */
    public void establecerVisible(boolean visible) {
        contenedorBarraEstado.setVisible(visible);
    }
}