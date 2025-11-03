package analisis;

import juego.ConfiguracionPartida;
import juego.Partida;
import juego.events.GameEvent;

import java.io.IOException;
import java.util.UUID;

/**
 * Versión EXTENDIDA del análisis - 5000 partidas
 * 
 * IMPORTANTE: Este archivo ejecuta 5000 partidas para obtener
 * resultados estadísticamente robustos.
 * 
 * Tiempo estimado de ejecución: 30-45 minutos
 */
public class EjemploAnalisisExtendido {
    
    // PARÁMETRO PRINCIPAL - Cambiar según necesites
    private static final int NUMERO_PARTIDAS = 5000;
    
    // Configuración de progreso
    private static final int INTERVALO_PROGRESO = 100; // Mostrar progreso cada 100 partidas
    
    public static void main(String[] args) {
        long tiempoInicio = System.currentTimeMillis();
        
        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║   ANÁLISIS EXTENDIDO - CLASH ROYALE SIMULATION               ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Configuración:");
        System.out.println("  • Número de partidas: " + NUMERO_PARTIDAS);
        System.out.println("  • Tiempo estimado: " + estimarTiempo(NUMERO_PARTIDAS));
        System.out.println();
        System.out.println("Iniciando simulación...\n");
        
        // Crear el gestor de análisis
        GestorAnalisis gestorAnalisis = new GestorAnalisis();
        
        // Ejecutar partidas con barra de progreso
        for (int i = 0; i < NUMERO_PARTIDAS; i++) {
            ejecutarYAnalizarPartida(gestorAnalisis, i + 1);
            
            if ((i + 1) % INTERVALO_PROGRESO == 0) {
                mostrarProgreso(i + 1, NUMERO_PARTIDAS, tiempoInicio);
            }
        }
        
        // Tiempo total
        long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
        
        System.out.println("\n" + "═".repeat(70));
        System.out.println("SIMULACIÓN COMPLETADA");
        System.out.println("═".repeat(70));
        System.out.println("  Tiempo total: " + formatearTiempo(tiempoTotal));
        System.out.println("  Partidas: " + NUMERO_PARTIDAS);
        System.out.println("  Promedio: " + (tiempoTotal / NUMERO_PARTIDAS) + " ms/partida");
        System.out.println();
        
        // Generar reporte
        System.out.println("Generando reporte estadístico...\n");
        String reporte = gestorAnalisis.generarReporte();
        System.out.println(reporte);
        
        // Exportar a CSV
        exportarDatos(gestorAnalisis);
        
        System.out.println("\n" + "═".repeat(70));
        System.out.println("PRÓXIMOS PASOS:");
        System.out.println("═".repeat(70));
        System.out.println("  1. python analizar_datos.py");
        System.out.println("  2. python analisis_estadistico.py");
        System.out.println("  3. python predictor_enfrentamientos.py");
        System.out.println("  4. python ml_predictor.py");
        System.out.println();
        System.out.println("¡Análisis completo! Revisa los archivos en datos_analisis/");
        System.out.println("═".repeat(70) + "\n");
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
    
    /**
     * Exporta todos los datos a CSV
     */
    private static void exportarDatos(GestorAnalisis gestorAnalisis) {
        try {
            String carpetaSalida = "datos_analisis/";
            
            System.out.println("Exportando datos a CSV...");
            
            ExportadorCSV.exportarResumenPartidas(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "resumen_partidas.csv"
            );
            System.out.println("  ✓ resumen_partidas.csv");
            
            ExportadorCSV.exportarEstadisticasJugadores(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "estadisticas_jugadores.csv"
            );
            System.out.println("  ✓ estadisticas_jugadores.csv");
            
            ExportadorCSV.exportarEventos(
                gestorAnalisis.getRegistros(), 
                carpetaSalida + "eventos_partidas.csv"
            );
            System.out.println("  ✓ eventos_partidas.csv");
            
            System.out.println("\nArchivos CSV guardados en: " + carpetaSalida);
            
        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra progreso con barra visual
     */
    private static void mostrarProgreso(int actual, int total, long tiempoInicio) {
        double porcentaje = (actual * 100.0) / total;
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
        long tiempoEstimado = (long) ((tiempoTranscurrido / (double) actual) * (total - actual));
        
        // Barra de progreso
        int longitudBarra = 40;
        int progreso = (int) ((actual / (double) total) * longitudBarra);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < longitudBarra; i++) {
            if (i < progreso) {
                barra.append("█");
            } else {
                barra.append("░");
            }
        }
        barra.append("]");
        
        System.out.printf("\r%s %6.2f%% | %d/%d | Tiempo: %s | ETA: %s",
            barra.toString(),
            porcentaje,
            actual,
            total,
            formatearTiempo(tiempoTranscurrido),
            formatearTiempo(tiempoEstimado)
        );
    }
    
    /**
     * Estima el tiempo de ejecución
     */
    private static String estimarTiempo(int partidas) {
        // Asumiendo ~500ms por partida en promedio
        long milisegundos = partidas * 500L;
        return formatearTiempo(milisegundos);
    }
    
    /**
     * Formatea milisegundos a formato legible
     */
    private static String formatearTiempo(long milisegundos) {
        long segundos = milisegundos / 1000;
        long minutos = segundos / 60;
        segundos = segundos % 60;
        
        if (minutos > 0) {
            return String.format("%d min %d seg", minutos, segundos);
        } else {
            return String.format("%d seg", segundos);
        }
    }
}
