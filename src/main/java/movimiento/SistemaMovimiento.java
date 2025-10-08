package movimiento;

import tablero.Tablero;
import tablero.Posicion;
import entidades.tropas.Tropa;
import entidades.base.EntidadJuego;
import entidades.edificios.Torre;
import entidades.edificios.TorreRey;
import entidades.edificios.TorrePrincesa;

import java.util.List;

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
        // Si el objetivo actual ya no es válido (murió o es nulo), buscar uno nuevo.
        if (tropa.getObjetivo() == null || !tropa.getObjetivo().estaViva()) {
            EntidadJuego nuevoObjetivo = buscarEntidadObjetivo(tropa);
            tropa.setObjetivo(nuevoObjetivo);
            if (nuevoObjetivo == null) {
                return; // No hay objetivos disponibles, la tropa no se mueve.
            }
        }

        // Si ya estamos en rango, no hay necesidad de moverse. El sistema de combate se encargará.
        if (tropa.estaEnRangoDeAtaque(tropa.getObjetivo().getPosicion())) {
            return;
        }

        // Moverse hacia el objetivo según la velocidad de la tropa.
        if (tickActual % tropa.getTicksParaMover() == 0) {
            Posicion siguientePaso = buscadorCaminos.encontrarSiguientePaso(tropa, tropa.getObjetivo().getPosicion());

            if (siguientePaso != null && !hayObstaculoEn(siguientePaso, tropa)) {
                tropa.setPosicion(siguientePaso);
            } else {
                // Si el camino está bloqueado, invalidar el objetivo para forzar una nueva búsqueda en el siguiente tick.
                tropa.setObjetivo(null);
            }
        }
    }

    private EntidadJuego buscarEntidadObjetivo(Tropa tropa) {
        int jugadorEnemigo = (tropa.getJugadorId() == 1) ? 2 : 1;

        if (tropa.getTipoObjetivo() == Tropa.TipoObjetivo.ESTRUCTURAS) {
            return encontrarTorreObjetivo(tropa.getPosicion(), jugadorEnemigo);
        } else { // TROPAS_Y_ESTRUCTURAS
            return encontrarObjetivoMasCercano(tropa, jugadorEnemigo);
        }
    }

    private Torre encontrarTorreObjetivo(Posicion origen, int jugadorEnemigo) {
        Torre torreMasCercana = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Torre torre : tablero.getTorresJugador(jugadorEnemigo)) {
            if (torre.estaViva()) {
                // Regla: No atacar a la Torre del Rey si hay Torres de Princesa vivas.
                if (torre instanceof TorreRey && hayTorresPrincesaVivas(jugadorEnemigo)) {
                    continue;
                }
                double distancia = origen.calcularDistancia(torre.getPosicion());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    torreMasCercana = torre;
                }
            }
        }
        return torreMasCercana;
    }

    private EntidadJuego encontrarObjetivoMasCercano(Tropa tropa, int jugadorEnemigo) {
        EntidadJuego objetivoMasCercano = null;
        double menorDistancia = Double.MAX_VALUE;

        // Buscar en tropas enemigas
        for (Tropa tropaEnemiga : tablero.getTropasJugador(jugadorEnemigo)) {
            if (tropaEnemiga.estaViva()) {
                double distancia = tropa.getPosicion().calcularDistancia(tropaEnemiga.getPosicion());
                if (distancia < menorDistancia) {
                    menorDistancia = distancia;
                    objetivoMasCercano = tropaEnemiga;
                }
            }
        }

        // Buscar en torres enemigas
        Torre torreObjetivo = encontrarTorreObjetivo(tropa.getPosicion(), jugadorEnemigo);
        if (torreObjetivo != null) {
            double distancia = tropa.getPosicion().calcularDistancia(torreObjetivo.getPosicion());
            if (distancia < menorDistancia) {
                objetivoMasCercano = torreObjetivo;
            }
        }

        return objetivoMasCercano;
    }
    
    private boolean hayTorresPrincesaVivas(int jugadorId) {
        for (Torre torre : tablero.getTorresJugador(jugadorId)) {
            if (torre instanceof TorrePrincesa && torre.estaViva()) {
                return true;
            }
        }
        return false;
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