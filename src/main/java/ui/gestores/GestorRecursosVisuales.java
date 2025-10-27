package ui.gestores;

import javafx.scene.image.Image;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;

/**
 * Gestor centralizado para cargar y gestionar recursos visuales
 * Adaptado para tu estructura de archivos de imagenCartas/
 */
public class GestorRecursosVisuales {
    private static GestorRecursosVisuales instancia;
    private Map<String, Image> cacheImagenes;
    private Map<String, ColorAdjust> filtrosJugador;
    private Map<String, String> mapeoTropasACartas;

    private GestorRecursosVisuales() {
        try {
            cacheImagenes = new HashMap<>();
            filtrosJugador = new HashMap<>();
            mapeoTropasACartas = new HashMap<>();
            inicializarFiltros();
            inicializarMapeoTropas();
            System.out.println("✅ GestorRecursosVisuales inicializado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error inicializando GestorRecursosVisuales: " + e.getMessage());
            e.printStackTrace();
            // Asegurar que los maps no sean null incluso si hay error
            cacheImagenes = new HashMap<>();
            filtrosJugador = new HashMap<>();
            mapeoTropasACartas = new HashMap<>();
        }
    }

    public static GestorRecursosVisuales getInstancia() {
        if (instancia == null) {
            instancia = new GestorRecursosVisuales();
        }
        return instancia;
    }

    private void inicializarFiltros() {
        // Filtro para Jugador 1 (Azul)
        ColorAdjust filtroJugador1 = new ColorAdjust();
        filtroJugador1.setHue(0.6); // Tono azul
        filtroJugador1.setSaturation(0.3);
        filtroJugador1.setBrightness(0.1);

        // Filtro para Jugador 2 (Rojo)
        ColorAdjust filtroJugador2 = new ColorAdjust();
        filtroJugador2.setHue(-0.3); // Tono rojizo
        filtroJugador2.setSaturation(0.4);
        filtroJugador2.setBrightness(0.1);

        filtrosJugador.put("jugador1", filtroJugador1);
        filtrosJugador.put("jugador2", filtroJugador2);
    }

    private void inicializarMapeoTropas() {
        // Mapeo de nombres de tropas a nombres de archivo de cartas
        mapeoTropasACartas.put("gigante", "Card_Giant.png");
        mapeoTropasACartas.put("giant", "Card_Giant.png");

        mapeoTropasACartas.put("caballero", "Card_Knight.png");
        mapeoTropasACartas.put("knight", "Card_Knight.png");

        mapeoTropasACartas.put("arquera", "Card_Archer.png");
        mapeoTropasACartas.put("archer", "Card_Archer.png");

        mapeoTropasACartas.put("duende", "Card_Goblin.png"); // Asumo que duende = Goblin
        mapeoTropasACartas.put("goblin", "Card_Goblin.png");

        mapeoTropasACartas.put("esqueleto", "Card_Skeleton Army.png"); // Usamos Skeleton Army para esqueletos
        mapeoTropasACartas.put("skeleton", "Card_Skeleton Army.png");

        mapeoTropasACartas.put("mago", "Card_Wizard.png");
        mapeoTropasACartas.put("wizard", "Card_Wizard.png");

        mapeoTropasACartas.put("dragon", "Baby_Dragon.png");
        mapeoTropasACartas.put("baby dragon", "Baby_Dragon.png");

        mapeoTropasACartas.put("barbaro", "Card_Barbarian.png"); // Asumo que bárbaro = Barbarian
        mapeoTropasACartas.put("barbarian", "Card_Barbarian.png");

        mapeoTropasACartas.put("valkyrie", "Card_Valkyrie.png");
        mapeoTropasACartas.put("valquiria", "Card_Valkyrie.png");


        mapeoTropasACartas.put("pekka", "Card_PEKKA.png");
        mapeoTropasACartas.put("mini pekka", "Card_Mini PEKKA.png");

        mapeoTropasACartas.put("principe", "Card_Prince.png");
        mapeoTropasACartas.put("prince", "Card_Prince.png");

        mapeoTropasACartas.put("muskettera", "Card_Musketeer.png");
        mapeoTropasACartas.put("musketeer", "Card_Musketeer.png");

        mapeoTropasACartas.put("mago de hielo", "Card_Ice Wizard.png");
        mapeoTropasACartas.put("ice wizard", "Card_Ice Wizard.png");

        mapeoTropasACartas.put("globo", "Balloon.png");
        mapeoTropasACartas.put("balloon", "Balloon.png");

        // Default fallback
        mapeoTropasACartas.put("default", "Card_Knight.png");
    }

    /**
     * Obtiene la imagen para una tropa usando las cartas de imagenCartas/
     */
    public Image obtenerImagenTropa(String nombreTropa) {
        String clave = normalizarNombreTropa(nombreTropa);

        // Si ya está en cache, devolverla
        if (cacheImagenes.containsKey(clave)) {
            return cacheImagenes.get(clave);
        }

        // Obtener el nombre del archivo de la carta
        String nombreArchivo = obtenerNombreArchivoCarta(nombreTropa);
        String rutaCompleta = "imagenCartas/" + nombreArchivo;

        try {
            // Cargar la imagen
            Image imagen = cargarImagenDesdeRuta(rutaCompleta);
            cacheImagenes.put(clave, imagen);
            System.out.println("✅ Imagen de tropa cargada: " + nombreTropa + " -> " + rutaCompleta);
            return imagen;
        } catch (Exception e) {
            System.err.println("❌ Error cargando imagen de tropa: " + nombreTropa);
            System.err.println("   Ruta intentada: " + rutaCompleta);

            // Intentar cargar con extensión .png si no la tenía
            if (!nombreArchivo.toLowerCase().endsWith(".png")) {
                try {
                    String rutaConExtension = rutaCompleta + ".png";
                    Image imagen = cargarImagenDesdeRuta(rutaConExtension);
                    cacheImagenes.put(clave, imagen);
                    System.out.println("✅ Imagen cargada con extensión: " + rutaConExtension);
                    return imagen;
                } catch (Exception e2) {
                    System.err.println("❌ También falló con extensión: " + rutaCompleta + ".png");
                }
            }

            // Crear placeholder como último recurso
            Image placeholder = crearPlaceholderTropa(nombreTropa);
            cacheImagenes.put(clave, placeholder);
            return placeholder;
        }
    }

    /**
     * Normaliza el nombre de la tropa para usar como clave
     */
    private String normalizarNombreTropa(String nombreTropa) {
        return nombreTropa.toLowerCase()
                .replace(" ", "_")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n");
    }

    /**
     * Obtiene el nombre del archivo de carta basado en el nombre de la tropa
     */
    private String obtenerNombreArchivoCarta(String nombreTropa) {
        String nombreNormalizado = normalizarNombreTropa(nombreTropa);

        // Buscar en el mapeo
        for (Map.Entry<String, String> entry : mapeoTropasACartas.entrySet()) {
            if (nombreNormalizado.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // Si no encuentra, usar default
        System.err.println("⚠️  No se encontró mapeo para tropa: " + nombreTropa);
        return mapeoTropasACartas.get("default");
    }

    /**
     * Carga una imagen desde una ruta
     */
    private Image cargarImagenDesdeRuta(String rutaImagen) {
        try {
            String rutaCompleta = "/" + rutaImagen;
            InputStream imagenStream = getClass().getResourceAsStream(rutaCompleta);

            if (imagenStream != null) {
                Image imagen = new Image(imagenStream);
                return imagen;
            } else {
                throw new RuntimeException("No se encontró la imagen: " + rutaCompleta);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando imagen: " + rutaImagen, e);
        }
    }

    /**
     * Crea un placeholder específico para tropas
     */
    private Image crearPlaceholderTropa(String nombreTropa) {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(32, 32);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        // Fondo circular de color según el tipo de tropa
        Color colorFondo = obtenerColorPorTipoTropa(nombreTropa);
        gc.setFill(colorFondo);
        gc.fillOval(2, 2, 28, 28);

        // Borde
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1.5);
        gc.strokeOval(2, 2, 28, 28);

        // Texto con las iniciales
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 8));
        String iniciales = obtenerInicialesTropa(nombreTropa);
        gc.fillText(iniciales, 16 - (iniciales.length() * 2.5), 18);

        return canvas.snapshot(null, null);
    }

    private Color obtenerColorPorTipoTropa(String nombreTropa) {
        String nombre = nombreTropa.toLowerCase();
        if (nombre.contains("gigante") || nombre.contains("giant")) return Color.PURPLE;
        if (nombre.contains("caballero") || nombre.contains("knight")) return Color.SILVER;
        if (nombre.contains("arquera") || nombre.contains("archer")) return Color.GREEN;
        if (nombre.contains("duende") || nombre.contains("goblin")) return Color.LIMEGREEN;
        if (nombre.contains("esqueleto") || nombre.contains("skeleton")) return Color.WHITESMOKE;
        if (nombre.contains("mago") || nombre.contains("wizard")) return Color.BLUE;
        if (nombre.contains("dragon")) return Color.ORANGE;
        if (nombre.contains("barbaro") || nombre.contains("barbarian")) return Color.BROWN;
        if (nombre.contains("valkyrie") || nombre.contains("valquiria")) return Color.PINK;
        if (nombre.contains("pekka")) return Color.DARKBLUE;
        if (nombre.contains("principe") || nombre.contains("prince")) return Color.GOLD;
        if (nombre.contains("muskettera") || nombre.contains("musketeer")) return Color.DARKGREEN;
        if (nombre.contains("hielo") || nombre.contains("ice")) return Color.LIGHTBLUE;
        if (nombre.contains("globo") || nombre.contains("balloon")) return Color.RED;
        return Color.GRAY;
    }

    private String obtenerInicialesTropa(String nombreTropa) {
        String nombre = nombreTropa.toLowerCase();
        if (nombre.contains("gigante") || nombre.contains("giant")) return "GIG";
        if (nombre.contains("caballero") || nombre.contains("knight")) return "CAB";
        if (nombre.contains("arquera") || nombre.contains("archer")) return "ARQ";
        if (nombre.contains("duende") || nombre.contains("goblin")) return "DUE";
        if (nombre.contains("esqueleto") || nombre.contains("skeleton")) return "ESQ";
        if (nombre.contains("mago") || nombre.contains("wizard")) return "MAG";
        if (nombre.contains("dragon")) return "DRA";
        if (nombre.contains("barbaro") || nombre.contains("barbarian")) return "BAR";
        if (nombre.contains("valkyrie") || nombre.contains("valquiria")) return "VAL";
        if (nombre.contains("pekka")) return "PEK";
        if (nombre.contains("principe") || nombre.contains("prince")) return "PRI";
        if (nombre.contains("muskettera") || nombre.contains("musketeer")) return "MUS";
        if (nombre.contains("hielo") || nombre.contains("ice")) return "HIE";
        if (nombre.contains("globo") || nombre.contains("balloon")) return "GLO";
        return "TRO";
    }

    public ColorAdjust obtenerFiltroJugador(int jugadorId) {
        String clave = jugadorId == 1 ? "jugador1" : "jugador2";
        ColorAdjust original = filtrosJugador.get(clave);

        // Crear una copia para no compartir la misma instancia
        ColorAdjust copia = new ColorAdjust();
        copia.setHue(original.getHue());
        copia.setSaturation(original.getSaturation());
        copia.setBrightness(original.getBrightness());
        copia.setContrast(original.getContrast());

        return copia;
    }

    /**
     * Método para debug: muestra todos los mapeos disponibles
     */
    public void mostrarMapeosDisponibles() {
        System.out.println("=== Mapeos de Tropas a Cartas ===");
        for (Map.Entry<String, String> entry : mapeoTropasACartas.entrySet()) {
            System.out.println("Tropa: '" + entry.getKey() + "' -> Carta: '" + entry.getValue() + "'");
        }
    }

    /**
     * Limpia el cache de imágenes
     */
    public void limpiarCache() {
        cacheImagenes.clear();
        System.out.println("🔄 Cache de imágenes limpiado");
    }
}