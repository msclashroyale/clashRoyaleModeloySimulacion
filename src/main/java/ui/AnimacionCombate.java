package ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Maneja animaciones visuales individuales para eventos de combate
 */
public class AnimacionCombate {
    private StackPane celda;
    private TipoAnimacion tipo;
    private Timeline animacion;
    private int ticksRestantes;
    private static final int DURACION_ANIMACION = 3;

    public AnimacionCombate(StackPane celda, TipoAnimacion tipo) {
        this.celda = celda;
        this.tipo = tipo;
        this.ticksRestantes = DURACION_ANIMACION;
        iniciarAnimacion();
    }

    /**
     * Inicia la animación correspondiente según el tipo, guardando el color original
     */
    private void iniciarAnimacion() {
        Rectangle rect = (Rectangle) celda.getChildren().get(0);

        switch (tipo) {
            case ATACANDO:
                animarAtaque(rect);
                break;
            case RECIBIENDO_DANIO:
                animarDanio(rect);
                break;
            case AMBOS:
                animarAmbos(rect);
                break;
        }
    }

    /**
     * Animación de ataque: parpadeo naranja/amarillo con brillo sin alterar dimensiones
     */
    private void animarAtaque(Rectangle rect) {
        Color colorOriginal = (Color) rect.getFill();

        Timeline parpadeo = new Timeline();
        parpadeo.setCycleCount(6);
        parpadeo.setAutoReverse(true);

        KeyFrame frame1 = new KeyFrame(Duration.millis(100), e -> {
            rect.setFill(Color.ORANGE.interpolate(colorOriginal, 0.3));
            rect.setEffect(new Glow(0.6));
        });

        KeyFrame frame2 = new KeyFrame(Duration.millis(200), e -> {
            rect.setFill(Color.YELLOW.interpolate(colorOriginal, 0.5));
            rect.setEffect(new Glow(0.3));
        });

        parpadeo.getKeyFrames().addAll(frame1, frame2);
        parpadeo.setOnFinished(e -> limpiarEfectos(rect, colorOriginal));
        parpadeo.play();

        this.animacion = parpadeo;
    }

    /**
     * Animación de daño: parpadeo rojo intenso con sombra sin alterar dimensiones
     */
    private void animarDanio(Rectangle rect) {
        Color colorOriginal = (Color) rect.getFill();

        Timeline parpadeo = new Timeline();
        parpadeo.setCycleCount(8);
        parpadeo.setAutoReverse(true);

        KeyFrame frame1 = new KeyFrame(Duration.millis(80), e -> {
            rect.setFill(Color.CRIMSON.interpolate(colorOriginal, 0.2));
            DropShadow sombra = new DropShadow();
            sombra.setColor(Color.RED);
            sombra.setRadius(6);
            rect.setEffect(sombra);
        });

        KeyFrame frame2 = new KeyFrame(Duration.millis(160), e -> {
            rect.setFill(Color.DARKRED.interpolate(colorOriginal, 0.4));
            rect.setEffect(new Glow(0.5));
        });

        parpadeo.getKeyFrames().addAll(frame1, frame2);
        parpadeo.setOnFinished(e -> limpiarEfectos(rect, colorOriginal));
        parpadeo.play();

        this.animacion = parpadeo;
    }

    /**
     * Animación combinada: efectos de ataque y daño alternando sin alterar dimensiones
     */
    private void animarAmbos(Rectangle rect) {
        Color colorOriginal = (Color) rect.getFill();

        Timeline parpadeo = new Timeline();
        parpadeo.setCycleCount(10);
        parpadeo.setAutoReverse(false);

        KeyFrame frame1 = new KeyFrame(Duration.millis(60), e -> {
            rect.setFill(Color.ORANGE.interpolate(colorOriginal, 0.3));
            rect.setEffect(new Glow(0.5));
        });

        KeyFrame frame2 = new KeyFrame(Duration.millis(120), e -> {
            rect.setFill(Color.CRIMSON.interpolate(colorOriginal, 0.2));
            DropShadow sombra = new DropShadow();
            sombra.setColor(Color.RED);
            sombra.setRadius(4);
            rect.setEffect(sombra);
        });

        parpadeo.getKeyFrames().addAll(frame1, frame2);
        parpadeo.setOnFinished(e -> limpiarEfectos(rect, colorOriginal));
        parpadeo.play();

        this.animacion = parpadeo;
    }

    /**
     * Restaura el aspecto visual original de la celda preservando dimensiones
     */
    private void limpiarEfectos(Rectangle rect, Color colorOriginal) {
        rect.setFill(colorOriginal);
        rect.setEffect(null);
        // Mantener stroke original sin modificar strokeWidth
    }

    /**
     * Detiene la animación inmediatamente y restaura el color original
     */
    public void detener() {
        if (animacion != null) {
            animacion.stop();
            Rectangle rect = (Rectangle) celda.getChildren().get(0);
            // Restaurar al color que debería tener según el estado del juego
            rect.setEffect(null);
            // El color se restaurará en la próxima actualización normal de la vista
        }
    }

    /**
     * Actualiza el estado de la animación
     * @return true si la animación debe eliminarse
     */
    public boolean actualizar() {
        ticksRestantes--;
        return ticksRestantes <= 0;
    }

    /**
     * Obtiene el tipo de animación
     */
    public TipoAnimacion getTipo() {
        return tipo;
    }

    /**
     * Obtiene los ticks restantes
     */
    public int getTicksRestantes() {
        return ticksRestantes;
    }
}