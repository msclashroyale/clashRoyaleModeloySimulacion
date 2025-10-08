package movimiento;

import entidades.tropas.Tropa;
import tablero.Tablero;
import tablero.Posicion;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase refactorizada que maneja el pathfinding (búsqueda de rutas) para las tropas.
 * Mantiene toda la lógica sofisticada del Pathfinder original pero con mejor organización.
 */
public class BuscadorCaminos {

    private final Tablero tablero;

    public BuscadorCaminos(Tablero tablero) {
        this.tablero = tablero;
    }

    /**
     * Encuentra el siguiente paso óptimo para una tropa hacia su destino.
     * Mantiene la lógica original pero con nombres más claros.
     */
    public Posicion encontrarSiguientePaso(Tropa tropa, Posicion destino) {
        Posicion posicionActual = tropa.getPosicion();

        // Si ya está en el destino, no moverse
        if (posicionActual.equals(destino)) {
            return null;
        }

        // Si está dentro del rango de ataque, no necesita moverse más
        double distanciaAlDestino = posicionActual.calcularDistancia(destino);
        if (distanciaAlDestino <= tropa.getRangoAtaque()) {
            return null;
        }

        // Verificar si necesita cruzar el río usando un puente
        if (necesitaCruzarPorPuente(posicionActual, destino)) {
            Posicion puente = encontrarPuenteMasCercano(posicionActual);

            // Si no está en el puente, ir hacia él primero
            if (!posicionActual.equals(puente)) {
                return calcularSiguientePasoHacia(posicionActual, puente);
            }
        }

        // Movimiento normal hacia el destino
        return calcularSiguientePasoHacia(posicionActual, destino);
    }

    /**
     * Calcula el siguiente paso hacia un destino específico.
     * Prioriza movimiento diagonal, luego horizontal/vertical.
     */
    private Posicion calcularSiguientePasoHacia(Posicion desde, Posicion hacia) {
        // Calcular dirección (signo de la diferencia)
        int deltaX = Integer.signum(hacia.getX() - desde.getX());
        int deltaY = Integer.signum(hacia.getY() - desde.getY());

        // Lista de movimientos candidatos en orden de prioridad
        List<Posicion> candidatos = new ArrayList<>();

        // Prioridad 1: Movimiento diagonal directo (más eficiente)
        if (deltaX != 0 && deltaY != 0) {
            candidatos.add(new Posicion(desde.getX() + deltaX, desde.getY() + deltaY));
        }

        // Prioridad 2: Movimientos horizontales y verticales
        if (deltaX != 0) {
            candidatos.add(new Posicion(desde.getX() + deltaX, desde.getY()));
        }
        if (deltaY != 0) {
            candidatos.add(new Posicion(desde.getX(), desde.getY() + deltaY));
        }

        // Buscar el primer candidato que sea válido
        for (Posicion candidato : candidatos) {
            if (esPosicionValidaParaMoverse(candidato)) {
                return candidato;
            }
        }

        return null; // No hay movimiento posible
    }

    /**
     * Verifica si una posición es válida para que una tropa se mueva a ella
     */
    private boolean esPosicionValidaParaMoverse(Posicion posicion) {
        // Verificar límites del tablero
        if (posicion.getX() < 0 || posicion.getX() >= Tablero.ANCHO ||
                posicion.getY() < 0 || posicion.getY() >= Tablero.ALTO) {
            return false;
        }

        // Verificar si el terreno es transitable
        if (!tablero.getTipoTerreno(posicion.getX(), posicion.getY()).esTransitable()) {
            return false;
        }

        // Verificar que no haya otra tropa en esa posición
        return tablero.obtenerTropaEnPosicion(posicion) == null;
    }

    /**
     * Encuentra el puente más cercano a una posición dada.
     * Considera las 4 posiciones de puente disponibles.
     */
    public Posicion encontrarPuenteMasCercano(Posicion desde) {
        // Las 4 posiciones de puente disponibles
        Posicion puente1Rio1 = new Posicion(Tablero.PUENTE_X1, Tablero.RIO_Y1); // (5,15)
        Posicion puente1Rio2 = new Posicion(Tablero.PUENTE_X1, Tablero.RIO_Y2); // (5,16)
        Posicion puente2Rio1 = new Posicion(Tablero.PUENTE_X2, Tablero.RIO_Y1); // (12,15)
        Posicion puente2Rio2 = new Posicion(Tablero.PUENTE_X2, Tablero.RIO_Y2); // (12,16)

        // Calcular distancia a cada puente
        double distancia1_1 = desde.calcularDistancia(puente1Rio1);
        double distancia1_2 = desde.calcularDistancia(puente1Rio2);
        double distancia2_1 = desde.calcularDistancia(puente2Rio1);
        double distancia2_2 = desde.calcularDistancia(puente2Rio2);

        // Encontrar el puente con menor distancia
        double menorDistancia = Math.min(Math.min(distancia1_1, distancia1_2),
                Math.min(distancia2_1, distancia2_2));

        // Retornar el puente más cercano
        if (menorDistancia == distancia1_1) return puente1Rio1;
        if (menorDistancia == distancia1_2) return puente1Rio2;
        if (menorDistancia == distancia2_1) return puente2Rio1;
        return puente2Rio2;
    }

    /**
     * Verifica si una tropa necesita usar un puente para llegar a su destino.
     * Esto ocurre cuando están en lados opuestos del río.
     */
    public boolean necesitaCruzarPorPuente(Posicion desde, Posicion hasta) {
        // Verificar en qué lado del río están las posiciones
        boolean desdeArriba = desde.getY() < Tablero.RIO_Y1;    // Y < 15
        boolean desdeAbajo = desde.getY() > Tablero.RIO_Y2;     // Y > 16
        boolean hastaArriba = hasta.getY() < Tablero.RIO_Y1;    // Y < 15
        boolean hastaAbajo = hasta.getY() > Tablero.RIO_Y2;     // Y > 16

        // Necesita puente si uno está arriba del río y otro abajo
        return (desdeArriba && hastaAbajo) || (desdeAbajo && hastaArriba);
    }

    /**
     * Busca una ruta alternativa cuando el camino directo está bloqueado.
     * Útil para evitar tropas que se queden atascadas.
     */
    public Posicion buscarRutaAlternativa(Posicion actual, Posicion destino) {
        // Obtener todas las posiciones adyacentes (8 direcciones)
        List<Posicion> posicionesAdyacentes = obtenerPosicionesAdyacentes(actual);

        // Filtrar solo las posiciones válidas para moverse
        List<Posicion> posicionesValidas = new ArrayList<>();
        for (Posicion posicion : posicionesAdyacentes) {
            if (esPosicionValidaParaMoverse(posicion)) {
                posicionesValidas.add(posicion);
            }
        }

        if (posicionesValidas.isEmpty()) {
            return null; // No hay alternativas disponibles
        }

        // De las posiciones válidas, elegir la que más se acerque al destino
        return elegirPosicionMasCercanaAlDestino(posicionesValidas, destino);
    }

    /**
     * Obtiene las 8 posiciones adyacentes a una posición central.
     * Incluye movimientos cardinales y diagonales.
     */
    private List<Posicion> obtenerPosicionesAdyacentes(Posicion centro) {
        List<Posicion> adyacentes = new ArrayList<>();

        // Deltas para las 8 direcciones: N, NE, E, SE, S, SW, W, NW
        int[] deltaX = {-1, -1, -1,  0,  0,  1,  1,  1};
        int[] deltaY = {-1,  0,  1, -1,  1, -1,  0,  1};

        for (int i = 0; i < 8; i++) {
            int nuevaX = centro.getX() + deltaX[i];
            int nuevaY = centro.getY() + deltaY[i];
            adyacentes.add(new Posicion(nuevaX, nuevaY));
        }

        return adyacentes;
    }

    /**
     * De una lista de posiciones válidas, elige la que esté más cerca del destino.
     */
    private Posicion elegirPosicionMasCercanaAlDestino(List<Posicion> posicionesValidas, Posicion destino) {
        Posicion mejorOpcion = null;
        double menorDistancia = Double.MAX_VALUE;

        for (Posicion posicion : posicionesValidas) {
            double distancia = posicion.calcularDistancia(destino);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                mejorOpcion = posicion;
            }
        }

        return mejorOpcion;
    }

    /**
     * Encuentra una ruta completa desde un punto hasta otro.
     * Útil para planificación más avanzada de movimientos.
     */
    public List<Posicion> planificarRutaCompleta(Posicion desde, Posicion hasta) {
        List<Posicion> rutaPlanificada = new ArrayList<>();

        // Si necesita puente, agregarlo como punto intermedio
        if (necesitaCruzarPorPuente(desde, hasta)) {
            Posicion puenteNecesario = encontrarPuenteMasCercano(desde);
            rutaPlanificada.add(puenteNecesario);
        }

        // Agregar destino final
        rutaPlanificada.add(hasta);

        return rutaPlanificada;
    }

    /**
     * Verifica si hay un camino libre directo entre dos posiciones.
     * Útil para optimizaciones de pathfinding.
     */
    public boolean hayCaminoLibreDirecto(Posicion desde, Posicion hasta) {
        // Implementación simple: verificar si no necesita puente
        return !necesitaCruzarPorPuente(desde, hasta);
    }
}
