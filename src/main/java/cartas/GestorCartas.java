package cartas;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

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
        todasLasCartas.add(new Carta("Gigante", 5, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Giant.png"));
        todasLasCartas.add(new Carta("P.E.K.K.A", 7, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_PEKKA.png"));
        todasLasCartas.add(new Carta("Príncipe", 5, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Prince.png"));

        // TROPAS TERRESTRES NORMALES
        todasLasCartas.add(new Carta("Caballero", 3, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Knight.png"));
        todasLasCartas.add(new Carta("Mini P.E.K.K.A", 4, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Mini PEKKA.png"));
        todasLasCartas.add(new Carta("Mosquetera", 4, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Musketeer.png"));
        todasLasCartas.add(new Carta("Arqueras", 3, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Archer.png"));
        todasLasCartas.add(new Carta("Bárbaros", 5, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Barbarians.png"));
        todasLasCartas.add(new Carta("Ejército de Esqueletos", 3, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Skeleton Army.png"));

        // TROPAS ÁREA
        todasLasCartas.add(new Carta("Valquiria", 4, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Valkyrie.png"));
        todasLasCartas.add(new Carta("Mago", 5, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Wizard.png"));
        todasLasCartas.add(new Carta("Mago de Hielo", 3, TipoCarta.TROPA_TERRESTRE, "imagenCartas/Card_Ice Wizard.png"));

        // TROPAS AÉREAS
        todasLasCartas.add(new Carta("Bebé Dragón", 4, TipoCarta.TROPA_AEREA, "imagenCartas/Baby Dragon.png"));
        todasLasCartas.add(new Carta("Globo Bombástico", 5, TipoCarta.TROPA_AEREA, "imagenCartas/Balloon.png"));

        // HECHIZOS (para futuras versiones)
        todasLasCartas.add(new Carta("Flechas", 3, TipoCarta.HECHIZO, "imagenCartas/Arrows.png"));
        todasLasCartas.add(new Carta("Bola de Fuego", 4, TipoCarta.HECHIZO, "imagenCartas/boladefuego.png"));
    }

    public List<Carta> getTodasLasCartas() {
        return new ArrayList<>(todasLasCartas);
    }

    public List<Carta> getCartasTropas() {
        return todasLasCartas.stream()
                .filter(carta -> carta.getTipo() == TipoCarta.TROPA_TERRESTRE ||
                        carta.getTipo() == TipoCarta.TROPA_AEREA)
                .collect(Collectors.toList());
    }

    public Carta buscarCartaPorNombre(String nombre) {
        return todasLasCartas.stream()
                .filter(carta -> carta.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }


}