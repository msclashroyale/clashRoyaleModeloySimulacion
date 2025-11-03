// ConfiguracionResponsive.java
package ui.configuracionResponsive;

import javafx.stage.Screen;

public class ConfiguracionResponsive {
    private static final double UMBRAL_PANTALLA_GRANDE = 1600; // 1600px de ancho o más = pantalla grande

    public enum TipoPantalla {
        MONITOR_GRANDE,
        NOTEBOOK
    }

    public static TipoPantalla detectarTipoPantalla() {
        double anchoPantalla = Screen.getPrimary().getVisualBounds().getWidth();
        return anchoPantalla >= UMBRAL_PANTALLA_GRANDE ? TipoPantalla.MONITOR_GRANDE : TipoPantalla.NOTEBOOK;
    }

    public static class DimensionesResponsive {
        public final double ANCHO_VENTANA;
        public final double ALTO_VENTANA;
        public final double ANCHO_PANEL_JUGADOR;
        public final double TAMANO_CELDA_ARENA;
        public final double ESPACIADO_PANEL;

        public DimensionesResponsive(TipoPantalla tipo) {
            if (tipo == TipoPantalla.MONITOR_GRANDE) {
                // Valores originales para monitores grandes
                this.ANCHO_VENTANA = 1600;
                this.ALTO_VENTANA = 1000;
                this.ANCHO_PANEL_JUGADOR = 350;
                this.TAMANO_CELDA_ARENA = 29;
                this.ESPACIADO_PANEL = 20;
            } else {
                // Valores optimizados para notebooks
                this.ANCHO_VENTANA = 1200;
                this.ALTO_VENTANA = 800;
                this.ANCHO_PANEL_JUGADOR = 260;
                this.TAMANO_CELDA_ARENA = 20;
                this.ESPACIADO_PANEL = 15;
            }
        }
    }
}