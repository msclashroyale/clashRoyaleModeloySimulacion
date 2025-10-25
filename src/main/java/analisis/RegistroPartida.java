package analisis;

import jugador.Jugador;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contiene todos los datos recolectados de una partida individual
 */
public class RegistroPartida {
    // Identificación de la partida
    private final String idPartida;
    private final LocalDateTime fechaHora;
    
    // Información de jugadores
    private final String nombreJ1;
    private final String nombreJ2;
    private final String estrategiaJ1;
    private final String estrategiaJ2;
    private final int nivelJ1;
    private final int nivelJ2;
    
    // Resultado
    private int ganador; // 0=empate, 1=J1, 2=J2
    private String motivoVictoria;
    private int duracionSegundos;
    
    // Estadísticas generales
    private int torresDestruidasJ1;
    private int torresDestruidasJ2;
    private int vidaFinalTorresJ1;
    private int vidaFinalTorresJ2;
    
    // Eventos de la partida
    private final List<EventoPartida> eventos;
    
    // Estadísticas por jugador
    private EstadisticasPartidaJugador statsJ1;
    private EstadisticasPartidaJugador statsJ2;
    
    public RegistroPartida(String idPartida, Jugador j1, Jugador j2) {
        this.idPartida = idPartida;
        this.fechaHora = LocalDateTime.now();
        
        this.nombreJ1 = j1.getNombre();
        this.nombreJ2 = j2.getNombre();
        this.estrategiaJ1 = j1.getEstrategiaIA().getClass().getSimpleName();
        this.estrategiaJ2 = j2.getEstrategiaIA().getClass().getSimpleName();
        this.nivelJ1 = j1.getNivel();
        this.nivelJ2 = j2.getNivel();
        
        this.eventos = new ArrayList<>();
        this.statsJ1 = new EstadisticasPartidaJugador(1);
        this.statsJ2 = new EstadisticasPartidaJugador(2);
        
        this.torresDestruidasJ1 = 0;
        this.torresDestruidasJ2 = 0;
    }
    
    public void registrarEvento(EventoPartida evento) {
        eventos.add(evento);
    }
    
    public void finalizarPartida(int ganador, String motivo, int duracion, int torresJ1, int torresJ2, int vidaJ1, int vidaJ2) {
        this.ganador = ganador;
        this.motivoVictoria = motivo;
        this.duracionSegundos = duracion;
        this.torresDestruidasJ1 = torresJ1;
        this.torresDestruidasJ2 = torresJ2;
        this.vidaFinalTorresJ1 = vidaJ1;
        this.vidaFinalTorresJ2 = vidaJ2;
    }
    
    // Getters
    public String getIdPartida() { return idPartida; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getNombreJ1() { return nombreJ1; }
    public String getNombreJ2() { return nombreJ2; }
    public String getEstrategiaJ1() { return estrategiaJ1; }
    public String getEstrategiaJ2() { return estrategiaJ2; }
    public int getNivelJ1() { return nivelJ1; }
    public int getNivelJ2() { return nivelJ2; }
    public int getGanador() { return ganador; }
    public String getMotivoVictoria() { return motivoVictoria; }
    public int getDuracionSegundos() { return duracionSegundos; }
    public int getTorresDestruidasJ1() { return torresDestruidasJ1; }
    public int getTorresDestruidasJ2() { return torresDestruidasJ2; }
    public int getVidaFinalTorresJ1() { return vidaFinalTorresJ1; }
    public int getVidaFinalTorresJ2() { return vidaFinalTorresJ2; }
    public List<EventoPartida> getEventos() { return eventos; }
    public EstadisticasPartidaJugador getStatsJ1() { return statsJ1; }
    public EstadisticasPartidaJugador getStatsJ2() { return statsJ2; }
}
