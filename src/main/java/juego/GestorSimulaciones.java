package juego;

import juego.data.ExportadorCSV;
import juego.data.RecopiladorDatos;
import juego.data.RegistroCSV;
import juego.events.GameEvent;
import juego.events.PartidaTerminadaEvent;
import juego.events.TropaDesplegadaEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Director de orquesta para ejecutar múltiples simulaciones de partidas de forma "headless"
 * y recopilar todos los datos generados.
 */
public class GestorSimulaciones {

    // --- CONFIGURACIÓN DE LA SIMULACIÓN ---
    private static final int NUM_SIMULACIONES = 10;
    private static final String RUTA_SALIDA_CSV = "simulaciones.csv";

    public static void main(String[] args) {
        System.out.println("Iniciando gestor de simulaciones...");

        List<RegistroCSV> todosLosRegistros = new ArrayList<>();

        long tiempoInicio = System.currentTimeMillis();

        for (int i = 0; i < NUM_SIMULACIONES; i++) {
            String idPartida = "partida_" + i;
            System.out.println("Ejecutando simulación: " + idPartida);

            // 1. Crear la partida y la configuración
            ConfiguracionPartida config = new ConfiguracionPartida();
            Partida partida = new Partida(config);

            // 2. Crear y suscribir nuestro "espía" (RecopiladorDatos) a eventos específicos
            RecopiladorDatos recopilador = new RecopiladorDatos(idPartida, partida);
            partida.getEventManager().subscribe(TropaDesplegadaEvent.class, recopilador);
            partida.getEventManager().subscribe(PartidaTerminadaEvent.class, recopilador);

            // 3. Inicializar y ejecutar la partida hasta el final
            partida.inicializar();
            while (!partida.isPartidaTerminada()) {
                partida.ejecutarTick();
            }

            // 4. Acumular los datos de la partida recién terminada
            todosLosRegistros.addAll(recopilador.getRegistros());
        }

        long tiempoFin = System.currentTimeMillis();
        double tiempoTotalSegundos = (tiempoFin - tiempoInicio) / 1000.0;

        System.out.println("\n==================================================");
        System.out.printf("Simulación completada. Se ejecutaron %d partidas en %.2f segundos.%n", NUM_SIMULACIONES, tiempoTotalSegundos);
        System.out.printf("Se generaron un total de %d eventos (filas en el CSV).%n", todosLosRegistros.size());
        System.out.println("==================================================\n");

        // 5. Exportar todos los datos acumulados a un fichero CSV
        System.out.println("Iniciando exportación a CSV...");
        ExportadorCSV.exportar(todosLosRegistros, RUTA_SALIDA_CSV);
    }
}
