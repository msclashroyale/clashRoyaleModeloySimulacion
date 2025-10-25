package analisis;

import juego.ConfiguracionPartida;
import juego.Partida;
import juego.events.GameEvent;

import java.io.IOException;
import java.util.UUID;

/**
 * Ejemplo de uso del sistema de análisis de datos
 */
public class EjemploAnalisis {
    
    public static void main(String[] args) {
        // Crear el gestor de análisis
        GestorAnalisis gestorAnalisis = new GestorAnalisis();
        
        // Ejecutar múltiples partidas
        int numeroPartidas = 100;
        System.out.println("Ejecutando " + numeroPartidas + " partidas...\n");
        
        for (int i = 0; i < numeroPartidas; i++) {
            ejecutarYAnalizarPartida(gestorAnalisis, i + 1);
            
            if ((i + 1) % 10 == 0) {
                System.out.printf("Partidas completadas: %d/%d\n", i + 1, numeroPartidas);
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ANÁLISIS COMPLETADO");
        System.out.println("=".repeat(60) + "\n");
        
        // Generar reporte
        String reporte = gestorAnalisis.generarReporte();
        System.out.println(reporte);
        
        // Exportar a CSV
        try {
            String carpetaSalida = "datos_analisis/";
            
            ExportadorCSV.exportarResumenPartidas(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "resumen_partidas.csv"
            );
            
            ExportadorCSV.exportarEstadisticasJugadores(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "estadisticas_jugadores.csv"
            );
            
            ExportadorCSV.exportarEventos(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "eventos_partidas.csv"
            );
            
            System.out.println("\nArchivos CSV exportados exitosamente a: " + carpetaSalida);
            System.out.println("  - resumen_partidas.csv");
            System.out.println("  - estadisticas_jugadores.csv");
            System.out.println("  - eventos_partidas.csv");
            
        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
        }
    }
    
    /**
     * Ejecuta una partida y recolecta sus datos
     */
    private static void ejecutarYAnalizarPartida(GestorAnalisis gestorAnalisis, int numeroPartida) {
        // Crear configuración de partida
        ConfiguracionPartida config = new ConfiguracionPartida();
        
        // Crear partida
        Partida partida = new Partida(config);
        
        // Crear recolector de datos
        RecolectorDatos recolector = new RecolectorDatos(
            partida.getJugador1(), 
            partida.getJugador2(),
            partida.getTablero()
        );
        
        // Generar ID único para la partida
        String idPartida = String.format("PARTIDA_%04d_%s", 
            numeroPartida, 
            UUID.randomUUID().toString().substring(0, 8));
        
        recolector.iniciarNuevaPartida(idPartida);
        
        // Suscribir el recolector a todos los eventos
        partida.getEventManager().subscribe(GameEvent.class, recolector);
        
        // Inicializar y ejecutar partida
        partida.inicializar();
        
        while (!partida.isPartidaTerminada()) {
            partida.ejecutarTick();
            recolector.actualizarTick(partida.getTickActual());
        }
        
        // Agregar el registro al gestor
        gestorAnalisis.agregarRegistro(recolector.getRegistroActual());
    }
}
