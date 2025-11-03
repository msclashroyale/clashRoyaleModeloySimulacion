package ui;

import javafx.animation.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ui.efectos.SistemaParticulas;

/**
 * Maneja animaciones visuales individuales para eventos de combate
 * Versión mejorada con efectos fluidos y sistema de partículas
 */
public class AnimacionCombate {
    private StackPane celda;
    private TipoAnimacion tipo;
    private Timeline animacion;
    private ParallelTransition animacionParalela;
    private SequentialTransition animacionSecuencial;
    private int ticksRestantes;
    private static final int DURACION_ANIMACION = 2;
    private SistemaParticulas sistemaParticulas; // DECLARACIÓN AÑADIDA
    private Rectangle rect;
    private Color colorOriginal;

    public AnimacionCombate(StackPane celda, TipoAnimacion tipo, SistemaParticulas sistemaParticulas) {
        this.celda = celda;
        this.tipo = tipo;
        this.sistemaParticulas = sistemaParticulas; // INICIALIZACIÓN AÑADIDA
        this.ticksRestantes = DURACION_ANIMACION;
        this.rect = (Rectangle) celda.getChildren().get(0);
        this.colorOriginal = (Color) rect.getFill();
        iniciarAnimacion();
    }

    /**
     * Inicia la animación correspondiente según el tipo
     */
    private void iniciarAnimacion() {
        // Guardar el color original
        colorOriginal = (Color) rect.getFill();

        switch (tipo) {
            case ATACANDO:
                animarAtaqueMejorado();
                break;
            case RECIBIENDO_DANIO:
                animarDanioMejorado();
                break;
            case AMBOS:
                animarCombinadoMejorado();
                break;
        }
    }

    /**
     * Animación de ataque mejorada: efectos de energía y partículas doradas
     */
    private void animarAtaqueMejorado() {
        // 1. Efecto de pulso suave
        ScaleTransition escala = new ScaleTransition(Duration.millis(300), celda);
        escala.setFromX(1.0);
        escala.setFromY(1.0);
        escala.setToX(1.08);
        escala.setToY(1.08);
        escala.setAutoReverse(true);
        escala.setCycleCount(2);

        // 2. Transición de color suave
        FillTransition color = new FillTransition(Duration.millis(400), rect);
        color.setFromValue(colorOriginal);
        color.setToValue(Color.rgb(255, 200, 50, 0.7)); // Dorado semitransparente
        color.setAutoReverse(true);
        color.setCycleCount(2);

        // 3. Efecto de brillo dinámico
        Glow brillo = new Glow();
        brillo.setLevel(0.0);
        rect.setEffect(brillo);

        Timeline animacionBrillo = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(brillo.levelProperty(), 0.0)),
                new KeyFrame(Duration.millis(150), new KeyValue(brillo.levelProperty(), 0.6)),
                new KeyFrame(Duration.millis(300), new KeyValue(brillo.levelProperty(), 0.0))
        );
        animacionBrillo.setCycleCount(2);

        // 4. Partículas de energía dorada
        double centroX = celda.getLayoutX() + celda.getWidth() / 2;
        double centroY = celda.getLayoutY() + celda.getHeight() / 2;

        Timeline particulas = new Timeline(
                new KeyFrame(Duration.millis(0), e ->
                        sistemaParticulas.crearExplosion(centroX, centroY, Color.GOLD, 6)
                ),
                new KeyFrame(Duration.millis(200), e ->
                        sistemaParticulas.crearExplosion(centroX, centroY, Color.ORANGE, 4)
                )
        );

        // Combinar todas las animaciones
        animacionParalela = new ParallelTransition(escala, color, animacionBrillo, particulas);

        animacionParalela.setOnFinished(e -> {
            restaurarAspectoOriginal();
        });

        animacionParalela.play();
    }

    /**
     * Animación de daño mejorada: efectos de impacto y partículas rojas
     */
    private void animarDanioMejorado() {
        // 1. Efecto de sacudida sutil
        TranslateTransition sacudidaX = new TranslateTransition(Duration.millis(40), celda);
        sacudidaX.setByX(3);
        sacudidaX.setCycleCount(6);
        sacudidaX.setAutoReverse(true);

        TranslateTransition sacudidaY = new TranslateTransition(Duration.millis(60), celda);
        sacudidaY.setByY(2);
        sacudidaY.setCycleCount(4);
        sacudidaY.setAutoReverse(true);

        // 2. Efecto de color rojo pulsante
        FillTransition color = new FillTransition(Duration.millis(400), rect);
        color.setFromValue(colorOriginal);
        color.setToValue(Color.rgb(255, 50, 50, 0.6)); // Rojo semitransparente
        color.setAutoReverse(true);
        color.setCycleCount(2);

        // 3. Efecto de sombra roja pulsante
        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.TRANSPARENT);
        sombra.setRadius(0);
        rect.setEffect(sombra);

        Timeline animacionSombra = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sombra.colorProperty(), Color.TRANSPARENT),
                        new KeyValue(sombra.radiusProperty(), 0)
                ),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(sombra.colorProperty(), Color.RED),
                        new KeyValue(sombra.radiusProperty(), 8)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sombra.colorProperty(), Color.DARKRED),
                        new KeyValue(sombra.radiusProperty(), 12)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(sombra.colorProperty(), Color.TRANSPARENT),
                        new KeyValue(sombra.radiusProperty(), 0)
                )
        );
        animacionSombra.setCycleCount(2);

        // 4. Partículas de impacto rojas
        double centroX = celda.getLayoutX() + celda.getWidth() / 2;
        double centroY = celda.getLayoutY() + celda.getHeight() / 2;
        sistemaParticulas.crearExplosion(centroX, centroY, Color.RED, 8);

        // Combinar animaciones
        animacionParalela = new ParallelTransition(
                sacudidaX, sacudidaY, color, animacionSombra
        );

        animacionParalela.setOnFinished(e -> {
            restaurarAspectoOriginal();
        });

        animacionParalela.play();
    }

    /**
     * Animación combinada mejorada: secuencia de ataque y daño
     */
    private void animarCombinadoMejorado() {
        // FASE 1: ATAQUE (dorado)
        ScaleTransition escalaAtaque = new ScaleTransition(Duration.millis(150), celda);
        escalaAtaque.setToX(1.1);
        escalaAtaque.setToY(1.1);

        FillTransition colorAtaque = new FillTransition(Duration.millis(150), rect);
        colorAtaque.setToValue(Color.rgb(255, 200, 50, 0.8));

        Glow brilloAtaque = new Glow();
        brilloAtaque.setLevel(0.0);
        rect.setEffect(brilloAtaque);

        Timeline brilloAtaqueAnim = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(brilloAtaque.levelProperty(), 0.0)),
                new KeyFrame(Duration.millis(75), new KeyValue(brilloAtaque.levelProperty(), 0.8)),
                new KeyFrame(Duration.millis(150), new KeyValue(brilloAtaque.levelProperty(), 0.0))
        );

        // FASE 2: DAÑO (rojo)
        ScaleTransition escalaDanio = new ScaleTransition(Duration.millis(150), celda);
        escalaDanio.setToX(0.95);
        escalaDanio.setToY(0.95);

        FillTransition colorDanio = new FillTransition(Duration.millis(150), rect);
        colorDanio.setToValue(Color.rgb(255, 50, 50, 0.7));

        TranslateTransition sacudida = new TranslateTransition(Duration.millis(100), celda);
        sacudida.setByX(4);
        sacudida.setAutoReverse(true);
        sacudida.setCycleCount(2);

        // FASE 3: RECUPERACIÓN
        ScaleTransition escalaFinal = new ScaleTransition(Duration.millis(100), celda);
        escalaFinal.setToX(1.0);
        escalaFinal.setToY(1.0);

        FillTransition colorFinal = new FillTransition(Duration.millis(100), rect);
        colorFinal.setToValue(colorOriginal);

        // Partículas para cada fase
        double centroX = celda.getLayoutX() + celda.getWidth() / 2;
        double centroY = celda.getLayoutY() + celda.getHeight() / 2;

        ParallelTransition faseAtaque = new ParallelTransition(
                escalaAtaque, colorAtaque, brilloAtaqueAnim
        );
        faseAtaque.setOnFinished(e -> {
            sistemaParticulas.crearExplosion(centroX, centroY, Color.GOLD, 5);
        });

        ParallelTransition faseDanio = new ParallelTransition(
                escalaDanio, colorDanio, sacudida
        );
        faseDanio.setOnFinished(e -> {
            sistemaParticulas.crearExplosion(centroX, centroY, Color.RED, 5);
        });

        ParallelTransition faseFinal = new ParallelTransition(escalaFinal, colorFinal);

        // Secuencia completa
        animacionSecuencial = new SequentialTransition(faseAtaque, faseDanio, faseFinal);

        animacionSecuencial.setOnFinished(e -> {
            restaurarAspectoOriginal();
        });

        animacionSecuencial.play();
    }

    /**
     * Restaura el aspecto visual original de la celda
     */
    private void restaurarAspectoOriginal() {
        rect.setFill(colorOriginal);
        rect.setEffect(null);
        celda.setScaleX(1.0);
        celda.setScaleY(1.0);
        celda.setTranslateX(0);
        celda.setTranslateY(0);
    }

    /**
     * Detiene la animación inmediatamente y restaura el aspecto original
     */
    public void detener() {
        if (animacionParalela != null) {
            animacionParalela.stop();
        }
        if (animacionSecuencial != null) {
            animacionSecuencial.stop();
        }
        if (animacion != null) {
            animacion.stop();
        }
        restaurarAspectoOriginal();
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


    /**
     * Verifica si la animación está activa
     */
    public boolean estaActiva() {
        if (animacionParalela != null) {
            return animacionParalela.getStatus() == Animation.Status.RUNNING;
        }
        if (animacionSecuencial != null) {
            return animacionSecuencial.getStatus() == Animation.Status.RUNNING;
        }
        if (animacion != null) {
            return animacion.getStatus() == Animation.Status.RUNNING;
        }
        return false;
    }

}