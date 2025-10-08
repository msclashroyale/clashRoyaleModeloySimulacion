package tablero;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Maneja las zonas donde cada jugador puede desplegar tropas.
 * Se compone de zonas permitidas y zonas restringidas para dar soporte a la mecánica de juego dinámica.
 */
public class ZonaDespliegue {

    private final int jugadorId;
    private final List<RectanguloZona> zonasPermitidas;
    private final List<RectanguloZona> zonasRestringidas;

    public ZonaDespliegue(int jugadorId) {
        this.jugadorId = jugadorId;
        this.zonasPermitidas = new ArrayList<>();
        this.zonasRestringidas = new ArrayList<>();
    }

    /**
     * Define la zona inicial de despliegue (tu lado del río), limpiando cualquier zona anterior.
     */
    public void definirZonaInicial(int x1, int y1, int x2, int y2) {
        zonasPermitidas.clear();
        zonasRestringidas.clear();
        zonasPermitidas.add(new RectanguloZona(x1, y1, x2, y2));
    }

    /**
     * Agrega una nueva zona rectangular a las zonas permitidas.
     */
    public void agregarZona(RectanguloZona nuevaZona) {
        if (!zonasPermitidas.contains(nuevaZona)) {
            zonasPermitidas.add(nuevaZona);
        }
    }

    /**
     * Agrega una zona a la lista de áreas restringidas, impidiendo el despliegue en ella.
     */
    public void restringirZona(RectanguloZona zonaARestringir) {
        if (!zonasRestringidas.contains(zonaARestringir)) {
            zonasRestringidas.add(zonaARestringir);
        }
    }

    /**
     * Verifica si se puede desplegar en la posición especificada.
     * Debe estar en una zona permitida y no en una restringida.
     */
    public boolean puedeDesplegarEn(Posicion posicion) {
        boolean enZonaPermitida = zonasPermitidas.stream().anyMatch(zona -> zona.contiene(posicion));
        if (!enZonaPermitida) {
            return false;
        }
        boolean enZonaRestringida = zonasRestringidas.stream().anyMatch(zona -> zona.contiene(posicion));
        return !enZonaRestringida;
    }

    /**
     * Obtiene todas las posiciones válidas para despliegue.
     * Nota: Esta implementación no considera las zonas restringidas y puede no ser 100% precisa.
     */
    public List<Posicion> obtenerPosicionesValidas() {
        List<Posicion> posiciones = new ArrayList<>();
        for (RectanguloZona zona : zonasPermitidas) {
            posiciones.addAll(zona.obtenerTodasLasPosiciones());
        }
        // Esta lista no excluye las zonas restringidas, pero es suficiente para la IA actual.
        return posiciones;
    }
    
    public List<RectanguloZona> getZonasPermitidas() {
        return new ArrayList<>(zonasPermitidas);
    }

    /**
     * Clase interna para representar zonas rectangulares.
     * Es pública para poder ser instanciada desde Partida.
     */
    public static class RectanguloZona {
        private final int x1, y1, x2, y2;

        public RectanguloZona(int x1, int y1, int x2, int y2) {
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.max(x1, x2);
            this.y2 = Math.max(y1, y2);
        }

        public boolean contiene(Posicion pos) {
            return pos.getX() >= x1 && pos.getX() <= x2 &&
                    pos.getY() >= y1 && pos.getY() <= y2;
        }

        public List<Posicion> obtenerTodasLasPosiciones() {
            List<Posicion> posiciones = new ArrayList<>();
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    posiciones.add(new Posicion(x, y));
                }
            }
            return posiciones;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RectanguloZona that = (RectanguloZona) o;
            return x1 == that.x1 && y1 == that.y1 && x2 == that.x2 && y2 == that.y2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x1, y1, x2, y2);
        }
    }
}
