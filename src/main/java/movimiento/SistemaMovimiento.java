package movimiento;

import tablero.Tablero;
import tablero.Posicion;
import entidades.tropas.Tropa;
import entidades.base.EntidadJuego;
import entidades.edificios.Torre;
import entidades.edificios.TorreRey;
import entidades.edificios.TorrePrincesa;

import java.util.List;
import java.util.stream.Collectors;

public class SistemaMovimiento {

    private final Tablero tablero;
    private final BuscadorCaminos buscadorCaminos;

    public SistemaMovimiento(Tablero tablero) {
        this.tablero = tablero;
        this.buscadorCaminos = new BuscadorCaminos(tablero);
    }

    public void actualizarTropas(int tickActual) {
        List<Tropa> tropasVivas = tablero.getTropas().stream()
                .filter(Tropa::estaViva)
                .toList();

        for (Tropa tropa : tropasVivas) {
            actualizarTropa(tropa, tickActual);
        }
    }

    private void actualizarTropa(Tropa tropa, int tickActual) {
        // --- NUEVA LÓGICA DE RE-EVALUACIÓN CONSTANTE DE OBJETIVO ---
        EntidadJuego nuevoObjetivo = determinarNuevoObjetivo(tropa);
        tropa.setObjetivo(nuevoObjetivo);

        if (nuevoObjetivo == null) {
            return; // No hay objetivos disponibles, la tropa no se mueve.
        }

        // Si ya estamos en rango, no hay necesidad de moverse. El sistema de combate se encargará.
        if (tropa.estaEnRangoDeAtaque(tropa.getObjetivo())) {
            return;
        }

        // Moverse hacia el objetivo según la velocidad de la tropa.
        if (tickActual % tropa.getTicksParaMover() == 0) {
            Posicion siguientePaso = buscadorCaminos.encontrarSiguientePaso(tropa, tropa.getObjetivo(), tropa.getPosicionAnterior());

            if (siguientePaso != null && !hayObstaculoEn(siguientePaso, tropa)) {
                tropa.setPosicion(siguientePaso);
            } else {
                // Si el camino principal está bloqueado, buscar una ruta alternativa para rodear el obstáculo.
                Posicion pasoAlternativo = buscadorCaminos.buscarRutaAlternativa(tropa.getPosicion(), tropa.getObjetivo().getPosicion());
                if (pasoAlternativo != null) {
                    tropa.setPosicion(pasoAlternativo);
                }
            }
        }
    }

    private EntidadJuego determinarNuevoObjetivo(Tropa tropa) {
        // Las tropas que solo atacan estructuras ignoran a otras tropas.
        if (tropa.getTipoObjetivo() == Tropa.TipoObjetivo.ESTRUCTURAS) {
            return encontrarTorreObjetivoPrioritaria(tropa);
        }

        // 1. Buscar tropas enemigas dentro del rango de detección.
        EntidadJuego objetivo = encontrarTropaEnemigaEnRango(tropa);
        if (objetivo != null) {
            return objetivo;
        }

        // 2. Si no hay tropas en rango, buscar la torre prioritaria.
        return encontrarTorreObjetivoPrioritaria(tropa);
    }

    private Tropa encontrarTropaEnemigaEnRango(Tropa tropa) {
        int jugadorEnemigo = (tropa.getJugadorId() == 1) ? 2 : 1;
        Tropa tropaMasCercana = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Tropa tropaEnemiga : tablero.getTropasJugador(jugadorEnemigo)) {
            if (tropaEnemiga.estaViva()) {
                double distancia = tropa.getPosicion().calcularDistancia(tropaEnemiga);
                // Comprobar si está dentro del rango de detección
                if (distancia <= tropa.getRangoDeteccion() && distancia < menorDistancia) {
                    menorDistancia = distancia;
                    tropaMasCercana = tropaEnemiga;
                }
            }
        }
        return tropaMasCercana;
    }

    private Torre encontrarTorreObjetivoPrioritaria(Tropa tropa) {
        int jugadorEnemigo = (tropa.getJugadorId() == 1) ? 2 : 1;
        List<Torre> torresEnemigas = tablero.getTorresJugador(jugadorEnemigo);

        List<Torre> torresPrincesaVivas = torresEnemigas.stream()
                .filter(t -> t instanceof TorrePrincesa && t.estaViva())
                .collect(Collectors.toList());

        // Si ambas torres de princesa están vivas, ir a por la más cercana.
        if (torresPrincesaVivas.size() == 2) {
            Torre torreIzq = torresPrincesaVivas.get(0);
            Torre torreDer = torresPrincesaVivas.get(1);
            double distIzq = tropa.getPosicion().calcularDistancia(torreIzq);
            double distDer = tropa.getPosicion().calcularDistancia(torreDer);
            return distIzq <= distDer ? torreIzq : torreDer;
        }

        // Si solo una torre de princesa está viva, decidir entre esa y la del rey.
        if (torresPrincesaVivas.size() == 1) {
            Torre torrePrincesaRestante = torresPrincesaVivas.get(0);
            Torre torreRey = torresEnemigas.stream()
                    .filter(t -> t instanceof TorreRey)
                    .findFirst().orElse(null);

            if (torreRey == null) return torrePrincesaRestante; // Salvaguarda

            double distPrincesa = tropa.getPosicion().calcularDistancia(torrePrincesaRestante);
            double distRey = tropa.getPosicion().calcularDistancia(torreRey);
            return distPrincesa <= distRey ? torrePrincesaRestante : torreRey;
        }

        // Si no hay torres de princesa, el único objetivo es la torre del rey.
        return torresEnemigas.stream()
                .filter(t -> t instanceof TorreRey && t.estaViva())
                .findFirst().orElse(null);
    }

    private boolean hayObstaculoEn(Posicion posicion, Tropa tropaActual) {
        EntidadJuego entidad = tablero.obtenerTropaEnPosicion(posicion);
        if (entidad != null && entidad != tropaActual) {
            return true;
        }
        if (tablero.obtenerTorreEnPosicion(posicion) != null) {
            return true;
        }
        return !tablero.getTipoTerreno(posicion.getX(), posicion.getY()).esTransitable();
    }
}