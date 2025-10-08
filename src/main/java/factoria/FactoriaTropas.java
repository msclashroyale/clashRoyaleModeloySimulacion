package factoria;

import entidades.tropas.Tropa;
import tablero.Posicion;
import java.util.HashMap;
import java.util.Map;

public class FactoriaTropas {

    private static final Map<String, Tropa> prototipos = new HashMap<>();

    static {
        // TROPAS TANQUE
        prototipos.put("gigante", new Tropa("Gigante", 2000, 120, 3, 1, 5, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.ESTRUCTURAS, 0, "imagenes/Giant.png", null, 0, 0));
        prototipos.put("p.e.k.k.a", new Tropa("P.E.K.K.A", 2600, 510, 3, 1, 7, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/PEKKA.png", null, 0, 0));
        prototipos.put("príncipe", new Tropa("Príncipe", 1100, 220, 2, 1, 5, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Prince.png", null, 0, 0));
        prototipos.put("globo bombástico", new Tropa("Globo Bombástico", 1050, 600, 2, 1, 5, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.ESTRUCTURAS, 0, "imagenes/Balloon.png", null, 0, 0));

        // TROPAS NORMALES
        prototipos.put("caballero", new Tropa("Caballero", 660, 75, 2, 1, 3, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Knight.png", null, 0, 0));
        prototipos.put("mini p.e.k.k.a", new Tropa("Mini P.E.K.K.A", 600, 340, 1, 1, 4, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/MP.png", null, 0, 0));
        prototipos.put("mosquetera", new Tropa("Mosquetera", 340, 100, 2, 6, 4, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Musk.png", null, 0, 0));
        prototipos.put("arqueras", new Tropa("Arqueras", 125, 33, 2, 5, 3, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Arqueras.png", null, 0, 0));
        prototipos.put("bárbaros", new Tropa("Bárbaros", 300, 75, 2, 1, 5, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Barbs.png", null, 0, 0));
        prototipos.put("ejército de esqueletos", new Tropa("Ejército de Esqueletos", 51, 51, 1, 1, 3, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "imagenes/Skarmy.png", null, 0, 0));
        prototipos.put("bebé dragón", new Tropa("Bebé Dragón", 800, 100, 2, 4, 4, Tropa.TipoAtaque.AREA, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 1, "imagenes/BabyD.png", null, 0, 0));

        // TROPAS ÁREA
        prototipos.put("valquiria", new Tropa("Valquiria", 880, 120, 2, 1, 4, Tropa.TipoAtaque.AREA, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 2, "imagenes/Valk.png", null, 0, 0));
        prototipos.put("mago", new Tropa("Mago", 340, 130, 2, 5, 5, Tropa.TipoAtaque.AREA, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 2, "imagenes/Wiz.png", null, 0, 0));
        prototipos.put("mago de hielo", new Tropa("Mago de Hielo", 590, 63, 2, 5, 3, Tropa.TipoAtaque.AREA, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 2, "imagenes/Mago_de_hielo.png", null, 0, 0));
    }

    public static Tropa crearTropa(String nombreCarta, Posicion posicion, int nivel, int jugadorId) {
        Tropa prototipo = prototipos.get(nombreCarta.toLowerCase());
        if (prototipo == null) {
            // Tropa por defecto si no se encuentra
            prototipo = new Tropa("Tropa Desconocida", 400, 50, 2, 1, 1, Tropa.TipoAtaque.INDIVIDUAL, Tropa.TipoObjetivo.TROPAS_Y_ESTRUCTURAS, 0, "", null, 0, 0);
        }
        return new Tropa(prototipo, posicion, nivel, jugadorId);
    }
}