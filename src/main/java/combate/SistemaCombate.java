package combate;

import entidades.base.EntidadJuego;
import entidades.edificios.Torre;
import entidades.tropas.Tropa;
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
    private List<EntidadJuego> entidadesMuertas;

    public SistemaCombate(Tablero tablero) {
        this.tablero = tablero;
        this.entidadesMuertas = new ArrayList<>();
    }

    /**
     * Ejecuta un tick completo de combate.
     * Todas las entidades atacan si pueden.
     */
    public void ejecutarCombate(int tickActual) {
        entidadesMuertas.clear();

        // Torres atacan
        ejecutarAtaquesTorres(tickActual);

        // Tropas atacan
        ejecutarAtaquesTropas(tickActual);

        // Marcar entidades muertas (el tablero se encarga de limpiarlas)
        marcarEntidadesMuertas();
    }

    private void ejecutarAtaquesTorres(int tickActual) {
        for (Torre torre : tablero.getTorres()) {
            if (!torre.estaViva()) continue;

            // Las torres tienen su propia lógica simple de búsqueda de objetivos en rango.
            Tropa objetivo = encontrarTropaObjetivoParaTorre(torre);
            if (objetivo != null && torre.puedeAtacar(tickActual)) {
                int danio = torre.atacar(objetivo, tickActual);
                if (danio > 0) {
                    System.out.println("Torre " + torre.getClass().getSimpleName() +
                            " J" + torre.getJugadorId() +
                            " ataca a " + objetivo.getNombre() +
                            " J" + objetivo.getJugadorId() +
                            " por " + danio + " de daño");

                    if (!objetivo.estaViva()) {
                        entidadesMuertas.add(objetivo);
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
            String nombreObjetivo = (objetivo instanceof Tropa) ? ((Tropa)objetivo).getNombre() : objetivo.getClass().getSimpleName();
            System.out.println(tropa.getNombre() +
                    " J" + tropa.getJugadorId() +
                    " ataca a " + nombreObjetivo +
                    " J" + objetivo.getJugadorId() +
                    " por " + danio + " de daño");

            if (!objetivo.estaViva()) {
                entidadesMuertas.add(objetivo);
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
                objetivoSecundario.recibirDanio(tropa.getDanioAtaque());
                String nombreObjetivo = (objetivoSecundario instanceof Tropa) ? ((Tropa)objetivoSecundario).getNombre() : objetivoSecundario.getClass().getSimpleName();
                System.out.println("  → Daño en área a " + nombreObjetivo +
                        " J" + objetivoSecundario.getJugadorId() +
                        " por " + tropa.getDanioAtaque() + " de daño");

                if (!objetivoSecundario.estaViva()) {
                    entidadesMuertas.add(objetivoSecundario);
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
                double distancia = torre.getPosicion().calcularDistancia(tropa.getPosicion());
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
            if (tropa.estaViva() && centro.calcularDistancia(tropa.getPosicion()) <= radio) {
                entidadesEnArea.add(tropa);
            }
        }

        // Añadir torres enemigas en el área
        for (Torre torre : tablero.getTorresJugador(jugadorEnemigo)) {
            if (torre.estaViva() && centro.calcularDistancia(torre.getPosicion()) <= radio) {
                entidadesEnArea.add(torre);
            }
        }

        return entidadesEnArea;
    }

    private void marcarEntidadesMuertas() {
        for (EntidadJuego entidadJuego : entidadesMuertas) {
            if (entidadJuego instanceof Tropa) {
                System.out.println("→ " + ((Tropa)entidadJuego).getNombre() +
                        " J" + entidadJuego.getJugadorId() + " eliminada del campo");
            }
        }
    }

    public List<EntidadJuego> getEntidadesMuertas() {
        return new ArrayList<>(entidadesMuertas);
    }
}

