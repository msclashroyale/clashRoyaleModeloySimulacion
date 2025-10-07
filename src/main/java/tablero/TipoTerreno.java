package tablero;

public enum TipoTerreno {
    VACIO(true, '.'),
    RIO(false, '~'),
    PUENTE(true, '='),
    TORRE_REY(false, 'R'),
    TORRE_PRINCESA(false, 'P');

    private final boolean esTransitable;
    private final char simbolo;

    TipoTerreno(boolean esTransitable, char simbolo) {
        this.esTransitable = esTransitable;
        this.simbolo = simbolo;
    }

    public boolean esTransitable() {
        return esTransitable;
    }

    public char getSimbolo() {
        return simbolo;
    }

    public boolean esTorre() {
        return this == TORRE_REY || this == TORRE_PRINCESA;
    }
}