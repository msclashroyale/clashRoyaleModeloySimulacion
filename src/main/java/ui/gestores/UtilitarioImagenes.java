package ui.gestores;

import javafx.scene.image.Image;
import java.io.InputStream;

/**
 * Utilidades compartidas para carga de imágenes
 * Usado tanto por ComponentePanelJugador como por ComponenteArena
 */
public class UtilitarioImagenes {

    /**
     * Carga una imagen desde una ruta, con manejo de errores
     */
    public static Image cargarImagen(String rutaImagen) {
        try {
            String rutaCompleta = rutaImagen.startsWith("/") ? rutaImagen : "/" + rutaImagen;
            InputStream imagenStream = UtilitarioImagenes.class.getResourceAsStream(rutaCompleta);

            if (imagenStream != null) {
                Image imagen = new Image(imagenStream);
                System.out.println("✅ Imagen cargada: " + rutaImagen);
                return imagen;
            } else {
                System.err.println("❌ No se encontró la imagen: " + rutaCompleta);
                return crearImagenPlaceholder(32, 32, "NF");
            }
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen: " + rutaImagen + " - " + e.getMessage());
            return crearImagenPlaceholder(32, 32, "ERR");
        }
    }

    /**
     * Crea una imagen de placeholder
     */
    public static Image crearImagenPlaceholder(double ancho, double alto, String texto) {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(ancho, alto);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        gc.fillRect(0, 0, ancho, alto);

        gc.setStroke(javafx.scene.paint.Color.DARKGRAY);
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, ancho, alto);

        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        gc.fillText(texto, ancho/2 - 6, alto/2 + 3);

        return canvas.snapshot(null, null);
    }
}