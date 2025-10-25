package analisis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Gestor central del sistema de análisis de datos
 */
public class GestorAnalisis {
    private final List<RegistroPartida> registros;
    
    public GestorAnalisis() {
        this.registros = new ArrayList<>();
    }
    
    public void agregarRegistro(RegistroPartida registro) {
        registros.add(registro);
    }
    
    public List<RegistroPartida> getRegistros() {
        return new ArrayList<>(registros);
    }
    
    /**
     * Calcula estadísticas generales de todas las partidas
     */
    public EstadisticasGenerales calcularEstadisticasGenerales() {
        if (registros.isEmpty()) {
            return new EstadisticasGenerales();
        }
        
        EstadisticasGenerales stats = new EstadisticasGenerales();
        stats.totalPartidas = registros.size();
        
        int duracionTotal = 0;
        int victoriasJ1 = 0;
        int victoriasJ2 = 0;
        int empates = 0;
        
        for (RegistroPartida r : registros) {
            duracionTotal += r.getDuracionSegundos();
            
            if (r.getGanador() == 0) empates++;
            else if (r.getGanador() == 1) victoriasJ1++;
            else victoriasJ2++;
        }
        
        stats.duracionPromedio = duracionTotal / (double) registros.size();
        stats.victoriasJugador1 = victoriasJ1;
        stats.victoriasJugador2 = victoriasJ2;
        stats.empates = empates;
        
        return stats;
    }
    
    /**
     * Calcula estadísticas por estrategia
     */
    public Map<String, EstadisticasEstrategia> calcularEstadisticasPorEstrategia() {
        Map<String, EstadisticasEstrategia> estadisticas = new HashMap<>();
        
        for (RegistroPartida r : registros) {
            // Procesar jugador 1
            String estrategiaJ1 = r.getEstrategiaJ1();
            EstadisticasEstrategia statsJ1 = estadisticas.computeIfAbsent(
                estrategiaJ1, k -> new EstadisticasEstrategia(k));
            
            statsJ1.agregarPartida(r, 1);
            
            // Procesar jugador 2
            String estrategiaJ2 = r.getEstrategiaJ2();
            EstadisticasEstrategia statsJ2 = estadisticas.computeIfAbsent(
                estrategiaJ2, k -> new EstadisticasEstrategia(k));
            
            statsJ2.agregarPartida(r, 2);
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene enfrentamientos entre estrategias
     */
    public Map<String, EnfrentamientoEstrategias> calcularEnfrentamientos() {
        Map<String, EnfrentamientoEstrategias> enfrentamientos = new HashMap<>();
        
        for (RegistroPartida r : registros) {
            String key1 = r.getEstrategiaJ1() + "_vs_" + r.getEstrategiaJ2();
            String key2 = r.getEstrategiaJ2() + "_vs_" + r.getEstrategiaJ1();
            
            EnfrentamientoEstrategias enf = enfrentamientos.get(key1);
            if (enf == null) {
                enf = enfrentamientos.get(key2);
            }
            
            if (enf == null) {
                enf = new EnfrentamientoEstrategias(r.getEstrategiaJ1(), r.getEstrategiaJ2());
                enfrentamientos.put(key1, enf);
            }
            
            enf.agregarResultado(r);
        }
        
        return enfrentamientos;
    }
    
    /**
     * Genera un reporte textual de las estadísticas
     */
    public String generarReporte() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append("REPORTE DE ANÁLISIS DE PARTIDAS\n");
        sb.append("=".repeat(60)).append("\n\n");
        
        // Estadísticas generales
        EstadisticasGenerales general = calcularEstadisticasGenerales();
        sb.append("ESTADÍSTICAS GENERALES\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Total de partidas: %d\n", general.totalPartidas));
        sb.append(String.format("Duración promedio: %.1f segundos\n", general.duracionPromedio));
        sb.append(String.format("Victorias J1: %d (%.1f%%)\n", 
            general.victoriasJugador1, 
            100.0 * general.victoriasJugador1 / general.totalPartidas));
        sb.append(String.format("Victorias J2: %d (%.1f%%)\n", 
            general.victoriasJugador2, 
            100.0 * general.victoriasJugador2 / general.totalPartidas));
        sb.append(String.format("Empates: %d (%.1f%%)\n\n", 
            general.empates, 
            100.0 * general.empates / general.totalPartidas));
        
        // Estadísticas por estrategia
        Map<String, EstadisticasEstrategia> porEstrategia = calcularEstadisticasPorEstrategia();
        sb.append("ESTADÍSTICAS POR ESTRATEGIA\n");
        sb.append("-".repeat(40)).append("\n");
        
        for (Map.Entry<String, EstadisticasEstrategia> entry : porEstrategia.entrySet()) {
            EstadisticasEstrategia stats = entry.getValue();
            sb.append(String.format("\n%s:\n", entry.getKey()));
            sb.append(String.format("  Partidas: %d\n", stats.partidasJugadas));
            sb.append(String.format("  Victorias: %d (%.1f%%)\n", 
                stats.victorias, stats.getTasaVictoria()));
            sb.append(String.format("  Derrotas: %d\n", stats.derrotas));
            sb.append(String.format("  Empates: %d\n", stats.empates));
            sb.append(String.format("  Promedio cartas jugadas: %.1f\n", stats.getPromedioCartasJugadas()));
            sb.append(String.format("  Promedio daño causado: %.1f\n", stats.getPromedioDanioCausado()));
        }
        
        return sb.toString();
    }
    
    // Clases internas para organizar estadísticas
    
    public static class EstadisticasGenerales {
        public int totalPartidas;
        public double duracionPromedio;
        public int victoriasJugador1;
        public int victoriasJugador2;
        public int empates;
    }
    
    public static class EstadisticasEstrategia {
        private final String nombre;
        private int partidasJugadas;
        private int victorias;
        private int derrotas;
        private int empates;
        private int torresDestruidasTotal;
        private int cartasJugadasTotal;
        private int danioCausadoTotal;
        private int danioRecibidoTotal;
        
        public EstadisticasEstrategia(String nombre) {
            this.nombre = nombre;
        }
        
        public void agregarPartida(RegistroPartida r, int jugadorId) {
            partidasJugadas++;
            
            if (r.getGanador() == 0) {
                empates++;
            } else if (r.getGanador() == jugadorId) {
                victorias++;
            } else {
                derrotas++;
            }
            
            EstadisticasPartidaJugador stats = jugadorId == 1 ? r.getStatsJ1() : r.getStatsJ2();
            torresDestruidasTotal += stats.getTorresDestruidas();
            cartasJugadasTotal += stats.getCartasJugadas();
            danioCausadoTotal += stats.getDañoCausado();
            danioRecibidoTotal += stats.getDañoRecibido();
        }
        
        public String getNombre() { return nombre; }
        public int getPartidasJugadas() { return partidasJugadas; }
        public int getVictorias() { return victorias; }
        public int getDerrotas() { return derrotas; }
        public int getEmpates() { return empates; }
        
        public double getTasaVictoria() {
            return partidasJugadas > 0 ? 100.0 * victorias / partidasJugadas : 0;
        }
        
        public double getPromedioTorresDestruidas() {
            return partidasJugadas > 0 ? (double) torresDestruidasTotal / partidasJugadas : 0;
        }
        
        public double getPromedioCartasJugadas() {
            return partidasJugadas > 0 ? (double) cartasJugadasTotal / partidasJugadas : 0;
        }
        
        public double getPromedioDanioCausado() {
            return partidasJugadas > 0 ? (double) danioCausadoTotal / partidasJugadas : 0;
        }
        
        public double getPromedioDanioRecibido() {
            return partidasJugadas > 0 ? (double) danioRecibidoTotal / partidasJugadas : 0;
        }
    }
    
    public static class EnfrentamientoEstrategias {
        private final String estrategia1;
        private final String estrategia2;
        private int partidas;
        private int victoriasE1;
        private int victoriasE2;
        private int empates;
        
        public EnfrentamientoEstrategias(String e1, String e2) {
            this.estrategia1 = e1;
            this.estrategia2 = e2;
        }
        
        public void agregarResultado(RegistroPartida r) {
            partidas++;
            
            boolean e1EsJ1 = r.getEstrategiaJ1().equals(estrategia1);
            
            if (r.getGanador() == 0) {
                empates++;
            } else if ((e1EsJ1 && r.getGanador() == 1) || (!e1EsJ1 && r.getGanador() == 2)) {
                victoriasE1++;
            } else {
                victoriasE2++;
            }
        }
        
        public String getEstrategia1() { return estrategia1; }
        public String getEstrategia2() { return estrategia2; }
        public int getPartidas() { return partidas; }
        public int getVictoriasE1() { return victoriasE1; }
        public int getVictoriasE2() { return victoriasE2; }
        public int getEmpates() { return empates; }
        
        public double getTasaVictoriaE1() {
            return partidas > 0 ? 100.0 * victoriasE1 / partidas : 0;
        }
        
        public double getTasaVictoriaE2() {
            return partidas > 0 ? 100.0 * victoriasE2 / partidas : 0;
        }
    }
}
