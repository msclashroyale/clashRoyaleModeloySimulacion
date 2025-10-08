/**
 * Clase principal actualizada para usar la arquitectura refactorizada
 * de Clash Royale con sistema de cartas
 */
public class Main {
    /**
     * Método principal de entrada de la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("    CLASH ROYALE     ");
        System.out.println("=================================================");
        System.out.println();

        try {
            // Lanzar la aplicación JavaFX refactorizada
            javafx.application.Application.launch(ui.AplicacionClashRoyale.class, args);

        } catch (Exception e) {
            System.err.println("Error fatal al iniciar la aplicación:");
            e.printStackTrace();

            // Mostrar información adicional para debugging
            System.err.println("\nInformación del sistema:");
            System.err.println("Java Version: " + System.getProperty("java.version"));
            System.err.println("JavaFX Version: " + System.getProperty("javafx.version"));
            System.err.println("OS: " + System.getProperty("os.name"));

            System.exit(1);
        }
    }
}
