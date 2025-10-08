package ui.componentes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ui.constantes.ConstantesUI;

/**
 * Componente que maneja los controles principales del juego
 * Incluye botones de play/pause, step, reset y ayuda
 */
public class ComponentePanelControl {

    private VBox contenedorControles;
    private Button botonPlayPause;
    private Button botonReset;
    private Button botonPaso;

    // Interfaces funcionales para callbacks
    private Runnable accionPlayPause;
    private Runnable accionPaso;
    private Runnable accionReiniciar;

    /**
     * Constructor - inicializa el componente de controles
     */
    public ComponentePanelControl() {
        inicializarComponente();
    }

    /**
     * Inicializa todos los elementos del panel de control
     */
    private void inicializarComponente() {
        contenedorControles = new VBox(10);
        contenedorControles.setAlignment(Pos.TOP_CENTER);
        contenedorControles.setPadding(new Insets(10));

        crearBotones();
        crearEtiquetaAyuda();
    }

    /**
     * Crea todos los botones de control
     */
    private void crearBotones() {
        // Botón Play/Pause
        botonPlayPause = new Button(ConstantesUI.Etiquetas.BOTON_INICIAR);
        botonPlayPause.setStyle(ConstantesUI.Estilos.BOTON_PRIMARIO);
        botonPlayPause.setOnAction(e -> {
            if (accionPlayPause != null) {
                accionPlayPause.run();
            }
        });

        // Botón Step
        botonPaso = new Button(ConstantesUI.Etiquetas.BOTON_PASO);
        botonPaso.setStyle(ConstantesUI.Estilos.BOTON_INFORMACION);
        botonPaso.setOnAction(e -> {
            if (accionPaso != null) {
                accionPaso.run();
            }
        });

        // Botón Reset
        botonReset = new Button(ConstantesUI.Etiquetas.BOTON_REINICIAR);
        botonReset.setStyle(ConstantesUI.Estilos.BOTON_SECUNDARIO);
        botonReset.setOnAction(e -> {
            if (accionReiniciar != null) {
                accionReiniciar.run();
            }
        });

        contenedorControles.getChildren().addAll(botonPlayPause, botonPaso, botonReset);
    }

    /**
     * Crea la etiqueta de ayuda con los controles de teclado
     */
    private void crearEtiquetaAyuda() {
        Separator separador = new Separator();

        Label ayuda = new Label(ConstantesUI.Etiquetas.AYUDA_CONTROLES);
        ayuda.setTextFill(Color.WHITE);
        ayuda.setFont(ConstantesUI.Fuentes.TEXTO_PEQUENO);

        contenedorControles.getChildren().addAll(separador, ayuda);
    }

    /**
     * Actualiza el estado del botón play/pause
     * @param estaEjecutandose true si el juego está corriendo, false si está pausado
     */
    public void actualizarBotonPlayPause(boolean estaEjecutandose) {
        if (estaEjecutandose) {
            botonPlayPause.setText(ConstantesUI.Etiquetas.BOTON_PAUSAR);
            botonPlayPause.setStyle(ConstantesUI.Estilos.BOTON_PELIGRO);
        } else {
            botonPlayPause.setText(ConstantesUI.Etiquetas.BOTON_INICIAR);
            botonPlayPause.setStyle(ConstantesUI.Estilos.BOTON_PRIMARIO);
        }
    }

    /**
     * Configura la acción del botón play/pause
     * @param accion Runnable que se ejecutará al presionar el botón
     */
    public void configurarAccionPlayPause(Runnable accion) {
        this.accionPlayPause = accion;
    }

    /**
     * Configura la acción del botón paso
     * @param accion Runnable que se ejecutará al presionar el botón
     */
    public void configurarAccionPaso(Runnable accion) {
        this.accionPaso = accion;
    }

    /**
     * Configura la acción del botón reiniciar
     * @param accion Runnable que se ejecutará al presionar el botón
     */
    public void configurarAccionReiniciar(Runnable accion) {
        this.accionReiniciar = accion;
    }

    /**
     * Habilita o deshabilita todos los botones
     * @param habilitado true para habilitar, false para deshabilitar
     */
    public void establecerBotonesHabilitados(boolean habilitado) {
        botonPlayPause.setDisable(!habilitado);
        botonPaso.setDisable(!habilitado);
        botonReset.setDisable(!habilitado);
    }

    /**
     * Habilita o deshabilita solo el botón de paso
     * Útil cuando el juego está corriendo automáticamente
     * @param habilitado true para habilitar, false para deshabilitar
     */
    public void establecerBotonPasoHabilitado(boolean habilitado) {
        botonPaso.setDisable(!habilitado);
    }

    /**
     * Obtiene el componente JavaFX para agregarlo a la interfaz
     * @return HBox contenedor de los controles
     */
    public VBox obtenerComponente() {
        return contenedorControles;
    }
}