package juego.events;

import entidades.base.EntidadJuego;
import entidades.tropas.Tropa;

public class EntidadDestruidaEvent implements GameEvent {
    private final EntidadJuego entidad;

    public EntidadDestruidaEvent(EntidadJuego entidad) {
        this.entidad = entidad;
    }

    public EntidadJuego getEntidad() {
        return entidad;
    }

    public String getNombreEntidad() {
        if (entidad instanceof Tropa) {
            return ((Tropa) entidad).getNombre();
        }
        return entidad.getClass().getSimpleName();
    }
}
