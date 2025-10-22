package tablero;

import java.util.Objects;
import entidades.base.EntidadJuego;

public class Posicion {
    private int x;
    private int y;

    public Posicion(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    //Calcula la distancia en línea recta entre dos puntos usando el teorema de Pitágoras.
    public double calcularDistancia(Posicion otra) {
        double deltaX = otra.x - this.x;
        double deltaY = otra.y - this.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Calcula la distancia desde este punto hasta el borde más cercano de una EntidadJuego.
     * Esto es crucial para que las tropas interactúen con el área de los edificios, no solo con su centro.
     * @param entidad La entidad objetivo.
     * @return La distancia al borde más cercano de la entidad.
     */
    public double calcularDistancia(EntidadJuego entidad) {
        Posicion centroEntidad = entidad.getPosicion();
        double ancho = entidad.getAncho();
        double alto = entidad.getAlto();

        // El centro de una casilla (x,y) está en (x,y) en un sistema de coordenadas continuas.
        // Un edificio 3x3 centrado en (cx,cy) ocupa de (cx-1.5) a (cx+1.5).
        double entidadIzq = centroEntidad.getX() - ancho / 2.0;
        double entidadDer = centroEntidad.getX() + ancho / 2.0;
        double entidadAba = centroEntidad.getY() - alto / 2.0;
        double entidadArr = centroEntidad.getY() + alto / 2.0;

        // Encontrar el punto (closestX, closestY) en el borde del rectángulo más cercano a esta posición (this.x, this.y).
        double closestX = Math.max(entidadIzq, Math.min(this.x, entidadDer));
        double closestY = Math.max(entidadAba, Math.min(this.y, entidadArr));

        // Calcular la distancia euclidiana a ese punto más cercano.
        double deltaX = this.x - closestX;
        double deltaY = this.y - closestY;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    public boolean esAdyacente(Posicion otra) {
        return calcularDistancia(otra) <= 1.5;
    }

    public Posicion sumar(int deltaX, int deltaY) {
        return new Posicion(this.x + deltaX, this.y + deltaY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Posicion posicion = (Posicion) obj;
        return x == posicion.x && y == posicion.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}