package ui.constantes;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Constantes centralizadas para la interfaz de usuario
 */
public class ConstantesUI {

    // COLORES
    public static final class Colores {
        public static final Color FONDO_PRIMARIO = Color.web("#1e3c72");
        public static final Color FONDO_SECUNDARIO = Color.web("#2a5298");
        public static final Color JUGADOR_1_PRIMARIO = Color.web("#3b82f6");
        public static final Color JUGADOR_2_PRIMARIO = Color.web("#ef4444");
        public static final Color EXITO = Color.web("#10b981");
        public static final Color PELIGRO = Color.web("#ef4444");
        public static final Color ADVERTENCIA = Color.web("#f59e0b");
        public static final Color INFORMACION = Color.web("#3b82f6");
        public static final Color NEUTRAL = Color.web("#6b7280");

        // Colores del arena
        public static final Color ARENA_ZONA_J1 = Color.LIGHTCYAN;
        public static final Color ARENA_ZONA_J2 = Color.MISTYROSE;
        public static final Color ARENA_ZONA_NEUTRAL = Color.LIGHTGREEN;
        public static final Color ARENA_RIO = Color.LIGHTBLUE;
        public static final Color ARENA_PUENTE = Color.BROWN;
        public static final Color ARENA_BORDE = Color.WHITE;

        // Estados de vida
        public static final Color VIDA_EXCELENTE = Color.web("#22c55e");
        public static final Color VIDA_BUENA = Color.web("#eab308");
        public static final Color VIDA_ADVERTENCIA = Color.web("#f97316");
        public static final Color VIDA_CRITICA = Color.web("#ef4444");
        public static final Color VIDA_DESTRUIDA = Color.web("#4b5563");
    }

    // ESTILOS CSS
    public static final class Estilos {
        public static final String GRADIENTE_FONDO =
                "-fx-background-color: linear-gradient(to bottom, #1e3c72, #2a5298);";

        public static final String PANEL_BASE =
                "-fx-background-radius: 10; -fx-padding: 12;";

        public static final String PANEL_JUGADOR_1 =
                "-fx-background-color: rgba(59, 130, 246, 0.9);" + PANEL_BASE;

        public static final String PANEL_JUGADOR_2 =
                "-fx-background-color: rgba(239, 68, 68, 0.9);" + PANEL_BASE;

        public static final String PANEL_CABECERA =
                "-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 10; -fx-padding: 10;";

        public static final String BOTON_PRIMARIO =
                "-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        public static final String BOTON_SECUNDARIO =
                "-fx-background-color: #6b7280; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        public static final String BOTON_INFORMACION =
                "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        public static final String BOTON_PELIGRO =
                "-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;";

        public static final String MOSTRAR_TIEMPO =
                "-fx-background-color: black; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 5;";

        public static final String CARTA_DISPONIBLE =
                "-fx-background-color: rgba(0, 255, 0, 0.3); -fx-background-radius: 3; -fx-padding: 2;";

        public static final String CARTA_NO_DISPONIBLE =
                "-fx-background-color: rgba(255, 0, 0, 0.3); -fx-background-radius: 3; -fx-padding: 2;";

        public static final String CONTENEDOR_LISTA =
                "-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 5; -fx-padding: 5;";

        public static final String GRILLA_ARENA =
                "-fx-border-color: white; -fx-border-width: 2; -fx-background-color: rgba(255, 255, 255, 0.1);";
    }

    // FUENTES
    public static final class Fuentes {
        public static final Font TITULO_GRANDE = Font.font("Arial", FontWeight.BOLD, 24);
        public static final Font TITULO_MEDIANO = Font.font("Arial", FontWeight.BOLD, 18);
        public static final Font TITULO_PEQUENO = Font.font("Arial", FontWeight.BOLD, 16);
        public static final Font SUBTITULO = Font.font("Arial", FontWeight.BOLD, 14);
        public static final Font TEXTO_GRANDE = Font.font("Arial", 14);
        public static final Font TEXTO_MEDIANO = Font.font("Arial", 12);
        public static final Font TEXTO_PEQUENO = Font.font("Arial", 10);
        public static final Font TEXTO_LEYENDA = Font.font("Arial", 9);
        public static final Font TEXTO_DIMINUTO = Font.font("Arial", 8);
        public static final Font TEXTO_MICRO = Font.font("Arial", 7);
    }

    // DIMENSIONES
    public static final class Dimensiones {
        public static final double ANCHO_VENTANA = 1200;
        public static final double ALTO_VENTANA = 800;
        public static final double ANCHO_PANEL_JUGADOR = 400;
        public static final double TAMANO_CELDA_ARENA = 20;
        public static final double ESPACIADO_PANEL = 15;
        public static final double ESPACIADO_PEQUENO = 8;
        public static final double ESPACIADO_DIMINUTO = 3;
    }

    // TIMING Y ANIMACIONES
    public static final class Tiempos {
        public static final int DURACION_TICK_JUEGO_MS = 1000;
        public static final int DURACION_ANIMACION_TICKS = 3;
        public static final int ANIMACION_RAPIDA_MS = 100;
        public static final int ANIMACION_MEDIA_MS = 200;
        public static final int ANIMACION_LENTA_MS = 500;
    }

    // TEXTO Y ETIQUETAS
    public static final class Etiquetas {
        public static final String TITULO_APP = "CLASH ROYALE - SISTEMA DE CARTAS";
        public static final String TITULO_JUGADOR_1 = "JUGADOR 1";
        public static final String TITULO_JUGADOR_2 = "JUGADOR 2";
        public static final String TITULO_ARENA = "ARENA 18x32";
        public static final String CARTAS_EN_MANO = "CARTAS EN MANO";
        public static final String ESTADO_TORRES = "ESTADO TORRES";
        public static final String TROPAS_VIVAS = "TROPAS VIVAS";
        public static final String TITULO_LEYENDA = "LEYENDA:";
        public static final String JUEGO_PAUSADO = "PAUSADO";
        public static final String JUEGO_EJECUTANDO = "EJECUTANDO";
        public static final String JUEGO_TERMINADO = "TERMINADO";
        public static final String JUGADOR_1_GANA = "🏆 JUGADOR 1 GANA";
        public static final String JUGADOR_2_GANA = "🏆 JUGADOR 2 GANA";
        public static final String EMPATE = "🤝 EMPATE";

        // Botones
        public static final String BOTON_INICIAR = "▶ INICIAR";
        public static final String BOTON_PAUSAR = "⏸ PAUSAR";
        public static final String BOTON_REINICIAR = "🔄 RESET";
        public static final String BOTON_PASO = "➡️ STEP";

        // Ayuda
        public static final String AYUDA_CONTROLES = "Controles: ESPACIO=Play/Pause | ENTER=Step | R=Reset";

        // Estados
        public static final String SIN_TROPAS_DESPLEGADAS = "Sin tropas desplegadas";
        public static final String TORRE_DESTRUIDA = "DESTRUIDA";

        // Leyenda
        public static final String[] ELEMENTOS_LEYENDA = {"R/r=Rey", "P/p=Princesa", "G/g=Gigante", "K/k=Caballero"};
    }

    // CONFIGURACIÓN DEL JUEGO
    public static final class ConfiguracionJuego {
        public static final int ANCHO_ARENA = 32;
        public static final int ALTO_ARENA = 18;
        public static final int ELIXIR_INICIAL = 5;
        public static final int ELIXIR_MAXIMO = 10;
        public static final int CANTIDAD_TORRES_INICIAL = 3;
        public static final double RANGO_ATAQUE_TORRE = 6.0;
    }
}