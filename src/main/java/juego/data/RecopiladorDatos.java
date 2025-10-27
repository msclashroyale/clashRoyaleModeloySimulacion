package juego.data;

import cartas.Carta;
import entidades.base.EntidadJuego;
import entidades.tropas.Tropa;
import juego.Partida;
import juego.events.*;
import jugador.Jugador;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * El "espía". Escucha todos los eventos de una partida y los convierte en registros CSV.
 */
public class RecopiladorDatos implements GameEventListener {

    private final String idPartida;
    private final Partida partida; // Referencia a la partida para consultar estado
    private final List<RegistroCSV> registros;

    private String ganadorFinal = "";
    private String duracionFinal = "";

    public RecopiladorDatos(String idPartida, Partida partida) {
        this.idPartida = idPartida;
        this.partida = partida;
        this.registros = new ArrayList<>();
    }

    @Override
    public void onGameEvent(GameEvent event) {
        RegistroCSV registro = new RegistroCSV();

        // 1. Rellenar datos comunes (la "foto" del instante)
        rellenarDatosComunes(registro);

        // 2. Rellenar datos específicos del evento
        if (event instanceof TropaDesplegadaEvent) {
            procesarTropaDesplegada(registro, (TropaDesplegadaEvent) event);
        } else if (event instanceof PartidaTerminadaEvent) {
            procesarPartidaTerminada(registro, (PartidaTerminadaEvent) event);
        }

        registros.add(registro);

        // 3. Si la partida ha terminado, actualizar todos los registros anteriores
        if (!ganadorFinal.isEmpty()) {
            actualizarRegistrosConResultadoFinal();
        }
    }

    private void rellenarDatosComunes(RegistroCSV registro) {
        Jugador j1 = partida.getJugador1();
        Jugador j2 = partida.getJugador2();

        registro.id_partida = this.idPartida;
        registro.tick = String.valueOf(partida.getTickActual());
        registro.estrategia_j1 = j1.getEstrategiaIA().getClass().getSimpleName();
        registro.estrategia_j2 = j2.getEstrategiaIA().getClass().getSimpleName();
        registro.elixir_j1 = String.valueOf(j1.getSistemaElixir().getElixirActual());
        registro.elixir_j2 = String.valueOf(j2.getSistemaElixir().getElixirActual());
        registro.mano_j1 = formatearCartas(j1.getMazo().getCartasEnMano());
        registro.mano_j2 = formatearCartas(j2.getMazo().getCartasEnMano());
        registro.mazo_j1 = formatearCartas(j1.getMazo().getMazoCompleto());
        registro.mazo_j2 = formatearCartas(j2.getMazo().getMazoCompleto());
    }

    private void procesarTropaDesplegada(RegistroCSV registro, TropaDesplegadaEvent event) {
        Tropa tropa = event.getTropa();
        registro.tipo_evento = "TROPA_DESPLEGADA";
        registro.actor_evento = "JUGADOR_" + event.getJugador().getId();
        registro.carta_usada = tropa.getNombre();
        registro.coste_carta = String.valueOf(tropa.getCostoElixir());
        registro.pos_x = String.valueOf(event.getPosicion().getX());
        registro.pos_y = String.valueOf(event.getPosicion().getY());

        // Nuevos atributos
        registro.tropa_vida_maxima = String.valueOf(tropa.getVidaMaxima());
        registro.tropa_dano = String.valueOf(tropa.getDanioAtaque());
        registro.tropa_rango = String.valueOf(tropa.getRangoAtaque());
        registro.tropa_tipo_ataque = tropa.getTipoAtaque().name();
        registro.tropa_tipo_objetivo = tropa.getTipoObjetivo().name();
        registro.tropa_nivel = String.valueOf(tropa.getNivel());
    }

    /*private void procesarAtaqueRealizado(RegistroCSV registro, AtaqueRealizadoEvent event) {
        registro.tipo_evento = "ATAQUE_REALIZADO";
        registro.actor_evento = "ENTIDAD_" + event.getAtacante().getId();
        registro.id_atacante = String.valueOf(event.getAtacante().getId());
        registro.id_objetivo = String.valueOf(event.getObjetivo().getId());
        registro.dano_base = String.valueOf(event.getDanio());
        registro.vida_restante_obj = String.valueOf(event.getObjetivo().getVidaActual());
    }*/

    /*private void procesarEntidadDestruida(RegistroCSV registro, EntidadDestruidaEvent event) {
        registro.tipo_evento = "ENTIDAD_DESTRUIDA";
        registro.id_entidad_destruida = String.valueOf(event.getEntidad().getId());
    }*/

    private void procesarPartidaTerminada(RegistroCSV registro, PartidaTerminadaEvent event) {
        registro.tipo_evento = "PARTIDA_TERMINADA";
        this.ganadorFinal = (event.getGanadorId() == 0) ? "EMPATE" : "JUGADOR_" + event.getGanadorId();
        this.duracionFinal = String.valueOf(partida.getTickActual());
    }

    private void actualizarRegistrosConResultadoFinal() {
        for (RegistroCSV r : registros) {
            if (r.ganador_final.isEmpty()) {
                r.ganador_final = this.ganadorFinal;
            }
            if (r.duracion_final.isEmpty()) {
                r.duracion_final = this.duracionFinal;
            }
        }
    }

    private String formatearCartas(List<Carta> cartas) {
        return cartas.stream()
                   .map(Carta::getNombre)
                   .collect(Collectors.joining("|")); // Usamos | como separador
    }

    public List<RegistroCSV> getRegistros() {
        return registros;
    }
}
