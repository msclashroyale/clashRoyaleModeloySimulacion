package juego.events;

/**
 * Interfaz para los 'escuchadores' de eventos del juego.
 * Cualquier clase que quiera reaccionar a eventos debe implementar esta interfaz.
 */
public interface GameEventListener {
    void onGameEvent(GameEvent event);
}
