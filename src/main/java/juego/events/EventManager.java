package juego.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor central de eventos. Se encarga de mantener un registro de los listeners
 * y de notificarles cuando un evento ocurre.
 */
public class EventManager {
    private final Map<Class<? extends GameEvent>, List<GameEventListener>> listeners = new HashMap<>();

    public void subscribe(Class<? extends GameEvent> eventType, GameEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(Class<? extends GameEvent> eventType, GameEventListener listener) {
        List<GameEventListener> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void notify(GameEvent event) {
        // Notificar a los listeners suscritos al tipo de evento específico
        List<GameEventListener> specificListeners = listeners.get(event.getClass());
        if (specificListeners != null) {
            for (GameEventListener listener : new ArrayList<>(specificListeners)) { // Copia para evitar ConcurrentModificationException
                listener.onGameEvent(event);
            }
        }

        // Notificar a los listeners suscritos a todos los eventos (GameEvent.class)
        List<GameEventListener> allEventListeners = listeners.get(GameEvent.class);
        if (allEventListeners != null) {
            for (GameEventListener listener : new ArrayList<>(allEventListeners)) { // Copia para evitar ConcurrentModificationException
                listener.onGameEvent(event);
            }
        }
    }
}
