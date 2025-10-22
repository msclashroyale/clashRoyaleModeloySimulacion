package cartas;

import java.util.*;

public class GestorCartas {
    private static GestorCartas instancia;
    private List<Carta> todasLasCartas;

    private GestorCartas() {
        inicializarCartas();
    }

    public static GestorCartas getInstance() {
        if (instancia == null) {
            instancia = new GestorCartas();
        }
        return instancia;
    }

    private void inicializarCartas() {
        todasLasCartas = new ArrayList<>();

        // TROPAS TERRESTRES TANQUE
        todasLasCartas.add(new Carta("Gigante", 5, TipoCarta.TROPA_TERRESTRE, "imagenes/Giant.png"));
        todasLasCartas.add(new Carta("P.E.K.K.A", 7, TipoCarta.TROPA_TERRESTRE, "imagenes/PEKKA.png"));
        todasLasCartas.add(new Carta("Príncipe", 5, TipoCarta.TROPA_TERRESTRE, "imagenes/Prince.png"));

        // TROPAS TERRESTRES NORMALES
        todasLasCartas.add(new Carta("Caballero", 3, TipoCarta.TROPA_TERRESTRE, "imagenes/Knight.png"));
        todasLasCartas.add(new Carta("Mini P.E.K.K.A", 4, TipoCarta.TROPA_TERRESTRE, "imagenes/MP.png"));
        todasLasCartas.add(new Carta("Mosquetera", 4, TipoCarta.TROPA_TERRESTRE, "imagenes/Musk.png"));
        todasLasCartas.add(new Carta("Arqueras", 3, TipoCarta.TROPA_TERRESTRE, "imagenes/Arqueras.png"));
        todasLasCartas.add(new Carta("Bárbaros", 5, TipoCarta.TROPA_TERRESTRE, "imagenes/Barbs.png"));
        todasLasCartas.add(new Carta("Ejército de Esqueletos", 3, TipoCarta.TROPA_TERRESTRE, "imagenes/Skarmy.png"));

        // TROPAS ÁREA
        todasLasCartas.add(new Carta("Valquiria", 4, TipoCarta.TROPA_TERRESTRE, "imagenes/Valk.png"));
        todasLasCartas.add(new Carta("Mago", 5, TipoCarta.TROPA_TERRESTRE, "imagenes/Wiz.png"));
        todasLasCartas.add(new Carta("Mago de Hielo", 3, TipoCarta.TROPA_TERRESTRE, "imagenes/Mago_de_hielo.png"));

        // TROPAS AÉREAS
        todasLasCartas.add(new Carta("Bebé Dragón", 4, TipoCarta.TROPA_AEREA, "imagenes/BabyD.png"));
        todasLasCartas.add(new Carta("Globo Bombástico", 5, TipoCarta.TROPA_AEREA, "imagenes/Balloon.png"));

        // HECHIZOS (para futuras versiones)
        todasLasCartas.add(new Carta("Flechas", 3, TipoCarta.HECHIZO, "imagenes/flechas.png"));
        todasLasCartas.add(new Carta("Bola de Fuego", 4, TipoCarta.HECHIZO, "imagenes/boladefuego.png"));
    }

    public List<Carta> getTodasLasCartas() {
        return new ArrayList<>(todasLasCartas);
    }

    public List<Carta> getCartasTropas() {
        return new ArrayList<>(todasLasCartas.stream()
                .filter(carta -> carta.getTipo() == TipoCarta.TROPA_TERRESTRE)
                .toList());
    }

    public Carta buscarCartaPorNombre(String nombre) {
        return todasLasCartas.stream()
                .filter(carta -> carta.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
}