package combate;

import entidades.base.EntidadJuego;
import entidades.edificios.Torre;
import entidades.tropas.Tropa;
import juego.events.AtaqueRealizadoEvent;
import juego.events.EntidadDestruidaEvent;
import juego.events.EventManager;
import tablero.Tablero;
import tablero.Posicion;

import java.util.ArrayList;
import java.util.List;

/**
 * Maneja todo el sistema de combate del juego.
 * Detecta objetivos, ejecuta ataques y maneja muerte de entidades.
 */
public class SistemaCombate {
    private Tablero tablero;
    private EventManager eventManager;

    public SistemaCombate(Tablero tablero, EventManager eventManager) {
        this.tablero = tablero;
        this.eventManager = eventManager;
    }

    /**
     * Ejecuta un tick completo de combate.
     * Todas las entidades atacan si pueden.
     */
    public void ejecutarCombate(int tickActual) {
        // Torres atacan
        ejecutarAtaquesTorres(tickActual);

        // Tropas atacan
        ejecutarAtaquesTropas(tickActual);
    }

    private void ejecutarAtaquesTorres(int tickActual) {
        for (Torre torre : tablero.getTorres()) {
            if (!torre.estaViva()) continue;

            // Las torres tienen su propia lógica simple de búsqueda de objetivos en rango.
            Tropa objetivo = encontrarTropaObjetivoParaTorre(torre);
            if (objetivo != null && torre.puedeAtacar(tickActual)) {
                int danio = torre.atacar(objetivo, tickActual);
                if (danio > 0) {
                    eventManager.notify(new AtaqueRealizadoEvent(torre, objetivo, danio));

                    if (!objetivo.estaViva()) {
                        eventManager.notify(new EntidadDestruidaEvent(objetivo));
                    }
                }
            }
        }
    }

    private void ejecutarAtaquesTropas(int tickActual) {
        for (Tropa tropa : tablero.getTropas()) {
            if (!tropa.estaViva()) continue;

            // Usar el objetivo asignado por el SistemaMovimiento.
            EntidadJuego objetivo = tropa.getObjetivo();

            // Si el objetivo existe y la tropa puede atacarlo, proceder.
            if (objetivo != null && tropa.puedeAtacar(objetivo, tickActual)) {
                if (tropa.getTipoAtaque() == Tropa.TipoAtaque.AREA) {
                    ejecutarAtaqueArea(tropa, objetivo, tickActual);
                } else {
                    ejecutarAtaqueIndividual(tropa, objetivo, tickActual);
                }
            }
        }
    }

    private void ejecutarAtaqueIndividual(Tropa tropa, EntidadJuego objetivo, int tickActual) {
        int danio = tropa.atacar(objetivo, tickActual);
        if (danio > 0) {
            eventManager.notify(new AtaqueRealizadoEvent(tropa, objetivo, danio));

            if (!objetivo.estaViva()) {
                eventManager.notify(new EntidadDestruidaEvent(objetivo));
            }
        }
    }

    private void ejecutarAtaqueArea(Tropa tropa, EntidadJuego objetivoPrincipal, int tickActual) {
        // Atacar objetivo principal
        ejecutarAtaqueIndividual(tropa, objetivoPrincipal, tickActual);

        // Buscar objetivos secundarios en el radio de área
        List<EntidadJuego> objetivosEnArea = encontrarEntidadesEnArea(
                objetivoPrincipal.getPosicion(),
                tropa.getRadioArea(),
                tropa.getJugadorId()
        );

        for (EntidadJuego objetivoSecundario : objetivosEnArea) {
            if (objetivoSecundario != objetivoPrincipal && objetivoSecundario.estaViva()) {
                int danioArea = tropa.getDanioAtaque();
                objetivoSecundario.recibirDanio(danioArea);
                eventManager.notify(new AtaqueRealizadoEvent(tropa, objetivoSecundario, danioArea));

                if (!objetivoSecundario.estaViva()) {
                    eventManager.notify(new EntidadDestruidaEvent(objetivoSecundario));
                }
            }
        }
    }

    private Tropa encontrarTropaObjetivoParaTorre(Torre torre) {
        Tropa objetivoMasCercano = null;
        double menorDistancia = Double.MAX_VALUE;
        int jugadorEnemigo = torre.getJugadorId() == 1 ? 2 : 1;

        for (Tropa tropa : tablero.getTropasJugador(jugadorEnemigo)) {
            if (tropa.estaViva()) {
                // CORREGIDO: Usar el cálculo de distancia a la entidad
                double distancia = torre.getPosicion().calcularDistancia(tropa);
                if (distancia <= torre.getRangoAtaque() && distancia < menorDistancia) {
                    menorDistancia = distancia;
                    objetivoMasCercano = tropa;
                }
            }
        }
        return objetivoMasCercano;
    }

    private List<EntidadJuego> encontrarEntidadesEnArea(Posicion centro, int radio, int jugadorAtacante) {
        List<EntidadJuego> entidadesEnArea = new ArrayList<>();
        int jugadorEnemigo = jugadorAtacante == 1 ? 2 : 1;

        // Añadir tropas enemigas en el área
        for (Tropa tropa : tablero.getTropasJugador(jugadorEnemigo)) {
            // CORREGIDO: Usar el cálculo de distancia a la entidad
            if (tropa.estaViva() && centro.calcularDistancia(tropa) <= radio) {
                entidadesEnArea.add(tropa);
            }
        }

        // Añadir torres enemigas en el área
        for (Torre torre : tablero.getTorresJugador(jugadorEnemigo)) {
            // CORREGIDO: Usar el cálculo de distancia a la entidad
            if (torre.estaViva() && centro.calcularDistancia(torre) <= radio) {
                entidadesEnArea.add(torre);
            }
        }

        return entidadesEnArea;
    }


}

