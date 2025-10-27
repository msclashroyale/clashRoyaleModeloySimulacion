package analisis;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporta los registros de partidas a archivos CSV
 */
public class ExportadorCSV {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Exporta un resumen de todas las partidas a un CSV
     */
    public static void exportarResumenPartidas(List<RegistroPartida> registros, String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), java.nio.charset.StandardCharsets.UTF_8))) {
            // Escribir encabezado
            writer.println("id_partida,fecha_hora,estrategia_j1,estrategia_j2,nivel_j1,nivel_j2," +
                    "ganador,motivo_victoria,duracion_segundos," +
                    "torres_destruidas_j1,torres_destruidas_j2," +
                    "vida_final_j1,vida_final_j2," +
                    "cartas_jugadas_j1,cartas_jugadas_j2," +
                    "elixir_gastado_j1,elixir_gastado_j2," +
                    "tropas_invocadas_j1,tropas_invocadas_j2," +
                    "danio_causado_j1,danio_causado_j2," +
                    "danio_recibido_j1,danio_recibido_j2," +
                    "ataques_j1,ataques_j2");
            
            // Escribir datos
            for (RegistroPartida registro : registros) {
                writer.println(formatearRegistroPartida(registro));
            }
        }
    }
    
    /**
     * Exporta estadísticas detalladas por jugador
     */
    public static void exportarEstadisticasJugadores(List<RegistroPartida> registros, String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), java.nio.charset.StandardCharsets.UTF_8))) {
            // Escribir encabezado
            writer.println("id_partida,fecha_hora,jugador_id,nombre,estrategia,nivel," +
                    "resultado,cartas_jugadas,elixir_gastado,elixir_desperdiciado," +
                    "tropas_invocadas,tropas_muertas,ataques_realizados," +
                    "danio_causado,danio_recibido,torres_destruidas,danio_a_torres," +
                    "primer_carta_segundo,ultima_carta_segundo," +
                    "promedio_elixir_carta,ratio_danio");
            
            // Escribir datos de ambos jugadores por partida
            for (RegistroPartida registro : registros) {
                writer.println(formatearEstadisticasJugador(registro, 1));
                writer.println(formatearEstadisticasJugador(registro, 2));
            }
        }
    }
    
    /**
     * Exporta todos los eventos de todas las partidas
     */
    public static void exportarEventos(List<RegistroPartida> registros, String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), java.nio.charset.StandardCharsets.UTF_8))) {
            // Escribir encabezado
            writer.println("id_partida,segundo,jugador_id,tipo_evento,detalles");
            
            // Escribir eventos
            for (RegistroPartida registro : registros) {
                for (EventoPartida evento : registro.getEventos()) {
                    writer.printf("%s,%d,%d,%s,\"%s\"%n",
                            registro.getIdPartida(),
                            evento.getSegundo(),
                            evento.getJugadorId(),
                            evento.getTipo(),
                            evento.getDetalles().replace("\"", "\"\""));
                }
            }
        }
    }
    
    /**
     * Exporta análisis de estrategias
     */
    public static void exportarAnalisisEstrategias(List<RegistroPartida> registros, String rutaArchivo) throws IOException {
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), java.nio.charset.StandardCharsets.UTF_8))) {
            writer.println("estrategia,partidas_jugadas,victorias,derrotas,empates," +
                    "tasa_victoria,promedio_duracion,promedio_torres_destruidas," +
                    "promedio_cartas_jugadas,promedio_elixir_gastado," +
                    "promedio_danio_causado,promedio_danio_recibido");
            
            // Aquí se puede implementar el cálculo de estadísticas por estrategia
            // Por ahora solo dejamos la estructura
        }
    }
    
    private static String formatearRegistroPartida(RegistroPartida r) {
        return String.format("%s,%s,%s,%s,%d,%d,%d,\"%s\",%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d",
                r.getIdPartida(),
                FORMATTER.format(r.getFechaHora()),
                r.getEstrategiaJ1(),
                r.getEstrategiaJ2(),
                r.getNivelJ1(),
                r.getNivelJ2(),
                r.getGanador(),
                r.getMotivoVictoria(),
                r.getDuracionSegundos(),
                r.getTorresDestruidasJ1(),
                r.getTorresDestruidasJ2(),
                r.getVidaFinalTorresJ1(),
                r.getVidaFinalTorresJ2(),
                r.getStatsJ1().getCartasJugadas(),
                r.getStatsJ2().getCartasJugadas(),
                r.getStatsJ1().getElixirGastado(),
                r.getStatsJ2().getElixirGastado(),
                r.getStatsJ1().getTropasInvocadas(),
                r.getStatsJ2().getTropasInvocadas(),
                r.getStatsJ1().getDañoCausado(),
                r.getStatsJ2().getDañoCausado(),
                r.getStatsJ1().getDañoRecibido(),
                r.getStatsJ2().getDañoRecibido(),
                r.getStatsJ1().getAtaqueRealizados(),
                r.getStatsJ2().getAtaqueRealizados()
        );
    }
    
    private static String formatearEstadisticasJugador(RegistroPartida r, int jugadorId) {
        EstadisticasPartidaJugador stats = jugadorId == 1 ? r.getStatsJ1() : r.getStatsJ2();
        String nombre = jugadorId == 1 ? r.getNombreJ1() : r.getNombreJ2();
        String estrategia = jugadorId == 1 ? r.getEstrategiaJ1() : r.getEstrategiaJ2();
        int nivel = jugadorId == 1 ? r.getNivelJ1() : r.getNivelJ2();
        
        String resultado;
        if (r.getGanador() == 0) {
            resultado = "EMPATE";
        } else if (r.getGanador() == jugadorId) {
            resultado = "VICTORIA";
        } else {
            resultado = "DERROTA";
        }
        
        return String.format("%s,%s,%d,%s,%s,%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%.2f,%.2f",
                r.getIdPartida(),
                FORMATTER.format(r.getFechaHora()),
                jugadorId,
                nombre,
                estrategia,
                nivel,
                resultado,
                stats.getCartasJugadas(),
                stats.getElixirGastado(),
                stats.getElixirDesperdiciado(),
                stats.getTropasInvocadas(),
                stats.getTropasMuertas(),
                stats.getAtaqueRealizados(),
                stats.getDañoCausado(),
                stats.getDañoRecibido(),
                stats.getTorresDestruidas(),
                stats.getDañoATorres(),
                stats.getPrimerCartaSegundo(),
                stats.getUltimaCartaSegundo(),
                stats.getPromedioElixirPorCarta(),
                stats.getRatioDañoCausadoRecibido()
        );
    }
}
