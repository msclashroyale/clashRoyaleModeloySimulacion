package tablero;

import java.util.Objects;

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
        int deltaX = otra.x - this.x;
        int deltaY = otra.y - this.y;
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