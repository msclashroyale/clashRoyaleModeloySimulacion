package ui.efectos;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SistemaParticulas {
    private Pane contenedorPadre;
    private List<Circle> particulasActivas;
    private Random random;

    public SistemaParticulas(Pane contenedorPadre) {
        this.contenedorPadre = contenedorPadre;
        this.particulasActivas = new ArrayList<>();
        this.random = new Random();
    }

    public void crearExplosion(double x, double y, Color colorBase, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            crearParticula(x, y, colorBase);
        }
    }

    private void crearParticula(double x, double y, Color colorBase) {
        // Variación de color
        Color color = variarColor(colorBase);

        // Tamaño aleatorio
        double tamaño = 1.5 + random.nextDouble() * 3;

        Circle particula = new Circle(tamaño, color);
        particula.setCenterX(x);
        particula.setCenterY(y);
        particula.setOpacity(0.9);

        contenedorPadre.getChildren().add(particula);
        particulasActivas.add(particula);

        // Parámetros aleatorios para movimiento natural
        double angulo = random.nextDouble() * 360;
        double velocidad = 1 + random.nextDouble() * 4;
        double distancia = 10 + random.nextDouble() * 25;
        double duracion = 400 + random.nextDouble() * 400;

        double destinoX = x + Math.cos(Math.toRadians(angulo)) * distancia;
        double destinoY = y + Math.sin(Math.toRadians(angulo)) * distancia;

        // Animación de movimiento y desvanecimiento
        Timeline animacion = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(particula.centerXProperty(), x),
                        new KeyValue(particula.centerYProperty(), y),
                        new KeyValue(particula.opacityProperty(), 0.9),
                        new KeyValue(particula.radiusProperty(), tamaño)
                ),
                new KeyFrame(Duration.millis(duracion * 0.3),
                        new KeyValue(particula.centerXProperty(), x + (destinoX - x) * 0.3),
                        new KeyValue(particula.centerYProperty(), y + (destinoY - y) * 0.3),
                        new KeyValue(particula.opacityProperty(), 0.7),
                        new KeyValue(particula.radiusProperty(), tamaño * 0.8)
                ),
                new KeyFrame(Duration.millis(duracion),
                        new KeyValue(particula.centerXProperty(), destinoX),
                        new KeyValue(particula.centerYProperty(), destinoY),
                        new KeyValue(particula.opacityProperty(), 0),
                        new KeyValue(particula.radiusProperty(), tamaño * 0.3)
                )
        );

        animacion.setOnFinished(e -> {
            contenedorPadre.getChildren().remove(particula);
            particulasActivas.remove(particula);
        });

        animacion.play();
    }

    private Color variarColor(Color colorBase) {
        double variacion = 0.2;
        double r = clamp(colorBase.getRed() + (random.nextDouble() - 0.5) * variacion);
        double g = clamp(colorBase.getGreen() + (random.nextDouble() - 0.5) * variacion);
        double b = clamp(colorBase.getBlue() + (random.nextDouble() - 0.5) * variacion);
        return Color.color(r, g, b, colorBase.getOpacity());
    }

    private double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    public void limpiar() {
        for (Circle particula : new ArrayList<>(particulasActivas)) {
            contenedorPadre.getChildren().remove(particula);
        }
        particulasActivas.clear();
    }

    public int getNumeroParticulasActivas() {
        return particulasActivas.size();
    }
}