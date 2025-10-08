package ui;

/**
 * Enumeración que define los tipos de animaciones visuales para eventos de combate
 */
public enum TipoAnimacion {
    /**
     * Animación cuando una unidad está atacando
     */
    ATACANDO,

    /**
     * Animación cuando una unidad recibe daño
     */
    RECIBIENDO_DANIO,

    /**
     * Animación cuando una unidad ataca y recibe daño simultáneamente
     */
    AMBOS
}