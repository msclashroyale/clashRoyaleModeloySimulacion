package analisis;

import juego.events.*;
import jugador.Jugador;
import tablero.Tablero;
import entidades.edificios.Torre;

/**
 * Recolector de datos que escucha todos los eventos del juego y los registra
 */
public class RecolectorDatos implements GameEventListener {
    private RegistroPartida registroActual;
    private final Jugador jugador1;
    private final Jugador jugador2;
    private final Tablero tablero;
    private int tickActual;
    
    public RecolectorDatos(Jugador j1, Jugador j2, Tablero tablero) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.tablero = tablero;
    }
    
    public void iniciarNuevaPartida(String idPartida) {
        this.registroActual = new RegistroPartida(idPartida, jugador1, jugador2);
        this.tickActual = 0;
    }
    
    public void actualizarTick(int tick) {
        this.tickActual = tick;
    }
    
    @Override
    public void onGameEvent(GameEvent event) {
        if (registroActual == null) return;
        
        if (event instanceof TropaDesplegadaEvent) {
            procesarTropaDesplegada((TropaDesplegadaEvent) event);
        } else if (event instanceof AtaqueRealizadoEvent) {
            procesarAtaque((AtaqueRealizadoEvent) event);
        } else if (event instanceof EntidadDestruidaEvent) {
            procesarEntidadDestruida((EntidadDestruidaEvent) event);
        } else if (event instanceof PartidaTerminadaEvent) {
            procesarPartidaTerminada((PartidaTerminadaEvent) event);
        }
    }
    
    private void procesarTropaDesplegada(TropaDesplegadaEvent event) {
        int jugadorId = event.getJugador().getId();
        EstadisticasPartidaJugador stats = getStatsJugador(jugadorId);
        
        // Registrar carta jugada
        int costo = event.getTropa().getCostoElixir();
        stats.registrarCartaJugada(tickActual, costo);
        stats.registrarTropaInvocada();
        
        // Registrar evento
        String detalles = String.format("Carta: %s (costo: %d) en (%d,%d)", 
            event.getTropa().getNombre(), 
            costo,
            event.getPosicion().getX(), 
            event.getPosicion().getY());
        
        registroActual.registrarEvento(new EventoPartida(
            EventoPartida.TipoEvento.TROPA_DESPLEGADA,
            tickActual,
            jugadorId,
            detalles
        ));
    }
    
    private void procesarAtaque(AtaqueRealizadoEvent event) {
        int atacanteId = event.getAtacante().getJugadorId();
        int defensorId = event.getObjetivo().getJugadorId();
        int danio = event.getDanio();
        
        EstadisticasPartidaJugador statsAtacante = getStatsJugador(atacanteId);
        EstadisticasPartidaJugador statsDefensor = getStatsJugador(defensorId);
        
        statsAtacante.registrarAtaque(danio);
        statsDefensor.registrarDañoRecibido(danio);
        
        // Si el defensor es una torre, registrar daño a torres
        if (event.getObjetivo() instanceof Torre) {
            statsAtacante.registrarDañoATorre(danio);
        }
        
        String detalles = String.format("%s atacó a %s por %d de daño",
            event.getNombreAtacante(),
            event.getNombreObjetivo(),
            danio);
        
        registroActual.registrarEvento(new EventoPartida(
            EventoPartida.TipoEvento.ATAQUE_REALIZADO,
            tickActual,
            atacanteId,
            detalles
        ));
    }
    
    private void procesarEntidadDestruida(EntidadDestruidaEvent event) {
        int jugadorId = event.getEntidad().getJugadorId();
        
        // Si es una tropa
        if (event.getEntidad() instanceof entidades.tropas.Tropa) {
            EstadisticasPartidaJugador stats = getStatsJugador(jugadorId);
            stats.registrarTropaMuerta();
            
            String detalles = String.format("Tropa destruida: %s", event.getNombreEntidad());
            registroActual.registrarEvento(new EventoPartida(
                EventoPartida.TipoEvento.TROPA_MUERTA,
                tickActual,
                jugadorId,
                detalles
            ));
        }
        
        // Si es una torre
        if (event.getEntidad() instanceof Torre) {
            int destruidaPor = (jugadorId == 1) ? 2 : 1;
            EstadisticasPartidaJugador stats = getStatsJugador(destruidaPor);
            stats.registrarTorreDestruida();
            
            String detalles = String.format("Torre destruida: %s", event.getNombreEntidad());
            registroActual.registrarEvento(new EventoPartida(
                EventoPartida.TipoEvento.TORRE_DESTRUIDA,
                tickActual,
                destruidaPor,
                detalles
            ));
        }
    }
    
    private void procesarPartidaTerminada(PartidaTerminadaEvent event) {
        // Calcular estadísticas finales
        int torresJ1 = tablero.contarTorresVivas(1);
        int torresJ2 = tablero.contarTorresVivas(2);
        int vidaJ1 = tablero.calcularVidaTotalTorres(1);
        int vidaJ2 = tablero.calcularVidaTotalTorres(2);
        
        registroActual.finalizarPartida(
            event.getGanadorId(),
            event.getMotivo(),
            tickActual,
            3 - torresJ2, // Torres destruidas = 3 - torres vivas del oponente
            3 - torresJ1,
            vidaJ1,
            vidaJ2
        );
        
        registroActual.registrarEvento(new EventoPartida(
            EventoPartida.TipoEvento.PARTIDA_TERMINADA,
            tickActual,
            event.getGanadorId(),
            "Ganador: J" + event.getGanadorId() + " - " + event.getMotivo()
        ));
    }
    
    private EstadisticasPartidaJugador getStatsJugador(int jugadorId) {
        return jugadorId == 1 ? registroActual.getStatsJ1() : registroActual.getStatsJ2();
    }
    
    public RegistroPartida getRegistroActual() {
        return registroActual;
    }
}
