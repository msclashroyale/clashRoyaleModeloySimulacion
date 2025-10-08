package ui;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.controladores.ControladorUIPrincipal;
import ui.constantes.ConstantesUI;

/**
 * Aplicación principal de Clash Royale refactorizada
 * Clase simplificada que solo se encarga del ciclo de vida de la aplicación JavaFX
 */
public class AplicacionClashRoyale extends Application {

    private ControladorUIPrincipal controladorUI;

    /**
     * Método principal de entrada de la aplicación JavaFX
     * @param escenarioPrincipal Escenario principal proporcionado por JavaFX
     */
    @Override
    public void start(Stage escenarioPrincipal) {
        try {
            // Crear el controlador principal de la UI
            controladorUI = new ControladorUIPrincipal();

            // Configurar el escenario
            configurarEscenario(escenarioPrincipal);

            // Inicializar la interfaz de usuario
            controladorUI.inicializarUI(escenarioPrincipal);

            // Mostrar la ventana
            controladorUI.mostrar();

        } catch (Exception e) {
            manejarErrorInicializacion(e);
        }
    }

    /**
     * Configura las propiedades básicas del escenario principal
     * @param escenario Escenario a configurar
     */
    private void configurarEscenario(Stage escenario) {
        // Configurar propiedades básicas de la ventana
        escenario.setTitle(ConstantesUI.Etiquetas.TITULO_APP);
        escenario.setResizable(true);
        escenario.setMinWidth(ConstantesUI.Dimensiones.ANCHO_VENTANA * 0.8);
        escenario.setMinHeight(ConstantesUI.Dimensiones.ALTO_VENTANA * 0.8);

        // Centrar la ventana en la pantalla
        escenario.centerOnScreen();

        // Configurar comportamiento al cerrar
        escenario.setOnCloseRequest(event -> {
            manejarCierreAplicacion();
        });
    }

    /**
     * Maneja el cierre de la aplicación de forma limpia
     */
    private void manejarCierreAplicacion() {
        System.out.println("Cerrando Clash Royale Simulator...");

        // Realizar limpieza si es necesario
        if (controladorUI != null) {
            // El controlador UI ya maneja su propia limpieza
            System.out.println("Limpieza de UI completada.");
        }

        System.out.println("Aplicación cerrada correctamente.");
    }

    /**
     * Maneja errores durante la inicialización
     * @param error Error ocurrido
     */
    private void manejarErrorInicializacion(Exception error) {
        System.err.println("Error durante la inicialización de la aplicación:");
        error.printStackTrace();

        // Mostrar mensaje de error al usuario
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alerta.setTitle("Error de Inicialización");
        alerta.setHeaderText("No se pudo inicializar la aplicación");
        alerta.setContentText("Ocurrió un error inesperado durante la inicialización: " +
                error.getMessage());

        alerta.showAndWait();

        // Cerrar la aplicación
        javafx.application.Platform.exit();
    }

    /**
     * Método de entrada principal de la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        try {
            System.out.println("Iniciando Clash Royale Simulator...");
            System.out.println("Versión: Sistema de Cartas Mejorado");
            System.out.println("================================");

            // Configurar propiedades del sistema para JavaFX si es necesario
            configurarSistema();

            // Lanzar la aplicación JavaFX
            launch(args);

        } catch (Exception e) {
            System.err.println("Error fatal en el main:");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Configura propiedades del sistema necesarias para la aplicación
     */
    private static void configurarSistema() {
        // Configurar propiedades para mejor rendimiento de JavaFX
        System.setProperty("javafx.animation.fullspeed", "true");
        System.setProperty("javafx.animation.pulse", "60");

        // Configurar propiedades de renderizado si es necesario
        // System.setProperty("prism.order", "sw"); // Usar software rendering si hay problemas con hardware

        System.out.println("Configuración del sistema completada.");
    }

    /**
     * Método llamado cuando la aplicación está a punto de cerrarse
     */
    @Override
    public void stop() throws Exception {
        System.out.println("Ejecutando limpieza final de la aplicación...");

        // Llamar al método stop del padre
        super.stop();

        System.out.println("Aplicación terminada.");
    }
}