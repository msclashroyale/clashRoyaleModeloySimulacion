package juego.data;

import java.util.StringJoiner;

/**
 * Clase que representa una única fila (un evento) en el fichero CSV de salida.
 * Usamos campos públicos para simplificar el acceso y la modificación de datos.
 */
public class RegistroCSV {
    // --- Identificadores y Estado General ---
    public String id_partida = "";
    public String tick = "";
    public String tipo_evento = "";
    public String ganador_final = "";
    public String duracion_final = "";
    public String estrategia_j1 = "";
    public String estrategia_j2 = "";

    // --- Estado de Jugadores en el Tick del Evento ---
    public String elixir_j1 = "";
    public String elixir_j2 = "";
    public String mano_j1 = "";
    public String mano_j2 = "";
    public String mazo_j1 = "";
    public String mazo_j2 = "";

    // --- Detalles del Evento (pueden estar vacíos) ---
    public String actor_evento = "";
    public String carta_usada = "";
    public String coste_carta = "";
    public String pos_x = "";
    public String pos_y = "";
    public String tropa_vida_maxima = "";
    public String tropa_dano = "";
    public String tropa_rango = "";
    public String tropa_tipo_ataque = "";
    public String tropa_tipo_objetivo = "";
    public String tropa_nivel = "";


    /**
     * Genera la cabecera del fichero CSV.
     * @return Un string con los nombres de las columnas separados por comas.
     */
    public static String getHeader() {
        return "id_partida,tick,tipo_evento,ganador_final,duracion_final,estrategia_j1,estrategia_j2," +
               "elixir_j1,elixir_j2,mano_j1,mano_j2,mazo_j1,mazo_j2," +
               "actor_evento,carta_usada,coste_carta,pos_x,pos_y," +
               "tropa_vida_maxima,tropa_dano,tropa_rango,tropa_tipo_ataque,tropa_tipo_objetivo,tropa_nivel";
    }

    /**
     * Convierte los datos de este registro a una línea de CSV.
     * @return Un string con los valores separados por comas.
     */
    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",");
        sj.add(id_partida).add(tick).add(tipo_evento).add(ganador_final).add(duracion_final).add(estrategia_j1).add(estrategia_j2);
        sj.add(elixir_j1).add(elixir_j2).add(mano_j1).add(mano_j2).add(mazo_j1).add(mazo_j2);
        sj.add(actor_evento).add(carta_usada).add(coste_carta).add(pos_x).add(pos_y);
        sj.add(tropa_vida_maxima).add(tropa_dano).add(tropa_rango).add(tropa_tipo_ataque).add(tropa_tipo_objetivo).add(tropa_nivel);
        return sj.toString();
    }
}
