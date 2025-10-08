//Zona de despliegue 
package tablero;

import java.util.ArrayList;
import java.util.List;


 //Maneja las zonas donde cada jugador puede desplegar tropas
 // Se expande cuando se destruyen torres princesa enemigas

public class ZonaDespliegue {

    private final int jugadorId;
    private final List<RectanguloZona> zonasPermitidas;

    public ZonaDespliegue(int jugadorId) {
        this.jugadorId = jugadorId;
        this.zonasPermitidas = new ArrayList<>();
    }

    
     //Define la zona inicial de despliegue (tu lado del río)
     
    public void definirZonaInicial(int x1, int y1, int x2, int y2) {
        zonasPermitidas.clear();
        zonasPermitidas.add(new RectanguloZona(x1, y1, x2, y2));
    }

    
     //Expande la zona cuando se destruye una torre princesa enemiga
     
    public void expandirZona(Posicion torrePrincesaDestruida) {
        // Determinar qué lado del tablero expandir
        boolean esLadoIzquierdo = torrePrincesaDestruida.getX() < 9; // Centro del tablero

        if (jugadorId == 1) { 
            // Jugador 1 puede expandirse hacia abajo
            if (esLadoIzquierdo) {
                zonasPermitidas.add(new RectanguloZona(0, 15, 8, 24)); // Lado izquierdo
            } else {
                zonasPermitidas.add(new RectanguloZona(9, 15, 17, 24)); // Lado derecho
            }
        } else {
            // Jugador 2 puede expandirse hacia arriba
            if (esLadoIzquierdo) {
                zonasPermitidas.add(new RectanguloZona(0, 7, 8, 16)); // Lado izquierdo
            } else {
                zonasPermitidas.add(new RectanguloZona(9, 7, 17, 16)); // Lado derecho
            }
        }
    }

    
     // Verifica si se puede desplegar en la posición especificada
     
    public boolean puedeDesplegarEn(Posicion posicion) {
        return zonasPermitidas.stream()
                .anyMatch(zona -> zona.contiene(posicion));
    }

    
     //Obtiene todas las posiciones válidas para despliegue
     
    public List<Posicion> obtenerPosicionesValidas() {
        List<Posicion> posiciones = new ArrayList<>();
        for (RectanguloZona zona : zonasPermitidas) {
            posiciones.addAll(zona.obtenerTodasLasPosiciones());
        }
        return posiciones;
    }

    // Clase interna para representar zonas rectangulares
    private static class RectanguloZona {
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
    }
}